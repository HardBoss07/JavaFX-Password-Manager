package ch.bosshard.matteo.javapasswordmanager;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.util.Base64;

public class AESUtil {
    private static final String AES = "AES";
    private static final String AES_CIPHER = "AES/CBC/PKCS5Padding";

    // Encrypt a plaintext string using AES
    public static String encrypt(String plaintext, String key) throws Exception {
        // Generate a 256-bit key from the master password
        byte[] keyBytes = generateKey(key);

        // Create AES secret key and initialization vector
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES);
        IvParameterSpec iv = new IvParameterSpec(keyBytes, 0, 16);

        // Initialize the cipher
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        // Encrypt the plaintext
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypt an encrypted string using AES
    public static String decrypt(String encryptedText, String key) throws Exception {
        // Generate a 256-bit key from the master password
        byte[] keyBytes = generateKey(key);

        // Create AES secret key and initialization vector
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES);
        IvParameterSpec iv = new IvParameterSpec(keyBytes, 0, 16);

        // Initialize the cipher
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        // Decrypt the ciphertext
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted);
    }

    // Generate a 256-bit key from the master password
    private static byte[] generateKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = key.getBytes("UTF-8");
        return sha.digest(keyBytes);
    }
}
