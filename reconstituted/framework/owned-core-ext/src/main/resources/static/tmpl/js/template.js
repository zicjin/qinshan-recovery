<#assign excludeFields = ["CREATEDBYID","CREATEDBYNAME","CREATEDTIME","CREATEDBYORGID","CREATEDBYORGNAME","PROCESSSTATUS"]>
var ${data.jsObjectName} = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }<#if data.audit == "1">, Gikam.status.getBpmnColumn(${data.jsObjectName}.workflow.dbTable)</#if><#list data.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && (!field.mainDisplay??  || field.mainDisplay == "1" ) >, {
            field : '${field.field}',
            title : '${field.ext$.i118n}'<#if field.field=="id" && data.layout == "detail">,
            type : 'link'</#if>
        }</#if></#list> ];
    },

    getBaseInfoFormFields : function() {
        return [<#list data.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && (!field.detailDisplay??  || field.detailDisplay == "1")> {
            field : '${field.field}',
            title : '${field.ext$.i118n}'<#if field_index==0>,
            preInsert : true</#if><#if field.ext$.validators??>,
            validators : ${field.ext$.validators}</#if>
        }<#if field_has_next>,</#if></#if></#list> ];
    },

<#list data.subTableTemplateAttrList as subTableTemplateAttr>
    get${subTableTemplateAttr.tableObject}ListColumns : function() {
        return [ {
            checkbox : true
        }<#list subTableTemplateAttr.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && field.field!="id" && field.field != subTableTemplateAttr.foreignField>, {
            field : '${field.field}',
            title : '${field.ext$.i118n}',
            editor : true
        }</#if></#list> ];
    },

</#list>
<#list data.funcList as func>
    <#if func == "audit">
    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(${data.jsObjectName}.workflow.dbTable)<#list data.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && (!field.mainDisplay??  || field.mainDisplay == "1" )>, {
            field : '${field.field}',
            title : '${field.ext$.i118n}'<#if field.field=="id">,
            type : 'link'</#if>
        }</#if></#list> ];
    },

    <#elseif func == "search">
    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(${data.jsObjectName}.workflow.dbTable)<#list data.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && (!field.mainDisplay??  || field.mainDisplay == "1" )>, {
            field : '${field.field}',
            title : '${field.ext$.i118n}'<#if field.field=="id">,
            type : 'link'</#if>
        }</#if></#list> ];
    },

    <#elseif func == "undo">
    getUndoListColumns : function() {
        return [ {
            checkbox : true
        }<#list data.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && (!field.mainDisplay??  || field.mainDisplay == "1" )>, {
            field : '${field.field}',
            title : '${field.ext$.i118n}'<#if field.field=="id">,
            type : 'link'</#if>
        }</#if></#list> ];
    },

    <#elseif func == "choose">
    getChooseListColumns : function() {
        return [<#list data.fields as field><#if !(excludeFields)?seq_contains(field.field?upper_case) && (!field.mainDisplay??  || field.mainDisplay == "1" )> {
            field : '${field.field}',
            title : '${field.ext$.i118n}'
        }<#if field_has_next>,</#if></#if></#list> ];
    },

    </#if>
</#list>
    baseUrl : IFM_CONTEXT + '${data.resourcePath}'<#if data.audit == "1">,

    workflow : {
        dbTable : '${data.tableName}',
        columns : [ {
            field : 'id',
            title : '${data.tableName}.ID'
        } ]
    }
    </#if>

});
