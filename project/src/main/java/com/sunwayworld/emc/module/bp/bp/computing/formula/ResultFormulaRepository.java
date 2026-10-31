package com.sunwayworld.emc.module.bp.bp.computing.formula;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamFormulaHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.script.exeception.BshIgnoreException;
import com.sunwayworld.framework.utils.*;
import com.sunwayworld.module.sys.formula.annotation.Formula;

public class ResultFormulaRepository {

    /**
     * 匹配SQL语句中参数正则表达式
     */
    private static final Pattern ARG_PATTERN = Pattern.compile("#[A-Za-z]+\\w*#");

    /**
     * 数值区间正则表达式
     */
    private static final Pattern NUMERICAL_RANGE_PATTERN = Pattern.compile("[\\[|\\(]\\S+,\\S+[\\)|\\]]");

    /**
     * 获取当前处理中的样品结果
     */
    public static EmcOrderTaskResultBean getCurrent() {
        return GikamComputingDataHolder.getCurrentData();
    }

    /**
     * 获取一个样品、一个测试、一个分析项下的样品结果（匹配多条时选择一个）<br>
     * args[0]：样品名称（必填）<br>
     * args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
     * args[2]：样品编号（选填，默认是当前样品结果的样品编号）
     */
    @Formula(itemName = "args[0]")
    public static EmcOrderTaskResultBean get(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        if (taskResultList.isEmpty()) {
            return new EmcOrderTaskResultBean();
        } else {
            return taskResultList.get(0);
        }
    }

    /**
     * 获取某一样品下、某一测试下、某一分析项、重复数为1的样品结果<br>
     * args[0]：样品名称（必填）<br>
     * args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
     * args[2]：样品编号（选填，默认是当前样品结果的样品编号）
     * args[3]：要取值的列名（选填，默认是报出结果字段orginalResult，不区分大小写） args[4]: 是否忽略null值（选填
     * args[4]：默认值，当分析项不存在时的默认值，若未设置则默认为0
     */
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static String getResult(String... args) {
        List<EmcOrderTaskResultBean> filterTaskResultList = getFilterTaskResultList(args);

        List<EmcOrderTaskResultBean> taskResultList = filterTaskResultList.stream().filter(r -> ObjectUtils.equals(1L, r.getRepeatNo())).collect(Collectors.toList());

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 ? args[3] : "originalResult";

        String ignoreNull = args.length > 4 ? args[4] : "true";

        if (taskResultList.isEmpty()) {
            if ("false".equals(ignoreNull)) {
                return null;
            }
            throw new BshIgnoreException();
        } else {
            EmcOrderTaskResultBean orderTaskResult = taskResultList.get(0);
            Object propertyValue = BeanUtils.getPropertyValue(orderTaskResult, propertyName);
            // 当字段值为空，或者结果值为数字型但被手动置为无效结果时
            boolean ignoreFlag = ObjectUtils.isEmpty(propertyValue) || ("number".equals(orderTaskResult.getResultCategory()) && "/".equals(propertyValue));
            if (ignoreFlag) {
                if ("false".equals(ignoreNull)) {
                    return null;
                }
                throw new BshIgnoreException();
            }
            return BeanUtils.getPropertyValue(orderTaskResult, propertyName);
        }
    }

    /**
     * 获取某一样品下、某一测试下、某一分析项、重复数为1的样品结果<br>
     * args[0]：样品名称（必填）<br>
     * args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
     * args[2]：样品编号（选填，默认是当前样品结果的样品编号）
     * args[3]：要取值的列名（选填，默认是报出结果字段orginalResult，不区分大小写） args[4]: 是否忽略null值（选填
     * args[4]：是否忽略空值
     * args[5]: 不忽略空值时的默认值
     */
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static String getRN(String... args) {
        List<EmcOrderTaskResultBean> filterTaskResultList = getTaskResultList(args);

        List<EmcOrderTaskResultBean> taskResultList = filterTaskResultList.stream().filter(r -> ObjectUtils.equals(1L, r.getRepeatNo())).collect(Collectors.toList());

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 ? args[3] : "originalResult";

        String ignoreNull = args.length > 4 ? args[4] : "true";

        String defaultValue = args.length > 5 ? args[5] : null;

        if (taskResultList.isEmpty()) {
            if ("false".equals(ignoreNull)) {
                return defaultValue;
            }
            throw new BshIgnoreException();
        } else {
            EmcOrderTaskResultBean orderTaskResult = taskResultList.get(0);
            Object propertyValue = BeanUtils.getPropertyValue(orderTaskResult, propertyName);
            // 当字段值为空，或者结果值为数字型但被手动置为无效结果时
            boolean ignoreFlag = ObjectUtils.isEmpty(propertyValue) || ("number".equals(orderTaskResult.getResultCategory()) && "/".equals(propertyValue));
            if (ignoreFlag) {
                if ("false".equals(ignoreNull)) {
                    return defaultValue;
                }
                throw new BshIgnoreException();
            }
            return BeanUtils.getPropertyValue(orderTaskResult, propertyName);
        }
    }

    /**
     * 获取某一样品下、某一测试下、某一分析项、某一重复数的样品结果<br>
     * args[0]：样品名称（必填）<br>
     * args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
     * args[2]：样品编号（选填，默认是当前样品结果的样品编号）<br>
     * args[3]：重复数（选填，默认是当前样品结果的重复数）
     * args[4]：要取值的列名（选填，默认是报出结果字段orginalResult，不区分大小写）
     */
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur", repeatNo = "args[3]||cur")
    public static String getRR(String... args) {
        List<EmcOrderTaskResultBean> filterTaskResultList = getTaskResultList(args);

        EmcOrderTaskResultBean taskResult = GikamComputingDataHolder.getCurrentData();

        Long repeatNo = (args.length > 3 && NumberUtils.isNumber(args[3])) ? NumberUtils.parseLong(args[3]) : taskResult.getRepeatNo();

        List<EmcOrderTaskResultBean> taskResultList = filterTaskResultList.stream().filter(r -> ObjectUtils.equals(repeatNo, r.getRepeatNo())).collect(Collectors.toList());

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 4 && !StringUtils.isEmpty(args[4]) ? args[4] : "originalResult";

        if (taskResultList.isEmpty()) {
            throw new BshIgnoreException();
        } else {
            EmcOrderTaskResultBean orderTaskResult = taskResultList.get(0);
            if (ObjectUtils.isEmpty(BeanUtils.getPropertyValue(orderTaskResult, propertyName))) {
                throw new BshIgnoreException();
            }
            return BeanUtils.getPropertyValue(orderTaskResult, propertyName);
        }

    }

    /**
     * 获取当前样品结果的父样编号
     *
     */
    public static String getParentOrderNo() {
        return getCurrent().getParentOrderNo();
    }

    /**
     * 获取当前计算结果的父样编号
     *
     * @param roundtype
     *            修约类型，必输项。
     * @param picture
     *            修约长度，必输项。
     * @param value
     *            要修约的数，必输项。
     */
    public static String getRounding(String roundtype, String picture, String value) {
        return NumberUtils.rounding(value, roundtype + "," + picture);
    }

