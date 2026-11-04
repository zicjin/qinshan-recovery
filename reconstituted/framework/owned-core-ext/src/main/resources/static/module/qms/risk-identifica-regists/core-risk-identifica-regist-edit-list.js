coreRiskIdentificaRegist.editPage = {

    getDataGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-identifica-regist-edit-list-grid',
            type : 'grid',
            renderTo : workspace.window.$dom,
            columns : coreRiskIdentificaRegist.getEditListColumns(),
            url : coreRiskIdentificaRegist.baseUrl + '/queries/raw',
            requestData : {
                processStatus_IN : 'draft,draft_rejected'
            },
            deleteFormatter : function(row) {
                return row.riskNo;
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('core-risk-identifica-regist-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            onCellClick : function(field, row) {
                field === 'riskNo' && coreRiskIdentificaRegist.loadDetailPage(row.id, 'edit');
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('core-risk-identifica-regist-edit-list-uploader').grid.cleanData();
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.postSync(coreRiskIdentificaRegist.baseUrl, Gikam.getJsonWrapper({}, [ '', [ {} ] ])).done(function(item) {
                        coreRiskIdentificaRegist.loadDetailPage(item, 'edit');
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-risk-identifica-regist-edit-list-grid').deleteRows(coreRiskIdentificaRegist.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-risk-identifica-regist-edit-list-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_RISK_CONTROL', {
                        id : rows[0].id
                    });
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-risk-identifica-regist-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreRiskIdentificaRegist
        }).done(function() {
            grid.refresh();
        });
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-risk-identifica-regist-edit-list-uploader',
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-identifica-regist-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '60%',
                items : [ _this.getDataGrid() ]
            },
            center : {
                items : [ _this.getAttachment() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }

}