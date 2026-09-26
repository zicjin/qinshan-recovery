package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.impl;

import java.math.BigDecimal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.computing.async.AsyncFormulaComputingService;
import com.sunwayworld.emc.module.bp.bp.reading.EmcOrderTaskResultReadingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service.EmcEquiptAcqFileService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.bean.EmcEquiptAcqParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.EmcEquiptAcqParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasPreAnalyService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

/**
 * @author zhengxy@sunwayworld.com 2021年06月22日
 */
@Repository
@GikamBean
public class PfEquiptAcqResultServiceImpl extends EmcEquiptAcqResultServiceImpl {

    private static final String DILUTION_ITEM_NAME = "稀释倍数";
    private static final Pattern DILUTION_ORDER_NO_PATTERN = Pattern.compile("\\S+-\\d+");
    private static final String[] SERIAL_ACQ_SUFFIXS = {"前", "后"};

    @Autowired
    private EmcEquiptService equiptService;
    @Autowired
    private EmcEquiptAcqParamService equiptAcqParamService;
    @Autowired
    private EmcEquiptAcqScriptService equiptAcqScriptService;
    @Autowired
    private EmcEasPreAnalyService easPreAnalyService;
    @Autowired
    private EmcEasParamService easParamService;
    @Autowired
    private EmcEasResultService easResultService;
    @Autowired
    private EmcEquiptTestItemService equiptTestItemService;
    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;
    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcRunService runService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private AsyncFormulaComputingService asyncFormulaComputingService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void insertResultByAnalyAcqFiles(List<EmcEquiptAcqFileBean> acqFileList) {

        List<EmcEquiptAcqFileBean> updateAcqFileList = new ArrayList<>();
        List<EmcEquiptAcqResultBean> allResultList = new ArrayList<>();
        Long equiptId = acqFileList.get(0).getEquiptId();

        EmcEquiptBean equipt = equiptService.selectDetail(equiptId);
        if (equipt == null) {
            return;
        }

        String scriptCategory = equipt.getScriptCategory();
        Long scriptId = equipt.getAcqScriptId();

        if (scriptCategory == null || scriptId == null) {
            return;
        }

        List<Long> validScriptIdList = new ArrayList<>();
        validScriptIdList.add(scriptId);

        String acqScriptIds = equipt.getAcqScriptIds();
        if (!StringUtils.isEmpty(acqScriptIds)) {
            List<Long> otherAcqScriptIdList = Arrays.stream(StringUtils.split(acqScriptIds, ",")).filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
            validScriptIdList.addAll(otherAcqScriptIdList);
        }
        List<EmcEquiptAcqScriptBean> scriptList = equiptAcqScriptService.getDao().selectListByOneColumnValues(validScriptIdList, "id");
        Map<Long, EmcEquiptAcqScriptBean> scriptMap = scriptList.stream().collect(Collectors.toMap(EmcEquiptAcqScriptBean::getId, Function.identity()));

        List<Long> noResultTaskIdList = new ArrayList<>();
        List<EmcOrderTaskResultBean> allUpdateOrderTaskResultList = new ArrayList<>();
        List<String> judgedQcCodeList = Arrays.asList(PfConstant.QC_QXJH, PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD);

        // 判断走通用脚本还是定制脚本
        if ("acq-script".equals(scriptCategory)) {// 通用脚本

            EmcEquiptAcqScriptBean curScript = null;
            for (Long curScriptId : validScriptIdList) {
                EmcEquiptAcqScriptBean script = scriptMap.get(curScriptId);
                if (script == null) {
                    continue;
                }

                parseResult(curScriptId, acqFileList, script, updateAcqFileList, allResultList);
                curScript = script;
                if (!allResultList.isEmpty()) {
                    break;
                }
            }

            if (curScript == null || allResultList.isEmpty()) {
                return;
            }

            // 稀释后采集
            boolean diluteAcq = Constant.YES.equals(curScript.getExt$Item("diluteAcq"));
            // 判定后采集
            boolean judgedAcq = Constant.YES.equals(curScript.getExt$Item("judgedAcq"));
            boolean serialAcq = "1".equals(curScript.getSerialAcq()) && allResultList.stream()
                    .map(EmcEquiptAcqResultBean::getBizNo)
                    .anyMatch(bizNo -> Arrays.stream(SERIAL_ACQ_SUFFIXS).anyMatch(suffix -> StringUtils.contains(bizNo, suffix)));

            getDao().insert(allResultList);

            // 将对应的测试结果进行更新
            // 根据仪器编码找到维护的分析项，与结果表进行匹配，找到需要更新的分析项

            List<EmcEquiptTestItemBean> equiptTestItemList = equiptTestItemService.selectListByFilter(SearchFilter.instance().match("equiptId", equiptId).filter(MatchPattern.EQ));

            List<EmcEquiptTestItemBean> updateEquiptTestItemList = equiptTestItemList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMatchItemName()) && !ObjectUtils.isEmpty(e.getMatchResultName()))
                    .collect(Collectors.toList());

