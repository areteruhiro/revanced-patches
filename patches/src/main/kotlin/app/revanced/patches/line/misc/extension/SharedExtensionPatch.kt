package app.revanced.patches.line.misc.extension

import app.revanced.patches.line.misc.extension.hooks.applicationInitHook
import app.revanced.patches.shared.misc.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(
    "line",
    applicationInitHook,
)
