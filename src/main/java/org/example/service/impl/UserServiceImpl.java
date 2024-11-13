package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.UserService;

import java.sql.SQLException;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepositoryImpl();

    @Override
    public User registerUser(User user) {
        boolean alreadyExist = findByUserNameOrEmail(user.getUsername(), user.getEmail());
        if (alreadyExist) {
            System.out.println("Already exist");
            return null;
        }
        return userRepository.registerUser(user);
    }

    @Override
    public User loginUser(String username, String password, String email) throws SQLException {
        return userRepository.loginUser(username, password, email);
    }

    @Override
    public boolean updateUserProfile(int userId, String displayName, String bio, String username, String newPassword) throws SQLException {
        return userRepository.updateUserProfile(userId, displayName, bio, username, newPassword);
    }

    @Override
    public boolean findByUserNameOrEmail(String username, String email) {
        return userRepository.findByUserNameOrEmail(username, email);
    }
}
