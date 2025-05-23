package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.encryption.SecretEncryptionUtil;
import ch.bbw.pr.tresorbackend.model.Secret;
import ch.bbw.pr.tresorbackend.model.User;
import ch.bbw.pr.tresorbackend.repository.SecretRepository;
import ch.bbw.pr.tresorbackend.repository.UserRepository;
import ch.bbw.pr.tresorbackend.service.SecretService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SecretServiceImpl – mit benutzerbezogener AES-Verschlüsselung
 */
@Service
@AllArgsConstructor
public class SecretServiceImpl implements SecretService {

   private SecretRepository secretRepository;
   private UserRepository userRepository;

   @Override
   public Secret createSecret(Secret secret) {
      return secretRepository.save(secret);
   }

   @Override
   public Secret saveSecret(Long userId, String title, String content) {
       String encrypted = encryptContent(userId, content);
       Secret secret = new Secret(null, userId, title, encrypted);
       return secretRepository.save(secret);
   }

   @Override
   public List<Secret> getSecrets(Long userId) {
      return secretRepository.findByUserId(userId).stream()
              .map(secret -> {
                  String decrypted = decryptContent(userId, secret.getEncryptedContent());
                  secret.setEncryptedContent(decrypted);
                  return secret;
              }).collect(Collectors.toList());
  }

   @Override
   public Secret getSecretById(Long secretId) {
      return secretRepository.findById(secretId).orElse(null);
   }

   @Override
   public List<Secret> getAllSecrets() {
      return secretRepository.findAll();
   }

   @Override
   public Secret updateSecret(Secret secret) {
      return secretRepository.save(secret);
   }

   @Override
   public void deleteSecret(Long secretId) {
      secretRepository.deleteById(secretId);
   }

   @Override
   public List<Secret> getSecretsByUserId(Long userId) {
      return secretRepository.findByUserId(userId);
   }

   public String encryptContent(Long userId, String content) {  // verschlüsseln
      User user = userRepository.findById(userId).orElseThrow();
      return SecretEncryptionUtil.encrypt(content, user.getPassword());
   }

   public String decryptContent(Long userId, String encrypted) { // entschlüsseln
      User user = userRepository.findById(userId).orElseThrow();
      return SecretEncryptionUtil.decrypt(encrypted, user.getPassword());
   }
}
