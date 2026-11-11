import com.sunwayworld.emc.framework.file.LocalCoreFileManager;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.filter.FileSecureFilter;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.manager.impl.CoreFileFtpManagerImpl;
import com.sunwayworld.module.item.file.filter.CoreFileFtpFilter;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.exception.FtpException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.time.LocalDateTime;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Collections;
import java.util.stream.Stream;

/** Public storage seam; real filesystem, no listener, database or supplier runtime. */
public final class LocalCoreFileManagerCheck {
    private static int checks;
    private static final byte[] CONTENT = "local attachment bytes\n".getBytes(StandardCharsets.UTF_8);

    static void check(boolean value, String label) {
        checks++;
        if (!value) throw new AssertionError(label);
    }

    static FilePathDTO file(String name) {
        return FilePathDTO.of("secure", "20261111", "fixture-hash", name);
    }

    interface Attempt { void run() throws Exception; }

    static void rejects(Attempt attempt, String label) throws Exception {
        try {
            attempt.run();
        } catch (RuntimeException expected) {
            check(true, label);
            return;
        }
        throw new AssertionError(label + " accepted");
    }

    static void bytes(Path temporary) throws Exception {
        LocalCoreFileManager manager = new LocalCoreFileManager(temporary.resolve("files"));
        final boolean[] closed = {false};
        ByteArrayInputStream stream = new ByteArrayInputStream(CONTENT) {
            @Override public void close() { closed[0] = true; }
        };
        manager.upload(file("attachment.txt"), stream);
        check(!closed[0], "caller owns upload stream");
        Path result = temporary.resolve("download.txt");
        manager.download(file("attachment.txt"), result);
        check(Arrays.equals(CONTENT, Files.readAllBytes(result)), "upload/download preserve bytes");
        manager.upload(file("multipart.txt"), new MockMultipartFile("file", CONTENT));
        manager.download(file("multipart.txt"), result);
        check(Arrays.equals(CONTENT, Files.readAllBytes(result)), "multipart upload preserves bytes");
        manager.upload(file("path.txt"), result);
        manager.copyFile(file("path.txt"), file("copied.txt"));
        manager.download(file("copied.txt"), result);
        check(Arrays.equals(CONTENT, Files.readAllBytes(result)), "Path upload and copy preserve bytes");
        CoreFileBean from = new CoreFileBean();
        from.setScope("secure"); from.setMd5Name("fixture-hash"); from.setName("copied.txt");
        from.setCreatedTime(LocalDateTime.of(2026, 11, 11, 12, 0));
        manager.duplicateFile(from, "duplicate.txt");
        manager.download(file("duplicate.txt"), result);
        check(Arrays.equals(CONTENT, Files.readAllBytes(result)), "duplicate uses original file metadata path");
        check("copied.txt".equals(from.getName()), "duplicate leaves source metadata intact");
        Path local = temporary.resolve("files/secure/20261111/fixture-hash/attachment.txt");
        manager.upload(file("attachment.txt"), local);
        manager.download(file("attachment.txt"), local);
        manager.copyFile(file("attachment.txt"), file("attachment.txt"));
        check(Arrays.equals(CONTENT, Files.readAllBytes(local)), "same-file operations do not truncate bytes");
        FilePathDTO report = FilePathDTO.of("finereport", "20261111", "template", "report.txt");
        manager.upload(report, new ByteArrayInputStream(CONTENT));
        check(Files.isRegularFile(temporary.resolve("files/finereport/template/report.txt")),
                "finereport retains original timestamp-free layout");
    }