            List<String> orderNoList = allResultList.stream().map(EmcEquiptAcqResultBean::getBizNo).distinct().collect(Collectors.toList());
            if (orderNoList.isEmpty()) {
                return;
            }

            List<String> runIdList = acqFileList.stream().filter(acqFile -> !StringUtils.isEmpty(acqFile.getRunId())).map(EmcEquiptAcqFileBean::getRunId).collect(Collectors.toList());
            List<EmcRunBean> runList = runService.getDao().selectListByOneColumnValues(runIdList, "ID", Arrays.asList("ID", "STATUS"));
            List<String> validRunIdList = runList.stream().filter(run -> StringUtils.equals("draft", run.getStatus())).map(EmcRunBean::getId).collect(Collectors.toList());

            // 对于有稀释倍数的，需要处理样品编号
            Set<String> actualOrderNoList = new HashSet<>(orderNoList);
            if (diluteAcq) {
                orderNoList.stream().filter(orderNo -> DILUTION_ORDER_NO_PATTERN.matcher(orderNo).matches()).forEach(orderNo -> actualOrderNoList.add(orderNo.substring(0, orderNo.lastIndexOf("-"))));
            }
            if (serialAcq) {
                Arrays.stream(SERIAL_ACQ_SUFFIXS).forEach(suffix -> orderNoList.forEach(orderNo -> actualOrderNoList.add(StringUtils.replace(orderNo, suffix, ""))));
            }

