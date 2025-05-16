package ch.bbw.pr.tresorbackend.controller;

import ch.bbw.pr.tresorbackend.model.Secret;
import ch.bbw.pr.tresorbackend.model.NewSecret;
import ch.bbw.pr.tresorbackend.model.EncryptCredentials;
import ch.bbw.pr.tresorbackend.model.User;
import ch.bbw.pr.tresorbackend.service.SecretService;
import ch.bbw.pr.tresorbackend.service.UserService;
import ch.bbw.pr.tresorbackend.service.impl.SecretServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SecretController – AES-basiert mit individuellem User-Schlüssel
 * @author Peter Rutschmann
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/secrets")
public class SecretController {

   private SecretService secretService;
   private UserService userService;

   // create secret REST API
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping
   public ResponseEntity<String> createSecret(@Valid @RequestBody NewSecret newSecret, BindingResult bindingResult) {
      if (bindingResult.hasErrors()) {
         List<String> errors = bindingResult.getFieldErrors().stream()
               .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
               .collect(Collectors.toList());
         JsonArray arr = new JsonArray();
         errors.forEach(arr::add);
         JsonObject obj = new JsonObject();
         obj.add("message", arr);
         return ResponseEntity.badRequest().body(new Gson().toJson(obj));
      }

      User user = userService.findByEmail(newSecret.getEmail());
      String contentString = newSecret.getContent().toString();
      String encrypted = secretService.encryptContent(user.getId(), contentString);

      Secret secret = new Secret(null, user.getId(), newSecret.getTitle(), encrypted);
      secretService.createSecret(secret);

      JsonObject obj = new JsonObject();
      obj.addProperty("answer", "Secret saved");
      return ResponseEntity.accepted().body(new Gson().toJson(obj));
   }

   // Get secrets by userId
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/byuserid")
   public ResponseEntity<List<Secret>> getSecretsByUserId(@RequestBody EncryptCredentials credentials) {
      List<Secret> secrets = secretService.getSecretsByUserId(credentials.getUserId());
      if (secrets.isEmpty()) {
         return ResponseEntity.notFound().build();
      }

      for (Secret secret : secrets) {
         String decrypted = ((SecretServiceImpl) secretService)
               .decryptContent(credentials.getUserId(), secret.getEncryptedContent());
         secret.setEncryptedContent(decrypted);
      }

      return ResponseEntity.ok(secrets);
   }

   // Get secrets by email
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PostMapping("/byemail")
   public ResponseEntity<List<Secret>> getSecretsByEmail(@RequestBody EncryptCredentials credentials) {
      User user = userService.findByEmail(credentials.getEmail());
      List<Secret> secrets = secretService.getSecretsByUserId(user.getId());
      if (secrets.isEmpty()) {
         return ResponseEntity.notFound().build();
      }

      for (Secret secret : secrets) {
         String decrypted = ((SecretServiceImpl) secretService)
               .decryptContent(user.getId(), secret.getEncryptedContent());
         secret.setEncryptedContent(decrypted);
      }

      return ResponseEntity.ok(secrets);
   }

   // Get all secrets
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @GetMapping
   public ResponseEntity<List<Secret>> getAllSecrets() {
      List<Secret> secrets = secretService.getAllSecrets();
      return new ResponseEntity<>(secrets, HttpStatus.OK);
   }

   // Update secret
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @PutMapping("{id}")
   public ResponseEntity<String> updateSecret(
         @PathVariable("id") Long secretId,
         @Valid @RequestBody NewSecret newSecret,
         BindingResult bindingResult) {

      if (bindingResult.hasErrors()) {
         List<String> errors = bindingResult.getFieldErrors().stream()
               .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
               .collect(Collectors.toList());
         JsonArray arr = new JsonArray();
         errors.forEach(arr::add);
         JsonObject obj = new JsonObject();
         obj.add("message", arr);
         return ResponseEntity.badRequest().body(new Gson().toJson(obj));
      }

      Secret dbSecrete = secretService.getSecretById(secretId);
      if (dbSecrete == null) {
         JsonObject obj = new JsonObject();
         obj.addProperty("answer", "Secret not found in db");
         return ResponseEntity.badRequest().body(new Gson().toJson(obj));
      }

      User user = userService.findByEmail(newSecret.getEmail());
      if (!dbSecrete.getUserId().equals(user.getId())) {
         JsonObject obj = new JsonObject();
         obj.addProperty("answer", "Secret has not same user id");
         return ResponseEntity.badRequest().body(new Gson().toJson(obj));
      }

      String contentString = newSecret.getContent().toString();
      String encrypted = secretService.encryptContent(user.getId(), contentString); // secret als klartext geschickt

      Secret updatedSecret = new Secret(secretId, user.getId(), newSecret.getTitle(), encrypted); // neues secret-objekt wird mit verschlüsseltem inhalt erstellt
      secretService.updateSecret(updatedSecret); // db speichern

      JsonObject obj = new JsonObject();
      obj.addProperty("answer", "Secret updated");
      return ResponseEntity.accepted().body(new Gson().toJson(obj));
   }

   // Delete secret
   @CrossOrigin(origins = "${CROSS_ORIGIN}")
   @DeleteMapping("{id}")
   public ResponseEntity<String> deleteSecret(@PathVariable("id") Long secretId) {
      secretService.deleteSecret(secretId);
      return new ResponseEntity<>("Secret successfully deleted!", HttpStatus.OK);
   }
}