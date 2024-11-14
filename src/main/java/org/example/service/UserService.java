package org.example.service;

import org.example.model.User;

public interface UserService {
    User registerUser(User user);

    User loginUser(String username, String password, String email);

    boolean updateDisplayName(int userId, String displayName);

    boolean updateBio(int userId, String bio);

    boolean updateUsername(int userId, String username);

    boolean updatePassword(int userId, String password);

    boolean findByUserNameOrEmail(String username, String email);
}
