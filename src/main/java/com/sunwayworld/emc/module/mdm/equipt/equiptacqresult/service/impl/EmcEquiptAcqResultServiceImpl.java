package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.reading.EmcOrderTaskResultReadingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.service.EmcEquiptAcqFileService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.bean.EmcEquiptAcqParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.EmcEquiptAcqParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.dao.EmcEquiptAcqResultDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.EmcEquiptAcqResultService;
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
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

@Repository
@GikamBean
public class EmcEquiptAcqResultServiceImpl implements EmcEquiptAcqResultService {

    @Autowired
    private EmcEquiptAcqResultDao emcEquiptAcqResultDao;
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

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptAcqResultDao getDao() {
        return emcEquiptAcqResultDao;
    }

    @Transactional
    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void insertResultByAnalyAcqFiles(List<EmcEquiptAcqFileBean> acqFileList) {

        List<EmcEquiptAcqFileBean> updateAcqFileList = new ArrayList<>();
        List<EmcEquiptAcqResultBean> allResultList = new ArrayList<EmcEquiptAcqResultBean>();
        Long equiptId = acqFileList.get(0).getEquiptId();

        EmcEquiptBean equipt = equiptService.selectDetail(equiptId);
        String scriptCategory = equipt.getScriptCategory();
        Long scriptId = equipt.getAcqScriptId();

        if (equipt == null || scriptCategory == null || scriptId == null) {
            return;
        }

        EmcEquiptAcqScriptBean script = equiptAcqScriptService.selectDetail(scriptId);
        List<EmcOrderTaskResultBean> allUpdateOrderTaskResultList = new ArrayList<>();

        // 判断走通用脚本还是定制脚本
        if ("acq-script".equals(scriptCategory)) {// 通用脚本

            List<EmcEasResultBean> scriptResultList = easResultService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ));
            if (scriptResultList.isEmpty()) {
                return;
            }

