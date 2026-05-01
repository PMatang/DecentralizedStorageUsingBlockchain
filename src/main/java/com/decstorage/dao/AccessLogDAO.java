package com.decstorage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.decstorage.util.DBConnection;

public class AccessLogDAO {

    public void log(int userId, int fileId, String action) {
        String sql = "INSERT INTO access_logs (user_id, file_id, action) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, fileId);
            ps.setString(3, action);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // log but don't crash the request
        }
    }

    // Fetch logs for dashboard display
    public List<String[]> getRecentLogs(int limit) {
        String sql = """
            SELECT u.username, f.file_name, l.action, l.timestamp
            FROM access_logs l
            JOIN users u ON l.user_id = u.id
            JOIN files_meta f ON l.file_id = f.id
            ORDER BY l.timestamp DESC
            LIMIT ?
            """;
        List<String[]> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logs.add(new String[]{
                    rs.getString("username"),
                    rs.getString("file_name"),
                    rs.getString("action"),
                    rs.getTimestamp("timestamp").toString()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}