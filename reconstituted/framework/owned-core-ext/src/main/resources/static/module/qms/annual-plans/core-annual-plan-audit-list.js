coreAnnualPlan.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-annual-plan-audit-list-grid',
            url : coreAnnualPlan.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            activateFirstRow : true,
            columns : coreAnnualPlan.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'annualPlanNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    _this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('qms-annual-plan-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-annual-plan-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var comp = Gikam.getComp('core-annual-plan-audit-list-grid');
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
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-annual-plan-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreAnnualPlan
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-annual-plan-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreAnnualPlan
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
                    id : 'qms-annual-plan-audit-list-uploader',
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
        workspace.window.load(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'review'
        }));
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-annual-plan-audit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '60%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getAttachment() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
