package com.decstorage.service;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class IPFSService {

    // ── CONFIG — paste your Pinata keys here ─────────────────
    private static final String API_KEY    = "ac432b6c9adbd0da015f";
    private static final String API_SECRET = "e48efc004615923e9aff234387b689e1f30de9dddd20e92ebf399bfdcfb4228d";
    // ─────────────────────────────────────────────────────────

    private static final String PINATA_URL =
        "https://api.pinata.cloud/pinning/pinFileToIPFS";

    /**
     * Uploads encrypted bytes to IPFS via Pinata.
     * Returns the IPFS CID (Content Identifier) on success.
     */
    public static String uploadToIPFS(byte[] fileBytes, String fileName)
            throws Exception {

        String boundary = "----DecStorageBoundary" + System.currentTimeMillis();
        byte[] body     = buildMultipart(fileBytes, fileName, boundary);

        HttpClient  client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(PINATA_URL))
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .header("pinata_api_key",        API_KEY)
            .header("pinata_secret_api_key", API_SECRET)
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .build();

        HttpResponse<String> response = client.send(
    request, HttpResponse.BodyHandlers.ofString()
);

System.out.println("Pinata response code: " + response.statusCode());
System.out.println("Pinata response body: " + response.body());

if (response.statusCode() != 200) {
    throw new Exception("Pinata upload failed: HTTP "
        + response.statusCode() + " — " + response.body());
}

        // Parse CID from JSON response
        // Response looks like: {"IpfsHash":"Qm...","PinSize":123,...}
        String body2 = response.body();
        int start = body2.indexOf("\"IpfsHash\":\"") + 12;
        int end   = body2.indexOf("\"", start);
        return body2.substring(start, end);
    }

    /**
     * Returns the public IPFS gateway URL for a given CID.
     * Anyone with the CID can fetch the raw (encrypted) file.
     */
    public static String getGatewayUrl(String cid) {
        return "https://gateway.pinata.cloud/ipfs/" + cid;
    }

    // ── Multipart builder ─────────────────────────────────────
    private static byte[] buildMultipart(byte[] file, String fileName,
                                          String boundary) throws Exception {
        String CRLF = "\r\n";

        StringBuilder header = new StringBuilder();
        header.append("--").append(boundary).append(CRLF);
        header.append("Content-Disposition: form-data; ")
              .append("name=\"file\"; filename=\"").append(fileName).append("\"")
              .append(CRLF);
        header.append("Content-Type: application/octet-stream").append(CRLF);
        header.append(CRLF);

        byte[] headerBytes = header.toString().getBytes(StandardCharsets.UTF_8);
        byte[] footerBytes = (CRLF + "--" + boundary + "--" + CRLF)
                                .getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(headerBytes);
        out.write(file);
        out.write(footerBytes);
        return out.toByteArray();
    }
}