package com.decstorage.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.decstorage.dao.AccessLogDAO;
import com.decstorage.dao.FileDAO;
import com.decstorage.model.FileMetadata;
import com.decstorage.model.User;
import com.decstorage.service.ABACPolicyEvaluator;
import com.decstorage.service.AESEncryptionService;
import com.decstorage.service.BlockchainService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/access")
public class FileAccessServlet extends HttpServlet {

    private final FileDAO      fileDAO      = new FileDAO();
    private final AccessLogDAO accessLogDAO = new AccessLogDAO();

    // GET /access         → show all files listing page
    // GET /access?download=fileId  → ABAC check + decrypt + serve file
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String downloadId = req.getParameter("download");

        if (downloadId != null) {
            handleDownload(req, resp, user, Integer.parseInt(downloadId));
        } else {
            // Show file browser
            try {
                req.setAttribute("files", fileDAO.getAll());
                req.setAttribute("user", user);
                req.getRequestDispatcher("files.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect("dashboard.jsp");
            }
        }
    }

    private void handleDownload(HttpServletRequest req, HttpServletResponse resp,
                                 User user, int fileId) throws IOException {
        try {
            FileMetadata file = fileDAO.getById(fileId);

            if (file == null) {
                resp.sendRedirect("access?error=notfound");
                return;
            }

            // ── ABAC EVALUATION ──────────────────────────────
            boolean allowed = ABACPolicyEvaluator.evaluate(user, file.getPolicyJson());

            // Log to DB
accessLogDAO.log(user.getId(), fileId, allowed ? "ALLOW" : "DENY");

// Log to blockchain
BlockchainService.logAccess(fileId, allowed);

            if (!allowed) {
                resp.sendRedirect("access?error=denied");
                return;
            }
            // ─────────────────────────────────────────────────

            // Read encrypted file from disk
            byte[] encrypted = Files.readAllBytes(Paths.get(file.getLocalPath()));

            // Decrypt using stored AES key
            byte[] aesKey    = AESEncryptionService.fromBase64(file.getAesKeyBase64());
            byte[] decrypted = AESEncryptionService.decrypt(encrypted, aesKey);

            // Stream decrypted file to browser as download
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + file.getFileName() + "\"");
            resp.setContentLength(decrypted.length);

            try (OutputStream out = resp.getOutputStream()) {
                out.write(decrypted);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("access?error=servererror");
        }
    }
}