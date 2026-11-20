# Firebase setup

YogaMate uses Firebase Authentication (email/password) and Cloud Firestore for account profiles, workout history, and calculated achievements. Pose catalog data and educational content remain local. Passwords are managed only by Firebase Authentication.

## Firebase project setup

1. Open the Firebase project used by the `plus_BE` branch.
2. Add an Android app with package name `com.viscit.yogamate` (the donor app's `com.kel9.yogamate` config will not work for this application ID).
3. Download that app's configuration as `app/google-services.json`. This local file is ignored by Git.
4. Enable **Authentication → Email/Password** and create a Firestore database in production mode.
5. Publish the rules in `firestore.rules` from **Firestore Database → Rules**.
6. Sync Gradle and run on a device or emulator with internet access.

## Firestore data

- Profile: `users/{uid}` (`name`, `email`, `goal`).
- Workout history: `users/{uid}/history/{entryId}`.
- Achievements: `users/{uid}/achievements/{code}`.

Rules restrict profile, history, and achievement reads/writes to the signed-in owner. Public pose and education collections are read-only in the rules; the app currently continues to use its bundled local content.

Prototype-only local profile, password, and workout history values are cleared on app startup. Existing Firebase sessions remain managed by Firebase Auth.