            List<EmcEasPreAnalyBean> scriptPreAnalyList = easPreAnalyService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ));
            List<EmcEasParamBean> scriptParamList = easParamService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", scriptId).filter(MatchPattern.EQ));
            List<EmcEasParamBean> resultParamList = new ArrayList<>();
            List<EmcEasParamBean> orderNoParamList = new ArrayList<>();

            List<Long> scriptParamIdList = scriptParamList.stream().map(e -> e.getParamId()).collect(Collectors.toList());
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
                    }
                }

                // 如果数据多段，分割字符串获取数据
                String[] paragraphArray = null;
                if (EmcConstant.EQUIPT_ACQ_DATA_FORMAT_MULTIPLE.equals(script.getDataFormat()) && !StringUtils.isEmpty(paragraphMark) && content.contains(paragraphMark)) {
                    paragraphArray = content.split(paragraphMark);
                    for (int i = paragraphArray.length - 1; i >= 0; i--) {
                        paragraphArray[i] = "defaultStartFlag" + paragraphArray[i] + "defaultEndFlag";
                    }
                } else {
                    paragraphArray = new String[1];
                    content = "defaultStartFlag" + content + "defaultEndFlag";
                    paragraphArray[0] = content;
                }

                // 生成分析结果
                List<EmcEquiptAcqResultBean> resultList = new ArrayList<EmcEquiptAcqResultBean>();
                String allResultContent = analyParagraphArray(acqFileId, rowDataSeparator, resultParamList, orderNoParamList, Collections.emptyList(), scriptResultList, script, paragraphArray, resultList);

                if (resultList.size() > 0) {
                    for (int i = 0; i < resultList.size(); i++) {
                        resultList.get(i).setId(ApplicationContextHelper.getNextIdentity());
                        resultList.get(i).setFileName(equiptAcqFile.getSourceFileName());
                    }
                    // 插入样品分析结果表
                    allResultList.addAll(resultList);
                    equiptAcqFile.setStatus(EmcConstant.EQUIPT_ACQ_FILE_STATUS_DONE);
                    equiptAcqFile.setResult(allResultContent.toString());
                    updateAcqFileList.add(equiptAcqFile);
                }
            }

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

            List<EmcOrderTaskResultBean> allOrderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                    .match(new String[] { "ORDERNO", "EQUIPTACQUNO" }, orderNoList).filter(MatchPattern.OR)
                    .match("RUNID", validRunIdList).filter(MatchPattern.SEQ));

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
                        matchAcqResultList.forEach(a -> {

                            // 匹配到结果表的分析项
                            List<EmcOrderTaskResultBean> updateOrderTaskResultList = allOrderTaskResultList.stream()
                                    .filter(o -> ObjectUtils.equals(o.getItemId(), e.getItemId()) && ObjectUtils.equals(o.getTestMethodId(), e.getTestMethodId())
                                            && (ObjectUtils.equals(o.getExt$Item("equiptacquno"), a.getBizNo()) || (StringUtils.isEmpty(o.getExt$Item("equiptacquno")) && ObjectUtils.equals(o.getOrderNo(), a.getBizNo()))))
                                    .collect(Collectors.toList());

                            if (!updateOrderTaskResultList.isEmpty()) {
                                updateOrderTaskResultList.forEach(o -> o.setOriginalResult(a.getResult()));
                            }

                            allUpdateOrderTaskResultList.addAll(updateOrderTaskResultList);
                        });
                    }
                });
            }
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
            acqFileService.getDao().update(updateAcqFileList, "status", "result");
        }

        // 将文件信息添加到样品测试表下
        List<CoreFileBean> insertCoreFileList = new ArrayList<>();
        List<Long> orderTaskIdList = allUpdateOrderTaskResultList.stream().map(e -> e.getOrderTaskId()).distinct().collect(Collectors.toList());

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

        coreFileService.getDao().insert(insertCoreFileList);
        orderTaskResultService.getDao().update(allUpdateOrderTaskResultList, "originalResult");

        // 触发计算公式
        List<Long> orderTaskResultIdList = allUpdateOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
        if (!orderTaskResultIdList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(orderTaskResultIdList).compute();
        }
    }

    @Override
    @Transactional
    public void insertSceneResultByAnalyAcqFiles(List<EmcEquiptAcqFileBean> acqFileList) {
    }

    @Override
    public List<EmcEquiptAcqResultBean> getSceneResultList(List<String> contentList, Long scriptId) {
        return null;
    }

    @Override
    public String analyParagraphArray(Long acqFileId, String rowDataSeparator, List<EmcEasParamBean> resultParamList, List<EmcEasParamBean> orderNoParamList, List<EmcEasParamBean> testNameParamList, List<EmcEasResultBean> scriptResultList,
            EmcEquiptAcqScriptBean script, String[] paragraphArray, List<EmcEquiptAcqResultBean> resultList) {
        StringBuilder allResultContent = new StringBuilder();
        Long rowNo = 1L; // 行号 从1开始,一个文件出来的行号递增

        String testFormat = script.getTestFormat();
        String orderNoFormat = script.getOrderNoFormat();

        for (String paragraph : paragraphArray) {
            String[] testRowCells = new String[1];
            Boolean testRowFindFlag = false;

            for (EmcEasParamBean resultParam : resultParamList) {

                String beginSymbol = resultParam.getBeginSymbol();
                String endSymbol = resultParam.getEndSymbol();

                if (StringUtils.contains(paragraph, beginSymbol) && StringUtils.contains(paragraph, endSymbol) && paragraph.indexOf(beginSymbol) < paragraph.indexOf(endSymbol)) {

                    String orderNumberBegin = null; // 样品开始标记
                    String orderNumberEnd = null; // 样品结束标记
                    for (int i = orderNoParamList.size() - 1; i >= 0; i--) {
                        EmcEasParamBean orderNoParam = orderNoParamList.get(i);
                        orderNumberBegin = orderNoParam.getBeginSymbol();
                        orderNumberEnd = orderNoParam.getEndSymbol();
                    }

                    // 拿到样品编号
                    String orderNo = null;
                    if (EmcConstant.EQUIPT_ACQ_ORDER_NO_FORMAT_SINGLE.equals(orderNoFormat) && StringUtils.contains(paragraph, orderNumberBegin) && StringUtils.contains(paragraph, orderNumberEnd)) {
                        // 样品编号
                        orderNo = paragraph.substring(paragraph.indexOf(orderNumberBegin) + orderNumberBegin.length(), paragraph.indexOf(orderNumberEnd));
                        // 去除空格
                        orderNo = StringUtils.trim(orderNo);
                        orderNo = orderNo.replaceAll("/r/n", "");
                    }

                    String testNameBegin = null; // 样品开始标记
                    String testNameEnd = null; // 样品结束标记
                    String testName = null;
                    for (int i = testNameParamList.size() - 1; i >= 0; i--) {
                        EmcEasParamBean testNameParam = testNameParamList.get(i);
                        testNameBegin = testNameParam.getBeginSymbol();
                        testNameEnd = testNameParam.getEndSymbol();
                        if (!ObjectUtils.isEmpty(testNameEnd) && !ObjectUtils.isEmpty(testNameBegin) && StringUtils.contains(paragraph, testNameBegin) && StringUtils.contains(paragraph, testNameEnd)) {
                            // 样品编号
                            testName = paragraph.substring(paragraph.indexOf(testNameBegin) + testNameBegin.length(), paragraph.indexOf(testNameEnd));
                            // 去除空格
                            testName = StringUtils.trim(testName);
                            testName = testName.replaceAll("/r/n", "");
                        }
                    }

                    String resultContent = paragraph.substring(paragraph.indexOf(beginSymbol), paragraph.indexOf(endSymbol));
                    allResultContent.append(resultContent);

                    BufferedReader reader = new BufferedReader(new StringReader(resultContent));

                    String resultRow = null;
                    try {
                        while ((resultRow = reader.readLine()) != null) {
                            if (StringUtils.isEmpty(resultRow)) {
                                continue;
                            }
                            boolean thisRowEitherUse = false;
                            String[] resultCell = resultRow.split(rowDataSeparator);

                            // 特殊处理特殊文件（根据分析项名称才找到对应的结果列）
                            List<EmcEasResultBean> dynamicCreatingScriptResultList = scriptResultList.stream().filter(e -> e.getDynamicCreating() != null).collect(Collectors.toList());

                            if (!dynamicCreatingScriptResultList.isEmpty() && !testRowFindFlag) {
                                testRowCells = resultCell;
                            }

                            for (EmcEasResultBean scriptResultBean : scriptResultList) {

                                Long resultColumnNo = scriptResultBean.getResultColumnNo();
                                Long itemColumnNo = scriptResultBean.getTestItemColumnNo();
                                Long orderColumnNo = scriptResultBean.getShortNoColumnNo();

                                // 特殊处理特殊文件（根据分析项名称才找到对应的结果列）
                                String dynamicCreating = scriptResultBean.getDynamicCreating();

                                if (dynamicCreating != null) {
                                    int delimiterIndex = dynamicCreating.lastIndexOf(",");
                                    String itemName = dynamicCreating.substring(0, delimiterIndex);
                                    String indexScript = dynamicCreating.substring(delimiterIndex + 1);
                                    Boolean findFlag = false;

                                    for (int i = 0; i < testRowCells.length; i++) {
                                        if (testRowCells[i] != null && testRowCells[i].contains(itemName)) {

                                            Double ResultIndex = ArithUtils.eval(StringUtils.replace(indexScript, "X", i + ""));
                                            testRowFindFlag = true;
                                            findFlag = true;

                                            if (findFlag) {
                                                resultColumnNo = ResultIndex.longValue();
                                                break;
                                            }
                                        }
                                    }

                                    if (!findFlag) {
                                        continue;
                                    }
                                }

                                if (orderNo != null) {// 样品编号已截取

                                    if (resultColumnNo != null && itemColumnNo != null && resultColumnNo < resultCell.length && itemColumnNo < resultCell.length) {
                                        String result = resultCell[NumberUtils.parseInt(resultColumnNo)];
                                        if (NumberUtils.isNumber(result)) {
                                            EmcEquiptAcqResultBean acqResultBean = new EmcEquiptAcqResultBean();
                                            acqResultBean.setResult(result);
                                            if (Constant.YES.equals(testFormat)) {
                                                acqResultBean.setItemName(Optional.ofNullable(testName).orElse(scriptResultBean.getFieldName()));
                                            } else {
                                                acqResultBean.setItemName(Optional.ofNullable(testName).orElse(resultCell[NumberUtils.parseInt(itemColumnNo)]));
                                            }
                                            acqResultBean.setFieldName(scriptResultBean.getFieldName());
                                            thisRowEitherUse = true;
                                            acqResultBean.setRowNo(rowNo);
                                            acqResultBean.setBizNo(orderNo);
                                            acqResultBean.setEquiptAcqFileId(acqFileId);
                                            resultList.add(acqResultBean);
                                        }
                                    }
                                } else { // 样品编号在表格列上

                                    if (resultColumnNo != null && itemColumnNo != null && orderColumnNo != null && resultColumnNo < resultCell.length && itemColumnNo < resultCell.length
                                            && orderColumnNo < resultCell.length) {
                                        String result = resultCell[NumberUtils.parseInt(resultColumnNo)];
                                        if (NumberUtils.isNumber(result)) {
                                            EmcEquiptAcqResultBean acqResultBean = new EmcEquiptAcqResultBean();
                                            acqResultBean.setResult(result);
                                            if (Constant.YES.equals(testFormat)) {
                                                acqResultBean.setItemName(Optional.ofNullable(testName).orElse(scriptResultBean.getFieldName()));
                                            } else {
                                                acqResultBean.setItemName(Optional.ofNullable(testName).orElse(resultCell[NumberUtils.parseInt(itemColumnNo)]));
                                            }
                                            acqResultBean.setFieldName(scriptResultBean.getFieldName());
                                            thisRowEitherUse = true;
                                            acqResultBean.setRowNo(rowNo);
                                            acqResultBean.setBizNo(resultCell[NumberUtils.parseInt(orderColumnNo)]);
                                            acqResultBean.setEquiptAcqFileId(acqFileId);
                                            resultList.add(acqResultBean);
                                        }
                                    }
                                }
                            }
                            if (thisRowEitherUse == true) {
                                ++rowNo;
                            }
                        }
                    } catch (IOException e) {
                        throw new UnexpectedException(e);
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                /* ignore */
                            }
                        }
                    }
                }

            }
        }
        return allResultContent.toString();
    }

    /**
     * 预处理 删除delete 删除行deleteRow 替换replace
     *
     * @param content
     *            被操作的内容
     * @param scriptPreAnalyList
     *            预处理操作
     * @return 删除指定内容行后的结果
     */
    @Override
    public String preAnalyContent(String content, List<EmcEasPreAnalyBean> scriptPreAnalyList) {
        scriptPreAnalyList.stream().filter(pre -> ObjectUtils.isEmpty(pre.getSortNum())).forEach(pre -> pre.setSortNum(1));
        scriptPreAnalyList.sort(Comparator.comparing(EmcEasPreAnalyBean::getSortNum));
        for (EmcEasPreAnalyBean preAnaly : scriptPreAnalyList) {
            String fieldKeySymbol = preAnaly.getFieldSymbol();

            if (!StringUtils.isEmpty(fieldKeySymbol)) {
                String preAnalyAction = preAnaly.getPreAnalyCategory();

                if (EmcConstant.EQUIPT_ACQ_PRE_ANALY_DELETE.equals(preAnalyAction)) {
                    content = StringUtils.replace(content, fieldKeySymbol, "");
                } else if (EmcConstant.EQUIPT_ACQ_PRE_ANALY_DELETE_LINE.equals(preAnalyAction)) {
                    if (content.contains(fieldKeySymbol)) {
                        content = deleteRow(content, fieldKeySymbol);
                    }
                } else if (EmcConstant.EQUIPT_ACQ_PRE_ANALY_REPLACE.equals(preAnalyAction)) {
                    String fieldReplaceSymbol = preAnaly.getFieldReplaceSymbol();
                    content = StringUtils.replace(content, fieldKeySymbol, Optional.ofNullable(fieldReplaceSymbol).orElse(""));
                } else if (EmcConstant.EQUIPT_ACQ_PRE_ANALY_REPLACE_TO_SPACE.equals(preAnalyAction)) {
                    content = StringUtils.replace(content, fieldKeySymbol, " ");
                } else if (EmcConstant.EQUIPT_ACQ_PRE_ANALY_REGULAR_REPLACE.equals(preAnalyAction)) {
                    String fieldReplaceSymbol = preAnaly.getFieldReplaceSymbol();
                    content = content.replaceAll(fieldKeySymbol, Optional.ofNullable(fieldReplaceSymbol).orElse(""));
                } else if (EmcConstant.EQUIPT_ACQ_PRE_ANALY_REGULAR_REPLACE_TO_SPACE.equals(preAnalyAction)) {
                    content = content.replaceAll(fieldKeySymbol, " ");
                }
            }
        }
        return content;
    }

    @Override
    public String analyParagraphArray(Long acqFileId, String rowDataSeparator, List<EmcEasParamBean> resultParamList, List<EmcEasParamBean> orderNoParamList, List<EmcEasResultBean> scriptResultList, EmcEquiptAcqScriptBean script, String[] paragraphArray, List<EmcEquiptAcqResultBean> resultList) {
        StringBuilder allResultContent = new StringBuilder();
        Long rowNo = 1L; // 行号 从1开始,一个文件出来的行号递增

        String testFormat = script.getTestFormat();
        String orderNoFormat = script.getOrderNoFormat();

        for (String paragraph : paragraphArray) {
            String[] testRowCells = new String[1];
            Boolean testRowFindFlag = false;

            for (EmcEasParamBean resultParam : resultParamList) {

                String beginSymbol = resultParam.getBeginSymbol();
                String endSymbol = resultParam.getEndSymbol();

                if (StringUtils.contains(paragraph, beginSymbol) && StringUtils.contains(paragraph, endSymbol) && paragraph.indexOf(beginSymbol) < paragraph.indexOf(endSymbol)) {

                    String orderNumberBegin = null; // 样品开始标记
                    String orderNumberEnd = null; // 样品结束标记
                    for (int i = orderNoParamList.size() - 1; i >= 0; i--) {
                        EmcEasParamBean orderNoParam = orderNoParamList.get(i);
                        orderNumberBegin = orderNoParam.getBeginSymbol();
                        orderNumberEnd = orderNoParam.getEndSymbol();
                    }

                    // 拿到样品编号
                    String orderNo = null;
                    if (EmcConstant.EQUIPT_ACQ_ORDER_NO_FORMAT_SINGLE.equals(orderNoFormat) && StringUtils.contains(paragraph, orderNumberBegin) && StringUtils.contains(paragraph, orderNumberEnd)) {
                        // 样品编号
                        orderNo = paragraph.substring(paragraph.indexOf(orderNumberBegin) + orderNumberBegin.length(), paragraph.indexOf(orderNumberEnd));
                        // 去除空格
                        orderNo = StringUtils.trim(orderNo);
                        orderNo = orderNo.replaceAll("/r/n", "");
                    }

                    String resultContent = paragraph.substring(paragraph.indexOf(beginSymbol), paragraph.indexOf(endSymbol));
                    allResultContent.append(resultContent);

                    BufferedReader reader = new BufferedReader(new StringReader(resultContent));

                    String resultRow = null;
                    try {
                        while ((resultRow = reader.readLine()) != null) {
                            if (StringUtils.isEmpty(resultRow)) {
                                continue;
                            }
                            boolean thisRowEitherUse = false;
                            String[] resultCell = resultRow.split(rowDataSeparator);

                            // 特殊处理特殊文件（根据分析项名称才找到对应的结果列）
                            List<EmcEasResultBean> dynamicCreatingScriptResultList = scriptResultList.stream().filter(e -> e.getDynamicCreating() != null).collect(Collectors.toList());

                            if (!dynamicCreatingScriptResultList.isEmpty() && !testRowFindFlag) {
                                testRowCells = resultCell;
                            }

                            for (EmcEasResultBean scriptResultBean : scriptResultList) {

                                Long resultColumnNo = scriptResultBean.getResultColumnNo();
                                Long itemColumnNo = scriptResultBean.getTestItemColumnNo();
                                Long orderColumnNo = scriptResultBean.getShortNoColumnNo();

                                // 特殊处理特殊文件（根据分析项名称才找到对应的结果列）
                                String dynamicCreating = scriptResultBean.getDynamicCreating();

                                if (dynamicCreating != null) {
                                    int delimiterIndex = dynamicCreating.lastIndexOf(",");
                                    String itemName = dynamicCreating.substring(0, delimiterIndex);
                                    String indexScript = dynamicCreating.substring(delimiterIndex + 1);
                                    Boolean findFlag = false;

                                    for (int i = 0; i < testRowCells.length; i++) {
                                        if (testRowCells[i] != null && testRowCells[i].contains(itemName)) {

                                            Double ResultIndex = ArithUtils.eval(StringUtils.replace(indexScript, "X", i + ""));
                                            testRowFindFlag = true;
                                            findFlag = true;

                                            if (findFlag) {
                                                resultColumnNo = ResultIndex.longValue();
                                                break;
                                            }
                                        }
                                    }

                                    if (!findFlag) {
                                        continue;
                                    }
                                }

                                if (orderNo != null) {// 样品编号已截取

                                    if (resultColumnNo != null && itemColumnNo != null && resultColumnNo < resultCell.length && itemColumnNo < resultCell.length) {
                                        String result = resultCell[NumberUtils.parseInt(resultColumnNo)];
                                        if (NumberUtils.isNumber(result)) {
                                            EmcEquiptAcqResultBean acqResultBean = new EmcEquiptAcqResultBean();
                                            acqResultBean.setResult(result);
                                            if (Constant.YES.equals(testFormat)) {
                                                acqResultBean.setItemName(scriptResultBean.getFieldName());
                                            } else {
                                                acqResultBean.setItemName(resultCell[NumberUtils.parseInt(itemColumnNo)]);
                                            }
                                            acqResultBean.setFieldName(scriptResultBean.getFieldName());
                                            thisRowEitherUse = true;
                                            acqResultBean.setRowNo(rowNo);
                                            acqResultBean.setBizNo(orderNo);
                                            acqResultBean.setEquiptAcqFileId(acqFileId);
                                            resultList.add(acqResultBean);
                                        }
                                    }
                                } else { // 样品编号在表格列上

                                    if (resultColumnNo != null && itemColumnNo != null && orderColumnNo != null && resultColumnNo < resultCell.length && itemColumnNo < resultCell.length
                                            && orderColumnNo < resultCell.length) {
                                        String result = resultCell[NumberUtils.parseInt(resultColumnNo)];
                                        if (NumberUtils.isNumber(result)) {
                                            EmcEquiptAcqResultBean acqResultBean = new EmcEquiptAcqResultBean();
                                            acqResultBean.setResult(result);
                                            if (Constant.YES.equals(testFormat)) {
                                                acqResultBean.setItemName(scriptResultBean.getFieldName());
                                            } else {
                                                acqResultBean.setItemName(resultCell[NumberUtils.parseInt(itemColumnNo)]);
                                            }
                                            acqResultBean.setFieldName(scriptResultBean.getFieldName());
                                            thisRowEitherUse = true;
                                            acqResultBean.setRowNo(rowNo);
                                            acqResultBean.setBizNo(resultCell[NumberUtils.parseInt(orderColumnNo)]);
                                            acqResultBean.setEquiptAcqFileId(acqFileId);
                                            resultList.add(acqResultBean);
                                        }
                                    }
                                }
                            }
                            if (thisRowEitherUse == true) {
                                ++rowNo;
                            }
                        }
                    } catch (IOException e) {
                        throw new UnexpectedException(e);
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                /* ignore */
                            }
                        }
                    }
                }

            }
        }
        return allResultContent.toString();
    }

    /**
     * 删除还有关键字的行
     *
     * @param content
     *            被操作的内容
     * @param key
     *            要删除行含有的关键字符串
     * @return 删除指定内容行后的结果
     */
    private static String deleteRow(String content, String key) {
        String[] splits = content.split("(\\r|\\n)");

        StringBuilder sb = new StringBuilder();
        for (String value : splits) {
            if (!value.contains(key)) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }

                sb.append(value);
            }
        }

        return sb.toString();
    }

}
