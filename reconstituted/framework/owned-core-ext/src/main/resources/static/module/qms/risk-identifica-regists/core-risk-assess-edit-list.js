coreRiskIdentificaRegist.assessPage = {
    getToolbarButtons : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
            icon : 'submit',
            onClick : function() {
                if (Gikam.getComp('core-risk-assess-edit-list-factor-grid').validate()) {
                    _this.submit();
                }
            }
        }, {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
            icon : 'reject',
            onClick : function() {
                _this.reject();
            }
        }, {
            type : 'button',
            text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
            icon : 'print',
            onClick : function() {
                var rows = Gikam.getComp('core-risk-assess-edit-list-regist-grid').getSelections();
                if (rows.length != 1) {
                    Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                    return;
                }
                Gikam.viewReport('QMS_RISK_CONTROL', {
                    id : rows[0].id
                });
            }
        } ]
    },

    submit : function() {
        var grid = Gikam.getComp('core-risk-assess-edit-list-regist-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-assess-edit-list-factor-grid').cleanData();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-risk-assess-edit-list-regist-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-assess-edit-list-factor-grid').cleanData();
        });
    },

    // 批量维护的表单
    getBatchForm : function() {
        return {
            id : 'core-risk-identifica-regist-batch-form',
            type : 'form',
            autoSave : false,
            columns : 2,
            fields : coreRiskIdentificaRegist.getAssessRegistFactorFormFields(),
        }
    },

    // 批量弹窗
    getModal : function() {
        var _this = this;
        var modal = Gikam.create('modal', {
            width : 800,
            height : 500,
            title : 'T_CORE_RISK_ASSESS.BUTTON.BATCH_TO_CHECK',
        });
        Gikam.create('layout', {
            id : 'core-risk-assess-edit-list-batch-layout',
            renderTo : modal.window.$dom,
            north : {
                height : 5,
                items : [ {
                    type : 'btnToolbar',
                    items : [ {
                        id : 'core-risk-assess-edit-list-batch-form-add-button',
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var form = Gikam.getComp('core-risk-identifica-regist-batch-form');
                            var dataGrid = Gikam.getComp('core-risk-assess-edit-list-factor-grid');
                            if (form.validate()) {
                                var data = form.getData();
                                var selectionData = dataGrid.getSelections();
                                for (var i = 0; i < selectionData.length; i++) {
                                    if (data.severity) {
                                        selectionData[i].severity = data.severity;
                                    }
                                    if (data.incidence) {
                                        selectionData[i].incidence = data.incidence;
                                    }
                                    var severity = data.severity == undefined ? 0 : data.severity;
                                    var incidence = data.incidence == undefined ? 0 : data.incidence;
                                    var sum = Number(severity) + Number(incidence);
                                    if (sum > 0) {
                                        selectionData[i].riskRate = sum;
                                    }
                                    if (data.beAuditOrgName) {
                                        selectionData[i].beAuditOrgName = data.beAuditOrgName;
                                    }
                                    if (data.beAuditOrgId) {
                                        selectionData[i].beAuditOrgId = data.beAuditOrgId;
                                    }
                                    if (data.refWorkFlowId) {
                                        selectionData[i].refWorkFlowId = data.refWorkFlowId;
                                    }
                                    if (data.refWorkFlowType) {
                                        selectionData[i].refWorkFlowType = data.refWorkFlowType;
                                    }
                                    if (data.riskResolution) {
                                        selectionData[i].riskResolution = data.riskResolution;
                                    }
                                }
                                workspace.window.showMask(true);
                                Gikam.put(coreRiskIdentificaRegist.registFactorBaseUrl + '/' + selectionData[0].id, Gikam.getJsonWrapper({}, [ '', selectionData ])).done(function(items) {
                                    Gikam.getLastModal().close();
                                    dataGrid.refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'T_CORE_RISK_ASSESS.BUTTON.BATCH_TO_CHECK',
                        items : [ _this.getBatchForm() ]
                    } ]
                } ]
            }
        });
    },

    getRegistDataGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-assess-edit-list-regist-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getAssessRegistColumns(),
            toolbar : _this.getToolbarButtons(),
            activateFirstRow : true,
            url : coreRiskIdentificaRegist.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            onRowActive : function(index, row) {
                Gikam.getComp('core-risk-assess-edit-list-factor-grid').refresh({
                    url : Gikam.printf(coreRiskIdentificaRegist.registFactorBaseUrl + '/queries', {
                        id : row.id
                    })
                });
                Gikam.getAsyncComp('core-risk-assess-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            }
        }
    },

    getRegistFactorDataGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-assess-edit-list-factor-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getAssessRegistFactorColumns(),
            checkOnActive : false,
            autoSave : true,
            service : 'coreRiskIdentificaFactorServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'T_CORE_RISK_ASSESS.BUTTON.BATCH_TO_CHECK',
                icon : 'batch-to-check',
                onClick : function() {
                    var grid = Gikam.getComp('core-risk-assess-edit-list-factor-grid');
                    if (grid.getData().length == 0) {
                        Gikam.alert('T_CORE_RISK_IDENTIFICA_FACTOR.CHOOSEONEITEMBEFORE');
                        return;
                    }
                    if (grid.getSelections().length == 0) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    _this.getModal();
                }
            } ],
            onBeforeUpdate : function(row, keys) {
                if (keys.includes('severity') || keys.includes('incidence')) {
                    var severity = row.row.severity == undefined ? 0 : row.row.severity;
                    var incidence = row.row.incidence == undefined ? 0 : row.row.incidence;
                    var sum = Number(severity) + Number(incidence);
                    Gikam.getComp('core-risk-assess-edit-list-factor-grid').setData({
                        id : row.row.id,
                        riskRate : sum
                    });
                }
            }
        }
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-risk-assess-edit-list-uploader',
                    dbTable : 'T_CORE_RISK_IDENTIFICA_REGIST',
                    bizId : 0,
                    onBeforeDelete : function() {
                        var rows = this.grid.data;
                        var canDelete = true;
                        Gikam.each(rows, function(index, row) {
                            if (workspace.user.id != row.createdById) {
                                canDelete = false;
                                Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                                return;
                            }
                        })
                        return canDelete;
                    }
                } ]
            } ]
        }
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'layout',
            west : {
                width : '50%',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ _this.getRegistDataGrid() ]
                    },
                    center : {
                        items : [ _this.getAttachment() ]
                    }
                } ]
            },
            center : {
                items : [ _this.getRegistFactorDataGrid() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },
};
