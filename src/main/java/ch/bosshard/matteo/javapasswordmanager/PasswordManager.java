package ch.bosshard.matteo.javapasswordmanager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PasswordManager {
    private String hashedMasterPassword;
    private String salt;
    private List<PasswordEntry> passwordEntries = new ArrayList<>();

    // Generate a random salt
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // Hash a password with a salt
    private static String hashPassword(String password, byte[] salt) throws Exception {
        int iterations = 10000;
        int keyLength = 256;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = f.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // Set the master password
    public void setMasterPassword(String masterPassword) {
        try {
            byte[] saltBytes = generateSalt();
            this.salt = Base64.getEncoder().encodeToString(saltBytes);
            this.hashedMasterPassword = hashPassword(masterPassword, saltBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Validate the master password
    public boolean validateMasterPassword(String inputPassword) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(this.salt);
            String hashedInputPassword = hashPassword(inputPassword, saltBytes);
            return hashedMasterPassword.equals(hashedInputPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add a password for a service
    public void addPassword(String service, String plaintextPassword) {
        try {
            byte[] saltBytes = generateSalt();
            String hashedPassword = hashPassword(plaintextPassword, saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);
            passwordEntries.add(new PasswordEntry(service, plaintextPassword, hashedPassword, salt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retrieve all stored password entries
    public List<PasswordEntry> getPasswordEntries() {
        return passwordEntries;
    }

    public String getHashedPassword() {
        return hashedMasterPassword;
    }

    public String getSalt() {
        return salt;
    }
}
