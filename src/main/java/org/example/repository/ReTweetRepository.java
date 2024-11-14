package org.example.repository;

import org.example.model.Retweet;
import org.example.model.Tweet;

import java.util.List;

public interface ReTweetRepository {
    Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent);

    Retweet getRetweetById(int retweetId);

    Integer postReTweet(int userId, String content, Retweet retweet);

    List<Tweet> getTweetsAndRetweets();
}
