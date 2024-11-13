package org.example.service;

public interface ReTweetService {
    Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent);

}
