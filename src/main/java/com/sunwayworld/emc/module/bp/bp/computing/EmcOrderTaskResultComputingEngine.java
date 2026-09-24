package com.sunwayworld.emc.module.bp.bp.computing;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.formula.ResultFormulaRepository;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.framework.beans.BeanPropertyEvent;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamComputingEngine;
import com.sunwayworld.framework.formula.GikamFormulaHelper;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;

public class EmcOrderTaskResultComputingEngine implements GikamComputingEngine {

    /** 点位下的样品测试结果 */
    private static final String FOLDER_ORDER_TASK_RESULT_LIST = "FOLDER_ORDER_TASK_RESULT_LIST";
    /** 参与计算的样品测试结果 */
    private static final String COMPUTE_ORDER_TASK_RESULT_LIST = "COMPUTE_ORDER_TASK_RESULT_LIST";
    /** 批下所有的样品测试结果 */
    private static final String ALL_ORDER_TASK_RESULT_LIST = "RUN_ORDER_TASK_RESULT_LIST";
    /** 批下所有的样品测试 */
    private static final String ALL_ORDER_TASK_LIST = "RUN_ORDER_TASK_LIST";

    /** 点位下分析项的样品浓度 */
    private static final String STOCK_CONCENT_LIST = "STOCK_CONCENT_LIST";
    /** 点位下分析项QC指标List */
    private static final String ITEM_QC_INDEX_LIST = "ITEM_QC_INDEX_LIST";
    /** 点位下分析项分段修约List */
    private static final String ITEM_REVISION_LIST = "ITEM_REVISION_LIST";
    /** 分析批List */
    private static final String RUN_LIST = "RUN_LIST";

    /** 匹配脚本中sql函数 */
    private static final Pattern SQL_PATTER = Pattern.compile("executeSQL\\(.*\\)");
    /** 匹配sql语句中的参数 */
    private static final Pattern ARG_PATTERN = Pattern.compile("#[A-Za-z]+\\w*#");

    private static final Logger logger = LogManager.getLogger(EmcOrderTaskResultComputingEngine.class);

    /** 参与计算的样品测试结果ID */
    private List<Long> resultIdList;

    private EmcOrderTaskResultComputingEngine() {
    }

    public static EmcOrderTaskResultComputingEngine getEngine(List<Long> resultIdList) {
        EmcOrderTaskResultComputingEngine engine = new EmcOrderTaskResultComputingEngine();
        engine.resultIdList = resultIdList;
        return engine;
    }

    @Override
    public BshExecutor getBshExecutor() {
        BshExecutor bsh = BshExecutor.instance();

        bsh.eval("import com.sunwayworld.emc.module.bp.bp.computing.bean.OrderTaskResultDTO;");
        bsh.eval("import com.sunwayworld.framework.utils.StringUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.ArithUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.NumberUtils;");
        bsh.eval("import com.sunwayworld.emc.module.bp.bp.bean.*;");
        bsh.eval("import java.text.*;");

        GikamFormulaRegistry.INSTANCE.getRepository().forEach(c -> {
            bsh.eval("import static " + c.getName() + ".*;");
        });

        return bsh;
    }

    @Override
    public void sortData() {
        // 这里的排序方式有问题，在初始化数据时已经排过序
        // 排序样品结果表
        List<EmcOrderTaskResultBean> orderTaskResultList = getComputeTaskResultList();

        // 排序
        CollectionUtils.sort(orderTaskResultList, getComparator());
    }

    @Override
    public void processData() {
        EmcOrderTaskResultService taskResultService = ApplicationContextHelper.getBean(EmcOrderTaskResultService.class);

        List<EmcOrderTaskResultBean> updateTaskResultList = new ArrayList<>();
        // 获取更新的字段
        Set<String> updatedColumnList = new HashSet<>();

        // 遍历批下的需要计算的样品结果
        List<EmcOrderTaskResultBean> runOrderTaskList = EmcOrderTaskResultComputingEngine.getAllTaskResultList();

        for (EmcOrderTaskResultBean runOrderTaskResult : runOrderTaskList) {
            BeanPropertyListener proxy = (BeanPropertyListener) runOrderTaskResult;

            List<BeanPropertyEvent> propertyEventList = new ArrayList<>(proxy.getChangedPropertyEventList());

            if (!propertyEventList.isEmpty()) {
                updateTaskResultList.add(runOrderTaskResult);
                propertyEventList.forEach(p -> updatedColumnList.add(p.getPropertyDescriptor().getName().toUpperCase()));
            }
        }

        if (!updateTaskResultList.isEmpty() && !updatedColumnList.isEmpty()) {
            taskResultService.getDao().update(updateTaskResultList, updatedColumnList.toArray(new String[0]));
        }

    }

