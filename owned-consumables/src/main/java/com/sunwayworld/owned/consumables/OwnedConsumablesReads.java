package com.sunwayworld.owned.consumables;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.owned.internal.data.ConsumableQueryService;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedReadRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** One query service using the exact identity singleton that issues HTTP sessions. */
public final class OwnedConsumablesReads {
    private final ConsumableQueryService query;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;

    public OwnedConsumablesReads(DataSource source, JdbcPersonnelIdentity identity,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary) {
        this.query = new ConsumableQueryService(source, identity);
        this.provider = provider;
        this.boundary = boundary;
    }

    <T extends AbstractPersistable<?>> Page<T> list(RestJsonWrapperBean wrapper, Class<T> type) {
        try { return page(query.list(OwnedReadRequest.parse(wrapper), principal()), type); }
        catch (SQLException failure) { throw failed(failure); }
    }
    <T extends AbstractPersistable<?>> T detail(String id, Class<T> type) {
        try { return bean(query.detail(id, principal()), type); }
        catch (SQLException failure) { throw failed(failure); }
    }
    <T extends AbstractPersistable<?>> Page<T> stock(String id, RestJsonWrapperBean wrapper, Class<T> type) {
        try { return page(query.stock(id, OwnedReadRequest.parse(wrapper), principal()), type); }
        catch (SQLException failure) { throw failed(failure); }
    }
    <T extends AbstractPersistable<?>> T stockDetail(Long id, Class<T> type) {
        try { return bean(query.stockDetail(id, principal()), type); }
        catch (SQLException failure) { throw failed(failure); }
    }
    <T extends AbstractPersistable<?>> Page<T> logs(Long id, RestJsonWrapperBean wrapper, Class<T> type) {
        try { return page(query.stockLog(id, OwnedReadRequest.parse(wrapper), principal()), type); }
        catch (SQLException failure) { throw failed(failure); }
    }
    <T extends AbstractPersistable<?>> T stockLogDetail(Long id, Class<T> type) {
        try { return bean(query.stockLogDetail(id, principal()), type); }
        catch (SQLException failure) { throw failed(failure); }
    }

    private JdbcPersonnelIdentity.Principal principal() throws SQLException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        HttpSession session = ((ServletRequestAttributes) attributes).getRequest().getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.CONSUMABLE_READ);
        return provider.delegatePrincipal(boundary.currentPrincipal(session));
    }
    private static IllegalStateException failed(SQLException cause) {
        return new IllegalStateException("consumables read failed", cause);
    }
    private static <T extends AbstractPersistable<?>> Page<T> page(Pagination<Map<String,Object>> page, Class<T> type) {
        List<T> rows = new ArrayList<T>();
        for (Map<String,Object> row : page.getRows()) rows.add(bean(row, type));
        return new Pagination<T>(page, rows);
    }
    private static <T extends AbstractPersistable<?>> T bean(Map<String,Object> row, Class<T> type) {
        if (row == null) return null;
        final T bean;
        try { bean = type.newInstance(); }
        catch (ReflectiveOperationException failure) { throw new IllegalStateException("original DTO cannot be instantiated", failure); }
        Map<String,Method> setters = new HashMap<String,Method>();
        for (Method method : type.getMethods()) {
            if (method.isBridge() || method.isSynthetic() || !method.getName().startsWith("set") || method.getParameterTypes().length != 1) continue;
            if ("setExt$".equals(method.getName())) continue;
            setters.put(method.getName().substring(3).toUpperCase(Locale.ROOT), method);
        }
        for (Map.Entry<String,Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || "RN".equalsIgnoreCase(key) || value == null) continue;
            Method setter = setters.get(key.replace("_", "").toUpperCase(Locale.ROOT));
            if (setter == null) { bean.setExt$Item(key, String.valueOf(value)); continue; }
            try { setter.invoke(bean, value(value, setter.getParameterTypes()[0])); }
            catch (IllegalAccessException failure) { throw new IllegalStateException("original DTO setter inaccessible", failure); }
            catch (InvocationTargetException failure) { throw new IllegalStateException("original DTO setter failed", failure.getCause()); }
        }
        return bean;
    }
    private static Object value(Object value, Class<?> target) {
        if (target.isInstance(value)) return value;
        if (target == String.class) return String.valueOf(value);
        if (target == Long.class) return number(value).longValue();
        if (target == Double.class) return number(value).doubleValue();
        if (target == LocalDateTime.class) {
            if (value instanceof Timestamp) return ((Timestamp)value).toLocalDateTime();
            if (value instanceof java.util.Date) return new Timestamp(((java.util.Date)value).getTime()).toLocalDateTime();
            String text = String.valueOf(value).trim();
            try { return LocalDateTime.parse(text); }
            catch (java.time.format.DateTimeParseException ignored) { return Timestamp.valueOf(text.replace('T',' ')).toLocalDateTime(); }
        }
        throw new IllegalStateException("unsupported original DTO value type: " + target.getName());
    }
    private static Number number(Object value) {
        return value instanceof Number ? (Number)value : new java.math.BigDecimal(String.valueOf(value));
    }
}
