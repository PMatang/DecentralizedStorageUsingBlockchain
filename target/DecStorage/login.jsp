<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>DecStorage — Login</title>
  <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="auth-card">
  <div class="brand">
    <div class="brand-icon">🔐</div>
    <span class="brand-name">DECSTORAGE</span>
  </div>

  <h2>Welcome back</h2>
  <p style="margin-bottom:24px;margin-top:4px">Sign in to your secure vault</p>

  <% if ("1".equals(request.getParameter("registered"))) { %>
    <div class="success">Account created — you can now sign in.</div>
  <% } %>
  <% if ("1".equals(request.getParameter("logout"))) { %>
    <div class="success">You have been signed out.</div>
  <% } %>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error">${error}</div>
  <% } %>

  <form method="post" action="auth">
    <input type="hidden" name="action" value="login">
    <label>Username</label>
    <input type="text" name="username" placeholder="Enter username" required autofocus>
    <label>Password</label>
    <input type="password" name="password" placeholder="Enter password" required>
    <button type="submit" style="width:100%;margin-top:24px;justify-content:center">
      Sign In →
    </button>
  </form>

  <p style="text-align:center">
    No account? <a href="register.jsp">Create one</a>
  </p>
</div>
</body>
</html>