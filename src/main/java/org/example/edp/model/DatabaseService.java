package org.example.edp.model;

import org.example.edp.view.HistoryList.FactCardData;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private static final String DB_URL = "jdbc:sqlite:favorites.db";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = """
                    CREATE TABLE IF NOT EXISTS favorites (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        type TEXT NOT NULL,
                        content TEXT NOT NULL,
                        author TEXT,
                        saved_at TEXT
                    );
                    """;
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveFact(Fact fact) {
        String sql = "INSERT INTO favorites(type, content, author, saved_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "fact");
            pstmt.setString(2, fact.getText());
            pstmt.setString(3, fact.getSource());
            pstmt.setString(4, LocalDateTime.now().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveQuote(Quote quote) {
        String sql = "INSERT INTO favorites(type, content, author, saved_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "quote");
            pstmt.setString(2, quote.getContent());
            pstmt.setString(3, quote.getAuthor());
            pstmt.setString(4, LocalDateTime.now().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadFavoritesByType(String typeFilter) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM favorites";

        if (!typeFilter.equalsIgnoreCase("all")) {
            sql += " WHERE type = ?";
        }

        sql += " ORDER BY saved_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!typeFilter.equalsIgnoreCase("all")) {
                pstmt.setString(1, typeFilter.toLowerCase());
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                String content = rs.getString("content");
                String author = rs.getString("author");
                String savedAt = rs.getString("saved_at");

                String display = "[" + type.toUpperCase() + "] " + content;
                if (author != null && !author.isEmpty()) {
                    display += " — " + author;
                }
                display += " (" + savedAt + ")";
                list.add(display);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<FactCardData> loadFavoritesAsCards(String typeFilter) {
        List<FactCardData> list = new ArrayList<>();
        String sql = "SELECT type, content, author FROM favorites";

        if (!typeFilter.equalsIgnoreCase("all")) {
            sql += " WHERE type = ?";
        }

        sql += " ORDER BY saved_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!typeFilter.equalsIgnoreCase("all")) {
                pstmt.setString(1, typeFilter.toLowerCase());
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                String content = rs.getString("content");
                String author = rs.getString("author");

                list.add(new FactCardData(type, content, author));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}