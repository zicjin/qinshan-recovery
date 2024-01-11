package com.sunwayworld.emc.module.bp.order.orderretain.resource.impl;

import java.time.LocalDate;
import java.util.List;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.framework.data.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.resource.EmcOrderRetainResource;
import com.sunwayworld.emc.module.bp.order.orderretain.service.EmcOrderRetainService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@GikamBean
@RestController
@LogModule(value = "留样")
public class EmcOrderRetainResourceImpl implements EmcOrderRetainResource {

    @Autowired
    private EmcOrderRetainService orderRetainService;

    @Override
    public EmcOrderRetainService getService() {
        return orderRetainService;
    }

    @Override
    public void handleRetainOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderRetainBean> emcOrderRetainBeanList = wrapper.parse(EmcOrderRetainBean.class);
        String userId = LocalContextHelper.getLoginUserId();
        LocalDate handleDate = LocalDate.now();
        emcOrderRetainBeanList.forEach(bean -> {
            bean.setHandleUserId(userId);
            bean.setHandleDate(handleDate);
        });
        this.getService().updateIfChanged(emcOrderRetainBeanList);
    }

    @Override
    public void retainOrder(RestJsonWrapperBean wrapper) {
        this.getService().retainOrder(wrapper);
    }

    @Override
    public Page<EmcOrderRetainOverdueBean> selectOverdueOrderRetain(RestJsonWrapperBean wrapper) {
        return getService().selectOverdueOrderRetain(wrapper);
    }
}
