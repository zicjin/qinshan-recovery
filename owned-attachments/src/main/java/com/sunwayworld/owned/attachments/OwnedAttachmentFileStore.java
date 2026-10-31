package com.sunwayworld.owned.attachments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Resolves and removes attachment files below the explicitly configured root. */
final class OwnedAttachmentFileStore {
    private final Path root;

    OwnedAttachmentFileStore(Path root) {
        if (root == null) throw new IllegalArgumentException("attachment root is required");
        this.root = root.toAbsolutePath().normalize();
    }

    Path resolve(String scope, String date, String md5, String name) {
        if (!"secure".equals(scope) || date == null || !date.matches("[0-9]{8}")
                || md5 == null || !md5.matches("[a-f0-9]{32}") || name == null
                || name.length() > 200 || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0
                || ".".equals(name) || "..".equals(name) || name.matches(".*[\\x00-\\x1f\\x7f].*")) {
            throw new IllegalArgumentException("Unsafe attachment metadata");
        }
        Path result = root.resolve(scope).resolve(date).resolve(md5).resolve(name).normalize();
        if (!result.startsWith(root)) throw new IllegalArgumentException("Attachment path outside root");
        return result;
    }

    void deleteQuietly(Path path) {
        if (path == null || !path.normalize().startsWith(root)) return;
        try { Files.deleteIfExists(path); } catch (IOException ignored) { }
    }
}
