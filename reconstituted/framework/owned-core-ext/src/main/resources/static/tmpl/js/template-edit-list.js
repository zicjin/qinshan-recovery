${data.jsObjectName}.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : '${data.jsFileName}-edit-list-grid',
            url : ${data.jsObjectName}.baseUrl + '/queries/raw',
            service : '${data.javaObjectName?uncap_first}ServiceImpl',
            dbTable : '${data.tableName?upper_case}',
            columns : ${data.jsObjectName}.getEditListColumns(),<#if data.layout == "detail">
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },</#if><#if data.layout != "detail">
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ '${data.jsFileName}-edit-list-base-info-form'<#list data.subTableTemplateAttrList as subTableTemplateAttr>, '${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid'</#list> ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('${data.jsFileName}-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(${data.jsObjectName}.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

<#list data.subTableTemplateAttrList as subTableTemplateAttr>
                Gikam.getAsyncComp('${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(${data.jsObjectName}.baseUrl + '/{id}/${subTableTemplateAttr.path}s/queries', {
                            id : row.id
                        })
                    });
                });

</#list>
            },</#if>
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : '${data.i118nPackageName}.MODAL.ADD',
                        fields : ${data.jsObjectName}.getBaseInfoFormFields(),
                        url : ${data.jsObjectName}.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('${data.jsFileName}-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('${data.jsFileName}-edit-list-grid').deleteRows(${data.jsObjectName}.baseUrl);
                }
            }<#if data.audit == "1">, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }</#if> ]
        }
    },

<#if data.layout != "detail">
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '${data.i118nPackageName}.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : '${data.jsObjectName}ServiceImpl',
                    dbTable : '${data.tableName?upper_case}',
                    id : '${data.jsFileName}-edit-list-base-info-form',
                    fields : ${data.jsObjectName}.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('${data.jsFileName}-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }<#list data.subTableTemplateAttrList as subTableTemplateAttr>, _this.get${subTableTemplateAttr.tableObject}Grid()</#list> ]
        }
    },

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
                deleteFormatter : function(row) {
                    return row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.getComp('${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid').insert(Gikam.printf(${data.jsObjectName}.baseUrl + '/{id}/${subTableTemplateAttr.path}s', {
                            id : Gikam.getComp('${data.jsFileName}-edit-list-grid').getActivedRow().id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('${data.jsFileName}-edit-list-${subTableTemplateAttr.path}-grid').deleteRows(Gikam.printf(${data.jsObjectName}.baseUrl + '/{id}/${subTableTemplateAttr.path}s', {
                            id : Gikam.getComp('${data.jsFileName}-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ],
                columns : ${data.jsObjectName}.get${subTableTemplateAttr.tableObject}ListColumns()
            } ]
        };
    },

</#list>
</#if>
<#if data.audit == "1">
    submit : function() {
        var grid = Gikam.getComp('${data.jsFileName}-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : ${data.jsObjectName}
        }).done(function() {
            grid.refresh();
        });
    },

</#if>
<#if data.layout == "detail">
    loadDetailPage : function(id) {
        workspace.window.load(${data.jsObjectName}.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

</#if>
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : '${data.jsFileName}-edit-list-layout',
            renderTo : workspace.window.$dom,
            <#if data.layout == "detail">
            center : {
                items : [ this.getGridParam() ]
            }
            </#if>
            <#if data.layout != "detail">
            west : {
                width : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
            </#if>
        });
    },

    init : function() {
        this.create();
    }
};
