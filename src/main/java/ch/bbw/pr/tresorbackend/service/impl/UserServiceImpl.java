package ch.bbw.pr.tresorbackend.service.impl;

import ch.bbw.pr.tresorbackend.model.User;
import ch.bbw.pr.tresorbackend.repository.UserRepository;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;

   @Value("${security.pepper}")
   private String pepper;

   public UserServiceImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   @Override
   public User createUser(User user) {
      String salt = PasswordUtil.generateSalt();
      String hashed = PasswordUtil.hashPassword(user.getPasswordHash(), salt, pepper);
      user.setSalt(salt);
      user.setPasswordHash(hashed);
      return userRepository.save(user);
   }

   @Override
   public User getUserById(Long userId) {
      return userRepository.findById(userId).orElse(null);
   }

   @Override
   public User findByEmail(String email) {
      return userRepository.findByEmail(email).orElse(null);
   }

   @Override
   public List<User> getAllUsers() {
      return (List<User>) userRepository.findAll();
   }

   @Override
   public User updateUser(User user) {
      User existingUser = userRepository.findById(user.getId()).orElse(null);
      if (existingUser == null) return null;

      existingUser.setFirstName(user.getFirstName());
      existingUser.setLastName(user.getLastName());
      existingUser.setEmail(user.getEmail());
      // Passwort wird NICHT geändert hier – du kannst aber ein eigenes Passwort-Update-Feature machen

      return userRepository.save(existingUser);
   }

   @Override
   public void deleteUser(Long userId) {
      userRepository.deleteById(userId);
   }

   @Override
   public boolean login(String email, String password) {
      
      Optional<User> userOpt = userRepository.findByEmail(email);
      if (userOpt.isEmpty()) return false;
      User user = userOpt.get();
      String hashed = PasswordUtil.hashPassword(password, user.getSalt(), pepper);
      return hashed.equals(user.getPasswordHash());
   }
}