    static void atomic(Path temporary) throws Exception {
        Path root = temporary.resolve("atomic-files");
        LocalCoreFileManager manager = new LocalCoreFileManager(root);
        manager.upload(file("existing.txt"), new ByteArrayInputStream(CONTENT));
        Path existing = root.resolve("secure/20261111/fixture-hash/existing.txt");
        final boolean[] closed = {false};
        InputStream broken = new InputStream() {
            int remaining = 20;
            @Override public int read() throws IOException {
                if (--remaining < 0) throw new IOException("private storage diagnostic must not escape");
                return 'x';
            }
            @Override public void close() { closed[0] = true; }
        };
        rejects(() -> manager.upload(file("existing.txt"), broken), "partial upload fails explicitly");
        check(Arrays.equals(CONTENT, Files.readAllBytes(existing)), "partial upload preserves previous bytes");
        check(!closed[0], "failed upload leaves caller stream open");
        try (Stream<Path> children = Files.list(existing.getParent())) {
            check(children.count() == 1, "failed upload removes temporary file");
        }
        rejects(() -> manager.download(file("missing.txt"), existing), "missing source cannot overwrite download target");
        check(Arrays.equals(CONTENT, Files.readAllBytes(existing)), "missing download retains existing target");
        Path absent = root.resolve("secure/20261111/fixture-hash/missing.txt");
        rejects(() -> manager.download(file("missing.txt"), absent), "same-path missing download fails");
        rejects(() -> manager.upload(file("missing.txt"), absent), "same-path missing upload fails");
        rejects(() -> manager.copyFile(file("missing.txt"), file("missing.txt")), "same-path missing copy fails");
        rejects(() -> manager.copyFile(file("missing.txt"), file("existing.txt")), "copy missing source fails");
        check(!Files.exists(absent), "missing source does not create a success placeholder");
        Path directory = root.resolve("secure/20261111/fixture-hash/directory.txt");
        Files.createDirectory(directory);
        rejects(() -> manager.download(file("directory.txt"), directory), "same-path directory download fails");
    }

    static void paths(Path temporary) throws Exception {
        Path root = temporary.resolve("safe-files");
        LocalCoreFileManager manager = new LocalCoreFileManager(root);
        byte[] outside = "outside storage must be preserved".getBytes(StandardCharsets.UTF_8);
        for (String component : new String[]{"..", ".", "../escape", "/absolute", "a/b", "a\\b", "C:\\absolute", "bad\nname"}) {
            rejects(() -> manager.upload(FilePathDTO.of("secure", "20261111", component, "fixture.txt"), new ByteArrayInputStream(CONTENT)),
                    "unsafe irregular name");
            rejects(() -> manager.upload(FilePathDTO.of("secure", component, "fixture", "fixture.txt"), new ByteArrayInputStream(CONTENT)),
                    "unsafe timestamp");
            rejects(() -> manager.upload(file(component), new ByteArrayInputStream(CONTENT)), "unsafe filename");
        }
        rejects(() -> manager.upload(file(""), new ByteArrayInputStream(CONTENT)), "empty filename");
        rejects(() -> manager.upload(FilePathDTO.of("secure", "", "", "fixture.txt"), new ByteArrayInputStream(CONTENT)),
                "empty irregular name");
        FilePathDTO noDate = FilePathDTO.of("temp", "fixture", "no-date.txt");
        manager.upload(noDate, new ByteArrayInputStream(CONTENT));
        check(Files.isRegularFile(root.resolve("temp/fixture/no-date.txt")), "original empty timestamp is supported");
        Path outsideRoot = temporary.resolve("outside");
        Files.createDirectory(outsideRoot);
        Files.write(outsideRoot.resolve("fixture.txt"), outside);
        Path rootLink = temporary.resolve("linked-files");
        Files.createSymbolicLink(rootLink, outsideRoot);
        rejects(() -> new LocalCoreFileManager(rootLink).upload(file("fixture.txt"), new ByteArrayInputStream(CONTENT)),
                "symlink root rejected");
        rejects(() -> new LocalCoreFileManager(rootLink.resolve("nested")).upload(file("fixture.txt"), new ByteArrayInputStream(CONTENT)),
                "symlink ancestor of root rejected");
        Files.createSymbolicLink(root.resolve("secure"), outsideRoot);
        rejects(() -> manager.upload(file("fixture.txt"), new ByteArrayInputStream(CONTENT)), "symlink storage ancestor rejected");
        Files.delete(root.resolve("secure"));
        manager.upload(file("fixture.txt"), new ByteArrayInputStream(CONTENT));
        Path stored = root.resolve("secure/20261111/fixture-hash/fixture.txt");
        Files.delete(stored);
        Files.createSymbolicLink(stored, outsideRoot.resolve("fixture.txt"));
        rejects(() -> manager.upload(file("fixture.txt"), new ByteArrayInputStream(CONTENT)), "symlink storage target rejected");
        rejects(() -> manager.download(file("fixture.txt"), temporary.resolve("read.txt")), "symlink source download rejected");
        Files.delete(stored);
        manager.upload(file("fixture.txt"), new ByteArrayInputStream(CONTENT));
        Path sourceLink = temporary.resolve("source-link");
        Files.createSymbolicLink(sourceLink, outsideRoot.resolve("fixture.txt"));
        rejects(() -> manager.upload(file("other.txt"), sourceLink), "symlink upload source rejected");
        Path sourceDirectoryLink = temporary.resolve("source-directory-link");
        Files.createSymbolicLink(sourceDirectoryLink, outsideRoot);
        rejects(() -> manager.upload(file("other.txt"), sourceDirectoryLink.resolve("fixture.txt")), "symlink upload source ancestor rejected");
        Path destinationLink = temporary.resolve("destination-link");
        Files.createSymbolicLink(destinationLink, outsideRoot.resolve("fixture.txt"));
        rejects(() -> manager.download(file("fixture.txt"), destinationLink), "symlink download destination rejected");
        rejects(() -> manager.download(file("fixture.txt"), sourceDirectoryLink.resolve("fixture.txt")), "symlink download destination ancestor rejected");
        check(Arrays.equals(outside, Files.readAllBytes(outsideRoot.resolve("fixture.txt"))), "rejected paths preserve external file bytes");
        rejects(() -> manager.upload(FilePathDTO.of("secure", "20261111", "newdir", "../escape.txt"), new ByteArrayInputStream(CONTENT)),
                "traversal rejected before directory creation");
        check(!Files.exists(root.resolve("secure/20261111/newdir")), "rejected traversal creates no storage directory");
    }

