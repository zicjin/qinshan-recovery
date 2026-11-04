/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.crypto.digests.SM3Digest;
import com.sunwayworld.framework.crypto.macs.HMac;
import com.sunwayworld.framework.crypto.params.KeyParameter;
import com.sunwayworld.framework.exception.EncryptException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncryptUtils {
    public static final String SM3Encrypt(String text) {
        return SM3Helper.encrypt(text);
    }

    public static final String MD5Encrypt(String text) {
        return MD5Helper.MD5Encrypt(text);
    }

    public static final String MD5Encrypt(File file) {
        return MD5Helper.MD5Encrypt(file);
    }

    public static final String DESEncrypt(String text) {
        return StringUtils.trimAllWhitespace(DESAlgorithm.getInstance().encrypt(text));
    }

    public static final String DESDecrypt(String encryptedText) {
        return DESAlgorithm.getInstance().decrypt(encryptedText);
    }

    public static final String base64Encode(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static final String base64Encode(byte[] b) {
        if (b == null || b.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(b);
    }

    public static final String base64Decode(String encodedText) {
        if (StringUtils.isEmpty(encodedText)) {
            return "";
        }
        return new String(Base64.getDecoder().decode(encodedText), StandardCharsets.UTF_8);
    }

    public static final byte[] base64Decode2Bytes(String encodedText) {
        if (StringUtils.isEmpty(encodedText)) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(encodedText);
    }

    public static final String getRSAPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static final String getRSAPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static final RsaKeyPair genRSAKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            return new RsaKeyPair(privateKeyString, publicKeyString);
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new EncryptException(nsae);
        }
    }

    public static final String RSAEncrypt(String text, String publicKey) {
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        try {
            RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(1, pubKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            throw new EncryptException(ex);
        }
    }

    public static final String RSADecrypt(String encodedText, String privateKey) {
        try {
            byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes("UTF-8"));
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, priKey);
            return new String(cipher.doFinal(textBytes), "UTF-8");
        }
        catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            throw new EncryptException(ex);
        }
    }

    public static final String bcryptEncode(String text) {
        return new BCryptPasswordEncoder().encode(text);
    }

    public static final boolean bcryptMatches(String rawText, String encodedText) {
        return new BCryptPasswordEncoder().matches(rawText, encodedText);
    }

    public static class RsaKeyPair {
        private String privateKey;
        private String publicKey;

        public RsaKeyPair(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return this.privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return this.publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }

    private static class DESAlgorithm {
        private static final String RANDOM_SEED = "SUNWAY_2019_LIMS_SEED";
        private static volatile DESAlgorithm instance;
        private SecretKey secretKey;

        private DESAlgorithm() {
            try {
                DESKeySpec dks = new DESKeySpec(RANDOM_SEED.getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                this.secretKey = keyFactory.generateSecret(dks);
            }
            catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                throw new UnexpectedException(ex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public static DESAlgorithm getInstance() {
            if (instance != null) return instance;
            Class<DESAlgorithm> clazz = DESAlgorithm.class;
            synchronized (DESAlgorithm.class) {
                if (instance != null) return instance;
                instance = new DESAlgorithm();
                // ** MonitorExit[var0] (shouldn't be in output)
                return instance;
            }
        }

        public String encrypt(String text) {
            try {
                SecureRandom sr = new SecureRandom();
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(1, (Key)this.secretKey, sr);
                return StringUtils.trimAllWhitespace(Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes())));
            }
            catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public String decrypt(String encryptedText) {
            try {
                SecureRandom sr = new SecureRandom();
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(2, (Key)this.secretKey, sr);
                return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
            }
            catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
                throw new UnexpectedException(ex);
            }
        }
    }

    private static class MD5Helper {
        private MD5Helper() {
        }

        public static final String MD5Encrypt(String target) {
            if (target == null) {
                return null;
            }
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                return StringUtils.toHexString(md.digest(target.getBytes("UTF-8")));
            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                throw new EncryptException(ex);
            }
        }

        public static final String MD5Encrypt(File file) {
            if (!file.exists() || !file.isFile()) {
                return null;
            }
            MessageDigest digest = null;
            byte[] buffer = new byte[1024];
            try (FileInputStream in = new FileInputStream(file);){
                int len;
                digest = MessageDigest.getInstance("MD5");
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
            }
            catch (IOException | NoSuchAlgorithmException ex) {
                throw new EncryptException(ex);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        }
    }

    private static class SM3Helper {
        private static byte[] SM3_KEY_BYTES = null;

        private SM3Helper() {
        }

        public static final String encrypt(String text) {
            try {
                byte[] bytes = text.getBytes("UTF-8");
                byte[] result = SM3Helper.encrypt(SM3_KEY_BYTES, bytes);
                return StringUtils.toHexString(result);
            }
            catch (UnsupportedEncodingException uee) {
                throw new ApplicationRuntimeException(uee);
            }
        }

        public static final byte[] encrypt(byte[] key, byte[] bytes) {
            KeyParameter keyParameter = new KeyParameter(key);
            SM3Digest digest = new SM3Digest();
            HMac mac = new HMac(digest);
            mac.init(keyParameter);
            mac.update(bytes, 0, bytes.length);
            byte[] result = new byte[mac.getMacSize()];
            mac.doFinal(result, 0);
            return result;
        }

        static {
            try {
                SM3_KEY_BYTES = "SUNWAY_SM3_SEED".getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException uee) {
                throw new ApplicationRuntimeException(uee);
            }
        }
    }
}
