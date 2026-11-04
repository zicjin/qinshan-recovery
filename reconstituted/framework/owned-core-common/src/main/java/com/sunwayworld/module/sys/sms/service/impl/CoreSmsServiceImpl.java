/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.ResultEntity;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.sms.bean.CoreSmsBean;
import com.sunwayworld.module.sys.sms.bean.CoreSmsErrorLogBean;
import com.sunwayworld.module.sys.sms.core.CoreSmsManager;
import com.sunwayworld.module.sys.sms.dao.CoreSmsDao;
import com.sunwayworld.module.sys.sms.service.CoreSmsErrorLogService;
import com.sunwayworld.module.sys.sms.service.CoreSmsService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreSmsServiceImpl
implements CoreSmsService {
    private static final Logger logger = LogManager.getLogger(CoreSmsService.class);
    @Autowired
    private CoreSmsDao smsDao;
    @Autowired
    private CoreSmsErrorLogService smsErrorLogService;
    @Autowired(required=false)
    private CoreSmsManager smsManager;
    @Value(value="${sunway.sms.max-retries:5}")
    private long maxRetries;

    @Override
    public CoreSmsDao getDao() {
        return this.smsDao;
    }

    @Override
    public Page<CoreSmsErrorLogBean> selectErrorLogPagination(Long id, RestJsonWrapperBean wrapper) {
        wrapper.removePageParams();
        return this.smsErrorLogService.selectPaginationByFilter(SearchFilter.instance().match("SMSID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void reset(RestJsonWrapperBean wrapper) {
        List<Long> idList = wrapper.parseId(Long.class);
        if (!idList.isEmpty()) {
            List smsList = idList.stream().map(i -> {
                CoreSmsBean sms = new CoreSmsBean();
                sms.setId((Long)i);
                sms.setRetryQty(0L);
                sms.setStatus("0");
                return sms;
            }).collect(Collectors.toList());
            this.getDao().update(smsList, new String[]{"RETRYQTY", "STATUS", "SENDTIME"});
        }
    }

    @Override
    @Transactional
    public void close(RestJsonWrapperBean wrapper) {
        List<Long> idList = wrapper.parseId(Long.class);
        if (!idList.isEmpty()) {
            List smsList = idList.stream().map(i -> {
                CoreSmsBean sms = new CoreSmsBean();
                sms.setId((Long)i);
                sms.setStatus("3");
                return sms;
            }).collect(Collectors.toList());
            this.getDao().update(smsList, new String[]{"STATUS"});
        }
    }

    @Override
    @Transactional
    public void push(String type, String user, String mobile, String message) {
        CoreSmsBean sms = new CoreSmsBean();
        sms.setId(ApplicationContextHelper.getNextIdentity());
        sms.setType(type);
        sms.setUser(user);
        sms.setMobile(mobile);
        sms.setMessage(message);
        this.getDao().insert(sms);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void send(Long id) {
        CoreSmsBean sms = (CoreSmsBean)this.getDao().selectByIdIfPresent(id);
        if (sms == null) {
            return;
        }
        if (this.smsManager == null) {
            sms.setStatus("2");
            this.getDao().update(sms, new String[]{"STATUS"});
            this.insertErrorLog(sms.getId(), I18nHelper.getMessage("CORE.MODULE.SYS.SMS.TIP.NO_SMSMANAGER", new String[0]));
            return;
        }
        try {
            this.smsManager.send(sms.getMobile(), sms.getMessage());
            sms.setStatus("1");
            sms.setSendTime(LocalDateTime.now());
            this.getDao().update(sms, new String[]{"STATUS", "SENDTIME"});
        }
        catch (Exception ex) {
            logger.warn(ex);
            sms.setRetryQty(sms.getRetryQty() + 1L);
            if (sms.getRetryQty() >= this.maxRetries) {
                sms.setStatus("2");
            }
            this.getDao().update(sms, new String[]{"STATUS", "RETRYQTY"});
            this.insertErrorLog(sms.getId(), ObjectUtils.getStackTrace(ex));
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public ResultEntity send(String type, String user, String mobile, String message) {
        CoreSmsBean sms = new CoreSmsBean();
        sms.setId(ApplicationContextHelper.getNextIdentity());
        sms.setType(type);
        sms.setUser(user);
        sms.setMobile(mobile);
        sms.setMessage(message);
        if (this.smsManager == null) {
            sms.setStatus("2");
            this.getDao().insert(sms);
            this.insertErrorLog(sms.getId(), I18nHelper.getMessage("CORE.MODULE.SYS.SMS.TIP.NO_SMSMANAGER", new String[0]));
            return ResultEntity.fail("CORE.MODULE.SYS.SMS.TIP.NO_SMSMANAGER");
        }
        try {
            this.smsManager.send(mobile, message);
            sms.setStatus("1");
            sms.setSendTime(LocalDateTime.now());
            this.getDao().insert(sms);
            return ResultEntity.success();
        }
        catch (Exception ex) {
            logger.warn(ex);
            sms.setStatus("2");
            this.getDao().insert(sms);
            this.insertErrorLog(sms.getId(), ObjectUtils.getStackTrace(ex));
            return ResultEntity.fail(ex.getMessage());
        }
    }

    private void insertErrorLog(Long smsId, String errorLog) {
        CoreSmsErrorLogBean smsErrorLog = new CoreSmsErrorLogBean();
        smsErrorLog.setId(ApplicationContextHelper.getNextIdentity());
        smsErrorLog.setSmsId(smsId);
        smsErrorLog.setErrorLog(errorLog);
        this.smsErrorLogService.getDao().insert((CoreSmsErrorLogBean)smsErrorLog);
    }
}
