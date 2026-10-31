package com.sunwayworld.emc.module.bp.mobile.service;

import org.springframework.web.multipart.MultipartFile;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileFileDTO;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
public interface EmcMobileProjectService {

    EmcMobileDTO selectProjectListByCond(RestJsonWrapperBean wrapper);

    EmcMobileDTO selectProjectData(String id, RestJsonWrapperBean wrapper);

    EmcMobileDTO selectOperationTypeByCond(RestJsonWrapperBean wrapper);

    EmcMobileDTO selectStaticDataByCond(RestJsonWrapperBean wrapper);

    EmcMobileDTO uploadProjectInfo(RestJsonWrapperBean wrapper);

    EmcMobileDTO uploadProjectFile(EmcMobileFileDTO mobileFile, MultipartFile file);

    Page<EmcSampleBean> selectMobileScanner(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectDetailLabel(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectLabelDetail(RestJsonWrapperBean wrapper);

    String updateSannerByLabelId(RestJsonWrapperBean wrapper);

    void updateRemarkByLabelId(RestJsonWrapperBean wrapper);

}
