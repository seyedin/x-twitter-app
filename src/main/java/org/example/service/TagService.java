package org.example.service;

import java.sql.SQLException;

public interface TagService {
    Integer addTagToTweet(int tweetId, String tagName);

    int getOrCreateTagId(String tagName) throws SQLException;
}