    @Override
    public void run(BshExecutor bsh) {
        boolean dependencyCheck = this.isDependencyCheck();

        List<EmcOrderTaskResultBean> resultList = getComputeTaskResultList();

        List<EmcOrderTaskResultBean> lBlankResultList = resultList.stream().filter(o -> EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(o.getQcCategoryCode())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> sortedResultList = new ArrayList<>(lBlankResultList);

        List<EmcOrderTaskResultBean> nAResultList = resultList.stream().filter(o -> EmcConstant.N_A.equals(o.getQcCategoryCode())).collect(Collectors.toList());
        sortedResultList.addAll(nAResultList);

        List<EmcOrderTaskResultBean> otherQcResultList = resultList.stream().filter(o -> !EmcConstant.N_A.equals(o.getQcCategoryCode()) && !EmcConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(o.getQcCategoryCode())).collect(Collectors.toList());
        sortedResultList.addAll(otherQcResultList);

        for (EmcOrderTaskResultBean result : sortedResultList) {

            try {
                GikamComputingDataHolder.setCurrentData(result);
                String script = GikamFormulaHelper.getRevisedScript(result.getExt$Item("testscript"));

                if (!StringUtils.isEmpty(script)) {
                    script = reviseSqlArgument(script);
                    logger.info("script=====================" + script);

                    Object resultValue = bsh.eval(script,
                            I18nHelper.getMessage("T_EMC_ORDER_TASK_RESULT.TIP.COMPUTING.BAD_TEST_SCRIPT", result.getExt$Item("testname"), result.getExt$Item("methodname"), result.getItemName()));

                    if (!ObjectUtils.isEmpty(resultValue)) {
                        String finalResultValue = ObjectUtils.toString(resultValue);

                        // 实际计算时，要把计算出来的结果赋值到原始值上
                        if (!dependencyCheck) {
                            result.setOriginalResult(finalResultValue);
                        }
                    }
                }

                // 触发标准设置公式
                if (!dependencyCheck) {
                    result.setReportResult(result.getOriginalResult());

                    if (StringUtils.isEmpty(result.getOriginalResult())) {
                        result.setResultStatus(null);
                        continue;
                    }

                    // 对于分析类型的，如果原始结果小于0，则报出结果直接赋0
                    if (StringUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, result.getAnalysisType()) &&
                            StringUtils.equals(Constant.YES, result.getExt$Item("negativetozero")) &&
                            NumberUtils.isNumber(result.getOriginalResult()) && NumberUtils.parseDouble(result.getOriginalResult()) < 0) {
                        result.setReportResult("0");
                    }

                    result.setResultStatus(EmcBizConstant.ORDERTASKRESULT_STATUS_DONE);
                    // 计算公式一
                    String script1 = result.getExt$Item("script1");
                    if (!StringUtils.isEmpty(script1)) {
                        bsh.eval(GikamFormulaHelper.getRevisedScript(script1), I18nHelper.getMessage("T_EMC_ORDER_TASK_RESULT.TIP.COMPUTING.BAD_STANDARD_SET_SCRIPT1", result.getItemName()));
                    }

                    // 是否需要根据转换系数进行转换
                    boolean needConvert = StringUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, result.getAnalysisType())
                            && NumberUtils.isNumber(result.getConvertFactor())
                            && NumberUtils.parseDouble(result.getConvertFactor()) != 0D
                            && NumberUtils.isNumber(result.getReportResult());

                    String reportResult = needConvert ? String.valueOf(ArithUtils.div(result.getReportResult(), result.getConvertFactor())) : result.getReportResult();

                    result.setReportResult(ResultFormulaRepository.getReviseValue(reportResult));

                    // 验证公式
                    String validateScript = result.getExt$Item("validateScript");
                    if (!StringUtils.isEmpty(validateScript)) {
                        bsh.eval(GikamFormulaHelper.getRevisedScript(validateScript),
                                I18nHelper.getMessage("T_EMC_ORDER_TASK_RESULT.TIP.COMPUTING.BAD_STANDARD_SET_VALIDATE_SCRIPT", result.getItemName()));
                    }

                    // 计算公式二
                    String script2 = result.getExt$Item("script2");
                    // 只有分析项的计算质控结果为是的才执行计算2
                    if (Constant.YES.equals(result.getExt$Item("calqcresult")) && !StringUtils.isEmpty(script2)) {
                        bsh.eval(EmcGikamFormulaHelper.getRevisedScript(script2), I18nHelper.getMessage("T_EMC_ORDER_TASK_RESULT.TIP.COMPUTING.BAD_STANDARD_SET_SCRIPT2", result.getItemName()));
                    }
                }

            } catch (Exception e) {
                if (!dependencyCheck) {
                    result.setOriginalResult(null);
                    result.setReportResult(null);
                    result.setResultStatus(null);
                }

                if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                    throw e;
                } else {
                    logger.error(e.getMessage(), e);
                }
            } finally {
                GikamComputingDataHolder.removeCurrentData();
            }
        }
    }

    /**
     * 初始化计算公式所需的数据
     */
    @Override
    public void initData() {
        EmcOrderTaskResultService taskResultService = ApplicationContextHelper.getBean(EmcOrderTaskResultService.class);

        // 先根据resultIdList查询样品结果
        List<EmcOrderTaskResultBean> taskResultList = taskResultService.selectScriptListByFilter(SearchFilter.instance().match("ID", this.resultIdList).filter(MatchPattern.OR));

        // 筛选出是正常样品且录数完成的结果，针对当所有样品数据录入完毕后再修改正常样品时，质控样结果不会重新计算的问题
        List<EmcOrderTaskResultBean> normalResultList = taskResultList.stream().filter(i -> ObjectUtils.isEmpty(i.getQcCategoryId()) && "Done".equals(i.getResultStatus())).collect(Collectors.toList());
        if (!normalResultList.isEmpty()) {
            List<Long> normalOrderTaskIdList = normalResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());

            EmcOrderTaskService orderTaskService = ApplicationContextHelper.getBean(EmcOrderTaskService.class);
            List<EmcOrderTaskBean> childOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(normalOrderTaskIdList, "PARENTID", Arrays.asList("ID", "RUNID"));
            List<Long> childOrderTaskIdList = childOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getRunId(), normalResultList.get(0).getRunId())).map(EmcOrderTaskBean::getId).collect(Collectors.toList());

            if (!childOrderTaskIdList.isEmpty()) {
                List<Long> normalItemIdList = normalResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
                // 查找子样品结果
                List<EmcOrderTaskResultBean> subTaskResultList = taskResultService.selectScriptListByFilter(SearchFilter.instance().match("ORDERTASKID", childOrderTaskIdList).filter(MatchPattern.OR)
                        .match("ITEMID", normalItemIdList).filter(MatchPattern.OR));
                if (!subTaskResultList.isEmpty()) {
                    subTaskResultList.stream().filter(subResult -> taskResultList.stream().noneMatch(r -> ObjectUtils.equals(r.getId(), subResult.getId()))).forEach(taskResultList::add);
                }
            }
        }

        // 筛选出是质控样且录数完成的结果，针对当所有样品数据录入完毕后再修改质控样品时，正常样结果不会重新计算的问题
        List<EmcOrderTaskResultBean> qcResultList = taskResultList.stream().filter(i -> !ObjectUtils.isEmpty(i.getQcCategoryId()) && "Done".equals(i.getResultStatus())).collect(Collectors.toList());
        if (!qcResultList.isEmpty()) {
            List<Long> parentOrderTaskIdList = qcResultList.stream().map(e -> NumberUtils.parseLong(e.getExt$Item("taskparentid"))).distinct().collect(Collectors.toList());

            EmcOrderTaskService orderTaskService = ApplicationContextHelper.getBean(EmcOrderTaskService.class);
            List<EmcOrderTaskBean> parentOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(parentOrderTaskIdList, "ID", Arrays.asList("ID", "RUNID"));
            List<Long> runParentOrderTaskIdList = parentOrderTaskList.stream().filter(task -> ObjectUtils.equals(task.getRunId(), qcResultList.get(0).getRunId())).map(EmcOrderTaskBean::getId).collect(Collectors.toList());

            if (!runParentOrderTaskIdList.isEmpty()) {
                List<Long> qcItemIdList = qcResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
                // 查找父样品结果
                List<EmcOrderTaskResultBean> parentTaskResultList = taskResultService.selectScriptListByFilter(SearchFilter.instance().match("ORDERTASKID", runParentOrderTaskIdList).filter(MatchPattern.OR)
                        .match("ITEMID", qcItemIdList).filter(MatchPattern.OR));
                if (!parentTaskResultList.isEmpty()) {
                    parentTaskResultList.stream().filter(subResult -> taskResultList.stream().noneMatch(r -> ObjectUtils.equals(r.getId(), subResult.getId()))).forEach(taskResultList::add);
                }
            }

            // 如果质控结果包含实验室平行，但是不包含样品加标时，需要查询样品加标，涉及到均值问题
            List<String> qcCategoryCodeList = qcResultList.stream().map(EmcOrderTaskResultBean::getQcCategoryCode).distinct().collect(Collectors.toList());
            boolean containLdupNonSpkc = qcCategoryCodeList.contains(PfConstant.QC_L_DUP) && !qcCategoryCodeList.contains(PfConstant.QCCATEGORY_QCCATGORYID_L_SPKC);
            if (containLdupNonSpkc) {
                List<EmcOrderTaskBean> filterTaskList = new ArrayList<>(parentOrderTaskIdList.size());
                parentOrderTaskIdList.forEach(parentTaskId -> {
                    EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                    filterTask.setParentId(parentTaskId);
                    filterTask.setQcCategoryCode(PfConstant.QCCATEGORY_QCCATGORYID_L_SPKC);
                    filterTask.setRunId(qcResultList.get(0).getRunId());
                    filterTaskList.add(filterTask);
                });
                List<EmcOrderTaskBean> runSpkcTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("parentId", "qcCategoryCode", "runId"), Arrays.asList("ID", "RUNID"));
                List<Long> runSpkcTaskIdList = runSpkcTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
                if (!runSpkcTaskIdList.isEmpty()) {
                    List<Long> qcItemIdList = qcResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
                    // 查找父样品结果
                    List<EmcOrderTaskResultBean> spkcTaskResultList = taskResultService.selectScriptListByFilter(SearchFilter.instance().match("ORDERTASKID", runSpkcTaskIdList).filter(MatchPattern.OR)
                            .match("ITEMID", qcItemIdList).filter(MatchPattern.OR));
                    if (!spkcTaskResultList.isEmpty()) {
                        spkcTaskResultList.stream().filter(subResult -> taskResultList.stream().noneMatch(r -> ObjectUtils.equals(r.getId(), subResult.getId()))).forEach(taskResultList::add);
                    }
                }
            }
        }

        List<EmcOrderTaskResultBean> computeTaskResultList = new ArrayList<>(taskResultList);

        List<String> itemIdList = taskResultList.stream().map(r -> "" + r.getItemId()).distinct().collect(Collectors.toList());

        CoreFormulaRequiredService requiredService = ApplicationContextHelper.getBean(CoreFormulaRequiredService.class);

        List<CoreFormulaRequiredBean> formulaRequiredList = new ArrayList<>();

        setupFormulaRequiredList(requiredService, formulaRequiredList, itemIdList);

        if (!formulaRequiredList.isEmpty()) {
            List<Long> relatedItemIdList = formulaRequiredList.stream().map(r -> NumberUtils.parseLong(r.getItemId())).collect(Collectors.toList());

            List<Long> allRelatedItemIdList = requiredService.selectListByFilter(SearchFilter.instance().match("ITEMID", relatedItemIdList).filter(MatchPattern.OR)).stream()
                    .map(e -> Long.valueOf(e.getRequiredItemId())).collect(Collectors.toList());

            allRelatedItemIdList.addAll(relatedItemIdList);

            List<Long> folderIdList = taskResultList.stream().map(EmcOrderTaskResultBean::getFolderId).distinct().collect(Collectors.toList());

            List<EmcOrderTaskResultBean> allRelatedTaskResultList = taskResultService
                    .selectScriptListByFilter(SearchFilter.instance().match("FOLDERID", folderIdList).filter(MatchPattern.OR).match("ITEMID", allRelatedItemIdList).filter(MatchPattern.OR));

            // 需要计算的样品结果List
            List<EmcOrderTaskResultBean> relatedTaskResultList = allRelatedTaskResultList.stream().filter(e -> relatedItemIdList.contains(e.getItemId())).collect(Collectors.toList());

            for (EmcOrderTaskResultBean selectedTaskResult : allRelatedTaskResultList) {
                if (taskResultList.stream().noneMatch(r -> r.getId().equals(selectedTaskResult.getId()))) {
                    taskResultList.add(selectedTaskResult);
                }
            }

            for (EmcOrderTaskResultBean taskResult : relatedTaskResultList) {
                if (computeTaskResultList.stream().noneMatch(r -> r.getId().equals(taskResult.getId()))) {
                    computeTaskResultList.add(taskResult);
                }
            }
        }

        List<EmcOrderTaskResultBean> computeTaskResultProxyList = ObjectUtils.clone(computeTaskResultList).stream().map(BeanUtils::getPropertyListenerProxy).collect(Collectors.toList());

        /**
         * 注：非代理类用于测试公式，代理类用于实际计算公式
         */
        // 对需要进行计算的结果按依赖关系排序
        boolean containDioxin = computeTaskResultProxyList.stream().anyMatch(r -> StringUtils.contains(r.getItemAliasName(), PfConstant.DIOXIN_KEYWORD));
        if (!formulaRequiredList.isEmpty() && containDioxin) {
            Map<Long, Set<Long>> requiredItemIdMap = formulaRequiredList.stream().collect(Collectors.groupingBy(r -> NumberUtils.parseLong(r.getItemId()),
                    Collectors.collectingAndThen(Collectors.toList(), v -> v.stream().map(CoreFormulaRequiredBean::getRequiredItemId).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toSet()))));

            requiredItemIdMap.forEach((itemId, relatedItemIdSet) -> relatedItemIdSet.remove(itemId));

            LinkedHashMap<String, List<EmcOrderTaskResultBean>> orderResultMap = computeTaskResultProxyList.stream().sorted(Comparator.comparing(EmcOrderTaskResultBean::getOrderId)).collect(Collectors.groupingBy(r -> String.join("$", r.getOrderId().toString(), r.getRepeatNo().toString()), LinkedHashMap::new, Collectors.toList()));
            computeTaskResultProxyList.clear();
            orderResultMap.forEach((orderId, curResultList) -> {
                CollectionUtils.sort(curResultList, getComparator(requiredItemIdMap));
                computeTaskResultProxyList.addAll(curResultList);
            });
            orderResultMap.clear();
        }

        GikamComputingDataHolder.put(GikamComputingDataHolder.DEPENDENCY_CHECK_PREFIX + COMPUTE_ORDER_TASK_RESULT_LIST, computeTaskResultList);
        GikamComputingDataHolder.put(COMPUTE_ORDER_TASK_RESULT_LIST, computeTaskResultProxyList);

        List<EmcOrderTaskResultBean> copyList = new ArrayList<>(computeTaskResultList);
        List<EmcOrderTaskResultBean> copyProxyList = new ArrayList<>(computeTaskResultProxyList);
        for (EmcOrderTaskResultBean folderTaskResult : taskResultList) {
            if (copyList.stream().noneMatch(r -> r.getId().equals(folderTaskResult.getId()))) {
                copyList.add(folderTaskResult);
                copyProxyList.add(BeanUtils.getPropertyListenerProxy(ObjectUtils.clone(folderTaskResult)));
            }
        }

        // 对应点位下的关联样品结果
        GikamComputingDataHolder.put(GikamComputingDataHolder.DEPENDENCY_CHECK_PREFIX + FOLDER_ORDER_TASK_RESULT_LIST, copyList);
        GikamComputingDataHolder.put(FOLDER_ORDER_TASK_RESULT_LIST, copyProxyList);
    }

    @Override
    public void testDeadlock() {
        Map<Long, Set<Long>> map = GikamComputingDataHolder.getRelevantData();

        if (map != null) {
            for (Long resultId : map.keySet()) {
                if (containsLeaf(resultId, resultId)) {

                    List<EmcOrderTaskResultBean> folderTaskResultList = getComputeTaskResultList();

                    EmcOrderTaskResultBean deadLockTaskResult = folderTaskResultList.stream().filter(e -> ObjectUtils.equals(resultId, e.getId())).findFirst().get();

                    throw new FormulaException(I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK") + ":" + I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK.TESTNAME")
                            + deadLockTaskResult.getExt$Item("testname") + "," + I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK.METHODNAME") + deadLockTaskResult.getExt$Item("methodname") + ","
                            + I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK.ITEMNAME") + deadLockTaskResult.getItemName());
                }
            }
        }
    }

    /**
     * 获取参与计算的样品结果
     * @return
     */
    public static List<EmcOrderTaskResultBean> getComputeTaskResultList() {
        return GikamComputingDataHolder.get(COMPUTE_ORDER_TASK_RESULT_LIST);
    }

    /**
     * 获取点位下的样品结果
     * @return
     */
    public static List<EmcOrderTaskResultBean> getFolderTaskResultList() {
        return GikamComputingDataHolder.get(FOLDER_ORDER_TASK_RESULT_LIST);
    }

    /**
     * 获取所有样品结果和相关批下的样品结果
     * @return
     */
    public static List<EmcOrderTaskResultBean> getAllTaskResultList() {
        List<EmcOrderTaskResultBean> taskResultList = GikamComputingDataHolder.get(ALL_ORDER_TASK_RESULT_LIST);

        if (taskResultList == null) {
            EmcOrderTaskResultService taskResultService = ApplicationContextHelper.getBean(EmcOrderTaskResultService.class);

            // 获取对应的用于计算以来关系和实际计算的代理类
            List<EmcOrderTaskResultBean> folderTaskResultList = GikamComputingDataHolder.getRaw(GikamComputingDataHolder.DEPENDENCY_CHECK_PREFIX + COMPUTE_ORDER_TASK_RESULT_LIST);
            List<EmcOrderTaskResultBean> folderTaskResultProxyList = GikamComputingDataHolder.getRaw(COMPUTE_ORDER_TASK_RESULT_LIST);
            folderTaskResultList = Optional.ofNullable(folderTaskResultList).orElseGet(ArrayList::new);
            folderTaskResultProxyList = Optional.ofNullable(folderTaskResultProxyList).orElseGet(ArrayList::new);

            List<String> runIdList = folderTaskResultList.stream().map(EmcOrderTaskResultBean::getRunId).filter(runId -> !StringUtils.isEmpty(runId)).distinct().collect(Collectors.toList());

            // 查询所有批下的样品结果
            List<EmcOrderTaskResultBean> runTaskResultList = (runIdList.isEmpty() ? Collections.emptyList()
                    : taskResultService.selectScriptListByFilter(SearchFilter.instance().match("RUNID", runIdList).filter(MatchPattern.OR)));

            // 保证点位下的Object和批下的Object是同一个
            List<EmcOrderTaskResultBean> copyList = new ArrayList<>(folderTaskResultList);
            List<EmcOrderTaskResultBean> copyProxyList = new ArrayList<>(folderTaskResultProxyList);
            List<Long> copyIdList = copyList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
            runTaskResultList.stream().filter(r -> !copyIdList.contains(r.getId())).forEach(r -> {
                copyList.add(r);
                copyProxyList.add(BeanUtils.getPropertyListenerProxy(ObjectUtils.clone(r)));
            });

            GikamComputingDataHolder.put(GikamComputingDataHolder.DEPENDENCY_CHECK_PREFIX + ALL_ORDER_TASK_RESULT_LIST, copyList);
            GikamComputingDataHolder.put(ALL_ORDER_TASK_RESULT_LIST, copyProxyList);

            return GikamComputingDataHolder.get(ALL_ORDER_TASK_RESULT_LIST);
        }

        return taskResultList;
    }

    /**
     * 获取批下所有的样品测试
     * @return
     */
    public static List<EmcOrderTaskBean> getAllOrderTaskList() {
        List<EmcOrderTaskBean> orderTaskList = GikamComputingDataHolder.get(ALL_ORDER_TASK_LIST);

        if (orderTaskList == null) {
            EmcOrderTaskService orderTaskService = ApplicationContextHelper.getBean(EmcOrderTaskService.class);

            List<EmcOrderTaskResultBean> taskResultList = getAllTaskResultList();
            List<Long> orderTaskIdList = taskResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());

            orderTaskList = orderTaskService.getDao().selectListByIds(orderTaskIdList, Arrays.asList("ID", "PARENTID", "QCCATEGORYCODE"));
            GikamComputingDataHolder.put(ALL_ORDER_TASK_LIST, orderTaskList);
        }

        return orderTaskList;
    }

    /**
     * 获取点位下分析项的样品浓度
     */
    public static List<EmcStockConcentBean> getStockConcentList() {
        List<EmcStockConcentBean> stockConcentList = GikamComputingDataHolder.get(STOCK_CONCENT_LIST);

        if (stockConcentList == null) {
            EmcStockConcentService stockConcentService = ApplicationContextHelper.getBean(EmcStockConcentService.class);

            List<EmcOrderTaskResultBean> folderTaskResultList = getFolderTaskResultList();

            List<EmcStockConcentBean> filterStockConcentList = new ArrayList<>();

            folderTaskResultList.forEach(e -> {
                EmcStockConcentBean stockConcent = new EmcStockConcentBean();

                stockConcent.setExt$Item("batchno", e.getExt$Item("stockbatchno"));
                stockConcent.setExt$Item("itemid", e.getItemId() + "");

                filterStockConcentList.add(stockConcent);

            });

            stockConcentList = stockConcentService.selectLimitList(filterStockConcentList);

            // 点位下相关联的样品浓度
            GikamComputingDataHolder.put(STOCK_CONCENT_LIST, stockConcentList);
        }

        return stockConcentList;
    }

    /**
     * 获取批信息
     */
    public static List<EmcRunBean> getRunList() {
        List<EmcRunBean> runList = GikamComputingDataHolder.get(RUN_LIST);

        if (runList == null) {
            EmcRunService runService = ApplicationContextHelper.getBean(EmcRunService.class);

            List<EmcOrderTaskResultBean> folderTaskResultList = getFolderTaskResultList();

            List<String> runIdList = folderTaskResultList.stream().map(EmcOrderTaskResultBean::getRunId).filter(runId -> !StringUtils.isEmpty(runId)).distinct().collect(Collectors.toList());

            runList = runService.selectCurveInfoListByFilter(SearchFilter.instance().match("ID", runIdList).filter(MatchPattern.OR));

            // 批List
            GikamComputingDataHolder.put(RUN_LIST, runList);
        }

        return runList;
    }

    /**
     * 获取点位下分析项的QC指标
     */
    public static List<EmcItemQcIndexBean> getItemQcIndexList() {
        List<EmcItemQcIndexBean> itemQcIndexList = GikamComputingDataHolder.get(ITEM_QC_INDEX_LIST);

        if (itemQcIndexList == null) {
            EmcItemQcIndexService itemQcIndexService = ApplicationContextHelper.getBean(EmcItemQcIndexService.class);

            List<EmcOrderTaskResultBean> folderTaskResultList = getFolderTaskResultList();

            // 查询QC指标
            List<Long> itemIdList = folderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

            itemQcIndexList = itemQcIndexService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");

            // 点位下相关联的QC指标
            GikamComputingDataHolder.put(ITEM_QC_INDEX_LIST, itemQcIndexList);
        }

        return itemQcIndexList;
    }

    /**
     * 获取点位下分析项的分段修约
     */
    public static List<EmcItemRevisionBean> getItemRevisionList() {
        List<EmcItemRevisionBean> itemRevisionList = GikamComputingDataHolder.get(ITEM_REVISION_LIST);

        if (itemRevisionList == null) {
            EmcItemRevisionService itemRevisionService = ApplicationContextHelper.getBean(EmcItemRevisionService.class);

            List<EmcOrderTaskResultBean> folderTaskResultList = getFolderTaskResultList();

            // 查询QC指标及分段修约
            List<Long> itemIdList = folderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

            itemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");

            // 点位下相关联的QC指标
            GikamComputingDataHolder.put(ITEM_REVISION_LIST, itemRevisionList);
        }

        return itemRevisionList;
    }

    /**
     * 修正脚本中的SQL参数
     * @param script 公式脚本
     * @return 修正后的脚本
     */
    private static String reviseSqlArgument(String script) {
        if (StringUtils.isEmpty(script)) {
            return script;
        }

        Matcher sqlMatcher = SQL_PATTER.matcher(script);
        while (sqlMatcher.find()) {
            String ov = sqlMatcher.group();
            String oldOv = ov;

            ov = ov.replaceAll("\",\"", ",");
            Matcher argMatcher = ARG_PATTERN.matcher(ov);
            List<String> argList = new ArrayList<>();
            while (argMatcher.find()) {
                String arg = argMatcher.group();
                argList.add(arg.substring(1, arg.length() - 1));
            }

            StringBuilder replacedValue = new StringBuilder(ov.substring(0, ov.length() - 2));
            if (!argList.isEmpty()) {
                argList.forEach(s -> replacedValue.append(",").append(s));
            }
            replacedValue.append("})");
            ov = replacedValue.toString();

            script = StringUtils.replace(script, oldOv, ov);
        }

        return script;
    }

    private Comparator<EmcOrderTaskResultBean> getComparator(Map<Long, Set<Long>> map) {
        return (v1, v2) -> {
            Set<Long> set1 = map.get(v1.getItemId());

            if (set1 != null && set1.contains(v2.getItemId())) { // v1 依赖于 v2
                return 1;
            }

            Set<Long> set2 = map.get(v2.getItemId());

            if (set2 != null && set2.contains(v1.getItemId())) { // v2 依赖于 v1
                return -1;
            }

            return 0;
        };
    }

    private Comparator<EmcOrderTaskResultBean> getComparator() {
        Map<Long, Set<Long>> map = GikamComputingDataHolder.getRelevantData();

        return (v1, v2) -> {
            Set<Long> set1 = map.get(v1.getId());

            if (set1 != null && set1.contains(v2.getId())) { // v1 依赖于 v2
                return 1;
            }

            Set<Long> set2 = map.get(v2.getId());

            if (set2 != null && set2.contains(v1.getId())) { // v2 依赖于 v1
                return -1;
            }

            return 0;
        };
    }

    private void setupFormulaRequiredList(CoreFormulaRequiredService requiredService, List<CoreFormulaRequiredBean> formulaRequiredList, List<String> itemIdList) {
        if (itemIdList.isEmpty()) {
            return;
        }

        List<CoreFormulaRequiredBean> requiredList = requiredService.selectListByFilter(SearchFilter.instance().match("REQUIREDITEMID", itemIdList).filter(MatchPattern.OR), Order.asc("ID"));

        requiredList.removeIf(r -> formulaRequiredList.stream().anyMatch(r1 -> r1.getId().equals(r.getId())));

        if (requiredList.isEmpty()) {
            return;
        }

        formulaRequiredList.addAll(requiredList);

        List<String> nextItemIdList = requiredList.stream().map(CoreFormulaRequiredBean::getItemId).distinct().collect(Collectors.toList());

        setupFormulaRequiredList(requiredService, formulaRequiredList, nextItemIdList);
    }
}
