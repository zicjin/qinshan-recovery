package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedAuditTrailController;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Contract guard for the original grid payload's filter and direct shapes. */
public final class OwnedAuditTrailQueryContractTest {
    public static void main(String[] args) throws Exception {
        Class<?> queryType = Class.forName(
                "com.sunwayworld.owned.internal.web.OwnedAuditTrailController$AuditQuery");
        Method from = queryType.getDeclaredMethod("from", QueryRequest.class, boolean.class);
        from.setAccessible(true);

        Object filtered = from.invoke(null,
                QueryRequest.parse("{\"p\":{\"f\":{\"recordId_SEQ\":\"123\"}}}", null), true);
        check(longField(queryType, filtered, "recordId") == 123L,
                "recordId_SEQ inside the original f envelope must be accepted");

        Object direct = from.invoke(null,
                QueryRequest.parse("{\"p\":{\"recordId_SEQ\":\"456\"}}", null), true);
        check(longField(queryType, direct, "recordId") == 456L,
                "direct recordId_SEQ must remain accepted");

        System.out.println("owned audit trail query payload contract passed (filter and direct shapes)");
    }

    private static long longField(Class<?> type, Object value, String name) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return ((Long) field.get(value)).longValue();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
