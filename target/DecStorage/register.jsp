<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>DecStorage — Register</title>
  <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="auth-card" style="max-width:520px">
  <div class="brand">
    <div class="brand-icon">🔐</div>
    <span class="brand-name">DECSTORAGE</span>
  </div>

  <h2>Create account</h2>
  <p style="margin-bottom:24px;margin-top:4px">Your attributes define your access level</p>

  <% if (request.getAttribute("error") != null) { %>
    <div class="error">${error}</div>
  <% } %>

  <form method="post" action="auth">
    <input type="hidden" name="action" value="register">

    <label>Username</label>
    <input type="text" name="username" placeholder="Choose a username" required>

    <label>Email</label>
    <input type="email" name="email" placeholder="your@email.com" required>

    <label>Password</label>
    <input type="password" name="password" placeholder="Min 6 characters" required>

    <label>Role</label>
    <select name="role">
      <option value="Doctor">Doctor</option>
      <option value="Admin">Admin</option>
      <option value="Researcher">Researcher</option>
      <option value="Student">Student</option>
    </select>

    <label>Department</label>
    <select name="department">
      <option value="Medical">Medical</option>
      <option value="IT">IT</option>
      <option value="CSE">CSE</option>
      <option value="Research">Research</option>
    </select>

    <label>Clearance Level</label>
    <select name="clearance_level">
      <option value="Low">Low</option>
      <option value="Medium">Medium</option>
      <option value="High">High</option>
    </select>

    <button type="submit" style="width:100%;margin-top:24px;justify-content:center">
      Create Account →
    </button>
  </form>

  <p style="text-align:center">
    Have an account? <a href="login.jsp">Sign in</a>
  </p>
</div>
</body>
</html>