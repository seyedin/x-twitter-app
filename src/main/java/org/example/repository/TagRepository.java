package org.example.repository;

import java.sql.SQLException;

public interface TagRepository {
    Integer addTagToTweet(int tweetId, String tagName);

    // متد برای پیدا کردن یا ایجاد یک تگ
    int getOrCreateTagId(String tagName) throws SQLException;
}
