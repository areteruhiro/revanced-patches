package app.revanced.patches.line.misc.extension.hooks

import app.revanced.patches.shared.misc.extension.extensionHook

internal val applicationInitHook = extensionHook {
    custom { methodDef, classDef ->
           methodDef.name == "onCreate" && classDef.type == "Ljp/naver/line/android/activity/main/MainActivity;"
}
