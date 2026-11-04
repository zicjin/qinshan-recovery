coreAnnualPlan.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [{
                type : 'button',
                id : 'core-qms-annual-plan-detail-back-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    var comp = Gikam.getComp('core-annual-plan-edit-list-grid');
                    if (!Gikam.isEmpty(comp)) {
                        comp.refresh();
                    }
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-add-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreAnnualPlan.baseUrl, Gikam.getJsonWrapper({}, [ '', [{'planCategory':_this.planCategory}] ])).done(function(item) {
                        Gikam.getComp('core-annual-plan-detail-info').refresh({
                            url : Gikam.printf(coreAnnualPlan.baseUrl + '/{id}', {
                                id : item
                            })
                        });
                        Gikam.getComp('core-annual-plan-detail-child-comp').refresh(_this.getRefreshParam(item));
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                    
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-delete-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var data = Gikam.getComp('core-annual-plan-detail-info').getData();
                    workspace.window.showMask(true);
                    Gikam.del(coreAnnualPlan.baseUrl, Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-annual-plan-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-submit-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-annual-plan-detail-info');
                    var formChild = Gikam.getComp('core-annual-plan-detail-child-comp');
                    if (Gikam.isFalse(form.validate()) || Gikam.isFalse(formChild.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreAnnualPlan
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-annual-plan-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-pass-button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-annual-plan-detail-info');
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreAnnualPlan
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-annual-plan-audit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-reject-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-annual-plan-detail-info');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreAnnualPlan
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-annual-plan-audit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-copy-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var comp = Gikam.getComp('core-annual-plan-detail-info');
                    var annualPlanId = comp.getData().id;
                    workspace.window.showMask(true);
                    Gikam.post(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/{id}/action/copy', {
                        id : annualPlanId
                    })).done(function(item) {
                        Gikam.getComp('core-annual-plan-detail-info').refresh({
                            url : Gikam.printf(coreAnnualPlan.baseUrl + '/{id}', {
                                id : item
                            })
                        });
                        Gikam.getComp('core-annual-plan-detail-child-comp').refresh(_this.getRefreshParam(item));
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-print-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    coreAnnualPlan.printReport(_this.param.id)
                }
            }, {
                type : 'button',
                id : 'core-qms-annual-plan-detail-search-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.SEARCH',
                icon : 'search',
                onClick : function() {
                    workspace.window.load(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/page/search-list');
                }
            } ]
        }
    },

    getAnnualPlanForm : function() {
        var _this = this;
        return {
            type : 'form',
            url :Gikam.printf(coreAnnualPlan.baseUrl + '/{id}', {
                id : _this.param.id
            }),
            service : 'coreAnnualPlanServiceImpl',
            id : 'core-annual-plan-detail-info',
            fields : coreAnnualPlan.getAnnualPlanBaseInfoFormFields(),
            columns : 4
        }
    },
    
    getChildForm : function() {
        var _this = this;
        return {
            type : 'form',
            url : Gikam.printf(_this.childUrl, {
                id : _this.param.id
            }),
            service : _this.childService,
            id : 'core-annual-plan-detail-child-comp',
            fields : _this.childFields,
            columns : 1,
            titleWidth : 110
        }
    },
    
    getChildGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            autoSave : false,
            fill : true,
            id : 'core-annual-plan-detail-child-comp',
            service : _this.childService,
            url : _this.childUrl,
            requestData : {
                annualPlanId_SEQ : _this.param.id
            },
            columns : _this.childFields,
            deleteFormatter : function(row) {
                return _this.planCategory == 'empTrainPlan'?(Gikam.isEmpty(row.userName)?'' : row.userName) : (Gikam.isEmpty(row.equName)?'' : row.equName);
            },
            onLoadSuccess  : function(rows) {
                var grid = Gikam.getComp('core-annual-plan-detail-child-comp');
                if (Gikam.isEmpty(grid.getData())) {
                    var insertRows = new Array;
                    for (var i = 0; i < 10; i++) {
                        insertRows[i] = {id : 0};
                    }
                    var grid = Gikam.getComp('core-annual-plan-detail-child-comp');
                    grid.refreshByData({
                        rows : insertRows
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADDROW',
                icon : 'add',
                onClick : function() {
                    var grid = Gikam.getComp('core-annual-plan-detail-child-comp');
                    var rows = grid.getData();
                    rows[rows.length] = {
                            id : 0
                    };
                    grid.refreshByData({
                        rows : rows
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETEROW',
                icon : 'remove-row',
                onClick : function() {
                    _this.removeRow();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            } ]
        };
    },
   
    removeRow : function() {
        // 删除人员培训子表行分两种-假数据行和真实数据行，假行直接在前端删除，真实行发送请求后端删除
        var _this = this;
        var grid = Gikam.getComp('core-annual-plan-detail-child-comp' );
        var gridData = grid.getData();
        var refreshGridData = grid.getData();
        var selectRows = grid.getSelections();
        for (var i = selectRows.length - 1; i > -1; i--) {
            if (Gikam.isEmpty(selectRows[i].id) || selectRows[i].id === 0) {
                _this.removeRowByIndex(gridData, selectRows[i].gridIndex - 1);
            }
            _this.removeRowByIndex(refreshGridData, selectRows[i].gridIndex - 1);
        }
        grid.deleteRows(Gikam.printf(_this.deleteUrl)).done(function(){

        }).always(function() {
            grid.refreshByData({
                rows : refreshGridData
            });
        });
    },

    removeRowByIndex : function(rows, index) {
        rows.splice(index, 1);
        Gikam.each(rows, function(index, row){
            row.gridIndex = index + 1;
        })
    },

    save : function() {
        var _this = this;
        var grid = Gikam.getComp('core-annual-plan-detail-child-comp' );
        //先删除无效的行（对于人员培训计划是员工信息未填写的行，其它是设备编号）
        var gridData = grid.getData();
        for (var i = gridData.length - 1; i > -1; i--){
            if (Gikam.isEmpty(_this.planCategory == 'empTrainPlan'?gridData[i].userId : gridData[i].equCode)) {
                _this.removeRowByIndex(gridData, gridData[i].gridIndex - 1);
            }
        }
        grid.refreshByData({rows : gridData});
        if (Gikam.isFalse(grid.validate()))
            return;
        workspace.window.showMask();
        Gikam.put(Gikam.printf(_this.putUrl, {
            annualPlanId : _this.param.id
        }), Gikam.getJsonWrapper({}, [ '', gridData ])).done(function() {
            grid.refresh({
                url : _this.childUrl,
                requestData : {
                    annualPlanId_SEQ : _this.param.id
                }
            });
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    getChildTableComp : function() {
        var _this = this;
        if (_this.planCategory == 'empTrainPlan' || _this.planCategory == 'verifyCalibrate' || _this.planCategory == 'periodVerify' || _this.planCategory == 'equFuncCheck') {
            return _this.getChildGrid();
        } else {
            return _this.getChildForm();
        }
    },
    
    createPage : function() {
        var _this = this;
        var pageConfig = {
                bizId : _this.param.id,
                pageObject : coreAnnualPlan,
                config : _this.pageConfig,
                prior : _this.param.sourcePage
        };
        Gikam.create('layout', {
            north : {
                height : '17%',
                items : [ _this.getBtnToolbar(), _this.getAnnualPlanForm()]
            },
            center : {
                height : '83%',
                items : [ _this.getChildTableComp() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.loadIniData();
        this.createPage();
    },
    
    loadIniData : function() {
        var _this = this;
        var url = coreAnnualPlan.baseUrl + '/queries';
        var jsonWrapper = Gikam.getJsonWrapper({
            f : {
                id_SEQ : _this.param.id
            }
        });
        Gikam.postSync(url,jsonWrapper).done(function(item) {
            _this.planCategory = item.rows[0].planCategory;
        });
        switch (_this.planCategory) {
        case 'empTrainPlan'://人员培训计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/emp-train-plan/queries';
            _this.putUrl = coreAnnualPlan.baseUrl + '/{annualPlanId}/action/save-emp-train-plans',
            _this.deleteUrl = coreAnnualPlan.baseUrl + '/action/delete-emp-train-plans';
            _this.childService = 'coreEmpTrainPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getTrainPlanListColumns();
            break;
        case 'mReviewPlan'://管理评审计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/{id}/manage-review-plan';
            _this.childService = 'coreManageReviewPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getManageReviewFormFields();
            break;
        case 'inAuditPlan'://内部审核计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/{id}/internal-audit-plan';
            _this.childService = 'coreInternalAuditPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getInternalAuditFormFields();
            break;
        case 'dailySupervise'://日常监督计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/{id}/daily-supervision-plan';
            _this.childService = 'coreDailySupervisionPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getDailySupervisionFormFields();
            break;
        case 'qualityControl'://检验质量控制计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/{id}/quality-control-plan';
            _this.childService = 'coreQualityControlPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getQualityControlFormFields();
            break;
        case 'verifyCalibrate'://仪器设备检定校准计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/verify-calibrate-plan/queries';
            _this.putUrl = coreAnnualPlan.baseUrl + '/{annualPlanId}/action/save-verify-calibrate-plans',
            _this.deleteUrl = coreAnnualPlan.baseUrl + '/action/delete-verify-calibrate-plans';
            _this.childService = 'coreVerifyCalibratePlanServiceImpl';
            _this.childFields = coreAnnualPlan.getCalibratePlanListColumns();
            break;
        case 'periodVerify'://期间核查计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/period-verify-plan/queries';
            _this.putUrl = coreAnnualPlan.baseUrl + '/{annualPlanId}/action/save-period-verify-plans',
            _this.deleteUrl = coreAnnualPlan.baseUrl + '/action/delete-period-verify-plans';
            _this.childService = 'corePeriodVerifyPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getPeriodVerifyPlanListColumns();
            break;
        case 'equFuncCheck'://仪器设备功能检查计划
            _this.childUrl = coreAnnualPlan.baseUrl + '/equ-func-check-plan/queries';
            _this.putUrl = coreAnnualPlan.baseUrl + '/{annualPlanId}/action/save-equ-func-check-plans',
            _this.deleteUrl = coreAnnualPlan.baseUrl + '/action/delete-equ-func-check-plans';
            _this.childService = 'coreEquFuncCheckPlanServiceImpl';
            _this.childFields = coreAnnualPlan.getFuncCheckPlanListColumns();
            break;
        };
    },
    
    getRefreshParam : function(refreshId) {
        var _this = this;
        if (_this.planCategory == 'empTrainPlan' || _this.planCategory == 'verifyCalibrate' || _this.planCategory == 'periodVerify' || _this.planCategory == 'equFuncCheck') {
            return {
                url : _this.childUrl, 
                requestData : {
                    annualPlanId_SEQ : refreshId
                }
            }
        } else {
            return {
                url : Gikam.printf( _this.childUrl, {
                    id : refreshId
                })
            }
        }
    },
    
    planCategory : '',
    
    childUrl : '',
    
    putUrl : '',
    
    deleteUrl : '',
    
    childService : '',
    
    chidldFileds : '',
    
}
