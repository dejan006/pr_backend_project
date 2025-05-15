package ch.bbw.pr.tresorbackend.service;

import ch.bbw.pr.tresorbackend.model.User;
import java.util.List;

/**
 * UserService Interface – definiert alle User-Operationen
 * @author Peter Rutschmann
 */
public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    User findByEmail(String email);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Long userId);

    boolean login(String email, String password);
}
