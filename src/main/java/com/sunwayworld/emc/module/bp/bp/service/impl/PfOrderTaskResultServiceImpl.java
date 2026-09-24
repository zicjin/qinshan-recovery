package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.utils.EmcNumberUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.ResultInputResultDTO;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.computing.async.AsyncFormulaComputingService;
import com.sunwayworld.emc.module.bp.bp.dao.PfOrderTaskResultDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.TableCellDescriptor;
import com.sunwayworld.framework.support.table.TableHelper;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/15 14:03
 */
@Repository
@GikamBean
public class PfOrderTaskResultServiceImpl extends EmcOrderTaskResultServiceImpl implements PfOrderTaskResultService {

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private PfOrderTaskResultDao orderTaskResultDao;

    @Autowired
    private AsyncFormulaComputingService asyncFormulaComputingService;

    @Override
    public List<TableHeadBean> selectResultInputResultTitleList(RestJsonWrapperBean wrapper) {
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);

        // 此处只查询所需字段，减少查询时间
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(run.getId(), "RUNID", Arrays.asList("ID", "ORDERID", "TESTID", "QCCATEGORYCODE", "STATUS"));
        orderTaskList = orderTaskList.stream().filter(a -> a.getStatus().equals(EmcBizConstant.ORDERTASK_STATUS_LOGGED)).collect(Collectors.toList());
        // 过滤出正常样品，以正常样品拥有的分析项作为列参考
        List<EmcOrderTaskBean> naOrderTaskList = orderTaskList.stream().filter(t -> StringUtils.equals(EmcConstant.N_A, t.getQcCategoryCode())).collect(Collectors.toList());

