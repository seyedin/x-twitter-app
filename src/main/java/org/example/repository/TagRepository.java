package org.example.repository;

import java.sql.SQLException;

public interface TagRepository {
    Integer addTagToTweet(int tweetId, String tagName);

    int getOrCreateTagId(String tagName) throws SQLException;
}
