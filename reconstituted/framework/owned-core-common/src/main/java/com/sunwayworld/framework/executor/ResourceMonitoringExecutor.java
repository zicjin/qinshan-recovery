/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.executor;

import com.sunwayworld.framework.data.ObjectHolder;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.executor.core.TraceRunnable;
import com.sunwayworld.framework.utils.SystemUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResourceMonitoringExecutor {
    private static final Logger logger = LogManager.getLogger(ResourceMonitoringExecutor.class);
    private static final int SLEEP_INTERVAL = 100;
    private static long momeryThreshold = 50L;
    private static long cpuThreshold = 50L;
    private static long maxAliveSec = 600L;
    private static long maxTaskSize = 10L;
    private static boolean enabled = true;
    public static final Map<Long, Thread> THREAD_CONTAINER = new HashMap<Long, Thread>();

    @Value(value="${sunway.executor.resource-monitoring.momery-threshold:50}")
    public void setMomeryThreshold(long momeryThreshold) {
        ResourceMonitoringExecutor.momeryThreshold = momeryThreshold;
    }

    @Value(value="${sunway.executor.resource-monitoring.cpu-threshold:50}")
    public void setCpuThreshold(long cpuThreshold) {
        ResourceMonitoringExecutor.cpuThreshold = cpuThreshold;
    }

    @Value(value="${sunway.executor.resource-monitoring.max-alive-sec:600}")
    public void setMaxAliveSec(long maxAliveSec) {
        ResourceMonitoringExecutor.maxAliveSec = maxAliveSec;
    }

    @Value(value="${sunway.executor.resource-monitoring.max-task-size:10}")
    public void setMaxTaskSize(long maxTaskSize) {
        ResourceMonitoringExecutor.maxTaskSize = maxTaskSize;
    }

    @Value(value="${sunway.executor.resource-monitoring.enabled:true}")
    public void setEnabled(boolean enabled) {
        ResourceMonitoringExecutor.enabled = enabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void run(Runnable task) {
        Map<Long, Thread> map = THREAD_CONTAINER;
        synchronized (map) {
            if ((long)THREAD_CONTAINER.size() >= maxTaskSize) {
                throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.TASK_OVERLOAD", new String[0]);
            }
        }
        Long tid = Thread.currentThread().getId();
        if (THREAD_CONTAINER.get(tid) != null) {
            throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.UNABLE_RUN_MULTIPLE_TASK", new String[0]);
        }
        try {
            if (enabled) {
                ResourceMonitoringExecutor.checkCpuUsage();
                ResourceMonitoringExecutor.checkMemoryUsage();
            }
            ObjectHolder runningStatusHolder = new ObjectHolder(false);
            TraceRunnable runnable = TraceRunnable.of(() -> {
                try {
                    task.run();
                }
                finally {
                    runningStatusHolder.setObject(true);
                }
            });
            Thread thread = new Thread(runnable);
            THREAD_CONTAINER.put(tid, thread);
            long deadline = System.currentTimeMillis() + maxAliveSec * 1000L;
            thread.start();
            while (!runningStatusHolder.getOrDefault(false).booleanValue()) {
                if (deadline > System.currentTimeMillis()) {
                    Thread.sleep(100L);
                    if (!enabled) continue;
                    ResourceMonitoringExecutor.checkMemoryUsage();
                    continue;
                }
                try {
                    thread.stop();
                }
                catch (Exception ex) {
                    logger.warn(ex.getMessage(), (Throwable)ex);
                }
                System.gc();
                throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.RUNNING_TIMEOUT", new String[0]);
            }
        }
        catch (ApplicationRuntimeException | ApplicationWarningException ae) {
            throw ae;
        }
        catch (Exception ex) {
            throw new ApplicationRuntimeException(ex);
        }
        finally {
            THREAD_CONTAINER.remove(tid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T run(Supplier<T> task) {
        Map<Long, Thread> map = THREAD_CONTAINER;
        synchronized (map) {
            if ((long)THREAD_CONTAINER.size() >= maxTaskSize) {
                throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.TASK_OVERLOAD", new String[0]);
            }
        }
        Long tid = Thread.currentThread().getId();
        if (THREAD_CONTAINER.get(tid) != null) {
            throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.UNABLE_RUN_MULTIPLE_TASK", new String[0]);
        }
        try {
            if (enabled) {
                ResourceMonitoringExecutor.checkCpuUsage();
                ResourceMonitoringExecutor.checkMemoryUsage();
            }
            ObjectHolder resultHolder = new ObjectHolder();
            ObjectHolder compeletedStatusHolder = new ObjectHolder(false);
            TraceRunnable runnable = TraceRunnable.of(() -> {
                try {
                    resultHolder.setObject(task.get());
                }
                finally {
                    compeletedStatusHolder.setObject(true);
                }
            });
            Thread thread = new Thread(runnable);
            THREAD_CONTAINER.put(tid, thread);
            long deadline = System.currentTimeMillis() + maxAliveSec * 1000L;
            thread.start();
            while (!compeletedStatusHolder.getOrDefault(false).booleanValue()) {
                if (deadline > System.currentTimeMillis()) {
                    Thread.sleep(100L);
                    if (!enabled) continue;
                    ResourceMonitoringExecutor.checkMemoryUsage();
                    continue;
                }
                try {
                    thread.stop();
                }
                catch (Exception ex) {
                    logger.warn(ex.getMessage(), (Throwable)ex);
                }
                throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.RUNNING_TIMEOUT", new String[0]);
            }
            T t = resultHolder.getOrDefault(null);
            return t;
        }
        catch (ApplicationRuntimeException | ApplicationWarningException ae) {
            throw ae;
        }
        catch (Exception ex) {
            throw new ApplicationRuntimeException(ex);
        }
        finally {
            THREAD_CONTAINER.remove(tid);
        }
    }

    private static void checkCpuUsage() {
        if ((long)SystemUtils.getCpuRatio() > cpuThreshold) {
            throw new ApplicationRuntimeException("EXECUTOR.RESOURCE_MONITORING.CPU_USAGE_OVERLOAD", new String[0]);
        }
    }

    private static void checkMemoryUsage() {
        if ((long)SystemUtils.getMemoryUsageRatio() > momeryThreshold) {
            Long tid = Thread.currentThread().getId();
            Thread thread = THREAD_CONTAINER.get(tid);
            if (thread != null) {
                try {
                    thread.stop();
                }
                catch (Exception ex) {
                    logger.warn(ex.getMessage(), (Throwable)ex);
                }
                finally {
                    THREAD_CONTAINER.remove(tid);
                }
            }
            throw new ApplicationWarningException("EXECUTOR.RESOURCE_MONITORING.MEMORY_USAGE_OVERLOAD", new String[0]);
        }
    }
}
