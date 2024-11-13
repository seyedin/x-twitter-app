package org.example.datasource;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    @Getter
    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/xtwitter", "Maktab", "Maktab123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
