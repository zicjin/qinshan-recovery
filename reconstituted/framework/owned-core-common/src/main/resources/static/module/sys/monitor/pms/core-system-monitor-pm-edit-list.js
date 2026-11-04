coreSystemMonitorPm.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-system-monitor-pm-sql-edit-list-grid',
            url : coreSystemMonitorPm.baseUrl + '/queries/raw',
            service : 'coreSystemMonitorSqlServiceImpl',
            dbTable : 'T_CORE_SYSTEM_MONITOR_SQL',
            deleteFormatter : function(row) {
                return row.sqlSource || row.id;
            },
            requestData : {
                revisionFlag_SEQ : '0'
            },
            columns : coreSystemMonitorPm.getEditListColumns(true),
            onBeforeLoad : function(data) {
                data.forEach(function(item) {
                    item.viewSql = Gikam.propI18N('CORE.MODULE.SYS.MONITOR.PMS.SQL.BUTTON.VIEWSQL');
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.MONITOR.PMS.SQL.BUTTON.REVISE',
                icon : 'modify',
                onClick : function() {
                    _this.revise('1')
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-system-monitor-pm-sql-edit-list-grid').deleteRows(coreSystemMonitorPm.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.CHEM.BUTTON.CLEAR',
                icon : 'remove-row',
                onClick : function() {
                    _this.clean(Gikam.getComp('core-system-monitor-pm-sql-edit-list-grid'))
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.MONITOR.PMS.SQL.BUTTON.REVISEDSQL',
                icon : 'copy',
                onClick : function() {
                    _this.revisedSql(Gikam.getComp('core-system-monitor-pm-sql-edit-list-grid'))
                }
            }]
        }
    },

    revise : function (){
        var selectedRows = Gikam.getComp('core-system-monitor-pm-sql-edit-list-grid').getSelections();
        if (selectedRows.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('confirm', {
            message : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.PMS.SQL.TIPS.CONFIRM_REVISE'),
            onYesClick : function() {
                Gikam.put(coreSystemMonitorPm.baseUrl + '/action/revise', Gikam.getJsonWrapper(null, [ 'CoreSystemMonitorSqlServiceImpl', selectedRows ])).done(function() {
                    Gikam.getComp('core-system-monitor-pm-sql-edit-list-grid').refresh();
                }).always(function() {
                });
            }
        });
    },

    cancelRevise : function (){
        var selectedRows = Gikam.getComp('core-system-monitor-pm-sql-modify-list-grid').getSelections();
        if (selectedRows.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('confirm', {
            message : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.PMS.SQL.TIPS.CANCEL_REVISE'),
            onYesClick : function() {
                Gikam.put(coreSystemMonitorPm.baseUrl + '/action/cancel-revise', Gikam.getJsonWrapper(null, [ 'CoreSystemMonitorSqlServiceImpl', selectedRows ])).done(function() {
                    Gikam.getComp('core-system-monitor-pm-sql-modify-list-grid').refresh();
                }).always(function() {
                });
            }
        });
    },

    clean : function (getComp){
        Gikam.create('confirm', {
            message : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.PMS.SQL.TIPS.CLEAN_SQL'),
            onYesClick : function() {
                Gikam.del(coreSystemMonitorPm.baseUrl + '/action/empty',null).done(function (){
                    getComp.refresh();
                })
            }
        });
    },

    viewSql : function (index,revisionFlag) {

        var data = '';
        if(revisionFlag === 1){
            data = Gikam.getComp('core-system-monitor-pm-sql-modify-list-grid').getData()[index].sql;
        }else {
            data = Gikam.getComp('core-system-monitor-pm-sql-edit-list-grid').getData()[index].sql;
        }
        var modal = Gikam.create('modal', {
            id : 'modal',
            title : 'T_CORE_SYSTEM_MONITOR_SQL.SQL',
            width : 800,
            height : 300
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            north : {
                height : 5,
                items : [{
                    type : 'btnToolbar',
                    items : [{
                        type : 'button',
                        text : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.PMS.SQL.BUTTON.COPYSQL'),
                        icon : 'default',
                        onClick : function() {
                            Gikam.copyText(data);
                        }
                    }]
                }]
            },
            center : {
                items : [{
                    type: 'form',
                    id: 'core-system-monitor-pm-sql-edit-list-form',
                    fields : [{
                        field : 'id',
                        renderType:'html',
                        readonly : true,
                        width : '100%',
                        value : data,
                        textStyleFormatter : function () {
                            return {
                                border: 'none',
                                background: 'none',
                                width: '100%'
                            }
                        }
                    }]

                }]
            }
        })
    },

    revisedSql : function (getComp){
        var _this = this;
        var modal = Gikam.create('modal', {
            id : 'modal',
            title : 'CORE.MODULE.SYS.MONITOR.PMS.SQL.TITLE.REVISED',
            width : 1000,
            height : 500,
            toolbarTop : true,
            onAfterClose : function (){
                getComp.refresh();
            }
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [{
                    type: 'grid',
                    id: 'core-system-monitor-pm-sql-modify-list-grid',
                    url: coreSystemMonitorPm.baseUrl + '/queries/raw',
                    service: 'coreSystemMonitorSqlServiceImpl',
                    dbTable: 'T_CORE_SYSTEM_MONITOR_SQL',
                    requestData: {
                        revisionFlag_SEQ: '1'
                    },
                    columns: coreSystemMonitorPm.getEditListColumns(false),
                    toolbar: [{
                        type: 'button',
                        text: 'CORE.MODULE.SYS.MONITOR.PMS.SQL.BUTTON.CANCEL_REVISE',
                        icon: 'modify',
                        onClick: function () {
                            _this.cancelRevise();
                        }
                    }]
                }]
            }
        })
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-system-monitor-pm-sql-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [{
                    type: 'tab',
                    panels: [{
                        title: 'CORE.MODULE.SYS.MONITOR.PMS.SQL.TAB.BASE_INFO',
                        items: [this.getGridParam()]
                    }]
                }]
            }
        });
        !window.viewSql && (window.viewSql = this.viewSql);
    },

    init : function() {
        this.create();
        workspace.window.removeTitle();
    }
};
