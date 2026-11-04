/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.loop;

import com.sunwayworld.framework.exception.ConcurrentException;
import com.sunwayworld.framework.loop.registry.LoopRunner;
import com.sunwayworld.framework.loop.registry.LoopRunnerRegistry;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ExceptionUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Configuration
@DependsOn(value={"applicationPreparedListener"})
@Component
public class LoopConfiguration {
    private static final Logger logger = LogManager.getLogger(LoopExecutor.class);
    @Lazy
    @Autowired
    private TenantManager tenantManager;

    public void init() {
        new Thread(new LoopExecutor()).start();
        List<Class<?>> loopRunnerClassList = ClassUtils.getGikamConcreteClassList(LoopRunner.class);
        loopRunnerClassList.forEach(c -> LoopRunnerRegistry.INSTANCE.add((Class<? extends LoopRunner>)c));
    }

    private class LoopExecutor
    implements Runnable {
        private LoopExecutor() {
        }

        @Override
        public void run() {
            while (true) {
                ArrayList<LoopRunner> runnerList = new ArrayList<LoopRunner>(LoopRunnerRegistry.INSTANCE.getRunnerContainer());
                try {
                    LoopConfiguration.this.tenantManager.call("LOOPRUNNER", () -> runnerList.stream().filter(r -> r.active()).forEach(r -> {
                        try {
                            r.run();
                        }
                        catch (Exception ex) {
                            if (ExceptionUtils.getCause(ex, ConcurrentException.class) != null) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(ex.getMessage(), (Throwable)ex);
                                }
                            }
                            logger.error(ex.getMessage(), (Throwable)ex);
                        }
                    }), new String[0]);
                    Thread.sleep(1L);
                }
                catch (InterruptedException interruptedException) {
                }
                catch (Exception exception) {
                }
            }
        }
    }
}
