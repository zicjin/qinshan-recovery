/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.service.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.dao.CoreRemoteCallConfigDao;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallConfigService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreRemoteCallConfigServiceImpl
implements CoreRemoteCallConfigService {
    @Autowired
    private CoreRemoteCallConfigDao remoteCallConfigDao;
    @Autowired
    private CoreRemoteCallRequestService coreRemoteCallRequestService;
    @Autowired
    private CoreRemoteCallResponseService coreRemoteCallResponseService;

    @Override
    public CoreRemoteCallConfigDao getDao() {
        return this.remoteCallConfigDao;
    }

    @Override
    public Page<CoreRemoteCallRequestBean> selectRequestPaginationByConfigId(String id, RestJsonWrapperBean wrapper) {
        return this.coreRemoteCallRequestService.selectPaginationByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<CoreRemoteCallResponseBean> selectResponsePaginationByRequestId(String id, RestJsonWrapperBean wrapper) {
        return this.coreRemoteCallResponseService.selectPaginationByFilter(SearchFilter.instance().match("REQUESTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void resetRequest(RestJsonWrapperBean wrapper) {
        this.coreRemoteCallRequestService.reset(wrapper);
    }

    @Override
    public CoreRemoteCallRequestBean selectRequestById(Long id, RestJsonWrapperBean wrapper) {
        return (CoreRemoteCallRequestBean)this.coreRemoteCallRequestService.selectDetail(id);
    }
}
