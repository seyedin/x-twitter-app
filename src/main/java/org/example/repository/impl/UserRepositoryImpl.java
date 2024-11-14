package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserRepositoryImpl implements UserRepository {

    private static final String SAVE_USER_QUERY = """
            INSERT INTO Users (username, display_name, email, password, bio)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String FIND_USER_BY_USERNAME_OR_EMAIL = """
            select *
                        from users u
                        where u.username = ? 
                           or u.email = ? 
            """;
    private static String FIND_USER_BY_USERNAME_EMAIL_PASSWORD_QUERY = """
            select *
            from users u
            where (u.username = ? and password = ?)
               or (u.email = ? and u.password = ?)
            """;
    private static String UPDATE_USERNAME_QUERY = """
            UPDATE users SET  username = ? WHERE id = ?
            """;
    private static String UPDATE_DISPLAYNAME_QUERY = """
            UPDATE users SET display_name = ? WHERE id = ?
            """;
    private static String UPDATE_BIO_QUERY = """
            UPDATE users SET bio = ? WHERE id = ?
            """;
    private static String UPDATE_PASSWORD_QUERY = """
            UPDATE users SET password = ? WHERE id = ?
            """;

    @Override
    public User registerUser(User user) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Use RETURN_GENERATED_KEYS to retrieve the auto-generated ID.
            PreparedStatement preparedStatement = conn.prepareStatement(SAVE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);

            String hashedPassword = Utils.hashPassword(user.getPassword());
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getDisplayName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setString(5, user.getBio());

            // Execute the update and check if a row was inserted.
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the generated keys.
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1); // Retrieve the generated ID.
                    user.setUserId(id);
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User loginUser(String username, String password, String email) {
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
                user.setCreateDate(resultSet.getTimestamp("created_date").toLocalDateTime());

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUsername(int userId, String username) {

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_USERNAME_QUERY);

            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was updated
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateDisplayName(int userId, String displayName) {

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_DISPLAYNAME_QUERY);

            preparedStatement.setString(1, displayName);
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was updated
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateBio(int userId, String bio) {

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_BIO_QUERY);

            preparedStatement.setString(1, bio);
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was updated
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePassword(int userId, String password) {

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_PASSWORD_QUERY);

            String hashedPassword = Utils.hashPassword(password);
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);

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
