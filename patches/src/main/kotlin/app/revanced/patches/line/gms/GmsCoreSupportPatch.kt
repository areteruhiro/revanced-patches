package app.revanced.patches.line.gms

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.Option
import app.revanced.patches.line.misc.extension.extensionPatch
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch
import app.revanced.patches.shared.misc.gms.fingerprints.*
import app.revanced.util.exception

object LineConstants {
    const val PACKAGE_NAME = "jp.naver.line.android"
    const val SPOOFED_PACKAGE = "app.revanced.line.android"
    const val SIGNATURE_SHA256 = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2"
    const val MICROG_SIGNATURE = "1be388ce61a43b6a0b60b42928b6cbe0d83b46a8e873aa193e25f0a589d230c8"
}

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    name = "LINE microG Support",
    description = "Enables full microG compatibility for LINE",
    fromPackageName = LineConstants.PACKAGE_NAME,
    toPackageName = LineConstants.SPOOFED_PACKAGE,
    mainActivityOnCreateFingerprint = lineMainActivityFingerprint(),
    extensionPatch = extensionPatch,
    gmsCoreSupportResourcePatchFactory = ::lineGmsResourcePatch,
    additionalPatches = listOf(
        SafetyNetPatch,
        FirebaseRedirectPatch,
        CredentialProviderPatch
    )
) {
    compatibleWith(LineConstants.PACKAGE_NAME)
    dependsOn(extensionPatch)
}

private fun lineMainActivityFingerprint() = MethodFingerprint(
    className = "Ljp/naver/line/android/activity/main/MainActivity;",
    method = "onCreate",
    parameters = listOf("Landroid/os/Bundle;")
)

private fun lineGmsResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>
) = ResourcePatch(
    replacements = mapOf(
        "values/security.xml" to """
            <resources>
                <string-array name="trusted_signatures">
                    <item>${LineConstants.SIGNATURE_SHA256}</item>
                    <item>${LineConstants.MICROG_SIGNATURE}</item>
                </string-array>
            </resources>
        """
    ),
    packageName = LineConstants.SPOOFED_PACKAGE
)

object SafetyNetPatch : MethodPatch(
    name = "Bypass SafetyNet",
    description = "Bypasses SafetyNet attestation checks",
    fingerprint = MethodFingerprint(
        className = "Lcom/google/android/gms/safetynet/SafetyNetClient;",
        method = "attest"
    )
) {
    override fun execute() {
        method.replaceInstructions(
            0,
            """
            const/4 v0, 0x0
            return v0
            """
        ) ?: throw exception("SafetyNet method not found")
    }
}

object FirebaseRedirectPatch : MethodPatch(
    name = "Redirect Firebase",
    description = "Redirects Firebase calls to microG",
    fingerprint = MethodFingerprint(
        className = "Lcom/google/firebase/FirebaseApp;",
        method = "initializeApp"
    )
) {
    override fun execute() {
        method.replaceInstructions(
            0,
            """
            const-string v0, "microG"
            return-object v0
            """
        ) ?: throw exception("FirebaseApp method not found")
    }
}

object CredentialProviderPatch : MethodPatch(
    name = "Credential Provider Patch",
    description = "Modifies credential provider for microG",
    fingerprint = MethodFingerprint(
        className = "Landroidx/credentials/playservices/CredentialProviderPlayServicesImpl;",
        method = "isGooglePlayServicesAvailable"
    )
) {
    override fun execute() {
        method.addInstructions(
            0,
            """
            const/4 v0, 0x0
            return v0
            """
        ) ?: throw exception("CredentialProvider method not found")
    }
}
