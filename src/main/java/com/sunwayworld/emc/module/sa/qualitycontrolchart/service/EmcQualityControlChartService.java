package com.sunwayworld.emc.module.sa.qualitycontrolchart.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcQualityControlChartService extends GenericService<EmcOrderTaskResultBean, Long> {

    Page<EmcOrderTaskResultBean> selectOrderTaskResultPagination(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskBean> selectTestSelectableList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskResultBean> selectItemSelectableList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskBean> selectQcCategorySelectableList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskBean> selectUserSelectableList(RestJsonWrapperBean wrapper);
}
