/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.support;

public class CoreScheduleTaskScriptTemplateHelper {
    public static String generateScheduleTaskScript(String taskId) {
        StringBuilder script = new StringBuilder();
        script.append(" import com.sunwayworld.framework.support.base.service.MapService; \n");
        script.append(" import org.springframework.stereotype.Repository; \n");
        script.append(" import org.springframework.transaction.annotation.Transactional; \n");
        script.append("  \n");
        script.append(" @Repository \n");
        script.append(" public class ScheduleTaskScript" + taskId + " implements MapService { \n");
        script.append("  \n");
        script.append("     /** \n");
        script.append("      * \u5b9a\u65f6\u4efb\u52a1\u6267\u884c \n");
        script.append("      */ \n");
        script.append("     @Transactional \n");
        script.append("     public void execute(String param) { \n");
        script.append("  \n");
        script.append("     } \n");
        script.append("  \n");
        script.append(" } \n");
        return script.toString();
    }
}
