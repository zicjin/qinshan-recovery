emcConvenMtl.checkPage = {

    getGridParam : function() {
        var _this = this, columns = _this.param.bpmn_statusCode === 'check' ? emcConvenMtl.getCheckListColumns() : emcConvenMtl.getAuditListColumns();
        return {
            type : 'grid',
            id : 'emc-conven-mtl-check-list-grid',
            url : emcConvenMtl.baseUrl + '/queries/tasks',
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
            id : 'emc-conven-mtl-check-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.CONVENMTLS.MODAL.CONVEN_MTL',
            width : Gikam.width.XL,
            height : 460,
            url : emcConvenMtl.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-conven-mtl-check-list-grid').refresh();
            }
        });
    },

    pass : function() {
        var grid = Gikam.getComp('emc-conven-mtl-check-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcConvenMtl
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-conven-mtl-check-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcConvenMtl
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
