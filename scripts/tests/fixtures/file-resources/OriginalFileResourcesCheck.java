import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import com.sunwayworld.boot.ApplicationMvcConfiguration;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.filter.FileSecureFilter;
import com.sunwayworld.module.item.file.manager.CoreFileManager;

/** Real resource registration and handler execution; no listener or database. */
public final class OriginalFileResourcesCheck {
    private static final byte[] CONTENT = "approved unicode cache fixture\n".getBytes(StandardCharsets.UTF_8);
    private static final String FILE_URL = "/files/secure/20240717/approved-cache/附件(已处理).txt";

    private static final class ExposedRegistry extends ResourceHandlerRegistry {
        ExposedRegistry(ApplicationContext context, ServletContext servletContext) {
            super(context, servletContext);
        }
        SimpleUrlHandlerMapping mapping() { return (SimpleUrlHandlerMapping) super.getHandlerMapping(); }
    }

    private static MockHttpServletResponse perform(SimpleUrlHandlerMapping mapping,
            MockServletContext servletContext, String method, String path) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, method, path);
        MockHttpServletResponse response = new MockHttpServletResponse();
        dispatch(mapping, request, response);
        return response;
    }

    private static void dispatch(SimpleUrlHandlerMapping mapping,
            MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
        HandlerExecutionChain chain = mapping.getHandler(request);
        if (chain == null) {
            response.setStatus(404);
            return;
        }
        try {
            for (org.springframework.web.servlet.HandlerInterceptor interceptor : chain.getInterceptors() == null ? new org.springframework.web.servlet.HandlerInterceptor[0] : chain.getInterceptors()) {
                if (!interceptor.preHandle(request, response, chain.getHandler())) return;
            }
            new HttpRequestHandlerAdapter().handle(request, response, chain.getHandler());
        } catch (HttpRequestMethodNotSupportedException error) {
            response.setStatus(405);
        }
    }

    private static void expect(int status, byte[] bytes, MockHttpServletResponse response, String label) {
        if (response.getStatus() != status) {
            throw new AssertionError(label + " expected " + status + " but was " + response.getStatus());
        }
        if (bytes != null && !Arrays.equals(bytes, response.getContentAsByteArray())) {
            throw new AssertionError(label + " response bytes differ");
        }
        System.out.println(label + ": PASS (" + status + ")");
    }

    private static FileSecureFilter permissionFilter() {
        FileSecureFilter filter = new FileSecureFilter();
        CoreFileManager manager = (CoreFileManager) Proxy.newProxyInstance(
                OriginalFileResourcesCheck.class.getClassLoader(), new Class<?>[]{CoreFileManager.class},
                (proxy, method, arguments) -> {
                    if ("getSecurity".equals(method.getName())) return "approved-signature";
                    if ("toString".equals(method.getName())) return "signature-calculator-test-dependency";
                    throw new AssertionError("Unexpected manager method: " + method.getName());
                });
        ReflectionTestUtils.setField(filter, "fileManager", manager);
        ReflectionTestUtils.setField(filter, "accessPermissionEnabled", true);
        return filter;
    }

    private static void protectedRequest(FileSecureFilter filter, SimpleUrlHandlerMapping mapping,
            MockServletContext servletContext, String method, String path, String security,
            boolean allowed, String label) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, method, path);
        if (security != null) request.addParameter("security", security);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean reachedHandler = new AtomicBoolean();
        FilterChain actualResourceChain = (req, res) -> {
            reachedHandler.set(true);
            try {
                dispatch(mapping, (MockHttpServletRequest) req, (MockHttpServletResponse) res);
            } catch (Exception error) {
                throw new javax.servlet.ServletException(error);
            }
        };
        Throwable rejection = null;
        try {
            filter.doFilter(request, response, actualResourceChain);
        } catch (com.sunwayworld.framework.exception.core.ApplicationRuntimeException error) {
            rejection = error;
        }
        if (!allowed) {
            if (reachedHandler.get()) throw new AssertionError(label + " reached file resource handler");
            if (response.getContentAsByteArray().length != 0) throw new AssertionError(label + " exposed file bytes");
            if (rejection == null && response.getStatus() < 400) {
                throw new AssertionError(label + " neither rejected nor returned an error");
            }
            System.out.println(label + ": PASS (denied before handler)");
        } else {
            if (rejection != null) throw new AssertionError(label + " rejected valid signature", rejection);
            if (!reachedHandler.get()) throw new AssertionError(label + " did not reach resource handler");
            expect(200, "HEAD".equals(method) ? new byte[0] : CONTENT, response, label);
            if ("HEAD".equals(method) && !String.valueOf(CONTENT.length).equals(response.getHeader("Content-Length"))) {
                throw new AssertionError(label + " did not preserve actual file length");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path root = Paths.get(args[0]);
        System.setProperty("user.dir", root.toString());
        Path cache = root.resolve("files/secure/20240717/approved-cache/附件(已处理).txt");
        Files.createDirectories(cache.getParent());
        Files.write(cache, CONTENT);
        Files.write(root.resolve("outside.txt"), "outside-must-not-be-served".getBytes(StandardCharsets.UTF_8));
        MockServletContext servletContext = new MockServletContext();
        try (GenericWebApplicationContext context = new GenericWebApplicationContext()) {
            context.setServletContext(servletContext);
            context.refresh();
            ApplicationContextHelper.setApplicationContext(context);
            org.springframework.context.support.StaticMessageSource messages = new org.springframework.context.support.StaticMessageSource();
            messages.setUseCodeAsDefaultMessage(true);
            new com.sunwayworld.framework.i18n.I18nHelper().setMessageSource(messages);
            ExposedRegistry registry = new ExposedRegistry(context, servletContext);
            new ApplicationMvcConfiguration(new WebMvcProperties()).addResourceHandlers(registry);
            SimpleUrlHandlerMapping mapping = registry.mapping();
            mapping.setServletContext(servletContext);
            mapping.setApplicationContext(context);
            String selected = args.length > 1 ? args[1] : "all";
            if (!"security".equals(selected)) {
                expect(200, CONTENT, perform(mapping, servletContext, "GET", FILE_URL), "original-files-unicode-GET");
                expect(200, "static-fixture\n".getBytes(StandardCharsets.UTF_8),
                        perform(mapping, servletContext, "GET", "/static/file-resource-fixture.txt"), "original-static-GET");
                expect(404, null, perform(mapping, servletContext, "GET", "/files/secure/missing.txt"), "missing-file-GET");
                expect(404, null, perform(mapping, servletContext, "GET", "/files/../outside.txt"), "traversal-file-GET");
                expect(405, null, perform(mapping, servletContext, "POST", FILE_URL), "original-files-POST");
            }
            if (!"mapping".equals(selected)) {
                FileSecureFilter filter = permissionFilter();
                for (String method : new String[]{"HEAD", "GET"}) {
                    protectedRequest(filter, mapping, servletContext, method, FILE_URL, null, false, method + "-missing-signature");
                    protectedRequest(filter, mapping, servletContext, method, FILE_URL, "bad-signature", false, method + "-bad-signature");
                    protectedRequest(filter, mapping, servletContext, method, "/files/invalid-shape", "approved-signature", false, method + "-invalid-shape");
                    protectedRequest(filter, mapping, servletContext, method, "/files/unknown/20240717/approved-cache/fixture.txt", "approved-signature", false, method + "-unknown-scope");
                    protectedRequest(filter, mapping, servletContext, method, FILE_URL, "approved-signature", true, method + "-valid-signature");
                }
            }
        } finally {
            ApplicationContextHelper.setApplicationContext(null);
        }
    }
}
