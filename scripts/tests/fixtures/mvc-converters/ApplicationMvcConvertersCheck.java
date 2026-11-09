import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.sunwayworld.boot.ApplicationMvcConfiguration;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Exercises configuration used by the application, including actual wire bodies. */
public class ApplicationMvcConvertersCheck {
    private static final String XML = "<?xml version=\"1.0\"?><mapper>中文</mapper>";
    private static final String[] FAST_TYPES = {
        "application/json", "application/xml", "text/html", "text/plain", "text/xml", "text/javascript"
    };
    private static int checks;

    @RestController
    public static class ReadResource {
        @GetMapping("/string") public String text() { return XML; }
        @GetMapping("/list") public List<Map<String, String>> list() {
            return Collections.singletonList(Collections.singletonMap("name", "中文"));
        }
        @PostMapping("/echo") public Map<String, String> echo(@RequestBody Map<String, String> value) {
            return value;
        }
    }

    private static void equal(Object expected, Object actual, String label) {
        checks++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }

    private static List<MediaType> media(String... names) {
        List<MediaType> result = new ArrayList<>();
        for (String name : names) {
            MediaType parsed = MediaType.parseMediaType(name);
            result.add(new MediaType(parsed.getType(), parsed.getSubtype(), StandardCharsets.UTF_8));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        new ApplicationMvcConfiguration(new WebMvcProperties()).configureMessageConverters(converters);
        equal(StringHttpMessageConverter.class, converters.get(0).getClass(), "String converter first");
        equal(media("text/plain", "application/json"), converters.get(0).getSupportedMediaTypes(),
              "String types must not accept arbitrary binary or markup types");
        equal(true, converters.get(1) instanceof FastJsonHttpMessageConverter, "FastJson second");
        equal(media(FAST_TYPES), converters.get(1).getSupportedMediaTypes(), "Original ordered FastJson media types");
        equal(2, converters.size(), "Original explicit converter list; no accidental fallback converters");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ReadResource())
            .setMessageConverters(converters.toArray(new HttpMessageConverter<?>[0])).build();
        for (String accept : FAST_TYPES) {
            MvcResult text = mvc.perform(get("/string").accept(accept)).andReturn();
            equal(200, text.getResponse().getStatus(), "String HTTP " + accept);
            MediaType content = MediaType.parseMediaType(text.getResponse().getContentType());
            equal(accept, content.getType()+"/"+content.getSubtype(), "String negotiated type " + accept);
            equal(StandardCharsets.UTF_8, content.getCharset(), "String charset " + accept);
            boolean raw = accept.equals("text/plain") || accept.equals("application/json");
            equal(raw ? XML : JSON.toJSONString(XML, com.alibaba.fastjson.serializer.SerializerFeature.BrowserSecure), text.getResponse().getContentAsString(),
                  "Original raw String versus FastJson String serialization " + accept);
            if (!raw) equal(XML, JSON.parse(text.getResponse().getContentAsString()), "Encoded String retains content " + accept);
            MvcResult list = mvc.perform(get("/list").accept(accept)).andReturn();
            equal(200, list.getResponse().getStatus(), "List HTTP " + accept);
            content = MediaType.parseMediaType(list.getResponse().getContentType());
            equal(accept, content.getType()+"/"+content.getSubtype(), "List negotiated type " + accept);
            equal(StandardCharsets.UTF_8, content.getCharset(), "List charset " + accept);
            equal("中文", JSON.parseArray(list.getResponse().getContentAsString())
                  .getJSONObject(0).getString("name"), "Real JSON UTF-8 body " + accept);
        }
        for (String path : new String[]{"/string", "/list"}) {
            MvcResult result = mvc.perform(get(path).accept("image/png")).andReturn();
            equal(406, result.getResponse().getStatus(), "Unsupported image representation " + path);
            equal(0, result.getResponse().getContentAsByteArray().length, "No mislabeled body " + path);
        }
        MvcResult wildcard = mvc.perform(get("/string").accept("*/*")).andReturn();
        equal("text/plain;charset=UTF-8", wildcard.getResponse().getContentType(), "Wildcard String default");
        equal(XML, wildcard.getResponse().getContentAsString(), "Wildcard String raw body");
        MvcResult echo = mvc.perform(post("/echo").accept("application/json")
            .contentType("application/json;charset=UTF-8").content("{\"name\":\"中文\"}".getBytes(StandardCharsets.UTF_8))).andReturn();
        equal(200, echo.getResponse().getStatus(), "JSON request remains readable");
        equal("中文", JSON.parseObject(echo.getResponse().getContentAsString()).getString("name"), "JSON request body preserved");
        System.out.println("Actual MVC converter/DispatcherServlet checks: PASS " + checks);
    }
}
