import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unchecked")
public final class RedisHelperLoopbackTest {
    private RedisHelperLoopbackTest() {}

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        LettuceConnectionFactory factory = new LettuceConnectionFactory("127.0.0.1", port);
        factory.afterPropertiesSet();
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        StringRedisSerializer strings = new StringRedisSerializer();
        template.setConnectionFactory(factory);
        template.setKeySerializer(strings);
        template.setHashKeySerializer(strings);
        template.setValueSerializer(strings);
        template.setHashValueSerializer(strings);
        template.afterPropertiesSet();
        RedisHelper helper = new RedisHelper();
        helper.setRedisTemplate(template);
        try {
            assertEquals("alpha::one", RedisHelper.getRedisKey("alpha", "one"), "key format");

            RedisHelper.put("alpha", "one", "first");
            assertTrue(RedisHelper.exists("alpha", "one"), "put exists");
            assertEquals("first", RedisHelper.<String>get("alpha", "one"), "put/get");
            assertTrue(!RedisHelper.putIfAbsent("alpha", "one", "second", 30), "putIfAbsent preserves existing");
            assertEquals("first", RedisHelper.<String>get("alpha", "one"), "putIfAbsent value");

            RedisHelper.put("alpha", "ttl", "value", 30);
            assertTrue(template.getExpire("alpha::ttl", TimeUnit.SECONDS) > 0, "put ttl");
            RedisHelper.expire("alpha", "ttl", 30);
            assertTrue(template.getExpire("alpha::ttl", TimeUnit.SECONDS) > 0, "expire");

            RedisHelper.pushList("alpha", "list", "a");
            RedisHelper.pushListAll("alpha", "list", Arrays.asList("b", "c"));
            assertEquals(Arrays.asList("a", "b", "c"), RedisHelper.<String>getList("alpha", "list"), "list order");
            assertEquals("a", RedisHelper.<String>getListFirstValue("alpha", "list"), "list first");
            assertEquals(3, RedisHelper.getListSize("alpha", "list"), "list size");
            assertEquals(Arrays.asList("a", "b"), RedisHelper.<String>getList("alpha", "list", 2), "list bounded");
            RedisHelper.removeListFirstValue("alpha", "list");
            assertEquals(Arrays.asList("b", "c"), RedisHelper.<String>getList("alpha", "list"), "left pop");
            RedisHelper.pushList("alpha", "list", "d", 30);
            assertTrue(template.getExpire("alpha::list", TimeUnit.SECONDS) > 0, "list ttl");
            RedisHelper.removeList("alpha", "list", 1);
            assertEquals(Arrays.asList("c", "d"), RedisHelper.<String>getList("alpha", "list"), "list trim");

            RedisHelper.put("alpha", "evict-one", "x");
            RedisHelper.put("alpha", "evict-two", "y");
            RedisHelper.put("beta", "keep", "z");
            RedisHelper.evict("alpha");
            assertTrue(!RedisHelper.exists("alpha", "evict-one"), "pattern evict one");
            assertTrue(!RedisHelper.exists("alpha", "evict-two"), "pattern evict two");
            assertTrue(RedisHelper.exists("beta", "keep"), "pattern evict scope");
            RedisHelper.evict("beta", "keep");
            assertTrue(!RedisHelper.exists("beta", "keep"), "key evict");

            RedisHelper.put("multi", "before", "value");
            RedisHelper.multi(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    operations.opsForValue().set((K) "multi::after", (V) "value");
                    return null;
                }
            });
            assertEquals("value", RedisHelper.<String>get("multi", "after"), "multi");
        } finally {
            template.delete("multi::before");
            template.delete("multi::after");
            template.delete("alpha::ttl");
            template.delete("alpha::list");
            template.delete("alpha::one");
            template.delete("beta::keep");
            factory.destroy();
        }
        helper.setRedisTemplate(null);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("spring.redis.url", "redis://127.0.0.1:" + port);
        SpringApplication application = new SpringApplication(RedisBootConfiguration.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setDefaultProperties(properties);
        ConfigurableApplicationContext context = application.run();
        try {
            OwnedApplicationContext.register(context);
            RedisTemplate<String, Object> bootTemplate = RedisHelper.getRedisTemplate();
            bootTemplate.opsForValue().set("boot::lookup", "ok");
            assertEquals("ok", RedisHelper.<String>get("boot", "lookup"), "boot redis auto-configuration");
        } finally {
            OwnedApplicationContext.clear(context);
            context.close();
        }
        System.out.println("RedisHelper loopback contract: PASS");
    }

    @Configuration
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    public static class RedisBootConfiguration {}

    private static void assertTrue(boolean value, String label) {
        if (!value) throw new AssertionError(label);
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
        }
    }
}
