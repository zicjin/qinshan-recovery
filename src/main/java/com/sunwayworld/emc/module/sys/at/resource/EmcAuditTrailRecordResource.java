package com.sunwayworld.emc.module.sys.at.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;

public interface EmcAuditTrailRecordResource {

    Page<CoreAuditTrailRecordBean> selectRecordPaginationList(RestJsonWrapperBean wrapper);
}
