package com.diamondfire.helpbot.sys.tag;

import com.diamondfire.helpbot.sys.database.ConnectionProvider;
import com.diamondfire.helpbot.sys.tag.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores custom command tags in the {@code owen.tags} table.
 * Tags are cached in memory, as they are looked up for every prefixed message.
 */
public class TagHandler {

    private static final Map<String, Tag> TAGS = new ConcurrentHashMap<>();

    public static void initialize() {
        try (Connection connection = ConnectionProvider.getConnection()) {
            boolean created = createTable(connection);
            if (created) {
                for (Tag tag : DefaultTags.TAGS) {
                    insert(connection, tag);
                }
            }

            loadTags(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load tags", e);
        }
    }

    public static List<Tag> getTags() {
        return List.copyOf(TAGS.values());
    }

    public static @NotNull Tag getTag(String activator) throws TagDoesNotExistException {
        Tag tag = TAGS.get(activator);
        if (tag == null) {
            throw new TagDoesNotExistException(String.format(
                    "A tag with activator `%s` does not exist.", activator));
        }

        return tag;
    }

    public static synchronized void newTag(Tag tag) throws TagAlreadyExistsException, SQLException {
        if (TAGS.containsKey(tag.getActivator())) {
            throw new TagAlreadyExistsException("A tag with this activator already exists.");
        }

        try (Connection connection = ConnectionProvider.getConnection()) {
            insert(connection, tag);
        }

        TAGS.put(tag.getActivator(), tag);
    }

    /**
     * Saves the changes of a modified tag.
     *
     * @param activator the activator the tag is currently stored under
     * @param tag       the updated tag
     */
    public static synchronized void updateTag(String activator, Tag tag) throws TagException, SQLException {
        getTag(activator);
        if (!activator.equals(tag.getActivator()) && TAGS.containsKey(tag.getActivator())) {
            throw new TagAlreadyExistsException("A tag with this activator already exists.");
        }

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE owen.tags SET activator = ?, title = ?, response = ?, author_id = ?, image = ? WHERE activator = ?")) {
            statement.setString(1, tag.getActivator());
            statement.setString(2, tag.getTitle());
            statement.setString(3, tag.getResponse());
            statement.setLong(4, tag.getAuthorId());
            statement.setString(5, tag.getImage());
            statement.setString(6, activator);
            statement.executeUpdate();
        }

        TAGS.remove(activator);
        TAGS.put(tag.getActivator(), tag);
    }

    public static synchronized void deleteTag(String activator) throws TagDoesNotExistException, SQLException {
        getTag(activator);

        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM owen.tags WHERE activator = ?")) {
            statement.setString(1, activator);
            statement.executeUpdate();
        }

        TAGS.remove(activator);
    }

    /**
     * @return true if the table did not exist yet and was created
     */
    private static boolean createTable(Connection connection) throws SQLException {
        try (ResultSet tables = connection.getMetaData().getTables("owen", null, "tags", null)) {
            if (tables.next()) {
                return false;
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS owen.tags (
                        activator  VARCHAR(100) NOT NULL PRIMARY KEY,
                        title      VARCHAR(256) NOT NULL,
                        response   TEXT         NOT NULL,
                        author_id  BIGINT       NOT NULL,
                        image      TEXT         NOT NULL,
                        created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ) DEFAULT CHARSET = utf8mb4""");
        }

        return true;
    }

    private static void loadTags(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet set = statement.executeQuery("SELECT activator, title, response, author_id, image FROM owen.tags")) {
            Map<String, Tag> loaded = new HashMap<>();
            while (set.next()) {
                Tag tag = new Tag(
                        set.getString("activator"),
                        set.getString("title"),
                        set.getString("response"),
                        set.getLong("author_id"),
                        set.getString("image")
                );
                loaded.put(tag.getActivator(), tag);
            }

            TAGS.clear();
            TAGS.putAll(loaded);
        }
    }

    private static void insert(Connection connection, Tag tag) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO owen.tags (activator, title, response, author_id, image) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, tag.getActivator());
            statement.setString(2, tag.getTitle());
            statement.setString(3, tag.getResponse());
            statement.setLong(4, tag.getAuthorId());
            statement.setString(5, tag.getImage());
            statement.executeUpdate();
        }
    }

}
