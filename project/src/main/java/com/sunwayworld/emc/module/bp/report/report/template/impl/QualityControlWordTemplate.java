package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.PfWord2007Utils;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateBasicDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcBlankDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcLdupDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcLspkcDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcMethodDeviceDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcMonitorItemDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcPdupDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcQuantityDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.QcStdDynamicTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 质控报告
 *
 * @author zhengxy@sunwayworld.com 2021年06月25日
 */
public class QualityControlWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;

    public QualityControlWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
    }

    @Override
    public WordTemplateBasicDTO buildBasicInformation() {
        WordTemplateBasicDTO wordTemplateBasicDTO = super.buildBasicInformation();

        List<EmcIndexCategoryBean> indexCategoryList = templateSourceDTO.getIndexCategoryList();
        String executeStd = indexCategoryList.stream().map(EmcIndexCategoryBean::getStdName).distinct().collect(Collectors.joining("、"));
        wordTemplateBasicDTO.setExecuteStd(executeStd);

        boolean sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());
        wordTemplateBasicDTO.setSamplingDesc(sampling ? PfConstant.QC_SAMPLING_DESC : "");
        wordTemplateBasicDTO.setSamplingEnv(sampling ? PfConstant.QC_SAMPLING_ENV : "");

        return wordTemplateBasicDTO;
    }

    @Override
    public void createResultTable() {
        // 插入公司LOGO
        //insertOrgLogoImage();

        // 检测项目
        QcMonitorItemDynamicTable monitorItemDynamicTable = new QcMonitorItemDynamicTable(document, templateSourceDTO.getOrderTaskList(), templateSourceDTO.getCoreCodeList());
        monitorItemDynamicTable.create();

        // 质控数量
        List<EmcOrderBean> curProjectOrderList = templateSourceDTO.getOrderList().stream().filter(order -> ObjectUtils.equals(order.getProjId(), templateSourceDTO.getProject().getId())).collect(Collectors.toList());
        List<EmcOrderBean> curProjectQCDupOrderList = templateSourceDTO.getQcOrderList().stream().filter(order -> StringUtils.equals(order.getQcCategoryCode(),EmcConstant.QC_P_DUP)).collect(Collectors.toList());
        curProjectOrderList.addAll(curProjectQCDupOrderList);
        QcQuantityDynamicTable qcQuantityDynamicTable = new QcQuantityDynamicTable(document, templateSourceDTO.getProject(), curProjectOrderList, templateSourceDTO.getOrderTaskList(), templateSourceDTO.getQcOrderTaskList(), templateSourceDTO.getQcTaskResultList(), templateSourceDTO.getCoreCodeList());
        qcQuantityDynamicTable.create();
        //普通报告采样项目:采样前分包的不体现在报告里，质控报告去除所有分包
        // 检测方法及仪器设备, 去除包含参数的测试
        List<EmcOrderTaskBean> orderTaskList = templateSourceDTO.getOrderTaskList();
        List<EmcOrderTaskResultBean> orderTaskResultList = templateSourceDTO.getOrderTaskResultList();

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), EmcConstant.DRY_MATTER_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD)
                && !StringUtils.contains(r.getTestName(), PfConstant.WATER_CONTENT_TEST_MATCH_KEYWORD_2)
                && !StringUtils.contains(r.getExt$Item("relatedTest"), Constant.YES)
                && ObjectUtils.isEmpty(r.getSubcontractorId())//去除分包的
                && EmcConstant.N_A.equals(r.getQcCategoryCode())).collect(Collectors.toList());
        //质控报告去除所有分包
        List<Long> validOrderTaskIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<Long> validTestMethodIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());

        // 质控报告模板中，在检测仪器设备之前有三个表格
        AtomicInteger dynamicTableIndex = new AtomicInteger(3);

        Map<String, List<EmcOrderTaskBean>> orderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderCategory));
        AtomicInteger deviceTableIndex = new AtomicInteger(3);
        orderTaskMap.forEach((orderCategory, curTaskList) -> {
            List<Long> curTaskIdList = curTaskList.stream().filter(task -> !StringUtils.equals(task.getExt$Item("other"), Constant.YES)).map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            if (curTaskIdList.isEmpty()) {
                return;
            }
            List<EmcReportResultBean> curReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> curTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());
            QcMethodDeviceDynamicTable deviceDynamicTable = new QcMethodDeviceDynamicTable(document, curTaskList, curReportResultList, templateSourceDTO.getEquiptList(), deviceTableIndex.getAndIncrement());
            deviceDynamicTable.create();
            dynamicTableIndex.incrementAndGet();
        });

        // 实验室质量结果控制
        List<EmcOrderBean> orderList = templateSourceDTO.getOrderList();

        List<EmcOrderTaskBean> qcOrderTaskList = templateSourceDTO.getQcOrderTaskList();
        List<EmcOrderTaskResultBean> qcTaskResultList = templateSourceDTO.getQcTaskResultList();

        EmcProjectBean project = templateSourceDTO.getProject();
        List<EmcOrderBean> allProjectOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getProjId(), project.getId())).collect(Collectors.toList());
        allProjectOrderList.addAll(templateSourceDTO.getQcOrderList());

        // 现场质控，现场质控样和NA样都可以作为实验室质控的父样
        List<EmcOrderBean> samplingQcOrderList = templateSourceDTO.getQcOrderList().stream().filter(order -> ObjectUtils.equals(order.getProjId(), project.getId())).collect(Collectors.toList());
        List<EmcOrderBean> projectParentOrderList = new ArrayList<>(orderList);
        projectParentOrderList.addAll(samplingQcOrderList);

        List<EmcOrderBean> validOrderList = allProjectOrderList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderBean::getId))), ArrayList::new));
        Map<String, List<EmcOrderBean>> orderMap = validOrderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getOrderCategory));
        //质控同样去除分包Begin
        List<EmcOrderTaskBean> validQcOrderTaskList = qcOrderTaskList.stream().filter(ok ->ObjectUtils.isEmpty(ok.getSubcontractorId())).collect(Collectors.toList());
        List<Long> validQcOrderTaskIdList = validQcOrderTaskList.stream().map(EmcOrderTaskBean :: getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> validQcTaskResultList = qcTaskResultList.stream().filter(res ->validQcOrderTaskIdList.contains(res.getOrderTaskId())).collect(Collectors.toList());
        //质控同样去除分包END
        List<EmcOrderTaskBean> projectOrderTaskList = new ArrayList<>(validOrderTaskList);
        projectOrderTaskList.addAll(validQcOrderTaskList);
        projectOrderTaskList.addAll(validOrderTaskList);
        List<EmcOrderTaskBean> allOrderTaskList = projectOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getId))), ArrayList::new));
        //去除分包测试结果
        List<EmcOrderTaskResultBean> validOrderTaskResultList=templateSourceDTO.getOrderTaskResultList().stream().filter(res ->validOrderTaskIdList.contains(res.getOrderTaskId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> projectOrderTaskResultList = new ArrayList<>(validOrderTaskResultList);
        projectOrderTaskResultList.addAll(validQcTaskResultList);
        projectOrderTaskResultList.addAll(validOrderTaskResultList);

        List<EmcOrderTaskResultBean> allOrderTaskResultList = projectOrderTaskResultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskResultBean::getId))), ArrayList::new));

        List<EmcQcRelatedOrderBean> qcRelatedOrderList = templateSourceDTO.getQcRelatedOrderList();

        boolean samplingProject = Constant.YES.equals(project.getSample());
        List<String> excludedQcCodes = Arrays.asList(PfConstant.QC_P_YSKB, PfConstant.QCCATEGORY_QCCATEGORYID_P_BLANK);
        List<EmcItemQcIndexBean> itemQcIndexList = templateSourceDTO.getItemQcIndexList();

        AtomicInteger qcResultTableIndex = new AtomicInteger();
        orderMap.forEach((orderCategory, curOrderList) -> {
            List<Long> curOrderIdList = curOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            //List<Long> relatedQcOrderIdList = qcRelatedOrderList.stream().filter(relatedOrder -> curOrderIdList.contains(relatedOrder.getRelatedOrderId())).map(EmcQcRelatedOrderBean::getQcOrderId).distinct().collect(Collectors.toList());

            List<EmcOrderTaskBean> curTaskList = qcOrderTaskList.stream().filter(task -> curOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());
            List<EmcOrderTaskResultBean> curResultList = qcTaskResultList.stream().filter(result -> curOrderIdList.contains(result.getOrderId())).collect(Collectors.toList());

            // 空白样质控结果
            List<EmcOrderTaskBean> blankTaskList = qcOrderTaskList.stream().filter(task -> StringUtils.endsWith(task.getQcName(), PfConstant.QC_BLANK_KEYWORD) && validTestMethodIdList.contains(task.getTestMethodId())).collect(Collectors.toList());
            // 如果是送样的项目，需要排除全程序空白和运输空白
            if (!samplingProject) {
                blankTaskList = blankTaskList.stream().filter(task -> !excludedQcCodes.contains(task.getQcCategoryCode())).collect(Collectors.toList());
            }
            if (!blankTaskList.isEmpty()) {
                List<Long> blankTaskIdList = blankTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> blankResultList = curResultList.stream().filter(result -> blankTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());
                //空白平行
                List<EmcOrderTaskBean> phBlankTaskList = blankTaskList.stream().filter(task -> StringUtils.containsIgnoreCase(task.getTestName(), PfConstant.QC_PDUP_PH_KEYWORD)).collect(Collectors.toList());
                List<Long> phBlankTaskIdList = phBlankTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> phBlankResultList = blankResultList.stream().filter(result -> phBlankTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());

                if (!phBlankTaskList.isEmpty() && !phBlankResultList.isEmpty()) {
                    QcBlankDynamicTable blankDynamicTable = new QcBlankDynamicTable(document, phBlankTaskList, phBlankResultList, allOrderTaskResultList,templateSourceDTO.getGroupItemList(),templateSourceDTO.getItemGroupList(), qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement());
                    blankDynamicTable.create();
                }

                if(!blankResultList.isEmpty()){
                    QcBlankDynamicTable blankDynamicTable = new QcBlankDynamicTable(document, blankTaskList, blankResultList, allOrderTaskResultList,templateSourceDTO.getGroupItemList(),templateSourceDTO.getItemGroupList(), qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement());
                    blankDynamicTable.create();
                }

            }

            // 现场平行质控结果，只有采样项目质控报告需要
            if (samplingProject) {
                List<EmcOrderBean> pdupOrderList = curOrderList.stream().filter(order -> StringUtils.equals(order.getQcCategoryCode(), PfConstant.QC_P_DUP)).sorted(Comparator.comparing(EmcOrderBean::getId)).collect(Collectors.toList());
                if (!pdupOrderList.isEmpty()) {
                    pdupOrderList.forEach(pdupOrder -> {
                        EmcOrderBean parentOrder = orderList.stream().filter(order -> StringUtils.equals(order.getOrderNo(), pdupOrder.getParentOrderNo())).findFirst().orElse(null);
                        if (parentOrder == null) {
                            return;
                        }

                        List<EmcOrderBean> tableOrderList = new ArrayList<>(2);
                        tableOrderList.add(parentOrder);
                        tableOrderList.add(pdupOrder);

                        List<Long> tableOrderIdList = tableOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                        List<EmcOrderTaskBean> tableTaskList = allOrderTaskList.stream().filter(task -> tableOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());
                        List<EmcOrderTaskResultBean> tableResultList = allOrderTaskResultList.stream().filter(result -> tableOrderIdList.contains(result.getOrderId())).collect(Collectors.toList());

                        List<EmcOrderTaskBean> phTaskList = tableTaskList.stream().filter(task -> StringUtils.containsIgnoreCase(task.getTestName(), PfConstant.QC_PDUP_PH_KEYWORD)).collect(Collectors.toList());
                        List<EmcOrderTaskBean> otherTaskList = new ArrayList<>();
                        List<EmcIndexSetBean> indexSetFinalPdupBeans = new ArrayList<>();
                        if (!phTaskList.isEmpty()) {
                            List<Long> phTaskIdList = phTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                            List<EmcOrderTaskResultBean> phResultList = tableResultList.stream().filter(result -> phTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());
                            QcPdupDynamicTable pdupDynamicTable = new QcPdupDynamicTable(document, tableOrderList, phTaskList, phResultList, false,indexSetFinalPdupBeans, true, qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement());
                            pdupDynamicTable.create();
                            otherTaskList = tableTaskList.stream().filter(task -> !phTaskIdList.contains(task.getId())).collect(Collectors.toList());
                        }else {
                            otherTaskList.addAll(tableTaskList);
                        }

                        List<Long> otherTaskIdList = otherTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                        if (!otherTaskIdList.isEmpty()) {
                            List<EmcOrderTaskResultBean> otherResultList = tableResultList.stream().filter(result -> otherTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());

                            QcPdupDynamicTable pdupDynamicTable = new QcPdupDynamicTable(document, tableOrderList, otherTaskList, otherResultList, false,indexSetFinalPdupBeans, false, qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement());
                            pdupDynamicTable.create();
                        }
                    });
                }
            }

            // 实验室平行质控结果
            List<EmcOrderBean> ldupOrderList = curOrderList.stream().filter(order -> StringUtils.equals(order.getQcCategoryCode(), PfConstant.QC_L_DUP)).collect(Collectors.toList());
            if (!ldupOrderList.isEmpty()) {
                List<String> parentOrderNoList = ldupOrderList.stream().map(EmcOrderBean::getParentOrderNo).distinct().collect(Collectors.toList());
                List<EmcOrderBean> parentOrderList = projectParentOrderList.stream().filter(order -> parentOrderNoList.contains(order.getOrderNo())).collect(Collectors.toList());

                if (!parentOrderList.isEmpty()) {
                    List<EmcOrderBean> tableOrderList = new ArrayList<>(parentOrderList);
                    tableOrderList.addAll(ldupOrderList);

                    List<Long> tableOrderIdList = tableOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                    List<EmcOrderTaskBean> tableTaskList = allOrderTaskList.stream().filter(task -> tableOrderIdList.contains(task.getOrderId()) && validTestMethodIdList.contains(task.getTestMethodId())).collect(Collectors.toList());
                    List<Long> tableTaskIdList = tableTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> tableResultList = allOrderTaskResultList.stream().filter(result -> tableTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());

                    List<EmcOrderTaskBean> phTaskList = tableTaskList.stream().filter(task -> StringUtils.containsIgnoreCase(task.getTestName(), PfConstant.QC_PDUP_PH_KEYWORD)).collect(Collectors.toList());
                    List<Long> phTaskIdList = phTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());

                    List<EmcOrderTaskResultBean> phResultList = tableResultList.stream().filter(result -> phTaskIdList.contains(result.getOrderTaskId())).collect(Collectors.toList());
                    List<EmcOrderTaskBean> ldupPhTaskList = phTaskList.stream().filter(phTask -> StringUtils.equals(phTask.getQcCategoryCode(), PfConstant.QC_L_DUP)).collect(Collectors.toList());


                    if (!phResultList.isEmpty() && !ldupPhTaskList.isEmpty()) {
                        QcLdupDynamicTable ldupDynamicTable = new QcLdupDynamicTable(document, tableOrderList, phTaskList, phResultList, true, qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement(), "RN8");
                        ldupDynamicTable.create();
                    }

                    List<EmcOrderTaskBean> otherTaskList = tableTaskList.stream().filter(task -> !phTaskIdList.contains(task.getId())).collect(Collectors.toList());
                    List<EmcOrderTaskResultBean> otherResultList = tableResultList.stream().filter(r -> validTestMethodIdList.contains(r.getTestMethodId())).collect(Collectors.toList());

                    if (!otherResultList.isEmpty()) {
                        List<EmcOrderTaskResultBean> ldupDisplayResultList = otherResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay()) && PfConstant.QC_L_DUP.equals(r.getQcCategoryCode())).collect(Collectors.toList());
                        ldupDisplayResultList.forEach(r -> {
                            EmcItemQcIndexBean itemQcIndex = itemQcIndexList.stream().filter(qcIndex -> ObjectUtils.equals(r.getItemId(), qcIndex.getItemId()) && ObjectUtils.equals(r.getQcCategoryId(), qcIndex.getQcCategoryId())).findFirst().orElseGet(EmcItemQcIndexBean::new);
                            r.setExt$Item("decisionCategory", Optional.ofNullable(itemQcIndex.getDecisionCategory()).orElse(EmcReportConstant.RN7_DECISION_CATEGORY));
                        });
                        Map<String, List<EmcOrderTaskResultBean>> decisionCategoryResultMap = ldupDisplayResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("decisionCategory")));
                        decisionCategoryResultMap.forEach((decisionCategory, curLdupResultList) -> {
                            List<Long> curTestIdList = curLdupResultList.stream().map(EmcOrderTaskResultBean::getTestId).distinct().collect(Collectors.toList());
                            List<Long> curLdupOrderIdList = curLdupResultList.stream().map(EmcOrderTaskResultBean::getOrderId).distinct().collect(Collectors.toList());
                            List<EmcOrderBean> curLdupOrderList = ldupOrderList.stream().filter(order -> curLdupOrderIdList.contains(order.getId())).collect(Collectors.toList());
                            List<String> curParentOrderNoList = curLdupOrderList.stream().map(EmcOrderBean::getParentOrderNo).distinct().collect(Collectors.toList());
                            List<EmcOrderBean> curParentOrderList = parentOrderList.stream().filter(order -> curParentOrderNoList.contains(order.getOrderNo())).collect(Collectors.toList());

                            List<EmcOrderBean> curTableOrderList = new ArrayList<>(curParentOrderList);
                            curTableOrderList.addAll(curLdupOrderList);
                            List<Long> curTableOrderIdList = curTableOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                            List<EmcOrderTaskBean> curDecisionTaskList = otherTaskList.stream().filter(task -> curTableOrderIdList.contains(task.getOrderId()) && curTestIdList.contains(task.getTestId())).collect(Collectors.toList());
                            List<Long> curDecisionTaskIdList = curDecisionTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                            List<EmcOrderTaskResultBean> curDecisionResultList = otherResultList.stream().filter(r -> curDecisionTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());

                            QcLdupDynamicTable ldupDynamicTable = new QcLdupDynamicTable(document, curTableOrderList, curDecisionTaskList, curDecisionResultList, false, qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement(), decisionCategory);
                            ldupDynamicTable.create();

                        });
                    }
                }
            }

            // 样品加标质控结果
            List<EmcOrderTaskBean> spkcTaskList = curTaskList.stream().filter(task -> StringUtils.endsWith(task.getQcName(), PfConstant.QC_LSPKC_KEYWORD) && validTestMethodIdList.contains(task.getTestMethodId())).collect(Collectors.toList());
            if (!spkcTaskList.isEmpty()) {
                List<Long> spkcTaskIdList = spkcTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> spkcResultList = curResultList.stream().filter(result -> spkcTaskIdList.contains(result.getOrderTaskId())
                        && (Constant.YES.equals(result.getReportDisplay()) || StringUtils.contains(result.getItemName(), EmcReportConstant.RESULT_ITEM_KEYWORD))).collect(Collectors.toList());

                if (!spkcResultList.isEmpty()) {
                    QcLspkcDynamicTable lspkcDynamicTable = new QcLspkcDynamicTable(document, spkcTaskList, spkcResultList, qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement());
                    lspkcDynamicTable.create();
                }
            }

            // 实验室标准样品质控结果
            List<EmcOrderTaskBean> stdTaskList = curTaskList.stream().filter(task -> StringUtils.equals(task.getQcCategoryCode(), PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD)).collect(Collectors.toList());
            if (!stdTaskList.isEmpty()) {
                List<Long> stdTaskIdList = stdTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                List<EmcOrderTaskResultBean> stdResultList = curResultList.stream().filter(result -> stdTaskIdList.contains(result.getOrderTaskId())
                        && (Constant.YES.equals(result.getReportDisplay()) || StringUtils.contains(result.getItemName(), EmcReportConstant.RESULT_ITEM_KEYWORD))).collect(Collectors.toList());

                if (!stdResultList.isEmpty()) {
                    QcStdDynamicTable stdDynamicTable = new QcStdDynamicTable(document, stdTaskList, stdResultList, qcResultTableIndex.incrementAndGet(), dynamicTableIndex.getAndIncrement());
                    stdDynamicTable.create();
                }
            }
        });
        //清一下list
    }

    @Override
    public void clean() {
        super.clean();
        Word2007Utils.removeKey(this.getDocument(), PfConstant.QC_MONITOR_ITEM_TABLE_REPLACE_KEYWORD);
        Word2007Utils.removeKey(this.getDocument(), PfConstant.QC_QUANTITY_TABLE_REPLACE_KEYWORD);
        Word2007Utils.removeKey(this.getDocument(), PfConstant.QC_METHOD_DEVICE_TABLE_REPLACE_KEYWORD);
        Word2007Utils.removeKey(this.getDocument(), PfConstant.QC_RESULT_TABLE_REPLACE_KEYWORD);
    }

    @Override
    protected void hiddenTableRow(CrmOpportunityMainBean opportunityMain) {

    }

    protected void insertOrgLogoImage() {
        List<CoreFileBean> coreFileList = templateSourceDTO.getCoreFileList();
        CoreFileBean logoFile = coreFileList.stream().filter(file -> StringUtils.equals(file.getExt$Item("orgLogo"), Constant.YES)).findFirst().orElse(null);
        if (null == logoFile) {
            return;
        }

        String logoFileUrl = FilePathManager.getLocalDirPath(FilePathDTO.of(logoFile.getScope(), DateTimeUtils.getDateSerialNumber(logoFile.getCreatedTime()), logoFile.getMd5Name(), logoFile.getName())).toString();
        File imageFile = new File(logoFileUrl);
        if (imageFile.exists()) {
            PfWord2007Utils.insertImageOfKey("LOGO", document, logoFileUrl, 100, 50);
        }
    }
}
