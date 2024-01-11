package com.sunwayworld.emc.module.bp.order.orderretain.service;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@GikamBean
public interface EmcOrderRetainService extends GenericService<EmcOrderRetainBean, Long>, GenericAuditableService<EmcOrderRetainBean, Long> {
    void retainOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderRetainOverdueBean> selectOverdueOrderRetain(RestJsonWrapperBean wrapper);
}
