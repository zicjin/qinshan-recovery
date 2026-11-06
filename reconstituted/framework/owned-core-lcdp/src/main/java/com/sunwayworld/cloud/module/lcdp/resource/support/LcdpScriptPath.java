package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.sunwayworld.framework.exception.checked.CheckedException;

/** Validates the script method address before any database or class lookup. */
public final class LcdpScriptPath {
    private LcdpScriptPath() {
    }

    public static String methodPath(String scriptPath) {
        int separator = separator(scriptPath);
        return scriptPath.substring(0, separator);
    }

    public static String methodName(String scriptPath) {
        int separator = separator(scriptPath);
        return scriptPath.substring(separator + 1);
    }

    private static int separator(String scriptPath) {
        if (scriptPath == null || scriptPath.trim().length() == 0) {
            throw new CheckedException("GIKAM.LCDP.SCRIPT_PATH_REQUIRED");
        }
        int separator = scriptPath.lastIndexOf('.');
        if (separator <= 0 || separator == scriptPath.length() - 1) {
            throw new CheckedException("GIKAM.LCDP.SCRIPT_PATH_INVALID");
        }
        return separator;
    }
}
