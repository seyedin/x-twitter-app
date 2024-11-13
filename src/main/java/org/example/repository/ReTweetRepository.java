package org.example.repository;

public interface ReTweetRepository {
    Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent);
}
