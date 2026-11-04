/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.SubTableTemplateAttrBean;
import java.util.List;

public class TemplateAttrBean {
    private String jsPath;
    private String jsFileName;
    private String jsObjectName;
    private String idFieldName;
    private String idFieldType;
    private String layout;
    private List<String> funcList;
    private List<CoreCodeGenerateConfigFieldBean> fields;
    private String tableDesc;
    private String tableName;
    private String create;
    private String audit;
    private String resourcePath;
    private String packageName;
    private String javaObjectName;
    private String tableObject;
    private String functionName;
    private String controllerPath;
    private String htmlPath;
    private String htmlFileName;
    private String createOracleTableSql;
    private String createMysqlTableSql;
    private String createPgsqlTableSql;
    private String createSqlServerStr;
    private String createMenuSql;
    private String editMenuId;
    private String auditMenuId;
    private String subTableName;
    private String foreignField;
    private String subTableDesc;
    private List<CoreCodeGenerateConfigFieldBean> subTableFields;
    private String subIdFieldType;
    private String nowDate;
    private String author;
    private String i118nPackageName;
    private String i118n;
    private String dateFlag;
    private String date;
    private String dateTime;
    private List<SubTableTemplateAttrBean> subTableTemplateAttrList;

    public String getJsPath() {
        return this.jsPath;
    }

    public void setJsPath(String jsPath) {
        this.jsPath = jsPath;
    }

    public String getJsFileName() {
        return this.jsFileName;
    }

    public void setJsFileName(String jsFileName) {
        this.jsFileName = jsFileName;
    }

    public String getJsObjectName() {
        return this.jsObjectName;
    }

    public void setJsObjectName(String jsObjectName) {
        this.jsObjectName = jsObjectName;
    }

    public String getIdFieldName() {
        return this.idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public String getIdFieldType() {
        return this.idFieldType;
    }

    public void setIdFieldType(String idFieldType) {
        this.idFieldType = idFieldType;
    }

    public String getCreate() {
        return this.create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getAudit() {
        return this.audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public List<String> getFuncList() {
        return this.funcList;
    }

    public void setFuncList(List<String> funcList) {
        this.funcList = funcList;
    }

    public List<CoreCodeGenerateConfigFieldBean> getFields() {
        return this.fields;
    }

    public void setFields(List<CoreCodeGenerateConfigFieldBean> fields) {
        this.fields = fields;
    }

    public String getI118nPackageName() {
        return this.i118nPackageName;
    }

    public void setI118nPackageName(String i118nPackageName) {
        this.i118nPackageName = i118nPackageName;
    }

    public String getTableDesc() {
        return this.tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getJavaObjectName() {
        return this.javaObjectName;
    }

    public void setJavaObjectName(String javaObjectName) {
        this.javaObjectName = javaObjectName;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getControllerPath() {
        return this.controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public String getHtmlPath() {
        return this.htmlPath;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public String getHtmlFileName() {
        return this.htmlFileName;
    }

    public void setHtmlFileName(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }

    public String getCreateOracleTableSql() {
        return this.createOracleTableSql;
    }

    public void setCreateOracleTableSql(String createOracleTableSql) {
        this.createOracleTableSql = createOracleTableSql;
    }

    public String getCreateMysqlTableSql() {
        return this.createMysqlTableSql;
    }

    public String getCreatePgsqlTableSql() {
        return this.createPgsqlTableSql;
    }

    public void setCreatePgsqlTableSql(String createPgsqlTableSql) {
        this.createPgsqlTableSql = createPgsqlTableSql;
    }

    public String getTableObject() {
        return this.tableObject;
    }

    public void setTableObject(String tableObject) {
        this.tableObject = tableObject;
    }

    public void setCreateMysqlTableSql(String createMysqlTableSql) {
        this.createMysqlTableSql = createMysqlTableSql;
    }

    public String getCreateMenuSql() {
        return this.createMenuSql;
    }

    public void setCreateMenuSql(String createMenuSql) {
        this.createMenuSql = createMenuSql;
    }

    public String getEditMenuId() {
        return this.editMenuId;
    }

    public void setEditMenuId(String editMenuId) {
        this.editMenuId = editMenuId;
    }

    public String getAuditMenuId() {
        return this.auditMenuId;
    }

    public void setAuditMenuId(String auditMenuId) {
        this.auditMenuId = auditMenuId;
    }

    public String getSubTableName() {
        return this.subTableName;
    }

    public void setSubTableName(String subTableName) {
        this.subTableName = subTableName;
    }

    public String getForeignField() {
        return this.foreignField;
    }

    public void setForeignField(String foreignField) {
        this.foreignField = foreignField;
    }

    public String getSubTableDesc() {
        return this.subTableDesc;
    }

    public void setSubTableDesc(String subTableDesc) {
        this.subTableDesc = subTableDesc;
    }

    public List<CoreCodeGenerateConfigFieldBean> getSubTableFields() {
        return this.subTableFields;
    }

    public void setSubTableFields(List<CoreCodeGenerateConfigFieldBean> subTableFields) {
        this.subTableFields = subTableFields;
    }

    public String getSubIdFieldType() {
        return this.subIdFieldType;
    }

    public void setSubIdFieldType(String subIdFieldType) {
        this.subIdFieldType = subIdFieldType;
    }

    public String getNowDate() {
        return this.nowDate;
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLayout() {
        return this.layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getI118n() {
        return this.i118n;
    }

    public void setI118n(String i118n) {
        this.i118n = i118n;
    }

    public String getCreateSqlServerStr() {
        return this.createSqlServerStr;
    }

    public void setCreateSqlServerStr(String createSqlServerStr) {
        this.createSqlServerStr = createSqlServerStr;
    }

    public List<SubTableTemplateAttrBean> getSubTableTemplateAttrList() {
        return this.subTableTemplateAttrList;
    }

    public void setSubTableTemplateAttrList(List<SubTableTemplateAttrBean> subTableTemplateAttrList) {
        this.subTableTemplateAttrList = subTableTemplateAttrList;
    }

    public String getDateFlag() {
        return this.dateFlag;
    }

    public void setDateFlag(String dateFlag) {
        this.dateFlag = dateFlag;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
