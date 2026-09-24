package com.sunwayworld.emc.module.bp.project.projscheme.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointSchemeService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2021年02月07日
 */
@Repository
public class EmcInitSchemeValidator implements DataValidator {

    @Autowired
    private EmcMonitorPointSchemeService monitorPointSchemeService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> monitorPointIdList = schemeList.stream().map(EmcProjSchemeBean::getMonitorPointId).collect(Collectors.toList());
        List<EmcMonitorPointSchemeBean> monitorPointSchemeList = monitorPointSchemeService.selectListByFilter(SearchFilter.instance().match("MONITORPOINTID", monitorPointIdList).filter(MatchPattern.OR));

        List<Integer> repeatedList = new ArrayList<>();
        Map<Long, List<EmcMonitorPointSchemeBean>> schemeMap = monitorPointSchemeList.stream().collect(Collectors.groupingBy(EmcMonitorPointSchemeBean::getMonitorPointId));
        schemeMap.forEach((monitorPointId, pointSchemeList) -> {
            List<String> testIdList = pointSchemeList.stream().map(scheme -> scheme.getExt$Item("testId") + '$' + scheme.getItemGroupId()).collect(Collectors.toList());
            long uniqueCount = testIdList.stream().distinct().count();
            if (uniqueCount < testIdList.size()) {
                repeatedList.add(0);
            }
        });

        if (!repeatedList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.MONITOR_POINT_SCHEME_EXIST_DUPLICATE");
            return false;
        }

        return true;
    }
}
