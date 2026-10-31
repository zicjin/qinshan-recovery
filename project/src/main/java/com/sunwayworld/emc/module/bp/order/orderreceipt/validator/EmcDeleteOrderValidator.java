package com.sunwayworld.emc.module.bp.order.orderreceipt.validator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2020年09月23日
 */
@Repository
public class EmcDeleteOrderValidator implements DataValidator {

    @Autowired
    private EmcOrderService orderService;

    @SuppressWarnings("deprecation")
	@Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> deleteOrderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> deleteOrderList = orderService.getDao().selectListByIds(deleteOrderIdList, Arrays.asList("ID", "ORDERNO", "FOLDERID"));

        List<String> deleteOrderNoList = deleteOrderList.stream().map(EmcOrderBean::getOrderNo).collect(Collectors.toList());
        List<Long> folderIdList = deleteOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());

        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(SearchFilter.instance().match("FOLDERID", folderIdList).filter(MatchPattern.IN));
        parameter.put("sampleId", "empty");
        List<EmcOrderBean> existedOrderList = orderService.selectList(parameter);
        String minOrderNo = existedOrderList.stream().min(Comparator.comparing(EmcOrderBean::getOrderNo)).orElseGet(EmcOrderBean::new).getOrderNo();
        if (deleteOrderNoList.contains(minOrderNo)) {
            this.addConstraintViolation("EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.CANNOT_DELETE_MIN_ORDER");
            return false;
        }

        List<EmcOrderBean> allOrderBeanList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId");
        for (Long folderId : folderIdList) {
            List<EmcOrderBean> curOrderBeanList = allOrderBeanList.stream().filter(e -> ObjectUtils.equals(folderId, e.getFolderId())).collect(Collectors.toList());
            List<EmcOrderBean> otherOrderBeanList = curOrderBeanList.stream().filter(e -> !deleteOrderNoList.contains(e.getOrderNo())).collect(Collectors.toList());
            if (otherOrderBeanList.isEmpty()) {
                addConstraintViolation("不能删除点位下所有样品");
                return false;
            }
        }

        return true;
    }
}
