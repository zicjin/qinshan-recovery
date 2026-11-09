import com.sunwayworld.boot.ApplicationConfiguration;
import com.sunwayworld.boot.ApplicationMvcConfiguration;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import java.util.*;
import javax.servlet.http.*;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.mock.web.*;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

/** Uses real configuration factories, registration, mapping and resolver semantics. */
public class ApplicationLocaleInterceptorCheck {
    private static int checks;
    private static final String CONFIGS = "/open/core/module/sys/i18ns/configs";

    private static void equal(Object expected, Object actual, String label) {
        checks++;
        if (!Objects.equals(expected, actual))
            throw new AssertionError(label+": expected "+expected+", got "+actual);
    }

    private static class Registry extends InterceptorRegistry {
        List<Object> entries() { return super.getInterceptors(); }
    }

    private static class Mapping extends AbstractHandlerMapping {
        private final HttpRequestHandler endpoint = (request, response) -> {
            LocaleResolver resolver = (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(resolver.resolveLocale(request).toLanguageTag());
        };
        @Override protected Object getHandlerInternal(HttpServletRequest request) { return endpoint; }
    }

    private static MockHttpServletRequest request(MockServletContext servletContext, String path,
                                                  String parameter, String value, Cookie... cookies) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", path);
        request.setServletPath(path);
        request.setPreferredLocales(Collections.singletonList(Locale.FRANCE));
        if (parameter != null) request.addParameter(parameter, value);
        if (cookies.length > 0) request.setCookies(cookies);
        return request;
    }

    private static MockHttpServletResponse dispatch(DispatcherServlet servlet, MockHttpServletRequest request)
            throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        equal(200, response.getStatus(), "DispatcherServlet status "+request.getRequestURI());
        return response;
    }

    private static Cookie localeCookie(MockHttpServletResponse response) {
        return response.getCookie("locale");
    }

    public static void main(String[] args) throws Exception {
        equal(null, ApplicationContextHelper.getApplicationContext(),
              "fresh JVM has no global application helper context");
        ApplicationConfiguration application = new ApplicationConfiguration();
        LocaleChangeInterceptor localeBean = application.localeChangeInterceptor();
        LocaleResolver resolverBean = application.localeResolver();
        MockServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
        DispatcherServlet servlet = null;
        try {
            context.getBeanFactory().registerSingleton("localeChangeInterceptor", localeBean);
            context.getBeanFactory().registerSingleton("localeResolver", resolverBean);
            context.refresh();
            // Spring gives this MVC configuration its owner context. The locale
            // registration must not depend on ApplicationConfiguration's later
            // PostConstruct initializing an unrelated global helper.
            ApplicationMvcConfiguration mvc = new ApplicationMvcConfiguration(new WebMvcProperties());
            mvc.setApplicationContext(context);
            Registry registry = new Registry();
            mvc.addInterceptors(registry);
            equal(null, ApplicationContextHelper.getApplicationContext(),
                  "MVC registration works before global helper initialization");
            Mapping mapping = new Mapping();
            mapping.setInterceptors(registry.entries().toArray());
            context.getBeanFactory().registerSingleton("handlerMapping", mapping);
            context.getBeanFactory().registerSingleton("handlerAdapter", new HttpRequestHandlerAdapter());
            mapping.setApplicationContext(context);
            servlet = new DispatcherServlet(context);
            servlet.init(new MockServletConfig(servletContext));

            MockHttpServletResponse changed = dispatch(servlet, request(servletContext, CONFIGS, "lang", "en-US"));
            // The first assertion reaches the reported failure, not merely a bean name.
            Cookie cookie = localeCookie(changed);
            equal(true, cookie != null, "lang request writes locale cookie");
            equal("en-US", cookie.getValue(), "lang cookie value");
            equal("en-US", changed.getContentAsString(), "handler sees changed locale");
            equal("lang", localeBean.getParamName(), "original bean uses lang parameter");

            int localeRegistrations = 0;
            for (Object registered : registry.entries()) {
                Object interceptor = registered instanceof MappedInterceptor
                    ? ((MappedInterceptor) registered).getInterceptor() : registered;
                if (interceptor instanceof LocaleChangeInterceptor) {
                    localeRegistrations++;
                    equal(true, interceptor == context.getBean("localeChangeInterceptor"),
                          "MVC uses same context LocaleChangeInterceptor bean");
                }
            }
            equal(1, localeRegistrations, "single locale interceptor registration");

            MockHttpServletResponse remembered = dispatch(servlet, request(servletContext, CONFIGS, null, null, cookie));
            equal("en-US", remembered.getContentAsString(), "subsequent request resolves persisted cookie");
            equal(null, localeCookie(remembered), "read does not rewrite locale cookie");

            MockHttpServletResponse alias = dispatch(servlet, request(servletContext, CONFIGS, "locale", "en-US"));
            equal(null, localeCookie(alias), "locale parameter is not an alternate mutation trigger");
            equal("zh-CN", alias.getContentAsString(), "normal resolver default remains China");

            MockHttpServletResponse defaults = dispatch(servlet, request(servletContext, CONFIGS, null, null));
            equal("zh-CN", defaults.getContentAsString(), "resolver default is independent of Accept-Language");
            equal(null, localeCookie(defaults), "default read has no cookie mutation");

            MockHttpServletResponse staticRequest = dispatch(servlet,
                request(servletContext, "/static/gikam/main/js/home.js", "lang", "en-US"));
            equal(null, localeCookie(staticRequest), "static request excludes locale change interception");
            equal("zh-CN", staticRequest.getContentAsString(), "static lang does not change resolver locale");
            MockHttpServletResponse staticCookie = dispatch(servlet,
                request(servletContext, "/static/module/example.js", "lang", "zh-CN", cookie));
            equal(null, localeCookie(staticCookie), "static request cannot overwrite an existing cookie");
            equal("en-US", staticCookie.getContentAsString(), "static exclusion still reads the normal cookie");

            MockHttpServletResponse switchBack = dispatch(servlet,
                request(servletContext, CONFIGS, "lang", "zh-CN", cookie));
            equal("zh-CN", localeCookie(switchBack).getValue(), "normal localeResolver persists later lang change");
            equal("zh-CN", switchBack.getContentAsString(), "same resolver sees later change");
            equal(null, ApplicationContextHelper.getApplicationContext(),
                  "locale requests never initialize or require global helper context");
            System.out.println("Actual application locale/DispatcherServlet checks: PASS "+checks);
        } finally {
            if (servlet != null) servlet.destroy();
            context.close();
        }
    }
}
