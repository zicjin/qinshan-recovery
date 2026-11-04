/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.CoreScheduleTaskDao;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskService;
import com.sunwayworld.cloud.module.schedule.schedule.support.CoreScheduleTaskScriptTemplateHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.TaskException;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreScheduleTaskServiceImpl
implements CoreScheduleTaskService {
    public static final String CLASS_KEY = "public class ";
    public static final String CLASS_KEY_EXPR = "public\\s+class\\s+";
    @Autowired
    private CoreScheduleTaskDao taskDao;

    @Override
    public CoreScheduleTaskDao getDao() {
        return this.taskDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean jsonWrapper) {
        CoreScheduleTaskBean scheduleTask = jsonWrapper.parseUnique(CoreScheduleTaskBean.class);
        String id = ApplicationContextHelper.getNextSequence(this.getDao().getTable());
        scheduleTask.setId(id);
        scheduleTask.setType("service");
        this.getDao().insert(scheduleTask);
        return id;
    }

    @Override
    public Boolean executableTask(String id, String param, LocalDateTime triggeredTime) {
        CoreScheduleTaskBean task = (CoreScheduleTaskBean)this.getDao().selectById(id);
        if (StringUtils.isEmpty(task.getIfExecutableServiceName())) {
            return true;
        }
        Class serviceType = ClassUtils.getClass(task.getIfExecutableServiceName());
        Object service = ApplicationContextHelper.getBean(serviceType);
        Method method = ReflectionUtils.getUniqueMethodByName(serviceType, task.getIfExecutableMethodName());
        if (method == null) {
            throw new TaskException("Invalid schedule task, id = " + id);
        }
        if (method.getParameterCount() > 0) {
            Object[] params = new Object[method.getParameterCount()];
            if (method.getParameterCount() > 1) {
                params[0] = triggeredTime;
                params[1] = ConvertUtils.convert(param, method.getParameterTypes()[1]);
            } else {
                params[0] = ConvertUtils.convert(param, method.getParameterTypes()[0]);
            }
            return ConvertUtils.convert(ReflectionUtils.invokeMethod(method, service, params), Boolean.class);
        }
        return ConvertUtils.convert(ReflectionUtils.invokeMethod(method, service), Boolean.class);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public String runTask(String id, String param, LocalDateTime triggeredTime) {
        CoreScheduleTaskBean task = (CoreScheduleTaskBean)this.getDao().selectById(id);
        String taskScript = task.getTaskScript();
        String lcdpServiceMethod = task.getLcdpServiceMethod();
        if (!StringUtils.isEmpty(lcdpServiceMethod)) {
            String result = (String)SpringUtils.invoke("lcdpResourceServiceImpl", "callScheduleScript", lcdpServiceMethod, new Object[]{param});
            return result;
        }
        if (!StringUtils.isEmpty(taskScript)) {
            taskScript = (String)SpringUtils.invoke("lcdpResourceServiceImpl", "replaceScriptContent", taskScript);
            String className = JavaCompilerManager.getClassName(taskScript);
            Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(taskScript);
            SpringUtils.registerBean(className, clazz);
            Object result = SpringUtils.invoke(className, "execute", param);
            return JSONObject.toJSONString(result);
        }
        Class serviceType = ClassUtils.getClass(task.getServiceName());
        Object service = ApplicationContextHelper.getBean(serviceType);
        Method method = ReflectionUtils.getUniqueMethodByName(serviceType, task.getMethodName());
        if (method == null) {
            throw new TaskException("Invalid schedule task, id = " + id);
        }
        if (method.getParameterCount() > 0) {
            Object[] params = new Object[method.getParameterCount()];
            if (method.getParameterCount() > 1) {
                params[0] = triggeredTime;
                params[1] = ConvertUtils.convert(param, method.getParameterTypes()[1]);
            } else {
                params[0] = ConvertUtils.convert(param, method.getParameterTypes()[0]);
            }
            return ConvertUtils.convert(ReflectionUtils.invokeMethod(method, service, params), String.class);
        }
        return ConvertUtils.convert(ReflectionUtils.invokeMethod(method, service), String.class);
    }

    @Override
    public CoreScheduleTaskBean selectDetail(String id) {
        CoreScheduleTaskBean task = (CoreScheduleTaskBean)this.getDao().selectByIdIfPresent(id);
        String taskScript = task.getTaskScript();
        if (StringUtils.isEmpty(taskScript)) {
            task.setTaskScript(CoreScheduleTaskScriptTemplateHelper.generateScheduleTaskScript(id));
        }
        return task;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.INSTANT_SAVE")
    public void saveScript(RestJsonWrapperBean wrapper) {
        CoreScheduleTaskBean task = wrapper.parseUnique(CoreScheduleTaskBean.class);
        String script = task.getTaskScript();
        String oldClassName = JavaCompilerManager.getClassName(script);
        String newClassName = "ScheduleTaskScript" + task.getId() + "_" + ApplicationContextHelper.getNextIdentity();
        String newScript = script.replaceFirst(CLASS_KEY_EXPR + oldClassName, CLASS_KEY + newClassName);
        task.setTaskScript(newScript);
        this.getDao().update(task, new String[]{"TASKSCRIPT"});
    }
}
