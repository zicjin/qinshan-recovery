package com.sunwayworld.emc.module.bp.report.report.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.collections4.ListUtils;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportLineBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportLineService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2023年11月29日
 */
@Repository
public class PfGenerateReportResultValidator implements DataValidator {

    @Autowired
    private EmcReportLineService reportLineService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        List<EmcReportLineBean> reportLineList = reportLineService.getDao().selectListByOneColumnValue(report.getId(), "reportId");

        // 查询需要在报告中展示的样品结果数据
        List<Long> orderTaskIdList = reportLineList.stream().map(EmcReportLineBean::getOrderTaskId).distinct().collect(Collectors.toList());
        if (orderTaskIdList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.REPORT.REPORTS.TIP.REPORT_HAS_NO_TASK");
            return false;
        }
        // 过滤掉分析录入合并的测试
        List<List<Long>> partitions = ListUtils.partition(orderTaskIdList, 200);
        List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();
        IntStream.range(0, partitions.size()).forEach(index -> {
            List<Long> partition = partitions.get(index);
            List<EmcOrderTaskBean> tasks = orderTaskService.getDao().selectListByOneColumnValues(partition, "id", Arrays.asList("id", "virtual"));
            orderTaskList.addAll(tasks);
        });
        List<Long> validOrderTaskIdList = orderTaskList.stream().filter(task -> !StringUtils.equals(task.getExt$Item("virtual"), Constant.YES)).map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        if (validOrderTaskIdList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.REPORT.REPORTS.TIP.REPORT_HAS_NO_TASK");
            return false;
        }

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                .match("orderTaskId", validOrderTaskIdList).filter(MatchPattern.OR)
                .match("REPORTDISPLAY", Constant.YES).filter(MatchPattern.SEQ)
                .match("RESULTSTATUS", EmcBizConstant.ORDERTASKRESULT_STATUS_DONE).filter(MatchPattern.SEQ));

        if (orderTaskResultList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_ITEM_DISPLAY_IN_REPORT");
            return false;
        }

        return true;
    }
}
