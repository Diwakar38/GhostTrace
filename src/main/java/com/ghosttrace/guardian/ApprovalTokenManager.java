package com.ghosttrace.guardian;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApprovalTokenManager {

    private final byte[] secretKey = new byte[32];
    private final Map<String, TokenRecord> pendingApprovals = new ConcurrentHashMap<>();

    public record TokenRecord(String command, Instant expiresAt) {}

    public ApprovalTokenManager() {
        new SecureRandom().nextBytes(secretKey);
    }

    public String generateToken(String command, int validitySeconds) {
        Instant expiresAt = Instant.now().plusSeconds(validitySeconds);
        String payload = command.trim() + ":" + expiresAt.getEpochSecond();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(signature).substring(0, 16);
            pendingApprovals.put(token, new TokenRecord(command.trim(), expiresAt));
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Error generating approval token", e);
        }
    }

    public boolean validateToken(String token, String command) {
        if (token == null || command == null) return false;
        TokenRecord record = pendingApprovals.get(token);
        if (record == null) return false;

        if (Instant.now().isAfter(record.expiresAt())) {
            pendingApprovals.remove(token);
            return false;
        }

        boolean matches = record.command().equals(command.trim());
        if (matches) {
            pendingApprovals.remove(token); // Single-use token
        }
        return matches;
    }

    public void revoke(String token) {
        pendingApprovals.remove(token);
    }
}
