#!/usr/bin/env python3
"""Import yoga_poses_seed rows into Firestore collection `poses`.

Defaults to a read-only preview. Pass --apply to write documents.
Existing imageUrl/audioUrl values are preserved when documents already exist.
"""

from __future__ import annotations

import argparse
import json
import os
import sys
from pathlib import Path
from typing import Any

from openpyxl import load_workbook


PROJECT_ID = "praktikum-cloud-9"
SHEET_NAME = "yoga_poses_seed"
COLLECTION_NAME = "poses"


def required_text(row: dict[str, Any], key: str, row_number: int) -> str:
    value = row.get(key)
    if value is None or not str(value).strip():
        raise ValueError(f"Row {row_number}: required column {key!r} is blank")
    return str(value).strip()


def integer_value(row: dict[str, Any], key: str, row_number: int) -> int:
    value = row.get(key)
    if value is None or str(value).strip() == "":
        raise ValueError(f"Row {row_number}: required column {key!r} is blank")
    try:
        number = float(value)
    except (TypeError, ValueError) as exc:
        raise ValueError(f"Row {row_number}: {key!r} must be an integer") from exc
    if not number.is_integer():
        raise ValueError(f"Row {row_number}: {key!r} must be an integer")
    return int(number)


def parse_steps(value: Any, row_number: int) -> list[str]:
    if value is None or not str(value).strip():
        raise ValueError(f"Row {row_number}: 'instruksi_langkah' is blank")
    parsed = json.loads(str(value)) if isinstance(value, str) else value
    if not isinstance(parsed, list) or not all(isinstance(step, str) for step in parsed):
        raise ValueError(f"Row {row_number}: 'instruksi_langkah' must be a JSON array of strings")
    return [step.strip() for step in parsed]


def read_poses(xlsx_path: Path) -> list[dict[str, Any]]:
    if not xlsx_path.is_file():
        raise FileNotFoundError(f"XLSX file not found: {xlsx_path}")

    workbook = load_workbook(xlsx_path, read_only=True, data_only=True)
    if SHEET_NAME not in workbook.sheetnames:
        raise ValueError(f"Sheet {SHEET_NAME!r} not found. Sheets: {workbook.sheetnames}")

    sheet = workbook[SHEET_NAME]
    rows = sheet.iter_rows(values_only=True)
    headers = next(rows, None)
    if not headers:
        raise ValueError(f"Sheet {SHEET_NAME!r} is empty")
    header_names = [str(value).strip() if value is not None else "" for value in headers]
    records: list[dict[str, Any]] = []
    seen_slugs: set[str] = set()

    for row_number, values in enumerate(rows, start=2):
        if not any(value is not None for value in values):
            continue
        row = dict(zip(header_names, values))
        slug = required_text(row, "slug", row_number)
        if slug in seen_slugs:
            raise ValueError(f"Row {row_number}: duplicate slug {slug!r}")
        seen_slugs.add(slug)

        name = required_text(row, "nama_pose", row_number)
        alt_name = required_text(row, "nama_alt", row_number)
        class_label = required_text(row, "class_label", row_number)
        source_image = row.get("link_img")
        source_audio = row.get("audio")

        records.append(
            {
                "documentId": slug,
                "data": {
                    "labelId": integer_value(row, "label_id", row_number),
                    "classIndex": integer_value(row, "class_index", row_number),
                    "classLabel": class_label,
                    "name": name,
                    "sanskrit": alt_name,
                    "slug": slug,
                    "level": required_text(row, "level", row_number),
                    "description": required_text(row, "description", row_number),
                    "steps": parse_steps(row.get("instruksi_langkah"), row_number),
                    # XLSX paths are relative server paths, not public URLs. Leave blank
                    # until the matching media URL has been checked and added in Firestore.
                    "imageUrl": "",
                    "audioUrl": "",
                    # Preserve the source values for reference while URLs are being set up.
                    "sourceImagePath": str(source_image).strip() if source_image else "",
                    "sourceAudioPath": str(source_audio).strip() if source_audio else "",
                },
            }
        )

    if not records:
        raise ValueError(f"No pose rows found in {SHEET_NAME!r}")
    return records


def write_poses(records: list[dict[str, Any]], project_id: str) -> None:
    credentials_path = os.environ.get("GOOGLE_APPLICATION_CREDENTIALS")
    if not credentials_path:
        raise RuntimeError(
            "Set GOOGLE_APPLICATION_CREDENTIALS to a Firebase service-account JSON path "
            "before using --apply. Keep that key outside the repository."
        )
    key_path = Path(credentials_path)
    if not key_path.is_file():
        raise FileNotFoundError(f"Service-account JSON not found: {key_path}")

    try:
        import firebase_admin
        from firebase_admin import credentials, firestore
    except ImportError as exc:
        raise RuntimeError("Install the importer dependencies: python -m pip install firebase-admin openpyxl") from exc

    if not firebase_admin._apps:
        firebase_admin.initialize_app(
            credentials.Certificate(str(key_path)),
            {"projectId": project_id},
        )
    db = firestore.client()
    batch = db.batch()

    for record in records:
        ref = db.collection(COLLECTION_NAME).document(record["documentId"])
        existing = ref.get()
        payload = dict(record["data"])
        if existing.exists:
            old_data = existing.to_dict() or {}
            payload["imageUrl"] = old_data.get("imageUrl", "")
            payload["audioUrl"] = old_data.get("audioUrl", "")
        batch.set(ref, payload, merge=True)

    batch.commit()


def main() -> int:
    parser = argparse.ArgumentParser(description="Preview or import the YogaMate XLSX pose seed into Firestore.")
    parser.add_argument("--xlsx", required=True, help="Path to seederyogamate.xlsx")
    parser.add_argument("--project", default=PROJECT_ID, help=f"Firebase project ID (default: {PROJECT_ID})")
    parser.add_argument("--apply", action="store_true", help="Write the documents to Firestore")
    args = parser.parse_args()

    try:
        records = read_poses(Path(args.xlsx).expanduser())
        print(f"Found {len(records)} poses in sheet {SHEET_NAME!r}.")
        for record in records:
            item = record["data"]
            print(f"  {record['documentId']}: {item['name']} (labelId={item['labelId']}, classIndex={item['classIndex']})")

        if not args.apply:
            print("Preview only; Firestore was not changed. Add --apply to import these documents.")
            return 0

        write_poses(records, args.project)
        print(f"Imported {len(records)} pose documents into {args.project}/{COLLECTION_NAME}.")
        print("Existing imageUrl/audioUrl values were preserved; new documents have blank URLs.")
        return 0
    except Exception as exc:
        print(f"Import failed: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
