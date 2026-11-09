import com.sunwayworld.boot.ApplicationConfiguration;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.tenant.TenantContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanCursor;
import org.springframework.data.redis.core.ScanIteration;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

/** Real Spring caching and application beans, with only the Redis wire replaced. */
public class ApplicationRedisConfigCheck {
    private static final String CACHE = "core-user-panel-config-layout";
    private static final String USER = "dedicated-cache-test-user";
    private static int checks;

    private static void equal(Object expected, Object actual, String label) {
        checks++;
        if (!Objects.equals(expected, actual))
            throw new AssertionError(label+": expected "+expected+", got "+actual);
    }

    private static void bytes(byte[] expected, byte[] actual, String label) {
        checks++;
        if (!Arrays.equals(expected, actual))
            throw new AssertionError(label+": expected "+Arrays.toString(expected)
                                     +", got "+Arrays.toString(actual));
    }

    private static byte[] utf8(String text) { return text.getBytes(StandardCharsets.UTF_8); }

    private static final class Key {
        final byte[] value;
        Key(byte[] value) { this.value = value.clone(); }
        @Override public boolean equals(Object other) {
            return other instanceof Key && Arrays.equals(value, ((Key) other).value);
        }
        @Override public int hashCode() { return Arrays.hashCode(value); }
    }

