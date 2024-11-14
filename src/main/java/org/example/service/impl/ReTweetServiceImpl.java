package org.example.service.impl;

import org.example.model.Retweet;
import org.example.model.Tweet;
import org.example.repository.ReTweetRepository;
import org.example.repository.impl.ReTweetRepositoryImpl;
import org.example.service.ReTweetService;

import java.util.List;

public class ReTweetServiceImpl implements ReTweetService {
    private final ReTweetRepository reTweetRepository = new ReTweetRepositoryImpl();

    @Override
    public Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent) {
        return reTweetRepository.postReTweet(userId, originalTweetId, parentRetweetId, additionalContent);
    }

    @Override
    public List<Tweet> getTweetsAndRetweets() {
        return reTweetRepository.getTweetsAndRetweets();
    }

    @Override
    public Retweet getRetweetById(int retweetId) {
        return reTweetRepository.getRetweetById(retweetId);
    }

    @Override
    public Integer postReTweet(int userId, String content, Retweet retweet) {
        return reTweetRepository.postReTweet(userId, content, retweet);
    }
}
