package com.sunwayworld.emc.module.bp.bp.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

@RequestMapping("/secure/emc/module/bp/bp/order-task-results")
public interface EmcOrderTaskResultResource extends GenericResource<EmcOrderTaskResultService, EmcOrderTaskResultBean, Long> {

    void changeResult(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectResultDataSummaryTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectResultDataSummaryRowList(RestJsonWrapperBean wrapper);
    
    List<TableHeadBean> selectDataQueryTitleList(RestJsonWrapperBean wrapper);
    
    Page<TableRowBean> selectDataQueryRowList(RestJsonWrapperBean wrapper);

}
