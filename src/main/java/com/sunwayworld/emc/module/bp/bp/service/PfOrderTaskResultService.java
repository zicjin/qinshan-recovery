package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 14:02
 */
public interface PfOrderTaskResultService extends EmcOrderTaskResultService {

    List<TableHeadBean> selectResultInputResultTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectResultInputResultRowList(RestJsonWrapperBean wrapper);

    void updateWeighingItemResult(RestJsonWrapperBean wrapper);

    void triggerComputeNoComplete(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputResultTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputResultRowList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskResultBean> selectResultRemind(MapperParameter mapperParameter);

    List<TableHeadBean> selectQuickInputSampleResultTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputSampleResultRowList(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputResultTitleListBySampleParam(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputResultRowListBySampleParam(RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQuickInputResultTitleListBySampleId(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQuickInputResultRowListBySampleId(RestJsonWrapperBean wrapper);
}
