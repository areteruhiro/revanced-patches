package app.revanced.patches.line.misc.extension

import app.revanced.patches.line.misc.extension.hooks.startActivityInitHook
import app.revanced.patches.shared.misc.extension.sharedExtensionPatch

val extensionPatch = sharedExtensionPatchh(startActivityInitHook)
