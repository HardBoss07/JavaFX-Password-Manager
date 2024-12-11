package ch.bosshard.matteo.javapasswordmanager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PasswordManager {
    private String hashedMasterPassword;
    private String salt;
    private List<PasswordEntry> passwordEntries = new ArrayList<>();
    private static final String FILE_NAME = "passwords.dat";

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

    private void saveMasterPassword() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write("hashedMasterPassword=" + hashedMasterPassword + "\n");
            writer.write("salt=" + salt + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMasterPassword() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("hashedMasterPassword=")) {
                    this.hashedMasterPassword = line.split("=")[1];
                } else if (line.startsWith("salt=")) {
                    this.salt = line.split("=")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePasswordEntries(String masterPassword) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            // Save each password entry
            for (PasswordEntry entry : passwordEntries) {
                if (entry.getPlaintextPassword() == null || entry.getPlaintextPassword().isEmpty()) {
                    System.err.println("Skipping empty password for service: " + entry.getService());
                    continue; // Skip invalid entries
                }

                // Encrypt the password using AESUtil class
                String encryptedPassword = AESUtil.encrypt(entry.getPlaintextPassword(), masterPassword);
                writer.write("service=" + entry.getService() + ",password=" + encryptedPassword + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPasswordEntries(String masterPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            passwordEntries.clear(); // Clear the list before loading new entries
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("service=")) {
                    String[] parts = line.split(",");
                    String service = parts[0].split("=")[1];
                    String encryptedPassword = parts[1].split("=")[1];

                    // Decrypt the password using AESUtil class
                    String decryptedPassword = AESUtil.decrypt(encryptedPassword, masterPassword);
                    passwordEntries.add(new PasswordEntry(service, decryptedPassword, null, null));
                }
            }
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
