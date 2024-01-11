package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderTaskResultService extends GenericService<EmcOrderTaskResultBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    void reviseResult(EmcOrderTaskResultBean resultBean);

    void initTaskResult(Long id, RestJsonWrapperBean wrapper);

    void setDoneTaskResult(RestJsonWrapperBean wrapper);
    
    void setDoneOrder(RestJsonWrapperBean wrapper);

    void setDataInputDefaultTaskResult(Long id, RestJsonWrapperBean wrapper);

    void changeResult(RestJsonWrapperBean wrapper);

    void addRepeatNo(Long id, RestJsonWrapperBean wrapper);

    void deleteRepeatNo(RestJsonWrapperBean wrapper);

    void syncTmplInfo(RestJsonWrapperBean wrapper);

    void removeFomula(RestJsonWrapperBean wrapper);

    void setDefaultTaskResult(Long id, RestJsonWrapperBean wrapper);

    List<EmcOrderTaskResultBean> selectScriptListByFilter(SearchFilter filter);

    List<TableHeadBean> selectResultDataSummaryTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectResultDataSummaryRowList(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskResultBean> selectUpdateIndexOrderTaskResult(SearchFilter filter, RestJsonWrapperBean wrapper);

    void setDoneSampleOrder(RestJsonWrapperBean wrapper);
    
    void setReportResultValue(EmcOrderTaskResultBean resultBean, EmcItemBean item, List<EmcItemRevisionBean> itemRevisionList);

    Page<EmcOrderTaskResultBean> selectAnalysisProjectTaskResultValidation(RestJsonWrapperBean wrapper);
    
    void updateResult(RestJsonWrapperBean wrapper);
    
    List<TableHeadBean> selectDataQueryTitleList(RestJsonWrapperBean wrapper);
    
    Page<TableRowBean> selectDataQueryRowList(RestJsonWrapperBean wrapper);
}
