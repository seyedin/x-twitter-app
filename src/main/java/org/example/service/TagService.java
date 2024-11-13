package org.example.service;

import org.example.model.Tag;

import java.sql.SQLException;
import java.util.List;

public interface TagService {
    Integer addTagToTweet(int tweetId, String tagName);

    int getOrCreateTagId(String tagName) throws SQLException;

    List<Tag> getAllTags();
}
