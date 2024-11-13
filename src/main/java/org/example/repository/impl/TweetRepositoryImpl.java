package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.Retweet;
import org.example.model.Tweet;
import org.example.repository.TweetRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TweetRepositoryImpl implements TweetRepository {

    private static final String GET_USER_TWEET_QUERY = """
            SELECT
                t.id as tweet_id,
                t.content AS tweet_content,
                t.created_date AS tweet_created_date,
                r.id as retweet_id,
                r.additional_content AS retweet_content,
                r.created_date AS retweet_created_date,
                rr.id AS retweet_of_retweet_id,
                rr.additional_content AS retweet_of_retweet_content,
                rr.created_date AS retweet_of_retweet_created_date
            FROM
               Users u
                    LEFT JOIN
                Tweets t ON u.id = t.user_id
                    LEFT JOIN
                Retweets r ON u.id = r.user_id AND (r.original_tweet_id = t.id OR r.parent_retweet_id IS NULL)
                    LEFT JOIN
                Retweets rr ON r.id = rr.parent_retweet_id
            WHERE
                u.id = ?;
            
            """;

    private static final String GET_TWEET_BY_USER_ID_QUERY = """
            select tweets.* from  tweets join users on tweets.user_id = users.id where  user_id = ?
            """;

    private static final String INSERT_TWEET_QUERY = """
            INSERT INTO Tweets (user_id, content)
            VALUES (?,  ?)
            """;

    private static final String UPDATE_TWEET_QUERY = """
            update Tweets set  content = ? 
            where id = ?
            """;

    private static final String DELETE_TWEET_QUERY = """
            DELETE FROM Tweets
            WHERE id = ?
            """;

    private static final String LIKE_TWEET_QUERY = """
            INSERT INTO Likes (user_id, tweet_id)
            VALUES (?, ?);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM Likes WHERE tweet_id = ?;
            """;

    private static final String INSERT_DISLIKE_QUERY = """
            INSERT INTO DisLikes (user_id, tweet_id)
            VALUES (?, ?);
            """;

    private static final String GET_ALL_TWEETS_QUERY = """
            select tweets.* from  tweets
            """;

    private static final String GET_TWEETS_BY_TAG_QUERY = """
            SELECT t.*
            FROM Tweets t
                     JOIN Tweet_Tag tt ON t.id = tt.tweet_id
                     JOIN Tags tg ON tt.tag_id = tg.id
            WHERE tg.name = ?
            """;

    @Override
    public List<Tweet> getTweetsAndRetweetsByUserId(int userId) {
        List<Tweet> tweets = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(GET_USER_TWEET_QUERY);

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // توییت اصلی
                Tweet tweet = new Tweet();
                tweet.setId(rs.getInt("tweet_id"));
                tweet.setContent(rs.getString("tweet_content"));
                tweet.setCreateDate(rs.getTimestamp("tweet_created_date").toLocalDateTime());

                // ری‌توییت مستقیم
                Retweet retweet = new Retweet();
                retweet.setRetweetId(rs.getInt("retweet_id"));
                retweet.setAdditionalContent(rs.getString("retweet_content"));
                retweet.setCreateDate(rs.getTimestamp("retweet_created_date").toLocalDateTime());

                // ری‌توییت از ری‌توییت
                Retweet retweetOfRetweet = new Retweet();
                retweetOfRetweet.setRetweetId(rs.getInt("retweet_of_retweet_id"));
                retweetOfRetweet.setAdditionalContent(rs.getString("retweet_of_retweet_content"));
                retweetOfRetweet.setCreateDate(rs.getTimestamp("retweet_of_retweet_created_date").toLocalDateTime());

                // اضافه کردن ری‌توییت‌ها به توییت
                List<Retweet> childRetweets = List.of(retweetOfRetweet);
                retweet.setChildRetweets(childRetweets);

                List<Retweet> retweets = List.of(retweet);
                tweet.setRetweets(retweets);

                tweets.add(tweet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    @Override
    public List<Tweet> getTweetByUserId(int userId) {
        List<Tweet> tweets = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(GET_TWEET_BY_USER_ID_QUERY);

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // توییت اصلی
                Tweet tweet = new Tweet();
                tweet.setId(rs.getInt("id"));
                tweet.setContent(rs.getString("content"));
                tweet.setCreateDate(rs.getTimestamp("created_date").toLocalDateTime());

                tweets.add(tweet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    @Override
    public List<Tweet> getAllTweets() {
        List<Tweet> tweets = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(GET_ALL_TWEETS_QUERY);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // توییت اصلی
                Tweet tweet = new Tweet();
                tweet.setId(rs.getInt("id"));
                tweet.setContent(rs.getString("content"));
                tweet.setCreateDate(rs.getTimestamp("created_date").toLocalDateTime());

                tweets.add(tweet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    @Override
    public Integer postTweet(int userId, String content) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_TWEET_QUERY);
            pstmt.setInt(1, userId);
            pstmt.setString(2, content);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new SQLException("Creating Tweet failed, no ID obtained.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateTweet(int tweetId, String newContent) throws SQLException {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement(UPDATE_TWEET_QUERY);
            prepareStatement.setInt(1, tweetId);
            prepareStatement.setString(2, newContent);

            int affectedRows = prepareStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was updated
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("updating Tweet failed, no ID obtained.");
        }
    }

    @Override
    public boolean deleteTweetById(int tweetId) throws SQLException {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement(DELETE_TWEET_QUERY);
            prepareStatement.setInt(1, tweetId);
            int affectedRows = prepareStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was deleted
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Deleting Tweet failed, no ID obtained.");
        }
    }

    @Override
    public Integer likeTweet(int userId, int tweetId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(LIKE_TWEET_QUERY);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, tweetId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new SQLException("failed to like tweet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer dislikeTweet(int userId, int tweetId) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Delete the like
            PreparedStatement deleteStmt = conn.prepareStatement(DELETE_LIKE_QUERY);
            deleteStmt.setInt(1, tweetId);
            deleteStmt.setInt(2, userId);
            deleteStmt.executeUpdate();

            // Insert the dislike
            PreparedStatement insertStmt = conn.prepareStatement(INSERT_DISLIKE_QUERY);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, tweetId);
            ResultSet resultSet = insertStmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1); // Return the generated ID
            } else {
                throw new SQLException("Failed to dislike tweet");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // متد برای بازیابی توییت‌ها بر اساس تگ
    @Override
    public List<Tweet> getTweetsByTag(String tagName) throws SQLException {
        List<Tweet> tweets = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(GET_TWEETS_BY_TAG_QUERY);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                Tweet tweet = new Tweet();
                tweet.setId(rs.getInt("id"));
                tweet.setContent(rs.getString("content"));
                tweet.setCreateDate(rs.getTimestamp("created_date").toLocalDateTime());

                tweets.add(tweet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }
}
