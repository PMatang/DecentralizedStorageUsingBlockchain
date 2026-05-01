package com.decstorage.dao;

import com.decstorage.model.User;
import com.decstorage.util.DBConnection;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.*;

public class UserDAO {

    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, role, department, clearance_level) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hashed = BCrypt.withDefaults().hashToString(12, user.getPasswordHash().toCharArray());

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashed);
            ps.setString(4, user.getRole());
            ps.setString(5, user.getDepartment());
            ps.setString(6, user.getClearanceLevel());
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // username or email already exists
        }
    }

    public User login(String username, String plainPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash);

                if (result.verified) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setDepartment(rs.getString("department"));
                    user.setClearanceLevel(rs.getString("clearance_level"));
                    user.setWalletAddress(rs.getString("wallet_address"));
                    return user;
                }
            }
            return null; // invalid credentials
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        }
    }
}