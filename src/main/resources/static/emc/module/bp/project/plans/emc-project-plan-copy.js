emcProjectPlan.copySchedulePage = {

    // 查询按钮
    getQueryBtnItem : function() {
        return {
            type: 'button',
            text: 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BUTTON.QUERY',
            class: 'blue',
            margin : '10px 0 0 15px',
            onClick : function () {
                var form = Gikam.getComp("emc-project-plan-copy-query-form");
                var formData = form.options.data;
                var taskDate = ''
                if (formData.taskYear) {
                    taskDate += formData.taskYear
                }
                if (formData.taskMonth) {
                    if((formData.taskMonth | 0) === formData.taskMonth && parseInt(formData.taskMonth) < 10) {
                        taskDate += '-0' + formData.taskMonth + '-'
                    } else {
                        taskDate += '-' + formData.taskMonth + '-'
                    }
                }
                Gikam.getComp('emc-project-plan-copy-project-list-grid').refresh({
                    url : emcProject.baseUrl + '/queries/searchable',
                    requestData : {
                        targetType : 'copyProject',
                        bizCategoryId_SEQ : formData.bizCategoryId,
                        monitorTmplId : formData.monitorTmplId,
                        projNo_CISC : formData.projNo,
                        projName_CISC : formData.projName,
                        testOrgName_CISC : formData.testOrgName,
                        entrustOrgName_CISC : formData.entrustOrgName,
                        createdById_SEQ : formData.createdById,
                        taskDate_CISC : taskDate,
                    },
                })
            }
        }
    },

    // 查询表单
    getQueryFormItem : function() {
        return {
            type : 'form',
            id : 'emc-project-plan-copy-query-form',
            columns : 5,
            url : '',
            titleWidth : 80,
            service : 'emcFileServiceImpl',
            fields : emcProjectPlan.getProjectQueryFormFields(),
            autoSave : false
        }
    },

    // 项目列表
    getProjectTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-project-plan-copy-project-list-tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.PROJECT',
                items : [ {
                    type : 'grid',
                    id : 'emc-project-plan-copy-project-list-grid',
                    url : '',
                    filter : false,
                    columns : emcProject.getProjectQueryListColumns(),
                    service : 'emcProjectServiceImpl',
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ "emc-project-plan-copy-monitor-point-grid",
                                "emc-project-plan-copy-order-grid" ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onCopyProjectActive(row);
                    }
                } ]
            } ]
        }
    },

    // 选中项目
    onCopyProjectActive : function(row) {
        Gikam.getComp('emc-project-plan-copy-result-form').setData({
            bizCategoryId : row.bizCategoryId
        });

        var tab = Gikam.getComp('emc-project-plan-copy-monitor-point-order-tab');
        if (row.sample === '1') {
            tab.revertPanel('emc-project-plan-copy-monitor-point-panel');
            tab.removePanel('emc-project-plan-copy-order-panel');
            Gikam.getComp('emc-project-plan-copy-monitor-point-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/queries/raw',
                requestData : {
                    projId_SEQ : row.id
                },
            })
        } else {
            tab.removePanel('emc-project-plan-copy-monitor-point-panel');
            tab.revertPanel('emc-project-plan-copy-order-panel');
            Gikam.getComp('emc-project-plan-copy-order-grid').refresh({
                url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                    id : row.id
                })
            });
        }
    },

    // 监测点列表
    getMonitorPointTab : function() {
        return {
            type : 'tab',
            id : 'emc-project-plan-copy-monitor-point-order-tab',
            width : Gikam.width.S,
            show : true,
            panels : [ {
                id : 'emc-project-plan-copy-monitor-point-panel',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.MONITORPOINT',
                items : [ {
                    type : 'grid',
                    filter : false,
                    id : 'emc-project-plan-copy-monitor-point-grid',
                    url : '',
                    columns : emcProject.getMonitorPointListColumns(),
                    service : 'emcProjSchemeServiceImpl'
                } ]
            }, {
                id : 'emc-project-plan-copy-order-panel',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                items : [ {
                    type : 'grid',
                    filter : false,
                    id : 'emc-project-plan-copy-order-grid',
                    url : '',
                    columns : emcProject.getProjectOrderColumns(),
                    service : 'emcOrderServiceImpl'
                } ]
            } ]
        }
    },

    // 复制表单
    getCopyFormItem : function() {
        return {
            type : 'form',
            id : 'emc-project-plan-copy-result-form',
            columns : 3,
            url : '',
            titleWidth : 120,
            service : 'emcFileServiceImpl',
            fields : emcProjectPlan.getCopyFormFields(),
            autoSave : false
        }
    },

    // 确认取消按钮组
    getConfirmCancelBtnItems : function() {
        return [ {
            type : 'button',
            text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CONFIRM',
            class : 'blue',
            margin : '12px 15px 0 30px',
            onClick : function() {
                var selections = Gikam.getComp('emc-project-plan-copy-project-list-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SELECT_AT_LEAST_ONE');
                    return;
                }
                var form = Gikam.getComp("emc-project-plan-copy-result-form");
                var formData = form.options.data
                if (form.validate()) {
                    var copyRows = []
                    selections.forEach(function (r) {
                        var row = {};
                        row.id = r.id;
                        row.bizCategoryId = formData.bizCategoryId;
                        row.taskDate = formData.taskDate;
                        row.sample = r.sample;
                        row.copyQty = formData.copyQty;
                        row.source = 'plan';
                        copyRows.push(row)
                    })
                    var url = emcProject.baseUrl + '/plans/copy';

                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(url, Gikam.getJsonWrapper({}, [ '', copyRows ])).done(function () {
                        Gikam.getComp('emc-project-plan-edit-list-grid').refresh();
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                        Gikam.getLastModal().close();
                    });
                }
            }
        }, {
            type : 'button',
            margin : '12px 0 0 0',
            text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CANCEL',
            onClick : function () {
                Gikam.getLastModal().close();
            }
        } ]
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 128,
                items : [ _this.getQueryBtnItem(), _this.getQueryFormItem() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [
                            this.getProjectTab()
                        ]
                    },
                    center : {
                        items : [
                            this.getMonitorPointTab()
                        ]
                    }
                } ]
            },
            south : {
                height : 56,
                items : [ {
                    type : 'layout',
                    west : {
                        width : '80%',
                        items : [ _this.getCopyFormItem()]
                    },
                    center : {
                        items : _this.getConfirmCancelBtnItems()
                    }
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}
