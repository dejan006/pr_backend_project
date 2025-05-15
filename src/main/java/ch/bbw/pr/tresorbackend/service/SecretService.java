package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.model.Secret;
import java.util.List;

/**
 * SecretService
 * @author Peter Rutschmann
 */
public interface SecretService {
   Secret createSecret(Secret secret);

   Secret getSecretById(Long secretId);

   List<Secret> getAllSecrets();

   Secret updateSecret(Secret secret);

   void deleteSecret(Long secretId);

   List<Secret> getSecretsByUserId(Long userId);

   Secret saveSecret(Long userId, String title, String content);

   List<Secret> getSecrets(Long userId);

   String encryptContent(Long userId, String content);
   String decryptContent(Long userId, String encrypted);   

}