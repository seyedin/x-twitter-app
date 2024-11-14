package org.example.repository.impl;

import org.example.datasource.DatabaseConnection;
import org.example.model.Tag;
import org.example.repository.TagRepository;

import java.sql.*;
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
            PreparedStatement preparedStatement = conn.prepareStatement(ADD_TAG_TO_TWEET_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, tweetId);
            preparedStatement.setInt(2, tagId);

            // Execute the insert operation
            int affectedRows = preparedStatement.executeUpdate();

            // Check if a row was added and retrieve the generated keys
            if (affectedRows > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1); // Retrieve the generated ID
                    }
                }
            }

            throw new SQLException("Failed to tag tweet; no ID obtained.");
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

            // Check if the tag already exists
            PreparedStatement selectStatement = conn.prepareStatement(SELECT_TAG_BY_NAME);
            selectStatement.setString(1, tagName);
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }


            // If the tag does not exist, create a new tag
            PreparedStatement insertStatement = conn.prepareStatement(INSERT_TAG_QUERY, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, tagName);

            // Execute the insert and check if the row was added
            int affectedRows = insertStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tagId = generatedKeys.getInt(1); // Retrieve the generated tag ID
                    }
                }
            }

            return tagId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to get or create tag ID.");
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
