package com.sunwayworld.emc.module.bp.bp.resource.impl;

import com.sunwayworld.emc.module.bp.bp.resource.PfOrderTaskResultResource;
import com.sunwayworld.emc.module.bp.bp.resource.impl.EmcOrderTaskResultResourceImpl;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class PfOrderTaskResultResourceImpl
extends EmcOrderTaskResultResourceImpl
implements PfOrderTaskResultResource {
    @Autowired
    private PfOrderTaskResultService orderTaskResultService;

    @Log(value="\u6837\u54c1\u79f0\u91cd-\u7ed3\u679c\u5f55\u5165\u8868\u5934\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/result-inputs/table-heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectResultInputResultTitleList(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectResultInputResultTitleList(wrapper);
    }

    @Log(value="\u6837\u54c1\u79f0\u91cd-\u7ed3\u679c\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/result-inputs/table-rows/queries"}, method={RequestMethod.POST})
    public List<TableRowBean> selectResultInputResultRowList(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectResultInputResultRowList(wrapper);
    }

    @Log(value="\u6837\u54c1\u79f0\u91cd-\u7ed3\u679c\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/qcnames/queries"}, method={RequestMethod.POST})
    public List<String> selectQcNames(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQcNames(wrapper);
    }

    @Log(value="\u66f4\u65b0\u6837\u54c1\u79f0\u91cd\u5206\u6790\u9879\u7ed3\u679c", type=LogType.UPDATE)
    @RequestMapping(value={"/action/update-weighing-item"}, method={RequestMethod.POST})
    public void updateWeighingItemResult(RestJsonWrapperBean wrapper) {
        this.orderTaskResultService.updateWeighingItemResult(wrapper);
    }

    @Log(value="\u89e6\u53d1\u8ba1\u7b97", type=LogType.UPDATE)
    @RequestMapping(value={"/action/trigger-compute-no-complete"}, method={RequestMethod.POST})
    public void triggerComputeNoComplete(RestJsonWrapperBean wrapper) {
        this.orderTaskResultService.triggerComputeNoComplete(wrapper);
    }

    @Log(value="\u5206\u6790\u5f55\u5165-\u5feb\u901f\u5f55\u5165\u8868\u5934\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/quick-inputs/table-heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectQuickInputResultTitleList(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputResultTitleList(wrapper);
    }

    @Log(value="\u5206\u6790\u5f55\u5165-\u5feb\u901f\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/quick-inputs/table-rows/queries"}, method={RequestMethod.POST})
    public List<TableRowBean> selectQuickInputResultRowList(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputResultRowList(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u5feb\u901f\u5f55\u5165\u8868\u5934\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/quick-inputs-sample/table-heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectQuickInputSampleResultTitleList(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputSampleResultTitleList(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u5feb\u901f\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/quick-inputs-sample/table-rows/queries"}, method={RequestMethod.POST})
    public List<TableRowBean> selectQuickInputSampleResultRowList(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputSampleResultRowList(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u6c14\u53c2\u6570\u5f55\u5165\u8868\u5934\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/gas-parameter/table-heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectQuickInputResultTitleListBySampleParam(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputResultTitleListBySampleParam(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u6c14\u53c2\u6570\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/gas-parameter/table-rows/queries"}, method={RequestMethod.POST})
    public List<TableRowBean> selectQuickInputResultRowListBySampleParam(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputResultRowListBySampleParam(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u6c14\u53c2\u6570\u7ed3\u679c\u5f55\u5165\u8868\u5934\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/gas-parameter-result/table-heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectQuickInputResultTitleListBySampleId(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputResultTitleListBySampleId(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u6c14\u53c2\u6570\u7ed3\u679c\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/gas-parameter-result/table-rows/queries"}, method={RequestMethod.POST})
    public List<TableRowBean> selectQuickInputResultRowListBySampleId(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectQuickInputResultRowListBySampleId(wrapper);
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u6279\u5f55\u5165\u6c14\u53c2\u6570\u7ed3\u679c", type=LogType.UPDATE)
    @RequestMapping(value={"/action/batch-input-gas-parameter"}, method={RequestMethod.POST})
    public void batchInputGasParameter(RestJsonWrapperBean wrapper) {
        this.orderTaskResultService.batchInputGasParameter(wrapper);
    }

    @Log(value="\u6837\u54c1\u79f0\u91cd-\u6570\u636e\u5f55\u5165\u8868\u5934\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/preprocessing-result/table-heads/queries"}, method={RequestMethod.POST})
    public List<TableHeadBean> selectPreprocessingQuickInputResultTitleListByProjIds(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectPreprocessingQuickInputResultTitleListByProjIds(wrapper);
    }

    @Log(value="\u6837\u54c1\u79f0\u91cd-\u6570\u636e\u5f55\u5165\u6570\u636e\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/preprocessing-result/table-rows/queries"}, method={RequestMethod.POST})
    public List<TableRowBean> selectPreprocessingQuickInputResultRowListByProjIds(RestJsonWrapperBean wrapper) {
        return this.orderTaskResultService.selectPreprocessingQuickInputResultRowListByProjIds(wrapper);
    }
}
