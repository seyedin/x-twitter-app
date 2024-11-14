package org.example.service;

import org.example.model.Tweet;

import java.util.List;

public interface TweetService {
    List<Tweet> getTweetsAndRetweetsByUserId(int userId);

    List<Tweet> getTweetByUserId(int userId);

    List<Tweet> getAllTweets();

    Tweet postTweet(int userId, String content);

    boolean updateTweet(int tweetId, String newContent);

    boolean deleteTweetById(int tweetId);

    Integer likeTweet(int userId, int tweetId);

    Integer dislikeTweet(int userId, int tweetId);

    List<Tweet> getTweetsByTag(String tagName);

    List<Tweet> getAllTweetsAndRetweets();

    Tweet getTweetById(int tweetId);
}
