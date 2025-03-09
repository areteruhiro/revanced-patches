package app.revanced.patches.line.misc.gms.fingerprints

import app.revanced.patcher.fingerprint

internal val MainActivityFingerprint = fingerprint {
        returns("V")
    parameters("Landroid/os/Bundle;")
    custom { methodDef, classDef ->
        methodDef.name == "onCreate" && classDef.type == "/MainActivity;"
    }
}
