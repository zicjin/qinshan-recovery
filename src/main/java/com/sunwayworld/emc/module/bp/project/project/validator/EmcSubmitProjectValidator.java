package com.sunwayworld.emc.module.bp.project.project.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author magh 2019年12月06日
 */
@Repository
public class EmcSubmitProjectValidator implements DataValidator {
    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcProjectService projectService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        List<Long> projectIdList = wrapper.parse(EmcProjectBean.class).stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcProjectBean> projectList = projectService.selectListByFilter(SearchFilter.instance().match("id", projectIdList).filter(MatchPattern.OR));

        List<Long> noSampleProjectIdList = projectList.stream().filter(e -> Constant.NO.equals(e.getSample())).map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(noSampleProjectIdList, "PROJID");

        for (Long projId : noSampleProjectIdList) {

            List<EmcOrderBean> projOrderList = orderList.stream().filter(e -> ObjectUtils.equals(projId, e.getProjId())).collect(Collectors.toList());

            if (projOrderList.isEmpty() || projOrderList.size() == 0) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ADD_ORDER_BEFORE_SUBMIT");
                return false;
            }
        }

        return true;
    }

}
