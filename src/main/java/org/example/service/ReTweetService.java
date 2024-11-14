package org.example.service;

import org.example.model.Retweet;
import org.example.model.Tweet;

import java.util.List;

public interface ReTweetService {
    Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent);

    List<Tweet> getTweetsAndRetweets();

    Retweet getRetweetById(int retweetId);

    Integer postReTweet(int userId, String content, Retweet retweet);

}
