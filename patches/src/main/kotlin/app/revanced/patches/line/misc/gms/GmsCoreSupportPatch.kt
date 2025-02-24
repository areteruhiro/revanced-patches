package app.revanced.patches.line.misc.gms

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.util.Base64

object LineCertificate {
    // 証明書基本情報
    const val PACKAGE_NAME = "jp.naver.line.android"
    const val SERIAL_NUMBER = "4d6b012a"
    const val VALIDITY = "2011-02-28 to 2111-02-04"
    
    // ハッシュ値
    const val SHA256 = "e682fe0bcd60907dfed515e0b8a4de03aa1c281d111a07833986602b6098afd2"
    
    // 公開鍵情報
    const val RSA_MODULUS = "8da86b1b1c76ce9e4e844f402fc50a28e4afc56cd8492d9ee42e67d002ea63942861fa0081e04e353e3f7288f6897779296afa4192b48b238d8062edc68feebc9f781f8c8d0ea01846bcd970c3158bd4a654ca92705217d1ec1c5a9b876d8d5926cc6a91334c5e8d8b97caca95016da130330792befab38f42c4f78449def067"
    const val RSA_EXPONENT = "10001"
    
    // 完全な証明書データ
    private const val BASE64_CERT = """
        MIICqzCCAhSgAwIBAgIETWsBKjANBgkqhkiG9w0BAQUFADCBmDELMAkGA1UEBhMCSlAxDjAMBgNV
        BAgTBVRva3lvMRswGQYDVQQHExJPb3Nha2kgU2luYWdhd2Eta3UxEzARBgNVBAoTCk5hdmVySmFw
        YW4xKTAnBgNVBAsTIFNlYXJjaCBTZXJ2aWNlIERldmVsb3BtZW50IDNUZWFtMRwwGgYDVQQDExN0
        c3V0b211IGhvcml5YXNoaWtpMCAXDTExMDIyODAxNTgwMloYDzIxMTEwMjA0MDE1ODAyWjCBmDEL
        MAkGA1UEBhMCSlAxDjAMBgNVBAgTBVRva3lvMRswGQYDVQQHExJPb3Nha2kgU2luYWdhd2Eta3Ux
        EzARBgNVBAoTCk5hdmVySmFwYW4xKTAnBgNVBAsTIFNlYXJjaCBTZXJ2aWNlIERldmVsb3BtZW50
        IDNUZWFtMRwwGgYDVQQDExN0c3V0b211IGhvcml5YXNoaWtpMIGfMA0GCSqGSIb3DQEBAQUAA4GN
        ADCBiQKBgQCNqGsbHHbOnk6ET0AvxQoo5K/FbNhJLZ7kLmfQAupjlChh+gCB4E41Pj9yiPaJd3kp
        avpBkrSLI42AYu3Gj+68n3gfjI0OoBhGvNlwwxWL1KZUypJwUhfR7Bxam4dtjVkmzGqRM0xejYuX
        ysqVAW2hMDMHkr76s49CxPeESd7wZwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAC2HdP71+BV8sQm1
        HDuUSGDaDf51Mmbw8fpfbif+cS94Qj7Xl//zg9byq4VWWl+3rkCPrOcvq4wVdMuN5HghudgQmHiP
        zFt/Bsrt6863wTskAhlNBDPchtZfhq5wnnAyUSLn6zpzmAE1yNjmUJlLnDSdg4V6w7kbZfBSAA/
        aYffa
    """

    fun getX509Certificate(): java.security.cert.X509Certificate {
        val decoded = Base64.getDecoder().decode(BASE64_CERT.filterNot { it.isWhitespace() })
        return CertificateFactory.getInstance("X.509")
            .generateCertificate(decoded.inputStream()) as java.security.cert.X509Certificate
    }
}

@Patch(
    name = "LINE Certificate Spoof",
    description = "LINEの公式証明書を模倣するパッチ",
    compatiblePackages = [CompatiblePackage(LineCertificate.PACKAGE_NAME)]
)
object LineCertificateSpoofPatch : BytecodePatch() {

    private val targetSignature = LineCertificate.SHA256

    override fun execute(context: BytecodeContext) {
        // パッケージ名偽装
        replacePackageReferences(context)
        
        // 署名検証バイパス
        bypassSignatureChecks(context)
        
        // 証明書情報注入
        injectCertificateData(context)
    }

    private fun replacePackageReferences(context: BytecodeContext) {
        context.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                method.implementation?.instructions?.forEachIndexed { index, instruction ->
                    if (instruction.opcode == Opcode.CONST_STRING) {
                        val str = (instruction as ReferenceInstruction).reference.toString()
                        if (str.startsWith(LineCertificate.PACKAGE_NAME)) {
                            (method as MutableMethod).replaceInstruction(
                                index,
                                "const-string v0, \"${LineCertificate.PACKAGE_NAME}\""
                            )
                        }
                    }
                }
            }
        }
    }

    private fun bypassSignatureChecks(context: BytecodeContext) {
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

    private fun injectCertificateData(context: BytecodeContext) {
        context.findClass("Landroid/content/pm/PackageManager;")?.mutableClass?.methods?.forEach { 
            when (it.name) {
                "getPackageInfo" -> it.addInstructions(
                    0,
                    """
                    invoke-static {p1}, ${this::class.java.name.replace('.', '/')}->spoofPackageName(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object p1
                    """
                )
                "getPackageSignatures" -> it.addInstructions(
                    0,
                    """
                    invoke-static {p0}, ${this::class.java.name.replace('.', '/')}->generateSpoofedSignature([B)[B
                    """
                )
            }
        }
    }

    @JvmStatic
    fun spoofPackageName(original: String): String {
        return if (original.startsWith("jp.naver.line")) LineCertificate.PACKAGE_NAME else original
    }

    @JvmStatic
    fun generateSpoofedSignature(original: ByteArray): ByteArray {
        return try {
            val currentHash = sha256(original)
            if (currentHash != targetSignature) {
                MessageDigest.getInstance("SHA-256").digest(
                    targetSignature.hexToBytes()
                )
            } else {
                original
            }
        } catch (e: Exception) {
            original
        }
    }

    private fun String.hexToBytes(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    private fun sha256(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }
}
