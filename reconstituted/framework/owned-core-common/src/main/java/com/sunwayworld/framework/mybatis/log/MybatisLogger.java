/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.log;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.ibatis.logging.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MybatisLogger
implements Log {
    private static final ThreadLocal<Level> LOGLEVEL_HOLDER = new ThreadLocal();
    private static final ThreadLocal<List<Consumer<String>>> CONSUMER_STACK_HOLDER = new ThreadLocal();
    private static final String LOG_PREPARING_SQL_PREFIX = "==>  Preparing: ";
    private static final String LOG_PARAMETERS_SQL_PREFIX = "==> Parameters: ";
    private static final String LOG_TOTAL_SQL_PREFIX = "<==      Total: ";
    private static final String LOG_UPDATES_SQL_PREFIX = "<==    Updates: ";
    private static final String LOG_CLOSING_JDBC_PREFIX = "Closing JDBC Connection ";
    private static final String LOG_CLOSING_SQLSESSION_PREFIX = "Closing non transactional SqlSession ";
    private static final String LOG_RELEASING_SQLSESSION_PREFIX = "Releasing transactional SqlSession ";
    private static final Logger logger = LogManager.getLogger("");
    private final String simpleSignature;
    private final String[] debugKeywords;
    private final int lengthLimitation;

    public MybatisLogger(String signature) {
        int index = signature.substring(0, signature.lastIndexOf(".")).lastIndexOf(".") + 1;
        this.simpleSignature = signature.substring(index);
        this.debugKeywords = StringUtils.split(ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.debug-keywords"), ",");
        this.lengthLimitation = ApplicationContextHelper.getEnvironment().getProperty("sunway.mybatis.logging.length-limitation", Integer.class, 0);
    }

    public static void addConsumer(Consumer<String> consumer) {
        List<Consumer<String>> consumerList = CONSUMER_STACK_HOLDER.get();
        if (consumerList == null) {
            consumerList = new ArrayList<Consumer<String>>();
            CONSUMER_STACK_HOLDER.set(consumerList);
        }
        consumerList.add(consumer);
    }

    private String getSimpleSignature() {
        return this.simpleSignature + " ";
    }

    @Override
    public boolean isDebugEnabled() {
        if (ApplicationContextHelper.stressTesting()) {
            return false;
        }
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        if (ApplicationContextHelper.stressTesting()) {
            return false;
        }
        return logger.isTraceEnabled();
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(this.getSimpleSignature() + message, throwable);
    }

    @Override
    public void error(String message) {
        logger.error(this.getSimpleSignature() + message);
    }

    @Override
    public void debug(String message) {
        if (MybatisPageHelper.isCountOnly() && !ApplicationContextHelper.loggingCountOnlyEnabled()) {
            return;
        }
        if (this.closingMessage(message)) {
            CONSUMER_STACK_HOLDER.remove();
            LOGLEVEL_HOLDER.remove();
            return;
        }
        if (!this.loggableMessage(message)) {
            return;
        }
        if (this.isPreparingMessage(message)) {
            if (this.isDebugMessage(message)) {
                LOGLEVEL_HOLDER.set(Level.DEBUG);
            } else {
                LOGLEVEL_HOLDER.set(Level.INFO);
            }
        }
        if (CONSUMER_STACK_HOLDER.get() != null) {
            CONSUMER_STACK_HOLDER.get().forEach(c -> c.accept(message));
        }
        String outputMessage = message;
        if (this.lengthLimitation > 0 && message.length() > this.lengthLimitation) {
            outputMessage = message.substring(0, this.lengthLimitation) + " (more)";
        }
        if (Level.DEBUG.equals(LOGLEVEL_HOLDER.get())) {
            if (logger.isDebugEnabled()) {
                logger.debug(this.getSimpleSignature() + outputMessage);
            }
        } else if (Level.INFO.equals(LOGLEVEL_HOLDER.get())) {
            logger.info(this.getSimpleSignature() + outputMessage);
        }
    }

    @Override
    public void trace(String message) {
        logger.debug(this.getSimpleSignature() + message);
    }

    @Override
    public void warn(String message) {
        if (ApplicationContextHelper.stressTesting()) {
            return;
        }
        logger.warn(this.getSimpleSignature() + message);
    }

    private boolean isPreparingMessage(String message) {
        return StringUtils.startsWith(message, LOG_PREPARING_SQL_PREFIX);
    }

    private boolean isDebugMessage(String message) {
        for (String keyword : this.debugKeywords) {
            if (!StringUtils.containsIgnoreCase(message, keyword)) continue;
            return true;
        }
        return false;
    }

    private boolean loggableMessage(String message) {
        return StringUtils.startsWith(message, LOG_PREPARING_SQL_PREFIX) || StringUtils.startsWith(message, LOG_PARAMETERS_SQL_PREFIX) || StringUtils.startsWith(message, LOG_TOTAL_SQL_PREFIX) || StringUtils.startsWith(message, LOG_UPDATES_SQL_PREFIX);
    }

    private boolean closingMessage(String message) {
        return StringUtils.startsWith(message, LOG_CLOSING_JDBC_PREFIX) || StringUtils.startsWith(message, LOG_CLOSING_SQLSESSION_PREFIX) || StringUtils.startsWith(message, LOG_RELEASING_SQLSESSION_PREFIX);
    }
}
