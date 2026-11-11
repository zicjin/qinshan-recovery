package com.sunwayworld.emc.framework.file;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Explicit same-application storage adapter for an operator-controlled local file volume.
 * The root and its ancestors must not be writable by untrusted local OS identities.
 */
@Repository
@Primary
@ConditionalOnProperty(name="owned.files.storage", havingValue="local")
public class LocalCoreFileManager implements CoreFileManager {
    private final Path root;

    public LocalCoreFileManager() {
        this(FilePathManager.getApplicationFilePath());
    }

    public LocalCoreFileManager(Path root) {
        this.root = absolute(Objects.requireNonNull(root, "File storage root required"));
    }

    private Path path(FilePathDTO file) {
        Objects.requireNonNull(file, "File descriptor required");
        Objects.requireNonNull(file.getScope(), "File scope required");
        component(file.getIrregularName());
        component(file.getFileName());
        Path directory = root.resolve(file.getScope().name());
        if (file.getScope() != FileScope.finereport && !StringUtils.isBlank(file.getTimestamp())) {
            component(file.getTimestamp());
            directory = directory.resolve(file.getTimestamp());
        }
        Path result = directory.resolve(file.getIrregularName()).resolve(file.getFileName());
        inspect(result);
        return result;
    }

    private static void component(String value) {
        if (value == null || value.isEmpty() || ".".equals(value) || "..".equals(value)
                || value.indexOf('/') >= 0 || value.indexOf('\\') >= 0 || value.indexOf(':') >= 0
                || value.chars().anyMatch(c -> c < 32 || c == 127)) {
            throw new IllegalArgumentException("Invalid local file path component");
        }
    }

    private static Path absolute(Path path) {
        for (Path component : Objects.requireNonNull(path, "File path required")) {
            if (".".equals(component.toString()) || "..".equals(component.toString())) {
                throw new IllegalArgumentException("Invalid local file path");
            }
        }
        return path.toAbsolutePath().normalize();
    }

    /** Inspect every existing ancestor, including ancestors above the storage root. */
    private static void inspect(Path path) {
        Path current = path.getRoot();
        for (Path component : path) {
            current = current.resolve(component);
            if (Files.isSymbolicLink(current)) {
                throw new IllegalStateException("Symbolic links are not permitted in local file paths");
            }
            if (!current.equals(path) && Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                    && !Files.isDirectory(current, LinkOption.NOFOLLOW_LINKS)) {
                throw new IllegalStateException("Invalid local file directory");
            }
        }
    }

    private static void directories(Path directory) throws IOException {
        inspect(directory);
        Path current = directory.getRoot();
        for (Path component : directory) {
            current = current.resolve(component);
            try {
                Files.createDirectory(current);
            } catch (FileAlreadyExistsException existing) {
                // An existing directory is permitted only after a no-follow check.
            }
            if (!Files.isDirectory(current, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(current)) {
                throw new IllegalStateException("Invalid local file directory");
            }
        }
    }

    @Override public void upload(FilePathDTO file, InputStream input) {
        write(path(file), Objects.requireNonNull(input, "Upload stream required"));
    }

    @Override public void upload(FilePathDTO file, MultipartFile input) {
        try (InputStream stream = input.getInputStream()) {
            upload(file, stream);
        } catch (IOException failure) {
            throw new IllegalStateException("Local file upload failed");
        }
    }

    @Override public void upload(FilePathDTO file, Path input) {
        copy(input, path(file));
    }

    @Override public void download(FilePathDTO file, Path target) {
        Path source = path(file);
        // A confirmed absent local object is distinct from an invalid path,
        // a nonregular object, permissions or a failed storage operation.
        if (Files.notExists(source, LinkOption.NOFOLLOW_LINKS)) {
            throw new UncheckedIOException("Local file is missing", new NoSuchFileException("Local file"));
        }
        copy(source, target);
    }

    @Override public void copyFile(FilePathDTO from, FilePathDTO to) {
        upload(to, path(from));
    }

    @Override public void duplicateFile(CoreFileBean from, String name) {
        CoreFileBean to = new CoreFileBean();
        BeanUtils.copyProperties(from, to);
        to.setName(name);
        copyFile(CoreFileUtils.toFilePath(from), CoreFileUtils.toFilePath(to));
    }

    @Override public String getDownloadUrl(FilePathDTO file) {
        path(file);
        return getSecuredDownloadUrl(ServletUtils.getUrl(null, FilePathManager.getUrl(file)), file);
    }

    @Override public String getAbsoluteDownloadUrl(FilePathDTO file) {
        path(file);
        String context = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return getSecuredDownloadUrl(ServletUtils.getUrl(context, FilePathManager.getUrl(file)), file);
    }

    private void copy(Path input, Path output) {
        Path source = absolute(input);
        Path target = absolute(output);
        inspect(source);
        inspect(target);
        if (!Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
            throw new IllegalStateException("Local source file is missing or invalid");
        }
        if (source.equals(target)) return;
        try (InputStream stream = Files.newInputStream(source, LinkOption.NOFOLLOW_LINKS)) {
            write(target, stream);
        } catch (IOException failure) {
            throw new IllegalStateException("Local file copy failed");
        }
    }

    private void write(Path target, InputStream stream) {
        Path temporary = null;
        try {
            inspect(target);
            if (Files.exists(target, LinkOption.NOFOLLOW_LINKS) && !Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
                throw new IllegalStateException("Invalid local file destination");
            }
            directories(target.getParent());
            temporary = Files.createTempFile(target.getParent(), ".local-upload-", ".part");
            try (OutputStream output = Files.newOutputStream(temporary, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING, LinkOption.NOFOLLOW_LINKS)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = stream.read(buffer)) != -1) output.write(buffer, 0, count);
            }
            inspect(target);
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException failure) {
            throw new IllegalStateException("Local file write failed");
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException failure) {
                    throw new IllegalStateException("Local temporary file cleanup failed");
                }
            }
        }
    }
}
