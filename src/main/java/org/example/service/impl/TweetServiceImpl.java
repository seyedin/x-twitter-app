package org.example.service.impl;

import org.example.model.Tweet;
import org.example.repository.TweetRepository;
import org.example.repository.impl.TweetRepositoryImpl;
import org.example.service.TweetService;

import java.util.List;

public class TweetServiceImpl implements TweetService {
    private final TweetRepository tweetRepository = new TweetRepositoryImpl();

    @Override
    public List<Tweet> getTweetsAndRetweetsByUserId(int userId) {
        return tweetRepository.getTweetsAndRetweetsByUserId(userId);
    }

    @Override
    public List<Tweet> getTweetByUserId(int userId) {
        return tweetRepository.getTweetByUserId(userId);
    }

    @Override
    public List<Tweet> getAllTweets() {
        return tweetRepository.getAllTweets();
    }

    @Override
    public Tweet postTweet(int userId, String content) {
        return tweetRepository.postTweet(userId, content);
    }

    @Override
    public boolean updateTweet(int tweetId, String newContent) {
        return tweetRepository.updateTweet(tweetId, newContent);
    }

    @Override
    public boolean deleteTweetById(int tweetId) {
        return tweetRepository.deleteTweetById(tweetId);
    }

    @Override
    public Integer likeTweet(int userId, int tweetId) {
        return tweetRepository.likeTweet(userId, tweetId);
    }

    @Override
    public Integer dislikeTweet(int userId, int tweetId) {
        return tweetRepository.dislikeTweet(userId, tweetId);
    }

    @Override
    public List<Tweet> getTweetsByTag(String tagName) {
        return tweetRepository.getTweetsByTag(tagName);
    }

    @Override
    public List<Tweet> getAllTweetsAndRetweets() {
        return tweetRepository.getAllTweetsAndRetweets();
    }

    @Override
    public Tweet getTweetById(int tweetId) {
        return tweetRepository.getTweetById(tweetId);
    }
}
