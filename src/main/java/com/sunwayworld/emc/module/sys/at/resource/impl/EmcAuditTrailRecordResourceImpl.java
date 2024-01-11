package com.sunwayworld.emc.module.sys.at.resource.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.sys.at.resource.EmcAuditTrailRecordResource;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.resource.impl.CoreAuditTrailRecordResourceImpl;

@GikamBean
@RestController
public class EmcAuditTrailRecordResourceImpl extends CoreAuditTrailRecordResourceImpl implements EmcAuditTrailRecordResource {

    @Override
    @Log(value = "审计跟踪记录查询", type = LogType.SELECT)
    @RequestMapping(value = "/queries", method = RequestMethod.POST)
    public Page<CoreAuditTrailRecordBean> selectRecordPaginationList(RestJsonWrapperBean wrapper) {
        return getService().selectPagination(wrapper);
    }

}
