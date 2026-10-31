package com.sunwayworld.owned.attachments.replay;

import com.sunwayworld.emc.module.erp.service.ErpFileService;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/** Bounded, source-labelled replay implementation of the original ERP file contract. */
public final class OwnedErpReplayFileService implements ErpFileService {
    public static final String DOWNLOAD_PATH = "/hj/erp/downloadFile";
    private static final long MAX_BYTES = 4L * 1024L * 1024L;
    private final String merchantId;
    private final OwnedReplayTransport transport;
    private final Path journal;
    private final int maxRetries;
    private final Map<String, byte[]> successful = new HashMap<String, byte[]>();
    private final Map<String, String> idempotencyPayloads = new HashMap<String, String>();

    public OwnedErpReplayFileService(String merchantId, OwnedReplayTransport transport, Path journal, int maxRetries) {
        if (merchantId == null || !merchantId.matches("[A-Za-z0-9_-]{1,64}")) throw new IllegalArgumentException("trusted merchant id required");
        if (transport == null || journal == null) throw new IllegalArgumentException("replay dependencies required");
        if (maxRetries < 0 || maxRetries > 3) throw new IllegalArgumentException("replay retry bound must be 0..3");
        this.merchantId = merchantId; this.transport = transport; this.journal = journal; this.maxRetries = maxRetries;
    }
    @Override public String getMerchantId() { return merchantId; }
    @Override public void downloadFile(String fileSign, File targetFile) {
        downloadFile(fileSign, targetFile, key(fileSign));
    }
    public synchronized void downloadFile(String fileSign, File targetFile, String idempotencyKey) {
        if (fileSign == null || fileSign.trim().isEmpty() || fileSign.length() > 256) throw new IllegalArgumentException("file sign required");
        if (targetFile == null) throw new IllegalArgumentException("target file required");
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty() || idempotencyKey.length() > 256) throw new IllegalArgumentException("idempotency key required");
        if (idempotencyPayloads.size() >= 128 && !idempotencyPayloads.containsKey(idempotencyKey)) throw new IllegalStateException("ERP replay session capacity exhausted");
        String previous = idempotencyPayloads.putIfAbsent(idempotencyKey, fileSign);
        if (previous != null && !previous.equals(fileSign)) throw new IllegalArgumentException("idempotency key conflict");
        Path target = targetFile.toPath().toAbsolutePath().normalize();
        Path parent = target.getParent();
        if (parent == null) throw new IllegalArgumentException("target parent required");
        try {
            Files.createDirectories(parent);
            byte[] cached = successful.get(idempotencyKey);
            if (cached != null) { writeAtomic(target, cached); trace(idempotencyKey, 200, 0, "replayed"); return; }
            OwnedReplayResponse response = null;
            for (int attempt = 1; attempt <= maxRetries + 1; attempt++) {
                try { response = transport.post(DOWNLOAD_PATH, merchantId, fileSign, idempotencyKey); }
                catch (Exception failure) {
                    trace(idempotencyKey, 599, attempt, "transport-failure");
                    if (attempt > maxRetries) throw new IllegalStateException("ERP replay transport failed", failure);
                    continue;
                }
                int status = response.getStatus();
                trace(idempotencyKey, status, attempt, "response");
                if (status >= 200 && status < 300) {
                    byte[] body = response.getBody();
                    if (body.length > MAX_BYTES) throw new IllegalStateException("ERP replay response exceeds bound");
                    writeAtomic(target, body); successful.put(idempotencyKey, body.clone()); return;
                }
                if (status < 500 || attempt > maxRetries) throw new IllegalStateException("ERP replay rejected with HTTP " + status);
            }
            throw new IllegalStateException("ERP replay retry bound exhausted");
        } catch (IOException failure) { throw new IllegalStateException("ERP replay target write failed", failure); }
    }
    private void writeAtomic(Path target, byte[] bytes) throws IOException {
        Path temporary = Files.createTempFile(target.getParent(), ".erp-replay-", ".part");
        try {
            Files.write(temporary, bytes, StandardOpenOption.TRUNCATE_EXISTING);
            try { Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException unsupported) { Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING); }
        }
        finally { Files.deleteIfExists(temporary); }
    }
    private void trace(String key, int status, int attempt, String outcome) throws IOException {
        Files.createDirectories(journal.toAbsolutePath().normalize().getParent());
        String digest = digest(key);
        String line = "{\"source\":\"erp-replay\",\"path\":\"" + DOWNLOAD_PATH + "\",\"merchant\":\"" + merchantId
                + "\",\"idempotency\":\"" + digest + "\",\"status\":" + status + ",\"attempt\":" + attempt
                + ",\"outcome\":\"" + outcome + "\"}\n";
        Files.write(journal, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    private static String key(String fileSign) { return digest("erp-replay:" + fileSign); }
    private static String digest(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(); for (byte valueByte : bytes) hex.append(String.format("%02x", valueByte & 255)); return hex.toString();
        } catch (Exception impossible) { throw new IllegalStateException("SHA-256 unavailable", impossible); }
    }
}
