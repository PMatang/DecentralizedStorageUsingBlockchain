package com.decstorage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.decstorage.model.FileMetadata;
import com.decstorage.util.DBConnection;

public class FileDAO {

    // Save file metadata, returns generated ID
    public int save(FileMetadata meta) throws SQLException {
    String sql = "INSERT INTO files_meta " +
                 "(owner_id, file_name, local_path, ipfs_cid, policy_json, aes_key_base64) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, meta.getOwnerId());
        ps.setString(2, meta.getFileName());
        ps.setString(3, meta.getLocalPath());
        ps.setString(4, meta.getIpfsCid());      
        ps.setString(5, meta.getPolicyJson());
        ps.setString(6, meta.getAesKeyBase64());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        return -1;
    }
}

    // Get all files owned by a user
    public List<FileMetadata> getByOwner(int ownerId) throws SQLException {
        String sql = "SELECT * FROM files_meta WHERE owner_id = ? ORDER BY uploaded_at DESC";
        List<FileMetadata> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Get single file by ID
    public FileMetadata getById(int fileId) throws SQLException {
        String sql = "SELECT * FROM files_meta WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fileId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        }
    }

    // Get all files (for access requests)
    public List<FileMetadata> getAll() throws SQLException {
        String sql = "SELECT * FROM files_meta ORDER BY uploaded_at DESC";
        List<FileMetadata> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private FileMetadata mapRow(ResultSet rs) throws SQLException {
        FileMetadata f = new FileMetadata();
        f.setId(rs.getInt("id"));
        f.setOwnerId(rs.getInt("owner_id"));
        f.setFileName(rs.getString("file_name"));
        f.setLocalPath(rs.getString("local_path"));
        f.setIpfsCid(rs.getString("ipfs_cid"));
        f.setPolicyJson(rs.getString("policy_json"));
        f.setAesKeyBase64(rs.getString("aes_key_base64"));
        f.setTxHash(rs.getString("tx_hash"));
        f.setUploadedAt(rs.getTimestamp("uploaded_at"));
        return f;
    }

    public void updateCid(int fileId, String cid) throws SQLException {
    String sql = "UPDATE files_meta SET ipfs_cid = ? WHERE id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, cid);
        ps.setInt(2, fileId);
        ps.executeUpdate();
    }
}
}
