package com.decstorage.service;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptionService {

    private static final String ALGO = "AES/CBC/PKCS5Padding";

    // Generate a new random 256-bit AES key
    public static byte[] generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    // Encrypt: returns IV (16 bytes) + ciphertext
    public static byte[] encrypt(byte[] data, byte[] keyBytes) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGO);

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] encrypted = cipher.doFinal(data);

        // Prepend IV so we can decrypt later
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    // Decrypt: splits IV from ciphertext then decrypts
    public static byte[] decrypt(byte[] data, byte[] keyBytes) throws Exception {
        byte[] iv = new byte[16];
        System.arraycopy(data, 0, iv, 0, 16);

        byte[] ciphertext = new byte[data.length - 16];
        System.arraycopy(data, 16, ciphertext, 0, ciphertext.length);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    // Helper: byte[] to Base64 string (for storing key in DB)
    public static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // Helper: Base64 string back to byte[]
    public static byte[] fromBase64(String data) {
        return Base64.getDecoder().decode(data);
    }
}