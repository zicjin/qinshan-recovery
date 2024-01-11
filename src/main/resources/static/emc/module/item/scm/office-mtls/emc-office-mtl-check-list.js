emcOfficeMtl.checkPage = {

    getGridParam : function() {
        var _this = this, columns = _this.param.bpmn_statusCode === 'check' ? emcOfficeMtl.getCheckListColumns() : emcOfficeMtl.getAuditListColumns();
        return {
            type : 'grid',
            id : 'emc-office-mtl-check-list-grid',
            url : emcOfficeMtl.baseUrl + '/queries/tasks',
            requestData : _this.param,
            columns : columns,
            onCellClick : function(field, row) {
                field === 'mtlName' && _this.loadDetailPage(row.id);
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-office-mtl-check-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.OFFICEMTLS.MODAL.OFFICE_MTL',
            width : Gikam.width.XL,
            height : 460,
            url : emcOfficeMtl.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-office-mtl-check-list-grid').refresh();
            }
        });
    },

    pass : function() {
        var grid = Gikam.getComp('emc-office-mtl-check-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcOfficeMtl
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-office-mtl-check-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcOfficeMtl
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
