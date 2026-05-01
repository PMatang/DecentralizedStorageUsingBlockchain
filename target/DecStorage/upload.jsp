<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.decstorage.model.User" %>
<%
  User user = (User) session.getAttribute("user");
  if (user == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
  <title>DecStorage — Upload</title>
  <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="auth-card" style="max-width:540px">
  <div class="brand">
    <div class="brand-icon">📤</div>
    <span class="brand-name">UPLOAD FILE</span>
  </div>

  <p style="margin-bottom:20px;margin-top:4px">
    File will be AES-256 encrypted then stored on IPFS
  </p>

  <% if (request.getAttribute("error") != null) { %>
    <div class="error">${error}</div>
  <% } %>

  <form method="post" action="upload" enctype="multipart/form-data">
    <label>Select File</label>
    <input type="file" name="file" required>

    <hr>

    <p style="margin:0 0 8px;font-size:12px;color:var(--text-hint);
       text-transform:uppercase;letter-spacing:0.06em">
      Access Policy — who can decrypt this file
    </p>

    <label>Required Role</label>
    <select name="role_policy">
      <option value="Any">Any role</option>
      <option value="Doctor">Doctor</option>
      <option value="Admin">Admin</option>
      <option value="Researcher">Researcher</option>
      <option value="Student">Student</option>
    </select>

    <label>Required Department</label>
    <select name="dept_policy">
      <option value="Any">Any department</option>
      <option value="Medical">Medical</option>
      <option value="IT">IT</option>
      <option value="CSE">CSE</option>
      <option value="Research">Research</option>
    </select>

    <label>Minimum Clearance</label>
    <select name="clearance_policy">
      <option value="Low">Low</option>
      <option value="Medium">Medium</option>
      <option value="High">High</option>
    </select>

    <input type="hidden" name="policy" id="policyField">

    <button type="submit" onclick="buildPolicy()"
            style="width:100%;margin-top:24px;justify-content:center">
      Encrypt &amp; Upload →
    </button>
  </form>

  <p style="text-align:center">
    <a href="dashboard.jsp">← Back to Dashboard</a>
  </p>
</div>

<script>
function buildPolicy() {
  var role      = document.querySelector('[name=role_policy]').value;
  var dept      = document.querySelector('[name=dept_policy]').value;
  var clearance = document.querySelector('[name=clearance_policy]').value;
  var policy = {};
  if (role      !== 'Any') policy.role       = role;
  if (dept      !== 'Any') policy.department = dept;
  if (clearance !== 'Low') policy.clearance  = clearance;
  document.getElementById('policyField').value = JSON.stringify(policy);
}
</script>
</body>
</html>