    /** Understands Redis commands only; it never translates application keys or values. */
    private static final class Wire implements InvocationHandler {
        final Map<Key, byte[]> values = new LinkedHashMap<>();
        final Map<Key, Map<Key, byte[]>> hashes = new LinkedHashMap<>();
        final List<byte[]> writes = new ArrayList<>();
        final List<byte[]> deletes = new ArrayList<>();
        final List<String> scans = new ArrayList<>();
        byte[] lastHashKey;
        byte[] lastHashField;
        byte[] lastHashValue;
        final RedisConnection connection = (RedisConnection) Proxy.newProxyInstance(
                RedisConnection.class.getClassLoader(), new Class<?>[]{RedisConnection.class}, this);
        final RedisConnectionFactory factory = (RedisConnectionFactory) Proxy.newProxyInstance(
                RedisConnectionFactory.class.getClassLoader(), new Class<?>[]{RedisConnectionFactory.class},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) return objectMethod(proxy, method, args);
                    if (method.getName().equals("getConnection")) return connection;
                    if (method.getName().equals("getConvertPipelineAndTxResults")) return true;
                    if (method.getName().equals("translateExceptionIfPossible")) return null;
                    throw new AssertionError("Unexpected Redis factory call: "+method);
                });

        private Object objectMethod(Object proxy, Method method, Object[] args) {
            if (method.getName().equals("equals")) return proxy == args[0];
            if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
            return "in-memory Redis wire boundary";
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args) {
            if (method.getDeclaringClass() == Object.class) return objectMethod(proxy, method, args);
            String name = method.getName();
            if (name.endsWith("Commands") || name.equals("getNativeConnection")) return connection;
            if (name.equals("close")) return null;
            if (name.equals("isClosed") || name.equals("isQueueing") || name.equals("isPipelined")) return false;
            if (name.equals("get")) return values.get(new Key((byte[]) args[0]));
            if (name.equals("set")) {
                byte[] key = (byte[]) args[0];
                values.put(new Key(key), ((byte[]) args[1]).clone());
                writes.add(key.clone());
                return true;
            }
            if (name.equals("exists")) {
                if (args[0] instanceof byte[]) return values.containsKey(new Key((byte[]) args[0]));
                long count = 0;
                for (byte[] key : (byte[][]) args[0]) if (values.containsKey(new Key(key))) count++;
                return count;
            }
            if (name.equals("del")) {
                long count = 0;
                for (byte[] key : (byte[][]) args[0]) {
                    deletes.add(key.clone());
                    if (values.remove(new Key(key)) != null) count++;
                }
                return count;
            }
            if (name.equals("hSet")) {
                lastHashKey = ((byte[]) args[0]).clone();
                lastHashField = ((byte[]) args[1]).clone();
                lastHashValue = ((byte[]) args[2]).clone();
                hashes.computeIfAbsent(new Key(lastHashKey), ignored -> new LinkedHashMap<>())
                      .put(new Key(lastHashField), lastHashValue);
                return true;
            }
            if (name.equals("hGet")) {
                Map<Key, byte[]> hash = hashes.get(new Key((byte[]) args[0]));
                return hash == null ? null : hash.get(new Key((byte[]) args[1]));
            }
            if (name.equals("scan")) {
                ScanOptions options = (ScanOptions) args[0];
                String pattern = options.getPattern();
                scans.add(pattern);
                if (!pattern.endsWith("*") || pattern.indexOf('*') != pattern.length()-1)
                    throw new AssertionError("Unexpected fixture SCAN pattern: "+pattern);
                String prefix = pattern.substring(0, pattern.length()-1);
                List<byte[]> matches = new ArrayList<>();
                for (Key key : values.keySet())
                    if (new String(key.value, StandardCharsets.UTF_8).startsWith(prefix)) matches.add(key.value.clone());
                return new ScanCursor<byte[]>(options) {
                    @Override protected ScanIteration<byte[]> doScan(long cursorId, ScanOptions scanOptions) {
                        return new ScanIteration<>(0, matches);
                    }
                }.open();
            }
            throw new AssertionError("Unexpected Redis connection call: "+method);
        }
    }

    public static class PanelLoader {
        private int loads;
        @Cacheable(cacheNames = CACHE, key = "#user")
        public String load(String user) { return "layout-"+(++loads); }
        @Cacheable(cacheNames = CACHE, key = "'null-layout'")
        public String absent() { return null; }
        public int loads() { return loads; }
    }

    @Configuration
    @EnableCaching(proxyTargetClass = true)
    public static class Caching {
        @Bean public PanelLoader panelLoader() { return new PanelLoader(); }
    }

    private static AnnotationConfigApplicationContext context(String namespace, boolean tenant) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
                "redis-contract", Collections.<String, Object>singletonMap("spring.redis.namespace", namespace)));
        if (tenant) context.getEnvironment().setActiveProfiles("tenant");
        ApplicationContextHelper.setApplicationContext(context);
        return context;
    }

    private static void familyEviction() throws Exception {
        Wire wire = new Wire();
        try (AnnotationConfigApplicationContext context = context("", false)) {
            ApplicationConfiguration configuration = new ApplicationConfiguration();
            configuration.setApplicationContext(context);
            RedisTemplate<String, Object> template = configuration.redisTemplate(wire.factory);
            new RedisHelper().setRedisTemplate(template);
            CacheManager manager = configuration.cacheManager(wire.factory);
            ((InitializingBean) manager).afterPropertiesSet();
            context.getBeanFactory().registerSingleton("cacheManager", manager);
            context.register(Caching.class);
            context.refresh();
            PanelLoader loader = context.getBean(PanelLoader.class);
            equal("layout-1", loader.load(USER), "first layout loads actual method");
            equal("layout-1", loader.load(USER), "second layout uses @Cacheable value");
            equal(1, loader.loads(), "warm read is genuinely cached");
            byte[] cacheKey = wire.writes.get(0).clone();
            bytes(utf8(CACHE+"::"+USER), cacheKey, "cache writer uses original logical key");
            byte[] unrelated = utf8("other-cache::keep");
            wire.values.put(new Key(unrelated), new byte[]{7});

            RedisHelper.evict(CACHE);
            // First failure must be the stale-layout behavior, not serializer introspection.
            equal("layout-2", loader.load(USER), "family eviction must make the same layout reload");
            equal(2, loader.loads(), "method runs again after family eviction");
            equal(Collections.singletonList(CACHE+"::*"), wire.scans, "family eviction scans intended family");
            equal(1, wire.deletes.size(), "family eviction deletes only one matching key");
            bytes(cacheKey, wire.deletes.get(0), "SCAN result deletes exact bytes written by cache writer");
            bytes(new byte[]{7}, wire.values.get(new Key(unrelated)), "unrelated cache is retained");

            Map<String, String> payload = Collections.singletonMap("title", "real-layout-value");
            template.opsForValue().set("plain-cache::item", payload);
            bytes(utf8("plain-cache::item"), wire.writes.get(wire.writes.size()-1), "template key is logical UTF-8");
            bytes(new JdkSerializationRedisSerializer().serialize(payload),
                  wire.values.get(new Key(utf8("plain-cache::item"))), "template value encoding remains JDK");
            equal(payload, template.opsForValue().get("plain-cache::item"), "template values round trip");
            template.opsForHash().put("hash-cache::item", "field", payload);
            bytes(utf8("hash-cache::item"), wire.lastHashKey, "hash container key matches template key encoding");
            bytes(utf8("field"), wire.lastHashField, "hash field key uses logical UTF-8");
            bytes(new JdkSerializationRedisSerializer().serialize(payload), wire.lastHashValue,
                  "hash value encoding remains JDK");
            equal(payload, template.opsForHash().get("hash-cache::item", "field"), "hash value round trips");
            bytes(new JdkSerializationRedisSerializer().serialize("layout-2"),
                  wire.values.get(new Key(cacheKey)), "cache value encoding remains JDK");

            int writesBeforeNull = wire.writes.size();
            boolean rejected = false;
            try { loader.absent(); } catch (IllegalArgumentException expected) { rejected = true; }
            equal(true, rejected, "cache null value is rejected");
            equal(writesBeforeNull, wire.writes.size(), "rejected cache null writes nothing");
        } finally {
            ApplicationContextHelper.setApplicationContext(null);
            TenantContext.removeTenant();
        }
    }

    private static void namespacedKeyCompatibility() throws Exception {
        Wire wire = new Wire();
        try (AnnotationConfigApplicationContext context = context("test-namespace:", true)) {
            TenantContext.setTenant("fixture-tenant");
            context.refresh();
            ApplicationConfiguration configuration = new ApplicationConfiguration();
            configuration.setApplicationContext(context);
            RedisTemplate<String, Object> template = configuration.redisTemplate(wire.factory);
            CacheManager manager = configuration.cacheManager(wire.factory);
            ((InitializingBean) manager).afterPropertiesSet();
            String logical = "namespace-cache::item";
            byte[] expected = utf8("test-namespace:"+logical+"#@#fixture-tenant");
            template.opsForValue().set(logical, "value");
            bytes(expected, wire.writes.get(0), "template honors namespace and tenant suffix");
            manager.getCache("namespace-cache").put("item", "value");
            bytes(expected, wire.writes.get(1), "cache writer shares namespace and tenant key contract");
            equal("value", template.opsForValue().get(logical), "template reads cache writer value with same key");
            template.opsForHash().put("hash", "field", "value");
            bytes(utf8("test-namespace:hash#@#fixture-tenant"), wire.lastHashKey,
                  "hash container honors namespace and tenant");
            bytes(utf8("test-namespace:field#@#fixture-tenant"), wire.lastHashField,
                  "hash field honors configured key serializer");
        } finally {
            ApplicationContextHelper.setApplicationContext(null);
            TenantContext.removeTenant();
        }
    }

    public static void main(String[] args) throws Exception {
        familyEviction();
        namespacedKeyCompatibility();
        System.out.println("PASS "+checks+" actual application Redis configuration checks");
    }
}