    static void urls(Path temporary) throws Exception {
        StaticApplicationContext context = new StaticApplicationContext();
        ApplicationContextHelper.setApplicationContext(context);
        LocalCoreFileManager manager = new LocalCoreFileManager(temporary.resolve("url-files"));
        FilePathDTO file = file("report one.txt");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/lims/core/module/item/files");
        request.setScheme("https"); request.setServerName("fixture.invalid"); request.setServerPort(8443);
        request.setContextPath("/lims");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        try {
            check("/files/secure/20261111/fixture-hash/report%20one.txt".equals(manager.getDownloadUrl(file)),
                    "relative URL retains original filename encoding");
            check("https://fixture.invalid:8443/lims/files/secure/20261111/fixture-hash/report%20one.txt".equals(manager.getAbsoluteDownloadUrl(file)),
                    "absolute URL retains original request context");
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
                    Collections.<String,Object>singletonMap("sunway.security.file-access-permission.enabled", true)));
            String expected = "/files/secure/20261111/fixture-hash/report%20one.txt?security=da01c7bdc0012e7dba12c725300cd977";
            check(expected.equals(manager.getDownloadUrl(file)), "relative URL uses original security signature");
            check(("https://fixture.invalid:8443/lims" + expected).equals(manager.getAbsoluteDownloadUrl(file)),
                    "absolute URL uses same original security signature");
            FileSecureFilter filter = new FileSecureFilter();
            ReflectionTestUtils.setField(filter, "fileManager", manager);
            ReflectionTestUtils.setField(filter, "accessPermissionEnabled", true);
            MockHttpServletRequest download = new MockHttpServletRequest("GET", "/files/secure/20261111/fixture-hash/report%20one.txt");
            final boolean[] reached = {false};
            rejects(() -> filter.doFilter(download, new MockHttpServletResponse(), (req,res) -> reached[0] = true),
                    "original file security filter rejects missing signature");
            check(!reached[0], "unsigned local URL never reaches static handler");
            download.addParameter("security", "da01c7bdc0012e7dba12c725300cd977");
            filter.doFilter(download, new MockHttpServletResponse(), (req,res) -> reached[0] = true);
            check(reached[0], "local generated URL accepted by original file security filter");
            rejects(() -> manager.getDownloadUrl(file("../escape.txt")), "unsafe URL path rejected");
        } finally {
            RequestContextHolder.resetRequestAttributes();
            ApplicationContextHelper.setApplicationContext(null);
            context.close();
        }
    }

    static void selection(Path temporary) throws Exception {
        System.setProperty("user.dir", temporary.toString());
        for (String mode : new String[]{"absent", "ftp", "local"}) {
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
                if (!"absent".equals(mode)) {
                    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
                            Collections.<String,Object>singletonMap("owned.files.storage", mode)));
                }
                context.getBeanFactory().registerSingleton("originalFtpManager", new CoreFileFtpManagerImpl());
                context.register(LocalCoreFileManager.class);
                context.refresh();
                CoreFileManager selected = context.getBean(CoreFileManager.class);
                check((selected instanceof LocalCoreFileManager) == "local".equals(mode),
                        "local storage requires explicit opt-in and wins type injection");
                check(context.getBeansOfType(CoreFileFtpManagerImpl.class).size() == 1,
                        "opt-in retains original FTP bean in same context");
                if ("local".equals(mode)) {
                    selected.upload(file("default-root.txt"), new ByteArrayInputStream(CONTENT));
                    Path cached = temporary.resolve("files/secure/20261111/fixture-hash/default-root.txt");
                    check(Arrays.equals(CONTENT, Files.readAllBytes(cached)), "noarg constructor shares original static cache root");
                    selected.download(file("default-root.txt"));
                    check(Arrays.equals(CONTENT, Files.readAllBytes(cached)), "original cache filter default download is safe");
                }
            }
        }
    }

    static void missing(Path temporary) throws Exception {
        Path work = temporary.resolve("request-files");
        ReflectionTestUtils.setField(FilePathManager.class, "applicationFileRootPath", work);
        LocalCoreFileManager manager = new LocalCoreFileManager(work.resolve("files"));
        StaticApplicationContext context = new StaticApplicationContext();
        ApplicationContextHelper.setApplicationContext(context);
        CoreFileFtpFilter cache = new CoreFileFtpFilter();
        ReflectionTestUtils.setField(cache, "fileManager", manager);
        CoreFileBean metadata = new CoreFileBean();
        metadata.setCreatedTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        CoreFileService service = (CoreFileService)Proxy.newProxyInstance(
                CoreFileService.class.getClassLoader(), new Class<?>[]{CoreFileService.class},
                (proxy, method, args) -> "selectFirstByFilter".equals(method.getName()) ? metadata : null);
        ReflectionTestUtils.setField(cache, "fileService", service);
        String url = "/files/secure/20261111/fixture-hash/absent.txt";
        try {
            MockHttpServletResponse absent = new MockHttpServletResponse();
            final boolean[] reached = {false};
            cache.doFilter(new MockHttpServletRequest("GET", url), absent, (req, res) -> reached[0] = true);
            check(absent.getStatus() == 404, "missing local GET is truthful 404");
            check(!reached[0] && absent.getContentAsByteArray().length == 0,
                    "missing local GET never creates or serves placeholder bytes");
            check(!Files.exists(work.resolve("files/secure/20261111/fixture-hash/absent.txt")),
                    "missing GET creates no file");

            manager.upload(file("present.txt"), new ByteArrayInputStream(CONTENT));
            String present = "/files/secure/20261111/fixture-hash/present.txt";
            FileSecureFilter security = new FileSecureFilter();
            ReflectionTestUtils.setField(security, "fileManager", manager);
            ReflectionTestUtils.setField(security, "accessPermissionEnabled", true);
            MockHttpServletRequest valid = new MockHttpServletRequest("GET", present);
            valid.addParameter("security", manager.getSecurity(file("present.txt")));
            MockHttpServletResponse good = new MockHttpServletResponse();
            cache.doFilter(valid, good, (req, res) -> security.doFilter(req, res, (r, out) ->
                    out.getOutputStream().write(Files.readAllBytes(work.resolve("files/secure/20261111/fixture-hash/present.txt")))));
            check(good.getStatus() == 200 && Arrays.equals(CONTENT, good.getContentAsByteArray()),
                    "existing signed local GET preserves actual bytes through original security filter");
            MockHttpServletResponse unsigned = new MockHttpServletResponse();
            rejects(() -> cache.doFilter(new MockHttpServletRequest("GET", present), unsigned,
                    (req, res) -> security.doFilter(req, res, (r, out) -> out.getOutputStream().write(CONTENT))),
                    "existing unsigned file remains denied");
            check(unsigned.getContentAsByteArray().length == 0 && unsigned.getStatus() != 404,
                    "security rejection is not masked as missing file");
            MockHttpServletResponse downstream = new MockHttpServletResponse();
            rejects(() -> cache.doFilter(valid, downstream, (req, res) -> {
                throw new UncheckedIOException(new NoSuchFileException("downstream fixture"));
            }), "downstream missing exceptions are not swallowed by storage filter");
            check(downstream.getStatus() != 404, "storage catch does not wrap downstream chain");

            Path directory = work.resolve("files/secure/20261111/fixture-hash/directory.txt");
            Files.createDirectory(directory);
            ReflectionTestUtils.setField(cache, "fileService", Proxy.newProxyInstance(
                    CoreFileService.class.getClassLoader(), new Class<?>[]{CoreFileService.class},
                    (proxy, method, args) -> null));
            MockHttpServletResponse invalid = new MockHttpServletResponse();
            rejects(() -> cache.doFilter(new MockHttpServletRequest("GET", "/files/secure/20261111/fixture-hash/directory.txt"),
                    invalid, (req, res) -> {}), "directory source remains invalid");
            check(invalid.getStatus() != 404, "nonregular source is not classified as absent");
            MockHttpServletResponse traversal = new MockHttpServletResponse();
            rejects(() -> cache.doFilter(new MockHttpServletRequest("GET", "/files/secure/20261111/../escape.txt"),
                    traversal, (req, res) -> {}), "unsafe path remains rejected");
            check(traversal.getStatus() != 404, "unsafe path is not classified as absent");
            ReflectionTestUtils.setField(cache, "fileManager", new CoreFileFtpManagerImpl() {
                @Override public void download(FilePathDTO file, Path target) {
                    throw new FtpException("GIKAM.EXCEPTION.FTP.CONNECTION");
                }
            });
            MockHttpServletResponse ftp = new MockHttpServletResponse();
            rejects(() -> cache.doFilter(new MockHttpServletRequest("GET", url), ftp, (req, res) -> {}),
                    "FTP failure propagates unchanged");
            check(ftp.getStatus() != 404, "FTP unavailable is not declared missing local file");
            ReflectionTestUtils.setField(cache, "fileManager", new CoreFileFtpManagerImpl() {
                @Override public void download(FilePathDTO file, Path target) {
                    throw new UncheckedIOException(new IOException("storage unavailable fixture"));
                }
            });
            MockHttpServletResponse io = new MockHttpServletResponse();
            rejects(() -> cache.doFilter(new MockHttpServletRequest("GET", url), io, (req, res) -> {}),
                    "other storage I/O failure propagates unchanged");
            check(io.getStatus() != 404, "other I/O failure is not classified as absent");
        } finally {
            ReflectionTestUtils.setField(FilePathManager.class, "applicationFileRootPath", null);
            ApplicationContextHelper.setApplicationContext(null);
            context.close();
        }
    }

    public static void main(String[] args) throws Exception {
        Path temporary = Paths.get(args[0]);
        String selected = args[1];
        if ("all".equals(selected) || "bytes".equals(selected)) bytes(temporary);
        if ("all".equals(selected) || "atomic".equals(selected)) atomic(temporary);
        if ("all".equals(selected) || "paths".equals(selected)) paths(temporary);
        if ("all".equals(selected) || "urls".equals(selected)) urls(temporary);
        if ("all".equals(selected) || "selection".equals(selected)) selection(temporary);
        if ("all".equals(selected) || "missing".equals(selected)) missing(temporary);
        System.out.println("PASS local CoreFileManager (" + checks + " checks; " + selected + ")");
    }
}
