package com.sunwayworld.emc.module.bp.mobile.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobilePaginationDTO;
import com.sunwayworld.emc.module.bp.mobile.resource.PfMobileProjectResource;
import com.sunwayworld.emc.module.bp.mobile.service.PfMobileProjectService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月02日
 */
@GikamBean
@RestController
@LogModule(value = "平板现场采样")
public class PfMobileProjectResourceImpl extends EmcMobileProjectResourceImpl implements PfMobileProjectResource {

    @Autowired
    private PfMobileProjectService mobileProjectService;

    @Override
    public EmcMobileDTO createFolder(RestJsonWrapperBean wrapper) {
        return mobileProjectService.createFolder(wrapper);
    }

    @Override
    public EmcMobileDTO createOrder(RestJsonWrapperBean wrapper) {
        return mobileProjectService.createOrder(wrapper);
    }

    @Override
    public EmcMobileDTO deleteFolder(RestJsonWrapperBean wrapper) {
        return mobileProjectService.deleteFolder(wrapper);
    }

    @Override
    public EmcMobileDTO deleteOrder(RestJsonWrapperBean wrapper) {
        return mobileProjectService.deleteOrder(wrapper);
    }

    @Override
    public EmcMobilePaginationDTO selectStaticDataByPagination(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectStaticDataByPagination(wrapper);
    }

    @Override
    public EmcMobilePaginationDTO selectProjectDataByPagination(Long id, RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectProjectDataByPagination(id, wrapper);
    }

    @Override
    public EmcMobileDTO createDelivery(RestJsonWrapperBean wrapper) {
        return mobileProjectService.createDelivery(wrapper);
    }
}
