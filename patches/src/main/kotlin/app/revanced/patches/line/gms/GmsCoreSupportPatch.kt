package app.revanced.patches.line.gms

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.line.misc.extension.extensionPatch
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch
import app.revanced.patches.shared.misc.gms.fingerprints.GooglePlayServicesCheckFingerprint
import app.revanced.patcher.util.proxy.mutableClass.MutableClass.Companion.toMutableClass
import app.revanced.util.exception

object LineConstants {
    const val PACKAGE_NAME = "jp.naver.line.android"
    const val SPOOFED_PACKAGE = "app.revanced.line.android"
    const val SIGNATURE_SHA256 = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2"
    const val MICROG_SIGNATURE = "1be388ce61a43b6a0b60b42928b6cbe0d83b46a8e873aa193e25f0a589d230c8"
}
// GmsCoreSupportPatch.kt
@Patch(
    name = "LINE microG Support",
    description = "Enables microG compatibility for LINE",
    compatiblePackages = [CompatiblePackage(LineConstants.PACKAGE_NAME)]
)
object GmsCoreSupportPatch : CompositePatch(
    listOf(
        GooglePlayServicesPatch,
        SafetyNetPatch,
        ResourceSignaturePatch
    )
) {
    private object GooglePlayServicesPatch : MethodPatch(
        name = "Google Play Services Check",
        description = "Bypass Google Play Services availability check",
        fingerprint = GooglePlayServicesCheckFingerprint
    ) {
        override fun execute(classProxy: ClassProxy) {
            classProxy.methods
                .first { it.name == "isGooglePlayServicesAvailable" }
                .replaceInstructions(
                    0,
                    """
                    const/4 v0, 0x0
                    return v0
                    """
                )
        }
    }

    private object SafetyNetPatch : MethodPatch(
        name = "SafetyNet Bypass",
        description = "Disable SafetyNet attestation checks",
        fingerprint = SafetyNetClientFingerprint
    ) {
        override fun execute(classProxy: ClassProxy) {
            classProxy.methods
                .first { it.name == "attest" }
                .replaceInstructions(
                    0,
                    """
                    new-instance v0, Lcom/google/android/gms/common/api/Status;
                    const/16 v1, 0x8
                    invoke-direct {v0, v1}, Lcom/google/android/gms/common/api/Status;-><init>(I)V
                    return-object v0
                    """
                )
        }
    }

    private object ResourceSignaturePatch : ResourcePatch(
        name = "Signature Spoofing",
        description = "Allow microG signature verification",
        replacements = mapOf(
            "res/values/security.xml" to """
                <resources>
                    <string-array name="trusted_signatures">
                        <item>${LineConstants.SIGNATURE_SHA256}</item>
                        <item>${LineConstants.MICROG_SIGNATURE}</item>
                    </string-array>
                </resources>
            """
        )
    )
}
