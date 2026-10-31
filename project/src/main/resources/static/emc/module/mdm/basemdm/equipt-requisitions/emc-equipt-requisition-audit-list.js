emcEquiptRequisition.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-requisition-audit-list-grid',
            url : emcEquiptRequisition.baseUrl + '/queries/tasks',
            columns : emcEquiptRequisition.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'ext$.equiptcode' && _this.loadDetailPage(row.id);
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-equipt-requisition-audit-list-grid').done(function(grid) {
                    Gikam.getComp('emc-equipt-requisition-audit-list-uploader').setOptions({
                        bizId : row.id,
                        readonly : true
                    });
                });
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-equipt-requisition-audit-list-grid').activeRowByIndex(0);
                } else if(Gikam.getComp('emc-equipt-requisition-audit-list-uploader')) {
                    Gikam.getComp('emc-equipt-requisition-audit-list-uploader').grid.cleanData();
                }
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
            } ]
        }
    },

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-equipt-requisition-audit-list-uploader',
            dbTable : 'T_EMC_EQUIPT_REQUISITION',
            readonly : true
        }
    },
    
    pass : function() {
        var grid = Gikam.getComp('emc-equipt-requisition-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcEquiptRequisition
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-equipt-requisition-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcEquiptRequisition
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.DETAIL',
            url : emcEquiptRequisition.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                operType : 'audit'
            }),
            width : 900,
            height : 230,
            onAfterClose : function() {
                Gikam.getComp('emc-equipt-requisition-audit-list-grid').refresh();
            }
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-requisition-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            },
            south : {
                height : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },
    
    init : function() {
        this.create();
    }
};
