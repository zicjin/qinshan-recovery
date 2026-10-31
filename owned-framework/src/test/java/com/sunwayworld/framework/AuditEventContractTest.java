package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.RotatingFileAuditEventSink;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;

/** Verifies audit records retain actor, organization, action, result and source. */
public final class AuditEventContractTest {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "owned-audit-contract-" + System.nanoTime() + ".jsonl");
        AuditEvent event = new AuditEvent(Instant.parse("2026-01-02T03:04:05Z"), "authorize",
                "u-a", "org-a", "personnel.read", AuditEvent.Outcome.DENIED,
                "permission-denied", "owned-http", Collections.<String, String>emptyMap());
        RotatingFileAuditEventSink sink = new RotatingFileAuditEventSink(path, 4096, 1);
        sink.record(event);
        String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        if (!json.contains("\"actorId\":\"u-a\"")
                || !json.contains("\"organizationId\":\"org-a\"")
                || !json.contains("\"action\":\"authorize\"")
                || !json.contains("\"outcome\":\"DENIED\"")
                || !json.contains("\"source\":\"owned-http\"")) {
            throw new AssertionError("audit provenance fields missing: " + json);
        }
        AuditEvent nullable = new AuditEvent(Instant.parse("2026-01-02T03:04:06Z"), "logout",
                null, null, null, AuditEvent.Outcome.DENIED, null, "owned-http", Collections.<String, String>emptyMap());
        sink.record(nullable);
        String[] lines = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim().split("\\n");
        String nullableJson = lines[lines.length - 1];
        if (!nullableJson.contains("\"actorId\":null")
                || !nullableJson.contains("\"organizationId\":null")
                || !nullableJson.contains("\"resource\":null")
                || !nullableJson.contains("\"reason\":null")) {
            throw new AssertionError("nullable audit fields must remain JSON null: " + nullableJson);
        }
        if (com.alibaba.fastjson.JSON.parseObject(nullableJson).get("organizationId") != null) {
            throw new AssertionError("nullable organizationId must parse as null: " + nullableJson);
        }
        Files.deleteIfExists(path);
        Files.deleteIfExists(path.resolveSibling(path.getFileName().toString() + ".1"));
        System.out.println("audit event contract checks passed");
    }
}
