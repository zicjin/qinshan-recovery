package com.sunwayworld.owned.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

/** File replay used only by isolated tests; caller supplies the disposable fixture root. */
public final class OwnedSchedulerReplay {
    private OwnedSchedulerReplay() { }
    public static String copy(Path fixture, Path output) throws IOException {
        if (fixture == null || output == null) throw new IllegalArgumentException("fixture and output are required");
        if (!Files.isRegularFile(fixture, LinkOption.NOFOLLOW_LINKS)) throw new IOException("fixture is not a regular file");
        Path source = fixture.toAbsolutePath().normalize(); Path target = output.toAbsolutePath().normalize();
        if (source.equals(target)) throw new IOException("replay output must differ from fixture");
        if (target.getParent() == null) throw new IOException("replay output must have a parent");
        Files.createDirectories(target.getParent());
        if (Files.size(source) > 1048576) throw new IOException("fixture exceeds size limit");
        MessageDigest digest;
        try { digest = MessageDigest.getInstance("SHA-256"); } catch (Exception impossible) { throw new IOException(impossible); }
        try (InputStream in = Files.newInputStream(source, LinkOption.NOFOLLOW_LINKS); OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
            byte[] buffer = new byte[8192]; int count;
            long copied = 0;
            while ((count = in.read(buffer)) >= 0) { if (count != 0) {
                copied += count;
                if (copied > 1048576) throw new IOException("fixture exceeds size limit");
                digest.update(buffer, 0, count); out.write(buffer, 0, count);
            } }
        }
        return hex(digest.digest());
    }
    private static String hex(byte[] bytes) { StringBuilder value = new StringBuilder(bytes.length * 2); for (byte b : bytes) value.append(String.format("%02x", b & 255)); return value.toString(); }
}
