package com.sunwayworld.owned.internal.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Resolves the original project controller view names without bringing the
 * supplier's Gikam runtime into the distribution. The project templates remain
 * the server-side page contract, while the imported supplier bundle stays the
 * browser runtime. This resolver performs only parameter substitutions and
 * never injects the simplified owned workspace shell.
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OwnedTemplateViewConfiguration {
    @Bean(name = {"ownedTemplateViewResolver", "thymeleafViewResolver"})
    public ViewResolver ownedTemplateViewResolver() {
        return new OwnedTemplateViewResolver();
    }

    static final class OwnedTemplateViewResolver implements ViewResolver, Ordered {
        @Override
        public int getOrder() {
            // Resolve source-owned project templates before Thymeleaf's
            // default resolver.  Otherwise the unresolved supplier
            // module/gikam fragment is parsed as a Thymeleaf fragment and
            // workspace routes fail with HTTP 500.
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public View resolveViewName(String viewName, Locale locale) {
            if (viewName == null || viewName.startsWith("redirect:") || viewName.startsWith("forward:")) {
                return null;
            }
            String normalized = viewName.startsWith("/") ? viewName.substring(1) : viewName;
            Resource template = new ClassPathResource("templates/" + normalized + ".html");
            return template.exists() ? new OwnedTemplateView(template, normalized) : null;
        }
    }

    static final class OwnedTemplateView implements View {
        private final Resource template;
        private final String viewName;

        OwnedTemplateView(Resource template, String viewName) {
            this.template = template;
            this.viewName = viewName;
        }

        @Override
        public String getContentType() {
            return "text/html;charset=UTF-8";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request,
                HttpServletResponse response) throws Exception {
            response.setContentType(getContentType());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(renderTemplate(read(template), request));
        }

        private static String read(Resource resource) throws IOException {
            try (InputStream input = resource.getInputStream()) {
                byte[] buffer = new byte[8192];
                StringBuilder text = new StringBuilder();
                int count;
                while ((count = input.read(buffer)) >= 0) {
                    text.append(new String(buffer, 0, count, "UTF-8"));
                }
                return text.toString();
            }
        }

        static String renderTemplate(String source, HttpServletRequest request) {
            return renderTemplate(source, request, 0);
        }

        private static String renderTemplate(String source, HttpServletRequest request, int depth) {
            String result = source;
            if (depth < 8) {
                Matcher fragments = FRAGMENT.matcher(result);
                StringBuffer expanded = new StringBuffer();
                while (fragments.find()) {
                    String name = fragments.group(1);
                    String replacement = "";
                    if (!name.startsWith("module/gikam/")) {
                        Resource nested = new ClassPathResource("templates/" + name + ".html");
                        if (nested.exists()) {
                            try {
                                replacement = renderTemplate(read(nested), request, depth + 1);
                            } catch (IOException failure) {
                                replacement = fragments.group(0);
                            }
                        } else {
                            replacement = fragments.group(0);
                        }
                    }
                    fragments.appendReplacement(expanded, Matcher.quoteReplacement(replacement));
                }
                fragments.appendTail(expanded);
                result = expanded.toString();
            }
            result = result.replaceAll("th:(src|href|action)=\"@\\{([^}]+)\\}\"", "$1=\"$2\"");
            result = result.replaceAll("\\s+th:inline=\\\"javascript\\\"", "");
            result = result.replace("[[${param}]]", pageParameters(request));
            result = result.replaceAll("\\[\\[\\$\\{[^}]+}\\]\\]", "null");
            return result;
        }

        private static final Pattern FRAGMENT = Pattern.compile(
                "<th:block\\s+th:replace=\"([^\"]+)\"\\s*(?:/>|></th:block>)",
                Pattern.CASE_INSENSITIVE);

        private static String pageParameters(HttpServletRequest request) {
            Map<String, String[]> source = request.getParameterMap();
            Map<String, Object> values = new LinkedHashMap<String, Object>();
            for (Map.Entry<String, String[]> entry : source.entrySet()) {
                String[] value = entry.getValue();
                if (value == null || value.length == 0) values.put(entry.getKey(), null);
                else if (value.length == 1) values.put(entry.getKey(), value[0]);
                else values.put(entry.getKey(), value);
            }
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (!first) json.append(',');
                first = false;
                json.append(quote(entry.getKey())).append(':');
                Object value = entry.getValue();
                if (value instanceof String[]) {
                    json.append('[');
                    String[] array = (String[]) value;
                    for (int i = 0; i < array.length; i++) {
                        if (i > 0) json.append(',');
                        json.append(quote(array[i]));
                    }
                    json.append(']');
                } else if (value == null) json.append("null");
                else json.append(quote(String.valueOf(value)));
            }
            return json.append('}').toString();
        }

        private static String quote(String value) {
            if (value == null) return "null";
            StringBuilder quoted = new StringBuilder("\"");
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '\\' || c == '\"') quoted.append('\\').append(c);
                else if (c == '\n') quoted.append("\\n");
                else if (c == '\r') quoted.append("\\r");
                else if (c == '<') quoted.append("\\u003c");
                else if (c == '>') quoted.append("\\u003e");
                else if (c == '&') quoted.append("\\u0026");
                else quoted.append(c);
            }
            return quoted.append('"').toString();
        }
    }
}
