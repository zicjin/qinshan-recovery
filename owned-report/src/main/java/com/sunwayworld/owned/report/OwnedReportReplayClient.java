package com.sunwayworld.owned.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import org.springframework.core.env.Environment;

/** Digest-addressed local replay only; there is no network transport or production URL. */
public final class OwnedReportReplayClient {
    private final Environment environment;
    public OwnedReportReplayClient(Environment environment) { this.environment = environment; }

    public ReplayResult render(String canonicalRequest) {
        if (!"replay".equals(environment.getProperty("owned.report.replay.mode"))) throw new Disabled();
        String configured = environment.getProperty("owned.report.replay.root");
        if (configured == null || configured.trim().length() == 0) throw new Disabled();
        try {
            Path root = Paths.get(configured).toRealPath();
            Path fixture = root.resolve(sha256(canonicalRequest) + ".json");
            if (!Files.isRegularFile(fixture, LinkOption.NOFOLLOW_LINKS) || Files.size(fixture) > 262144)
                throw new Failure("REPORT_REPLAY_MISSING");
            JSONObject value = JSON.parseObject(new String(Files.readAllBytes(fixture), StandardCharsets.UTF_8), Feature.DisableSpecialKeyDetect);
            if (!"synthetic-isolated-replay".equals(value.getString("source"))
                    || !canonicalRequest.equals(value.getString("request"))
                    || !value.keySet().equals(new java.util.HashSet<String>(java.util.Arrays.asList("source", "request", "status", "body"))))
                throw new Failure("REPORT_REPLAY_INVALID");
            int status = value.getIntValue("status");
            if (status != 200) throw new Failure("REPORT_REPLAY_UPSTREAM_" + status);
            String body = value.getString("body");
            if (body == null || body.trim().length() == 0) throw new Failure("REPORT_REPLAY_INVALID");
            return new ReplayResult(body, sha256(canonicalRequest));
        } catch (Failure failure) { throw failure; }
        catch (Exception failure) { throw new Failure("REPORT_REPLAY_UNAVAILABLE"); }
    }

    private static String sha256(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b & 255));
            return result.toString();
        } catch (Exception impossible) { throw new IllegalStateException(impossible); }
    }

    public static final class ReplayResult {
        private final String body, requestHash;
        ReplayResult(String body, String requestHash) { this.body = body; this.requestHash = requestHash; }
        public String getBody() { return body; }
        public String getRequestHash() { return requestHash; }
        public String getSource() { return "synthetic-isolated-replay"; }
    }
    public static final class Disabled extends RuntimeException {}
    public static final class Failure extends RuntimeException { public Failure(String code) { super(code); } }
}
