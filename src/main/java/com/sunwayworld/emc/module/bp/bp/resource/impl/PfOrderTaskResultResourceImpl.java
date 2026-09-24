package com.sunwayworld.emc.module.bp.bp.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.resource.PfOrderTaskResultResource;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 13:59
 */
@RestController
@GikamBean
public class PfOrderTaskResultResourceImpl extends EmcOrderTaskResultResourceImpl implements PfOrderTaskResultResource {

    @Autowired
    private PfOrderTaskResultService orderTaskResultService;

    @Override
    @Log(value = "样品称重-结果录入表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/result-inputs/table-heads/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectResultInputResultTitleList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectResultInputResultTitleList(wrapper);
    }

    @Override
    @Log(value = "样品称重-结果录入数据列表", type = LogType.SELECT)
    @RequestMapping(value = "/result-inputs/table-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectResultInputResultRowList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectResultInputResultRowList(wrapper);
    }

    @Override
    @Log(value = "更新样品称重分析项结果", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-weighing-item", method = RequestMethod.POST)
    public void updateWeighingItemResult(RestJsonWrapperBean wrapper) {
        orderTaskResultService.updateWeighingItemResult(wrapper);
    }

    @Override
    @Log(value = "触发计算", type = LogType.UPDATE)
    @RequestMapping(value = "/action/trigger-compute-no-complete", method = RequestMethod.POST)
    public void triggerComputeNoComplete(RestJsonWrapperBean wrapper) {
        orderTaskResultService.triggerComputeNoComplete(wrapper);
    }

    @Override
    @Log(value = "分析录入-快速录入表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/quick-inputs/table-heads/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectQuickInputResultTitleList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputResultTitleList(wrapper);
    }

    @Override
    @Log(value = "分析录入-快速录入数据列表", type = LogType.SELECT)
    @RequestMapping(value = "/quick-inputs/table-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectQuickInputResultRowList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputResultRowList(wrapper);
    }

    @Override
    @Log(value = "现场采样-快速录入表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/quick-inputs-sample/table-heads/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectQuickInputSampleResultTitleList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputSampleResultTitleList(wrapper);
    }

    @Override
    @Log(value = "现场采样-快速录入数据列表", type = LogType.SELECT)
    @RequestMapping(value = "/quick-inputs-sample/table-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectQuickInputSampleResultRowList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputSampleResultRowList(wrapper);
    }

    @Override
    @Log(value = "现场采样-气参数录入表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/gas-parameter/table-heads/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectQuickInputResultTitleListBySampleParam(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputResultTitleListBySampleParam(wrapper);
    }

    @Override
    @Log(value = "现场采样-气参数录入数据列表", type = LogType.SELECT)
    @RequestMapping(value = "/gas-parameter/table-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectQuickInputResultRowListBySampleParam(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputResultRowListBySampleParam(wrapper);
    }

    @Override
    @Log(value = "现场采样-气参数结果录入表头列表", type = LogType.SELECT)
    @RequestMapping(value = "/gas-parameter-result/table-heads/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectQuickInputResultTitleListBySampleId(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputResultTitleListBySampleId(wrapper);
    }

    @Override
    @Log(value = "现场采样-气参数结果录入数据列表", type = LogType.SELECT)
    @RequestMapping(value = "/gas-parameter-result/table-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectQuickInputResultRowListBySampleId(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectQuickInputResultRowListBySampleId(wrapper);
    }

    @Override
    @Log(value = "气参数批量录入", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-input-gas-parameter", method = RequestMethod.POST)
    public void batchInputGasParameter(RestJsonWrapperBean wrapper) {
        orderTaskResultService.batchInputGasParameter(wrapper);
    }

}
