<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.decstorage.model.User" %>
<%@ page import="com.decstorage.model.FileMetadata" %>
<%@ page import="com.decstorage.dao.FileDAO" %>
<%@ page import="java.util.List" %>
<%
  User user = (User) session.getAttribute("user");
  if (user == null) { response.sendRedirect("login.jsp"); return; }
  List<FileMetadata> myFiles = new FileDAO().getByOwner(user.getId());
%>
<!DOCTYPE html>
<html>
<head>
  <title>DecStorage — Dashboard</title>
  <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="dash-card">

  <div class="topbar">
    <span class="topbar-brand">⬡ DECSTORAGE</span>
    <div class="topbar-nav">
      <a href="upload.jsp"><button>+ Upload</button></a>
      <a href="access"><button class="btn-ghost">Browse Files</button></a>
      <a href="auth?action=logout"><button class="btn-danger">Sign Out</button></a>
    </div>
  </div>

  <% if ("1".equals(request.getParameter("uploaded"))) { %>
    <div class="success">File encrypted, uploaded to IPFS and registered on blockchain.</div>
  <% } %>

  <div class="user-badge">
    <span class="tag tag-cyan"><%= user.getUsername() %></span>
    <span class="tag"><%= user.getRole() %></span>
    <span class="tag"><%= user.getDepartment() %></span>
    <span class="tag tag-green"><%= user.getClearanceLevel() %> clearance</span>
  </div>

  <%-- Stat cards --%>
  <div class="stat-grid">
    <div class="stat-card">
      <div class="stat-val"><%= myFiles.size() %></div>
      <div class="stat-label">Files Uploaded</div>
    </div>
    <div class="stat-card">
      <div class="stat-val" style="color:#00e676">
        <%= myFiles.stream().filter(f -> f.getIpfsCid() != null && !f.getIpfsCid().isEmpty()).count() %>
      </div>
      <div class="stat-label">On IPFS</div>
    </div>
    <div class="stat-card">
      <div class="stat-val" style="color:#f9a825">AES</div>
      <div class="stat-label">Encryption</div>
    </div>
    <div class="stat-card">
      <div class="stat-val" style="font-size:18px">ETH</div>
      <div class="stat-label">Blockchain</div>
    </div>
  </div>

  <%-- My files table --%>
  <div class="section-header">
    <h3>My Files</h3>
    <div class="section-line"></div>
  </div>

  <% if (myFiles.isEmpty()) { %>
    <div class="empty">No files yet — upload your first encrypted file</div>
  <% } else { %>
    <table class="data-table">
      <tr>
        <th>#</th>
        <th>File Name</th>
        <th>Policy</th>
        <th>IPFS</th>
        <th>Uploaded</th>
      </tr>
      <%
        int i = 0;
        for (FileMetadata f : myFiles) {
          i++;
          boolean hasCid = f.getIpfsCid() != null && !f.getIpfsCid().isEmpty();
      %>
      <tr>
        <td style="color:var(--text-hint)"><%= i %></td>
        <td><strong><%= f.getFileName() %></strong></td>
        <td><code><%= f.getPolicyJson() %></code></td>
        <td>
          <% if (hasCid) { %>
            <span class="tag tag-green">✓ IPFS</span>
          <% } else { %>
            <span class="tag">Local</span>
          <% } %>
        </td>
        <td style="color:var(--text-dim);font-size:12px"><%= f.getUploadedAt() %></td>
      </tr>
      <% } %>
    </table>
  <% } %>
</div>
</body>
</html>