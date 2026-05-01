package com.decstorage.model;

import java.sql.Timestamp;

public class FileMetadata {
    private int       id;
    private int       ownerId;
    private String    fileName;
    private String    ipfsCid;
    private String    localPath;
    private String    policyJson;
    private String    aesKeyBase64;
    private String    txHash;
    private Timestamp uploadedAt;

    public int    getId()                        { return id; }
    public void   setId(int id)                  { this.id = id; }

    public int    getOwnerId()                   { return ownerId; }
    public void   setOwnerId(int ownerId)        { this.ownerId = ownerId; }

    public String getFileName()                  { return fileName; }
    public void   setFileName(String fileName)   { this.fileName = fileName; }

    public String getIpfsCid()                   { return ipfsCid; }
    public void   setIpfsCid(String ipfsCid)     { this.ipfsCid = ipfsCid; }

    public String getLocalPath()                 { return localPath; }
    public void   setLocalPath(String localPath) { this.localPath = localPath; }

    public String getPolicyJson()                { return policyJson; }
    public void   setPolicyJson(String p)        { this.policyJson = p; }

    public String getAesKeyBase64()              { return aesKeyBase64; }
    public void   setAesKeyBase64(String k)      { this.aesKeyBase64 = k; }

    public String getTxHash()                    { return txHash; }
    public void   setTxHash(String txHash)       { this.txHash = txHash; }

    public Timestamp getUploadedAt()             { return uploadedAt; }
    public void   setUploadedAt(Timestamp t)     { this.uploadedAt = t; }
}