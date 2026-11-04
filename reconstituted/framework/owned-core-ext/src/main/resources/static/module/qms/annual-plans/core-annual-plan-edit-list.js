coreAnnualPlan.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-annual-plan-edit-list-grid',
            url : coreAnnualPlan.baseUrl + '/queries/raw',
            columns : coreAnnualPlan.getEditListColumns(),
            activateFirstRow : true,
            deleteFormatter : function(row) {
                return row.annualPlanNo;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('qms-annual-plan-edit-list-uploader').grid.cleanData();
                }
            },
            onCellClick : function(field, row) {
                field === 'annualPlanNo' && _this.loadDetailPage(row.id);
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-annual-plan-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'QMS.MODULE.ANNUALPLAN.MODAL.ADD',
                        fields : coreAnnualPlan.getAnnualPlanBaseInfoFormFields(),
                        url : coreAnnualPlan.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-annual-plan-edit-list-grid').deleteRows(coreAnnualPlan.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('core-annual-plan-edit-list-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : coreAnnualPlan
                    }).done(function() {
                        grid.refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var comp = Gikam.getComp('core-annual-plan-edit-list-grid');
                    if (comp.getSelections().length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.COPY.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    var annualPlanId = comp.getSelections()[0].id;
                    Gikam.post(Gikam.printf(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/{id}/action/copy', {
                        id : annualPlanId
                    }), Gikam.getJsonWrapper({}, [ '', comp.getSelections() ])).done(function(copyId) {
                        _this.loadDetailPage(copyId);
                    })
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-annual-plan-edit-list-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    coreAnnualPlan.printReport(rows[0].id);
                }
            } ]
        }
    },
    
    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-annual-plan-edit-list-uploader',
                    dbTable : 'T_CORE_ANNUAL_PLAN',
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
    
    loadDetailPage : function(id) {
        var planCategory ;
        Gikam.getJson(Gikam.printf(coreAnnualPlan.baseUrl + '/{id}', {
            id : id
        })).done(function(item) {
            planCategory = item.planCategory;
            var sourcePage = planCategory == 'dailySupervise' ? 'edit_dailysupervision' : 'edit';
            workspace.window.load(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/page/detail' + Gikam.param({
                id : id,
                sourcePage : sourcePage
            }));
        })
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-annual-plan-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '60%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getAttachment() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
