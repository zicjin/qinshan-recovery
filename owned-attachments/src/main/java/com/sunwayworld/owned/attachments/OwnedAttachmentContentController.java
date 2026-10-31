package com.sunwayworld.owned.attachments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Serves only authorized home attachments under an explicit isolated file root. */
@RestController
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.attachments.enabled", havingValue = "true")
public final class OwnedAttachmentContentController {
    private final OwnedAttachmentReadDao dao;
    private final Path root;
    public OwnedAttachmentContentController(OwnedAttachmentReadDao dao, org.springframework.core.env.Environment environment) {
        String configured = environment.getProperty("owned.attachments.root");
        if (configured == null || configured.trim().isEmpty()) throw new IllegalStateException("Explicit owned.attachments.root required");
        Path root = Paths.get(configured);
        this.dao = dao;
        this.root = root.toAbsolutePath().normalize();
        try {
            if (!Files.isDirectory(this.root, LinkOption.NOFOLLOW_LINKS)
                    || !this.root.equals(this.root.toRealPath())) throw new IllegalStateException("Unsafe attachment root");
        } catch (IOException failure) { throw new IllegalStateException("Attachment root unavailable", failure); }
    }

    @GetMapping("/files/secure/{date}/{md5}/{name:.+}")
    public ResponseEntity<byte[]> content(@PathVariable String date, @PathVariable String md5, @PathVariable String name) {
        OwnedAttachmentReadDao.FileRecord record = dao.content("secure", date, md5, name);
        record.validate();
        Path file = root.resolve(record.scope).resolve(record.date).resolve(record.md5).resolve(record.name).normalize();
        if (!file.startsWith(root)) throw new IllegalArgumentException("Attachment path outside root");
        Path current = root;
        for (Path part : root.relativize(file)) {
            current = current.resolve(part);
            if (Files.isSymbolicLink(current)) throw new IllegalArgumentException("Attachment symlink rejected");
        }
        try {
            if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) throw new OwnedAttachmentReadDao.MissingFile();
            if (!file.toRealPath().startsWith(root)) throw new IllegalArgumentException("Attachment path outside root");
            Set<OpenOption> options = new HashSet<OpenOption>();
            options.add(StandardOpenOption.READ); options.add(LinkOption.NOFOLLOW_LINKS);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (SeekableByteChannel channel = Files.newByteChannel(file, options)) {
                if (channel.size() != record.size || channel.size() > 1048576) throw new IllegalStateException("Attachment size mismatch");
                ByteBuffer buffer = ByteBuffer.allocate(4096); int count;
                while ((count = channel.read(buffer)) != -1) {
                    if (output.size() + count > record.size || output.size() + count > 1048576) throw new IllegalStateException("Attachment changed while reading");
                    output.write(buffer.array(), 0, count); buffer.clear();
                }
            }
            if (output.size() != record.size) throw new IllegalStateException("Attachment size mismatch");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + org.springframework.web.util.UriUtils.encodePathSegment(record.name, java.nio.charset.StandardCharsets.UTF_8))
                    .body(output.toByteArray());
        } catch (NoSuchFileException failure) { throw new OwnedAttachmentReadDao.MissingFile(); }
        catch (IOException failure) { throw new IllegalStateException("Attachment content unavailable", failure); }
    }
}
