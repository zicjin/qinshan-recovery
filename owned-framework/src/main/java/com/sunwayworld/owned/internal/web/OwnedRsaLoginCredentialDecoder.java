package com.sunwayworld.owned.internal.web;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * Implements the archived browser's base64 X.509 public-key and RSA credential
 * wire format. A runtime owns one key pair; only its public key is exposed.
 */
public final class OwnedRsaLoginCredentialDecoder implements OwnedLoginCredentialDecoder {
    private final PrivateKey privateKey;
    private final String publicKey;

    public OwnedRsaLoginCredentialDecoder() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            // EncryptUtils.genRSAKeyPair in the archived framework initializes a
            // 1024-bit RSA pair; preserve that observable wire contract.
            generator.initialize(1024, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        } catch (GeneralSecurityException unavailable) {
            throw new IllegalStateException("RSA credential transport unavailable", unavailable);
        }
    }

    public String getPublicKey() { return publicKey; }

    @Override public char[] decode(String userId, String encodedPassword, String transportKey) {
        if (!publicKey.equals(transportKey) || encodedPassword == null || encodedPassword.length() > 512) {
            throw invalid();
        }
        byte[] ciphertext = null;
        byte[] plaintext = null;
        CharBuffer decoded = null;
        try {
            ciphertext = Base64.getDecoder().decode(encodedPassword);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            plaintext = cipher.doFinal(ciphertext);
            decoded = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(plaintext));
            char[] password = new char[decoded.remaining()];
            decoded.get(password);
            return password;
        } catch (GeneralSecurityException | CharacterCodingException | IllegalArgumentException rejected) {
            throw invalid();
        } finally {
            if (ciphertext != null) Arrays.fill(ciphertext, (byte) 0);
            if (plaintext != null) Arrays.fill(plaintext, (byte) 0);
            if (decoded != null && decoded.hasArray()) Arrays.fill(decoded.array(), '\0');
        }
    }

    private static IllegalArgumentException invalid() {
        return new IllegalArgumentException("GIKAM.SECURITY.LOGIN.INVALID_REQUEST");
    }
}
