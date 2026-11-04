coreLesTemplate.auditListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-les-template-audit-list-grid',
            url : coreLesTemplate.baseUrl + '/queries/tasks',
            columns : coreLesTemplate.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'templateNo' && _this.loadDetailPage(row.id);
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
        var grid = Gikam.getComp('core-les-template-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreLesTemplate
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-les-template-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreLesTemplate
        }).done(function() {
            grid.refresh();
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreLesTemplate.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            readonly : true
        }));

    },

    init : function() {
        this.param = {};
        this.create();
    }
};