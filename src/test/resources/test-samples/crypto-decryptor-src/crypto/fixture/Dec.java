package crypto.fixture;

import android.util.Base64;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** DESede + android.util.Base64 (the Jbaz.a shape) and AES/CBC + java.util.Base64 + IV. */
public class Dec {
    // DESede/ECB, key embedded as base64, android.util.Base64 for both key and input.
    public static String a(String s) {
        try {
            SecretKeySpec k = new SecretKeySpec(Base64.decode("AAECAwQFBgcICQoLDA0ODxAREhMUFRYX", 0), "DESede");
            Cipher c = Cipher.getInstance("DESede");
            c.init(2, k);
            return new String(c.doFinal(Base64.decode(s, 0)), Charset.forName("UTF-8"));
        } catch (Exception e) {
            return null;
        }
    }

    // AES/CBC/PKCS5 with an IvParameterSpec, java.util.Base64 decoder.
    public static String b(String s) {
        try {
            SecretKeySpec k = new SecretKeySpec(java.util.Base64.getDecoder().decode("ZGVmZ2hpamtsbW5vcHFycw=="), "AES");
            IvParameterSpec iv = new IvParameterSpec(java.util.Base64.getDecoder().decode("AAECAwQFBgcICQoLDA0ODw=="));
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(2, k, iv);
            return new String(c.doFinal(java.util.Base64.getDecoder().decode(s)), Charset.forName("UTF-8"));
        } catch (Exception e) {
            return null;
        }
    }
}
