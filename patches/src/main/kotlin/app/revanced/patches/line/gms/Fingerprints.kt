package app.revanced.patches.line.gms.fingerprints
import app.revanced.patcher.fingerprint
object MainActivityFingerprint : fingerprint(
    className = "Ljp/naver/line/android/activity/main/MainActivity;",
    method = "onCreate",
)

object SafetyNetClientFingerprint : fingerprint(
    className = "Lcom/google/android/gms/safetynet/SafetyNetClient;",
    method = "attest"
)
