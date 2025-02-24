package app.revanced.patches.line.misc.gms

import app.revanced.patcher.patch.Option
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch
import app.revanced.patches.shared.misc.settings.preference.IntentPreference
import app.revanced.patches.line.misc.settings.settingsPatch

object LineConstants {
    const val ORIGINAL_PACKAGE = "jp.naver.line.android"
    const val SPOOFED_PACKAGE = "jp.naver.line.android.revanced"
    const val SPOOFED_SIGNATURE = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2"
}

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = LineConstants.ORIGINAL_PACKAGE,
    toPackageName = LineConstants.SPOOFED_PACKAGE,
    mainActivityOnCreateFingerprint = lineMainActivityOnCreateFingerprint,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    compatibleWith(
        LineConstants.ORIGINAL_PACKAGE("15.1.4") // LINEの対象バージョン
    )
}

private fun gmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
) = app.revanced.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
    fromPackageName = LineConstants.ORIGINAL_PACKAGE,
    toPackageName = LineConstants.SPOOFED_PACKAGE,
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
    spoofedPackageSignature = LineConstants.SPOOFED_SIGNATURE,
    executeBlock = {
        addResources("line", "misc.gms.gmsCoreSupportResourcePatch")
        PreferenceScreen.MISC.addPreferences(
            IntentPreference(
                "microg_settings",
                intent = IntentPreference.Intent(
                    label = "MicroG設定",
                    targetClass = "org.microg.gms.ui.SettingsActivity"
                )
            )
        )
    }
) {
    dependsOn(settingsPatch, addResourcesPatch)
}
