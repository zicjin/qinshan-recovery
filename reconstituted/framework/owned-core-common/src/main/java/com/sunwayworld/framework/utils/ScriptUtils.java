/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.ScriptException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public abstract class ScriptUtils {
    private static final ScriptEngineManager manager = new ScriptEngineManager();
    private static final ScriptEngine javaScriptEngine = manager.getEngineByName("JavaScript");

    public static final <T> T evalJavaScript(String javaScript) {
        try {
            return (T)javaScriptEngine.eval(javaScript);
        }
        catch (javax.script.ScriptException e) {
            throw new ScriptException(e);
        }
    }
}
