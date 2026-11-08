# MediaPipe tasks-vision + protobuf-lite keduanya bergantung pada reflection:
#   1) protobuf membaca field generated message via nama field
#      ("Field platform_ ... not found" tanpa rule ini)
#   2) protobuf melakukan stack-walk untuk mencari kelas pemanggil; R8 full mode
#      meng-inline/menggabungkan frame stack hingga gagal
#      ("no caller found on the stack" tanpa rule ini)
# Consumer rules bawaan MediaPipe tidak mencakup keduanya.
-keep class com.google.protobuf.** { *; }
-keep class com.google.mediapipe.** { *; }
-dontwarn com.google.protobuf.**

# Kelas proto ini tidak ikut dalam AAR tasks-vision 0.10.14 (referensi menggantung
# di GraphProfiler/Graph — jalur profiler yang tidak dipakai aplikasi).
-dontwarn com.google.mediapipe.proto.CalculatorProfileProto$CalculatorProfile
-dontwarn com.google.mediapipe.proto.GraphTemplateProto$CalculatorGraphTemplate

# Nonaktifkan optimasi (inlining/class merging) agar stack-walk protobuf tetap
# valid. Shrinking & obfuscation tetap berjalan — penghematan ikon extended
# tidak hilang.
-dontoptimize
