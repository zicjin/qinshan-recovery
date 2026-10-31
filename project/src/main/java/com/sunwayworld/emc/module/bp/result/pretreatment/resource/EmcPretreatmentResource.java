package com.sunwayworld.emc.module.bp.result.pretreatment.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcExtractMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPretreatmentBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPurifyMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcPretreatmentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/pretreatments")
public interface EmcPretreatmentResource extends GenericResource<EmcPretreatmentService, EmcPretreatmentBean, Long> {

    Page<EmcExtractMethodBean> queryExtractMethodByPagination(RestJsonWrapperBean wrapper);

    void addExtractMethod(RestJsonWrapperBean wrapper);

    void deleteExtractMethod(RestJsonWrapperBean wrapper);

    Page<EmcPurifyMethodBean> queryPurifyMethodByPagination(RestJsonWrapperBean wrapper);

    void addPurifyMethod(RestJsonWrapperBean wrapper);

    void deletePurifyMethod(RestJsonWrapperBean wrapper);

    void complete(RestJsonWrapperBean wrapper);

    void copyTo(RestJsonWrapperBean wrapper);
}