    /**
     * 获取当前批下某一分析项、某个QC类型的样品结果的均值<br>
     * args[0]：分析项（必填）<br>
     * args[1]：QC类型（选填，默认是L-BLANK）<br>
     * args[2]：要取值的列名（选填，默认是原始结果字段orginalResult，不区分大小写）
     * args[3]：测试编码（选填，默认是当前样品结果的测试编码）<br>
     */
    @Formula(itemName = "args[0]", testId = "args[3]||cur")
    public static Double getRunQCAvg(String... args) {

        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        String itemName = args[0];
        String qcCategoryCode = args.length > 1 && !StringUtils.isEmpty(args[1]) ? args[1] : "L-BLANK";
        String propertyName = args.length > 2 && !StringUtils.isEmpty(args[2]) ? args[2] : "originalResult";
        Long testId = (args.length > 3 && NumberUtils.isNumber(args[3])) ? NumberUtils.parseLong(args[3]) : currentTaskResult.getTestId();

        List<EmcOrderTaskResultBean> runOrderTaskResultList = EmcOrderTaskResultComputingEngine.getAllTaskResultList();

        // 曲线均值特殊处理
        if (ObjectUtils.equals(currentTaskResult.getQcCategoryCode(), "QC-CAL0'") || ObjectUtils.equals(currentTaskResult.getQcCategoryCode(), "QC-CAL0")) {
            List<EmcOrderTaskResultBean> calOrderTaskResultList = runOrderTaskResultList.stream().filter(r -> (ObjectUtils.equals(r.getQcCategoryCode(), currentTaskResult.getQcCategoryCode()))
                    && ObjectUtils.equals(r.getTestId(), testId) && StringUtils.equals(r.getAnalysisType(), "analysis") && ObjectUtils.equals(itemName, r.getItemName())).collect(Collectors.toList());
            Object finals = BeanUtils.getPropertyValue(calOrderTaskResultList.get(0), propertyName);
            return Double.valueOf(finals.toString());
        }

        List<EmcOrderTaskResultBean> filterTaskResultList = new ArrayList<>();
        if (runOrderTaskResultList.stream().allMatch(r -> "QC_CAL".equals(r.getExt$Item("setname")))) {
            filterTaskResultList = runOrderTaskResultList.stream().filter(r -> (ObjectUtils.equals(r.getQcCategoryCode(), "QC-CAL0'") || ObjectUtils.equals(r.getQcCategoryCode(), "QC-CAL0"))
                    && ObjectUtils.equals(r.getTestId(), testId) && StringUtils.equals(r.getAnalysisType(), "analysis") && ObjectUtils.equals(itemName, r.getItemName())).collect(Collectors.toList());
        } else {
            filterTaskResultList = runOrderTaskResultList.stream()
                    .filter(r -> ObjectUtils.equals(r.getQcCategoryCode(), qcCategoryCode) && ObjectUtils.equals(r.getTestId(), testId) && StringUtils.equals(r.getAnalysisType(), "analysis")
                            && !StringUtils.isEmpty(r.getRunId()) && ObjectUtils.equals(r.getRunId(), currentTaskResult.getRunId()) && ObjectUtils.equals(itemName, r.getItemName()))
                    .collect(Collectors.toList());
        }

        List<EmcOrderTaskResultBean> taskResultList = filterTaskResultList.stream().filter(e -> {
            Object propertyValue = BeanUtils.getPropertyValue(e, propertyName);
            return !ObjectUtils.isEmpty(propertyValue) && NumberUtils.isNumber(propertyValue.toString());
        }).collect(Collectors.toList());

        GikamFormulaHelper.setupRelevantData(taskResultList);

        if (taskResultList.size() == 0) {
            return 0D;
        }

        Double sum = 0d;
        for (EmcOrderTaskResultBean taskResult : taskResultList) {

            Object propertyValue = BeanUtils.getPropertyValue(taskResult, propertyName);
            if (NumberUtils.parseDouble(propertyValue) < 0) {
                propertyValue = "0";
            }
            sum = ArithUtils.add(sum.toString(), propertyValue.toString());

        }

        return ArithUtils.div(sum, taskResultList.size());
    }

    /**
     * 获取当前批下的指定QC类型的均值
     *
     * @param qcCategoryCode
     *            QC类型编码，必输项
     */
    @Formula(itemName = "cur", testId = "cur")
    public static Double getQCAvg(String qcCategoryCode) {

        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcOrderTaskResultBean> runOrderTaskResultList = EmcOrderTaskResultComputingEngine.getAllTaskResultList();

        List<EmcOrderTaskResultBean> taskResultList = runOrderTaskResultList.stream()
                .filter(r -> ObjectUtils.equals(r.getQcCategoryCode(), qcCategoryCode) && ObjectUtils.equals(r.getTestId(), currentTaskResult.getTestId())
                        && NumberUtils.isNumber(r.getOriginalResult()) && StringUtils.equals(r.getAnalysisType(), "analysis") && !StringUtils.isEmpty(r.getRunId())
                        && ObjectUtils.equals(r.getRunId(), currentTaskResult.getRunId()) && ObjectUtils.equals(r.getItemId(), currentTaskResult.getItemId()))
                .collect(Collectors.toList());

        GikamFormulaHelper.setupRelevantData(taskResultList);

        if (taskResultList.size() == 0) {
            return null;
        }

        Double sum = 0d;
        for (EmcOrderTaskResultBean taskResult : taskResultList) {
            String originalResult = taskResult.getOriginalResult();
            if (NumberUtils.parseDouble(originalResult) < 0d) {
                originalResult = "0";
            }
            sum = ArithUtils.add(sum.toString(), originalResult);
        }

        return ArithUtils.div(sum, taskResultList.size());
    }

    /**
     * 获取曲线的斜率截距信息
     *
     */
    public static EmcOrderTaskResultBean getRunsCurveInfo() {

        EmcOrderTaskResultBean taskResult = GikamComputingDataHolder.getCurrentData();

        if (StringUtils.isEmpty(taskResult.getRunId())) {
            return null;
        }

        EmcRunBean run = EmcOrderTaskResultComputingEngine.getRunList().stream().filter(e -> taskResult.getRunId().equals(e.getId())).findFirst().orElse(new EmcRunBean());

        if (StringUtils.isEmpty(run.getExt$Item("rn2"))) {
            return null;
        }

        EmcOrderTaskResultBean orderTaskResult = new EmcOrderTaskResultBean();

        orderTaskResult.setRn2(run.getExt$Item("rn2"));
        orderTaskResult.setRn3(run.getExt$Item("rn3"));
        orderTaskResult.setRn4(run.getExt$Item("rn4"));

        return orderTaskResult;
    }

    // 范围：只和当前计算结果有关

    // 直接计算，重写方法
    // 分析项：当前
    // 获取当前分析项对应的 EmcStockConcentBean

    public static void updateResultLowAndHighInfo() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcStockConcentBean> stockConcentList = EmcOrderTaskResultComputingEngine.getStockConcentList();

        // 匹配上批号和分析项的数据再进行计算
/*
        boolean flag = stockConcentList.stream().anyMatch(
                e -> StringUtils.equals(e.getExt$Item("batchno"), currentTaskResult.getExt$Item("stockbatchno")) && StringUtils.equals(e.getExt$Item("itemid"), currentTaskResult.getItemId() + ""));
*/

        // 计算的时候如果当前分析项的报出结果落在指标低限和指标高限之间，结果正常。否则结果表的状态为OOS。
   //     if (flag) {

            String reportResult = currentTaskResult.getReportResult();

