package com.sunwayworld.emc.module.bp.result.pretreatment.service;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcExtractMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPretreatmentBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPurifyMethodBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
public interface EmcPretreatmentService extends GenericService<EmcPretreatmentBean, Long> {

    Page<EmcExtractMethodBean> queryExtractMethodByPagination(RestJsonWrapperBean wrapper);

    void addExtractMethod(RestJsonWrapperBean wrapper);

    void deleteExtractMethod(RestJsonWrapperBean wrapper);

    Page<EmcPurifyMethodBean> queryPurifyMethodByPagination(RestJsonWrapperBean wrapper);

    void addPurifyMethod(RestJsonWrapperBean wrapper);

    void deletePurifyMethod(RestJsonWrapperBean wrapper);

    void complete(RestJsonWrapperBean wrapper);

    void copyTo(RestJsonWrapperBean wrapper);
}
