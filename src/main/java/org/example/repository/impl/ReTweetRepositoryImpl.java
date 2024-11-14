package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.Retweet;
import org.example.model.Tweet;
import org.example.model.User;
import org.example.repository.ReTweetRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReTweetRepositoryImpl implements ReTweetRepository {
    private static final String INSERT_RETWEET_QUERY = """
            INSERT INTO Retweets (user_id, original_tweet_id, parent_retweet_id, additional_content)
            VALUES (?, ?, ?, ?);
            """;
    private static final String GET_USER_TWEET_QUERY = """
            SELECT
                t.user_id,
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
                u.id is not null ;
            """;

    @Override
    public Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_RETWEET_QUERY, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, userId);
            pstmt.setInt(2, originalTweetId);

            // Use setObject to allow null value for parentRetweetId
            if (parentRetweetId != null) {
                pstmt.setInt(3, parentRetweetId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            pstmt.setString(4, additionalContent);

            // Use executeUpdate() for INSERT
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated retweet ID
                }
            } else {
                System.out.println("Creating ReTweet failed, no ID obtained.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Retweet getRetweetById(int retweetId) {
        Retweet retweet = new Retweet();
        try {
            String sql = """
                    select * from  retweets where id =?
                    """;
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement(sql);
            prepareStatement.setInt(1, retweetId);

            ResultSet rs = prepareStatement.executeQuery();
            while (rs.next()) {
                User user = new User();
                Tweet originalTweet = new Tweet();
                Retweet parentRetweet = new Retweet();


                user.setUserId(rs.getInt("user_id"));
                parentRetweet.setRetweetId(rs.getInt("parent_retweet_id") == 0 ? null : rs.getInt("parent_retweet_id"));
                originalTweet.setId(rs.getInt("original_tweet_id") == 0 ? null : rs.getInt("original_tweet_id"));
                retweet.setCreateDate(rs.getTimestamp("created_date").toLocalDateTime());
                retweet.setRetweetId(rs.getInt("id"));
                retweet.setAdditionalContent(rs.getString("additional_content"));

                retweet.setUser(user);
                retweet.setOriginalTweetId(originalTweet);
                retweet.setParentRetweetId(parentRetweet);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retweet;
    }

    @Override
    public Integer postReTweet(int userId, String content, Retweet retweet) {
        try {
            content = content + " === " + retweet.getAdditionalContent();
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = null;
            String sql = "";

            if (retweet.getParentRetweetId() == null || retweet.getParentRetweetId().getRetweetId() == null) {
                Integer originalTweetId = retweet.getOriginalTweetId().getId();
                sql = """
                        INSERT INTO Retweets (user_id, original_tweet_id, parent_retweet_id, additional_content)
                        VALUES (?, ?, NULL, ?)
                        """;
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Request generated keys
                pstmt.setInt(1, userId);
                pstmt.setInt(2, originalTweetId);
                pstmt.setString(3, content);
            } else if (retweet.getOriginalTweetId() == null || retweet.getOriginalTweetId().getId() == null) {
                Integer parentRetweetId = retweet.getParentRetweetId().getRetweetId();
                sql = """
                        INSERT INTO Retweets (user_id, original_tweet_id, parent_retweet_id, additional_content)
                        VALUES (?,  NULL, ?,?)
                        """;
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Request generated keys
                pstmt.setInt(1, userId);
                pstmt.setInt(2, parentRetweetId);
                pstmt.setString(3, content);
            }

            // Execute the statement and check for generated keys
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Tweet failed, no rows affected.");
            }

            // Retrieve generated key
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
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
    public List<Tweet> getTweetsAndRetweets() {
        List<Tweet> tweets = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(GET_USER_TWEET_QUERY);

            //     pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // توییت اصلی
                Tweet tweet = new Tweet();
                tweet.setUserId(rs.getInt("user_id"));
                tweet.setId(rs.getInt("tweet_id"));
                tweet.setContent(rs.getString("tweet_content"));
                tweet.setCreateDate(rs.getTimestamp("tweet_created_date") != null ? rs.getTimestamp("tweet_created_date").toLocalDateTime() : null);

                // ری‌توییت مستقیم
                Retweet retweet = new Retweet();
                retweet.setRetweetId(rs.getInt("retweet_id"));
                retweet.setAdditionalContent(rs.getString("retweet_content"));
                retweet.setCreateDate(rs.getTimestamp("retweet_created_date") != null ? rs.getTimestamp("retweet_created_date").toLocalDateTime() : null);

                // ری‌توییت از ری‌توییت
                Retweet retweetOfRetweet = new Retweet();
                retweetOfRetweet.setRetweetId(rs.getInt("retweet_of_retweet_id"));
                retweetOfRetweet.setAdditionalContent(rs.getString("retweet_of_retweet_content"));
                retweetOfRetweet.setCreateDate(rs.getTimestamp("retweet_of_retweet_created_date") != null ? rs.getTimestamp("retweet_of_retweet_created_date").toLocalDateTime() : null);

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