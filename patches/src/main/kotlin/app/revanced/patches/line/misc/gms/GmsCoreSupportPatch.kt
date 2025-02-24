package app.revanced.patches.line.misc.gms

import app.revanced.patcher.patch.Option
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch
import app.revanced.patches.shared.misc.settings.preference.IntentPreference
import app.revanced.patches.shared.primeMethodFingerprint
import app.revanced.patches.line.misc.gms.Constants.LINE_PACKAGE_NAME
import app.revanced.patches.line.misc.gms.Constants.REVANCED_LINE_PACKAGE_NAME
import app.revanced.patches.line.misc.settings.settingsPatch
import app.revanced.util.Logger

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = LINE_PACKAGE_NAME,
    toPackageName = REVANCED_LINE_PACKAGE_NAME,
    primeMethodFingerprint = primeMethodFingerprint,
    mainActivityOnCreateFingerprint = mainActivityOnCreateFingerprint,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    dependsOn(
        settingsPatch,
        addResourcesPatch
    )

    compatibleWith(
        LINE_PACKAGE_NAME(
            "15.1.4"  // 対応するLINEのバージョンを指定
        )
    )
}

private val customSignatureOption = Option.StringOption(
    "spoofed-signature",
    default = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2",
    title = "偽装する署名",
    description = "使用する偽装署名のSHA-256ハッシュ値"
)

private fun gmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
) = app.revanced.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
    fromPackageName = LINE_PACKAGE_NAME,
    toPackageName = REVANCED_LINE_PACKAGE_NAME,
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
    spoofedPackageSignature = customSignatureOption.value,
    executeBlock = {
        addResources("line", "misc.gms.gmsCoreSupportResourcePatch")

        PreferenceScreen.MISC.addPreferences(
            IntentPreference(
                "microg_settings",
                intent = IntentPreference.Intent(
                    "MicroG設定",
                    "org.microg.gms.ui.SettingsActivity"
                )
            )
        )
    }
) {
    dependsOn(settingsPatch, addResourcesPatch)
}

object Constants {
    const val LINE_PACKAGE_NAME = "jp.naver.line.android"
    const val REVANCED_LINE_PACKAGE_NAME = "jp.naver.line.android.revanced"
}
