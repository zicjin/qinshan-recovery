package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateMonitorItemDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * 检测项目表（采样通用）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class MonitorItemDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcProjectBean project;
    private EmcReportBean report;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcEquiptBean> equiptList;
    private List<EmcRunBean> runList;
    private List<CoreCodeBean> coreCodeList;
    private int tableIndex;
    private boolean isDioxin;
    private boolean sampling;

    public MonitorItemDynamicTable(XWPFDocument document, EmcProjectBean project, EmcReportBean report, List<EmcOrderTaskBean> orderTaskList, List<EmcReportResultBean> reportResultList, List<EmcEquiptBean> equiptList, List<EmcRunBean> runList, List<CoreCodeBean> coreCodeList, int tableIndex) {
        this.document = document;
        this.project = project;
        this.report = report;
        this.orderTaskList = orderTaskList;
        this.reportResultList = reportResultList;
        this.equiptList = equiptList;
        this.runList = runList;
        this.coreCodeList = coreCodeList;
        this.tableIndex = tableIndex;
        this.isDioxin = StringUtils.equalsIgnoreCase(project.getBizCategoryCode(), "C");
        this.sampling = "1".equals(project.getSample());
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format("表%d 检测依据、仪器一览表", tableIndex);
    }

    @Override
    public int getTableIndex() {
        return 3;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return document.getTables().size() - 1;
    }

    @Override
    public int getTableWidth() {
        return 10204;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.MONITOR_ITEM_TABLE_HEADER_TITLES, 4, null);
        setTableTitle(PfConstant.MONITOR_ITEM_TABLE_HEADER_TITLES, 4, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return Arrays.asList(2000, 0, 0, 1000);
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        List<WordTemplateMonitorItemDTO> monitorItemDTOList = new ArrayList<>();

        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream()
                .filter(result -> StringUtils.equals(result.getDataType(), "sample"))
                .collect(Collectors.toList());
        sampleReportResultList.stream()
                .filter(result -> StringUtils.isEmpty(result.getExt$Item("itemGroup")))
                .forEach(result -> result.setExt$Item("itemGroup", result.getTestName()));
        Map<Long, String> sampleEquiptMap = equiptList.stream().collect(Collectors.toMap(EmcEquiptBean::getId, equipt -> equipt.getEquiptName() + "\n" + equipt.getSpecification() + "\n" + "（" + equipt.getEquiptId() + "）", (k1, k2) -> k1));
        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream()
                .filter(r -> !StringUtils.contains(r.getTestName(), "参数")
                        && !StringUtils.contains(r.getTestName(), "水分含量")
                        && !StringUtils.contains(r.getExt$Item("relatedTest"), "1")
                        && "N/A".equals(r.getQcCategoryCode()))
                .collect(Collectors.toList());
        if (isDioxin) {
            validOrderTaskList = validOrderTaskList.stream()
                    .filter(task -> !StringUtils.contains(task.getTestName(), "含水率"))
                    .collect(Collectors.toList());
        }

        Map<String, EmcRunBean> runMap = runList.stream().collect(Collectors.toMap(EmcRunBean::getId, Function.identity()));

        Map<String, String> testItemSortCodeMap = coreCodeList.stream().filter(code -> StringUtils.equals(code.getCodeCategoryId(), "testitemsort")).collect(Collectors.toMap(CoreCodeBean::getCode, CoreCodeBean::getCodeName, (v1, v2) -> v1));
        Map<String, Long> testItemSortNumMap = coreCodeList.stream().filter(code -> StringUtils.equals(code.getCodeCategoryId(), "testitemsort")).collect(Collectors.toMap(CoreCodeBean::getCode, code -> Optional.ofNullable(code.getOrderNo()).orElse(code.getId()), (v1, v2) -> v1));

        // 按样品类型进行分组
        validOrderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getOrderCategory));
        Map<String, List<EmcOrderTaskBean>> orderTaskMap = validOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getOrderCategory, LinkedHashMap::new, Collectors.toList()));
        orderTaskMap.forEach((orderCategory, curOrderTaskList) -> {
        	curOrderTaskList.stream().filter(task -> StringUtils.isEmpty(task.getExt$Item("itemGroup"))).forEach(task -> task.setExt$Item("itemGroup", task.getTestName()));

            Map<String, List<EmcOrderTaskBean>> testOrderTaskMap = curOrderTaskList.stream().collect(Collectors.groupingBy(task -> task.getTestId() + "$" + task.getTestMethodId()+ "$" + task.getExt$Item("itemGroup")));

            // 按分析科室进行排序展示
            Set<String> uniqueTestIdSet = new HashSet<>();

            curOrderTaskList.stream().filter(task -> StringUtils.isEmpty(task.getAnalysisOrgId())).forEach(task -> task.setAnalysisOrgId(""));

            WordTemplateUtils.sortedByAnalysisOrg(curOrderTaskList);

            Map<String, List<EmcOrderTaskBean>> orgOrderTaskMap = curOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getAnalysisOrgId, LinkedHashMap::new, Collectors.toList()));
            orgOrderTaskMap.forEach((analysisOrgId, orgTaskList) -> {
                // 根据测试ID + 方法ID去重（一个任务中同一测试可能有多个方法），生成检测项目表数据
                orgTaskList.stream().filter(task -> StringUtils.isEmpty(task.getExt$Item("itemGroup"))).forEach(task -> task.setExt$Item("itemGroup", task.getTestName()));
                ArrayList<EmcOrderTaskBean> uniqueTaskList = orgTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(task -> task.getExt$Item("itemGroup")))), ArrayList::new));
                List<EmcOrderTaskBean> testItemSortTaskList = uniqueTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getExt$Item("testItemSort"))).collect(Collectors.toList());

                // 有设置检测项目分类的，按系统编码的维护顺序排序
                testItemSortTaskList.forEach(task -> task.setExt$Item("testItemSortNum", Optional.ofNullable(testItemSortNumMap.get(task.getExt$Item("testItemSort"))).orElse((long) testItemSortNumMap.size()) + ""));
                testItemSortTaskList.sort(Comparator.comparing(task -> NumberUtils.parseLong(task.getExt$Item("testItemSortNum"))));

                List<Long> testItemSortTaskIdList = testItemSortTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                Map<String, List<EmcOrderTaskBean>> mergedTaskMap = testItemSortTaskList.stream().collect(Collectors.groupingBy(task -> String.join("$", task.getExt$Item("testItemSort"), task.getTestMethodBasis(), task.getEquiptId() + ""), LinkedHashMap::new, Collectors.toList()));

                // 未设置检测项目分类的测试
                List<EmcOrderTaskBean> otherTaskList = uniqueTaskList.stream().filter(task -> !testItemSortTaskIdList.contains(task.getId())).collect(Collectors.toList());

                // 设置了检测项目分类的，合并显示
                mergedTaskMap.forEach((groupKey, curTaskList) -> {
                    EmcOrderTaskBean firstTask = curTaskList.get(0);
                    String key = firstTask.getTestId() + "$" + firstTask.getTestMethodId()+ "$" + firstTask.getExt$Item("itemGroup");
                    if (uniqueTestIdSet.contains(groupKey)) {
                        return;
                    }

                    uniqueTestIdSet.add(groupKey);
                    WordTemplateMonitorItemDTO monitorItemDTO = new WordTemplateMonitorItemDTO();

                    String itemSortCode = groupKey.substring(0, groupKey.indexOf("$"));
                    String sortName = testItemSortCodeMap.get(itemSortCode);
                    if (StringUtils.isEmpty(sortName)) {
                        return;
                    }

                    monitorItemDTO.setTestName(sortName);
                    monitorItemDTO.setTestMethodName(firstTask.getTestMethodBasis());

                    List<String> sampleDevices = getSampleDevices(testOrderTaskMap.get(key), sampleEquiptMap);
                    String analysisDevice = getAnalysisDevice(testOrderTaskMap.get(key), sampleEquiptMap);
                    List<String> weighingDevices = getWeighingDevice(runMap.get(firstTask.getRunId()), sampleEquiptMap);

                    Set<String> deviceSet = new HashSet<>();
                    // 既有采样仪器又有分析仪器的测试，不显示采样仪器，只有现场检测的才显示采样仪器
                    if (StringUtils.isEmpty(analysisDevice) && !sampleDevices.isEmpty()) {
                        deviceSet.addAll(sampleDevices);
                    }
                    if (!StringUtils.isEmpty(analysisDevice)) {
                        deviceSet.add(analysisDevice);
                    }
                    deviceSet.addAll(weighingDevices);

                    monitorItemDTO.setDetectDevice(StringUtils.contains(analysisDevice, EmcConstant.N_A) ? null : (deviceSet.isEmpty() ? null : String.join("、", deviceSet)));

                    List<EmcReportResultBean> itemGroupReportResultList = sampleReportResultList.stream().filter(e -> ObjectUtils.equals(e.getOrderTaskId(), firstTask.getId()) && ObjectUtils.equals(e.getExt$Item("itemGroup"), firstTask.getExt$Item("itemGroup"))).collect(Collectors.toList());
                    List<EmcReportResultBean> lowLimitNotEmptyList = itemGroupReportResultList.stream().filter(e -> !StringUtils.isEmpty(e.getLowLimit())).collect(Collectors.toList());
                    EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
                    String limitValue = StringUtils.isBlank(limitResult.getLowLimit()) ? "-" : limitResult.getLowLimit() + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
                    monitorItemDTO.setLowLimit(limitValue);

                    monitorItemDTOList.add(monitorItemDTO);
                });

                // 未设置检测项目分类，依然单独显示
                otherTaskList.forEach(t -> {
                    String key = t.getTestId() + "$" + t.getTestMethodId()+ "$" + t.getExt$Item("itemGroup");

                    if (uniqueTestIdSet.contains(key)) {
                        return;
                    }

                    uniqueTestIdSet.add(key);
                    WordTemplateMonitorItemDTO monitorItemDTO = new WordTemplateMonitorItemDTO();
                    String itemGroup = StringUtils.replace(t.getExt$Item("itemGroup"), PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
                    monitorItemDTO.setTestName(itemGroup);
                    monitorItemDTO.setTestMethodName(t.getTestMethodBasis());

                    List<String> sampleDevices = getSampleDevices(testOrderTaskMap.get(key), sampleEquiptMap);
                    String analysisDevice = getAnalysisDevice(testOrderTaskMap.get(key), sampleEquiptMap);
                    List<String> weighingDevices = getWeighingDevice(runMap.get(t.getRunId()), sampleEquiptMap);

                    Set<String> deviceSet = new HashSet<>();
                    // 既有采样仪器又有分析仪器的测试，不显示采样仪器，只有现场检测的才显示采样仪器
                    if (StringUtils.isEmpty(analysisDevice) && !sampleDevices.isEmpty()) {
                        deviceSet.addAll(sampleDevices);
                    }
                    if (!StringUtils.isEmpty(analysisDevice)) {
                        deviceSet.add(analysisDevice);
                    }
                    deviceSet.addAll(weighingDevices);

                    monitorItemDTO.setDetectDevice(StringUtils.contains(analysisDevice, EmcConstant.N_A) ? null : (deviceSet.isEmpty() ? null : String.join("、", deviceSet)));

                    List<EmcReportResultBean> itemGroupReportResultList = sampleReportResultList.stream().filter(e -> ObjectUtils.equals(e.getOrderTaskId(), t.getId()) && ObjectUtils.equals(e.getExt$Item("itemGroup"), t.getExt$Item("itemGroup"))).collect(Collectors.toList());
                    List<EmcReportResultBean> lowLimitNotEmptyList = itemGroupReportResultList.stream().filter(e -> !StringUtils.isEmpty(e.getLowLimit())).collect(Collectors.toList());
                    EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
                    String limitValue = StringUtils.isBlank(limitResult.getLowLimit()) ? "-" : limitResult.getLowLimit() + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
                    monitorItemDTO.setLowLimit(limitValue);

                    monitorItemDTOList.add(monitorItemDTO);
                });

            });
        });

        // 按照样品类型、检测依据和检测仪器分组
        Map<String, List<WordTemplateMonitorItemDTO>> monitorItemGroupMap = monitorItemDTOList.stream().collect(Collectors.groupingBy(dto -> String.join("$", dto.getTestMethodName(), dto.getDetectDevice()), LinkedHashMap::new, Collectors.toList()));
        List<WordTemplateMonitorItemDTO> monitorItemGroupList = new ArrayList<>(monitorItemGroupMap.size());
        monitorItemGroupMap.forEach((key, curMonitorItemList) -> {
            WordTemplateMonitorItemDTO basicMonitorItem = curMonitorItemList.get(0);
            WordTemplateMonitorItemDTO monitorItemDTO = new WordTemplateMonitorItemDTO();
            String testName = curMonitorItemList.stream().map(WordTemplateMonitorItemDTO::getTestName).distinct().collect(Collectors.joining("、"));
            monitorItemDTO.setTestName(testName);
            monitorItemDTO.setTestMethodName(basicMonitorItem.getTestMethodName());
            monitorItemDTO.setDetectDevice(basicMonitorItem.getDetectDevice());
            monitorItemDTO.setLowLimit(basicMonitorItem.getLowLimit());
            monitorItemGroupList.add(monitorItemDTO);
        });

        monitorItemGroupList.forEach(m -> {
            List<BeanPropertyDescriptor> descriptors = BeanPropertyHelper.getBeanPropertyDescriptorList(WordTemplateMonitorItemDTO.class);
            List<Object> rowDataList = descriptors.stream().map(d -> d.getPropertyValue(m)).collect(Collectors.toList());
            tableBodyDataList.add(rowDataList);
        });

        // 常规报告需要增加表格备注
        if (!isDioxin) {
            dynamicTableDTO.setTableRemarkList(buildTableRemark());
        }

        return tableBodyDataList;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();

        dynamicTableDTO.setFontStyle("Times New Roman");
        List<List<Object>> tableDataList = new ArrayList<>();
        tableDataList.addAll(buildTableHeader(dynamicTableDTO));
        tableDataList.addAll(buildTableBody(dynamicTableDTO));
        WordTemplateUtils.listListNullToString(tableDataList, PfConstant.NULL_REPLACED_VALUE);

        dynamicTableDTO.setTableDataList(tableDataList);
        dynamicTableDTO.setReplaceKeyword(EmcConstant.MONITOR_TABLE_PLACEHOLDER);

        return dynamicTableDTO;
    }

    @SuppressWarnings("unused")
	private String getSampleDevice(EmcOrderTaskBean orderTask, Map<Long, String> sampleEquiptMap) {
        String samplingEquiptId = orderTask.getSamplingEquiptId();
        Long equiptId = orderTask.getEquiptId();

        // 采样设备包含两种，一种是现场使用的取样设备如取水器，另外就是现场直接分析设备如pH计
        List<String> equiptNameList = new ArrayList<>();
        if (!StringUtils.isEmpty(samplingEquiptId)) {
            String[] sampleEquiptIdList = StringUtils.split(samplingEquiptId, ",");
            Arrays.stream(sampleEquiptIdList).forEach(sampleEquiptId -> {
                Optional.ofNullable(sampleEquiptMap.get(NumberUtils.parseLong(sampleEquiptId))).ifPresent(equiptNameList::add);
            });
        }

        if (StringUtils.equals(orderTask.getAnalysisType(), EmcBizConstant.ANALYSISCATEGORY_SAMPLING) && !StringUtils.contains(sampleEquiptMap.get(equiptId), EmcConstant.EQUIPMENT_NA)) {
            equiptNameList.add(sampleEquiptMap.get(equiptId));
        }

        if (equiptNameList.isEmpty()) {
            return null;
        }

        return String.join("、", equiptNameList);
    }

    @SuppressWarnings("unused")
    private String getSampleDevice(List<EmcOrderTaskBean> orderTaskList, Map<Long, String> sampleEquiptMap) {
        return String.join("、", getSampleDevices(orderTaskList, sampleEquiptMap));
    }

    private List<String> getSampleDevices(List<EmcOrderTaskBean> orderTaskList, Map<Long, String> sampleEquiptMap) {
        List<EmcOrderTaskBean> uniqueEquiptTaskList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getEquiptId())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getEquiptId))), ArrayList::new));
        List<String> samplingEquiptIdList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getSamplingEquiptId())).map(EmcOrderTaskBean::getSamplingEquiptId).distinct().collect(Collectors.toList());

        // 采样设备包含两种，一种是现场使用的取样设备如取水器，另外就是现场直接分析设备如pH计
        List<String> equiptNameList = new ArrayList<>();

        samplingEquiptIdList.forEach(samplingEquiptId -> {
            String[] sampleEquiptIdList = StringUtils.split(samplingEquiptId, ",");
            Arrays.stream(sampleEquiptIdList).forEach(sampleEquiptId -> Optional.ofNullable(sampleEquiptMap.get(NumberUtils.parseLong(sampleEquiptId))).ifPresent(equiptNameList::add));
        });

        uniqueEquiptTaskList.forEach(orderTask -> {
            if (StringUtils.equals(orderTask.getAnalysisType(), EmcBizConstant.ANALYSISCATEGORY_SAMPLING) && !StringUtils.contains(sampleEquiptMap.get(orderTask.getEquiptId()), EmcConstant.EQUIPMENT_NA)) {
                equiptNameList.add(sampleEquiptMap.get(orderTask.getEquiptId()));
            }
        });

        return equiptNameList;
    }

    private String getAnalysisDevice(List<EmcOrderTaskBean> orderTaskList, Map<Long, String> sampleEquiptMap) {
        if (null == orderTaskList) {
            return null;
        }

        List<Long> equiptIdList = orderTaskList.stream().filter(task -> StringUtils.equals(task.getAnalysisType(), EmcBizConstant.ANALYSISCATEGORY_ANALYSIS)).map(EmcOrderTaskBean::getEquiptId).distinct().collect(Collectors.toList());
        Set<String> equiptSet = new HashSet<>();
        equiptIdList.forEach(equiptId -> Optional.ofNullable(sampleEquiptMap.get(equiptId)).ifPresent(equiptSet::add));

        return equiptSet.isEmpty() ? null : String.join("、", equiptSet);
    }

    private List<String> getWeighingDevice(EmcRunBean run, Map<Long, String> sampleEquiptMap) {
        if (run == null || StringUtils.isEmpty(run.getExt$Item("weighingEquiptId"))) {
            return Collections.emptyList();
        }

        // 获取批上的称重设备
        String weighingEquiptId = run.getExt$Item("weighingEquiptId");
        List<Long> deviceIdList = Arrays.stream(StringUtils.split(weighingEquiptId, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());

        List<String> excludeDeviceList = Arrays.asList(PfConstant.EXCLUDE_DEVICE_KEYWORDS);

        List<String> deviceList = new ArrayList<>(deviceIdList.size());
        deviceIdList.forEach(deviceId -> {
            String deviceName = sampleEquiptMap.get(deviceId);
            // 常规的需要排除特定的仪器设备
            if (!isDioxin) {
                boolean exclude = excludeDeviceList.stream().anyMatch(keyword -> StringUtils.contains(deviceName, keyword));
                if (exclude) {
                    return;
                }
            }
            deviceList.add(deviceName);
        });

        return deviceList;
    }

    private List<String> buildTableRemark() {
        List<String> tableRemarks = new ArrayList<>();
        if (!sampling) {
            boolean gas = isGas();
            boolean hasGas = hasGas();
            boolean zoneOfAeration = isZoneOfAeration();
            if (gas) {
                tableRemarks.add(PfConstant.DELIVERY_TABLE_REMARK_2);
            } else {
                if (hasGas) {
                    tableRemarks.add(PfConstant.DELIVERY_TABLE_REMARK_3);
                } else {
                    if (zoneOfAeration) {
                        tableRemarks.add(PfConstant.DELIVERY_TABLE_REMARK_4);
                    }
                }
            }
        }

        boolean containSlash = reportResultList.stream().anyMatch(r -> "/".equals(r.getReportResult()));
        if (!sampling && hasGas()) {
            // 对于送样的气的项目，需要排查折算浓度和排放速率
            List<String> excludedKeywords = Arrays.asList(PfConstant.GAS_EXCLUDED_RESULT_KEYWORDS);
            containSlash = reportResultList.stream().filter(r -> excludedKeywords.stream().noneMatch(k -> StringUtils.contains(r.getItemAliasName(), k))).anyMatch(r -> "/".equals(r.getReportResult()));
        }

        if (containSlash) {
            tableRemarks.add(PfConstant.SLASH_TABLE_REMARK);
        }

        boolean containNd = reportResultList.stream().anyMatch(r -> "ND".equals(r.getReportResult()));
        if (containNd) {
            tableRemarks.add(PfConstant.ND_TABLE_REMARK);
        }

        boolean containLowLimit = reportResultList.stream().anyMatch(r -> StringUtils.endsWith(r.getReportResult(), "L"));
        if (containLowLimit) {
            tableRemarks.add(PfConstant.LOW_LIMIT_TABLE_REAMRK);
        }

        // 添加前处理备注
        String pretreatment = project.getPretreatment();
        if (!StringUtils.isEmpty(pretreatment) && !StringUtils.equals(EmcReportConstant.NO_REMARK, pretreatment)) {
            tableRemarks.add(String.format(EmcReportConstant.SOLID_WASTE_PRETREATMENT_REMARK_PREFIX, pretreatment));
        }

        // 报告上的备注
        if (!StringUtils.isEmpty(report.getRemark())) {
            tableRemarks.add(report.getRemark());
        }

        return tableRemarks;
    }

    private boolean isGas() {
        List<String> orderBeforeCategories = reportResultList.stream().map(EmcReportResultBean::getOrderCategory).distinct().collect(Collectors.toList());
        List<String> orderCategories = orderBeforeCategories.stream().filter(t -> !StringUtils.equals(t, PfConstant.DIOXIN_ZONEOFAERATION)).collect(Collectors.toList());
        if(orderCategories.isEmpty()){
        	return false;
        }
        return Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> orderCategories.stream().allMatch(oc -> StringUtils.contains(oc, keyword)));
    }

    private boolean hasGas() {
        List<String> orderBeforeCategories = reportResultList.stream().map(EmcReportResultBean::getOrderCategory).distinct().collect(Collectors.toList());
        List<String> orderCategories = orderBeforeCategories.stream().filter(t -> !StringUtils.equals(t, PfConstant.DIOXIN_ZONEOFAERATION)).collect(Collectors.toList());
        if(orderCategories.isEmpty()){
        	return false;
        }
        return Arrays.stream(PfConstant.DIOXIN_GAS_KEYWORDS).anyMatch(keyword -> orderCategories.stream().anyMatch(oc -> StringUtils.contains(oc, keyword)));
    }
    //是否包气带
    private boolean isZoneOfAeration() {
        List<String> orderCategories = reportResultList.stream().map(EmcReportResultBean::getOrderCategory).distinct().collect(Collectors.toList());
        return Arrays.stream(PfConstant.DIOXIN_ZONEOFAERATION_KEYWORDS).anyMatch(keyword -> orderCategories.stream().allMatch(oc -> StringUtils.contains(oc, keyword)));
    }


    private boolean isWater() {
        List<String> orderCategories = reportResultList.stream().map(EmcReportResultBean::getOrderCategory).distinct().collect(Collectors.toList());
        return Arrays.stream(PfConstant.DIOXIN_WATER_KEYWORDS).anyMatch(keyword -> orderCategories.stream().allMatch(oc -> StringUtils.contains(oc, keyword)));
    }

}
