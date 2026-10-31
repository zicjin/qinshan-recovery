package com.sunwayworld.owned.internal.web;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Bounded, durable audit sink for deployments that do not provide their own
 * event store. Each event is one JSON line; rotation bounds local disk use.
 */
public final class RotatingFileAuditEventSink implements AuditEventSink {
    private final Path path;
    private final long maxBytes;
    private final int archiveCount;

    public RotatingFileAuditEventSink(Path path, long maxBytes, int archiveCount) {
        if (path == null) throw new IllegalArgumentException("audit path is required");
        if (maxBytes < 256) throw new IllegalArgumentException("audit max-bytes must be at least 256");
        if (archiveCount < 1) throw new IllegalArgumentException("audit archive-count must be positive");
        this.path = path;
        this.maxBytes = maxBytes;
        this.archiveCount = archiveCount;
        try {
            initialize();
        } catch (IOException failure) {
            throw new IllegalStateException("Unable to open configured security audit file", failure);
        }
    }

    @Override
    public synchronized void record(AuditEvent event) {
        if (event == null) throw new IllegalArgumentException("audit event is required");
        byte[] line = (json(event) + "\n").getBytes(StandardCharsets.UTF_8);
        if (line.length > maxBytes) throw new IllegalStateException("Security audit event exceeds configured max-bytes");
        try {
            initialize();
            long current = Files.exists(path) ? Files.size(path) : 0L;
            if (current > 0 && current + line.length > maxBytes) rotate();
            initialize();
            try (FileChannel output = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
                ByteBuffer buffer = ByteBuffer.wrap(line);
                while (buffer.hasRemaining()) output.write(buffer);
                output.force(true);
            }
        } catch (IOException failure) {
            throw new IllegalStateException("Unable to persist security audit event", failure);
        }
    }

    private void initialize() throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        if (!Files.exists(path)) {
            if (Files.getFileStore(parent == null ? path.toAbsolutePath().getParent() : parent)
                    .supportsFileAttributeView("posix")) {
                Files.createFile(path, java.nio.file.attribute.PosixFilePermissions.asFileAttribute(
                        java.nio.file.attribute.PosixFilePermissions.fromString("rw-------")));
            } else {
                Files.createFile(path);
            }
        }
        if (!Files.isRegularFile(path) || !Files.isWritable(path)) {
            throw new IOException("Configured audit path is not a writable file");
        }
    }

    private void rotate() throws IOException {
        for (int index = archiveCount - 1; index >= 1; index--) {
            Path old = archive(index);
            Path next = archive(index + 1);
            if (Files.exists(old)) Files.move(old, next, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        Files.move(path, archive(1), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private Path archive(int index) { return path.resolveSibling(path.getFileName().toString() + "." + index); }

    private static String json(AuditEvent event) {
        return "{\"occurredAt\":\"" + escape(String.valueOf(event.getOccurredAt()))
                + "\",\"action\":\"" + escape(event.getAction())
                + "\",\"actorId\":" + nullable(event.getActorId())
                + ",\"organizationId\":" + nullable(event.getOrganizationId())
                + ",\"resource\":" + nullable(event.getResource())
                + ",\"outcome\":\"" + escape(String.valueOf(event.getOutcome()))
                + "\",\"reason\":" + nullable(event.getReason())
                + ",\"source\":\"" + escape(event.getSource())
                + "\",\"attributes\":" + com.alibaba.fastjson.JSON.toJSONString(event.getAttributes()) + "}";
    }

    private static String nullable(String value) {
        return value == null ? "null" : "\"" + escape(value) + "\"";
    }

    private static String escape(String value) {
        if (value == null) return "";
        StringBuilder result = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character == '\\' || character == '"') result.append('\\').append(character);
            else if (character == '\n') result.append("\\n");
            else if (character == '\r') result.append("\\r");
            else if (character == '\t') result.append("\\t");
            else if (character < 0x20) result.append(String.format("\\u%04x", (int) character));
            else result.append(character);
        }
        return result.toString();
    }
}
