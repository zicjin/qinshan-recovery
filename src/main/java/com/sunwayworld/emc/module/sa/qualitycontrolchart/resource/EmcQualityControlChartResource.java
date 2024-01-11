package com.sunwayworld.emc.module.sa.qualitycontrolchart.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.service.EmcQualityControlChartService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/sa/quality-control-charts")
public interface EmcQualityControlChartResource extends GenericResource<EmcQualityControlChartService, EmcOrderTaskResultBean, Long> {

    Page<EmcOrderTaskResultBean> selectOrderTaskResultPagination(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskBean> selectTestSelectableList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskResultBean> selectItemSelectableList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskBean> selectQcCategorySelectableList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskBean> selectUserSelectableList(RestJsonWrapperBean wrapper);
}
