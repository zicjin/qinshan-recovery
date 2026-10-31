package com.sunwayworld.emc.framework.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * Standalone regression harness for the low-privilege generic CoreFile query
 * policy. It intentionally avoids a test-framework dependency because the
 * recovered production dependency set does not include one.
 */
public final class FileQueryGuardTest {

    private FileQueryGuardTest() {
    }

    public static void main(String[] args) throws Exception {
        PersonnelSignatureFileFilter filter = new PersonnelSignatureFileFilter(null, null);
        Method policy = PersonnelSignatureFileFilter.class
                .getDeclaredMethod("isExplicitlyNonPersonnelQuery", byte[].class);
        policy.setAccessible(true);

        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"targetId_SEQ\":\"T_OTHER$1\"},\"n\":-1}}", true);
        assertDecision(policy, filter,
                "{\"p\":\"{\\\"f\\\":{\\\"targetId_IN\\\":\\\"T_OTHER$1,T_OTHER$2\\\"}}\"}", true);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":\"{\\\"targetId_IN\\\":\\\"T_OTHER$1,T_OTHER$2\\\"}\"}}", true);
        assertDecision(policy, filter,
                "{\"requestJson\":\"{\\\"p\\\":{\\\"f\\\":{\\\"targetId\\\":\\\"T_OTHER$1\\\"}}}\"}", true);

        assertDecision(policy, filter,
                "{\"p\":\"{}\",\"f\":{\"targetId\":\"T_OTHER$1\"}}", false);
        assertDecision(policy, filter,
                "{\"f\":{\"targetId\":\"T_OTHER$1\"},\"requestJson\":\"{\\\"p\\\":\\\"{}\\\"}\"}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"targetId\":\"T_OTHER$1\"},\"qf\":{\"targetId\":\"T_CORE_USER$1\"}}}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"targetId\":\"T_OTHER$1\"},\"af\":{\"ignored\":true}}}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"targetId_OR\":\"T_CORE_USER$1\"}}}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"targetid_OR\":\"T_CORE_USER$1\"}}}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"ext$.targetId\":\"T_OTHER$1\"}}}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"targetId_IN\":\"T_OTHER$1,T_CORE_USER$1\"}}}", false);
        assertDecision(policy, filter,
                "{\"p\":{\"f\":{\"bizCategory_SEQ\":\"PIC\"}}}", false);

        System.out.println("FileQueryGuardTest PASS");
    }

    private static void assertDecision(Method policy, PersonnelSignatureFileFilter filter,
            String payload, boolean expected) throws IllegalAccessException, InvocationTargetException {
        boolean actual = ((Boolean) policy.invoke(filter, payload.getBytes(StandardCharsets.UTF_8))).booleanValue();
        if (actual != expected) {
            throw new AssertionError("policy mismatch for payload: " + payload);
        }
    }
}
