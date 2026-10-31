package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.computing.formula.ResultFormulaRepository;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStandardSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcStandardSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.formula.annotation.Formula;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
@GikamBean
public class EmcItemServiceImpl implements EmcItemService {

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcItemResultService itemResultService;

    @Autowired
    private CoreFormulaRequiredService formulaRequiredService;

    @Autowired
    private EmcStandardSetService emcStandardSetService;

    @Autowired
    @Lazy
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcItemDao emcItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcItemDao getDao() {
        return emcItemDao;
    }

    @Override
    public void postUpdate(List<String> updatedColumnNameList, List<Long> idList) {
        if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "SCRIPT")) {
            List<EmcItemBean> itemList = getDao().selectListByIds(idList);

            for (EmcItemBean item : itemList) {
                if (!StringUtils.isBlank(item.getScript())) {
                    insertFormulaRequired(item);
                }
            }
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long testMethodId, RestJsonWrapperBean jsonWrapper) {
        EmcItemBean emcItem = jsonWrapper.parseUnique(EmcItemBean.class);
        emcItem.setId(ApplicationContextHelper.getNextIdentity());
        emcItem.setTestMethodId(testMethodId);
        // 重复数--默认为1
        emcItem.setRepeatItem(EmcConstant.ONE);
        EmcStandardSetBean standardSet = emcStandardSetService.selectFirstByFilter(SearchFilter.instance().match("SETNAME", "NUMERIC_NA").filter(MatchPattern.SEQ));
        if (!ObjectUtils.isEmpty(standardSet)) {
            emcItem.setStandSetId(standardSet.getId());
        }
        emcItem.setResultCategory(EmcConstant.EMC_ITEM_RESULT_CATEGORY_NUMBER);
        emcItem.setLimitFlag(EmcConstant.EMC_ITEM_LIMIT_FLAG_LESS_OR_EQUAL_LOW);
        emcItem.setPnFunc(EmcConstant.EMC_ITEM_PNFUNC_SAMPLE);
        emcItem.setOriginalRecordDisplay(Constant.YES);
        emcItem.setReportDisplay(Constant.NO);
        getDao().insert(emcItem);
        return emcItem.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        EmcItemBean emcItem = jsonWrapper.parseUnique(EmcItemBean.class);

        // 复制测试分析项
        EmcItemBean copyEmcItem = getDao().selectById(emcItem.getId());
        copyEmcItem.setId(ApplicationContextHelper.getNextIdentity());
        copyEmcItem.setItemName(emcItem.getItemName());
        copyEmcItem.setItemAliasName(emcItem.getItemAliasName());
        getDao().insert(copyEmcItem);

        // 复制QC指标
        EmcItemQcIndexBean qcIndexFilter = new EmcItemQcIndexBean();
        qcIndexFilter.setItemId(emcItem.getId());
        List<EmcItemQcIndexBean> copyQcIndexList = itemQcIndexService.getDao().selectList(qcIndexFilter);
        copyQcIndexList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setItemId(copyEmcItem.getId());
        });
        itemQcIndexService.getDao().insert(copyQcIndexList);

        // 复制分段修约
        EmcItemRevisionBean revisionFilter = new EmcItemRevisionBean();
        revisionFilter.setItemId(emcItem.getId());
        List<EmcItemRevisionBean> copyRevisionList = itemRevisionService.getDao().selectList(revisionFilter);
        copyRevisionList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setItemId(copyEmcItem.getId());
        });
        itemRevisionService.getDao().insert(copyRevisionList);

        // 复制分析项结果
        EmcItemResultBean resultFilter = new EmcItemResultBean();
        resultFilter.setItemId(emcItem.getId());
        List<EmcItemResultBean> copyResultList = itemResultService.getDao().selectList(resultFilter);
        copyResultList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setItemId(copyEmcItem.getId());
        });
        itemResultService.getDao().insert(copyResultList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyTo(RestJsonWrapperBean jsonWrapper) {
        List<EmcTestMethodBean> testMethodList = jsonWrapper.parse(EmcTestMethodBean.class); // 测试方法
        List<EmcItemBean> testItemList = jsonWrapper.parse(EmcItemBean.class); // 分析项

        List<Long> testItemIdList = testItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());
        List<EmcItemBean> emcItemList = getDao().selectListByIds(testItemIdList);

        if (!emcItemList.isEmpty()) {
            List<EmcItemBean> insertItemList = new ArrayList<>();

            // 迭代分析项
            emcItemList.stream().forEach(e -> {
                // 迭代测试方法
                testMethodList.stream().forEach(v -> {
                    EmcItemBean emcItem = new EmcItemBean();
                    // 不复制制单字段
                    BeanUtils.copyProperties(e, emcItem, EmcConstant.CREATED_COLUMNS);
                    emcItem.setId(ApplicationContextHelper.getNextIdentity());
                    emcItem.setTestMethodId(v.getId());

                    insertItemList.add(emcItem);
                });
            });

            getDao().insert(insertItemList);

            // 添加分析项公式依赖关系
            insertItemList.stream().filter(item -> !StringUtils.isEmpty(item.getScript())).forEach(this::insertFormulaRequired);
        }
    }

    @Override
    public RestValidationResultBean validateItemNameUnique(RestJsonWrapperBean wrapper, Long testMethodId) {
        String json = wrapper.getParamValue("vu");
        JSONObject obj = JSONObject.parseObject(json);
        String itemName = obj.getString("itemName");
        EmcItemBean filter = new EmcItemBean();
        filter.setTestMethodId(testMethodId);
        filter.setItemName(itemName);

        List<EmcItemBean> itemList = getDao().selectList(filter);
        if (!itemList.isEmpty()) {
            return new RestValidationResultBean(false, "core.validator.column.not-unique");
        }
        return new RestValidationResultBean(true);
    }

    @Override
    public Page<EmcItemBean> selectTestItem(Long correlationId, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.put("correlationId", correlationId);
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectTestItem(parameter), rowBounds);
    }

    // -------------------------------------------------------------------------
    // 私有方法
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public void insertFormulaRequired(EmcItemBean item) {
        CoreFormulaRequiredBean deleteFilter = new CoreFormulaRequiredBean();
        deleteFilter.setItemId("" + item.getId());
        formulaRequiredService.getDao().deleteBy(deleteFilter, "ITEMID");

        List<Method> formulaMethodList = ReflectionUtils.getMethodList(ResultFormulaRepository.class, Modifier.PUBLIC + Modifier.STATIC);

        Map<Integer, FormulaMethodData> map = new HashMap<>();
        for (Method m : formulaMethodList) {
            Pattern pattern = Pattern.compile("(=\\s*|\\s+|;\\s*|^\\s*|=\"\"\\+\\(\\s*)" + m.getName() + "\\s*\\(([^()]*(\\([^()]*\\))?[^()]*)++\\)");

            Matcher matcher = pattern.matcher(item.getScript());

            while (matcher.find()) {
                int start = matcher.start();

                String matchValue = item.getScript().substring(start, matcher.end());

                int startPos = matchValue.indexOf("(");
                int endPos = matchValue.lastIndexOf(")");

                String paramValue = matchValue.substring(startPos + 1, endPos);

                if (StringUtils.isEmpty(paramValue)) {
                    continue;
                }

                List<String> paramList = new ArrayList<>();

                int lastComaPos = 0;
                int pos = -1;
                while ((pos = paramValue.indexOf(",", pos + 1)) > 0) {
                    String temp = paramValue.substring(0, pos);

                    int lastStartBracketsPos = temp.lastIndexOf("(");

                    if (lastStartBracketsPos < 0) {
                        paramList.add(paramValue.substring(lastComaPos, pos));
                        lastComaPos = pos + 1;
                    } else {
                        int lastEndBracketsPos = temp.lastIndexOf(")");

                        if (lastEndBracketsPos > lastStartBracketsPos) {
                            paramList.add(paramValue.substring(lastComaPos, pos));
                            lastComaPos = pos + 1;
                        }
                    }
                }

                paramList.add(paramValue.substring(lastComaPos));

                FormulaMethodData data = new FormulaMethodData();
                data.setMethod(m);
                data.setParamList(paramList);

                map.put(start, data);
            }
        }

        List<CoreFormulaRequiredBean> insertFormulaRequiredList = new ArrayList<>();

        map.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey())).filter(e -> ReflectionUtils.getAnnotation(e.getValue().getMethod(), Formula.class) != null).forEachOrdered(e -> {
            Formula formula = ReflectionUtils.getAnnotation(e.getValue().getMethod(), Formula.class);

            // 赋值分析项ID
            if (!StringUtils.isEmpty(formula.itemId())) {
                CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
                required.setRequiredItemId(getFormulaValue(e.getValue(), formula.itemId()));
                setFormulaRequired(required, formula, e.getValue(), item);
                insertFormulaRequiredList.add(required);
            } else if (!StringUtils.isEmpty(formula.itemName())) {
                String itemName = getFormulaValue(e.getValue(), formula.itemName());

                if (!StringUtils.isEmpty(itemName)) {
                    // 查找指定测试ID下的分析项
                    List<EmcItemBean> filteredItemList = new ArrayList<>();
                    if (!StringUtils.isEmpty(formula.testId())) {
                        String testId = getFormulaValue(e.getValue(), formula.testId());
                        if (NumberUtils.isNumber(testId)) {
                            EmcTestMethodBean filterTestMethod = new EmcTestMethodBean();
                            filterTestMethod.setTestId(NumberUtils.parseLong(testId));
                            List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectList(filterTestMethod);
                            if (testMethodList.isEmpty()) {
                                throw new FormulaException(I18nHelper.getMessage("T_EMC_ITEM.SCRIPT.INVALID_ITEMNAME", itemName));
                            }

                            List<EmcItemBean> filterItemList = new ArrayList<>();
                            testMethodList.forEach(t -> {
                                EmcItemBean filterItem = new EmcItemBean();
                                filterItem.setTestMethodId(t.getId());
                                filterItem.setItemName(itemName);
                                filterItemList.add(filterItem);
                            });

                            filteredItemList = getDao().selectList(filterItemList, Arrays.asList("TESTMETHODID", "ITEMNAME"), Arrays.asList("ID"));
                            if (filteredItemList.isEmpty()) {
                                throw new FormulaException(I18nHelper.getMessage("T_EMC_ITEM.SCRIPT.INVALID_ITEMNAME", itemName));
                            }
                        }
                    }

                    if (!filteredItemList.isEmpty()) {
                        // 若一个测试下对应多个测试方法，在保存分析项依赖关系时，需要将多个测试方法下的分析项依赖关系都添加上
                        filteredItemList.forEach(i -> {
                            CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
                            required.setRequiredItemId(i.getId() + "");
                            setFormulaRequired(required, formula, e.getValue(), item);
                            insertFormulaRequiredList.add(required);
                        });
                    } else {
                        EmcItemBean filter = new EmcItemBean();
                        filter.setTestMethodId(item.getTestMethodId());
                        filter.setItemName(itemName);
                        EmcItemBean filteredItem = getDao().selectOneIfPresent(filter);

                        if (filteredItem != null) {
                            CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
                            required.setRequiredItemId("" + filteredItem.getId());
                            setFormulaRequired(required, formula, e.getValue(), item);
                            insertFormulaRequiredList.add(required);
                        } else {
                            throw new FormulaException(I18nHelper.getMessage("T_EMC_ITEM.SCRIPT.INVALID_ITEMNAME", itemName));
                        }
                    }
                }
            }
        });

        formulaRequiredService.insert(insertFormulaRequiredList);
    }

    @Override
    @Transactional
    public void bacthInsertFormulaRequired(List<EmcItemBean> itemList) {
        List<CoreFormulaRequiredBean> insertFormulaRequiredList = new ArrayList<>();

        itemList.forEach(item ->{
            CoreFormulaRequiredBean deleteFilter = new CoreFormulaRequiredBean();
            deleteFilter.setItemId("" + item.getId());
            formulaRequiredService.getDao().deleteBy(deleteFilter, "ITEMID");

            List<Method> formulaMethodList = ReflectionUtils.getMethodList(ResultFormulaRepository.class, Modifier.PUBLIC + Modifier.STATIC);

            Map<Integer, FormulaMethodData> map = new HashMap<>();
            for (Method m : formulaMethodList) {
                Pattern pattern = Pattern.compile("(=\\s*|\\s+|;\\s*|^\\s*|=\"\"\\+\\(\\s*)" + m.getName() + "\\s*\\(([^()]*(\\([^()]*\\))?[^()]*)++\\)");

                Matcher matcher = pattern.matcher(item.getScript());

                while (matcher.find()) {
                    int start = matcher.start();

                    String matchValue = item.getScript().substring(start, matcher.end());

                    int startPos = matchValue.indexOf("(");
                    int endPos = matchValue.lastIndexOf(")");

                    String paramValue = matchValue.substring(startPos + 1, endPos);

                    if (StringUtils.isEmpty(paramValue)) {
                        continue;
                    }

                    List<String> paramList = new ArrayList<>();

                    int lastComaPos = 0;
                    int pos = -1;
                    while ((pos = paramValue.indexOf(",", pos + 1)) > 0) {
                        String temp = paramValue.substring(0, pos);

                        int lastStartBracketsPos = temp.lastIndexOf("(");

                        if (lastStartBracketsPos < 0) {
                            paramList.add(paramValue.substring(lastComaPos, pos));
                            lastComaPos = pos + 1;
                        } else {
                            int lastEndBracketsPos = temp.lastIndexOf(")");

                            if (lastEndBracketsPos > lastStartBracketsPos) {
                                paramList.add(paramValue.substring(lastComaPos, pos));
                                lastComaPos = pos + 1;
                            }
                        }
                    }

                    paramList.add(paramValue.substring(lastComaPos));

                    FormulaMethodData data = new FormulaMethodData();
                    data.setMethod(m);
                    data.setParamList(paramList);

                    map.put(start, data);
                }
            }

            map.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey())).filter(e -> ReflectionUtils.getAnnotation(e.getValue().getMethod(), Formula.class) != null).forEachOrdered(e -> {
                Formula formula = ReflectionUtils.getAnnotation(e.getValue().getMethod(), Formula.class);

                // 赋值分析项ID
                if (!StringUtils.isEmpty(formula.itemId())) {
                    CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
                    required.setRequiredItemId(getFormulaValue(e.getValue(), formula.itemId()));
                    setFormulaRequired(required, formula, e.getValue(), item);
                    insertFormulaRequiredList.add(required);
                } else if (!StringUtils.isEmpty(formula.itemName())) {
                    String itemName = getFormulaValue(e.getValue(), formula.itemName());

                    if (!StringUtils.isEmpty(itemName)) {
                        // 查找指定测试ID下的分析项
                        List<EmcItemBean> filteredItemList = new ArrayList<>();
                        if (!StringUtils.isEmpty(formula.testId())) {
                            String testId = getFormulaValue(e.getValue(), formula.testId());
                            if (NumberUtils.isNumber(testId)) {
                                EmcTestMethodBean filterTestMethod = new EmcTestMethodBean();
                                filterTestMethod.setTestId(NumberUtils.parseLong(testId));
                                List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectList(filterTestMethod);
                                if (testMethodList.isEmpty()) {
                                    throw new FormulaException(I18nHelper.getMessage("T_EMC_ITEM.SCRIPT.INVALID_ITEMNAME", itemName));
                                }

                                List<EmcItemBean> filterItemList = new ArrayList<>();
                                testMethodList.forEach(t -> {
                                    EmcItemBean filterItem = new EmcItemBean();
                                    filterItem.setTestMethodId(t.getId());
                                    filterItem.setItemName(itemName);
                                    filterItemList.add(filterItem);
                                });

                                filteredItemList = getDao().selectList(filterItemList, Arrays.asList("TESTMETHODID", "ITEMNAME"), Arrays.asList("ID"));
                                if (filteredItemList.isEmpty()) {
                                    throw new FormulaException(I18nHelper.getMessage("T_EMC_ITEM.SCRIPT.INVALID_ITEMNAME", itemName));
                                }
                            }
                        }

                        if (!filteredItemList.isEmpty()) {
                            // 若一个测试下对应多个测试方法，在保存分析项依赖关系时，需要将多个测试方法下的分析项依赖关系都添加上
                            filteredItemList.forEach(i -> {
                                CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
                                required.setRequiredItemId(i.getId() + "");
                                setFormulaRequired(required, formula, e.getValue(), item);
                                insertFormulaRequiredList.add(required);
                            });
                        } else {
                            EmcItemBean filter = new EmcItemBean();
                            filter.setTestMethodId(item.getTestMethodId());
                            filter.setItemName(itemName);
                            EmcItemBean filteredItem = getDao().selectOneIfPresent(filter);

                            if (filteredItem != null) {
                                CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
                                required.setRequiredItemId("" + filteredItem.getId());
                                setFormulaRequired(required, formula, e.getValue(), item);
                                insertFormulaRequiredList.add(required);
                            } else {
                                throw new FormulaException(I18nHelper.getMessage("T_EMC_ITEM.SCRIPT.INVALID_ITEMNAME", itemName));
                            }
                        }
                    }
                }
            });
        });

        formulaRequiredService.insert(insertFormulaRequiredList);
    }

    private String getFormulaValue(FormulaMethodData data, String expression) {
        if ("cur".equals(expression)) {
            return "cur";
        } else {
            int index = NumberUtils.parseInt(StringUtils.substringsBetween(expression, "[", "]").get(0));

            String paramValue = data.getParamList().size() < (index + 1) ? "" : data.getParamList().get(index);

            if (StringUtils.isEmpty(paramValue)) {
                if (StringUtils.endsWith(expression, "cur")) {
                    return "cur";
                } else {
                    return "";
                }
            } else {
                return paramValue;
            }
        }
    }

    private void setFormulaRequired(CoreFormulaRequiredBean required, Formula formula, FormulaMethodData data, EmcItemBean item) {
        // 赋值测试ID
        if (!StringUtils.isEmpty(formula.testId())) {
            required.setRequiredTestId(getFormulaValue(data, formula.testId()));
        }

        // 赋值样品编号
        if (!StringUtils.isEmpty(formula.orderId())) {
            required.setRequiredOrderId(getFormulaValue(data, formula.orderId()));
        }

        // 赋值重复数（getRR方法需要）
        if (!StringUtils.isEmpty(formula.repeatNo())) {
            required.setRequiredRepeatNo(getFormulaValue(data, formula.repeatNo()));
        }

        // 赋值是否关联点位下的数据
        if (formula.folder()) {
            required.setRequiredFolder(Constant.YES);
        } else {
            required.setRequiredFolder(Constant.NO);
        }

        // 赋值ID
        required.setId(ApplicationContextHelper.getNextIdentity());
        required.setItemId("" + item.getId());
    }

    // -------------------------------------------------------------------------
    // 私有类
    // -------------------------------------------------------------------------
    private static class FormulaMethodData {
        private Method method;
        private List<String> paramList;

        private FormulaMethodData() {
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public List<String> getParamList() {
            return paramList;
        }

        public void setParamList(List<String> paramList) {
            this.paramList = paramList;
        }
    }
}
