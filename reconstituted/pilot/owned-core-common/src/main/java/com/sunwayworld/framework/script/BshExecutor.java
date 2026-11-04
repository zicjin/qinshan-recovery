/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.script;

import bsh.EvalError;
import bsh.Interpreter;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.script.BshExecutorCache;
import com.sunwayworld.framework.script.exeception.BshException;
import com.sunwayworld.framework.script.exeception.BshIgnoreException;
import com.sunwayworld.framework.utils.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BshExecutor {
    private static final Logger logger = LogManager.getLogger(BshExecutor.class);
    private Interpreter interpreter = new Interpreter();

    private BshExecutor() {
    }

    public static BshExecutor instance() {
        return BshExecutorCache.INSTANCE.take();
    }

    public <V> void set(String name, V value) {
        try {
            this.interpreter.set(name, value);
        }
        catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);
            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), (Throwable)ignoreException);
                }
                return;
            }
            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);
            if (checkedException != null) {
                throw new BshException(checkedException.getMessage(), checkedException);
            }
            throw new BshException(ee);
        }
    }

    public <T> T eval(String script) {
        try {
            return (T)this.interpreter.eval(script);
        }
        catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);
            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), (Throwable)ignoreException);
                }
                return null;
            }
            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);
            if (checkedException != null) {
                throw new BshException(checkedException.getMessage(), checkedException);
            }
            throw new BshException(ee);
        }
    }

    public <T> T eval(String script, String errorMessage) {
        try {
            return (T)this.interpreter.eval(script);
        }
        catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);
            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), (Throwable)ignoreException);
                }
                return null;
            }
            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);
            if (checkedException != null) {
                throw new BshException(checkedException.getMessage(), checkedException);
            }
            throw new BshException(errorMessage, ee);
        }
    }

    public <T> T get(String name) {
        try {
            return (T)this.interpreter.get(name);
        }
        catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);
            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), (Throwable)ignoreException);
                }
                return null;
            }
            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);
            if (checkedException != null) {
                throw new BshException(checkedException.getMessage(), checkedException);
            }
            throw new BshException(ee);
        }
    }

    static BshExecutor createNewInstance() {
        BshExecutor bsh = new BshExecutor();
        bsh.eval("import com.sunwayworld.framework.utils.StringUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.ObjectUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.ConvertUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.ArithUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.NumberUtils;");
        return bsh;
    }
}
