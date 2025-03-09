package app.revanced.patches.line.misc.gms.fingerprints

import app.revanced.patcher.fingerprint
import app.revanced.patcher.Fingerprint

internal val MainActivityFingerprint = fingerprint {
    custom { methodDef, classDef ->
        val isMatch = methodDef.name == "onCreate" && classDef.type == "Ljp/naver/line/android/activity/main/MainActivity;"
        if (!isMatch) {
            println("Fingerprint mismatch: methodName=${methodDef.name}, classType=${classDef.type}")
        }

        isMatch
    }
}
