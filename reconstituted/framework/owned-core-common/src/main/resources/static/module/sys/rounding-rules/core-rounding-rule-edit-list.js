coreRoundingRule.editListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-rounding-rule-list-grid',
            url : coreRoundingRule.baseUrl + '/queries/raw',
            columns : coreRoundingRule.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            suspendFormatter : function(row) {
                return row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.ROUNDING_RULES.TITLE.ADD_ROUNDING_RULES',
                        fields : coreRoundingRule.getBaseInfoFormFields(),
                        url : coreRoundingRule.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var listGrid = Gikam.getComp('core-rounding-rule-list-grid');
                    if (listGrid.getSelections().filter(function(row) {
                        row.locked === '1'
                    }).length == 0) {
                        listGrid.deleteRows(coreRoundingRule.baseUrl);
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.UNSUSPEND',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-rounding-rule-list-grid').unsuspendRows(coreRoundingRule.baseUrl + '/action/unsuspend');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUSPEND',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-rounding-rule-list-grid').suspendRows(coreRoundingRule.baseUrl + '/action/suspend');
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreRoundingRule.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};