// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.bp.validator;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PfDeleteCheckProjectOrderItemGroupValidator
implements DataValidator {
    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderTaskBean> delOrderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<Long> deleteOrderItemGroupIdList = delOrderTaskList.stream().map(e -> NumberUtils.parseLong(e.getExt$Item("itemgroupid"))).distinct().collect(Collectors.toList());
        List<Long> deleteTestMethodIdList = delOrderTaskList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());
        Long orderId = delOrderTaskList.get(0).getOrderId();
        List<EmcOrderItemGroupBean> allOrderItemGroupBeanList = this.orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderId).filter(MatchPattern.SEQ).match("TestMethodId", deleteTestMethodIdList).filter(MatchPattern.OR), new Order[0]);
        for (Long tmId : deleteTestMethodIdList) {
            List<EmcOrderItemGroupBean> curOrderItemGroupBeanList = allOrderItemGroupBeanList.stream().filter(e -> ObjectUtils.equals(tmId, e.getTestMethodId())).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> otherOrderItemGroupBeanList = curOrderItemGroupBeanList.stream().filter(e -> !deleteOrderItemGroupIdList.contains(e.getItemGroupId())).collect(Collectors.toList());
            if (otherOrderItemGroupBeanList.isEmpty()) {
                this.addConstraintViolation("\u4e0d\u80fd\u5220\u9664\u6d4b\u8bd5\u4e0b\u6240\u6709\u5206\u6790\u9879\u7ec4\u3002\u5982\u8981\u5220\u9664\uff0c\u4f7f\u7528<\u5220\u9664\u6d4b\u8bd5>\u6309\u94ae\u3002");
                return false;
            }
        }
        return true;
    }
}
