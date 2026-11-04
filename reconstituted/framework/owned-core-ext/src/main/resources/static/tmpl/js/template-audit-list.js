${data.jsObjectName}.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : '${data.jsFileName}-audit-list-grid',
            url : ${data.jsObjectName}.baseUrl + '/queries/tasks',
            columns : ${data.jsObjectName}.getAuditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
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

    pass : function() {
        var grid = Gikam.getComp('${data.jsFileName}-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : ${data.jsObjectName}
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('${data.jsFileName}-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : ${data.jsObjectName}
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(${data.jsObjectName}.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : '${data.jsFileName}-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
