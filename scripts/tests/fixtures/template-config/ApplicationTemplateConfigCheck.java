import com.sunwayworld.boot.ApplicationConfiguration;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.thymeleaf.templateresolver.GikamResourceTemplateResolver;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.page.bean.CorePageConfigDTO;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import com.sunwayworld.module.sys.page.service.CorePageConfigService;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

/** Executes the original request-aware resource reader through real Thymeleaf. */
public class ApplicationTemplateConfigCheck {
    private static int checks;
    private static int pageReads;
    private static final Map<String, CorePageConfigDTO> DATA = new HashMap<>();

    private static void equal(Object expected, Object actual, String label) {
        checks++;
        if (!Objects.equals(expected, actual))
            throw new AssertionError(label+": expected "+expected+", got "+actual);
    }

    private interface Answer { Object call(Method method, Object[] args); }

    @SuppressWarnings("unchecked")
    private static <T> T service(Class<T> type, Answer answer) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                if (method.getName().equals("equals")) return proxy == args[0];
                return "finite template service dependency";
            }
            return answer.call(method, args);
        });
    }

    private static GenericWebApplicationContext context(Map<String, Object> properties) {
        GenericWebApplicationContext context = new GenericWebApplicationContext();
        Map<String, Object> values = new HashMap<>(properties);
        values.put("sunway.design.studio.enabled", "false");
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("template-contract", values));
        context.getBeanFactory().registerSingleton("pageConfigService", service(CorePageConfigService.class, (m, a) -> {
            if (!m.getName().equals("selectCurrentPageConfig")) throw new AssertionError(m);
            pageReads++;
            CorePageConfigDTO config = DATA.get(LocalContextHelper.getLoginUserId()+":"+a[0]);
            return config == null ? new CorePageConfigDTO() : config;
        }));
        context.getBeanFactory().registerSingleton("advancedSearchService", service(CoreStudioAdvSearchConfigService.class, (m, a) -> {
            if (!m.getName().equals("getCurrentUserConfigListByUrl")) throw new AssertionError(m);
            return Collections.emptyList();
        }));
        context.getBeanFactory().registerSingleton("adminPageConfigService", service(CoreAdminPageConfigService.class, (m, a) -> {
            if (!m.getName().equals("selectActivatedConfigLineList")) throw new AssertionError(m);
            return Collections.emptyList();
        }));
        CoreUserService users = service(CoreUserService.class, (m, a) -> {
            if (!m.getName().equals("selectById")) throw new AssertionError(m);
            CoreUserBean user = new CoreUserBean(); user.setId((String) a[0]); return user;
        });
        new LocalContextHelper().setCoreUserService(users);
        context.refresh();
        ApplicationContextHelper.setApplicationContext(context);
        return context;
    }

    private static GikamResourceTemplateResolver resolver(GenericWebApplicationContext context) {
        ApplicationConfiguration configuration = new ApplicationConfiguration();
        configuration.setApplicationContext(context);
        return (GikamResourceTemplateResolver) configuration.gikamTemplateResolver();
    }

    private static SpringTemplateEngine engine(GikamResourceTemplateResolver resolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    private static void store(String user, String uri, String grid, long width) {
        CorePageGridFieldConfigBean field = new CorePageGridFieldConfigBean();
        field.setUserId(user); field.setPageId(uri); field.setGridId(grid);
        field.setField("template-contract-field"); field.setWidth(width);
        field.setVisible("1"); field.setFixed("0");
        CorePageConfigDTO config = new CorePageConfigDTO(); config.addGrid(grid, field);
        DATA.put(user+":"+uri, config);
    }

    private static String render(SpringTemplateEngine engine, String template, String user, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setServletPath(uri);
        LocalContextHelper.setServletRequest(request);
        LoginUser principal = new LoginUser(user, "");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList()));
        Context data = new Context(Locale.CHINA); data.setVariable("label", "解析后的页面");
        return engine.process(template, data);
    }

    private static void release(GenericWebApplicationContext context) {
        LocalContextHelper.removeServletRequest();
        LocalContextHelper.removeUserLogin();
        SecurityContextHolder.clearContext();
        ApplicationContextHelper.setApplicationContext(null);
        context.close();
    }

    private static void dynamicRequests() {
        GenericWebApplicationContext context = context(Collections.emptyMap());
        try {
            GikamResourceTemplateResolver resolver = resolver(context);
            SpringTemplateEngine engine = engine(resolver);
            store("user-A", "/page-one", "grid-A", 111);
            String initial = render(engine, "template-contract", "user-A", "/page-one");
            equal(true, initial.contains("grid-A") && initial.contains("\"width\":111"),
                  "first render includes actual reader page configuration");
            int firstReads = pageReads;
            store("user-A", "/page-one", "grid-A", 222);
            String updated = render(engine, "template-contract", "user-A", "/page-one");
            // First failing behavior assertion reproduces DTO updated / HTML stale.
            equal(true, updated.contains("\"width\":222") && !updated.contains("\"width\":111"),
                  "same template rereads changed current user configuration");
            equal(firstReads+1, pageReads, "second render invokes real request-aware reader again");
            equal(false, resolver.isCacheable(), "original dynamic resolver always disables parsed-template caching");
            equal("utf-8", resolver.getCharacterEncoding(), "null encoding uses original fallback");
            equal("classpath:/templates/", resolver.getPrefix(), "null prefix uses original fallback");
            equal(".html", resolver.getSuffix(), "null suffix uses original fallback");
            equal(TemplateMode.HTML, resolver.getTemplateMode(), "original HTML template mode");
            equal(true, updated.contains("中文模板") && updated.contains("解析后的页面"), "UTF-8 text and real expression rendering");
            equal(false, updated.contains("th:text"), "Thymeleaf expression is evaluated");

            store("user-B", "/page-one", "grid-B", 333);
            String otherUser = render(engine, "template-contract", "user-B", "/page-one");
            equal(true, otherUser.contains("grid-B") && otherUser.contains("\"width\":333"), "same template follows new principal");
            equal(false, otherUser.contains("grid-A"), "first user's config is absent from other user's HTML");
            store("user-B", "/page-two", "grid-C", 444);
            String otherPage = render(engine, "template-contract", "user-B", "/page-two");
            equal(true, otherPage.contains("grid-C") && otherPage.contains("\"width\":444"), "same template follows current request URI");
            equal(false, otherPage.contains("grid-B"), "previous URI config is absent");
            DATA.remove("user-B:/page-two");
            String cleared = render(engine, "template-contract", "user-B", "/page-two");
            equal(false, cleared.contains("Gikam.setCompParam") || cleared.contains("grid-C"), "removed config is not retained in parsed template");
        } finally { release(context); DATA.clear(); }
    }

    private static void configuredResources(Path temporary) throws Exception {
        Path directory = temporary.resolve("configured-templates"); Files.createDirectories(directory);
        Files.write(directory.resolve("configured.tpl"),
                "<!DOCTYPE html><p th:text=\"${label}\">placeholder</p><b>café</b>".getBytes(Charset.forName("ISO-8859-1")));
        Map<String, Object> values = new HashMap<>();
        values.put("spring.thymeleaf.encoding", "ISO-8859-1");
        values.put("spring.thymeleaf.prefix", directory.toUri().toString());
        values.put("spring.thymeleaf.suffix", ".tpl");
        values.put("spring.thymeleaf.cache", "true");
        GenericWebApplicationContext context = context(values);
        try {
            GikamResourceTemplateResolver resolver = resolver(context);
            equal("ISO-8859-1", resolver.getCharacterEncoding(), "configured encoding preserved");
            equal(directory.toUri().toString(), resolver.getPrefix(), "configured prefix preserved");
            equal(".tpl", resolver.getSuffix(), "configured suffix preserved");
            equal(false, resolver.isCacheable(), "generic cache property cannot override original fixed false");
            String result = render(engine(resolver), "configured", "user-C", "/configured-page");
            equal(true, result.contains("café") && result.contains("解析后的页面"), "configured resource path/suffix/encoding render through real reader");
        } finally { release(context); }

        values.put("spring.thymeleaf.encoding", "");
        values.put("spring.thymeleaf.prefix", "");
        values.put("spring.thymeleaf.suffix", "");
        context = context(values);
        try {
            GikamResourceTemplateResolver resolver = resolver(context);
            equal("", resolver.getCharacterEncoding(), "empty encoding is not replaced by default");
            equal("", resolver.getPrefix(), "empty prefix remains an explicit value");
            equal("", resolver.getSuffix(), "empty suffix remains an explicit value");
            // Public setters accept empty strings. Failure occurs when the real
            // InputStreamReader tries an invalid charset; it must not silently UTF-8 fallback.
            resolver.setPrefix(directory.toUri().toString()); resolver.setSuffix(".tpl");
            boolean failed = false;
            try { render(engine(resolver), "configured", "user-C", "/configured-page"); }
            catch (org.thymeleaf.exceptions.TemplateInputException expected) { failed = true; }
            equal(true, failed, "invalid explicit encoding fails during actual resource read");
        } finally { release(context); }
    }

    public static void main(String[] args) throws Exception {
        dynamicRequests();
        configuredResources(Paths.get(args[0]));
        System.out.println("Actual template configuration/Thymeleaf checks: PASS "+checks);
    }
}
