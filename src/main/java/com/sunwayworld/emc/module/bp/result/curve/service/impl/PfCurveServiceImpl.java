package com.sunwayworld.emc.module.bp.result.curve.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.curve.bean.EmcCurveBean;
import com.sunwayworld.emc.module.bp.result.curve.service.PfCurveService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2022年07月15日
 */
@Repository
@GikamBean
public class PfCurveServiceImpl extends EmcCurveServiceImpl implements PfCurveService {

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void compute(Long id, RestJsonWrapperBean wrapper) {

        EmcCurveBean curve = wrapper.parseUnique(EmcCurveBean.class);

        EmcOrderTaskResultBean resultFilter = new EmcOrderTaskResultBean();
        resultFilter.setFolderId(curve.getFolderId());

        EmcOrderTaskResultComputingEngine.getEngine(orderTaskResultService.getDao().selectColumnList(resultFilter, "ID", Long.class)).compute();

        // 计算曲线截距、斜率、相关系数
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(curve.getFolderId(), "FOLDERID");

        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

        // 筛选出指标名称为曲线校准的分析项id
        List<EmcItemBean> curveItemList = itemService
                .selectListByFilter(SearchFilter.instance().match("ID", itemIdList).filter(MatchPattern.OR).match("SETNAME", Arrays.asList("NUMERIC_CON", "NUMERIC_CON_NOB", "NUMERIC_CON_NOI")).filter(MatchPattern.OR));

        List<Long> curveItemIdList = curveItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> curveOrderTaskResultList = orderTaskResultList.stream().filter(e -> curveItemIdList.contains(e.getItemId())).collect(Collectors.toList());

        String computeType = curve.getExt$Item("type");
        List<EmcOrderTaskResultBean> updateOrderTaskResultList = StringUtils.equals("logCompute", computeType) ? calculateSlopeInterceptWithLog(curveOrderTaskResultList, curveItemList) : calculateSlopeIntercept(curveOrderTaskResultList, curveItemList);

        if (!updateOrderTaskResultList.isEmpty()) {
            orderTaskResultService.getDao().update(curveOrderTaskResultList, "RN2", "RN3", "RN4");

            // 更新曲线的斜率截距相关系数值
            EmcOrderTaskResultBean firstOrderTaskResult = curveOrderTaskResultList.get(0);
            curve.setSlope(firstOrderTaskResult.getRn2());
            curve.setIntercept(firstOrderTaskResult.getRn3());
            curve.setCoefficient(firstOrderTaskResult.getRn4());

            getDao().update(curve, "SLOPE", "INTERCEPT", "COEFFICIENT");
        }
    }

