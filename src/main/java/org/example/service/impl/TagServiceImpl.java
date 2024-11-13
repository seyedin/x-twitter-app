package org.example.service.impl;

import org.example.repository.TagRepository;
import org.example.repository.impl.TagRepositoryImpl;
import org.example.service.TagService;

import java.sql.SQLException;

public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository = new TagRepositoryImpl();

    @Override
    public Integer addTagToTweet(int tweetId, String tagName) {
        return tagRepository.addTagToTweet(tweetId, tagName);
    }

    @Override
    public int getOrCreateTagId(String tagName) throws SQLException {
        return tagRepository.getOrCreateTagId(tagName);
    }
}
