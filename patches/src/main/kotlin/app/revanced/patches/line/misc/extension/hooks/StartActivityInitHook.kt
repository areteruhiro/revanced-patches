package app.revanced.patches.line.misc.extension.hooks

import app.revanced.patches.shared.misc.extension.extensionHook
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val startActivityInitHook = extensionHook(
    customFilter = { methodDef, classDef ->
        methodDef.name == "onCreate" && classDef.type == "Ljp/naver/line/android/activity/main/MainActivity;"
    }
) {
    hook {
        val getApplicationContextIndex = method.implementation!!.indexOfFirstInstructionOrThrow {
            opcode == Opcode.INVOKE_VIRTUAL && ((this as ReferenceInstruction).reference as MethodReference).name == "getApplicationContext"
        }

        insertIndex = getApplicationContextIndex + 2 // Insert after move-result-object

        contextRegisterResolver = { method ->
            val moveResultInstruction = method.implementation!!.instructions.elementAt(getApplicationContextIndex + 1) as OneRegisterInstruction
            "v${moveResultInstruction.registerA}"
        }
    }

    opcodes(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.CONST_4,
        Opcode.IF_EQZ,
        Opcode.CONST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_BOOLEAN,
        Opcode.INVOKE_VIRTUAL, // Calls startActivity.getApplicationContext()
        Opcode.MOVE_RESULT_OBJECT
    )
}
