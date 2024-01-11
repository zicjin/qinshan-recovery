package com.sunwayworld.emc.module.bp.mobile.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileFileDTO;
import com.sunwayworld.emc.module.bp.mobile.resource.EmcMobileProjectResource;
import com.sunwayworld.emc.module.bp.mobile.service.EmcMobileProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
@GikamBean
@RestController
@LogModule(value = "平板现场采样")
public class EmcMobileProjectResourceImpl implements EmcMobileProjectResource {

    @Autowired
    private EmcMobileProjectService mobileProjectService;

    @Override
    public EmcMobileDTO selectProjectListByCond(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectProjectListByCond(wrapper);
    }

    @Override
    public EmcMobileDTO selectProjectData(String id, RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectProjectData(id, wrapper);
    }

    @Override
    public EmcMobileDTO selectOperationTypeByCond(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectOperationTypeByCond(wrapper);
    }

    @Override
    public EmcMobileDTO selectStaticDataByCond(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectStaticDataByCond(wrapper);
    }

    @Override
    public EmcMobileDTO uploadProjectInfo(RestJsonWrapperBean wrapper) {
        return mobileProjectService.uploadProjectInfo(wrapper);
    }

    @Override
    public EmcMobileDTO uploadProjectFile(EmcMobileFileDTO mobileFile, MultipartFile file) {
        return mobileProjectService.uploadProjectFile(mobileFile, file);
    }

    @Override
    public Page<EmcSampleBean> selectMobileScanner(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectMobileScanner(wrapper);
    }

    @Override
    public Page<EmcSampleBean> selectDetailLabel(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectDetailLabel(wrapper);
    }

    @Override
    public Page<EmcSampleBean> selectLabelDetail(RestJsonWrapperBean wrapper) {
        return mobileProjectService.selectLabelDetail(wrapper);
    }

    @Override
    public String updateSannerByLabelId(RestJsonWrapperBean wrapper) {
        return mobileProjectService.updateSannerByLabelId(wrapper);
    }

    @Override
    public void updateRemarkByLabelId(RestJsonWrapperBean wrapper) {
        mobileProjectService.updateRemarkByLabelId(wrapper);
    }
}
