package app.revanced.patches.line.misc.extension

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.shared.misc.extension.sharedExtensionPatch
import app.revanced.patches.line.gms.fingerprints.MainActivityFingerprint

internal val lineMainActivityInitHook = object : BytecodePatch() {
    override fun execute(context: BytecodeContext) {
        val mainActivityClass = MainActivityFingerprint.result?.classDef
            ?: throw Exception("MainActivity not found")
        
        mainActivityClass.methods
            .first { it.name == "onCreate" }
            .addInstructions(
                0,
                "invoke-static {}, Lorg/microg/gms/common/GmsClient;->initialize()V"
            )
    }
}

val extensionPatch = sharedExtensionPatch(
    extensionName = "LINE Extension",
    initializationHook = lineMainActivityInitHook
)
