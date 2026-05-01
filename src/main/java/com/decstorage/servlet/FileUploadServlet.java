package com.decstorage.servlet;

import com.decstorage.dao.FileDAO;
import com.decstorage.model.FileMetadata;
import com.decstorage.model.User;
import com.decstorage.service.AESEncryptionService;
import com.decstorage.service.BlockchainService;
import com.decstorage.service.IPFSService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/upload")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024)
public class FileUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/dec_storage_files/";

    @Override
    public void init() throws ServletException {
        new File(UPLOAD_DIR).mkdirs();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        Part filePart = req.getPart("file");
        String policyJson = req.getParameter("policy");

        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("error", "Please select a file.");
            req.getRequestDispatcher("upload.jsp").forward(req, resp);
            return;
        }

        String originalName = Paths.get(
            filePart.getSubmittedFileName()).getFileName().toString();

        try {
            // 1. Read original file bytes
            byte[] fileBytes = filePart.getInputStream().readAllBytes();

            // 2. Generate AES key and encrypt
            byte[] aesKey    = AESEncryptionService.generateKey();
            byte[] encrypted = AESEncryptionService.encrypt(fileBytes, aesKey);

            // 3. Save encrypted file to local disk
            String savedName = System.currentTimeMillis() + "_" + originalName + ".enc";
            String fullPath  = UPLOAD_DIR + savedName;
            Files.write(Paths.get(fullPath), encrypted);

            // 4. Upload to IPFS FIRST — before saving to DB
            String ipfsCid = "";
            try {
                ipfsCid = IPFSService.uploadToIPFS(encrypted, savedName);
                System.out.println("IPFS CID: " + ipfsCid);
            } catch (Exception e) {
                System.err.println("IPFS upload failed: " + e.getMessage());
            }

            // 5. Save metadata to DB with CID already set
            FileMetadata meta = new FileMetadata();
            meta.setOwnerId(user.getId());
            meta.setFileName(originalName);
            meta.setLocalPath(fullPath);
            meta.setIpfsCid(ipfsCid);
            meta.setPolicyJson(policyJson);
            meta.setAesKeyBase64(AESEncryptionService.toBase64(aesKey));

            int fileId = new FileDAO().save(meta);
            System.out.println("File saved to DB. ID: " + fileId);

            // 6. Register on blockchain with real CID
            try {
                String cid    = !ipfsCid.isEmpty() ? ipfsCid : "local:" + savedName;
                String txHash = BlockchainService.registerFile(cid, policyJson);
                System.out.println("Blockchain TX: " + txHash);
            } catch (Exception e) {
                System.err.println("Blockchain failed: " + e.getMessage());
            }

            resp.sendRedirect("dashboard.jsp?uploaded=1");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Upload failed: " + e.getMessage());
            req.getRequestDispatcher("upload.jsp").forward(req, resp);
        }
    }
}