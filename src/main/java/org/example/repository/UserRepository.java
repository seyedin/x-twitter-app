package org.example.repository;

import org.example.model.User;

public interface UserRepository {
    User registerUser(User user);

    User loginUser(String username, String password, String email);

    boolean updateDisplayName(int userId, String displayName);

    boolean updateBio(int userId, String bio);

    boolean updatePassword(int userId, String password);

    boolean updateUsername(int userId, String username);

    boolean findByUserNameOrEmail(String username, String email);
}
