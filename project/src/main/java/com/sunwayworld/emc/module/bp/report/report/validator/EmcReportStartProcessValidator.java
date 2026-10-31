package com.sunwayworld.emc.module.bp.report.report.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author magh 2019年12月18日
 */
@Repository
public class EmcReportStartProcessValidator implements DataValidator {

    @Autowired
    private EmcReportService reportService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        List<EmcReportBean> reportList = wrapper.parse(EmcReportBean.class);
        List<Long> reportIdList = reportList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());

        reportList = reportService.selectListByFilter(SearchFilter.instance().match("ID", reportIdList).filter(MatchPattern.OR));
        List<Long> projIdList = reportList.stream().map(e -> e.getProjId()).distinct().collect(Collectors.toList());
        List<EmcReportBean> allReportList = reportService.selectListByFilter(SearchFilter.instance().match("PROJID", projIdList).filter(MatchPattern.OR));

        List<EmcReportBean> draftReportList = allReportList.stream().filter(e -> ObjectUtils.equals(EmcBizConstant.REPORT_STATUS_DRAFT, e.getStatus())).collect(Collectors.toList());

        if (!draftReportList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.REPORT.REPORTS.TIP.EXIST_DRAFT");
            return false;
        }

        return true;
    }

}
