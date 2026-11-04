coreDailySupervision.editPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'core-daily-supervision-edit-list-grid',
            url : coreDailySupervision.baseUrl + '/queries/raw',
            columns : coreDailySupervision.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.dailyNo;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-daily-supervision-edit-list-base-info-form', 'core-daily-supervision-edit-list-supervision-item-grid', 'core-daily-supervision-edit-list-supervision-con-grid' ]);
                    Gikam.getComp('qms-daily-supervision-edit-list-uploader').grid.cleanData();
                    Gikam.getComp('qms-daily-supervision-edit-list-uploader').grid.setOptions({
                        toolbar : []
                    });
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-daily-supervision-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('core-daily-supervision-edit-list-supervision-item-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-items/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('qms-daily-supervision-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'MODULE.QMS.DAILYSUPERVISIONS.MODAL.ADD',
                        titleWidth : 110,
                        fields : coreDailySupervision.getInsertDailySupervisionFields(),
                        url : coreDailySupervision.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-daily-supervision-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-daily-supervision-edit-list-grid').deleteRows(coreDailySupervision.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('core-daily-supervision-edit-list-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : coreDailySupervision
                    }).done(function() {
                        grid.refresh();
                    });
                }
            } ]
        }
    },

    getSupervisionTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'MODULE.QMS.DAILYSUPERVISIONS.TAB.BASE_INFO',
                items : [ _this.getSupervisionItemGrid(), _this.getSupervisionConGrid(), {
                    type : 'form',
                    service : 'coreDailySupervisionServiceImpl',
                    id : 'core-daily-supervision-edit-list-base-info-form',
                    fields : coreDailySupervision.getBaseInfoFormFields(),
                    columns : 1,
                    titleWidth : 30
                } ]
            }, _this.getAttachment() ]
        }
    },

    getAttachment : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'qms-daily-supervision-edit-list-uploader',
                dbTable : 'T_CORE_DAILY_SUPERVISION',
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
        }
    },

    getSupervisionItemGrid : function() {
        return {
            type : 'grid',
            height : 220,
            id : 'core-daily-supervision-edit-list-supervision-item-grid',
            service : 'coreDailySupervisionItemServiceImpl',
            page : false,
            deleteFormatter : function(row) {
                return coreDailySupervision.getAuditNameByCode(row.auditName);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-daily-supervision-edit-list-supervision-con-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-daily-supervision-edit-list-supervision-con-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-cons/queries', {
                            id : row.id
                        })
                    }).done(function(item) {
                        
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'MODULE.QMS.DAILYSUPERVISIONS.MODAL.ADD',
                        id : 'core-daily-supervision-model-add-item',
                        fields : coreDailySupervision.getInsertDailySupervisionFields(),
                        url : Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-items', {
                            id : Gikam.getComp('core-daily-supervision-edit-list-grid').getActivedRow().id
                        }),
                    }).done(function(id) {
                        Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').deleteRows(Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-items', {
                        id : Gikam.getComp('core-daily-supervision-edit-list-grid').getActivedRow().id
                    }));
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_DAILY_SUPERVISION', {
                        id : rows[0].id
                    });
                }
            } ],
            columns : coreDailySupervision.getSupervisionItemListColumns()
        };
    },

    getSupervisionConGrid : function() {
        return {
            type : 'grid',
            height : 220,
            id : 'core-daily-supervision-edit-list-supervision-con-grid',
            service : 'coreDailySupervisionConServiceImpl',
            page : false,
            deleteFormatter : function(row) {
                return row.auditContent;
            },
            onLoadSuccess  : function(rows) {
                var con = Gikam.getComp('core-daily-supervision-edit-list-supervision-con-grid');
                Gikam.each(rows, function(index, row) {
                    con.toFieldsEdit(index, ['auditName', 'auditContent']);
                    con.toFieldsReadonly(index, ['auditResult', 'remark']);
                })
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.getComp('core-daily-supervision-edit-list-supervision-con-grid').insert(Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-cons', {
                        id : Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').getActivedRow().id
                    }), [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-daily-supervision-edit-list-supervision-con-grid').deleteRows(Gikam.printf(coreDailySupervision.baseUrl + '/{id}/supervision-cons', {
                        id : Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').getActivedRow().id
                    }));
                }
            } ],
            columns : coreDailySupervision.getSupervisionConListColumns()
        };
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-daily-supervision-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '35%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getSupervisionTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
