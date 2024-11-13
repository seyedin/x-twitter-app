package org.example.repository;

import org.example.model.User;

import java.sql.SQLException;

public interface UserRepository {
    User registerUser(User user);

    User loginUser(String username, String password, String email);

    boolean updateUserProfile(int userId, String displayName, String bio, String username, String newPassword) ;

    boolean findByUserNameOrEmail(String username, String email);
}
