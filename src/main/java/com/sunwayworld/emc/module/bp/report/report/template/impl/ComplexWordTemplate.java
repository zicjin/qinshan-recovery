package com.sunwayworld.emc.module.bp.report.report.template.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateSourceDTO;
import com.sunwayworld.emc.module.bp.report.report.template.BaseWordTemplate;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.GasParameterDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.MonitorItemDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.NoiseDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.NonSamplingSoilDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.NonSamplingWaterDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.OrderScenePhotoTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.SimpleMonitorItemDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.SoilDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.UnorganizedGasDynamicTable;
import com.sunwayworld.emc.module.bp.report.report.template.table.WaterDynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 综合报告
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class ComplexWordTemplate extends BaseWordTemplate {

    private XWPFDocument document;
    private WordTemplateSourceDTO templateSourceDTO;
    private boolean sampling;
    private boolean showPhoto;

    public ComplexWordTemplate(XWPFDocument document, WordTemplateSourceDTO templateSourceDTO) {
        super(document, templateSourceDTO);
        this.document = document;
        this.templateSourceDTO = templateSourceDTO;
        this.sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());
        this.showPhoto = isShowPhoto();
    }

    @Override
    public void createResultTable() {
        // 获取点位对应的监测模板
        List<String> monitorTmplIdList = templateSourceDTO.getOrderList().stream().filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId())).map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        if (monitorTmplIdList.isEmpty()) {
            return;
        }

        // 根据监测模板ID查询监测模板，通过监测模板组ID来区分水、气、土、声
        List<EmcMonitorTmplBean> monitorTmplList = templateSourceDTO.getMonitorTmplList();
        // 根据监测模板组ID对监测模板进行分组
        Map<Long, List<EmcMonitorTmplBean>> monitorTmplMap = monitorTmplList.stream().collect(Collectors.groupingBy(EmcMonitorTmplBean::getMonitorTmplGroupId));

        // 水对应的监测模板
        List<EmcMonitorTmplBean> waterMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.WATER_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        // 气对应的监测模板
        List<EmcMonitorTmplBean> gasMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.GAS_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        // 土对应的监测模板
        List<EmcMonitorTmplBean> soilMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.SOIL_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        // 固废对应的监测模板
        List<EmcMonitorTmplBean> solidWasteMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.SOLID_WASTE_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);
        soilMonitorTmplList.addAll(solidWasteMonitorTmplList);

        // 声对应的监测模板
        List<EmcMonitorTmplBean> noiseMonitorTmplList = Optional.ofNullable(monitorTmplMap.get(PfConstant.NOISE_MONITOR_TMPL_GROUP_ID)).orElseGet(ArrayList::new);

        Map<String, LocalDate> minDateMap = sortResult(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList, noiseMonitorTmplList, templateSourceDTO.getOrderList());

        AtomicInteger totalTableIndex = new AtomicInteger(monitorTmplList.size());
        int totalTableCount = totalTableIndex.get();

        // 送样或现场照片
        if (showPhoto) {
            List<CoreFileBean> photoList = templateSourceDTO.getCoreFileList().stream().filter(file -> StringUtils.equals(file.getBizCategory(), "photo")
                    && (StringUtils.startsWith(file.getTargetId(), "T_EMC_SAMPLE") || StringUtils.startsWith(file.getTargetId(), "T_EMC_PROJECT"))).collect(Collectors.toList());
            List<CoreFileBean> validPhotoList = photoList.stream().filter(file -> FileHelper.getAbsoluteDirPath(FileRepresentation.of(file)).toFile().exists()).collect(Collectors.toList());

            OrderScenePhotoTable photoTable = new OrderScenePhotoTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getFolderList(), templateSourceDTO.getOrderList(), validPhotoList, monitorTmplList.size() + 1);
            photoTable.create();
        }

        // 创建结果表
        createResultTable(waterMonitorTmplList, gasMonitorTmplList, soilMonitorTmplList,noiseMonitorTmplList, totalTableIndex, totalTableCount, minDateMap);

        // 创建检测项目表
        int monitorItemTableIndex = monitorTmplList.size() + (showPhoto ? 3 : 2);
        MonitorItemDynamicTable monitorItemDynamicTable = new MonitorItemDynamicTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getReport(), templateSourceDTO.getOrderTaskList(), templateSourceDTO.getReportResultList(), templateSourceDTO.getEquiptList(), templateSourceDTO.getRunList(), templateSourceDTO.getCoreCodeList(), monitorItemTableIndex);
        monitorItemDynamicTable.create();

        // 创建检测项目一览表
        int simpleMiTableIndex = monitorTmplList.size() + (showPhoto ? 2 : 1);
        SimpleMonitorItemDynamicTable simpleMonitorItemDynamicTable = new SimpleMonitorItemDynamicTable(getDocument(), templateSourceDTO.getProject(), templateSourceDTO.getOrderList(), templateSourceDTO.getOrderTaskList(), simpleMiTableIndex);
        simpleMonitorItemDynamicTable.create();
    }

    @Override
    protected int getTestBasicTableIndex() {
        return super.getTestBasicTableIndex() + (showPhoto ? 1 : 0);
    }

    private boolean isShowPhoto() {
        List<CoreFileBean> photoList = templateSourceDTO.getCoreFileList().stream().filter(file -> StringUtils.equals(file.getBizCategory(), "photo")
                && (StringUtils.startsWith(file.getTargetId(), "T_EMC_SAMPLE") || StringUtils.startsWith(file.getTargetId(), "T_EMC_PROJECT"))).collect(Collectors.toList());
        List<CoreFileBean> validPhotoList = photoList.stream().filter(file -> FileHelper.getAbsoluteDirPath(FileRepresentation.of(file)).toFile().exists()).collect(Collectors.toList());

        return !validPhotoList.isEmpty();
    }

    /**
     * 计算报告中的结果表总数
     *  - 有组织废气需要按点位和采样日期生成单独的结果表格
     *  - 其他的是一个样品类型一个结果表格
     * @param monitorTmplList
     * @param gasMonitorTmplList
     * @return
     */
    private AtomicInteger calcTableCount(List<EmcMonitorTmplBean> monitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList) {
        List<String> gasTmplIdList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
        int otherTableCount = (int) monitorTmplList.stream().filter(m -> !gasTmplIdList.contains(m.getId())).count();
        AtomicInteger tableCount = new AtomicInteger(otherTableCount);

        List<String> unorganizedGasTmplNameList = gasMonitorTmplList.stream().filter(t -> StringUtils.contains(t.getMonitorTmplName(), PfConstant.DIOXIN_ENV_GAS_KEYWORD)
                || StringUtils.contains(t.getMonitorTmplName(), PfConstant.DIOXIN_UNORGANIZED_GAS_KEYWORD)).map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());

        tableCount.addAndGet(unorganizedGasTmplNameList.size());

        List<EmcMonitorTmplBean> organizedGasTmplList = gasMonitorTmplList.stream().filter(m -> !unorganizedGasTmplNameList.contains(m.getMonitorTmplName())).collect(Collectors.toList());
        List<String> organizedGasTmplNameList = organizedGasTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());

        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> organizedGasTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(organizedGasTmplList, templateSourceDTO.getFolderList());
        organizedGasTmplList.forEach((tmpl -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(tmpl.getMonitorTmplName())).orElseGet(ArrayList::new);
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> folderOrderList = curOrderList.stream().filter(order -> folderIdList.contains(order.getFolderId())).collect(Collectors.toList());
            Map<String, List<EmcOrderBean>> folderOrderMap = folderOrderList.stream().collect(Collectors.groupingBy(order -> ObjectUtils.isEmpty(order.getSampleBeginDate()) ? "-" : DateTimeUtils.formatLocalDate(order.getSampleBeginDate())));
            tableCount.addAndGet(folderOrderMap.size());
        }));

        return tableCount;
    }

    private void createResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList,List<EmcMonitorTmplBean> noiseMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount, Map<String, LocalDate> minDateMap) {
        minDateMap.forEach((key, minDate) -> {
            if (PfConstant.WATER_KEYWORD.equals(key)) {
                createWaterResultTable(waterMonitorTmplList, totalTableIndex, totalTableCount);
            } else if (PfConstant.GAS_KEYWORD.equals(key)) {
                createGasResultTable(gasMonitorTmplList, totalTableIndex, totalTableCount);
            } else if (PfConstant.SOIL_KEYWORD.equals(key)) {
                createSoilResultTable(soilMonitorTmplList, totalTableIndex, totalTableCount);
            } else if (PfConstant.NOISE_KEYWORD.equals(key)) {
                createNoiseResultTable(noiseMonitorTmplList, totalTableIndex, totalTableCount);
            }
        });
    }

    /**
     * 创建水与废水检测结果表
     * @param waterMonitorTmplList 水与废水类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createWaterResultTable(List<EmcMonitorTmplBean> waterMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (waterMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = waterMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(waterMonitorTmplList, templateSourceDTO.getFolderList());
        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);

            int waterTableIndex = totalTableIndex.getAndDecrement();
            boolean last = waterTableIndex == totalTableCount;

            folderList.sort(Comparator.comparing(EmcFolderBean::getId).reversed());

            List<Long> curFolderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> curFolderOrderList = templateSourceDTO.getOrderList().stream().filter(order -> curFolderIdList.contains(order.getFolderId())).collect(Collectors.toList());

            // 过滤掉关联测试
            List<EmcOrderTaskBean> validTaskList = templateSourceDTO.getOrderTaskList().stream().filter(task -> curFolderIdList.contains(task.getFolderId()) && !StringUtils.equals(task.getExt$Item("relatedTest"), Constant.YES)).collect(Collectors.toList());
            List<Long> validTaskIdList = validTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> curFolderIdList.contains(r.getFolderId()) && validTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());

            // 按样品下的测试数量进行分组
            curFolderOrderList.forEach(order -> {
                long curTestCount = validTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), order.getId())).map(EmcOrderTaskBean::getTestId).distinct().count();
                order.setExt$Item("testCount", curTestCount + "");
            });
            curFolderOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo).reversed());
            Map<String, List<EmcOrderBean>> curFolderOrderMap = curFolderOrderList.stream().collect(Collectors.groupingBy(order -> order.getExt$Item("testCount"), LinkedHashMap::new, Collectors.toList()));

            AtomicInteger countIndex = new AtomicInteger();
            curFolderOrderMap.forEach((testCount, curCountOrderList) -> {
                curCountOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo));
                List<List<EmcOrderBean>> subOrderList = new ArrayList<>(ListUtils.partition(curCountOrderList, PfConstant.MAX_COLUMN_QTY));
                Collections.reverse(subOrderList);
                boolean isFirst = (countIndex.getAndIncrement() == curFolderOrderMap.size() - 1);

                IntStream.range(0, subOrderList.size()).forEach(subIndex -> {
                    List<EmcOrderBean> partOrderList = subOrderList.get(subIndex);
                    List<Long> partOrderIdList = partOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                    List<Long> partFolderIdList = partOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
                    List<EmcFolderBean> partFolderList = folderList.stream().filter(folder -> partFolderIdList.contains(folder.getId())).collect(Collectors.toList());

                    List<EmcReportResultBean> partResultList = folderReportResultList.stream().filter(result -> partOrderIdList.contains(result.getOrderId())).collect(Collectors.toList());

                    List<EmcOrderTaskBean> partTaskList = templateSourceDTO.getOrderTaskList().stream().filter(task -> validTaskIdList.contains(task.getId()) && partOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());
                    boolean breakNewPage = (subIndex == 0) && last;
                    boolean isFirstTable = isFirst && (subIndex == subOrderList.size() - 1);

                    if (!sampling) {
                        NonSamplingWaterDynamicTable waterDynamicTable = new NonSamplingWaterDynamicTable(document, templateSourceDTO.getReport(), partFolderList, partOrderList, partTaskList, partResultList, templateSourceDTO.getIndexCategoryList(), waterTableIndex, isFirstTable, false);
                        waterDynamicTable.setGroupItemList(templateSourceDTO.getGroupItemList());
                        waterDynamicTable.setItemGroupList(templateSourceDTO.getItemGroupList());
                        waterDynamicTable.create();
                    } else {
                        WaterDynamicTable waterDynamicTable = new WaterDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), partFolderList, partOrderList, partResultList, waterTableIndex, isFirst, !breakNewPage);
                        waterDynamicTable.setGroupItemList(templateSourceDTO.getGroupItemList());
                        waterDynamicTable.setItemGroupList(templateSourceDTO.getItemGroupList());
                        waterDynamicTable.create();
                    }
                });
            });
        });
    }

    /**
     * 创建空气与废气检测结果表
     * @param gasMonitorTmplList 空气与废气类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createGasResultTable(List<EmcMonitorTmplBean> gasMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (gasMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(gasMonitorTmplList, templateSourceDTO.getFolderList());
        List<String> unorganizedGasTmplNameList = gasMonitorTmplList.stream().filter(t -> StringUtils.contains(t.getMonitorTmplName(), PfConstant.DIOXIN_ENV_GAS_KEYWORD)
                || StringUtils.contains(t.getMonitorTmplName(), PfConstant.DIOXIN_UNORGANIZED_GAS_KEYWORD)).map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcFolderBean> folderUnorganizedList = new ArrayList<>();
        folderListMap.forEach((monitorTmplName, folderList) -> {
            if(unorganizedGasTmplNameList.contains(monitorTmplName)){
                folderUnorganizedList.addAll(folderList);
            }
        });
        //排除无组织废气的生成管道参数
        List<Long> folderUnorgIdList = folderUnorganizedList.stream().map(EmcFolderBean :: getId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderOrgList = templateSourceDTO.getFolderList().stream().filter(f ->!folderUnorgIdList.contains(f.getId())).distinct().collect(Collectors.toList());
        List<Long> folderOrgIdList = folderOrgList.stream().map(EmcFolderBean :: getId).distinct().collect(Collectors.toList());

        List<EmcOrderBean> organizedOrderList = templateSourceDTO.getOrderList().stream().filter(o ->folderOrgIdList.contains(o.getFolderId())).collect(Collectors.toList());
        boolean displayParameter = Constant.YES.equals(templateSourceDTO.getReport().getExt$Item("displayParameter"));
        // 判定是否显示有组织废气参数
        List<EmcReportResultBean> reportResultAfterGasParamList= templateSourceDTO.getReportResultList();
        List<EmcOrderTaskBean> orderTaskGasParamList= templateSourceDTO.getOrderTaskList();
        List<EmcOrderTaskResultBean> resultBeans = templateSourceDTO.getOrderTaskResultList();

        //传个是否生成管道参数的参数
        Boolean displayParameterFinal = displayParameter && monitorTmplNameList.size() != unorganizedGasTmplNameList.size();
        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);
            if (unorganizedGasTmplNameList.contains(orderCategory)) {
                // 环境空气或无组织模板
                createUnorganizedGasResultTable(folderList, totalTableIndex, totalTableCount);
                return;
            }

            int gasTableIndex = totalTableIndex.getAndDecrement();
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> folderOrderList = curOrderList.stream().filter(order -> folderIdList.contains(order.getFolderId())).collect(Collectors.toList());
            Map<String, List<EmcOrderBean>> folderOrderMap = folderOrderList.stream().collect(Collectors.groupingBy(order -> ObjectUtils.isEmpty(order.getSampleBeginDate()) ? "-" : DateTimeUtils.formatLocalDate(order.getSampleBeginDate())));
            folderOrderMap.forEach((sampleDate, dateOrderList) -> {
                // 有组织废气
                createOrganizedGasResultTable(folderList, dateOrderList,displayParameterFinal, gasTableIndex, totalTableCount);
            });
        });
    }

    /**
     * 创建环境空气或无组织废气结果表
     * @param folderList 点位列表
     * @param totalTableIndex 结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createUnorganizedGasResultTable(List<EmcFolderBean> folderList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (folderList.isEmpty()) {
            return;
        }

        int gasTableIndex = totalTableIndex.getAndDecrement();
        boolean last = gasTableIndex == totalTableCount;
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        List<EmcReportResultBean> folderGasReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId()) && !StringUtils.contains(r.getTestName(), PfConstant.PARAMETER_TEST_MATCH_KEYWORD)).collect(Collectors.toList());
        List<LocalDate> sampleBeginDateList = folderGasReportResultList.stream().map(EmcReportResultBean ::getSampleBeginDate).collect(Collectors.toList());
        List<LocalDate> sampleBeginNotNullDateList = sampleBeginDateList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(!sampleBeginNotNullDateList.isEmpty()){
            folderGasReportResultList.sort(Comparator.comparing(EmcReportResultBean::getSampleBeginDate).reversed());
        };

        List<String> testNameList = folderGasReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
        List<List<String>> subList = new ArrayList<>(ListUtils.partition(testNameList, 4));
        Collections.reverse(subList);

        IntStream.range(0, subList.size()).forEach(index -> {
            List<String> curTestNameList = subList.get(index);
            List<EmcReportResultBean> curResultList = folderGasReportResultList.stream().filter(result -> curTestNameList.contains(result.getTestName())).collect(Collectors.toList());
            List<Long> curFolderIdList = curResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> curFolderList = folderList.stream().filter(folder -> curFolderIdList.contains(folder.getId())).collect(Collectors.toList());

            List<Long> curOrderIdList = curResultList.stream().map(EmcReportResultBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> curOrderIdList.contains(order.getId())).collect(Collectors.toList());
            List<LocalDate> sampleBeginDateOrderList = curOrderList.stream().map(EmcOrderBean::getSampleBeginDate).distinct().collect(Collectors.toList());
            boolean isFirst = (index == subList.size() - 1);
            boolean breakNewPage = (index == 0) && last;
            if (!sampleBeginDateOrderList.isEmpty()) {
                sampleBeginDateOrderList.sort(Comparator.reverseOrder());
                sampleBeginDateOrderList.forEach(s -> {
                    List<EmcOrderBean> curOrderNewList = curOrderList.stream().filter(o ->ObjectUtils.equals(s, o.getSampleBeginDate())).collect(Collectors.toList());
                    List<Long> folderNewIdList = curOrderNewList.stream().map(EmcOrderBean :: getFolderId).distinct().collect(Collectors.toList());
                    List<EmcFolderBean> curFolderNewList = curFolderList.stream().filter(f ->folderNewIdList.contains(f.getId())).collect(Collectors.toList());
                    List<EmcReportResultBean> curResultNewList = curResultList.stream().filter(res ->folderNewIdList.contains(res.getFolderId()))
                            .filter(res ->ObjectUtils.equals(s, res.getSampleBeginDate())).collect(Collectors.toList());
                    UnorganizedGasDynamicTable gasDynamicTable = new UnorganizedGasDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), curFolderNewList, curOrderNewList, curResultNewList, gasTableIndex, isFirst, !breakNewPage);
                    gasDynamicTable.create();;
                });
            } else {
                UnorganizedGasDynamicTable gasDynamicTable = new UnorganizedGasDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), curFolderList, curOrderList, curResultList, gasTableIndex, isFirst, !breakNewPage);
                gasDynamicTable.create();;
            }


        });
    }

    /**
     * 创建有组织废气结果表
     * @param folderList 点位列表
     * @param totalTableIndex 结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createOrganizedGasResultTable(List<EmcFolderBean> folderList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (folderList.isEmpty()) {
            return;
        }

        int gasTableIndex = totalTableIndex.getAndDecrement();
        boolean last = gasTableIndex == totalTableCount;
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        List<EmcReportResultBean> folderGasReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId()) && !StringUtils.contains(r.getTestName(), PfConstant.PARAMETER_TEST_MATCH_KEYWORD)).collect(Collectors.toList());
        List<LocalDate> sampleBeginDateList = folderGasReportResultList.stream().map(EmcReportResultBean ::getSampleBeginDate).collect(Collectors.toList());
        List<LocalDate> sampleBeginNotNullDateList = sampleBeginDateList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(!sampleBeginNotNullDateList.isEmpty()){
            folderGasReportResultList.sort(Comparator.comparing(EmcReportResultBean::getSampleBeginDate).reversed());
        };

        List<String> testNameList = folderGasReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
        List<List<String>> subList = new ArrayList<>(ListUtils.partition(testNameList, 2));
        Collections.reverse(subList);

        IntStream.range(0, subList.size()).forEach(index -> {
            List<String> curTestNameList = subList.get(index);
            List<EmcReportResultBean> curResultList = folderGasReportResultList.stream().filter(result -> curTestNameList.contains(result.getTestName())).collect(Collectors.toList());
            List<Long> curFolderIdList = curResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> curFolderList = folderList.stream().filter(folder -> curFolderIdList.contains(folder.getId())).collect(Collectors.toList());

            List<Long> curOrderIdList = curResultList.stream().map(EmcReportResultBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> curOrderIdList.contains(order.getId())).collect(Collectors.toList());

            boolean isFirst = (index == subList.size() - 1);
            boolean breakNewPage = (index == 0) && last;
            GasDynamicTable gasDynamicTable = new GasDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), curFolderList, curOrderList, curResultList,templateSourceDTO.getOrderTaskResultList(), gasTableIndex, isFirst, !breakNewPage);
            gasDynamicTable.create();
        });
    }

    /**
     * 创建有组织废气结果表
     * @param orderList 点位列表
     * @param totalTableIndex 结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createOrganizedGasResultTable0(List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (orderList.isEmpty()) {
            return;
        }

        int gasTableIndex = totalTableIndex.getAndDecrement();
        boolean last = gasTableIndex == totalTableCount;
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcReportResultBean> orderGasReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> orderIdList.contains(r.getOrderId()) && !StringUtils.contains(r.getTestName(), PfConstant.PARAMETER_TEST_MATCH_KEYWORD)).collect(Collectors.toList());
        List<LocalDate> sampleBeginDateList = orderGasReportResultList.stream().map(EmcReportResultBean ::getSampleBeginDate).collect(Collectors.toList());
        List<LocalDate> sampleBeginNotNullDateList = sampleBeginDateList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(!sampleBeginNotNullDateList.isEmpty()){
            orderGasReportResultList.sort(Comparator.comparing(EmcReportResultBean::getSampleBeginDate).reversed());
        }

        List<String> testNameList = orderGasReportResultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
        List<List<String>> subList = new ArrayList<>(ListUtils.partition(testNameList, 2));
        Collections.reverse(subList);

        IntStream.range(0, subList.size()).forEach(index -> {
            List<String> curTestNameList = subList.get(index);
            List<EmcReportResultBean> curResultList = orderGasReportResultList.stream().filter(result -> curTestNameList.contains(result.getTestName())).collect(Collectors.toList());
            List<Long> curFolderIdList = curResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
            List<EmcFolderBean> curFolderList = folderList.stream().filter(folder -> curFolderIdList.contains(folder.getId())).collect(Collectors.toList());

            List<Long> curOrderIdList = curResultList.stream().map(EmcReportResultBean::getOrderId).distinct().collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> curOrderIdList.contains(order.getId())).collect(Collectors.toList());

            boolean isFirst = (index == subList.size() - 1);
            boolean breakNewPage = (index == 0) && last;
            GasDynamicTable gasDynamicTable = new GasDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), curFolderList, curOrderList, curResultList,templateSourceDTO.getOrderTaskResultList(), gasTableIndex, isFirst, !breakNewPage);
            gasDynamicTable.create();
        });
    }

    /**
     * 创建有组织废气结果表
     * @param orderList 点位列表
     * @param gasTableIndex 结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createOrganizedGasResultTable(List<EmcFolderBean> folderList, List<EmcOrderBean> orderList,Boolean displayParameterFinal, int gasTableIndex, int totalTableCount) {
        if (orderList.isEmpty()) {
            return;
        }

        boolean last = gasTableIndex == totalTableCount+1;
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcReportResultBean> orderGasReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> orderIdList.contains(r.getOrderId()) && !StringUtils.contains(r.getTestName(), PfConstant.PARAMETER_TEST_MATCH_KEYWORD)).collect(Collectors.toList());

        // 其他结果
        List<EmcReportResultBean> validReportResultList = orderGasReportResultList.stream().filter(r -> !StringUtils.equals(r.getExt$Item("reportLimitType"), "samplingVolume")).collect(Collectors.toList());

        // 过滤出需要根据采样体积来判断的结果
        List<EmcReportResultBean> dynamicResultList = orderGasReportResultList.stream().filter(r -> StringUtils.equals(r.getExt$Item("reportLimitType"), "samplingVolume")).collect(Collectors.toList());
        Map<String, List<EmcReportResultBean>> testResultMap = dynamicResultList.stream().collect(Collectors.groupingBy(r -> r.getOrderNo() + r.getTestId()));
        testResultMap.forEach((testKey, resultList) -> {
            EmcReportResultBean samplingVolumeResult = resultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.GAS_SAMPLING_VOLUME_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
            boolean validVolume = NumberUtils.isNumber(samplingVolumeResult.getReportResult());
            resultList.forEach(r -> {
                if (sampling) {
                    r.setExt$Item("reportLimitType", validVolume ? "lowLimit" : "lowValue");
                } else {
                    // 送样有体积，出样品浓度，送样无体积，出样品含量
                    r.setExt$Item("reportLimitType", validVolume ? "lowValue" : "lowValue2");
                }
            });
            validReportResultList.addAll(resultList);
        });

        // 如果没有设置报告检出类型，默认为按检出限
        validReportResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("reportLimitType"))).forEach(r -> r.setExt$Item("reportLimitType", "lowLimit"));

        // 将报告结果按检出限或者检出量分组
        Map<String, List<EmcReportResultBean>> reportLimitResultMap = validReportResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("reportLimitType")));

        AtomicInteger curTableIndex = new AtomicInteger();
        reportLimitResultMap.forEach((reportLimitType, resultList) -> {
            List<String> testNameList = resultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
            List<List<String>> subList = new ArrayList<>(ListUtils.partition(testNameList, 2));
            curTableIndex.addAndGet(subList.size());
        });

        List<EmcOrderTaskResultBean> orderTaskResultBeans = templateSourceDTO.getOrderTaskResultList();
        List<EmcOrderTaskBean> orderTaskBeans = templateSourceDTO.getOrderTaskList();
        reportLimitResultMap.forEach((reportLimitType, resultList) -> {
            List<String> testNameList = resultList.stream().map(EmcReportResultBean::getTestName).distinct().collect(Collectors.toList());
            List<List<String>> subList = new ArrayList<>(ListUtils.partition(testNameList, 2));
            Collections.reverse(subList);

            IntStream.range(0, subList.size()).forEach(index -> {
                List<String> curTestNameList = subList.get(index);
                List<EmcReportResultBean> curResultList = resultList.stream().filter(result -> curTestNameList.contains(result.getTestName())).collect(Collectors.toList());
                List<Long> curFolderIdList = curResultList.stream().map(EmcReportResultBean::getFolderId).distinct().collect(Collectors.toList());
                List<EmcFolderBean> curFolderList = folderList.stream().filter(folder -> curFolderIdList.contains(folder.getId())).collect(Collectors.toList());

                List<Long> curOrderIdList = curResultList.stream().map(EmcReportResultBean::getOrderId).distinct().collect(Collectors.toList());
                List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> curOrderIdList.contains(order.getId())).collect(Collectors.toList());

                List<Long> orderTaskIdList = curResultList.stream().map(EmcReportResultBean :: getOrderTaskId).distinct().collect(Collectors.toList());
                List<EmcOrderTaskBean> gasOrderTaskList = orderTaskBeans.stream().filter(ok ->orderTaskIdList.contains(ok.getId())).distinct().collect(Collectors.toList());
                List<EmcOrderTaskResultBean> gasOrderTaskResultList = orderTaskResultBeans.stream().filter(res ->orderTaskIdList.contains(res.getOrderTaskId())).distinct().collect(Collectors.toList());

                boolean isFirst = curTableIndex.decrementAndGet() == 0;
                boolean breakNewPage = (index == 0) && last;
                if(displayParameterFinal){
                    //根据样品测试循环，拿到每个样品测试不同的管道数据组合
                    //不同测试的结果 -- 报出结果
                    List<String> gasParamStrList = new ArrayList<>();
                    Map<Long,String> orderTaskParamMatchMap = new LinkedHashMap<>();
                    gasOrderTaskList.forEach(ok ->{
                        List<String> gasSingleTaskParamStrList = new ArrayList<>();
                        List<EmcOrderTaskResultBean> orderTaskResultParamList = gasOrderTaskResultList.stream().filter(res ->ObjectUtils.equals(res.getOrderTaskId(),ok.getId())).distinct().collect(Collectors.toList());
                        Arrays.stream(PfConstant.GAS_PARAMETER_RESULT_KEYWORDS).forEach(keyword -> {
                            EmcOrderTaskResultBean orderTaskResult = orderTaskResultParamList.stream().filter(result -> StringUtils.contains(result.getItemAliasName(), keyword)).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                            gasSingleTaskParamStrList.add(orderTaskResult.getReportResult());
                        });
                        String singleTaskStr = StringUtils.join(gasSingleTaskParamStrList,",");
                        orderTaskParamMatchMap.put(ok.getId(),singleTaskStr);
                        if(!gasParamStrList.contains(singleTaskStr)){
                            gasParamStrList.add(singleTaskStr);
                        }
                    });
                    //创建的时候判断是否已经有过，那我外边一定有个去重的mapper

                    if(!gasParamStrList.isEmpty()){
                        GasParameterDynamicTable parameterDynamicTable = new GasParameterDynamicTable(document, curFolderList, curOrderList,curResultList,gasOrderTaskResultList, true);
                        parameterDynamicTable.create();
                    }
                }
                GasDynamicTable gasDynamicTable = new GasDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), curFolderList, curOrderList, curResultList,templateSourceDTO.getOrderTaskResultList(), gasTableIndex, isFirst, !breakNewPage);
                gasDynamicTable.create();

            });
        });
    }

    /**
     * 创建土壤检测结果表
     * @param soilMonitorTmplList 土壤类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createSoilResultTable(List<EmcMonitorTmplBean> soilMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (soilMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = soilMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        boolean sampling = Constant.YES.equals(templateSourceDTO.getProject().getSample());

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(soilMonitorTmplList, templateSourceDTO.getFolderList());
        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> folderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);

            int soilTableIndex = totalTableIndex.getAndDecrement();
            boolean last = soilTableIndex == totalTableCount;

            folderList.sort(Comparator.comparing(EmcFolderBean::getId).reversed());

            List<Long> curFolderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcOrderBean> curFolderOrderList = templateSourceDTO.getOrderList().stream().filter(order -> curFolderIdList.contains(order.getFolderId())).collect(Collectors.toList());

            // 过滤掉关联测试
            List<EmcOrderTaskBean> validTaskList = templateSourceDTO.getOrderTaskList().stream().filter(task -> curFolderIdList.contains(task.getFolderId()) && !StringUtils.equals(task.getExt$Item("relatedTest"), Constant.YES)).collect(Collectors.toList());
            List<Long> validTaskIdList = validTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
            List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> curFolderIdList.contains(r.getFolderId()) && validTaskIdList.contains(r.getOrderTaskId())).collect(Collectors.toList());

            // 按样品下的测试数量进行分组
            curFolderOrderList.forEach(order -> {
                long curTestCount = validTaskList.stream().filter(task -> ObjectUtils.equals(task.getOrderId(), order.getId())).map(EmcOrderTaskBean::getTestId).distinct().count();
                order.setExt$Item("testCount", curTestCount + "");
            });
            curFolderOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo).reversed());
            Map<String, List<EmcOrderBean>> curFolderOrderMap = curFolderOrderList.stream().collect(Collectors.groupingBy(order -> order.getExt$Item("testCount"), LinkedHashMap::new, Collectors.toList()));

            AtomicInteger countIndex = new AtomicInteger();
            curFolderOrderMap.forEach((testCount, curCountOrderList) -> {
                curCountOrderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo));
                List<List<EmcOrderBean>> subOrderList = new ArrayList<>(ListUtils.partition(curCountOrderList, PfConstant.MAX_COLUMN_QTY));
                Collections.reverse(subOrderList);
                boolean isFirst = (countIndex.getAndIncrement() == curFolderOrderMap.size() - 1);

                IntStream.range(0, subOrderList.size()).forEach(subIndex -> {
                    List<EmcOrderBean> partOrderList = subOrderList.get(subIndex);
                    List<Long> partOrderIdList = partOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                    List<Long> partFolderIdList = partOrderList.stream().map(EmcOrderBean::getFolderId).distinct().collect(Collectors.toList());
                    List<EmcFolderBean> partFolderList = folderList.stream().filter(folder -> partFolderIdList.contains(folder.getId())).collect(Collectors.toList());

                    List<EmcReportResultBean> partResultList = folderReportResultList.stream().filter(result -> partOrderIdList.contains(result.getOrderId())).collect(Collectors.toList());

                    List<EmcOrderTaskBean> partTaskList = templateSourceDTO.getOrderTaskList().stream().filter(task -> validTaskIdList.contains(task.getId()) && partOrderIdList.contains(task.getOrderId())).collect(Collectors.toList());

                    boolean isFirstTable = isFirst && (subIndex == subOrderList.size() - 1);

                    if (!sampling) {
                        NonSamplingSoilDynamicTable soilDynamicTable = new NonSamplingSoilDynamicTable(document, templateSourceDTO.getReport(), partFolderList, partOrderList, partTaskList, partResultList, templateSourceDTO.getIndexCategoryList(), soilTableIndex, isFirstTable, false);
                        soilDynamicTable.setGroupItemList(templateSourceDTO.getGroupItemList());
                        soilDynamicTable.setItemGroupList(templateSourceDTO.getItemGroupList());
                        soilDynamicTable.create();
                    } else {
                        SoilDynamicTable soilDynamicTable = new SoilDynamicTable(document, templateSourceDTO.getProject(), templateSourceDTO.getReport(), partFolderList, partOrderList, partResultList, soilTableIndex, isFirst, false);
                        soilDynamicTable.setGroupItemList(templateSourceDTO.getGroupItemList());
                        soilDynamicTable.setItemGroupList(templateSourceDTO.getItemGroupList());
                        soilDynamicTable.create();
                    }
                });
            });
        });
    }

    /**
     * 创建噪声检测结果表
     * @param noiseMonitorTmplList 噪声类监测模板列表
     * @param totalTableIndex 当前结果表索引
     * @param totalTableCount 结果表总数
     */
    private void createNoiseResultTable(List<EmcMonitorTmplBean> noiseMonitorTmplList, AtomicInteger totalTableIndex, int totalTableCount) {
        if (noiseMonitorTmplList.isEmpty()) {
            return;
        }

        List<String> monitorTmplNameList = noiseMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<EmcOrderBean> curOrderList = templateSourceDTO.getOrderList().stream().filter(order -> monitorTmplNameList.contains(order.getOrderCategory())).collect(Collectors.toList());
        Map<String, LocalDate> minDateMap = sortByDate(curOrderList, false);

        Map<String, List<EmcFolderBean>> folderListMap = groupingFolderByOrderCategory(noiseMonitorTmplList, templateSourceDTO.getFolderList());

        minDateMap.forEach((orderCategory, minDate) -> {
            List<EmcFolderBean> curFolderList = Optional.ofNullable(folderListMap.get(orderCategory)).orElseGet(ArrayList::new);

            int noiseTableIndex = totalTableIndex.getAndDecrement();
            boolean last = noiseTableIndex == totalTableCount;
            List<Long> folderIdList = curFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            List<EmcReportResultBean> folderReportResultList = templateSourceDTO.getReportResultList().stream().filter(r -> folderIdList.contains(r.getFolderId())).collect(Collectors.toList());

            NoiseDynamicTable noiseDynamicTable = new NoiseDynamicTable(document, templateSourceDTO.getReport(), curFolderList, folderReportResultList, noiseTableIndex, true, last);
            noiseDynamicTable.create();
        });
    }

    private Map<String, LocalDate> sortResult(List<EmcMonitorTmplBean> waterMonitorTmplList, List<EmcMonitorTmplBean> gasMonitorTmplList, List<EmcMonitorTmplBean> soilMonitorTmplList, List<EmcMonitorTmplBean> noiseMonitorTmplList, List<EmcOrderBean> orderList) {
        Map<String, LocalDate> sortedDateMap = sortByDate(orderList, false);
        List<String> waterTmplNameList = waterMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> gasTmplNameList = gasMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> soilTmplNameList = soilMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());
        List<String> noiseTmplNameList = noiseMonitorTmplList.stream().map(EmcMonitorTmplBean::getMonitorTmplName).collect(Collectors.toList());

        Map<String, LocalDate> minDateMap = new LinkedHashMap<>(3);
        sortedDateMap.forEach((orderCategory, minDate) -> {
            if (waterTmplNameList.contains(orderCategory)) {
                LocalDate waterDate = Optional.ofNullable(minDateMap.get(PfConstant.WATER_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.WATER_KEYWORD, waterDate.isBefore(minDate) ? waterDate : minDate);
            }
            if (gasTmplNameList.contains(orderCategory)) {
                LocalDate gasDate = Optional.ofNullable(minDateMap.get(PfConstant.GAS_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.GAS_KEYWORD, gasDate.isBefore(minDate) ? gasDate : minDate);
            }
            if (soilTmplNameList.contains(orderCategory)) {
                LocalDate soilDate = Optional.ofNullable(minDateMap.get(PfConstant.SOIL_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.SOIL_KEYWORD, soilDate.isBefore(minDate) ? soilDate : minDate);
            }
            if (noiseTmplNameList.contains(orderCategory)) {
                LocalDate soilDate = Optional.ofNullable(minDateMap.get(PfConstant.NOISE_KEYWORD)).orElse(minDate);
                minDateMap.put(PfConstant.NOISE_KEYWORD, soilDate.isBefore(minDate) ? soilDate : minDate);
            }
        });

        return minDateMap;
    }

}
