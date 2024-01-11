package com.sunwayworld.framework.script;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.script.exeception.BshException;
import com.sunwayworld.framework.script.exeception.BshIgnoreException;
import com.sunwayworld.framework.utils.ExceptionUtils;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * BeanShell的执行类，对于一类的数据操作，解决避免重复set参数导致数据操作慢的问题
 */
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
        } catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);

            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), ignoreException);
                }
                return;
            }

            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);

            if (checkedException != null) {
                throw new BshException(checkedException.getMessage());
            }

            throw new BshException(ee);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T eval(String script) {
        try {
            return (T) this.interpreter.eval(script);
        } catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);

            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), ignoreException);
                }
                return null;
            }

            logger.error("The Wrong Script:\n{} \nCaused by: {}", script, ee);
            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);

            if (checkedException != null) {
                throw new BshException(checkedException.getMessage());
            }

            throw new BshException(ee);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T eval(String script, String errorMessage) {
        try {
            return (T) this.interpreter.eval(script);
        } catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);

            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), ignoreException);
                }
                return null;
            }

            logger.error("The Wrong Script:\n{} \nCaused by: {}", script, ee);
            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);

            if (checkedException != null) {
                throw new BshException(checkedException.getMessage());
            }

            throw new BshException(errorMessage);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        try {
            return (T) this.interpreter.get(name);
        } catch (EvalError ee) {
            BshIgnoreException ignoreException = ExceptionUtils.getCause(ee, BshIgnoreException.class);

            if (ignoreException != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ignoreException.getMessage(), ignoreException);
                }
                return null;
            }

            CheckedException checkedException = ExceptionUtils.getCause(ee, CheckedException.class);

            if (checkedException != null) {
                throw new BshException(checkedException.getMessage());
            }

            throw new BshException(ee);
        }
    }

    //--------------------------------------------------------------
    // 默认方法
    //--------------------------------------------------------------
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
