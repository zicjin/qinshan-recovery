${data.jsObjectName}.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('${data.tableName}', _this.param.id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '${data.i118nPackageName}.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : '${data.jsObjectName}ServiceImpl',
                    dbTable : '${data.tableName?upper_case}',
                    id : '${data.jsFileName}-detail-base-info-form',
                    fields : ${data.jsObjectName}.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

<#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ <#list data.subTableTemplateAttrList as subTableTemplateAttr>_this.get${subTableTemplateAttr.tableObject}Grid()<#if subTableTemplateAttr_has_next>, </#if></#list> ]
        }
    },

</#if>
    <#list data.subTableTemplateAttrList as subTableTemplateAttr>
    get${subTableTemplateAttr.tableObject}Grid : function() {
        var _this = this;
        return {
            title : '${data.i118nPackageName}.TAB.${subTableTemplateAttr.i118n}',
            items : [ {
                type : 'grid',
                id : '${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid',
                service : '${subTableTemplateAttr.javaObjectName?uncap_first}ServiceImpl',
                dbTable : '${subTableTemplateAttr.subTableName?upper_case}',
                url : _this.getCompUrl('${subTableTemplateAttr.tableObject?uncap_first}Grid'),
                deleteFormatter : function(row) {
                    return row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.getComp('${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid').insert(Gikam.printf(${data.jsObjectName}.baseUrl + '/{id}/${subTableTemplateAttr.path}s', {
                            id : _this.param.id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid').deleteRows(Gikam.printf(${data.jsObjectName}.baseUrl + '/{id}/${subTableTemplateAttr.path}s', {
                            id : _this.param.id
                        }));
                    }
                } ],
                columns : ${data.jsObjectName}.get${subTableTemplateAttr.tableObject}ListColumns()
            } ]
        };
    },

</#list>
    urlManager : {
        baseInfoForm : ${data.jsObjectName}.baseUrl + '/{id}',
<#list data.subTableTemplateAttrList as subTableTemplateAttr>
        ${subTableTemplateAttr.tableObject?uncap_first}Grid : ${data.jsObjectName}.baseUrl + '/{id}/${subTableTemplateAttr.path}s/queries',
</#list>
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            <#if data.subTableTemplateAttrList?? && (data.subTableTemplateAttrList?size > 0) >
            north : {
                height : '50%',
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            center : {
                items : [ this.getSouthTab() ]
            },
            <#else>
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            </#if>
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
