coreScheduleTaskConfig.searchListPage = {
    getTaskConfigGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-schedule-task-config-list-grid',
            url : coreScheduleTaskConfig.baseUrl + '/queries/searchable',
            columns : coreScheduleTaskConfig.getSimpleSearchListColumns(),
            checkOnActive : false,
            genericQueryFields : [ {
                field : 'ext$.taskname'
            }, {
                field : 'configName'
            } ],
            onRowActive : function(index, row) {
                if (row) {
                    _this.selectedTaskId = row.id;
                    Gikam.getAsyncComp('core-schedule-task-log-list-grid').done(function (form) {
                        form.refresh({
                            url : _this.getTaskLogUrl(),
                        });
                    });
                }
            },

            onSelect : function(row, $row) {
                _this.selectedTaskId = row.id;
                var logListGrid = Gikam.getComp('core-schedule-task-log-list-grid');
                logListGrid.setOptions({url:_this.getTaskLogUrl()});
                logListGrid.refresh();
            },
            onCellClick : function(field, row) {
                field === 'ext$.taskname' && _this.loadConfigDetailPage(row.id);
            }
        }
    },

    getTaskLogParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-schedule-task-log-list-grid',
            url : _this.getTaskLogUrl(),
            columns : coreScheduleTaskConfig.getTaskLogSearchListColumns(),
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'success'
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadTaskLogDetailPage(row.taskId, row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            west : {
                width : 350,
                items : [ _this.getTaskConfigGridParam() ]
            },
            center : {
                items : [ _this.getTaskLogParam() ]
            },
            renderTo : workspace.window.$dom,
        });
    },

    getTaskLogUrl : function() {
        if (this.selectedTaskId) {
            return Gikam.printf(coreScheduleTaskConfig.baseUrl + '/{id}/schedule-task-logs/queries', {
                id : this.selectedTaskId
            })
        }
    },

    loadConfigDetailPage : function(id) {
        if (id) {
            workspace.window.load(coreScheduleTaskConfig.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }));
        }
    },

    loadTaskLogDetailPage : function(taskId, id) {
        if (id) {
            workspace.window.load(IFM_CONTEXT + '/secure/core/module/sys/schedule-task-logs/page/detail' + Gikam.param({
                id : id
            }));
        }
    },

    init : function() {
        this.create();
    }
};