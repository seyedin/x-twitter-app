package org.example.repository;

import org.example.model.Tweet;

import java.sql.SQLException;
import java.util.List;

public interface TweetRepository {
    List<Tweet> getTweetsAndRetweetsByUserId(int userId);

    List<Tweet> getTweetByUserId(int userId);

    List<Tweet> getAllTweets();

    Tweet postTweet(int userId, String content);

    boolean updateTweet(int tweetId, String newContent) ;

    boolean deleteTweetById(int tweetId);

    Integer likeTweet(int userId, int tweetId);

    Integer dislikeTweet(int userId, int tweetId);

    List<Tweet> getTweetsByTag(String tagName) throws SQLException;
}
