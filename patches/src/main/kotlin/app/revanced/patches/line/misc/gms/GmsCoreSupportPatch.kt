package app.revanced.patches.line

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import java.security.MessageDigest

@Patch(
    name = "LINE Package Spoof",
    description = "LINEのパッケージ情報と署名を偽装するパッチ",
    compatiblePackages = [app.revanced.patcher.patch.CompatiblePackage("jp.naver.line.android")]
)
object LinePackageSpoofPatch : BytecodePatch() {

    private const val TARGET_PACKAGE = "jp.naver.line.android"
    private const val SPOOFED_SIGNATURE = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2"

    override fun execute(context: BytecodeContext) {
        // パッケージ名偽装処理
        spoofPackageNames(context)

        // 署名検証バイパス処理
        bypassSignatureVerification(context)

        // PackageManagerメソッドフック
        hookPackageManagerMethods(context)
    }

    private fun spoofPackageNames(context: BytecodeContext) {
        context.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                method.implementation?.instructions?.forEachIndexed { index, instruction ->
                    if (instruction.opcode == Opcode.CONST_STRING) {
                        val string = (instruction as ReferenceInstruction).reference.toString()
                        if (string.startsWith("jp.naver.line")) {
                            (method as MutableMethod).replaceInstruction(
                                index,
                                "const-string v0, \"$TARGET_PACKAGE\""
                            )
                        }
                    }
                }
            }
        }
    }

    private fun bypassSignatureVerification(context: BytecodeContext) {
        context.findClass("Ljp/naver/line/android/util/SignatureVerifier;")?.mutableClass?.methods
            ?.firstOrNull { it.name == "verifySignature" }
            ?.apply {
                implementation = implementation?.apply {
                    instructions.clear()
                    addInstruction("const/4 v0, 0x1")
                    addInstruction("return v0")
                }
            }
    }

    private fun hookPackageManagerMethods(context: BytecodeContext) {
        context.findClass("Landroid/content/pm/PackageManager;")?.mutableClass?.methods?.forEach { method ->
            when (method.name) {
                "getPackageInfo" -> method.addInstructions(
                    0,
                    """
                    invoke-static {p1}, ${this::class.java.name.replace('.', '/')}->spoofPackageName(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object p1
                    """
                )
                "getPackageSignatures" -> method.addInstructions(
                    0,
                    """
                    invoke-static {p0}, ${this::class.java.name.replace('.', '/')}->spoofSignature([B)[B
                    """
                )
            }
        }
    }

    @JvmStatic
    fun spoofPackageName(originalName: String): String {
        return if (originalName.startsWith("jp.naver.line")) TARGET_PACKAGE else originalName
    }

    override fun generateSpoofedSignature(original: ByteArray): ByteArray {
        return try {
            // カスタム署名オプションの値を取得
            val targetSignature = customSignatureOption.value
            
            // 現在の署名ハッシュを計算
            val currentHash = sha256(original)
            
            when {
                // 現在の署名が期待値と異なる場合のみ偽装
                currentHash != targetSignature -> {
                    Logger.printDebug("署名を偽装: $currentHash → $targetSignature")
                    MessageDigest.getInstance("SHA-256")
                        .digest(targetSignature.hexToBytes())
                }
                // 既に期待する署名の場合は変更不要
                else -> {
                    Logger.printDebug("署名変更不要: $currentHash")
                    original
                }
            }
        } catch (e: Exception) {
            Logger.printError("署名生成エラー", e)
            original // エラー時はオリジナルを返す
        }
    }

    // 16進文字列→ByteArray変換拡張関数
    private fun String.hexToBytes(): ByteArray {
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray() // 修正された部分
    }

    private fun sha256(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }
}
