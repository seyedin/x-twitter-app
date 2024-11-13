package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.repository.ReTweetRepository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ReTweetRepositoryImpl implements ReTweetRepository {
    private static final String INSERT_RETWEET_QUERY = """
            INSERT INTO Retweets (user_id, original_tweet_id, parent_retweet_id, additional_content)
            VALUES (?, ?, ?, ?)
            """;

    @Override
    public Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_RETWEET_QUERY);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, originalTweetId);
            pstmt.setInt(3, parentRetweetId);
            pstmt.setString(4, additionalContent);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                System.out.println("Creating ReTweet failed, no ID obtained.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}