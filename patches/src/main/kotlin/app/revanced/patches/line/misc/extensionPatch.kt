package app.revanced.patches.line.misc.extension

import app.revanced.patches.shared.misc.extension.sharedExtensionPatch
import app.revanced.patches.line.gms.fingerprints.PushServiceFingerprint

val extensionPatch = sharedExtensionPatch(
    initializationHook = { context ->
        // プッシュ通知サービスの初期化
        PushServiceFingerprint.resolve(context)
            ?: throw Exception("PushService not found")
    },
    additionalPatches = listOf(
        FirebaseRedirectPatch(),
        AuthTokenPatch()
    )
)
