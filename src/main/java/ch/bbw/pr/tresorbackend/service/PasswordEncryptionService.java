package ch.bbw.pr.tresorbackend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Service;

/**
 * PasswordEncryptionService
 * 
 * @author Peter Rutschmann
 */
@Service
public class PasswordEncryptionService {

    @Value("${encryption.pepper}")
    private String pepper;

    public String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hashPassword(String password, String salt) {
        try {
            String combined = salt + password + pepper;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Hashen", e);
        }
    }
}