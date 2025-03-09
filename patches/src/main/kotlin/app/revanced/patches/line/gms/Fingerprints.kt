Fingerprints.kt
package app.revanced.patches.line.gms

import app.revanced.patcher.fingerprint

internal val homeActivityOnCreateFingerprint = fingerprint {
    custom { methodDef, classDef ->
        methodDef.name == "onCreate" && classDef.endsWith("/HomeActivity;")
    }
}
