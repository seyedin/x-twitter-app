package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {

    private static final String SAVE_USER_QUERY = """
            INSERT INTO Users (username, display_name, email, password, bio)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static String FIND_USER_BY_USERNAME_EMAIL_PASSWORD_QUERY = """
            select *
            from users u
            where (u.username = ? and password = ?)
               or (u.email = ? and u.password = ?)
            """;

    private static String UPDATE_USER_QUERY = """
            UPDATE users SET display_name = ?, bio = ?, username = ?, password = ? WHERE user_id = ?
            """;

    private static final String FIND_USER_BY_USERNAME_OR_EMAIL = """
            select *
                        from users u
                        where u.username = ? 
                           or u.email = ? 
            """;


    @Override
    public User registerUser(User user) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(SAVE_USER_QUERY);
            String hashedPassword = Utils.hashPassword(user.getPassword());
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getDisplayName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setString(5, user.getBio());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                user.setUserId(id);
                return user;
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User loginUser(String username, String password, String email) throws SQLException {
        try {
            String hashedPassword = Utils.hashPassword(password);
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(FIND_USER_BY_USERNAME_EMAIL_PASSWORD_QUERY);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, hashedPassword);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

             User user = new User();
             user.setUserId(resultSet.getInt("id"));
             user.setDisplayName(resultSet.getString("display_name"));
             user.setEmail(resultSet.getString("email"));
             user.setUsername(resultSet.getString("username"));
             user.setPassword(resultSet.getString("password"));
             user.setBio(resultSet.getString("bio"));
             user.setCreateDate(resultSet.getTimestamp("create_date").toLocalDateTime());

             return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateUserProfile(int userId, String displayName, String bio, String username, String newPassword) throws SQLException {

        try {
            String hashedPassword = Utils.hashPassword(newPassword);
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_USER_QUERY);
            preparedStatement.setString(1, displayName);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setInt(5, userId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was updated
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean findByUserNameOrEmail(String username, String email) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(FIND_USER_BY_USERNAME_OR_EMAIL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
