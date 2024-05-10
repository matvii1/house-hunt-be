package com.house.hunter.util;

import io.github.cdimascio.dotenv.Dotenv;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static String readEncryptedSecretFromEnv() {
        return System.getenv("JWT_SECRET_KEY");
    }

    private static SecretKey decryptSecretKey(byte[] encryptedKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        SecretKey decryptionKey = generateDecryptionKey();
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey, parameterSpec);
        byte[] decryptedKey = cipher.doFinal(encryptedKey);
        return new SecretKeySpec(decryptedKey, "AES");
    }

    private static SecretKey generateDecryptionKey() throws NoSuchAlgorithmException {
        // Generate or retrieve the decryption key used to decrypt the secret key
        // This could be a master key stored securely elsewhere
        // For simplicity, we generate a new key here
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    private static String encryptSecretKey(SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        byte[] iv = generateRandomIV();
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());
        byte[] combinedData = ByteBuffer.allocate(iv.length + encryptedKey.length)
                .put(iv)
                .put(encryptedKey)
                .array();
        return Base64.getEncoder().encodeToString(combinedData);
    }

    private static byte[] generateRandomIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }


}
