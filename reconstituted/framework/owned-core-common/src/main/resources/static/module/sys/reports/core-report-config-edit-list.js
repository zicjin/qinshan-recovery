coreReportConfig.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-report-config-edit-list-grid',
            url : coreReportConfig.baseUrl + '/queries/raw',
            showSortArrow: true,
            columns : coreReportConfig.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.REPORTS.TAB.ADD_REPORT_INFO',
                        fields : coreReportConfig.getBaseInfoFormFields(),
                        url : coreReportConfig.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-report-config-edit-list-grid').deleteRows(coreReportConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE_UP',
                icon : 'move-up',
                onClick : function() {
                    Gikam.getComp('core-report-config-edit-list-grid').moveUp(coreReportConfig.baseUrl + '/action/swap/{firstId}/{secondId}')
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE_DOWN',
                icon : 'move-down',
                onClick : function() {
                    Gikam.getComp('core-report-config-edit-list-grid').moveDown(coreReportConfig.baseUrl + '/action/swap/{firstId}/{secondId}')
                }
            } ]
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.preInsert({
                            modalTitle : 'CORE.MODULE.SYS.REPORTS.TAB.ADD_REPORT_INFO',
                            fields : coreReportConfig.getBaseInfoFormFields(),
                            url : coreReportConfig.baseUrl,
                        }).done(function(id) {
                            _this.loadDetailPage(id);
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('core-report-config-edit-list-grid').deleteRows(coreReportConfig.baseUrl);
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-report-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreReportConfig.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};