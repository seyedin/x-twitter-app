package org.example.service;

import org.example.model.User;

import java.sql.SQLException;

public interface UserService {
    User registerUser(User user);

    User loginUser(String username, String password, String email) throws SQLException;

    boolean updateUserProfile(int userId, String displayName, String bio, String username, String newPassword) throws SQLException;

    boolean findByUserNameOrEmail(String username, String email);
}
