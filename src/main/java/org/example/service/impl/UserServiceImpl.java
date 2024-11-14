package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.UserService;

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
    public User loginUser(String username, String password, String email) {
        return userRepository.loginUser(username, password, email);
    }

    @Override
    public boolean updateUsername(int userId, String username) {
        return userRepository.updateUsername(userId, username);
    }

    @Override
    public boolean updateDisplayName(int userId, String displayName) {
        return userRepository.updateDisplayName(userId, displayName);
    }

    @Override
    public boolean updateBio(int userId, String bio) {
        return userRepository.updateBio(userId, bio);
    }

    @Override
    public boolean updatePassword(int userId, String password) {
        return userRepository.updatePassword(userId, password);
    }

    @Override
    public boolean findByUserNameOrEmail(String username, String email) {
        return userRepository.findByUserNameOrEmail(username, email);
    }
}
