emcProject.copyPage = {

    // 查询按钮
    getQueryBtnItem : function() {
        return {
            type: 'button',
            text: 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BUTTON.QUERY',
            class: 'blue',
            margin : '10px 0 0 15px',
            onClick : function () {
                var form = Gikam.getComp("emc-project-copy-query-form");
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
                Gikam.getComp('emc-project-copy-project-list-grid').refresh({
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
                        org_filter : '1',
                    },
                })
            }
        }
    },

    // 查询表单
    getQueryFormItem : function() {
        return {
            type : 'form',
            id : 'emc-project-copy-query-form',
            columns : 5,
            url : '',
            titleWidth : 80,
            service : 'emcFileServiceImpl',
            fields : emcProject.getProjectQueryFormFields(),
            autoSave : false
        }
    },

    // 项目列表
    getProjectTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-project-copy-project-list-tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.PROJECT',
                items : [ {
                    type : 'grid',
                    id : 'emc-project-copy-project-list-grid',
                    url : '',
                    filter : false,
                    columns : emcProject.getProjectQueryListColumns(),
                    service : 'emcProjectServiceImpl',
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ "emc-project-copy-monitor-point-grid",
                                "emc-project-copy-order-grid" ]);
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
        Gikam.getComp('emc-project-copy-result-form').setData({
            bizCategoryId : row.bizCategoryId,
            projName : row.projName
        });

        var tab = Gikam.getComp('emc-project-copy-monitor-point-order-tab');
        if (row.sample === '1') {
            tab.revertPanel('emc-project-copy-monitor-point-panel');
            tab.removePanel('emc-project-copy-order-panel');
            Gikam.getComp('emc-project-copy-monitor-point-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/queries/raw',
                requestData : {
                    projId_SEQ : row.id
                },
            })
        } else {
            tab.removePanel('emc-project-copy-monitor-point-panel');
            tab.revertPanel('emc-project-copy-order-panel');
            Gikam.getComp('emc-project-copy-order-grid').refresh({
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
            id : 'emc-project-copy-monitor-point-order-tab',
            width : Gikam.width.S,
            show : true,
            panels : [ {
                id : 'emc-project-copy-monitor-point-panel',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.MONITORPOINT',
                items : [ {
                    type : 'grid',
                    filter : false,
                    id : 'emc-project-copy-monitor-point-grid',
                    url : '',
                    columns : emcProject.getMonitorPointListColumns(),
                    service : 'emcProjSchemeServiceImpl'
                } ]
            }, {
                id : 'emc-project-copy-order-panel',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                items : [ {
                    type : 'grid',
                    filter : false,
                    id : 'emc-project-copy-order-grid',
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
            id : 'emc-project-copy-result-form',
            columns : 3,
            url : '',
            titleWidthAuto : true,
            // titleWidth : 120,
            fields : emcProject.getCopyFormFields(),
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
                var selections = Gikam.getComp('emc-project-copy-project-list-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SELECT_AT_LEAST_ONE');
                    return;
                }

                var form = Gikam.getComp("emc-project-copy-result-form");
                var formData = form.options.data
                if (form.validate()) {
                    var copyRows = []
                    selections.forEach(function (r) {
                        var row = {};
                        row.id = r.id;
                        row.year = formData.year;
                        row.bizCategoryId = formData.bizCategoryId;
                        row.bizCategoryCode = formData.bizCategoryId;
                        row.taskDate = formData.taskDate;
                        row.sample = r.sample;
                        row.copyQty = formData.copyQty;
                        row.projName = formData.projName;

                        row.contractid = formData.ext$.contractid;
                        row.contractno = formData.ext$.contractno;
                        row.biddername = formData.ext$.biddername;
                        row.splitContractId = formData.splitContractId,
                        // row.entrustOrgId = formData.entrustOrgId,
                        // row.entrustOrgName = formData.entrustOrgName,
                        // row.entrustOrgAddress = formData.entrustOrgAddress,
                        // row.entrustOrgContact = formData.entrustOrgContact,
                        // row.entrustOrgPhone = formData.entrustOrgPhone,
                        // row.testOrgId = formData.testOrgId,
                        // row.testOrgName = formData.testOrgName,
                        // row.testOrgAddress = formData.testOrgAddress,
                        // row.testOrgContact = formData.testOrgContact,
                        // row.testOrgPhone = formData.testOrgPhone,
                        row.reportPostAddress = formData.reportPostAddress,
                        row.addressee = formData.addressee,
                        row.recipientPhone = formData.recipientPhone,
                        copyRows.push(row)
                    })
                    var url = emcProject.baseUrl + '/action/copy';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.postText(url, Gikam.getJsonWrapper({}, [ '', copyRows ])).always(function () {
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
                height : 130,
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
                height : 112,
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ _this.getCopyFormItem()]
                    },
                    east : {
                        width : 150,
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
