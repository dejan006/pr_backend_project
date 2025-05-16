package ch.bbw.pr.tresorbackend.util;

import org.jasypt.util.text.AES256TextEncryptor;

/**
 * EncryptUtil
 * Encrypts and decrypts content using Jasypt (AES 256).
 * Key is user-specific (e.g. passwordHash).
 * @author Peter Rutschmann
 */
public class EncryptUtil {

    private final AES256TextEncryptor textEncryptor;

    public EncryptUtil(String secretKey) {
        this.textEncryptor = new AES256TextEncryptor();
        this.textEncryptor.setPassword(secretKey); // Verwende PasswortHash oder benutzerabhängigen Key
    }

    public String encrypt(String data) {
        return textEncryptor.encrypt(data);
    }

    public String decrypt(String data) {
        return textEncryptor.decrypt(data);
    }
}
