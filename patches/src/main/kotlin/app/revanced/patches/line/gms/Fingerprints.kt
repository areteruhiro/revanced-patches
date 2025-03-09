package app.revanced.patches.line.gms.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object MainActivityFingerprint : MethodFingerprint(
    className = "Ljp/naver/line/android/activity/main/MainActivity;",
    method = "onCreate",
    parameters = listOf("Landroid/os/Bundle;")
)

object SafetyNetClientFingerprint : MethodFingerprint(
    className = "Lcom/google/android/gms/safetynet/SafetyNetClient;",
    method = "attest"
)
