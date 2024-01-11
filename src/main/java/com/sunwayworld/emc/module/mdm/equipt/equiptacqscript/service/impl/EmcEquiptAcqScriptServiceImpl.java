package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.utils.EmcFileUtils;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.bean.EmcEquiptAcqParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.EmcEquiptAcqParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service.EmcEquiptAcqResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEquiptAcqScriptDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasPreAnalyService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

@Repository
@GikamBean
public class EmcEquiptAcqScriptServiceImpl implements EmcEquiptAcqScriptService {

    @Autowired
    private EmcEquiptAcqScriptDao emcEquiptAcqScriptDao;
    @Autowired
    private CoreFileService coreFileService;
    @Autowired
    private EmcEasParamService easParamService;
    @Autowired
    private EmcEasPreAnalyService easPreAnalyService;
    @Autowired
    private EmcEasResultService easResultService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptAcqScriptDao getDao() {
        return emcEquiptAcqScriptDao;
    }

    /**
     * 新增
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {

        EmcEquiptAcqScriptBean equiptAcqScriptBean = new EmcEquiptAcqScriptBean();
        equiptAcqScriptBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(equiptAcqScriptBean);
        return equiptAcqScriptBean.getId();

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertParam(Long id, RestJsonWrapperBean wrapper) {
        easParamService.insert(id, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteParam(Long id, RestJsonWrapperBean wrapper) {
        easParamService.delete(wrapper);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertPreAnaly(Long id) {
        return easPreAnalyService.insert(id);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deletePreAnaly(Long id, RestJsonWrapperBean wrapper) {
        easPreAnalyService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertResult(Long id) {
        return easResultService.insert(id);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteResult(Long id, RestJsonWrapperBean wrapper) {
        easResultService.delete(wrapper);
    }

    @Override
    public Map<String, String> extractTextFromFileForTest(Long id, Long fileId) {
        // 将文件的文本内容解析出来
        String result = extractTextByFileId(fileId, selectDetail(id).getAnalysisWay());

        List<String> keyStringList = new ArrayList<String>();
        List<EmcEasPreAnalyBean> scriptPreAnalyList = easPreAnalyService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        for (EmcEasPreAnalyBean preAnalyBean : scriptPreAnalyList) {
            String fieldSymbol = preAnalyBean.getFieldSymbol();// 关键字
            String replaceSymbol = preAnalyBean.getFieldReplaceSymbol();
            if (!StringUtils.isEmpty(fieldSymbol) && !keyStringList.contains(fieldSymbol)) {
                keyStringList.add(fieldSymbol);
            }
            if (!StringUtils.isEmpty(replaceSymbol) && !keyStringList.contains(replaceSymbol)) {
                keyStringList.add(replaceSymbol);
            }
        }
        List<EmcEasParamBean> scriptParamList = easParamService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        for (EmcEasParamBean paramBean : scriptParamList) {
            String beginSymbol = paramBean.getBeginSymbol();
            String endSymbol = paramBean.getEndSymbol();
            if (!StringUtils.isEmpty(beginSymbol) && !keyStringList.contains(beginSymbol)) {
                keyStringList.add(beginSymbol);
            }
            if (!StringUtils.isEmpty(endSymbol) && !keyStringList.contains(endSymbol)) {
                keyStringList.add(endSymbol);
            }
        }
        String view = result.replaceAll("\\r{0,}\\n", "<br/>");
        // 添加默认开始和结束标志
        view = "defaultStartFlag<br/>" + view + "<br/>defaultEndFlag";
        keyStringList.add("defaultStartFlag");
        keyStringList.add("defaultEndFlag");
        for (String keyString : keyStringList) {
            keyString = keyString.replaceAll("\\r{0,}\\n", "<br/>");
            view = view.replace(keyString, "<font color='red'>" + keyString + "</font>");
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("result", result);// 原始内容
        map.put("view", view);// 去空格，关键字变红
        return map;

    }

    @Override
    public Map<String, String> preAnalyTest(Long id, RestJsonWrapperBean wrapper) {
        EmcEquiptAcqFileBean fileBean = wrapper.parseUnique(EmcEquiptAcqFileBean.class);
        String result = fileBean.getResult();
        List<String> keyStringList = new ArrayList<String>();
        List<EmcEasPreAnalyBean> scriptPreAnalyList = easPreAnalyService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        Map<String, String> map = new HashMap<String, String>();

        // 处理数据
        result = ApplicationContextHelper.getBean(EmcEquiptAcqResultService.class).preAnalyContent(result, scriptPreAnalyList);
        List<EmcEasParamBean> scriptParamList = easParamService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        for (EmcEasParamBean paramBean : scriptParamList) {
            String beginSymbol = paramBean.getBeginSymbol();
            String endSymbol = paramBean.getEndSymbol();
            if (!StringUtils.isEmpty(beginSymbol) && !keyStringList.contains(beginSymbol)) {
                keyStringList.add(beginSymbol);
            }
            if (!StringUtils.isEmpty(endSymbol) && !keyStringList.contains(endSymbol)) {
                keyStringList.add(endSymbol);
            }
        }
        String view = result.replaceAll("\\r{0,}\\n", "<br/>");
        // 添加默认开始和结束标志
        view = "defaultStartFlag<br/>" + view + "<br/>defaultEndFlag";
        keyStringList.add("defaultStartFlag");
        keyStringList.add("defaultEndFlag");

        Pattern p = Pattern.compile("\\s+");

        for (String keyString : keyStringList) {
            keyString = keyString.replaceAll("\\r{0,}\\n", "<br/>");
            if (view.contains(keyString)) {
                view = view.replace(keyString, "<font color='red'>" + keyString + "</font>");
            } else {
                // 文档可能会有多个空格不容易看出来，在此特殊处理下，把多个空格变成一个空格
                Matcher m = p.matcher(view);
                view = m.replaceAll(" ").replace(keyString, "<font color='red'>" + keyString + "</font>");
            }
        }
        map.put("view", view);
        return map;
    }

    // 将文件的文本内容解析出来
    private String extractTextByFileId(Long fileId, String analysisWay) {
        CoreFileBean coreFile = coreFileService.selectDetail(fileId);
        File file = FileHelper.getAbsolutePath(FileRepresentation.of(coreFile.getScope(), DateTimeUtils.getDateSerialNumber(coreFile.getCreatedTime()), coreFile.getMd5Name(),
                coreFile.getName().substring(0, coreFile.getName().lastIndexOf(coreFile.getFileExt()) - 1) + "." + coreFile.getFileExt())).toFile();

        String result = null;
        String fileExtension = FileUtils.getFileExtension(file.getName()).toLowerCase();

        if (EmcConstant.ANALYSIS_WAY_PRIOR_DATA.equals(analysisWay) && EmcConstant.FILECATAEGORY_PDF.equals(fileExtension)) {

            result = EmcFileUtils.extractTextFromPDF(file);
        } else if (EmcConstant.FILECATAEGORY_XLSX.equals(fileExtension) || EmcConstant.FILECATAEGORY_XLS.equals(fileExtension)) {
            result = EmcFileUtils.extractTextFromXlsx(file);
        } else {
            result = FileUtils.extractTextFromFile(file);
        }
        return result;
    }

    @Override
    public Page<EmcEquiptAcqResultBean> analyResultTest(Long id, Long fileId) {
        EmcEquiptAcqParamService equiptAcqParamService = ApplicationContextHelper.getBean(EmcEquiptAcqParamService.class);
        EmcEquiptAcqResultService equiptAcqResultService = ApplicationContextHelper.getBean(EmcEquiptAcqResultService.class);

        // 原始文本
        EmcEquiptAcqScriptBean script = selectDetail(id);
        String result = extractTextByFileId(fileId, script.getAnalysisWay());

        // 预处理结果
        List<EmcEasPreAnalyBean> scriptPreAnalyList = easPreAnalyService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        String content = result;
        if (scriptPreAnalyList.size() > 0) {
            content = equiptAcqResultService.preAnalyContent(result, scriptPreAnalyList);
        }

        // 解析过程
        List<EmcEasParamBean> scriptParamList = easParamService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        List<EmcEasParamBean> resultParamList = new ArrayList<>();
        List<EmcEasParamBean> orderNoParamList = new ArrayList<>();
        List<EmcEasParamBean> testNameParamList = new ArrayList<>();

        String rowDataSeparator = script.getRowDataSeparator();
        List<EmcEasResultBean> scriptResultList = easResultService.selectListByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ));
        if (scriptResultList.size() == 0 || StringUtils.isEmpty(rowDataSeparator)) {
            return null;
        }

        // 分段参数+结果范围参数+样品范围参数+测试名称范围参数
        String paragraphMark = null; // 分段标记

        List<EmcEquiptAcqParamBean> allEquiptAcqParamList = equiptAcqParamService.selectAll();

        for (int i = scriptParamList.size() - 1; i >= 0; i--) {

            EmcEasParamBean scriptParam = scriptParamList.get(i);
            String field = allEquiptAcqParamList.stream().filter(e -> ObjectUtils.equals(e.getId(), scriptParam.getParamId())).findFirst().orElse(null).getField(); // 判断是什么作用的参数

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
        List<EmcEquiptAcqResultBean> resultList = new ArrayList<EmcEquiptAcqResultBean>();
        equiptAcqResultService.analyParagraphArray(null, rowDataSeparator, resultParamList, orderNoParamList,testNameParamList, scriptResultList, script, paragraphArray, resultList);

        return new Pagination<EmcEquiptAcqResultBean>(resultList);
    }

    @Override
    public RestValidationResultBean validateFieldName(RestJsonWrapperBean wrapper) {
        String validateUniqueFieldJson = wrapper.getParamValue("vu");
        if (null == validateUniqueFieldJson) {
            return new RestValidationResultBean(true);
        }
        Map<String, Object> validateUniqueFieldMap = JSONObject.parseObject(validateUniqueFieldJson, new TypeReference<Map<String, Object>>(){});
        Map<String, Object> columnMap = new CaseInsensitiveLinkedMap<>(validateUniqueFieldMap);
        Long id = (Long) ConvertUtils.convert(columnMap.get("id"), getDao().getEntityContext().getIdContext().getType());
        EmcEasResultBean easResult = easResultService.getDao().selectById(id);
        List<EmcEasResultBean> easResultList = easResultService.getDao().selectListByOneColumnValue(easResult.getScriptId(), "SCRIPTID");
        boolean anyMatch = easResultList.stream().anyMatch(result -> StringUtils.equals(result.getFieldName(), columnMap.get("fieldname").toString()));
        if (!anyMatch) {
            return new RestValidationResultBean(true);
        }

        return new RestValidationResultBean(false, "core.validator.column.not-unique");
    }

    @Override
    public void copyResult(Long id, RestJsonWrapperBean wrapper) {
        List<EmcEasResultBean> resultList = wrapper.parse(EmcEasResultBean.class);
        Collections.reverse(resultList);
        resultList.forEach(result -> {
            result.setId(ApplicationContextHelper.getNextIdentity());
            result.setFieldName(result.getFieldName() + "1");
        });
        easResultService.getDao().insert(resultList);
    }

}
