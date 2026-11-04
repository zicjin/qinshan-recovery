/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ScriptUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

public class CoreBpmnHelper {
    private static final Logger logger = LogManager.getLogger(CoreBpmnHelper.class);
    public static final String PROCESS_STATUS_RUNNING_FLAG = "RUN$";
    private static GlobalMapper globalMapper;

    public static final <ID> String getTargetId(String table, ID id) {
        return table + "$" + ObjectUtils.toString(id);
    }

    public static final String getCorrectedDiagram(String diagram) {
        return StringUtils.replace(diagram, "camunda:=\"\"", "");
    }

    public static boolean evalSequenceFlowConditionExpression(Element sequenceFlowElement, Map<String, Object> vars) {
        String expression = BpmnDiagramHelper.getConditionExpression(sequenceFlowElement);
        return CoreBpmnHelper.evalExpression(expression, vars);
    }

    public static boolean evalExpression(String expression, Map<String, Object> vars) {
        if (StringUtils.isEmpty(expression)) {
            return true;
        }
        String copyExpression = expression;
        List<String> substringList = StringUtils.substringsBetween(expression, "${", "}");
        for (String substring : substringList) {
            if (StringUtils.equalsIgnoreCase(substring, "loginUserId")) {
                copyExpression = StringUtils.replace(copyExpression, "${" + substring + "}", "'" + LocalContextHelper.getLoginUserId() + "'");
                continue;
            }
            if (CollectionUtils.containsKeyIgnorecase(vars, substring)) {
                Object value = CollectionUtils.getValueIgnorecase(vars, substring);
                if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                    copyExpression = StringUtils.replace(copyExpression, "${" + substring + "}", ObjectUtils.toString(value));
                    continue;
                }
                if (value == null) {
                    copyExpression = StringUtils.replace(copyExpression, "${" + substring + "}", "null");
                    continue;
                }
                copyExpression = StringUtils.replace(copyExpression, "${" + substring + "}", "'" + ObjectUtils.toString(value) + "'");
                continue;
            }
            if (!CollectionUtils.containsKeyIgnorecase(vars, substring)) {
                copyExpression = StringUtils.replace(copyExpression, "${" + substring + "}", "null");
                continue;
            }
            throw new BpmnException(I18nHelper.getMessage("GIKAM.BPMN.TIP.EXPRESSION_MISSING_PARAMETER", expression, substring), new String[0]);
        }
        try {
            return (Boolean)ScriptUtils.evalJavaScript(copyExpression);
        }
        catch (Exception ex) {
            logger.error(ex);
            throw new BpmnException(I18nHelper.getMessage("GIKAM.BPMN.TIP.EVAL_EXPRESSION_ERROR", expression, copyExpression), new String[0]);
        }
    }

    public static <ID> List<String> getCandidatorList(String selectBySql, ID itemId, Map<String, Object> vars) {
        String replacedSelectBySql = CoreBpmnHelper.getReplacedSql(selectBySql, itemId, vars);
        if (StringUtils.isBlank(ObjectUtils.toString(itemId)) || StringUtils.substringsBetween(replacedSelectBySql, "${", "}").size() > 0) {
            return CollectionUtils.emptyList();
        }
        List<Map<String, Object>> mapList = CoreBpmnHelper.getGlobalMapper().selectList(replacedSelectBySql);
        return mapList.stream().filter(e -> e != null).map(m -> (String)m.values().iterator().next()).collect(Collectors.toList());
    }

    public static <ID> List<String> getCandidatorList(String filterBySql, ID itemId, List<String> userList, List<Long> roleList, Map<String, Object> vars) {
        String replacedFilterBySql = CoreBpmnHelper.getReplacedFilterBySql(filterBySql, itemId, userList, roleList, vars);
        if (StringUtils.isBlank(ObjectUtils.toString(itemId)) || StringUtils.substringsBetween(replacedFilterBySql, "${", "}").size() > 0) {
            return CollectionUtils.emptyList();
        }
        List<Map<String, Object>> mapList = CoreBpmnHelper.getGlobalMapper().selectList(replacedFilterBySql);
        return mapList.stream().map(m -> (String)m.values().iterator().next()).collect(Collectors.toList());
    }

    public static String getReplacedSql(String sql, Object id, Map<String, Object> vars) {
        sql = StringUtils.replace(sql, "${id}", ObjectUtils.toString(id));
        sql = StringUtils.replace(sql, "${loginUser.id}", LocalContextHelper.getLoginUserId());
        sql = StringUtils.replace(sql, "${loginUser.userName}", LocalContextHelper.getLoginUserName());
        sql = StringUtils.replace(sql, "${loginUser.orgId}", LocalContextHelper.getLoginUser().getOrgId());
        sql = StringUtils.replace(sql, "${loginUser.orgName}", LocalContextHelper.getLoginUser().getOrgName());
        List<String> substringList = StringUtils.substringsBetween(sql, "${", "}");
        if (vars != null) {
            for (String substring : substringList) {
                if (CollectionUtils.containsKeyIgnorecase(vars, substring)) {
                    Object value = CollectionUtils.getValueIgnorecase(vars, substring);
                    if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                        sql = StringUtils.replace(sql, "${" + substring + "}", ObjectUtils.toString(value));
                        continue;
                    }
                    if (value == null) {
                        sql = StringUtils.replace(sql, "${" + substring + "}", "null");
                        continue;
                    }
                    sql = StringUtils.replace(sql, "${" + substring + "}", "'" + ObjectUtils.toString(value) + "'");
                    continue;
                }
                throw new BpmnException(I18nHelper.getMessage("GIKAM.BPMN.TIP.SELECT_BY_SQL_MISSING_PARAMETER", sql, substring), new String[0]);
            }
        }
        return sql;
    }

    public static String getReplacedFilterBySql(String filterBySql, Object id, List<String> userList, List<Long> roleList, Map<String, Object> vars) {
        filterBySql = StringUtils.replace(filterBySql, "${id}", ObjectUtils.toString(id));
        filterBySql = StringUtils.replace(filterBySql, "${loginUser.id}", LocalContextHelper.getLoginUserId());
        filterBySql = StringUtils.replace(filterBySql, "${loginUser.userName}", LocalContextHelper.getLoginUserName());
        filterBySql = StringUtils.replace(filterBySql, "${loginUser.orgId}", LocalContextHelper.getLoginUser().getOrgId());
        filterBySql = StringUtils.replace(filterBySql, "${loginUser.orgName}", LocalContextHelper.getLoginUser().getOrgName());
        StringBuilder usersParam = new StringBuilder();
        for (String string : userList) {
            if (usersParam.length() > 0) {
                usersParam.append(", ");
            }
            usersParam.append("'").append(string).append("'");
        }
        if (usersParam.length() == 0) {
            usersParam.append("''");
        }
        filterBySql = StringUtils.replace(filterBySql, "${users}", usersParam.toString());
        StringBuilder rolesParam = new StringBuilder();
        for (Long role : roleList) {
            if (rolesParam.length() > 0) {
                rolesParam.append(", ");
            }
            rolesParam.append(role);
        }
        if (usersParam.length() == 0) {
            usersParam.append("''");
        }
        if (rolesParam.length() == 0) {
            rolesParam.append("''");
        }
        List<String> list = StringUtils.substringsBetween(filterBySql, "${", "}");
        for (String substring : list) {
            if (CollectionUtils.containsKeyIgnorecase(vars, substring)) {
                Object value = CollectionUtils.getValueIgnorecase(vars, substring);
                if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                    filterBySql = StringUtils.replace(filterBySql, "${" + substring + "}", ObjectUtils.toString(value));
                    continue;
                }
                if (value == null) {
                    filterBySql = StringUtils.replace(filterBySql, "${" + substring + "}", "null");
                    continue;
                }
                filterBySql = StringUtils.replace(filterBySql, "${" + substring + "}", "'" + ObjectUtils.toString(value) + "'");
                continue;
            }
            throw new BpmnException(I18nHelper.getMessage("GIKAM.BPMN.TIP.FILTER_BY_SQL_MISSING_PARAMETER", filterBySql, substring), new String[0]);
        }
        return StringUtils.replace(filterBySql, "${roles}", rolesParam.toString());
    }

    private static GlobalMapper getGlobalMapper() {
        if (globalMapper == null) {
            globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);
        }
        return globalMapper;
    }
}
