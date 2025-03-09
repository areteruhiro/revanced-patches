package app.revanced.patches.line.misc.gms.fingerprints

import app.revanced.patcher.fingerprint

internal val MainActivityFingerprint = fingerprint {
    custom { methodDef, classDef ->
        methodDef.name == "onCreate" &&
        classDef.type == "Ljp/naver/line/android/activity/main/MainActivity;" &&
        methodDef.parameterTypes == listOf("Landroid/os/Bundle;")
    }
}

internal val SafetyNetClientFingerprint = fingerprint {
    custom { methodDef, classDef ->
        methodDef.name == "attest" &&
        classDef.type == "Lcom/google/android/gms/safetynet/SafetyNetClient;" &&
        methodDef.returnType == "Lcom/google/android/gms/tasks/Task;"
    }
}
