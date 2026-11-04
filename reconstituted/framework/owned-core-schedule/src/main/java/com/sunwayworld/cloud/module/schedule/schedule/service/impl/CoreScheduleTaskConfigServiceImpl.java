/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskBean;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskConfigBean;
import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.CoreScheduleTaskConfigDao;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskConfigService;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskLogService;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamGuardianLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.ReflectionException;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.utils.ExceptionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreScheduleTaskConfigServiceImpl
implements CoreScheduleTaskConfigService {
    private static final Logger logger = LogManager.getLogger(CoreScheduleTaskConfigService.class);
    @Autowired
    private CoreScheduleTaskConfigDao taskConfigDao;
    @Autowired
    private CoreScheduleTaskService taskService;
    @Autowired
    private CoreScheduleTaskLogService logService;
    @Autowired
    @Lazy
    private CoreScheduleTaskConfigService proxyInstance;

    @Override
    public CoreScheduleTaskConfigDao getDao() {
        return this.taskConfigDao;
    }

    @Override
    @Transactional
    public String insertByTask(String taskId) {
        CoreScheduleTaskConfigBean config = new CoreScheduleTaskConfigBean();
        config.setId(ApplicationContextHelper.getNextSequence("T_CORE_SCHEDULE_TASK_CONFIG"));
        config.setTaskId(taskId);
        config.setUid(config.getId());
        this.getDao().insert(config);
        return config.getId();
    }

    @Override
    public CoreServiceStatusDTO selectStatus(String id, String currentStatusCode) {
        CoreServiceStatusDTO status = new CoreServiceStatusDTO();
        CoreScheduleTaskConfigBean config = (CoreScheduleTaskConfigBean)this.getDao().selectByIdIfPresent(id);
        CoreServiceStatusDTO.ActiveStatus activeStatus = new CoreServiceStatusDTO.ActiveStatus();
        if (config != null) {
            activeStatus.setActivatedFlag(config.getActivatedFlag());
            CoreScheduleTaskBean task = (CoreScheduleTaskBean)this.taskService.selectById(config.getTaskId());
            status.setExt$Item("type", task.getType());
        } else {
            activeStatus.setActivatedFlag("0");
        }
        status.setActiveStatus(activeStatus);
        return status;
    }

    @Override
    @Transactional(timeout=300)
    public void scheduleTask(String id, LocalDateTime triggeredTime) {
        GikamGuardianLocker.isolatedRun("T_CORE_SCHEDULE_TASK_CONFIG:" + id, () -> {
            CoreScheduleTaskConfigBean config = (CoreScheduleTaskConfigBean)this.getDao().selectByIdIfPresent(id);
            if (config == null) {
                return;
            }
            if (!this.taskService.executableTask(config.getTaskId(), config.getParam(), triggeredTime).booleanValue()) {
                return;
            }
            CoreScheduleTaskLogBean log = new CoreScheduleTaskLogBean();
            log.setConfigId(config.getId());
            log.setCron(config.getCron());
            log.setStartTime(LocalDateTime.now());
            log.setParam(config.getParam());
            try {
                String executedResult = this.taskService.runTask(config.getTaskId(), config.getParam(), triggeredTime);
                log.setExecutedResult(executedResult);
                log.setSuccess("1");
            }
            catch (ReflectionException re) {
                logger.error(re.getMessage(), (Throwable)re);
                Throwable cause = ExceptionUtils.getDeepestCause(re);
                log.setLog(ObjectUtils.getStackTrace(cause));
                log.setSuccess("0");
            }
            catch (Throwable t) {
                logger.error(t.getMessage(), t);
                log.setLog(ObjectUtils.getStackTrace(t));
                log.setSuccess("0");
            }
            log.setEndTime(LocalDateTime.now());
            log.setId(ApplicationContextHelper.getNextIdentity());
            this.logService.getDao().insert((CoreScheduleTaskLogBean)log);
            this.updateTaskConfigTimes(config);
        });
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void updateTaskConfigTimes(CoreScheduleTaskConfigBean config) {
        config.setTimes(Optional.ofNullable(config.getTimes()).orElse(0L) + 1L);
        this.getDao().update(config, new String[]{"TIMES"});
    }

    @Override
    public void deleteByIdList(List<String> coreScheduleTaskConfigIdList) {
        this.getDao().deleteByIdList(coreScheduleTaskConfigIdList);
    }

    @Override
    public List<CoreScheduleTaskConfigBean> selectList(List<CoreScheduleTaskConfigBean> coreScheduleTaskConfigBeanTempList, List<String> searchColNames, List<String> selectColNames) {
        return this.getDao().selectList(coreScheduleTaskConfigBeanTempList, searchColNames, selectColNames, new Order[0]);
    }

    @Override
    public void update(List<CoreScheduleTaskConfigBean> configList, String ... updateColNames) {
        this.getDao().update(configList, updateColNames);
    }

    @Override
    public List<CoreScheduleTaskConfigBean> selectListByOneColumnValues(List<Long> limsEquiptEventScheduleIdList, String colName) {
        return this.getDao().selectListByOneColumnValues(limsEquiptEventScheduleIdList, colName, new Order[0]);
    }

    @Override
    public void insert(CoreScheduleTaskConfigBean coreScheduleTaskConfig) {
        this.getDao().insert(coreScheduleTaskConfig);
    }

    @Override
    public void deleteBy(List<CoreScheduleTaskConfigBean> taskConfigBeans) {
        this.getDao().deleteBy(taskConfigBeans, new String[0]);
    }
}
