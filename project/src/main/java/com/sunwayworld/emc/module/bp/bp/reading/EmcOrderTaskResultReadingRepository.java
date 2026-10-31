package com.sunwayworld.emc.module.bp.bp.reading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

public class EmcOrderTaskResultReadingRepository {

    public static List<EmcOrderTaskResultBean> getResultList(String... args) {

        if (args.length == 0) {
            throw new FormulaException("T_EMC_EQUIPT_ACQ_FILE.FORMULA.REQUIRE_ITEMNAME");
        }

        if (StringUtils.isEmpty(args[0])) {
            throw new FormulaException("T_EMC_EQUIPT_ACQ_FILE.FORMULA.REQUIRE_ITEMNAME");
        }
        if (StringUtils.isEmpty(args[1])) {
            throw new FormulaException("T_EMC_EQUIPT_ACQ_FILE.FORMULA.REQUIRE_BEGINROWINDEX");
        }
        if (StringUtils.isEmpty(args[2])) {
            throw new FormulaException("T_EMC_EQUIPT_ACQ_FILE.FORMULA.REQUIRE_ENDROWINDEX");
        }
        if (StringUtils.isEmpty(args[3])) {
            throw new FormulaException("T_EMC_EQUIPT_ACQ_FILE.FORMULA.REQUIRE_ORDERNOCOLUMNINDEX");
        }
        if (StringUtils.isEmpty(args[4])) {
            throw new FormulaException("T_EMC_EQUIPT_ACQ_FILE.FORMULA.REQUIRE_RESULTCOLUMNINDEX");
        }

        String itemName = args[0];

        int beginRowIndex = (args.length > 1 && NumberUtils.isNumber(args[1])) ? NumberUtils.parseInt(args[1]) : 0;
        int endRowIndex = (args.length > 2 && NumberUtils.isNumber(args[2])) ? NumberUtils.parseInt(args[2]) : 0;
        int orderNoColumnIndex = (args.length > 3 && NumberUtils.isNumber(args[3])) ? NumberUtils.parseInt(args[3]) : 0;
        int resultColumnIndex = (args.length > 4 && NumberUtils.isNumber(args[4])) ? NumberUtils.parseInt(args[4]) : 0;

        EmcEquiptAcqFileBean equiptAcqFile = (EmcEquiptAcqFileBean) GikamComputingDataHolder.get("equiptAcqFile");
        @SuppressWarnings("unchecked")
        List<EmcEquiptTestItemBean> equiptTestItemList = (List<EmcEquiptTestItemBean>) GikamComputingDataHolder.get("equiptTestItemList");
        @SuppressWarnings("unchecked")
        List<EmcOrderTaskResultBean> allOrderTaskResultList = (List<EmcOrderTaskResultBean>) GikamComputingDataHolder.get("orderTaskResultList");

        List<String> readValueList = Arrays.asList(StringUtils.split(equiptAcqFile.getContent(), "\n"));

        ArrayList<EmcOrderTaskResultBean> returnList = new ArrayList<EmcOrderTaskResultBean>();

        List<EmcEquiptTestItemBean> testMethodItemList = equiptTestItemList.stream().filter(e -> ObjectUtils.equals(e.getMatchItemName(), itemName)).collect(Collectors.toList());

        if (testMethodItemList != null && testMethodItemList.size() > 0) {
            for (int k = beginRowIndex; k <= endRowIndex; k++) {

                String dataRow = readValueList.get(k);
                String[] dataArray = StringUtils.split(dataRow.substring(0, dataRow.length()), " ");

                String orderNo = dataArray[orderNoColumnIndex];// 样品编号
                String result = dataArray[resultColumnIndex];// 分析项目结果

                for (int t = 0; t < testMethodItemList.size(); t++) {

                    EmcEquiptTestItemBean equiptTestItem = testMethodItemList.get(t);

                    Map<String, Object> orderTaskResultMap = new HashMap<String, Object>();
                    orderTaskResultMap.put("testMethodId", equiptTestItem.getTestMethodId());
                    orderTaskResultMap.put("itemId", equiptTestItem.getItemId());
                    orderTaskResultMap.put("orderNo", orderNo);

                    List<EmcOrderTaskResultBean> orderTaskResultList = allOrderTaskResultList.stream().filter(e -> BeanUtils.match(e, orderTaskResultMap)).collect(Collectors.toList());

                    if (orderTaskResultList != null && orderTaskResultList.size() > 0) {

                        for (int o = 0; o < orderTaskResultList.size(); o++) {
                            EmcOrderTaskResultBean orderTaskResult = orderTaskResultList.get(o);

                            orderTaskResult.setOriginalResult(result);
                            returnList.add(orderTaskResult);
                        }
                    }
                }
            }
        }
        return returnList;
    }
}
