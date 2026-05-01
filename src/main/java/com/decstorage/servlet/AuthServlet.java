package com.decstorage.servlet;

import java.io.IOException;

import com.decstorage.dao.UserDAO;
import com.decstorage.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    // ── REGISTER ──────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("register".equals(action)) {
            handleRegister(req, resp);
        } else if ("login".equals(action)) {
            handleLogin(req, resp);
        } else {
            resp.sendRedirect("login.jsp");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String username  = req.getParameter("username").trim();
        String email     = req.getParameter("email").trim();
        String password  = req.getParameter("password");
        String role      = req.getParameter("role");
        String dept      = req.getParameter("department");
        String clearance = req.getParameter("clearance_level");

        // Basic validation
        if (username.isEmpty() || password.length() < 6) {
            req.setAttribute("error", "Username required, password min 6 chars.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(password); // DAO will hash it
            user.setRole(role);
            user.setDepartment(dept);
            user.setClearanceLevel(clearance);

            boolean success = userDAO.register(user);
            if (success) {
                resp.sendRedirect("login.jsp?registered=1");
            } else {
                req.setAttribute("error", "Username or email already taken.");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Server error. Try again.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password");

        try {
            User user = userDAO.login(username, password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(30 * 60); // 30 min
                resp.sendRedirect("dashboard.jsp");
            } else {
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Server error. Try again.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    // ── LOGOUT ────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if ("logout".equals(req.getParameter("action"))) {
            req.getSession(false).invalidate();
            resp.sendRedirect("login.jsp?logout=1");
        }
    }
}