        // 按测试ID对样品测试进行分组，一个批可能包含多个测试
        Map<Long, List<EmcOrderTaskBean>> testOrderTaskMap = naOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestId, TreeMap::new, Collectors.toList()));
        // 一个测试可能包含多个正常样品，此处只取一个
        List<Long> naOrderTaskIdList = testOrderTaskMap.values().stream().map(l -> l.get(0).getId()).collect(Collectors.toList());

        if (naOrderTaskIdList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIdList = testOrderTaskMap.values().stream().map(list -> list.get(0).getOrderId()).distinct().collect(Collectors.toList());

        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR).match("RUNID", run.getId())
                .filter(MatchPattern.OR).match("STATUS", EmcBizConstant.ORDERTASK_STATUS_LOGGED).filter(MatchPattern.SEQ));

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectResultInputResultList(parameter);

        orderTaskResultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getItemOrderNo));
        // 避免重复展示，比如样品称重时，一个样品中的所有的测试的称重量都是一样的，录入一次即可
        Map<String, List<EmcOrderTaskResultBean>> orderTaskResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getItemName, LinkedHashMap::new, Collectors.toList()));

        List<TableHeadBean> tableHeadList = new ArrayList<>();

        orderTaskResultMap.forEach((itemName, resultList) -> {
            TableHeadBean tableHead = new TableHeadBean();
            EmcOrderTaskResultBean result = resultList.get(0);

            // 以分析项名称的HashCode作为列名，用于统一不同样品下测试不一致的情况
            tableHead.setField("$" + itemName.hashCode());

            StringBuilder title = new StringBuilder();
            title.append(Optional.ofNullable(result.getItemAliasName()).orElse(result.getItemName())).append("<br>");
            title.append(StringUtils.isEmpty(result.getUnitName()) ? "" : "(" + result.getUnitName() + ")");

            tableHead.setTitle(title.toString());
            tableHead.setExt$Item("script", result.getScriptFlag());
            tableHeadList.add(tableHead);
        });

        return tableHeadList;
    }

    @Override
    public List<TableRowBean> selectResultInputResultRowList(RestJsonWrapperBean wrapper) {
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);

        // 此处只查询所需字段，减少查询时间
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(run.getId(), "RUNID", Arrays.asList("ID", "STATUS"));
        orderTaskList = orderTaskList.stream().filter(a -> a.getStatus().equals(EmcBizConstant.ORDERTASK_STATUS_LOGGED)).collect(Collectors.toList());
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        if (orderTaskIdList.isEmpty()) {
            return Collections.emptyList();
        }

        String analysisType = run.getExt$Item("analysisType");

        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("orderTaskId", orderTaskIdList).filter(MatchPattern.OR)
                .match("analysisType", analysisType).filter(MatchPattern.SEQ)
                .match("STATUS", EmcBizConstant.ORDERTASK_STATUS_LOGGED).filter(MatchPattern.SEQ));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectResultInputResultList(parameter);

        List<ResultInputResultDTO> dataList = new ArrayList<>();
        orderTaskList.forEach(t -> orderTaskResultList.stream().filter(r -> ObjectUtils.equals(t.getId(), r.getOrderTaskId())).forEach(r -> {
                ResultInputResultDTO inputResult = new ResultInputResultDTO();
                inputResult.setId(r.getId());
                inputResult.setColId("$" + r.getItemName().hashCode());
                // 如果分析项结果存在公式则显示报出结果，否则显示原始结果
                inputResult.setColVal(Constant.YES.equals(r.getScriptFlag()) ? r.getReportResult() : r.getOriginalResult());

                inputResult.setRowNo(r.getOrderNo());
                inputResult.setExt$Item("scriptFlag", r.getScriptFlag());
                inputResult.setExt$Item("submitFlag", r.getSubmitFlag());
                inputResult.setExt$Item("resultCategory", r.getResultCategory());
                dataList.add(inputResult);
            }));

        TableCellDescriptor<ResultInputResultDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");
        cellDescriptor.addExt("submitFlag");
        cellDescriptor.addExt("resultCategory");
        cellDescriptor.addExt("scriptFlag");

        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dataList, cellDescriptor);
        for (TableRowBean row : tableRowList) {
            String orderNo = row.getPivotValue("rowNo");
            EmcOrderTaskResultBean result = orderTaskResultList.stream().filter(f -> ObjectUtils.equals(orderNo, f.getOrderNo())).findFirst().orElse(null);

            if (result != null) {
                String naMonitorPointName = StringUtils.isEmpty(result.getExt$Item("monitorpointname")) ? "" : result.getExt$Item("ordercategory") + "_" + result.getExt$Item("monitorpointname");
                String monitorPointName = StringUtils.equals(EmcConstant.N_A, result.getQcCategoryCode()) ? naMonitorPointName : result.getQcName();
                row.put("monitorPointName", monitorPointName);
                row.put("orderNo", result.getOrderNo());
                row.put("qcName", result.getQcName());
                row.put("cupNo", result.getExt$Item("cupno"));
                row.put("id", result.getOrderTaskId());
                row.put("orderId", result.getOrderId());
                row.put("folderId", result.getFolderId());
                row.put("projNo", result.getExt$Item("projNo"));
                row.put("weighingDate", result.getExt$Item("weighingDate"));
                row.put("relatedQcOrderNo", result.getExt$Item("relatedQcOrderNo"));
                row.put("vocBottle", result.getExt$Item("vocbottle"));
                row.put("oilFilmNo", result.getExt$Item("oilfilmno"));
                row.put("sampleBottleNo", result.getExt$Item("sampleBottleNo"));
                row.put("weightEndDate", result.getExt$Item("weightEndDate"));

            }
        }

        // 按杯号对行数据进行排序
        tableRowList.sort(Comparator.comparing(r -> NumberUtils.parseInt(Optional.ofNullable(r.get("cupNo")).orElse("1"))));
        orderTaskResultList.clear();
        orderTaskList.clear();
        return tableRowList;
    }

    @Override
    public void updateWeighingItemResult(RestJsonWrapperBean wrapper) {
        EmcOrderTaskResultBean taskResult = wrapper.parseUnique(EmcOrderTaskResultBean.class);
        String originalResult = taskResult.getOriginalResult();
        taskResult = selectById(taskResult.getId());

        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("ORDERID", taskResult.getOrderId()).filter(MatchPattern.SEQ)
                .match("RUNID", taskResult.getRunId()).filter(MatchPattern.OR)
                .match("useType", "weighing").filter(MatchPattern.SEQ)
                .match("itemName", taskResult.getItemName()).filter(MatchPattern.SEQ)
                .match("STATUS", EmcBizConstant.ORDERTASK_STATUS_LOGGED).filter(MatchPattern.SEQ));

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectResultInputResultList(parameter);
        orderTaskResultList.forEach(result -> result.setOriginalResult(originalResult));

        // 更新样品测试称重日期
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(orderTaskIdList, "id", Arrays.asList("id", "weighingDate"));
        List<EmcOrderTaskBean> emptyTaskList = orderTaskList.stream().filter(task -> StringUtils.isEmpty(task.getExt$Item("weighingDate"))).collect(Collectors.toList());
        if (!emptyTaskList.isEmpty()) {
            emptyTaskList.forEach(task -> task.setExt$Item("weighingDate", DateTimeUtils.formatLocalDate(LocalDate.now())));
            orderTaskService.getDao().update(emptyTaskList, "weighingDate");
        }

        orderTaskResultDao.update(orderTaskResultList, "originalResult");
        List<Long> resultIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        orderTaskResultList.clear();
        orderTaskList.clear();
        asyncFormulaComputingService.asyncComputing(taskResult.getRunId(), resultIdList, LocalContextHelper.getLoginUserId(), false);
    }

    /**
     * 根据原始结果计算报出结果
     */
    @Override
    public void setReportResultValue(EmcOrderTaskResultBean resultBean, EmcItemBean item, List<EmcItemRevisionBean> itemRevisionList) {
        if (item == null) {
            return;
        }

        String value = resultBean.getOriginalResult();
        if (!StringUtils.isEmpty(value) && NumberUtils.isNumber(value)) {
            double valueNum = NumberUtils.parseDouble(value);
            // 过滤出对应的分段修约数据
            List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream().filter(e -> e.getItemId().equals(resultBean.getItemId())).collect(Collectors.toList());
            String revisionRule = resultBean.getRevisionRule();

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
                    if (StringUtils.equals("<", lowLimitSymbol)) {
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

            // 判断是否需要使用科学计数法
            String itemNumRange = resultBean.getExt$Item("nonSnRange");
            String systemDefaultNumRange = ApplicationContextHelper.getConstantValue("NON_SCIENTIFIC_NOTATION_RANGE");
            String numRange = StringUtils.isEmpty(itemNumRange) ? systemDefaultNumRange : itemNumRange;
            boolean scientificNotation = Constant.YES.equals(item.getScientificNotation()) && EmcNumberUtils.isValidNumRange(numRange) && !EmcNumberUtils.inNumRange(revisionValue, numRange);

            String limitRevision = item.getLimitRevision();
            if (!StringUtils.isEmpty(limitRevision) && Constant.YES.equals(limitRevision) && !ObjectUtils.isEmpty(resultBean.getLowLimit())) {
                // 是否根据检出限修约为是, 判断检出低限是否为空
                String lowLimitStr = ObjectUtils.toString(resultBean.getLowLimit());
                int radix = EmcNumberUtils.getRadix(lowLimitStr);
                // 检出限修约值
                String lowLimitRevisionValue = NumberUtils.rounding(value, "FDA," + radix);
                // 比较检出限修约值和之前的计算修约值，取小数点位数小的值
                int isoRadix = EmcNumberUtils.getRadix(lowLimitRevisionValue);

                int revisionRadix = EmcNumberUtils.getRadix(revisionValue);
                if (isoRadix < revisionRadix) {
                    revisionValue = lowLimitRevisionValue;

                    if (scientificNotation) {
                        revisionValue = NumberUtils.scientificNotationFormat(NumberUtils.parseDouble(lowLimitRevisionValue), isoRadix);
                    }
                }
            }

            if (scientificNotation) {
                // 如果有修约规则，再次对科学计数法进行修约
                boolean validRevisionRule = !StringUtils.isEmpty(revisionRule) && StringUtils.contains(revisionRule, ",");
                int precision = EmcNumberUtils.getRadix(revisionValue);
                if (validRevisionRule && NumberUtils.isNumber(StringUtils.split(revisionRule, ",")[1])) {
                    precision = Math.max(precision, NumberUtils.parseInt(StringUtils.split(revisionRule, ",")[1]));
                }

                revisionValue = NumberUtils.scientificNotationFormat(NumberUtils.parseDouble(revisionValue), precision);
                if (validRevisionRule) {
                    revisionValue = NumberUtils.rounding(revisionValue, revisionRule);
                }
            }

            resultBean.setReportResult(revisionValue);
        } else {
            resultBean.setReportResult(value);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void changeResult(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<Long> calcResultIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());

        String convertFactor = orderTaskResultList.get(0).getConvertFactor();
        if (!StringUtils.isEmpty(convertFactor)) {
            List<EmcOrderTaskResultBean> orderTaskResultForTaskList = getDao().selectListByOneColumnValues(calcResultIdList, "id", Arrays.asList("id", "runId", "itemName"));
            List<String> itemNameList = orderTaskResultForTaskList.stream().map(EmcOrderTaskResultBean::getItemName).distinct().collect(Collectors.toList());
            List<String> runIdList = orderTaskResultForTaskList.stream().filter(ok ->!StringUtils.isEmpty(ok.getRunId())).map(EmcOrderTaskResultBean::getRunId).distinct().collect(Collectors.toList());

            List<EmcOrderTaskResultBean> runResultList = getDao().selectListByOneColumnValues(runIdList, "runId", Arrays.asList("id", "itemName", "convertFactor"));
            List<EmcOrderTaskResultBean> updateResultList = runResultList.stream().filter(r -> itemNameList.contains(r.getItemName()) && StringUtils.isEmpty(r.getConvertFactor())).collect(Collectors.toList());

            if (!updateResultList.isEmpty()) {
                updateResultList.forEach(r -> r.setConvertFactor(convertFactor));
                getDao().update(updateResultList, "convertFactor");
                List<Long> updateResultIdList = updateResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
                calcResultIdList.addAll(updateResultIdList);
            }
        }

        EmcOrderTaskResultComputingEngine.getEngine(calcResultIdList).compute();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void triggerComputeNoComplete(RestJsonWrapperBean wrapper) {
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);
        List<String> runIdList = runList.stream().filter(r ->!StringUtils.isEmpty(r.getId())).map(r ->r.getId()).distinct().collect(Collectors.toList());

        List<EmcOrderTaskResultBean> orderTaskResultBeforeUpdateList = getDao().selectListByOneColumnValues(runIdList, "runId");
        //报出结果为空的值
        List<EmcOrderTaskResultBean> orderTaskResultForUpdateList = orderTaskResultBeforeUpdateList.stream().filter(res ->StringUtils.isEmpty(res.getReportResult())).collect(Collectors.toList());
        List<Long> calcResultIdList = orderTaskResultForUpdateList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        orderTaskResultBeforeUpdateList.clear();
        orderTaskResultForUpdateList.clear();
        EmcOrderTaskResultComputingEngine.getEngine(calcResultIdList).compute();
    }

    /**
     * 分析快速录入-表头
     */
    @Override
    public List<TableHeadBean> selectQuickInputResultTitleList(RestJsonWrapperBean wrapper) {
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("RUNID", run.getId()).filter(MatchPattern.EQ));

        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultList(parameter);

        return getTableHeadListWithItemId(orderTaskResultList);//表头展示到itemAliasName
    }

    /**
     * 分析快速录入-数据
     */
    @Override
    public List<TableRowBean> selectQuickInputResultRowList(RestJsonWrapperBean wrapper) {
        EmcRunBean run = wrapper.parseUnique(EmcRunBean.class);

        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("RUNID", run.getId()).filter(MatchPattern.SEQ));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultList(parameter);

        return getTableRowByLabelNumber(orderTaskResultList);
    }

    @Override
    public List<EmcOrderTaskResultBean> selectResultRemind(MapperParameter mapperParameter) {
        return orderTaskResultDao.selectResultRemind(mapperParameter);
    }

    @Override
    public List<TableHeadBean> selectQuickInputSampleResultTitleList(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultList(wrapper.extractMapFilter());

        return getTableHeadListWithItemId(orderTaskResultList);
    }

    @Override
    public List<TableRowBean> selectQuickInputSampleResultRowList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultList(parameter);

        return getTableRowByLabelNumber(orderTaskResultList);
    }

    @Override
    public List<TableHeadBean> selectQuickInputResultTitleListBySampleParam(RestJsonWrapperBean wrapper) {
        EmcSampleBean sample = wrapper.parseUnique(EmcSampleBean.class);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.put("sampleId",sample.getId());
        parameter.put("paramProjId",sample.getProjId());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultListBySampleParam(parameter);
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(r -> r.getOrderTaskId()).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> naOrderTaskList = orderTaskService.getDao().selectListByIds(orderTaskIdList);
        Map<Long, List<EmcOrderTaskBean>> testOrderTaskMap = naOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestId, TreeMap::new, Collectors.toList()));
        // 一个测试可能包含多个正常样品，此处只取一个
        List<Long> naOrderTaskIdList = testOrderTaskMap.values().stream().map(l -> l.get(0).getId()).collect(Collectors.toList());

        if (naOrderTaskIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<EmcOrderTaskResultBean> otrUniqueList = orderTaskResultList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(e -> e.getItemId() + "" + e.getRepeatNo()))), ArrayList::new));

        otrUniqueList.sort(Comparator.comparingLong(EmcOrderTaskResultBean::getItemOrderNo));
        List<TableHeadBean> tableHeadList = new ArrayList<>();

        boolean containMultipleTest = testOrderTaskMap.size() > 1;
        otrUniqueList.forEach(r -> {
            TableHeadBean tableHead = new TableHeadBean();
            tableHead.setField(r.getTestId() + "$" + r.getItemId() + "$" + r.getRepeatNo());

            StringBuilder title = new StringBuilder();
            if (containMultipleTest) {
                title.append(r.getExt$Item("testname")).append("<br>");
            }
            title.append(r.getItemAliasName()).append("_").append(r.getRepeatNo()).append("<br>");

            title.append(Optional.ofNullable(r.getUnitName()).orElse("-"));

            tableHead.setTitle(title.toString());
            tableHead.setExt$Item("script", r.getScriptFlag());

            tableHeadList.add(tableHead);
        });

        return tableHeadList;
    }

    @Override
    public List<TableRowBean> selectQuickInputResultRowListBySampleParam(RestJsonWrapperBean wrapper) {
        EmcSampleBean sample = wrapper.parseUnique(EmcSampleBean.class);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.put("sampleId",sample.getId());
        parameter.put("paramProjId",sample.getProjId());
        //parameter.setFilter(SearchFilter.instance().match("ORDERTASKID", orderTaskIdList).filter(MatchPattern.OR).match("ANALYSISTYPE","analysis").filter(MatchPattern.EQ));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultListBySampleParam(parameter);
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(r -> r.getOrderTaskId()).distinct().collect(Collectors.toList());
        List<ResultInputResultDTO> dataList = new ArrayList<>();
        orderTaskIdList.forEach(t -> {
            orderTaskResultList.stream().filter(r -> ObjectUtils.equals(t, r.getOrderTaskId())).forEach(r -> {
                ResultInputResultDTO quickInputResult = new ResultInputResultDTO();
                quickInputResult.setId(r.getId());
                quickInputResult.setColId(r.getTestId() + "$" + r.getItemId() + "$" + r.getRepeatNo());
                if("1".equals(r.getScriptFlag())){
                    quickInputResult.setColVal(r.getReportResult());
                }else{
                    quickInputResult.setColVal(r.getOriginalResult());
                }
                quickInputResult.setRowNo(r.getExt$Item("sampleparamorderno"));
                quickInputResult.setExt$Item("submitFlag", r.getSubmitFlag());
                quickInputResult.setExt$Item("scriptFlag", r.getScriptFlag());
                dataList.add(quickInputResult);
            });
        });

        TableCellDescriptor<ResultInputResultDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");
        cellDescriptor.addExt("submitFlag");
        cellDescriptor.addExt("scriptFlag");
        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dataList, cellDescriptor);
        for (TableRowBean row : tableRowList) {
            String orderNo = row.getPivotValue("rowNo");
            EmcOrderTaskResultBean result = orderTaskResultList.stream().filter(f -> ObjectUtils.equals(orderNo, f.getExt$Item("sampleparamorderno"))).findFirst().orElse(null);

            if (result != null) {
                String naMonitorPointName = StringUtils.isEmpty(result.getExt$Item("monitorpointname")) ? "" : result.getExt$Item("ordercategory") + "_" + result.getExt$Item("monitorpointname");
                String monitorPointName = StringUtils.equals(EmcConstant.N_A, result.getQcCategoryCode()) ? naMonitorPointName : result.getQcName();
                row.put("monitorpointname", monitorPointName);
                row.put("cycle", result.getCycle());
                row.put("frequency", result.getFrequency());
                row.put("sampleparamorderno", result.getExt$Item("sampleparamorderno"));
                row.put("orderNo", result.getOrderNo());
                row.put("qcName", result.getQcName());
                row.put("cupNo", result.getExt$Item("cupno"));
                row.put("id", result.getOrderTaskId());
                row.put("folderId", result.getFolderId());
                row.put("clientOrderName", result.getExt$Item("clientordername"));
                row.put("orderId", result.getOrderId());
                row.put("orderNumber", result.getExt$Item("ordernumber"));
            }
        }

        // 按杯号对行数据进行排序
        List<TableRowBean> newtableRowList = tableRowList.stream().sorted(Comparator.comparing(r -> r.get("orderNumber").toString(),Comparator.naturalOrder())).collect(Collectors.toList());
        // tableRowList.sort(Comparator.comparing(r -> NumberUtils.parseInt(Optional.ofNullable(r.get("orderNumber")).orElse("1"))));

        return newtableRowList;
    }

    @Override
    public List<TableHeadBean> selectQuickInputResultTitleListBySampleId(RestJsonWrapperBean wrapper) {
        EmcSampleBean sample = wrapper.parseUnique(EmcSampleBean.class);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.put("sampleId",sample.getId());
        parameter.put("targetType","sampleParamInput");
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultListBySampleId(parameter);
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(r -> r.getOrderTaskId()).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> naOrderTaskList = orderTaskService.getDao().selectListByIds(orderTaskIdList);
        Map<Long, List<EmcOrderTaskBean>> testOrderTaskMap = naOrderTaskList.stream().collect(Collectors.groupingBy(EmcOrderTaskBean::getTestId, TreeMap::new, Collectors.toList()));
        // 一个测试可能包含多个正常样品，此处只取一个
        List<Long> naOrderTaskIdList = testOrderTaskMap.values().stream().map(l -> l.get(0).getId()).collect(Collectors.toList());

        if (naOrderTaskIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<EmcOrderTaskResultBean> oldotrUniqueList = orderTaskResultList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(e -> e.getItemId() + "" + e.getRepeatNo()))), ArrayList::new));
        //otrUniqueList.sort(Comparator.comparingLong(EmcOrderTaskResultBean::getTestId));
        //otrUniqueList.sort(Comparator.comparingLong(EmcOrderTaskResultBean::getItemOrderNo));
        List<EmcOrderTaskResultBean> otrUniqueList = oldotrUniqueList.stream().sorted(Comparator.comparing(EmcOrderTaskResultBean::getTestId).thenComparing(EmcOrderTaskResultBean::getItemOrderNo)).collect(Collectors.toList());
        List<TableHeadBean> tableHeadList = new ArrayList<>();

        boolean containMultipleTest = testOrderTaskMap.size() > 1;
        otrUniqueList.forEach(r -> {
            TableHeadBean tableHead = new TableHeadBean();
            tableHead.setField(r.getTestId() + "$" + r.getItemId() + "$" + r.getRepeatNo());

            StringBuilder title = new StringBuilder();
            if (containMultipleTest) {
                title.append(r.getExt$Item("testname")).append("<br>");
            }
            title.append(r.getItemAliasName()).append("_").append(r.getRepeatNo()).append("<br>");

            title.append(Optional.ofNullable(r.getUnitName()).orElse("-"));

            tableHead.setTitle(title.toString());
            tableHead.setExt$Item("script", r.getScriptFlag());

            tableHeadList.add(tableHead);
        });

        return tableHeadList;
    }

    @Override
    public List<TableRowBean> selectQuickInputResultRowListBySampleId(RestJsonWrapperBean wrapper) {
        EmcSampleBean sample = wrapper.parseUnique(EmcSampleBean.class);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.put("sampleId",sample.getId());
        parameter.put("targetType","sampleParamInput");
        //parameter.setFilter(SearchFilter.instance().match("ORDERTASKID", orderTaskIdList).filter(MatchPattern.OR).match("ANALYSISTYPE","analysis").filter(MatchPattern.EQ));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultDao.selectQuickInputResultListBySampleId(parameter);
        List<Long> orderTaskIdList = orderTaskResultList.stream().map(r -> r.getOrderTaskId()).distinct().collect(Collectors.toList());
        List<ResultInputResultDTO> dataList = new ArrayList<>();
        orderTaskIdList.forEach(t -> {
            orderTaskResultList.stream().filter(r -> ObjectUtils.equals(t, r.getOrderTaskId())).forEach(r -> {
                ResultInputResultDTO quickInputResult = new ResultInputResultDTO();
                quickInputResult.setId(r.getId());
                quickInputResult.setColId(r.getTestId() + "$" + r.getItemId() + "$" + r.getRepeatNo());
                if("1".equals(r.getScriptFlag())){
                    quickInputResult.setColVal(r.getReportResult());
                }else{
                    quickInputResult.setColVal(r.getOriginalResult());
                }
                quickInputResult.setRowNo(r.getOrderNo());
                quickInputResult.setExt$Item("submitFlag", r.getSubmitFlag());
                quickInputResult.setExt$Item("scriptFlag", r.getScriptFlag());
                dataList.add(quickInputResult);
            });
        });

        TableCellDescriptor<ResultInputResultDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");
        cellDescriptor.addExt("submitFlag");
        cellDescriptor.addExt("scriptFlag");
        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dataList, cellDescriptor);
        for (TableRowBean row : tableRowList) {
            String orderNo = row.getPivotValue("rowNo");
            EmcOrderTaskResultBean result = orderTaskResultList.stream().filter(f -> ObjectUtils.equals(orderNo, f.getOrderNo())).findFirst().orElse(null);

            if (result != null) {
                String naMonitorPointName = StringUtils.isEmpty(result.getExt$Item("monitorpointname")) ? "" : result.getExt$Item("ordercategory") + "_" + result.getExt$Item("monitorpointname");
                String monitorPointName = StringUtils.equals(EmcConstant.N_A, result.getQcCategoryCode()) ? naMonitorPointName : result.getQcName();
                row.put("monitorpointname", monitorPointName);
                row.put("cycle", result.getCycle());
                row.put("frequency", result.getFrequency());
                row.put("sampleparamorderno", result.getExt$Item("sampleparamorderno"));
                row.put("orderNo", result.getOrderNo());
                row.put("qcName", result.getQcName());
                row.put("cupNo", result.getExt$Item("cupno"));
                row.put("id", result.getOrderTaskId());
                row.put("folderId", result.getFolderId());
                row.put("clientOrderName", result.getExt$Item("clientordername"));
                row.put("orderId", result.getOrderId());
                row.put("orderNumber", result.getExt$Item("ordernumber"));
            }
        }

        // 按杯号对行数据进行排序
        List<TableRowBean> newtableRowList = tableRowList.stream().sorted(Comparator.comparing(r -> r.get("orderNumber").toString(),Comparator.naturalOrder())).collect(Collectors.toList());
        // tableRowList.sort(Comparator.comparing(r -> NumberUtils.parseInt(Optional.ofNullable(r.get("orderNumber")).orElse("1"))));

        return newtableRowList;
    }

    /**
     * 生成交叉表表头：设置到itemId一级，用在现场采样、分析录入页面
     * 标题也显示到itemAliasName
     */
    private List<TableHeadBean> getTableHeadListWithItemId(List<EmcOrderTaskResultBean> orderTaskResultList) {

        if (orderTaskResultList == null || orderTaskResultList.isEmpty()) {
            return Collections.emptyList();
        }

        List<EmcOrderTaskResultBean> uniqueList = orderTaskResultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(e -> e.getItemId() + "" + e.getRepeatNo()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(EmcOrderTaskResultBean::getAnalysisType, Comparator.reverseOrder()).thenComparing(EmcOrderTaskResultBean::getTestId).thenComparing(EmcOrderTaskResultBean::getItemOrderNo))
                .collect(Collectors.toList());

        List<TableHeadBean> tableHeadList = new ArrayList<>();
        uniqueList.forEach(result -> {
            TableHeadBean tableHead = new TableHeadBean();
            tableHead.setField(result.getTestId() + "$" + result.getItemId() + "$" + result.getRepeatNo());
            //tableHead.setTitle("<font color=\"blue\">" + result.getItemAliasName() + "</font>");
            tableHead.setTitle(result.getExt$Item("testname") + "<br>" + result.getItemAliasName() + "_" + result.getRepeatNo() + "<br>" + Optional.ofNullable(result.getUnitName()).orElse(""));
            tableHead.setExt$Item("script", result.getScriptFlag());
            tableHeadList.add(tableHead);
        });
        return tableHeadList;
    }

    /**
     * 生成交叉表数据-根据labelNumber；结果值取originalResult
     */
    private List<TableRowBean> getTableRowByLabelNumber(List<EmcOrderTaskResultBean> orderTaskResultList) {

        if (orderTaskResultList == null || orderTaskResultList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ResultInputResultDTO> dtoList = new ArrayList<>();
        orderTaskResultList.forEach(result -> {
            ResultInputResultDTO dto = new ResultInputResultDTO();
            dto.setId(result.getId());
            dto.setRowNo(result.getOrderNo());
            dto.setColId(result.getTestId() + "$" + result.getItemId() + "$" + result.getRepeatNo());
            if ("1".equals(result.getScriptFlag())) {//有公式的分析项，显示报出结果
                dto.setColVal(result.getReportResult());
            } else {
                dto.setColVal(result.getOriginalResult());
            }
            dto.setExt$Item("submitFlag", result.getSubmitFlag());
            dto.setExt$Item("scriptFlag", result.getScriptFlag());
            dto.setExt$Item("resultStatus", result.getResultStatus());
            dto.setExt$Item("resultCategory", result.getResultCategory());
            dtoList.add(dto);
        });

        TableCellDescriptor<ResultInputResultDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");
        cellDescriptor.addExt("submitFlag", "scriptFlag", "resultStatus", "resultCategory");//上面dto设置了的ext字段，需要在这里add进去
        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dtoList, cellDescriptor);

        for (TableRowBean row : tableRowList) {
            String orderNo = row.getPivotValue("rowNo");
            orderTaskResultList.stream().filter(f -> ObjectUtils.equals(orderNo, f.getOrderNo())).findFirst().ifPresent(result -> {
                row.put("projId", result.getProjId());
                row.put("projNo", result.getExt$Item("projNo"));
                row.put("projName", result.getExt$Item("projName"));
                row.put("id", result.getOrderTaskId());
                row.put("folderId", result.getFolderId());
                row.put("folderNo", result.getExt$Item("folderNo"));
                row.put("section", result.getExt$Item("section"));
                row.put("orderId", result.getOrderId());
                row.put("orderNo", result.getExt$Item("orderContainerNo"));
                row.put("equiptAcquNo", result.getExt$Item("equiptAcquNo"));
                row.put("orderCategory", result.getExt$Item("orderCategory"));
                row.put("labelNumber", result.getExt$Item("labelNumber"));
                row.put("labDisplayLabelNumber", result.getExt$Item("labDisplayLabelNumber"));
                row.put("cupNo", result.getExt$Item("cupNo"));
                row.put("sampleBeginDateTime", result.getExt$Item("sampleBeginDateTime"));
                row.put("analysisOrgId", result.getExt$Item("analysisOrgId"));
                row.put("analysisOrgName", result.getExt$Item("analysisOrgName"));
                row.put("sampleOrgId", result.getExt$Item("sampleOrgId"));
                row.put("sampleOrgName", result.getExt$Item("sampleOrgName"));
                row.put("qcName", result.getQcName());
                row.put("runId", result.getRunId());
                row.put("labDisplayQcName", "lab".equals(result.getExt$Item("useCategory")) ? result.getQcName() : "N/A");//实验室阶段展示的质控名称
            });
        }

        if (!ObjectUtils.isEmpty(tableRowList.get(0).get("cupNo"))) {
            tableRowList.sort(Comparator.comparing(r -> NumberUtils.parseInt(r.get("cupNo"))));//按杯号排序
        }

        return tableRowList;
    }
    @Override
    public List<String> selectQcNames(RestJsonWrapperBean wrapper) {
        return new ArrayList<String>();
    }

    @Override
    public void batchInputGasParameter(RestJsonWrapperBean wrapper) {
    }

    @Override
    public List<TableHeadBean> selectPreprocessingQuickInputResultTitleListByProjIds(RestJsonWrapperBean wrapper) {
        return selectQuickInputResultTitleList(wrapper);
    }

    @Override
    public List<TableRowBean> selectPreprocessingQuickInputResultRowListByProjIds(RestJsonWrapperBean wrapper) {
        return selectQuickInputResultRowList(wrapper);
    }

}
