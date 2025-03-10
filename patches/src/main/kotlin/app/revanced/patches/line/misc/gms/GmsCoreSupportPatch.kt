package app.revanced.patches.line.misc.gms

import app.revanced.patcher.patch.Option
import app.revanced.patches.line.misc.extension.extensionPatch
import app.revanced.patches.line.misc.gms.fingerprints.MainActivityFingerprint
import app.revanced.patches.shared.castContextFetchFingerprint
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch
import app.revanced.patches.shared.primeMethodFingerprint

internal object LineConstants {
    const val PACKAGE_NAME = "jp.naver.line.android"
    const val SPOOFED_PACKAGE = "app.revanced.line.android"
    const val OFFICIAL_SIGNATURE = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2"
    const val MICROG_SIGNATURE = "1be388ce61a43b6a0b60b42928b6cbe0d83b46a8e873aa193e25f0a589d230c8"
}

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = LineConstants.PACKAGE_NAME,
    toPackageName = LineConstants.SPOOFED_PACKAGE,
    primeMethodFingerprint = primeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        castContextFetchFingerprint
    ),
    mainActivityOnCreateFingerprint = MainActivityFingerprint,
    extensionPatch = extensionPatch, // Corrected to use the right extension patch
    gmsCoreSupportResourcePatchFactory = ::lineGmsCoreSupportResourcePatch,
) {
    compatibleWith(LineConstants.PACKAGE_NAME)
}

private fun lineGmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
) = app.revanced.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
    fromPackageName = LineConstants.PACKAGE_NAME,
    toPackageName = LineConstants.SPOOFED_PACKAGE,
    spoofedPackageSignature = LineConstants.MICROG_SIGNATURE, // Using MICROG_SIGNATURE for spoofing
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
)
