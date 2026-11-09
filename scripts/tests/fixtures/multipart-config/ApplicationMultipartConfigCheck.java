import com.sunwayworld.boot.ApplicationConfiguration;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import javax.servlet.MultipartConfigElement;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;

/** Uses the application bean and the same public Tomcat parser as the runtime. */
public class ApplicationMultipartConfigCheck {
    private static int checks;
    private static void equal(Object expected, Object actual, String label) {
        checks++;
        if (!Objects.equals(expected, actual)) throw new AssertionError(label+": expected "+expected+", got "+actual);
    }
    private static MultipartConfigElement config(Map<String,Object> properties) throws Exception {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture", properties));
            context.refresh();
            ApplicationConfiguration configuration = new ApplicationConfiguration();
            configuration.setApplicationContext(context);
            return configuration.multipartConfigElement();
        }
    }
    private static class Body implements UploadContext {
        private final byte[] bytes;
        private final long declared;
        boolean streamOpened;
        Body(byte[] bytes, long declared) { this.bytes=bytes; this.declared=declared; }
        public String getCharacterEncoding() { return "UTF-8"; }
        public String getContentType() { return "multipart/form-data; boundary=MultipartConfigRegression"; }
        public int getContentLength() { return (int)declared; }
        public long contentLength() { return declared; }
        public InputStream getInputStream() { streamOpened=true; return new ByteArrayInputStream(bytes); }
    }
    private static byte[] body(int size) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bytes.write(("--MultipartConfigRegression\r\nContent-Disposition: form-data; name=\"probe\"; filename=\"inert.bin\"\r\nContent-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byte[] content = new byte[size]; Arrays.fill(content, (byte)'x'); bytes.write(content);
        bytes.write("\r\n--MultipartConfigRegression--\r\n".getBytes(StandardCharsets.UTF_8));
        return bytes.toByteArray();
    }
    private static void parser(MultipartConfigElement config) throws Exception {
        Path directory = Paths.get(config.getLocation());
        DiskFileItemFactory factory = new DiskFileItemFactory(config.getFileSizeThreshold(), directory.toFile());
        ServletFileUpload parser = new ServletFileUpload(factory);
        parser.setFileSizeMax(config.getMaxFileSize()); parser.setSizeMax(config.getMaxRequestSize());
        byte[] bytes = body(3072);
        Body valid = new Body(bytes, bytes.length);
        List<FileItem> parts = parser.parseRequest(valid);
        try {
            equal(1, parts.size(), "one real parsed file part");
            equal(false, parts.get(0).isInMemory(), "above threshold spills to original directory");
            equal(3072L, parts.get(0).getSize(), "file content size");
            try (java.util.stream.Stream<Path> paths = Files.list(directory)) {
                equal(1L, paths.count(), "one spool file observed");
            }
        } finally { for (FileItem part : parts) part.delete(); }
        try (java.util.stream.Stream<Path> paths = Files.list(directory)) {
            equal(0L, paths.count(), "successful multipart cleanup");
        }
        Body declared = new Body(body(1), config.getMaxRequestSize()+1);
        try { parser.parseRequest(declared); throw new AssertionError("declared oversized request accepted"); }
        catch (SizeLimitExceededException expected) {
            equal(config.getMaxRequestSize(), expected.getPermittedSize(), "configured request maximum");
            equal(config.getMaxRequestSize()+1, expected.getActualSize(), "declared request length");
            equal(false, declared.streamOpened, "reject oversized declaration before body stream");
        }
        byte[] oversizedFile = body(9216);
        try { parser.parseRequest(new Body(oversizedFile, oversizedFile.length)); throw new AssertionError("oversized file accepted"); }
        catch (FileSizeLimitExceededException expected) { equal(8192L, expected.getPermittedSize(), "configured file maximum"); }
        try (java.util.stream.Stream<Path> paths = Files.list(directory)) {
            equal(0L, paths.count(), "failed multipart cleanup");
        }
    }
    public static void main(String[] args) throws Exception {
        String mode = args[0]; Map<String,Object> properties = new HashMap<>();
        Path expectedLocation = Paths.get(System.getProperty("user.dir"), "files", "temp", "multipart");
        if (mode.equals("directory-failure")) {
            Files.createDirectories(expectedLocation.getParent()); Files.write(expectedLocation, new byte[]{1});
            try { config(properties); throw new AssertionError("unusable multipart directory silently accepted"); }
            catch (IOException expected) { equal(true, Files.isRegularFile(expectedLocation), "directory failure preserved"); }
        } else if (mode.equals("empty") || mode.equals("invalid")) {
            properties.put("spring.servlet.multipart.max-file-size", mode.equals("empty") ? "" : "not-a-size");
            try { config(properties); throw new AssertionError("invalid configured size silently defaulted"); }
            catch (IllegalArgumentException expected) { checks++; }
        } else {
            if (!mode.equals("defaults")) {
                properties.put("spring.servlet.multipart.file-size-threshold", "1KB");
                properties.put("spring.servlet.multipart.max-file-size", "8KB");
                properties.put("spring.servlet.multipart.max-request-size", "16KB");
            }
            MultipartConfigElement actual = config(properties);
            equal(mode.equals("defaults") ? 104857600L : 8192L, actual.getMaxFileSize(), "source-backed max file bytes");
            equal(mode.equals("defaults") ? 104857600L : 16384L, actual.getMaxRequestSize(), "source-backed max request bytes");
            equal(mode.equals("defaults") ? 2097152 : 1024, actual.getFileSizeThreshold(), "source-backed disk threshold");
            equal(expectedLocation.toString(), actual.getLocation(), "original multipart location");
            equal(true, Files.isDirectory(expectedLocation), "original directory created");
            if (mode.equals("parser")) parser(actual);
        }
        System.out.println("Actual multipart configuration/parser "+mode+": PASS "+checks);
    }
}
