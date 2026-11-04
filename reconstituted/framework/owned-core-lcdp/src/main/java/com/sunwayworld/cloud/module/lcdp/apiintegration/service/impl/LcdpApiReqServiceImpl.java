/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiReqDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiReqService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiResService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptUtils;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpApiReqServiceImpl
implements LcdpApiReqService {
    @Autowired
    private LcdpApiReqDao lcdpApiReqDao;
    @Autowired
    @Lazy
    private LcdpApiIntegrationService lcdpApiIntegrationService;
    @Autowired
    private LcdpApiResService lcdpApiResService;

    @Override
    public LcdpApiReqDao getDao() {
        return this.lcdpApiReqDao;
    }

    @Override
    public Page<LcdpApiResBean> selectResPaginationByApiReqId(Long id, RestJsonWrapperBean wrapper) {
        return this.lcdpApiResService.selectPaginationByFilter(SearchFilter.instance().match("REQUESTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public Object doProcessRequest(LcdpApiReqBean request, Object ... args) {
        LcdpApiBean api = this.lcdpApiIntegrationService.selectApiByCode(request.getApiCode());
        LcdpApiResBean insertResponse = new LcdpApiResBean();
        insertResponse.setRequestId(request.getId());
        try {
            String returnValue = LcdpScriptUtils.callScriptMethod(api.getScriptMethodPath(), args);
            if (returnValue != null) {
                insertResponse.setStatus("1");
                insertResponse.setResponseBody(returnValue);
            } else {
                insertResponse.setStatus("1");
            }
            String string = returnValue;
            return string;
        }
        catch (Exception ex) {
            insertResponse.setStatus("0");
            insertResponse.setLog(ObjectUtils.getStackTrace(ex));
            throw ex;
        }
        finally {
            insertResponse.setResponseDateTime(LocalDateTime.now());
            ((LcdpApiReqServiceImpl)SunwayAopContext.currentProxy()).record(request, insertResponse);
        }
    }

    @Override
    public void reset(Long id) {
        LcdpApiReqBean request = (LcdpApiReqBean)this.selectById(id);
        ((LcdpApiReqServiceImpl)SunwayAopContext.currentProxy()).doProcessRequest(request, new Object[0]);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void record(LcdpApiReqBean request, LcdpApiResBean insertResponse) {
        if ("0".equals(insertResponse.getStatus())) {
            request.setFailedCount(request.getFailedCount() == null ? 1L : request.getFailedCount() + 1L);
            request.setStatus("-1");
        } else {
            request.setStatus("1");
        }
        request.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(request);
        insertResponse.setId(ApplicationContextHelper.getNextIdentity());
        insertResponse.setRequestId(request.getId());
        this.lcdpApiResService.getDao().insert((LcdpApiResBean)insertResponse);
    }
}
