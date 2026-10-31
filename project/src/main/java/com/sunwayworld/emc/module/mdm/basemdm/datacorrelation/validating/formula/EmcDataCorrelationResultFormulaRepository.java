package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.validating.formula;

import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationDTO;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

public class EmcDataCorrelationResultFormulaRepository {

    /**
     * 获取某一样品下、某一测试下、某一分析项、重复数为1的样品结果<br>
     * args[0]：样品名称（必填）<br>
     * args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
     * args[2]：样品编号（选填，默认是当前样品结果的样品编号）
     * args[3]：要取值的列名（选填，默认是报出结果字段orginalResult，不区分大小写）
     */
    public static String getResultRN(String... args) {

        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        String propertyName = args.length > 3 ? args[3] : "reportResult";

        if (taskResultList.isEmpty()) {
            return null;
        } else {
            EmcOrderTaskResultBean orderTaskResult = taskResultList.get(0);
            if (ObjectUtils.isEmpty(BeanUtils.getPropertyValue(orderTaskResult, propertyName))) {
                return null;
            }
            
            String propertyValue = BeanUtils.getPropertyValue(orderTaskResult, propertyName);
            // 报出结果为ND返回检出低限/2
            if ("ND".equals(propertyValue)) {
                propertyValue = ObjectUtils.toString(ArithUtils.div(NumberUtils.parseDouble(orderTaskResult.getLowLimit()), 2d));
            }
            // 报出结果为HD返回检出高限*2
            if ("HD".equals(propertyValue)) {
                propertyValue = ObjectUtils.toString(ArithUtils.mul(NumberUtils.parseDouble(orderTaskResult.getHighLimit()), 2d));
            }
            
            return propertyValue;
        }
    }

    // -----------------------------------------------------------------------
    // 私有方法
    // -----------------------------------------------------------------------
    private static List<EmcOrderTaskResultBean> getTaskResultList(String... args) {
        if (args.length == 0) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_ITEMNAME");
        }

        if (StringUtils.isEmpty(args[0])) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_ITEMNAME");
        }

        EmcDataCorrelationDTO dataCorrelationDTO = GikamComputingDataHolder.getCurrentData();

        List<EmcOrderTaskResultBean> orderTaskResultList = dataCorrelationDTO.getOrderTaskResultList();

        String itemName = args[0];
        Long testId = (args.length > 1 && NumberUtils.isNumber(args[1])) ? NumberUtils.parseLong(args[1]) : orderTaskResultList.get(0).getTestId();
        String orderNo = orderTaskResultList.get(0).getOrderNo();

        return orderTaskResultList.stream().filter(r -> ObjectUtils.equals(testId, r.getTestId()) && StringUtils.equals(orderNo, r.getOrderNo()) && StringUtils.equals(itemName, r.getItemName())).collect(Collectors.toList());
    }
}
