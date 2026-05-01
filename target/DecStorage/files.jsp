<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.decstorage.model.FileMetadata" %>
<%@ page import="com.decstorage.model.User" %>
<%@ page import="com.decstorage.dao.AccessLogDAO" %>
<%@ page import="com.decstorage.service.IPFSService" %>
<%@ page import="java.util.List" %>
<%
  User user = (User) session.getAttribute("user");
  if (user == null) { response.sendRedirect("login.jsp"); return; }

  List<FileMetadata> files = (List<FileMetadata>) request.getAttribute("files");
  List<String[]> logs = new AccessLogDAO().getRecentLogs(10);
  String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
  <title>File Browser — DecStorage</title>
  <link rel="stylesheet" href="css/auth.css">
  <style>
    table  { width:100%; border-collapse:collapse; margin-top:10px; }
    th, td { padding:10px; border:1px solid #ddd; text-align:left; font-size:14px; }
    th     { background:#f4f4f452; font-weight:bold; }
    .allow { color:green; font-weight:bold; }
    .deny  { color:red;   font-weight:bold; }
    .badge { display:inline-block; padding:2px 10px; border-radius:12px; font-size:12px; font-weight:bold; }
    .badge-allow { background:#e6f4ea; color:#2e7d32; }
    .badge-deny  { background:#fdecea; color:#c62828; }
    .ipfs-link   { color:#1a73e8; text-decoration:none; font-size:13px; }
    .ipfs-link:hover { text-decoration:underline; }
    .nav-links { margin-top:16px; }
    .nav-links a { margin-right:16px; color:#1a73e8; text-decoration:none; }
  </style>
</head>
<body>
<div class="auth-card" style="max-width:900px">

  <h2>File Browser</h2>
  <p>
    Logged in as <strong><%= user.getUsername() %></strong> |
    Role: <strong><%= user.getRole() %></strong> |
    Dept: <strong><%= user.getDepartment() %></strong> |
    Clearance: <strong><%= user.getClearanceLevel() %></strong>
  </p>

  <% if ("denied".equals(error)) { %>
    <p class="error">Access Denied — your attributes do not match this file's policy.</p>
  <% } else if ("notfound".equals(error)) { %>
    <p class="error">File not found.</p>
  <% } else if ("servererror".equals(error)) { %>
    <p class="error">Server error during download.</p>
  <% } %>

  <h3>All Files</h3>

  <% if (files == null || files.isEmpty()) { %>
    <p>No files uploaded yet.</p>
  <% } else { %>
    <table>
      <tr>
        <th>#</th>
        <th>File Name</th>
        <th>Access Policy</th>
        <th>IPFS</th>
        <th>Uploaded At</th>
        <th>Action</th>
      </tr>
      <%
        int fileIndex = 0;
        for (FileMetadata f : files) {
          fileIndex++;
          boolean hasCid = (f.getIpfsCid() != null && !f.getIpfsCid().isEmpty());
          String ipfsUrl = hasCid ? IPFSService.getGatewayUrl(f.getIpfsCid()) : "";
      %>
      <tr>
        <td><%= fileIndex %></td>
        <td><strong><%= f.getFileName() %></strong></td>
        <td><code><%= f.getPolicyJson() %></code></td>
        <td>
          <% if (hasCid) { %>
            <a class="ipfs-link" href="<%= ipfsUrl %>" target="_blank">View on IPFS &#8599;</a>
          <% } else { %>
            <span style="color:#999">Local only</span>
          <% } %>
        </td>
        <td><%= f.getUploadedAt() %></td>
        <td>
          <a href="access?download=<%= f.getId() %>"><button>Download</button></a>
        </td>
      </tr>
      <% } %>
    </table>
  <% } %>

  <h3 style="margin-top:30px">Recent Access Logs</h3>

  <% if (logs == null || logs.isEmpty()) { %>
    <p>No access attempts logged yet.</p>
  <% } else { %>
    <table>
      <tr>
        <th>#</th>
        <th>User</th>
        <th>File</th>
        <th>Result</th>
        <th>Timestamp</th>
      </tr>
      <%
        int logIndex = 0;
        for (String[] log : logs) {
          logIndex++;
          String badgeClass = "ALLOW".equals(log[2]) ? "badge-allow" : "badge-deny";
      %>
      <tr>
        <td><%= logIndex %></td>
        <td><%= log[0] %></td>
        <td><%= log[1] %></td>
        <td><span class="badge <%= badgeClass %>"><%= log[2] %></span></td>
        <td><%= log[3] %></td>
      </tr>
      <% } %>
    </table>
  <% } %>

  <div class="nav-links">
    <a href="dashboard.jsp">← Dashboard</a>
    <a href="upload.jsp">Upload File</a>
    <a href="auth?action=logout">Logout</a>
  </div>

</div>
</body>
</html>