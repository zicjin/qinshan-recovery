package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.sunwayworld.framework.exception.checked.CheckedException;

public final class LcdpScriptPathContractTest {
    private static int checks;

    public static void main(String[] args) {
        requireRejected(null, "GIKAM.LCDP.SCRIPT_PATH_REQUIRED");
        requireRejected("", "GIKAM.LCDP.SCRIPT_PATH_REQUIRED");
        requireRejected("   ", "GIKAM.LCDP.SCRIPT_PATH_REQUIRED");
        requireRejected("method", "GIKAM.LCDP.SCRIPT_PATH_INVALID");
        requireRejected(".method", "GIKAM.LCDP.SCRIPT_PATH_INVALID");
        requireRejected("path.", "GIKAM.LCDP.SCRIPT_PATH_INVALID");
        check("path.to.Script".equals(LcdpScriptPath.methodPath("path.to.Script.method")), "method path");
        check("method".equals(LcdpScriptPath.methodName("path.to.Script.method")), "method name");
        System.out.println("LCDP script path contract: PASS (" + checks + " checks)");
    }

    private static void requireRejected(String value, String expected) {
        try {
            LcdpScriptPath.methodPath(value);
            throw new AssertionError("expected script path rejection: " + expected);
        } catch (CheckedException failure) {
            check(expected.equals(failure.getMessage()), "script path rejection: " + expected);
        }
    }

    private static void check(boolean value, String label) {
        checks++;
        if (!value) throw new AssertionError(label);
    }
}