            List<EmcOrderTaskResultBean> allOrderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                    .match(new String[] { "ORDERNO", "EQUIPTACQUNO" }, new ArrayList<>(actualOrderNoList)).filter(MatchPattern.OR)
                    .match("RUNID", validRunIdList).filter(MatchPattern.OR));

            if (diluteAcq) {
                // 对于一个测试有多个分析项组的，每个分析项组可能都有一个稀释倍数
                List<Long> itemIdList = allOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
                List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemIdList, "itemId", Arrays.asList("id", "itemGroupId", "itemId"));
                Map<Long, Long> groupItemMap = groupItemList.stream().collect(Collectors.toMap(EmcGroupItemBean::getItemId, EmcGroupItemBean::getItemGroupId, (v1, v2) -> v1));
                allOrderTaskResultList.forEach(r -> Optional.ofNullable(groupItemMap.get(r.getItemId())).ifPresent(itemGroupId -> r.setExt$Item("itemGroupId", itemGroupId + "")));
            }

            if (!updateEquiptTestItemList.isEmpty() && allResultList.size() > 0) {
                updateEquiptTestItemList.forEach(e -> {

                    // 匹配到文件中对应的分析项
                    List<EmcEquiptAcqResultBean> matchAcqResultList = null;
                    if (allResultList.get(0).getTestName() != null) {

                        matchAcqResultList = allResultList.stream().filter(r -> ObjectUtils.equals(e.getMatchItemName(), r.getItemName())
                                && ObjectUtils.equals(e.getMatchResultName(), r.getFieldName()) && ObjectUtils.equals(e.getExt$Item("testname"), r.getTestName())).collect(Collectors.toList());
                    } else {

                        matchAcqResultList = allResultList.stream()
                                .filter(r -> ObjectUtils.equals(e.getMatchItemName(), r.getItemName()) && ObjectUtils.equals(e.getMatchResultName(), r.getFieldName())).collect(Collectors.toList());
                    }

                    if (!matchAcqResultList.isEmpty()) {
                        // 如果是稀释后采集，需要先获取到稀释倍数分析项的值，根据稀释倍数的值，动态拼接样品编号作为数据匹配的样品编号
                        if (diluteAcq) {
                            Map<String, List<EmcEquiptAcqResultBean>> orderNoAcqResultMap = matchAcqResultList.stream().collect(Collectors.groupingBy(EmcEquiptAcqResultBean::getBizNo));
                            // 按样品进行分组
                            Map<String, List<EmcOrderTaskResultBean>> orderTaskResultMap = allOrderTaskResultList.stream().collect(Collectors.groupingBy(result -> Optional.ofNullable(result.getExt$Item("equiptacquno")).orElse(result.getOrderNo())));
                            orderTaskResultMap.forEach((orderNo, taskResultList) -> {
                                taskResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroupId"))).forEach(r -> r.setExt$Item("itemGroupId", r.getOrderTaskId() + ""));
                                Map<String, List<EmcOrderTaskResultBean>> itemGroupResultMap = taskResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroupId")));

                                // 按测试进行分组，每个测试的稀释倍数可能不一样
                                itemGroupResultMap.forEach((orderTaskId, resultList) -> {
                                    EmcOrderTaskResultBean dilutionResult = resultList.stream().filter(result -> StringUtils.contains(result.getItemName(), DILUTION_ITEM_NAME)).findFirst().orElseGet(EmcOrderTaskResultBean::new);
                                    String dilutionValue = Optional.ofNullable(dilutionResult.getOriginalResult()).orElse("");
                                    String realOrderNo = (StringUtils.equals(dilutionValue, "1") || !NumberUtils.isNumber(dilutionValue)) ? orderNo : String.join("-", orderNo, dilutionValue);
                                    Optional.ofNullable(orderNoAcqResultMap.get(realOrderNo)).ifPresent(acqResultList -> {

                                        // 判定后采集，只针对曲线校核和实验室标准样品两种质控
                                        if (judgedAcq && judgedQcCodeList.contains(dilutionResult.getQcCategoryCode())) {
                                            judgeAndAcq(e, resultList, dilutionResult, acqResultList, allUpdateOrderTaskResultList);
                                            return;
                                        }

                                        acqResultList.forEach(acqResult -> {
                                            boolean scientificNumber = NumberUtils.isScientificNumber(acqResult.getResult());
                                            List<EmcOrderTaskResultBean> curUpdateResultList = resultList.stream().filter(result -> ObjectUtils.equals(e.getItemId(), result.getItemId()) && ObjectUtils.equals(e.getTestMethodId(), result.getTestMethodId())).collect(Collectors.toList());
                                            curUpdateResultList.forEach(result -> {
                                                result.setOriginalResult(scientificNumber ? NumberUtils.formatNumber(new BigDecimal(acqResult.getResult())) : acqResult.getResult());
                                            });
                                            allUpdateOrderTaskResultList.addAll(curUpdateResultList);
                                        });
                                    });
                                });
                            });
                        } else {
                            matchAcqResultList.forEach(a -> {
                                // 匹配到结果表的分析项
                                List<EmcOrderTaskResultBean> updateOrderTaskResultList = allOrderTaskResultList.stream()
                                        .filter(o -> ObjectUtils.equals(o.getItemId(), e.getItemId()) && ObjectUtils.equals(o.getTestMethodId(), e.getTestMethodId())
                                                && (ObjectUtils.equals(o.getExt$Item("equiptacquno"), a.getBizNo())
                                                    || (StringUtils.isEmpty(o.getExt$Item("equiptacquno")) && ObjectUtils.equals(o.getOrderNo(), a.getBizNo()))
                                                    || (serialAcq && Arrays.stream(SERIAL_ACQ_SUFFIXS).anyMatch(suffix -> ObjectUtils.equals(o.getExt$Item("equiptacquno") + suffix, a.getBizNo()) || (StringUtils.isEmpty(o.getExt$Item("equiptacquno")) && ObjectUtils.equals(o.getOrderNo() + suffix, a.getBizNo()))))))
                                        .collect(Collectors.toList());

                                if (!updateOrderTaskResultList.isEmpty()) {
                                    boolean scientificNumber = NumberUtils.isScientificNumber(a.getResult());
                                    updateOrderTaskResultList.forEach(o -> {
                                        o.setOriginalResult(scientificNumber ? NumberUtils.formatNumber(new BigDecimal(a.getResult())) : a.getResult());
                                    });
                                }

                                allUpdateOrderTaskResultList.addAll(updateOrderTaskResultList);
                            });
                        }
                    }
                });
            }

            // 未采集到数据的 task，只针对实验室空白
            List<Long> allTaskIdList = allOrderTaskResultList.stream().filter(r -> PfConstant.QCCATEGORY_QCCATEGORYID_L_BLANK.equals(r.getQcCategoryCode())).map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
            List<Long> updateTaskIdLIst = allUpdateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
            noResultTaskIdList.addAll(allTaskIdList.stream().filter(id -> !updateTaskIdLIst.contains(id)).collect(Collectors.toList()));
        } else {// 定制脚本
            for (EmcEquiptAcqFileBean equiptAcqFile : acqFileList) {

                List<EmcOrderTaskResultBean> updateOrderTaskResultList = EmcOrderTaskResultReadingEngine.getEngine(equiptAcqFile).read();

                if (updateOrderTaskResultList != null && updateOrderTaskResultList.size() > 0) {

                    equiptAcqFile.setStatus(EmcConstant.EQUIPT_ACQ_FILE_STATUS_DONE);
                    updateAcqFileList.add(equiptAcqFile);
                    allUpdateOrderTaskResultList.addAll(updateOrderTaskResultList);
                }
            }
        }

        // 文件数据已更新到结果表
        if (updateAcqFileList.size() > 0) {
            EmcEquiptAcqFileService acqFileService = ApplicationContextHelper.getBean(EmcEquiptAcqFileService.class);
            // 谱图文件内容太多时，会导致存储失败，解决方法待优化
            acqFileService.getDao().update(updateAcqFileList, "status");
        }

        // 将文件信息添加到样品测试表下
        List<CoreFileBean> insertCoreFileList = new ArrayList<>();
        List<Long> orderTaskIdList = allUpdateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());

        for (EmcEquiptAcqFileBean equiptAcqFileBean : acqFileList) {

            Long fileId = NumberUtils.parseLong(equiptAcqFileBean.getExt$().get("fileid"));
            CoreFileBean file = coreFileService.selectDetail(fileId);
            orderTaskIdList.forEach(id -> {

                CoreFileBean insertCoreFile = new CoreFileBean();

                BeanUtils.copyProperties(file, insertCoreFile);
                insertCoreFile.setId(ApplicationContextHelper.getNextIdentity());
                insertCoreFile.setTargetId(orderTaskService.getDao().getTable() + "$" + id);

                insertCoreFileList.add(insertCoreFile);

            });
        }

        // 更新仪器信息
        List<EmcOrderTaskBean> updateTaskList = new ArrayList<>(orderTaskIdList.size());
        orderTaskIdList.forEach(taskId -> {
            EmcOrderTaskBean updateTask = new EmcOrderTaskBean();
            updateTask.setId(taskId);
            updateTask.setEquiptId(equiptId);
            updateTaskList.add(updateTask);
        });

        noResultTaskIdList.forEach(taskId -> {
            EmcOrderTaskBean updateTask = new EmcOrderTaskBean();
            updateTask.setId(taskId);
            updateTask.setEquiptId(null);
            updateTaskList.add(updateTask);
        });

        coreFileService.getDao().insert(insertCoreFileList);
        orderTaskResultService.getDao().update(allUpdateOrderTaskResultList, "originalResult");
        orderTaskService.getDao().update(updateTaskList, "equiptId");

        // 触发计算公式
        List<Long> orderTaskResultIdList = allUpdateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        if (!orderTaskResultIdList.isEmpty()) {
            List<String> runIdList = acqFileList.stream().filter(acqFile -> !StringUtils.isEmpty(acqFile.getRunId())).map(EmcEquiptAcqFileBean::getRunId).distinct().collect(Collectors.toList());
            List<EmcRunBean> runList = runService.getDao().selectListByOneColumnValues(runIdList, "id", Arrays.asList("id", "analysisUserId"));

            boolean containBlab = allUpdateOrderTaskResultList.stream().anyMatch(r -> StringUtils.equals(r.getQcCategoryCode(), PfConstant.QC_L_BLAB));
            if (containBlab) {
                // 对于包含后管的，需要触发两次计算
                EmcOrderTaskResultComputingEngine.getEngine(orderTaskResultIdList).compute();
            }

            asyncFormulaComputingService.asyncComputing(runList, orderTaskResultIdList);
        }
    }

    @Override
    @Transactional
    public void insertSceneResultByAnalyAcqFiles(List<EmcEquiptAcqFileBean> acqFileList) {
        ArrayList updateAcqFileList = new ArrayList();
        Long scriptId = EmcConstant.EQUIPT_ACQ_SCRIPT_ID;
        ArrayList<String> contentList = new ArrayList<String>();
        for (EmcEquiptAcqFileBean equiptAcqFile : acqFileList) {
            String content = equiptAcqFile.getContent();
            contentList.add(content);
        }
        List<EmcEquiptAcqResultBean> allResultList = this.getSceneResultList(contentList, scriptId);
        if (allResultList.isEmpty()) {
            return;
        }
        this.getDao().insert(allResultList);
        List equiptNoList = allResultList.stream().filter(e -> !StringUtils.isBlank(e.getSceneEquiptNo())).map(EmcEquiptAcqResultBean::getSceneEquiptNo).distinct().collect(Collectors.toList());
        if (equiptNoList.isEmpty()) {
            return;
        }
        List fileNoList = allResultList.stream().filter(e -> !StringUtils.isBlank(e.getSceneFileNo())).map(EmcEquiptAcqResultBean::getSceneFileNo).distinct().collect(Collectors.toList());
        if (fileNoList.isEmpty()) {
            return;
        }
        List<EmcOrderTaskResultBean> allOrderTaskResultList = this.orderTaskResultService.selectListByFilter(SearchFilter.instance().match("SCENEEQUIPTCOLLECTNO", equiptNoList).filter(MatchPattern.OR).match("SCENEFILENO", fileNoList).filter(MatchPattern.OR), new Order[0]);
        ArrayList<EmcOrderTaskResultBean> allUpdateOrderTaskResultList = new ArrayList<>();
        allResultList.forEach(a2 -> {
            List<EmcOrderTaskResultBean> updateOrderTaskResultList = allOrderTaskResultList.stream().filter(o -> ObjectUtils.equals(o.getItemName(), a2.getItemName()) && ObjectUtils.equals(o.getExt$Item("sceneequiptcollectno"), a2.getSceneEquiptNo()) && ObjectUtils.equals(o.getSceneFileNo(), a2.getSceneFileNo())).collect(Collectors.toList());
            if (!updateOrderTaskResultList.isEmpty()) {
                updateOrderTaskResultList.forEach(o -> o.setOriginalResult(a2.getResult()));
            }
            allUpdateOrderTaskResultList.addAll(updateOrderTaskResultList);
        });
        this.orderTaskResultService.getDao().update(allUpdateOrderTaskResultList, new String[]{"originalResult"});
    }

    @Override
    public List<EmcEquiptAcqResultBean> getSceneResultList(List<String> contentList, Long scriptId) {
        ArrayList<EmcEquiptAcqResultBean> allResultList = new ArrayList<EmcEquiptAcqResultBean>();
        EmcEquiptAcqScriptBean scriptBean = (EmcEquiptAcqScriptBean)this.equiptAcqScriptService.selectByIdIfPresent(scriptId);
        if (scriptBean == null) {
            return Collections.emptyList();
        }
        List<EmcEasResultBean> scriptResultList = this.easResultService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ), new Order[0]);
        if (scriptResultList.isEmpty()) {
            return Collections.emptyList();
        }
        List<EmcEasParamBean> scriptParamList = this.easParamService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ), new Order[0]);
        EmcEasParamBean equipNoParamBean = scriptParamList.stream().filter(e -> ObjectUtils.equals("equiptNo", e.getExt$Item("field"))).findFirst().orElse(null);
        if (equipNoParamBean == null) {
            return Collections.emptyList();
        }
        EmcEasParamBean fileNoParamBean = scriptParamList.stream().filter(e -> ObjectUtils.equals("fileNo", e.getExt$Item("field"))).findFirst().orElse(null);
        if (fileNoParamBean == null) {
            return Collections.emptyList();
        }
        List<EmcEasPreAnalyBean> scriptPreAnalyList = this.easPreAnalyService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ), new Order[0]);
        for (String content : contentList) {
            if (StringUtils.isEmpty(content)) continue;
            content = this.preAnalyContent(content, scriptPreAnalyList);
            ArrayList<String> rowList = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new StringReader(content));
            String resultRow = null;
            try {
                while ((resultRow = reader.readLine()) != null) {
                    if (StringUtils.isBlank(resultRow)) continue;
                    rowList.add(resultRow);
                }
            }
            catch (IOException e2) {
                throw new UnexpectedException(e2);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException iOException) {}
                }
            }
            String equiptNo = this.getSceneResultByKey(rowList, equipNoParamBean.getBeginSymbol());
            String fileNo = this.getSceneResultByKey(rowList, fileNoParamBean.getBeginSymbol());
            for (EmcEasResultBean easResultBean : scriptResultList) {
                this.updateSceneAcqResultBeanList(allResultList, equiptNo, fileNo, easResultBean.getFieldName(), this.getSceneResultByKey(rowList, easResultBean.getFieldName()));
            }
        }
        return allResultList;
    }

    private void updateSceneAcqResultBeanList(List<EmcEquiptAcqResultBean> allResultList, String equiptNo, String fileNo, String itemName, String result) {
        EmcEquiptAcqResultBean acqResultBean = this.getSceneAcqResultBean(equiptNo, fileNo, itemName, result);
        if (acqResultBean != null) {
            allResultList.add(acqResultBean);
        }
    }

    private EmcEquiptAcqResultBean getSceneAcqResultBean(String equiptNo, String fileNo, String itemName, String result) {
        if (StringUtils.isBlank(itemName)) {
            return null;
        }
        if (StringUtils.isBlank(result)) {
            return null;
        }
        if (!NumberUtils.isNumber(result)) {
            return null;
        }
        EmcEquiptAcqResultBean acqResultBean = new EmcEquiptAcqResultBean();
        acqResultBean.setId(ApplicationContextHelper.getNextIdentity());
        acqResultBean.setSceneEquiptNo(equiptNo);
        acqResultBean.setSceneFileNo(fileNo);
        acqResultBean.setItemName(itemName);
        acqResultBean.setResult(result);
        return acqResultBean;
    }

    private String getSceneResultByKey(List<String> rowList, String key) {
        if (rowList == null || rowList.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }
        String row = rowList.stream().filter(e -> e.contains(key)).findFirst().orElse(null);
        if (row == null) {
            return null;
        }
        int resultBeginIndex = row.indexOf(key) + key.length();
        if (resultBeginIndex >= row.length()) {
            return null;
        }
        String result = row.substring(resultBeginIndex);
        result = StringUtils.trim(result);
        result = result.replaceAll("/r/n", "");
        return result;
    }


    /**
     * 判定后采集
     * @param testItem
     * @param resultList
     * @param dilutionResult
     * @param acqResultList
     * @param allUpdateOrderTaskResultList
     */
    private void judgeAndAcq(EmcEquiptTestItemBean testItem, List<EmcOrderTaskResultBean> resultList, EmcOrderTaskResultBean dilutionResult, List<EmcEquiptAcqResultBean> acqResultList, List<EmcOrderTaskResultBean> allUpdateOrderTaskResultList) {
        // 如果是曲线校核
        if (PfConstant.QC_QXJH.equals(dilutionResult.getQcCategoryCode())) {
            // 首先获取标点值
            EmcOrderTaskResultBean validStandQtyResult = resultList.stream().filter(result -> NumberUtils.isNumber(result.getStandQty())).findFirst().orElse(null);
            if (validStandQtyResult == null) {
                return;
            }

            double standQty = NumberUtils.parseDouble(validStandQtyResult.getStandQty());
            double minValue = standQty * 0.9;
            double maxValue = standQty * 1.1;
            EmcEquiptAcqResultBean validAcqResult = acqResultList.stream().filter(acqResult -> NumberUtils.isNumber(acqResult.getResult())
                    && NumberUtils.parseDouble(acqResult.getResult()) >= minValue
                    && NumberUtils.parseDouble(acqResult.getResult()) <= maxValue).findFirst().orElseGet(EmcEquiptAcqResultBean::new);
            boolean scientificNumber = NumberUtils.isScientificNumber(validAcqResult.getResult());

            List<EmcOrderTaskResultBean> curUpdateResultList = resultList.stream().filter(result -> ObjectUtils.equals(testItem.getItemId(), result.getItemId()) && ObjectUtils.equals(testItem.getTestMethodId(), result.getTestMethodId())).collect(Collectors.toList());
            curUpdateResultList.forEach(result -> {
                result.setOriginalResult(scientificNumber ? NumberUtils.formatNumber(new BigDecimal(validAcqResult.getResult())) : validAcqResult.getResult());
            });
            allUpdateOrderTaskResultList.addAll(curUpdateResultList);

            return;
        }

        // 实验室标准样品
        if (PfConstant.QCCATEGORY_QCCATEGORYID_QC_STD.equals(dilutionResult.getQcCategoryCode())) {
            // 首先获取指标高低限值
            EmcOrderTaskResultBean validResult = resultList.stream().filter(result -> NumberUtils.isNumber(result.getIndexLowLimit()) && NumberUtils.isNumber(result.getIndexHighLimit())).findFirst().orElse(null);
            if (validResult == null) {
                return;
            }

            EmcEquiptAcqResultBean validAcqResult = acqResultList.stream().filter(acqResult -> NumberUtils.isNumber(acqResult.getResult())
                    && NumberUtils.parseDouble(acqResult.getResult()) >= NumberUtils.parseDouble(validResult.getIndexLowLimit())
                    && NumberUtils.parseDouble(acqResult.getResult()) <= NumberUtils.parseDouble(validResult.getIndexHighLimit())).findFirst().orElseGet(EmcEquiptAcqResultBean::new);
            boolean scientificNumber = NumberUtils.isScientificNumber(validAcqResult.getResult());

            List<EmcOrderTaskResultBean> curUpdateResultList = resultList.stream().filter(result -> ObjectUtils.equals(testItem.getItemId(), result.getItemId()) && ObjectUtils.equals(testItem.getTestMethodId(), result.getTestMethodId())).collect(Collectors.toList());
            curUpdateResultList.forEach(result -> {
                result.setOriginalResult(scientificNumber ? NumberUtils.formatNumber(new BigDecimal(validAcqResult.getResult())) : validAcqResult.getResult());
            });
            allUpdateOrderTaskResultList.addAll(curUpdateResultList);
        }
    }

    private void parseResult(Long scriptId, List<EmcEquiptAcqFileBean> acqFileList, EmcEquiptAcqScriptBean script, List<EmcEquiptAcqFileBean> updateAcqFileList, List<EmcEquiptAcqResultBean> allResultList) {
        List<EmcEasResultBean> scriptResultList = easResultService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ));
        if (scriptResultList.isEmpty()) {
            return;
        }

        List<EmcEasPreAnalyBean> scriptPreAnalyList = easPreAnalyService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ));
        List<EmcEasParamBean> scriptParamList = easParamService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ));
        List<EmcEasParamBean> resultParamList = new ArrayList<>();
        List<EmcEasParamBean> orderNoParamList = new ArrayList<>();
        List<EmcEasParamBean> testNameParamList = new ArrayList<>();

        List<Long> scriptParamIdList = scriptParamList.stream().map(EmcEasParamBean::getParamId).collect(Collectors.toList());
        List<EmcEquiptAcqParamBean> acqParamList = equiptAcqParamService.getDao().selectListByOneColumnValues(scriptParamIdList, "id");

        for (EmcEquiptAcqFileBean equiptAcqFile : acqFileList) {

            Long acqFileId = equiptAcqFile.getId();
            String content = equiptAcqFile.getContent();

            if (StringUtils.isEmpty(content)) {
                continue;
            }

            String rowDataSeparator = script.getRowDataSeparator();
            if (StringUtils.isEmpty(rowDataSeparator)) {
                return;
            }

            // 预处理
            content = preAnalyContent(content, scriptPreAnalyList);

            // 分段参数+结果范围参数+样品范围参数+测试名称范围参数
            String paragraphMark = null; // 分段标记

            for (int i = scriptParamList.size() - 1; i >= 0; i--) {

                EmcEasParamBean scriptParam = scriptParamList.get(i);
                String field = acqParamList.stream().filter(e -> ObjectUtils.equals(e.getId(), scriptParam.getParamId())).findFirst().orElse(null).getField(); // 判断是什么作用的参数

                if (EmcConstant.EQUIPT_ACQ_PARAM_RESULT.equals(field)) {
                    resultParamList.add(scriptParam);
                } else if (EmcConstant.EQUIPT_ACQ_PARAM_ORDER_NUMBER.equals(field)) {
                    orderNoParamList.add(scriptParam);
                } else if (EmcConstant.EQUIPT_ACQ_PARAM_PARAGRAPH.equals(field)) {
                    paragraphMark = scriptParam.getBeginSymbol();
                } else if (EmcConstant.EQUIPT_ACQ_PARAM_TEST_NAME.equals(field)) {
                    testNameParamList.add(scriptParam);
                }
            }

            // 如果数据多段，分割字符串获取数据
            String[] paragraphArray = null;
            if (EmcConstant.EQUIPT_ACQ_DATA_FORMAT_MULTIPLE.equals(script.getDataFormat()) && !StringUtils.isEmpty(paragraphMark) && content.contains(paragraphMark)) {
                paragraphArray = content.split(paragraphMark);
                for (int i = paragraphArray.length - 1; i >= 0; i--) {
                    paragraphArray[i] = paragraphArray[i] + paragraphMark;
                }
            } else {
                paragraphArray = new String[1];
                content = "defaultStartFlag" + content + "defaultEndFlag";
                paragraphArray[0] = content;
            }

            // 生成分析结果
            List<EmcEquiptAcqResultBean> resultList = new ArrayList<>();
            String allResultContent = analyParagraphArray(acqFileId, rowDataSeparator, resultParamList, orderNoParamList, testNameParamList, scriptResultList, script, paragraphArray, resultList);

            if (!resultList.isEmpty()) {
                resultList.forEach(result -> {
                    result.setId(ApplicationContextHelper.getNextIdentity());
                    result.setFileName(equiptAcqFile.getSourceFileName());
                });

                // 插入样品分析结果表
                allResultList.addAll(resultList);
                equiptAcqFile.setStatus(EmcConstant.EQUIPT_ACQ_FILE_STATUS_DONE);
                equiptAcqFile.setResult(allResultContent);
                updateAcqFileList.add(equiptAcqFile);
            }
        }
    }
}
