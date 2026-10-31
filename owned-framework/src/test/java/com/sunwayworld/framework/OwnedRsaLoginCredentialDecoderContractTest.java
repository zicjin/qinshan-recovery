package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedRsaLoginCredentialDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;

/** Exercise the observed browser wire format against the owned private key. */
public final class OwnedRsaLoginCredentialDecoderContractTest {
    public static void main(String[] args) throws Exception {
        OwnedRsaLoginCredentialDecoder decoder = new OwnedRsaLoginCredentialDecoder();
        String publicKey = decoder.getPublicKey();
        PublicKey clientKey = KeyFactory.getInstance("RSA").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        Cipher client = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        client.init(Cipher.ENCRYPT_MODE, clientKey);
        String ciphertext = Base64.getEncoder().encodeToString(
                client.doFinal("密碼-fixture-42".getBytes(StandardCharsets.UTF_8)));
        char[] actual = decoder.decode("fixture", ciphertext, publicKey);
        if (!Arrays.equals("密碼-fixture-42".toCharArray(), actual)) throw new AssertionError("RSA credential round trip");
        Arrays.fill(actual, '\0');
        reject(decoder, "plaintext", publicKey);
        reject(decoder, ciphertext, "unknown-key");
        reject(decoder, ciphertext, null);
        reject(decoder, null, publicKey);
        OwnedRsaLoginCredentialDecoder other = new OwnedRsaLoginCredentialDecoder();
        reject(other, ciphertext, other.getPublicKey());
        if (publicKey.equals(other.getPublicKey())) throw new AssertionError("runtime key pairs must differ");
        System.out.println("owned RSA login credential: PASS (UTF-8 browser format, server private key, malformed and foreign-key rejection)");
    }

    private static void reject(OwnedRsaLoginCredentialDecoder decoder, String cipher, String key) {
        try { decoder.decode("fixture", cipher, key); }
        catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("invalid RSA transport accepted");
    }
}
