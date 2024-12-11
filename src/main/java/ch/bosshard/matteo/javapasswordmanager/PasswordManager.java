package ch.bosshard.matteo.javapasswordmanager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordManager {
    private String hashedMasterPassword;
    private String salt;

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static String hashPassword(String password, byte[] salt) throws Exception{
        int iterations = 10000;
        int keyLength = 256;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = f.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public void setMasterPassword(String masterPassword) {
        try {
            byte[] saltBytes = generateSalt();
            this.salt = Base64.getEncoder().encodeToString(saltBytes);
            this.hashedMasterPassword = hashPassword(masterPassword, saltBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public String getHashedPassword() {
        return hashedMasterPassword;
    }

    public String getSalt() {
        return salt;
    }

}
