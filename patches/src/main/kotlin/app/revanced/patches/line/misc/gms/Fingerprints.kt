package app.revanced.patches.line.misc.gms.fingerprints

import app.revanced.patcher.fingerprint

internal val MainActivityFingerprint = fingerprint {
    custom { methodDef, classDef ->
        methodDef.name == "onCreate" && classDef.endsWith("/StartActivity;")
    }
}
