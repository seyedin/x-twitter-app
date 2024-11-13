package org.example.service;

import org.example.model.Tweet;

import java.sql.SQLException;
import java.util.List;

public interface TweetService {
    List<Tweet> getTweetsAndRetweetsByUserId(int userId);

    List<Tweet> getTweetByUserId(int userId);

    List<Tweet> getAllTweets();

    Integer postTweet(int userId, String content);

    boolean updateTweet(int tweetId, String newContent) throws SQLException;

    boolean deleteTweetById(int tweetId) throws SQLException;

    Integer likeTweet(int userId, int tweetId);

    Integer dislikeTweet(int userId, int tweetId);

    List<Tweet> getTweetsByTag(String tagName) throws SQLException;
}
