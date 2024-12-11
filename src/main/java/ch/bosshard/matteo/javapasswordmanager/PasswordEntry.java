package ch.bosshard.matteo.javapasswordmanager;

public class PasswordEntry {
    private String service;
    private String plaintextPassword;
    private String hashedPassword;
    private String salt;

    public PasswordEntry(String service, String plaintextPassword, String hashedPassword, String salt) {
        this.service = service;
        this.plaintextPassword = plaintextPassword;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    // Getters and setters
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPlaintextPassword() {
        return plaintextPassword;
    }

    public void setPlaintextPassword(String plaintextPassword) {
        this.plaintextPassword = plaintextPassword;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