            if (NumberUtils.isNumber(reportResult)) {
                double reportResultNum = NumberUtils.parseDouble(reportResult);

                if (!ObjectUtils.isEmpty(currentTaskResult.getIndexLowLimit())) {
                    if (reportResultNum < NumberUtils.parseDouble(currentTaskResult.getIndexLowLimit())) {
                        currentTaskResult.setResultStatus("OOS");
                    }
                }

                if (!ObjectUtils.isEmpty(currentTaskResult.getIndexHighLimit())) {
                    if (reportResultNum > NumberUtils.parseDouble(currentTaskResult.getIndexHighLimit())) {
                        currentTaskResult.setResultStatus("OOS");
                    }
                }

            }
        //}

    }

    /* 更新父样结果 */

    // 范围：当前结果及当前结果的父样结果 (根据parentId来筛选即可)

    // 分析项名称：当前
    // 测试编码：当前
    @Formula(itemId = "curr", testId = "cur")
    public static void updateParentOrderInfo() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcOrderTaskResultBean> runOrderTaskResultList = EmcOrderTaskResultComputingEngine.getAllTaskResultList();

        List<EmcOrderTaskBean> runOrderTaskList = EmcOrderTaskResultComputingEngine.getAllOrderTaskList();

        // 查找当前结果的样品测试ID, 然后找到父样品测试ID，通过父样品测试ID和当前分析项ID找到父样结果
        runOrderTaskList.stream().filter(t -> ObjectUtils.equals(t.getId(), currentTaskResult.getOrderTaskId())).findFirst().ifPresent(t -> {
            runOrderTaskResultList.stream().filter(r -> ObjectUtils.equals(r.getOrderTaskId(), t.getParentId()) && ObjectUtils.equals(currentTaskResult.getItemId(), r.getItemId())).findFirst()
                    .ifPresent(r -> {
                        // 父样结果以原始结果为准，当原始结果小于0时，直接设为0（于2020-10-17沟通后修改）
                        // 当QC类型为样品加标时，父样结果取原始结果，其他类型取报出结果（于2020-11-13沟通后修改）
                        if (EmcConstant.QCCATEGORY_QCCATGORYID_L_SPKC.equals(currentTaskResult.getQcCategoryCode())
                                || EmcConstant.QCCATEGORY_QCCATEGORYID_L_SPKCK.equals(currentTaskResult.getQcCategoryCode())) {
                            currentTaskResult.setRn4(NumberUtils.isNumber(r.getOriginalResult()) && NumberUtils.parseDouble(r.getOriginalResult()) < 0d ? "0" : r.getOriginalResult());
                            // 把父样均值设置到rn5字段
                            currentTaskResult.setRn5(r.getAvgResult());
                            // 把父样报出结果更新到RN16字段
                            currentTaskResult.setRn16(r.getReportResult());
                        } else {
                            currentTaskResult.setRn4(r.getReportResult());
                            // 把父样均值设置到rn5字段
                            currentTaskResult.setRn5(r.getAvgResult());
                        }
                    });
        });

    }

    /*
     * 更新实验室平行样的相对偏差和绝对偏差
     * 把abs(实验室平板当前分析项的值-父样结果)/(实验室平板当前分析项的值+父样结果)*100作为相对偏差，更新到RN7字段上
     * 把abs(实验室平板当前分析项的值-父样结果)作为绝对偏差更新到RN8字段上
     *
     */

    // 范围：当前结果及当前结果的父样结果 (根据parentId来筛选即可)

    // 分析项名称：当前
    // 测试编码：当前
    @Formula(itemId = "curr", testId = "cur")
    public static void updatePRAError() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        // 先判断父样结果是不是已更新到RN4字段,如果RN4字段有值，直接取RN4字段为父样结果,否则的话先执行updateParentOrderInfo方法，更新父样结果到RN4字段
        updateParentOrderInfo();

        String qcIndexDecisionCategorys = getQcIndexDecisionCategorys();
        List<EmcItemQcIndexBean> itemQcIndexList = EmcOrderTaskResultComputingEngine.getItemQcIndexList();

        //特殊处理：针对PH值、方法：HJ 962-2018。质控分析项为字符型，偏差和误差都赋值0,均值也为字符
        if(ObjectUtils.equals(currentTaskResult.getExt$Item("itemresultcategory"),"char") &&
                !NumberUtils.isNumber(currentTaskResult.getRn4()) &&
                ObjectUtils.equals(currentTaskResult.getExt$Item("methodname"),"HJ 962-2018")){
            if (qcIndexDecisionCategorys.contains("RN2")) {
                currentTaskResult.setRn2("0");
            }
            if (qcIndexDecisionCategorys.contains("RN7")) {
                currentTaskResult.setRn7("0");
            }
            if (qcIndexDecisionCategorys.contains("RN8")) {
                currentTaskResult.setRn8("0");
            }
            if (qcIndexDecisionCategorys.contains("RN9")) {
                currentTaskResult.setRn9("0");
            }
        }

        if (!ObjectUtils.isEmpty(currentTaskResult.getRn4()) && (NumberUtils.isNumber(currentTaskResult.getRn4()) || "ND".equals(currentTaskResult.getRn4()))
                && !ObjectUtils.isEmpty(currentTaskResult.getReportResult()) && (NumberUtils.isNumber(currentTaskResult.getReportResult()) || "ND".equals(currentTaskResult.getReportResult()))) {

            // 当结果值为ND或0时不做处理，直接赋值
            if ("ND".equals(currentTaskResult.getReportResult()) && "ND".equals(currentTaskResult.getRn4())
                    || 0d == NumberUtils.parseDouble(currentTaskResult.getReportResult(), 0d) && 0d == NumberUtils.parseDouble(currentTaskResult.getRn4())) {
                if (qcIndexDecisionCategorys.contains("RN7")) {
                    EmcItemQcIndexBean qcIndexBean = itemQcIndexList.stream().filter(i -> ObjectUtils.equals(i.getDecisionCategory(), "RN7") && ObjectUtils.equals(i.getItemId(), currentTaskResult.getItemId())).findFirst().orElseGet(EmcItemQcIndexBean::new);
                    currentTaskResult.setRn7("/");
                    currentTaskResult.setIndexHighLimit(qcIndexBean.getStdHighLimit());
                    currentTaskResult.setIndexLowLimit(qcIndexBean.getStdLowLimit());
                }
                if (qcIndexDecisionCategorys.contains("RN8")) {
                    currentTaskResult.setRn8("/");
                    EmcItemQcIndexBean qcIndexBean = itemQcIndexList.stream().filter(i -> ObjectUtils.equals(i.getDecisionCategory(), "RN7") && ObjectUtils.equals(i.getItemId(), currentTaskResult.getItemId())).findFirst().orElseGet(EmcItemQcIndexBean::new);
                    currentTaskResult.setIndexHighLimit(qcIndexBean.getStdHighLimit());
                    currentTaskResult.setIndexLowLimit(qcIndexBean.getStdLowLimit());
                }
            } else {
                // 当前分析项的报出结果，当值为ND时作为0处理
                String currentResult = getNdNumber(currentTaskResult.getReportResult());
                String parentResult = getNdNumber(currentTaskResult.getRn4());
                String avgResult = getNdNumber(currentTaskResult.getAvgResult());
                // 把abs(实验室平板当前分析项的值-父样结果)/(实验室平板当前分析项的值+父样结果)*100作为相对偏差，更新到RN7字段上
                double reportSum = ArithUtils.add(currentResult, parentResult);

                if (reportSum != 0) {
                    double rn7 = ArithUtils.mul(ArithUtils.div(Math.abs(ArithUtils.sub(currentResult, parentResult)), reportSum), 100d);
                    // 获取相对偏差修约规则
                    String rn11 = currentTaskResult.getRn11();
                    if (qcIndexDecisionCategorys.contains("RN7")) {
                        if (!StringUtils.isEmpty(rn11) && StringUtils.contains(rn11, ",")) {
                            currentTaskResult.setRn7(NumberUtils.rounding(rn7 + "", rn11));
                        } else {
                            currentTaskResult.setRn7(NumberUtils.rounding(rn7 + "", "FDA,1"));
                        }
                    }
                }

                // abs(实验室平板当前分析项的值-父样结果)作为绝对相差更新到RN8字段上
                double rn8 = Math.abs(ArithUtils.sub(currentResult, parentResult));
                // rn8 值不需要修约，当结果都是整数时，由于差值为 double类型，所以计算出的差值会存在 .0，此时需要取整
                int curRadix = getRadix(currentResult);
                int parRadix = getRadix(parentResult);
                boolean integer = (curRadix == 0) && (parRadix == 0);
                String rn8Value = integer ? NumberUtils.rounding(rn8 + "", "FDA,0") : rn8 + "";
                if (qcIndexDecisionCategorys.contains("RN8")) {
                    currentTaskResult.setRn8(rn8Value);
                }

                // abs(实验室平板当前分析项的值-均值)作为绝对偏差更新到RN2字段上
                if (NumberUtils.isNumber(avgResult)) {
                    double rn2 = Math.abs(ArithUtils.sub(currentResult, avgResult));
                    // rn2 值不需要修约，当结果都是整数时，由于差值为 double类型，所以计算出的差值会存在 .0，此时需要取整
                    int avgRadix = getRadix(avgResult);
                    boolean rn2Integer = (curRadix == 0) && (avgRadix == 0);
                    String rn2Value = rn2Integer ? NumberUtils.rounding(rn2 + "", "FDA,0") : rn2 + "";
                    if (qcIndexDecisionCategorys.contains("RN2")) {
                        currentTaskResult.setRn2(rn2Value);
                    }
                }

                // 更新绝对误差到RN1字段
                if (qcIndexDecisionCategorys.contains("RN1")) {
                    currentTaskResult.setRn1(rn8Value);
                }

                // 把(样品浓度-平行样浓度)/浓度平均值（样品和平行样的浓度平均值）*100作为相对相差，更新到RN9字段上
                double avg = NumberUtils.toDouble(avgResult);
                double rn9 = 0;
                if(avg!=0) {
                	 rn9 = ArithUtils.mul(ArithUtils.div(Math.abs(ArithUtils.sub(parentResult,currentResult)), avg), 100d);
                }

                if (qcIndexDecisionCategorys.contains("RN9")) {
                    currentTaskResult.setRn9(NumberUtils.rounding(rn9 + "", "FDA,1"));
                }
            }
        }
    }

    /**
     * 计算标准差
     */
    @Formula(itemId = "curr", testId = "cur")
    public static void updateStandardDeviation() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcOrderTaskBean> orderTaskList = EmcOrderTaskResultComputingEngine.getAllOrderTaskList();

        // 查找当前结果的样品测试，然后判断该样品测试是否有父样品测试ID
        Optional<EmcOrderTaskBean> currentOrderTask = orderTaskList.stream().filter(t -> ObjectUtils.equals(t.getId(), currentTaskResult.getOrderTaskId())).findFirst();
        if (!currentOrderTask.isPresent() || currentOrderTask.get().getParentId() == null) {
            return;
        }

        List<EmcOrderTaskResultBean> runOrderTaskResultList = EmcOrderTaskResultComputingEngine.getAllTaskResultList();

        Long currentOrderTaskParentId = currentOrderTask.get().getParentId();
        List<Long> runOrderTaskIdList = orderTaskList.stream()
                .filter(t -> ObjectUtils.equals(t.getId(), currentOrderTaskParentId)
                        || (ObjectUtils.equals(currentOrderTaskParentId, t.getParentId()) && StringUtils.equals(currentOrderTask.get().getQcCategoryCode(), t.getQcCategoryCode())))
                .map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> calTaskResultList = runOrderTaskResultList.stream()
                .filter(r -> runOrderTaskIdList.contains(r.getOrderTaskId()) && ObjectUtils.equals(currentTaskResult.getItemId(), r.getItemId())).collect(Collectors.toList());

        if (!calTaskResultList.isEmpty()) {
            List<EmcOrderTaskResultBean> childResultList = calTaskResultList.stream().filter(r -> StringUtils.equals(r.getQcCategoryCode(), currentTaskResult.getQcCategoryCode())).collect(Collectors.toList());
            boolean allNd = calTaskResultList.stream().anyMatch(r -> "ND".equals(r.getReportResult()));
            String avgResult = calTaskResultList.get(0).getAvgResult();
            if (allNd || !NumberUtils.isNumber(avgResult)) {
                childResultList.forEach(r -> r.setRn14("/"));
            } else {
                List<String> validResultList = calTaskResultList.stream().filter(r -> NumberUtils.isNumber(r.getReportResult())).map(EmcOrderTaskResultBean::getReportResult).collect(Collectors.toList());
                if (validResultList.isEmpty()) {
                    return;
                }

                double avgResultVal = NumberUtils.parseDouble(avgResult);
                double sumVal = validResultList.stream().mapToDouble(NumberUtils::parseDouble).map(r -> ArithUtils.sub(r, avgResultVal) * ArithUtils.sub(r, avgResultVal)).sum();
                double divVal = ArithUtils.div(sumVal, validResultList.size());
                String standardDeviation = String.valueOf(ArithUtils.sqrt(divVal, 2));
                String revisedValue = NumberUtils.rounding(standardDeviation, "FDA,2");
                childResultList.forEach(r -> r.setRn14(revisedValue));
            }
        }
    }

    /*
     * 计算均值 把实验室平行和他的父样的结果计算出均值，按照当前分析项的修约规则进行修约，然后存储到结果表实验室平行样和其父样的均值字段上
     */

    // 范围：当前结果的父样结果及所有子样结果 （id或者parentId等于当前结果的parentId的所有结果记录）

    // 分析项名称：当前
    // 测试编码：当前
    @Formula(itemId = "curr", testId = "cur")
    public static void updateCalcAvg() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcOrderTaskBean> orderTaskList = EmcOrderTaskResultComputingEngine.getAllOrderTaskList();

        // 查找当前结果的样品测试，然后判断该样品测试是否有父样品测试ID
        Optional<EmcOrderTaskBean> currentOrderTask = orderTaskList.stream().filter(t -> ObjectUtils.equals(t.getId(), currentTaskResult.getOrderTaskId())).findFirst();
        if (!currentOrderTask.isPresent() || currentOrderTask.get().getParentId() == null) {
            return;
        }

        List<EmcOrderTaskResultBean> runOrderTaskResultList = EmcOrderTaskResultComputingEngine.getAllTaskResultList();

        Long currentOrderTaskParentId = currentOrderTask.get().getParentId();
        List<Long> runOrderTaskIdList = orderTaskList.stream()
                .filter(t -> ObjectUtils.equals(t.getId(), currentOrderTaskParentId)
                        || (ObjectUtils.equals(currentOrderTaskParentId, t.getParentId()) && StringUtils.equals(currentOrderTask.get().getQcCategoryCode(), t.getQcCategoryCode())))
                .map(EmcOrderTaskBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> calTaskResultList = runOrderTaskResultList.stream()
                .filter(r -> runOrderTaskIdList.contains(r.getOrderTaskId()) && ObjectUtils.equals(currentTaskResult.getItemId(), r.getItemId())).collect(Collectors.toList());

        if (calTaskResultList.size() > 0) {

            String revisedAvgResult = "";

            // 如果结果都为ND,平均结果也为ND
            if (calTaskResultList.stream().allMatch(r -> "ND".equals(r.getReportResult()))) {
                revisedAvgResult = "ND";
            } else {
                Double sum = 0d;
                for (EmcOrderTaskResultBean taskResult : calTaskResultList) {
                    if (NumberUtils.isNumber(taskResult.getReportResult())) {
                        sum = ArithUtils.add(sum.toString(), taskResult.getReportResult());
                    }
                }
                // 按照当前分析项的修约规则进行修约
                // 修约之前先对数据进行格式化，防止对科学计数法数字修约了
                double avgResult = ArithUtils.div(sum, calTaskResultList.size());
                revisedAvgResult = getReviseValue(NumberUtils.formatNumber(avgResult));
            }

            // 对于PH测试，有些方法均值需要特殊处理，如果报出结果不是数字类型，则均值直接赋值为报出结果
            if (StringUtils.containsIgnoreCase(currentTaskResult.getExt$Item("testName"), "pH")) {
                List<String> specialMethodNameList = Arrays.asList("HJ 962-2018", "HJ 1147-2020", "GB/T 15555.12-1995");
                if (specialMethodNameList.contains(currentTaskResult.getExt$Item("methodName"))) {
                    if (!NumberUtils.isNumber(currentTaskResult.getReportResult())) {
                        revisedAvgResult = currentTaskResult.getReportResult();
                    } else {
                        double sumValue = calTaskResultList.stream().filter(r -> NumberUtils.isNumber(r.getReportResult())).map(EmcOrderTaskResultBean::getReportResult).mapToDouble(NumberUtils::parseDouble).map(r -> Math.pow(10, -r)).sum();
                        double divValue = ArithUtils.div(sumValue, calTaskResultList.size());
                        double avgResult = -Math.log10(divValue);
                        revisedAvgResult = getReviseValue(NumberUtils.formatNumber(avgResult));
                    }
                }
            }

            for (EmcOrderTaskResultBean updateTaskResult : calTaskResultList) {
                updateTaskResult.setAvgResult(revisedAvgResult);
            }
        }

    }

    /*
     * 标准设置加标回收率
     *
     * 用(当前样品的结果-父样结果)/标准物质量字段*100得出加标回收率，更新到RN6字段上
     */

    // 范围：当前结果及当前结果的父样结果 (根据parentId来筛选即可)

    // 分析项名称：当前
    // 测试编码：当前
    @Formula(itemId = "curr", testId = "cur")
    public static void updateAddBlankRecoveryOrderInfo() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        // 先判断父样结果是不是已更新到RN4字段,如果RN4字段有值，直接取RN4字段为父样结果,否则的话先执行updateParentOrderInfo方法，更新父样结果到RN4字段
        if (ObjectUtils.isEmpty(currentTaskResult.getRn4())) {
            updateParentOrderInfo();
        }

        if (!ObjectUtils.isEmpty(currentTaskResult.getRn4()) && (NumberUtils.isNumber(currentTaskResult.getRn4()) || "ND".equals(currentTaskResult.getRn4()))
                && !ObjectUtils.isEmpty(currentTaskResult.getReportResult()) && (NumberUtils.isNumber(currentTaskResult.getReportResult()) || "ND".equals(currentTaskResult.getReportResult()))
                && !ObjectUtils.isEmpty(currentTaskResult.getStandQty()) && NumberUtils.parseDouble(currentTaskResult.getStandQty()) != 0d) {

            // 当前分析项的报出结果，当值为ND时作为0处理
            String currentResult = getNdNumber(currentTaskResult.getReportResult());
            String parentResult = getNdNumber(currentTaskResult.getRn4());

            // 用(当前样品的结果-父样结果)/标准物质量字段*100得出加标回收率，更新到RN6字段上
            double rn6 = ArithUtils.mul(ArithUtils.div(ArithUtils.sub(currentResult, parentResult), NumberUtils.parseDouble(currentTaskResult.getStandQty())), 100);

            currentTaskResult.setRn6(NumberUtils.rounding(rn6 + "", "FDA,1"));

        }

    }

    /*
     * 标准设置根据QC指标判定rn值是否超标
     * 根据当前分析项，找到测试管理中维护的质控类型以及判断条件，判断结果是否在范围内，在范围内，状态正常，不在范围内把结果表状态改成OOS
     *
     */

    // 范围：只和当前结果有关

    // 分析项ID：当前，查询当前分析项对应的 EmcItemQcIndexBean
    public static void updateQcSpecification() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcItemQcIndexBean> itemQcIndexList = EmcOrderTaskResultComputingEngine.getItemQcIndexList();

        List<EmcItemQcIndexBean> filterItemQcIndexList = itemQcIndexList.stream().filter(e -> e.getItemId().equals(currentTaskResult.getItemId()) && !StringUtils.isEmpty(e.getDecisionCategory()))
                .collect(Collectors.toList());

        if (!ObjectUtils.isEmpty(currentTaskResult.getQcCategoryId()) && filterItemQcIndexList.size() > 0) {

            // 当前计算结果QC类型对应的样品指标
            List<EmcItemQcIndexBean> qcIndexList = new ArrayList<>();
            List<EmcItemQcIndexBean> allQcIndexList = filterItemQcIndexList.stream().filter(e -> currentTaskResult.getQcCategoryId().equals(e.getQcCategoryId())).collect(Collectors.toList());

            /*获取QC指标规则： 如果当前结果的qc类型和样品类型能够匹配到QC指标，
            就获取相匹配的指标。如果一条都匹配不到，且还存在未维护样品类型的指标。则采用未维护样品类型的指标作为qc指标 **/
            if(!ObjectUtils.isEmpty(currentTaskResult.getExt$Item("monitortmplid"))){
                //如果过滤样品类型之后，还能查得到qc指标，则使用该qc指标
                qcIndexList = allQcIndexList.stream().filter(e -> ObjectUtils.equals(e.getMonitorTmplId(),currentTaskResult.getExt$Item("monitortmplid")))
                        .collect(Collectors.toList());
            }

            //如果过滤样品类型之后，查不到qc指标，获取未维护样品类型的指标。使用该qc指标
            if(CollectionUtils.isEmpty(qcIndexList)){
                qcIndexList = allQcIndexList.stream().filter(e -> ObjectUtils.isEmpty(e.getMonitorTmplId()))
                        .collect(Collectors.toList());
            }

            String reportResultValue = currentTaskResult.getReportResult();
            if (qcIndexList.size() > 0 && !StringUtils.isEmpty(reportResultValue)) {
                reportResultValue = EmcConstant.ND.equals(reportResultValue) ? "0" : reportResultValue;
                if (!NumberUtils.isNumber(reportResultValue)) {
                    return;
                }

                double reportResult = NumberUtils.parseDouble(reportResultValue);

                // 判断结果是否在结果低限、结果高限之间，如果在的话取这条QC指标的标准低限和标准高限和判定类别字段对比

                for (EmcItemQcIndexBean itemQcIndex : qcIndexList) {

                    String resultLowLimit = itemQcIndex.getResultLowLimit();

                    String resultHighLimit = itemQcIndex.getResultHighLimit();

                    if (!StringUtils.isEmpty(resultLowLimit) && NumberUtils.isNumber(resultLowLimit)) {

                        if (reportResult < NumberUtils.parseDouble(resultLowLimit)) {
                            continue;
                        }

                    }

                    if (!StringUtils.isEmpty(resultHighLimit) && NumberUtils.isNumber(resultHighLimit)) {

                        if (reportResult >= NumberUtils.parseDouble(resultHighLimit)) {
                            continue;
                        }

                    }

                    String stdHighLimit = itemQcIndex.getStdHighLimit();
                    String stdLowLimit = itemQcIndex.getStdLowLimit();
                    currentTaskResult.setIndexLowLimit(stdLowLimit);
                    currentTaskResult.setIndexHighLimit(stdHighLimit);

                    // 判断对应的判断类别值是否在范围内，在范围内，状态正常，不在范围内把结果状态改成OOS

                    String propertyValue = BeanUtils.getPropertyValue(currentTaskResult, itemQcIndex.getDecisionCategory());

                    if (!StringUtils.isEmpty(propertyValue) && NumberUtils.isNumber(propertyValue)) {

                        double propertyValueNum = NumberUtils.parseDouble(propertyValue);

                        currentTaskResult.setQcDecisionCategory(itemQcIndex.getDecisionCategory());

                        if (!StringUtils.isEmpty(stdLowLimit) && NumberUtils.isNumber(stdLowLimit)) {

                            if (propertyValueNum < NumberUtils.parseDouble(stdLowLimit)) {
                                currentTaskResult.setResultStatus("OOS");
                                break;
                            }

                        }

                        if (!StringUtils.isEmpty(stdHighLimit) && NumberUtils.isNumber(stdHighLimit)) {

                            if (propertyValueNum > NumberUtils.parseDouble(stdHighLimit)) {
                                currentTaskResult.setResultStatus("OOS");
                                break;
                            }

                        }
                    }

                }

            }

        }
    }

    /*
     * 计算曲线，更新曲线的截距、斜率、相关系数
     * 用于QC-CAL计算曲线的计算代码，把斜率、截距、相关系数三个字段作为RN2，RN3，RN4更新到曲线的分析项上
     */

    // 范围：点位下分析项名称等于当前分析项名称、重复数等于当前重复数、测试编码等于当前测试编码、qc类型编码字段包含QC-CAL的样品结果记录

    // 分析项名称：当前
    // 测试编码：当前
    @Formula(itemId = "curr", testId = "cur", folder = true)
    public static void updateRunsCurveInfo() {

        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcOrderTaskResultBean> orderTaskResultList = EmcOrderTaskResultComputingEngine.getFolderTaskResultList();

        List<EmcOrderTaskResultBean> updateOrderTaskResultList = orderTaskResultList.stream()
                .filter(r -> ObjectUtils.equals(currentTaskResult.getItemName(), r.getItemName()) && ObjectUtils.equals(currentTaskResult.getTestId(), r.getTestId())
                        && r.getQcCategoryCode().contains("QC-CAL") && ObjectUtils.equals(currentTaskResult.getFolderId(), r.getFolderId())
                        && ObjectUtils.equals(currentTaskResult.getRepeatNo(), r.getRepeatNo()))
                .collect(Collectors.toList());

        updateOrderTaskResultList.forEach(e -> {
            e.setRn2(currentTaskResult.getRn2());
            e.setRn3(currentTaskResult.getRn3());
            e.setRn4(currentTaskResult.getRn4());
        });

    }

    // -------------------------------------------------------------------------------------------------
    // 获取在一个样品、一个测试、一个分析项下的样品结果相关的查询方法
    // args[0]：分析项名称（必填）<br>
    // args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
    // args[2]：样品编号（选填，默认是当前样品结果的样品编号）<br>
    // args[3]：要取值的列名（选填，默认是报出结果字段reportResult，不区分大小写）
    // -------------------------------------------------------------------------------------------------

    // 所有相关查询方法范围：同样品、同测试或者指定测试下、某个分析项名称的结果记录(getMin-getRsd)

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 样品：指定或当前
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static Double getMin(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        return taskResultList.stream().mapToDouble(r -> NumberUtils.parseDouble(BeanUtils.getPropertyValue(r, propertyName))).min().orElse(0d);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 样品：指定或当前
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static Double getMax(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        return taskResultList.stream().mapToDouble(r -> NumberUtils.parseDouble(BeanUtils.getPropertyValue(r, propertyName))).max().orElse(0d);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 样品：指定或当前
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static Double getAvg(String... args) {
        List<EmcOrderTaskResultBean> filterTaskResultList = getTaskResultList(args);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        List<EmcOrderTaskResultBean> taskResultList = filterTaskResultList.stream().filter(e -> {
            Object propertyValue = BeanUtils.getPropertyValue(e, propertyName);
            return !ObjectUtils.isEmpty(propertyValue) && NumberUtils.isNumber(propertyValue.toString());
        }).collect(Collectors.toList());

        GikamFormulaHelper.setupRelevantData(taskResultList);

        if (taskResultList.isEmpty()) {
            throw new BshIgnoreException();
        }

        Double sum = 0d;
        for (EmcOrderTaskResultBean taskResult : taskResultList) {
            sum = ArithUtils.add(sum.toString(), BeanUtils.getPropertyValue(taskResult, propertyName));
        }

        return ArithUtils.div(sum, taskResultList.size());
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 样品：指定或当前
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static Double getSum(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        Double sum = 0d;
        for (EmcOrderTaskResultBean taskResult : taskResultList) {
            sum = ArithUtils.add(sum.toString(), BeanUtils.getPropertyValue(taskResult, propertyName));
        }

        return sum;
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 样品：指定或当前
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static Double getSd(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        if (taskResultList.isEmpty()) {
            return 0d;
        }

        double avg = 0d;
        for (EmcOrderTaskResultBean result : taskResultList) {
            avg = ArithUtils.add(avg, NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName)));
        }
        avg = ArithUtils.div(avg, taskResultList.size());

        double sum = 0;
        for (EmcOrderTaskResultBean result : taskResultList) {
            double propertyValue = NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName)) - avg;

            sum = ArithUtils.add(sum, ArithUtils.mul(propertyValue, propertyValue));
        }
        double value = ArithUtils.div(sum, (taskResultList.size() - 1));

        return ArithUtils.sqrt(value, 2);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 样品：指定或当前
    @Formula(itemName = "args[0]", testId = "args[1]||cur", orderId = "args[2]||cur")
    public static Double getRsd(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultList(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        if (taskResultList.isEmpty()) {
            return 0d;
        }

        double avg = 0d;
        for (EmcOrderTaskResultBean result : taskResultList) {
            avg = ArithUtils.add(avg, NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName)));
        }
        avg = ArithUtils.div(avg, taskResultList.size());

        double sum = 0;
        for (EmcOrderTaskResultBean result : taskResultList) {
            double propertyValue = NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName));

            propertyValue = (propertyValue - avg);

            sum = ArithUtils.add(sum, ArithUtils.mul(propertyValue, propertyValue));
        }

        double value = ArithUtils.div(sum, (taskResultList.size() - 1));
        value = ArithUtils.sqrt(value, 2);
        return ArithUtils.div(value, avg);
    }

    // -------------------------------------------------------------------------------------------------
    // 在一个样品、一个测试、一个分析项下的样品结果下， 获取同一个点位、<br>
    // 同一周期频次下的分析项相关的查询方法
    // args[0]：样品名称（必填）<br>
    // args[1]：测试编码（选填，默认是当前样品结果的测试编码）<br>
    // args[2]：样品编号（选填，默认是当前样品结果的样品编号）<br>
    // args[3]：要取均值的列名（选填，默认是报出结果字段reportResult，不区分大小写）
    // -------------------------------------------------------------------------------------------------

    // 所有相关查询方法范围：同一个点位下、同测试或者指定测试下、同周期、同频次、某个分析项名称的结果记录(getMinByPointer-getRsdByPointer)

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getMinByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        return taskResultList.stream().mapToDouble(r -> NumberUtils.parseDouble(BeanUtils.getPropertyValue(r, propertyName))).min().orElse(0d);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getMaxByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        return taskResultList.stream().mapToDouble(r -> NumberUtils.parseDouble(BeanUtils.getPropertyValue(r, propertyName))).max().orElse(0d);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getAvgByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        GikamFormulaHelper.setupRelevantData(taskResultList);

        // 当前分析项指标名称为曲线校准、曲线校准减空白时，默认值为报出结果
        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3]
                : ("NUMERIC_CON".equals(currentTaskResult.getExt$Item("itemsetname")) || "NUMERIC_CON_NOB".equals(currentTaskResult.getExt$Item("itemsetname"))) ? "reportResult" : "originalResult";

        Double sum = 0d;
        int size = 0;
        for (EmcOrderTaskResultBean taskResult : taskResultList) {

            // 排除现场空白，现场空白不参与均值计算（2020-10-13）
            if (StringUtils.equals(EmcConstant.QCCATEGORY_QCCATEGORYID_P_BLANK, taskResult.getQcCategoryCode())) {
                continue;
            }

            String propertyValue = BeanUtils.getPropertyValue(taskResult, propertyName);

            if ("reportResult".equals(propertyName)) {

                // 报出结果为ND返回检出低限/2
                if ("ND".equals(propertyValue)) {
                    propertyValue = ObjectUtils.toString(ArithUtils.div(NumberUtils.parseDouble(taskResult.getLowLimit()), 2d));
                }

                // 报出结果为HD返回2*检出高限
                if ("HD".equals(propertyValue)) {
                    propertyValue = ObjectUtils.toString(ArithUtils.mul(NumberUtils.parseDouble(taskResult.getHighLimit()), 2d));
                }

            }

            if (!NumberUtils.isNumber(propertyValue)) {
                continue;
            }

            sum = ArithUtils.add(sum.toString(), propertyValue);
            size++;
        }

        if (size == 0) {
            throw new BshIgnoreException();
        }

        return ArithUtils.div(sum, size);
    }

    // 油烟类的均值需特殊处理，如果其中一个值小于最大值的1/4 那么这个值不参与均值计算

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getLampblackAvgByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        GikamFormulaHelper.setupRelevantData(taskResultList);

        // 当前分析项指标名称为曲线校准、曲线校准减空白时，默认值为报出结果
        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3]
                : ("NUMERIC_CON".equals(currentTaskResult.getExt$Item("itemsetname")) || "NUMERIC_CON_NOB".equals(currentTaskResult.getExt$Item("itemsetname"))) ? "reportResult" : "originalResult";

        Double sum = 0d;
        int size = 0;
        List<Double> propertyValueList = new ArrayList<Double>();

        for (EmcOrderTaskResultBean taskResult : taskResultList) {

            String propertyValue = BeanUtils.getPropertyValue(taskResult, propertyName);

            if ("reportResult".equals(propertyName)) {

                // 报出结果为ND返回检出低限/2
                if ("ND".equals(propertyValue)) {
                    propertyValue = ObjectUtils.toString(ArithUtils.div(NumberUtils.parseDouble(taskResult.getLowLimit()), 2d));
                }

                // 报出结果为HD返回2*检出高限
                if ("HD".equals(propertyValue)) {
                    propertyValue = ObjectUtils.toString(ArithUtils.mul(NumberUtils.parseDouble(taskResult.getHighLimit()), 2d));
                }

            }

            if (NumberUtils.isNumber(propertyValue)) {
                propertyValueList.add(NumberUtils.parseDouble(propertyValue));
            }

        }

        if (!propertyValueList.isEmpty()) {
            double maxValue = propertyValueList.stream().mapToDouble(e -> e.doubleValue()).max().getAsDouble();

            // 四分之一最大值
            double quarter = ArithUtils.div(maxValue, 4d);

            for (Double propertyValue : propertyValueList) {
                if (propertyValue.doubleValue() >= quarter) {
                    sum = ArithUtils.add(sum, propertyValue);
                    size++;
                }
            }

        }

        if (size == 0) {
            return null;
        }

        return ArithUtils.div(sum, size);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getSumByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        Double sum = 0d;
        for (EmcOrderTaskResultBean taskResult : taskResultList) {
            sum = ArithUtils.add(sum.toString(), BeanUtils.getPropertyValue(taskResult, propertyName));
        }

        return sum;
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getSdByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        if (taskResultList.isEmpty()) {
            return 0d;
        }

        double avg = 0d;
        for (EmcOrderTaskResultBean result : taskResultList) {
            avg = ArithUtils.add(avg, NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName)));
        }
        avg = ArithUtils.div(avg, taskResultList.size());

        double sum = 0;
        for (EmcOrderTaskResultBean result : taskResultList) {
            double propertyValue = NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName)) - avg;

            sum = ArithUtils.add(sum, ArithUtils.mul(propertyValue, propertyValue));
        }
        double value = ArithUtils.div(sum, (taskResultList.size() - 1));

        return ArithUtils.sqrt(value, 2);
    }

    // 分析项名称：指定
    // 测试编码：指定或当前
    // 跨点位
    @Formula(itemName = "args[0]", testId = "args[1]||cur", folder = true)
    public static Double getRsdByPointer(String... args) {
        List<EmcOrderTaskResultBean> taskResultList = getTaskResultListByPointer(args);

        GikamFormulaHelper.setupRelevantData(taskResultList);

        String propertyName = args.length > 3 && !StringUtils.isEmpty(args[3]) ? args[3] : "originalResult";

        if (taskResultList.isEmpty()) {
            return 0d;
        }

        double avg = 0d;
        for (EmcOrderTaskResultBean result : taskResultList) {
            avg = ArithUtils.add(avg, NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName)));
        }
        avg = ArithUtils.div(avg, taskResultList.size());

        double sum = 0;
        for (EmcOrderTaskResultBean result : taskResultList) {
            double propertyValue = NumberUtils.parseDouble(BeanUtils.getPropertyValue(result, propertyName));

            propertyValue = (propertyValue - avg);

            sum = ArithUtils.add(sum, ArithUtils.mul(propertyValue, propertyValue));
        }

        double value = ArithUtils.div(sum, (taskResultList.size() - 1));
        value = ArithUtils.sqrt(value, 2);
        return ArithUtils.div(value, avg);
    }

    /**
     * 根据值和样品结果修约
     */

    // 分析项ID：当前
    // 重写，根据分析项查询 EmcItemRevisionBean
    public static String getReviseValue(String value) {
        if (StringUtils.isEmpty(value) || !NumberUtils.isNumber(value)) {
            return value;
        }

        double valueNum = NumberUtils.parseDouble(value);

        EmcOrderTaskResultBean resultBean = GikamComputingDataHolder.getCurrentData();

        List<EmcItemRevisionBean> itemRevisionList = EmcOrderTaskResultComputingEngine.getItemRevisionList();

        // 过滤出对应的分段修约数据
        List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream().filter(e -> e.getItemId().equals(resultBean.getItemId())).collect(Collectors.toList());
        String revisionRule = resultBean.getRevisionRule();

        //如果是实验室标准样品且标准值单位和分析项单位一致，或标准值单位未维护时，按照标准值修约，否则按照分析项修约规则修约，标准值单位维护在rn12字段上
        boolean reviseWithStandQty = (PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(resultBean.getQcCategoryCode())
                || EmcConstant.QCCATEGORY_QCCATEGORYID_PQC_STD.equals(resultBean.getQcCategoryCode()))
                && NumberUtils.isNumber(resultBean.getStandQty())
                && (StringUtils.equals(resultBean.getRn12(), resultBean.getUnitName()) || StringUtils.isEmpty(resultBean.getRn12()));
        if (reviseWithStandQty) {
            int radix = getRadix(resultBean.getStandQty());

            return NumberUtils.rounding(value, "FDA," + radix);
        }

        // 处理分段修约：
        // 1、根据值查询他所在区间范围内的分段修约
        for (EmcItemRevisionBean e : filterItemRevisionList) {
            // 下限符号
            String lowLimitSymbol = e.getLowLimitSymbol();
            // 下限
            String lowLimit = e.getLowLimit();
            // 上限符号
            String highLimitSymbol = e.getHighLimitSymbol();
            // 上限
            String highLimit = e.getHighLimit();

            if (!StringUtils.equals("/", lowLimitSymbol) && !StringUtils.isEmpty(lowLimit) && NumberUtils.isNumber(lowLimit)) {

                double lowLimitNum = NumberUtils.parseDouble(lowLimit);

                if (StringUtils.equals(">", lowLimitSymbol)) {
                    if (valueNum <= lowLimitNum) {
                        continue;
                    }
                } else {
                    if (valueNum < lowLimitNum) {
                        continue;
                    }
                }

            }

            if (!StringUtils.equals("/", highLimitSymbol) && !StringUtils.isEmpty(highLimit) && NumberUtils.isNumber(highLimit)) {

                double highLimitNum = NumberUtils.parseDouble(highLimit);

                if (StringUtils.equals("<", highLimitSymbol)) {
                    if (valueNum >= highLimitNum) {
                        continue;
                    }
                } else {
                    if (valueNum > highLimitNum) {
                        continue;
                    }
                }

            }

            revisionRule = e.getRevisionRule();
        }


        String revisionValue = value;
        if (!StringUtils.isEmpty(revisionRule) && !EmcConstant.REVISION_RULE_NONE_DESC.equals(revisionRule)) {
            revisionValue = NumberUtils.rounding(revisionValue, revisionRule);
        }

        // 判断是否需要使用科学计数法，首先获取分析项上的范围
        String itemNumRange = resultBean.getExt$Item("nonSnRange");
        String systemDefaultNumRange = ApplicationContextHelper.getConstantValue("NON_SCIENTIFIC_NOTATION_RANGE");
        String numRange = StringUtils.isEmpty(itemNumRange) ? systemDefaultNumRange : itemNumRange;
        boolean scientificNotation = Constant.YES.equals(resultBean.getExt$Item("scientificNotation")) && isValidNumRange(numRange) && !inNumRange(revisionValue, numRange);

        String limitrevision = resultBean.getExt$Item("limitrevision");
        if (!StringUtils.isEmpty(limitrevision) && Constant.YES.equals(limitrevision) && !ObjectUtils.isEmpty(resultBean.getLowLimit())) {
            // 是否根据检出限修约为是, 判断检出低限是否为空
            String lowLimitStr = ObjectUtils.toString(resultBean.getLowLimit());

            int radix = getRadix(lowLimitStr);

            // 检出限修约值
            String lowLimitRevisionValue = NumberUtils.rounding(value, "FDA," + radix);
            // 比较检出限修约值和之前的计算修约值，取小数点位数小的值
            int isoRadix = getRadix(lowLimitRevisionValue);

            int revisionRadix = getRadix(revisionValue);
            if (isoRadix < revisionRadix) {
                if (scientificNotation) {
                    return NumberUtils.scientificNotationFormat(NumberUtils.parseDouble(lowLimitRevisionValue), isoRadix);
                }

                return lowLimitRevisionValue;
            }

        }

        if (scientificNotation) {
            // 如果有修约规则，再次对科学计数法进行修约
            boolean validRevisionRule = !StringUtils.isEmpty(revisionRule) && StringUtils.contains(revisionRule, ",");
            int precision = getRadix(revisionValue);
            if (validRevisionRule && NumberUtils.isNumber(StringUtils.split(revisionRule, ",")[1])) {
                precision = Math.max(precision, NumberUtils.parseInt(StringUtils.split(revisionRule, ",")[1]));
            }

            revisionValue = NumberUtils.scientificNotationFormat(NumberUtils.parseDouble(revisionValue), precision);

            if (validRevisionRule) {
                revisionValue = NumberUtils.rounding(revisionValue, revisionRule);
            }
        } else if (NumberUtils.isScientificNumber(revisionValue) && StringUtils.contains(revisionRule, ",")) {
            revisionValue = NumberUtils.rounding(NumberUtils.parseDouble(revisionValue) + "", revisionRule);
        }

        return revisionValue;
    }

    /**
     * 执行SQL来获取结果
     *
     * @param args
     *            args[0] : 具体需要执行的sql语句 args[>1] : sql语句中需要用到的参数，参数的写法为 #参数名#
     * @return
     */
    public static List<Map<String, Object>> executeSQL(String... args) {
        if (args.length == 0 || StringUtils.isEmpty(args[0])) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_SQL");
        }
        String sql = args[0];
        Matcher matcher = ARG_PATTERN.matcher(sql);
        List<String> argList = new ArrayList<>();
        while (matcher.find()) {
            argList.add(matcher.group());
        }

        for (int i = 0; i < argList.size(); i++) {
            sql = sql.replaceAll(argList.get(i), "'" + args[i + 1] + "'");
        }

        GlobalMapper globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);

        return globalMapper.selectList(sql);
    }

    /**
     *
     * @param args
 *           args[0] : 具体需要执行的sql语句在SQL管理模块的编码 args[>1] : sql语句中需要用到的参数，参数的写法为
     *            #参数名#
     * @return
     */
    public static List<Map<String, Object>> findAndExecuteSQL(String... args) {
        if (args.length == 0 || StringUtils.isEmpty(args[0])) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_SQL");
        }
        GlobalMapper globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);
        String sql = globalMapper.selectOneString("select content from t_emc_sql where sqlNo = '" + args[0] + "'");
        Matcher matcher = ARG_PATTERN.matcher(sql);
        List<String> argList = new ArrayList<>();
        while (matcher.find()) {
            argList.add(matcher.group());
        }

        for (int i = 0; i < argList.size(); i++) {
            sql = sql.replaceAll(argList.get(i), "'" + args[i + 1] + "'");
        }
        return globalMapper.selectList(sql);
    }

    public static String scientificNotationFormat(String value, String precision) {
        if (!NumberUtils.isNumber(value) || !NumberUtils.isNumber(precision)) {
            return value;
        }

        // 判断是否需要使用科学计数法，首先获取分析项上的范围
        String systemDefaultNumRange = ApplicationContextHelper.getConstantValue("NON_SCIENTIFIC_NOTATION_RANGE");
        boolean scientificNotation = isValidNumRange(systemDefaultNumRange) && !inNumRange(value, systemDefaultNumRange);

        if (scientificNotation) {
            return NumberUtils.scientificNotationFormat(NumberUtils.parseDouble(value), NumberUtils.parseInt(precision));
        }

        return value;
    }

    public static boolean isNum(String text) {
        return NumberUtils.isNumber(text);
    }

    public static double toNum(String text) {
        if ("ND".equals(text)) {
            text = "0";
        }
        return NumberUtils.parseDouble(text);
    }

    /**
     * 获取当前结果QC指标的判定类别集合
     */
    public static String getQcIndexDecisionCategorys() {
        EmcOrderTaskResultBean currentTaskResult = GikamComputingDataHolder.getCurrentData();

        List<EmcItemQcIndexBean> itemQcIndexList = EmcOrderTaskResultComputingEngine.getItemQcIndexList();

        List<EmcItemQcIndexBean> filterItemQcIndexList = itemQcIndexList.stream().filter(e -> e.getItemId().equals(currentTaskResult.getItemId()) && !StringUtils.isEmpty(e.getDecisionCategory()))
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(currentTaskResult.getQcCategoryId()) || filterItemQcIndexList.size() == 0) {
            return "";
        }

        List<EmcItemQcIndexBean> allQcIndexList = filterItemQcIndexList.stream().filter(e -> currentTaskResult.getQcCategoryId().equals(e.getQcCategoryId())).collect(Collectors.toList());

        List<String> decisionCategoryList = allQcIndexList.stream().map(e -> e.getDecisionCategory()).distinct().collect(Collectors.toList());

        return StringUtils.join(decisionCategoryList, ",");
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

        EmcOrderTaskResultBean taskResult = GikamComputingDataHolder.getCurrentData();

        String itemName = args[0];
        Long testId = (args.length > 1 && NumberUtils.isNumber(args[1])) ? NumberUtils.parseLong(args[1]) : taskResult.getTestId();
        String orderNo = args.length > 2 && !ObjectUtils.isEmpty(args[2]) ? Objects.toString(args[2], taskResult.getOrderNo()) : taskResult.getOrderNo();

        //如果orderNo是空，则证明当前分析项是气的采样参数。气的采样参数为空，这里则改为用orderId过滤分析项
        Long orderId = null;
        if(ObjectUtils.isEmpty(orderNo)){
            orderId = taskResult.getOrderId();
        }

        //如果orderNo不为空，则还是采用之前的逻辑，为空则用orderId过滤
        if(!ObjectUtils.isEmpty(orderNo)){
            if (StringUtils.equals(orderNo, taskResult.getOrderNo())) {
                List<EmcOrderTaskResultBean> orderTaskResultList = EmcOrderTaskResultComputingEngine.getFolderTaskResultList();

                return orderTaskResultList.stream().filter(r -> ObjectUtils.equals(itemName, r.getItemName()) && ObjectUtils.equals(testId, r.getTestId()) && ObjectUtils.equals(orderNo, r.getOrderNo()))
                        .collect(Collectors.toList());
            }

            return EmcOrderTaskResultComputingEngine.getAllTaskResultList().stream().filter(r -> ObjectUtils.equals(itemName, r.getItemName()) && ObjectUtils.equals(testId, r.getTestId()) && ObjectUtils.equals(orderNo, r.getOrderNo()))
                    .collect(Collectors.toList());
        }else{
            Long finalOrderId = orderId;
            if (ObjectUtils.equals(finalOrderId, taskResult.getOrderId())) {
                List<EmcOrderTaskResultBean> orderTaskResultList = EmcOrderTaskResultComputingEngine.getFolderTaskResultList();

                return orderTaskResultList.stream().filter(r -> ObjectUtils.equals(itemName, r.getItemName()) && ObjectUtils.equals(testId, r.getTestId()) && ObjectUtils.equals(finalOrderId, r.getOrderId()))
                        .collect(Collectors.toList());
            }

            return EmcOrderTaskResultComputingEngine.getAllTaskResultList().stream().filter(r -> ObjectUtils.equals(itemName, r.getItemName()) && ObjectUtils.equals(testId, r.getTestId()) && ObjectUtils.equals(finalOrderId, r.getOrderId()))
                    .collect(Collectors.toList());
        }
    }

    private static List<EmcOrderTaskResultBean> getFilterTaskResultList(String... args) {
        if (args.length == 0) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_ITEMNAME");
        }

        if (StringUtils.isEmpty(args[0])) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_ITEMNAME");
        }

        EmcOrderTaskResultBean taskResult = GikamComputingDataHolder.getCurrentData();

        String itemName = args[0];
        Long testId = (args.length > 1 && NumberUtils.isNumber(args[1])) ? NumberUtils.parseLong(args[1]) : taskResult.getTestId();
        String orderNo = args.length > 2 && !ObjectUtils.isEmpty(args[2]) ? Objects.toString(args[2], taskResult.getOrderNo()) : taskResult.getOrderNo();

        return EmcOrderTaskResultComputingEngine.getAllTaskResultList().stream().filter(r -> ObjectUtils.equals(itemName, r.getItemName()) && ObjectUtils.equals(testId, r.getTestId()) && ObjectUtils.equals(orderNo, r.getOrderNo()))
                .collect(Collectors.toList());
    }

    /**
     * 在一个样品、一个测试、一个分析项下的样品结果， 获取同一个点位、同一周期频次下的样品结果
     */
    private static List<EmcOrderTaskResultBean> getTaskResultListByPointer(String... args) {
        if (args.length == 0) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_ITEMNAME");
        }

        if (StringUtils.isEmpty(args[0])) {
            throw new FormulaException("T_EMC_ORDER_TASK_RESULT.FORMULA.REQUIRE_ITEMNAME");
        }

        EmcOrderTaskResultBean taskResult = GikamComputingDataHolder.getCurrentData();

        String itemName = args[0];
        Long testId = (args.length > 1 && NumberUtils.isNumber(args[1])) ? NumberUtils.parseLong(args[1]) : taskResult.getTestId();

        List<EmcOrderTaskResultBean> orderTaskResultList = EmcOrderTaskResultComputingEngine.getFolderTaskResultList();

        return orderTaskResultList.stream()
                .filter(r -> ObjectUtils.equals(itemName, r.getItemName()) && ObjectUtils.equals(testId, r.getTestId()) && ObjectUtils.equals(r.getFolderId(), taskResult.getFolderId())
                        && ObjectUtils.equals(r.getCycle(), taskResult.getCycle()) && ObjectUtils.equals(r.getFrequency(), taskResult.getFrequency()))
                .collect(Collectors.toList());
    }

    /**
     * 当值为ND时，值作为0来参与计算
     */
    private static String getNdNumber(String value) {
        return "ND".equals(value) ? "0" : value;
    }

    /**
     * 计算当前数值小数点后的位数
     */
    private static int getRadix(String number) {
        String num = number;

        // 如果数值采用科学计数法，则先转换成实际值，再取小数位
        boolean scientificNumber = NumberUtils.isScientificNumber(number);
        if (scientificNumber) {
            num = NumberUtils.formatNumber(new BigDecimal(number));
        }

        if (!num.contains(".")) {
            return 0;
        }

        return num.length() - num.indexOf(".") - 1;
    }

    /**
     * 判断数值是否在区间范围内
     * @param result
     * @param numRange
     * @return
     */
    private static boolean inNumRange(String result, String numRange) {
        if (!NumberUtils.isNumber(result)) {
            return false;
        }

        if (!isValidNumRange(numRange)) {
            return false;
        }

        String[] pairs = StringUtils.split(numRange, ",");
        double lowValue = NumberUtils.parseDouble(pairs[0].substring(1));
        double highValue = NumberUtils.parseDouble(pairs[1].substring(0, pairs[1].length() - 1));

        double resultValue = NumberUtils.parseDouble(result);
        boolean lowMatch = pairs[0].startsWith("[") ? resultValue >= lowValue : resultValue > lowValue;
        boolean highMatch = pairs[1].endsWith("]") ? resultValue <= highValue : resultValue < highValue;

        return lowMatch && highMatch;
    }

    /**
     * 判断是否为有效的数字区间范围
     * @param range
     * @return
     */
    private static boolean isValidNumRange(String range) {
        Matcher matcher = NUMERICAL_RANGE_PATTERN.matcher(range);
        if (!matcher.matches()) {
            return false;
        }

        return Arrays.stream(StringUtils.split(range.replaceAll("[\\[|\\(|\\)|\\]]", ""), ",")).allMatch(NumberUtils::isNumber);
    }

}
