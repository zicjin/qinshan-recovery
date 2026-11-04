coreRiskIdentificaRegist.executePage = {
    getToolbarButtons : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
            icon : 'submit',
            onClick : function() {
                if (Gikam.getComp('core-risk-execute-edit-list-factor-grid').validate()) {
                    _this.pass();
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
                var rows = Gikam.getComp('core-risk-execute-edit-list-regist-grid').getSelections();
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

    pass : function() {
        var grid = Gikam.getComp('core-risk-execute-edit-list-regist-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-execute-edit-list-factor-grid').cleanData();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-risk-execute-edit-list-regist-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
            Gikam.getComp('core-risk-execute-edit-list-factor-grid').cleanData();
        });
    },

    getRegistDataGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-execute-edit-list-regist-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getExecuteColumns(),
            toolbar : _this.getToolbarButtons(),
            url : coreRiskIdentificaRegist.baseUrl + '/queries/tasks',
            activateFirstRow : true,
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            onRowActive : function(index, row) {
                Gikam.getComp('core-risk-execute-edit-list-factor-grid').refresh({
                    url : Gikam.printf(coreRiskIdentificaRegist.registFactorBaseUrl + '/queries', {
                        id : row.id
                    })
                });
                Gikam.getAsyncComp('core-risk-execute-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            }
        }
    },

    getRegistFactorDataGrid : function() {
        return {
            id : 'core-risk-execute-edit-list-factor-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getExecuteRegistFactorColumns(),
            checkOnActive : false,
            autoSave : true,
            service : 'coreRiskIdentificaFactorServiceImpl',
        }
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-risk-execute-edit-list-uploader',
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
            id : 'core-risk-execute-edit-list-layout',
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
