package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.Tag;
import org.example.repository.TagRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagRepositoryImpl implements TagRepository {
    private static final String SELECT_TAG_BY_NAME = """
            select * from tags where name like ?
            """;
    private static final String SELECT_ALL_TAGS = """
            select * from tags
            """;
    private static final String INSERT_TAG_QUERY = """
            INSERT INTO Tags (name)
            VALUES (?)
            """;

    private static final String ADD_TAG_TO_TWEET_QUERY = """
            INSERT INTO Tweet_Tag (tweet_id, tag_id)
            VALUES (?, ?)
            """;

    @Override
    public Integer addTagToTweet(int tweetId, String tagName) {
        try {
            int tagId = getOrCreateTagId(tagName);

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(ADD_TAG_TO_TWEET_QUERY);
            preparedStatement.setInt(1, tweetId);
            preparedStatement.setInt(2, tagId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new SQLException("failed to tag tweet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // متد برای پیدا کردن یا ایجاد یک تگ
    @Override
    public int getOrCreateTagId(String tagName) throws SQLException {
        try {
            Connection conn = DatabaseConnection.getConnection();
            int tagId = -1;

            // چک کردن اینکه آیا تگ قبلا وجود دارد
            PreparedStatement prepareStatement = conn.prepareStatement(SELECT_TAG_BY_NAME);
            prepareStatement.setString(1, tagName);
            try (ResultSet rs = prepareStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            // اگر تگ وجود نداشت، ایجاد تگ جدید
            PreparedStatement insertTag = conn.prepareStatement(INSERT_TAG_QUERY);
            insertTag.setString(1, tagName);
            try (ResultSet resultSet = insertTag.executeQuery()) {
                if (resultSet.next()) {
                    tagId = resultSet.getInt(1);
                }
            }

            return tagId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to get tag id");
        }
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_ALL_TAGS);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {

                Tag tag = new Tag();
                tag.setTagId(rs.getInt("id"));
                tag.setName(rs.getString("name"));

                tags.add(tag);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
