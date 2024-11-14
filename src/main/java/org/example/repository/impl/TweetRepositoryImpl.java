package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.Retweet;
import org.example.model.Tweet;
import org.example.repository.TweetRepository;

import java.sql.*;
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
            INSERT INTO Tweets (user_id, content, created_date)
            VALUES (?, ?, current_date);
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
            DELETE FROM Likes WHERE user_id = ? AND tweet_id = ?;
            """;

    private static final String INSERT_DISLIKE_QUERY = """
            INSERT INTO DisLikes (user_id, tweet_id)
            VALUES (?, ?) RETURNING id;
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

    private static final String GET_ALL_USERS_TWEETS_QUERY = """
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
                Timestamp tweetCreatedDate = rs.getTimestamp("tweet_created_date");
                tweet.setCreateDate(tweetCreatedDate != null ? tweetCreatedDate.toLocalDateTime() : null);

                // ری‌توییت مستقیم
                Retweet retweet = new Retweet();
                retweet.setRetweetId(rs.getInt("retweet_id"));
                retweet.setAdditionalContent(rs.getString("retweet_content"));
                Timestamp retweetCreatedDate = rs.getTimestamp("retweet_created_date");
                retweet.setCreateDate(retweetCreatedDate != null ? retweetCreatedDate.toLocalDateTime() : null);

                // ری‌توییت از ری‌توییت
                Retweet retweetOfRetweet = new Retweet();
                retweetOfRetweet.setRetweetId(rs.getInt("retweet_of_retweet_id"));
                retweetOfRetweet.setAdditionalContent(rs.getString("retweet_of_retweet_content"));
                Timestamp retweetOfRetweetCreatedDate = rs.getTimestamp("retweet_of_retweet_created_date");
                retweetOfRetweet.setCreateDate(retweetOfRetweetCreatedDate != null ? retweetOfRetweetCreatedDate.toLocalDateTime() : null);

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
    public Tweet getTweetById(int tweetId) {
        Tweet tweet = new Tweet();
        try {
            String sql = "select * from  tweets where id = ?";
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tweetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tweet.setId(rs.getInt("id"));
                tweet.setContent(rs.getString("content"));
                tweet.setCreateDate(rs.getTimestamp("created_date").toLocalDateTime());

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweet;
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
    public Tweet postTweet(int userId, String content) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Use RETURN_GENERATED_KEYS to retrieve the auto-generated ID.
            PreparedStatement preparedStatement = conn.prepareStatement(INSERT_TWEET_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, content);

            // Execute the update.
            int affectedRows = preparedStatement.executeUpdate();

            // Check if a row was inserted.
            if (affectedRows > 0) {
                // Get the generated keys.
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Tweet tweet = new Tweet();
                    tweet.setId(generatedKeys.getInt(1)); // Retrieve the generated ID.
                    tweet.setContent(content);
                    tweet.setUserId(userId);

                    return tweet;
                } else {
                    throw new SQLException("Creating Tweet failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateTweet(int tweetId, String newContent) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement(UPDATE_TWEET_QUERY);

            prepareStatement.setString(1, newContent);
            prepareStatement.setInt(2, tweetId);

            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                System.out.println("No tweet found with the given ID.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteTweetById(int tweetId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement(DELETE_TWEET_QUERY);
            prepareStatement.setInt(1, tweetId);
            int affectedRows = prepareStatement.executeUpdate();
            return affectedRows > 0; // Return true if a row was deleted
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer likeTweet(int userId, int tweetId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(LIKE_TWEET_QUERY);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, tweetId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return rowsAffected; // Optionally, return the number of rows affected
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
            PreparedStatement insertStmt = conn.prepareStatement(INSERT_DISLIKE_QUERY, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, tweetId);

            int rowsAffected = insertStmt.executeUpdate();

            // Check if any rows were inserted
            if (rowsAffected > 0) {
                // Retrieve generated keys (ID)
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated dislike ID
                }
            } else {
                throw new SQLException("Failed to insert dislike");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // متد برای بازیابی توییت‌ها بر اساس تگ
    @Override
    public List<Tweet> getTweetsByTag(String tagName) {
        List<Tweet> tweets = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();

            // ایجاد PreparedStatement و تنظیم پارامتر
            PreparedStatement preparedStatement = conn.prepareStatement(GET_TWEETS_BY_TAG_QUERY);
            preparedStatement.setString(1, tagName); // تنظیم پارامتر tagName

            ResultSet rs = preparedStatement.executeQuery();
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

    @Override
    public List<Tweet> getAllTweetsAndRetweets() {
        List<Tweet> tweets = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(GET_ALL_USERS_TWEETS_QUERY);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                // توییت اصلی
                Tweet tweet = new Tweet();
                tweet.setId(rs.getInt("tweet_id"));
                tweet.setContent(rs.getString("tweet_content"));
                Timestamp tweetCreatedDate = rs.getTimestamp("tweet_created_date");
                if (tweetCreatedDate != null) {
                    tweet.setCreateDate(tweetCreatedDate.toLocalDateTime());
                }

                // ری‌توییت مستقیم
                Retweet retweet = new Retweet();
                retweet.setRetweetId(rs.getInt("retweet_id"));
                retweet.setAdditionalContent(rs.getString("retweet_content"));
                Timestamp retweetCreatedDate = rs.getTimestamp("retweet_created_date");
                if (retweetCreatedDate != null) {
                    retweet.setCreateDate(retweetCreatedDate.toLocalDateTime());
                }

                // ری‌توییت از ری‌توییت
                Retweet retweetOfRetweet = new Retweet();
                retweetOfRetweet.setRetweetId(rs.getInt("retweet_of_retweet_id"));
                retweetOfRetweet.setAdditionalContent(rs.getString("retweet_of_retweet_content"));
                Timestamp retweetOfRetweetCreatedDate = rs.getTimestamp("retweet_of_retweet_created_date");
                if (retweetOfRetweetCreatedDate != null) {
                    retweetOfRetweet.setCreateDate(retweetOfRetweetCreatedDate.toLocalDateTime());
                }

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
}
