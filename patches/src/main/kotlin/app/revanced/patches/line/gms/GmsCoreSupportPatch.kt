package app.revanced.patches.line.gms

import app.revanced.patcher.patch.Option
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch
import app.revanced.patches.line.gms.fingerprints.MainActivityFingerprint

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = LineConstants.PACKAGE_NAME,
    mainActivityOnCreateFingerprint = MainActivityFingerprint,
    gmsCoreSupportResourcePatchFactory = ::lineGmsCoreSupportResourcePatch,
) {
    compatibleWith(LineConstants.PACKAGE_NAME)

}

private fun lineGmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
) = app.revanced.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
    fromPackageName = LineConstants.PACKAGE_NAME,
    spoofedPackageSignature = LineConstants.OFFICIAL_SIGNATURE,
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
)
