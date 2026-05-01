package com.decstorage.model;

public class User {
    private int    id;
    private String username;
    private String email;
    private String passwordHash;
    private String walletAddress;
    private String role;
    private String department;
    private String clearanceLevel;

    // Getters and setters
    public int    getId()             { return id; }
    public void   setId(int id)       { this.id = id; }

    public String getUsername()                   { return username; }
    public void   setUsername(String username)    { this.username = username; }

    public String getEmail()                      { return email; }
    public void   setEmail(String email)          { this.email = email; }

    public String getPasswordHash()               { return passwordHash; }
    public void   setPasswordHash(String h)       { this.passwordHash = h; }

    public String getWalletAddress()              { return walletAddress; }
    public void   setWalletAddress(String w)      { this.walletAddress = w; }

    public String getRole()                       { return role; }
    public void   setRole(String role)            { this.role = role; }

    public String getDepartment()                 { return department; }
    public void   setDepartment(String d)         { this.department = d; }

    public String getClearanceLevel()             { return clearanceLevel; }
    public void   setClearanceLevel(String c)     { this.clearanceLevel = c; }
}