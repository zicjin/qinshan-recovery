package com.sunwayworld.framework.context;

import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import com.sunwayworld.owned.internal.context.OwnedSequenceProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * Owned context and identity facade used by recovered project code.
 *
 * <p>Supplier license, MAC-binding, and online-user-limit methods are absent
 * by design. The remaining context, configuration, and identity operations
 * are implemented locally and never load a native library.</p>
 */
public class ApplicationContextHelper {
    private static final AtomicLong IDENTITY = new AtomicLong();
    private static final Environment FALLBACK_ENVIRONMENT = new StandardEnvironment();

    public ApplicationContextHelper() {}

    public static String getAppId() {
        return getEnvironment().getProperty("spring.application.name", "lims-owned");
    }

    public static ApplicationContext getApplicationContext() {
        return OwnedApplicationContext.getApplicationContext();
    }

    public static Environment getEnvironment() {
        ApplicationContext context = getApplicationContext();
        return context == null ? FALLBACK_ENVIRONMENT : context.getEnvironment();
    }

    public static <T> T getBean(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("type must not be null");
        ApplicationContext context = getApplicationContext();
        return context == null ? null : context.getBean(type);
    }

    public static <T> T getBeanIfPresent(Class<T> type) {
        if (type == null) return null;
        ApplicationContext context = getApplicationContext();
        return context == null || context.getBeansOfType(type).isEmpty() ? null : context.getBean(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        if (name == null) throw new IllegalArgumentException("name must not be null");
        ApplicationContext context = getApplicationContext();
        return context == null ? null : (T) context.getBean(name);
    }

    public static <T> T getBeanIfPresent(String name) {
        if (name == null) return null;
        ApplicationContext context = getApplicationContext();
        return context == null || !context.containsBean(name) ? null : getBean(name);
    }

    public static boolean isProfileActivated(String profile) {
        if (profile == null || profile.trim().isEmpty()) return false;
        for (String active : getEnvironment().getActiveProfiles()) if (profile.equals(active)) return true;
        return false;
    }

    public static String getConstantValue(String key) {
        return getEnvironment().getProperty(key);
    }

    public static String getConstantValue(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    public static Long getNextIdentity() {
        OwnedSequenceProvider provider = OwnedApplicationContext.getBean(OwnedSequenceProvider.class);
        if (provider != null) {
            try {
                return provider.nextIdentity();
            } catch (java.sql.SQLException failure) {
                throw new IllegalStateException("owned identity sequence unavailable", failure);
            }
        }
        return IDENTITY.incrementAndGet();
    }

    public static List<Long> getNextIdentityList(int count) {
        requireCount(count);
        List<Long> values = new ArrayList<Long>(count);
        for (int i = 0; i < count; i++) values.add(getNextIdentity());
        return values;
    }

    public static List<Long> getNextOrderNoList(int count) {
        return getNextIdentityList(count);
    }

    public static Long getNextOrderNo() {
        return getNextIdentity();
    }

    public static Long getNextSequenceNumber(String sequenceId) {
        return getNextIdentity();
    }

    public static List<Long> getNextSequenceNumberList(String sequenceId, int count) {
        return getNextIdentityList(count);
    }

    public static String getNextSequence(String sequenceId) {
        return String.valueOf(getNextIdentity());
    }

    public static String getNextSequence(String sequenceId, String key) {
        return getNextSequence(sequenceId);
    }

    public static List<String> getNextSequenceList(String sequenceId, int count) {
        requireCount(count);
        List<String> values = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) values.add(getNextSequence(sequenceId));
        return values;
    }

    public static List<String> getNextSequenceList(String sequenceId, String key, int count) {
        return getNextSequenceList(sequenceId, count);
    }

    public static String getServerAddress() {
        return getEnvironment().getProperty("server.address", "127.0.0.1");
    }

    public static boolean stressTesting() {
        return getEnvironment().getProperty("sunway.stress-testing", Boolean.class, Boolean.FALSE);
    }

    public static boolean loggingCountOnlyEnabled() {
        return getEnvironment().getProperty("sunway.logging.count-only", Boolean.class, Boolean.FALSE);
    }

    private static void requireCount(int count) {
        if (count < 0) throw new IllegalArgumentException("count must not be negative");
    }
}
