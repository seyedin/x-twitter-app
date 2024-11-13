package org.example.service.impl;

import org.example.repository.ReTweetRepository;
import org.example.repository.impl.ReTweetRepositoryImpl;
import org.example.service.ReTweetService;

public class ReTweetServiceImpl implements ReTweetService {
    private final ReTweetRepository reTweetRepository = new ReTweetRepositoryImpl();
    @Override
    public Integer postReTweet(Integer userId, Integer originalTweetId, Integer parentRetweetId, String additionalContent) {
        return reTweetRepository.postReTweet(userId, originalTweetId, parentRetweetId, additionalContent);
    }
}
