package app.revanced.patches.line.gms

import app.revanced.patcher.patch.Option
import app.revanced.patches.line.misc.extension.extensionPatch
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch

// LINE固有の定数
object LineConstants {
    const val LINE_PACKAGE_NAME = "jp.naver.line.android"
    const val REVANCED_LINE_PACKAGE_NAME = "app.revanced.line.android"
}

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = LineConstants.LINE_PACKAGE_NAME,
    toPackageName = LineConstants.REVANCED_LINE_PACKAGE_NAME,
    mainActivityOnCreateFingerprint = lineMainActivityFingerprint(),
    extensionPatch = extensionPatch,
    gmsCoreSupportResourcePatchFactory = ::lineGmsCoreSupportResourcePatch,
) {
    compatibleWith(LineConstants.LINE_PACKAGE_NAME)
    dependsOn(extensionPatch)
}

private fun lineMainActivityFingerprint() = MethodFingerprint(
    className = "Ljp/naver/line/android/activity/main/MainActivity;",
    method = "onCreate",
    parameters = listOf("Landroid/os/Bundle;")
)

private fun lineGmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
) = app.revanced.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
    fromPackageName = LineConstants.LINE_PACKAGE_NAME,
    toPackageName = LineConstants.REVANCED_LINE_PACKAGE_NAME,
    spoofedPackageSignature = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2",
    additionalSignatures = setOf(
        "1be388ce61a43b6a0b60b42928b6cbe0d83b46a8e873aa193e25f0a589d230c8" // microG開発者署名
    ),
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
)