    @Override
    public void copy(RestJsonWrapperBean wrapper) {
        String operation = wrapper.getParamValue("operation");
        String copyDate = wrapper.getParamValue("copyDate");
        List<EmcCurveBean> copyCurveList = wrapper.parse(EmcCurveBean.class);
        List<Long> folderIdList = copyCurveList.stream().map(EmcCurveBean::getFolderId).collect(Collectors.toList());

        List<EmcFolderBean> copyFolderList = folderService.getDao().selectListByIds(folderIdList);
        List<EmcOrderBean> copyOrderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");
        List<EmcOrderTaskBean> copyOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");
        List<EmcOrderTaskResultBean> copyOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");

        int qty = copyCurveList.size();
        List<String> folderNoList = ApplicationContextHelper.getNextSequenceList(folderService.getDao().getTable(), qty);
        List<String> curveNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), EmcConstant.CURVE_NO_PREFIX_A, qty);

        List<EmcCurveBean> newCurveList = new ArrayList<>(qty);

        for (int i = 0; i < qty; i++) {
            EmcFolderBean folder = copyFolderList.get(i);
            Long folderId = ApplicationContextHelper.getNextIdentity();
            String folderNo = folderNoList.get(i);
            String curveNo = curveNoList.get(i);

            for (EmcCurveBean curve : copyCurveList) {
                if (ObjectUtils.equals(folder.getId(), curve.getFolderId())) {
                    EmcCurveBean newCurve = new EmcCurveBean();
                    BeanUtils.copyProperties(curve, newCurve, EmcConstant.CREATED_COLUMNS);
                    newCurve.setId(ApplicationContextHelper.getNextIdentity());
                    newCurve.setCurveNo(curveNo);
                    newCurve.setFolderId(folderId);
                    newCurve.setStatus("unpublished");// 未发布
                    if (EmcConstant.CURVE_COPY.equals(operation)) {
                        newCurve.setIntercept(null);
                        newCurve.setSlope(null);
                        newCurve.setCoefficient(null);
                    }
                    if (Constant.NO.equals(copyDate)) {
                        newCurve.setExpireDate(null);
                    }
                    newCurveList.add(newCurve);
                }
            }

            for (EmcOrderBean order : copyOrderList) {
                if (ObjectUtils.equals(folder.getId(), order.getFolderId())) {
                    Long orderId = ApplicationContextHelper.getNextIdentity();
                    String orderNo = StringUtils.replace(order.getOrderNo(), folder.getFolderNo(), folderNo);
                    for (EmcOrderTaskBean orderTask : copyOrderTaskList) {
                        if (ObjectUtils.equals(order.getId(), orderTask.getOrderId())) {
                            Long orderTaskId = ApplicationContextHelper.getNextIdentity();
                            for (EmcOrderTaskResultBean result : copyOrderTaskResultList) {
                                if (ObjectUtils.equals(orderTask.getId(), result.getOrderTaskId())) {
                                    result.setId(ApplicationContextHelper.getNextIdentity());
                                    result.setOrderTaskId(orderTaskId);
                                    result.setOrderId(orderId);
                                    result.setOrderNo(orderNo);
                                    result.setFolderId(folderId);
                                    if (EmcConstant.CURVE_COPY.equals(operation)) {
                                        result.setOriginalResult(null);
                                        result.setReportResult(null);
                                        result.setRn2(null);// 斜率
                                        result.setRn3(null);// 截距
                                        result.setRn4(null);// 相关系数
                                    }
                                }
                            }
                            orderTask.setId(orderTaskId);
                            orderTask.setOrderId(orderId);
                            orderTask.setFolderId(folderId);
                        }
                    }
                    order.setId(orderId);
                    order.setFolderId(folderId);
                    order.setOrderNo(orderNo);
                }
            }

            folder.setId(folderId);
            folder.setFolderNo(folderNo);
        }

        this.getDao().insert(newCurveList);
        folderService.getDao().insert(copyFolderList);
        orderService.getDao().insert(copyOrderList);
        orderTaskService.getDao().insert(copyOrderTaskList);
        orderTaskResultService.getDao().insert(copyOrderTaskResultList);
    }

    // 计算曲线截距斜率相关系数(计算方式临时使用1.0方式，待后期考虑使用其他计算方式实现)
    @Override
    public List<EmcOrderTaskResultBean> calculateSlopeIntercept(List<EmcOrderTaskResultBean> curveOrderTaskResultList, List<EmcItemBean> curveItemList) {

        // 限制结果表的原始结果和标准物量都不为空才做计算
        if (!curveOrderTaskResultList.isEmpty() && curveOrderTaskResultList.stream().noneMatch(e -> StringUtils.isEmpty(e.getOriginalResult()))
                && curveOrderTaskResultList.stream().noneMatch(e -> ObjectUtils.isEmpty(e.getStandQty()))) {

            // 零点的样品结果数据
            List<EmcOrderTaskResultBean> zeroOrderTaskResultList = curveOrderTaskResultList.stream().filter(e -> e.getQcCategoryCode().contains("QC-CAL0")).collect(Collectors.toList());

            boolean ignoreZero = curveItemList.stream().anyMatch(item -> StringUtils.equals(Constant.YES, item.getExt$Item("ignoreZero")));

            // 参与实际斜率、截距计算的样品结果，如果同时存在0和0'的情况，则需要当作一组来计算
            List<EmcOrderTaskResultBean> calcCurveOrderTaskResultList = new ArrayList<>();

            // 曲线零点均值
            String blankcal = "0";
            if (!zeroOrderTaskResultList.isEmpty() && !ignoreZero) {

                Double sum = 0d;
                for (EmcOrderTaskResultBean taskResult : zeroOrderTaskResultList) {
                    if (NumberUtils.isNumber(taskResult.getOriginalResult())) {
                        sum = ArithUtils.add(sum.toString(), taskResult.getOriginalResult());
                    }
                }

                blankcal = NumberUtils.rounding(ArithUtils.div(sum, zeroOrderTaskResultList.size()) + "", "FDA,3");

                // 对于同时存在0和0'的情况，标准物质量一般都是相同的，所以这里就取第一个的标准物质量，没有计算均值
                // 对于同时存在0和0'的情况，两组合成一组，原始结果用均值替代
                EmcOrderTaskResultBean zeroOrderTaskResult = new EmcOrderTaskResultBean();
                zeroOrderTaskResult.setStandQty(zeroOrderTaskResultList.get(0).getStandQty());
                zeroOrderTaskResult.setOriginalResult(blankcal);
                calcCurveOrderTaskResultList.add(zeroOrderTaskResult);
            }

            // 添加除零点之外的曲线
            calcCurveOrderTaskResultList.addAll(curveOrderTaskResultList.stream().filter(e -> !e.getQcCategoryCode().contains("QC-CAL0")).collect(Collectors.toList()));

            String[] x = calcCurveOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getStandQty).toArray(String[]::new);
            String[] y = calcCurveOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getOriginalResult).toArray(String[]::new);

            // 截距、斜率、相关系数计算
            for (int k = 0; k < y.length; k++) {
                y[k] = (new BigDecimal(y[k]).subtract(new BigDecimal(blankcal))).toString();
            }

            BigDecimal n = new BigDecimal(x.length);
            BigDecimal sigmaxy = BigDecimal.ZERO;
            BigDecimal sigmax = BigDecimal.ZERO;
            BigDecimal sigmay = BigDecimal.ZERO;
            BigDecimal sigmax2 = BigDecimal.ZERO;
            BigDecimal sigmay2 = BigDecimal.ZERO;
            BigDecimal avx = BigDecimal.ZERO;
            BigDecimal avy = BigDecimal.ZERO;

            for (int k = 0; k < x.length; k++) {
                BigDecimal xi = new BigDecimal(x[k]);
                BigDecimal yi = new BigDecimal(y[k]);
                sigmaxy = sigmaxy.add(xi.multiply(yi));
                sigmax = sigmax.add(xi);
                sigmay = sigmay.add(yi);
                sigmax2 = sigmax2.add(xi.multiply(xi));
                sigmay2 = sigmay2.add(yi.multiply(yi));
            }

            avx = sigmax.divide(n, 12, BigDecimal.ROUND_HALF_UP);// 保留小数位
            avy = sigmay.divide(n, 12, BigDecimal.ROUND_HALF_UP);

            BigDecimal bu = (n.multiply(sigmaxy)).subtract(sigmax.multiply(sigmay));
            BigDecimal bl = (n.multiply(sigmax2)).subtract(sigmax.multiply(sigmax));

            BigDecimal slope = BigDecimal.ZERO;
            if (bl.compareTo(BigDecimal.ZERO) != 0) {
                slope = bu.divide(bl, 12, BigDecimal.ROUND_HALF_UP);
            }
            BigDecimal intercept = avy.subtract(slope.multiply(avx)).setScale(12, BigDecimal.ROUND_HALF_UP);
            BigDecimal r2u = (n.multiply(sigmaxy)).subtract(sigmax.multiply(sigmay));
            BigDecimal r2l = new BigDecimal(Math.sqrt(((n.multiply(sigmax2)).subtract(sigmax.multiply(sigmax))).multiply((n.multiply(sigmay2)).subtract(sigmay.multiply(sigmay))).doubleValue()));
            BigDecimal r2 = BigDecimal.ZERO;
            if (r2l.compareTo(BigDecimal.ZERO) != 0) {
                r2 = r2u.divide(r2l, 6, BigDecimal.ROUND_HALF_UP);
            }

            // *查询当前测试分析项的修约规则*//
            for (EmcOrderTaskResultBean curveOrderTaskResult : curveOrderTaskResultList) {

                EmcItemBean curveItem = curveItemList.stream().filter(e -> ObjectUtils.equals(curveOrderTaskResult.getItemId(), e.getId())).findFirst().get();

                String slopeRevision = StringUtils.isEmpty(curveItem.getSlopeRevision()) ? "ISO,4" : curveItem.getSlopeRevision();
                String RN2 = NumberUtils.rounding(slope.toString(), slopeRevision);

                String interceptRevision = StringUtils.isEmpty(curveItem.getInterceptRevision()) ? "ISO,4" : curveItem.getInterceptRevision();
                String RN3 = NumberUtils.rounding(intercept.toString(), interceptRevision);

                //相关系数取绝对值，处理负数问题
                BigDecimal mysteryValueDecimal = new BigDecimal(Math.abs(Float.parseFloat(r2.toString())));
                //四舍五入 改为 只舍不入
                String RN4 = mysteryValueDecimal.toString().length() > 6 ? NumberUtils.rounding(mysteryValueDecimal.toString(),"ZSBR,4") : mysteryValueDecimal.toString();

                if ("1.0000".equals(RN4)) {
                    RN4 = "0.9999";
                }

                curveOrderTaskResult.setRn2(RN2);
                curveOrderTaskResult.setRn3(RN3);
                curveOrderTaskResult.setRn4(RN4);

            }

            return curveOrderTaskResultList;

        }

        return Collections.emptyList();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        Long equiptId = NumberUtils.parseLong(jsonWrapper.getParamValue("equiptId"));
        Long testId = NumberUtils.parseLong(jsonWrapper.getParamValue("testId"));
        Long methodId = NumberUtils.parseLong(jsonWrapper.getParamValue("methodId"));
        Long testMethodId = NumberUtils.parseLong(jsonWrapper.getParamValue("testMethodId"));

        String curveType = jsonWrapper.getParamValue("curveType");

        List<EmcItemBean> itemList = jsonWrapper.parse(EmcItemBean.class);
        List<EmcQcCategoryBean> qcList = jsonWrapper.parse(EmcQcCategoryBean.class);

        // 测试方法
        //List<Long> testMethodIdList = itemList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("ID", testMethodId).filter(MatchPattern.OR));
        // 测试样品类型
        List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodId).filter(MatchPattern.OR));

        String setNameFilter = "NUMERIC_CON";

        //增加曲线选点，如果是NUMERIC_BON，那么只增加类型是NUMERIC_BON的分析项
        if("NUMERIC_BON".equals(itemList.get(0).getExt$Item("setname"))){
            setNameFilter = "NUMERIC_BON";
        }

        // 曲线结果分析项
        List<EmcItemBean> resultItemList = itemService.selectListByFilter(
                SearchFilter.instance().match("TESTID", testId).filter(MatchPattern.EQ).match("METHODID", methodId).filter(MatchPattern.EQ)
                .match("TESTMETHODID", testMethodId).filter(MatchPattern.EQ).match("SETNAME", setNameFilter).filter(MatchPattern.SB));

        List<EmcCurveBean> insertCurveList = new ArrayList<>();// 曲线
        List<EmcFolderBean> insertFolderList = new ArrayList<>();// 点位
        List<EmcOrderBean> insertOrderList = new ArrayList<>();// 样品
        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();// 样品测试
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();// 样品结果

        int qty = itemList.size();
        List<String> folderNoList = ApplicationContextHelper.getNextSequenceList(folderService.getDao().getTable(), qty);
        List<String> curveNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), EmcConstant.CURVE_NO_PREFIX_A, qty);
        for (int i = 0; i < qty; i++) {
            EmcItemBean item = itemList.get(i);
            String folderNo = folderNoList.get(i);
            String curveNo = curveNoList.get(i);

            EmcFolderBean folder = this.generateFolder(folderNo);
            insertFolderList.add(folder);

            EmcCurveBean curve = this.generateCurve(folder.getId(), curveNo, item, equiptId, curveType);
            insertCurveList.add(curve);

            EmcTestMethodBean testMethod = testMethodList.stream().filter(f -> ObjectUtils.equals(f.getId(), item.getTestMethodId())).findFirst().orElse(null);
            EmcTestMethodOrderCategoryBean testOrder = testOrderList.stream().filter(f -> ObjectUtils.equals(f.getTestMethodId(), item.getTestMethodId())).findFirst().orElse(null);

            int index = 1;
            for (EmcQcCategoryBean qc : qcList) {
                String orderNo = folder.getFolderNo() + StringUtils.leftPad("" + index, 3);// 生成样品编码
                EmcOrderBean order = this.generateOrder(orderNo, qc, folder);
                insertOrderList.add(order);

                EmcOrderTaskBean orderTask = this.generateOrderTask(equiptId, order, item, testMethod, testOrder);
                insertOrderTaskList.add(orderTask);

                List<EmcOrderTaskResultBean> resultList = this.generateTaskResultList(order, orderTask, resultItemList);
                insertOrderTaskResultList.addAll(resultList);
                index++;
            }
        }

        this.getDao().insert(insertCurveList);
        folderService.getDao().insert(insertFolderList);
        orderService.getDao().insert(insertOrderList);
        orderTaskService.getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);

        return insertCurveList.get(0).getId();
    }

    @Override
    public Long insertEquiptCurve(RestJsonWrapperBean wrapper) {
        EmcCurveBean curve = new EmcCurveBean();
        Long equiptId = NumberUtils.parseLong(wrapper.getParamValue("equiptId"));
        Long testId = NumberUtils.parseLong(wrapper.getParamValue("testId"));
        Long methodId = NumberUtils.parseLong(wrapper.getParamValue("methodId"));
        curve.setId(ApplicationContextHelper.getNextIdentity());
        curve.setCurveNo(ApplicationContextHelper.getNextSequence(getDao().getTable(),EmcConstant.CURVE_NO_PREFIX_A));
        curve.setEquiptId(equiptId);
        curve.setMethodId(methodId);
        curve.setTestId(testId);
        curve.setConfDate(LocalDateTime.now());
        curve.setStatus("unpublished");// 未发布
        curve.setCurveCategory("instrument");//默认为“仪器曲线配置”类型
        getDao().insert(curve);
        return curve.getId();
    }
}
