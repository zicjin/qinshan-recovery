package com.sunwayworld.owned.attachments.replay;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Loopback-only HTTP replay transport; no production URL or redirect is accepted. */
public final class OwnedErpReplayHttpTransport implements OwnedReplayTransport {
    private final URI base;
    public OwnedErpReplayHttpTransport(URI base) {
        if (base == null || !"http".equalsIgnoreCase(base.getScheme()) || base.getUserInfo() != null
                || base.getQuery() != null || base.getFragment() != null || !loopback(base.getHost())) {
            throw new IllegalArgumentException("ERP replay transport must use loopback HTTP");
        }
        if (base.getPort() < 1 || !"".equals(base.getPath()) || base.getRawAuthority() == null) throw new IllegalArgumentException("ERP replay base must contain only loopback origin");
        this.base = base;
    }
    @Override public OwnedReplayResponse post(String path, String merchantId, String fileSign, String idempotencyKey) throws Exception {
        if (!OwnedErpReplayFileService.DOWNLOAD_PATH.equals(path)) throw new IllegalArgumentException("unsupported ERP replay path");
        URI endpoint = base.resolve(path);
        HttpURLConnection connection = (HttpURLConnection) endpoint.toURL().openConnection();
        connection.setRequestMethod("POST"); connection.setConnectTimeout(3000); connection.setReadTimeout(5000);
        connection.setInstanceFollowRedirects(false);
        connection.setDoOutput(true); connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("X-Replay-Source", "erp-replay"); connection.setRequestProperty("X-Idempotency-Key", idempotencyKey);
        byte[] form = ("fileSign=" + URLEncoder.encode(fileSign, "UTF-8") + "&merchantId=" + URLEncoder.encode(merchantId, "UTF-8")).getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(form.length);
        try (OutputStream output = connection.getOutputStream()) { output.write(form); }
        int status = connection.getResponseCode();
        InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        if (stream != null) try (InputStream input = stream) { byte[] buffer = new byte[4096]; int count; while ((count = input.read(buffer)) != -1) { if (body.size() + count > 4 * 1024 * 1024) throw new IOException("replay response too large"); body.write(buffer, 0, count); } }
        connection.disconnect();
        return new OwnedReplayResponse(status, body.toByteArray());
    }
    private static boolean loopback(String host) {
        return "127.0.0.1".equals(host) || "[::1]".equals(host) || "::1".equals(host);
    }
}
