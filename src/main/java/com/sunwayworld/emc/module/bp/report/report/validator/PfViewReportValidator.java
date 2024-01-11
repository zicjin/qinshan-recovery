package com.sunwayworld.emc.module.bp.report.report.validator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年04月18日
 */
@Repository
public class PfViewReportValidator implements DataValidator {

    @Autowired
    private EmcReportResultService reportResultService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
        List<Long> reportIdList = reportList.stream().map(EmcReportBean::getId).collect(Collectors.toList());

        List<EmcReportResultBean> reportResultList = reportResultService.getDao().selectListByOneColumnValues(reportIdList, "reportId", Collections.singletonList("id"));
        if (reportResultList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.REPORT.REPORTS.TIP.PLEASE_GENERATE_RESULT_FIRST");
            return false;
        }

        return true;
    }
}
