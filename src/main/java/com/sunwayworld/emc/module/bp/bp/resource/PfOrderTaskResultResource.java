package com.sunwayworld.emc.module.bp.bp.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 13:58
 */
@RequestMapping("/secure/emc/module/bp/bp/order-task-results")
public interface PfOrderTaskResultResource extends EmcOrderTaskResultResource {

    List<TableHeadBean> selectResultInputResultTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectResultInputResultRowList(RestJsonWrapperBean wrapper);

    void updateWeighingItemResult(RestJsonWrapperBean wrapper);

    void triggerComputeNoComplete(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputResultTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputResultRowList(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputSampleResultTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputSampleResultRowList(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputResultTitleListBySampleParam(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputResultRowListBySampleParam(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputResultTitleListBySampleId(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputResultRowListBySampleId(RestJsonWrapperBean wrapper);

    void batchInputGasParameter(RestJsonWrapperBean wrapper);
}
