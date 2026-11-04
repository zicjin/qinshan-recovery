coreScheduleTask.editListPage = {
    getTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-schedule-task-list-grid',
            url : coreScheduleTask.baseUrl + '/queries/searchable',
            requestData : {
                type_SEQ : _this.param.type
            },
            columns : coreScheduleTask.getSimpleSearchListColumns(),
            checkOnActive : false,
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getAsyncComp('core-schedule-task-config-list-grid').done(function (form) {
                        form.refresh({
                            url : _this.getTaskConfigUrl(row.id),
                        });
                    });
                }
            },
            onSelect : function(row, $row) {
                var configListGrid = Gikam.getComp('core-schedule-task-config-list-grid');

                configListGrid.setOptions({
                    url : _this.getTaskConfigUrl(row.id)
                });
                configListGrid.refresh();
            },
            onCellClick : function(field, row) {
                field === 'taskName' && _this.loadTaskDetailPage(row.id);
            }
        }
    },

    getTaskConfigGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-schedule-task-config-list-grid',
            url : _this.getTaskConfigUrl(),
            columns : coreScheduleTask.getConfigListColumns(),
            onCellClick : function(field, row) {
                field === 'uid' && _this.loadTaskConfigDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addConfig();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.removeConfig();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activateConfig();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.SCHEDULE_TASKS.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.deactivateConfig();
                }
            } ]
        }
    },

    getTaskConfigBtnToolbarParam : function() {
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
                        _this.addConfig();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        _this.removeConfig();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.ACTIVE',
                    icon : 'enable',
                    onClick : function() {
                        _this.activateConfig();
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.SYS.SCHEDULE_TASKS.BUTTON.DEACTIVE',
                    icon : 'disable',
                    onClick : function() {
                        _this.deactivateConfig();
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;

        if (_this.param.type === 'service') {
            Gikam.create('layout', {
                west : {
                    width : 310,
                    items : [ _this.getTaskGridParam() ]
                },
                center : {
                    items : [ _this.getTaskConfigGridParam() ]
                },
                renderTo : workspace.window.$dom,
            });
        } else {
            Gikam.create('layout', {
                west : {
                    width : 310,
                    items : [ _this.getTaskGridParam() ]
                },
                center : {
                    items : [ _this.getTaskConfigGridParam() ]
                },
                renderTo : workspace.window.$dom,
            });
        }
    },

    loadTaskDetailPage : function(id) {
        if (id) {
            workspace.window.load(IFM_CONTEXT + '/secure/core/module/sys/schedule-tasks/page/detail' + Gikam.param({
                id : id
            }));
        }
    },

    loadTaskConfigDetailPage : function(id) {
        if (id) {
            workspace.window.load(IFM_CONTEXT + '/secure/core/module/sys/schedule-task-configs/page/detail' + Gikam.param({
                id : id
            }));
        }
    },

    getTaskConfigUrl : function(taskId) {
        if (taskId) {
            return Gikam.printf(coreScheduleTask.baseUrl + '/{id}/schedule-task-configs/queries', {
                id : taskId
            })
        }
    },

    addConfig : function() {
        var selectedTaskRows = Gikam.getComp('core-schedule-task-list-grid').getSelections();
        if (selectedTaskRows.length === 0) {
            Gikam.alert('CORE.MODULE.SYS.SCHEDULE_TASKS.TIP.CHOOSE_SCHEDULE_TASK');
            return;
        }

        var _this = this;

        var insertConfigUrl = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/schedule-tasks/{id}/schedule-task-configs', {
            id : selectedTaskRows[0].id
        });

        Gikam.postText(insertConfigUrl, Gikam.getJsonWrapper(null, [ 'coreScheduleTaskConfigServiceImpl', [ {} ] ])).done(function(id) {
            _this.loadTaskConfigDetailPage(id);
        }).always(function() {
            workspace.window.closeMask();
        })
    },

    removeConfig : function() {
        var selectedTaskConfigRows = Gikam.getComp('core-schedule-task-config-list-grid').getSelections();
        if (selectedTaskConfigRows.length === 0) {
            Gikam.alert('CORE.MODULE.SYS.SCHEDULE_TASKS.TIP.CHOOSE_SCHEDULE_TASK_CONFIG');
            return;
        }

        valid = true;
        selectedTaskConfigRows.forEach(function(row) {
            if (row.activatedFlag === '1') {
                valid = false;
            }
        });
        if (!valid) {
            Gikam.alert('CORE.MODULE.SYS.SCHEDULE_TASKS.TIP.CHOOSE_NOT_ACTIVE_CONFIG');
            return;
        }
        ;

        Gikam.getComp('core-schedule-task-config-list-grid').deleteRows(IFM_CONTEXT + '/secure/core/module/sys/schedule-task-configs');
    },

    activateConfig : function() {
        if(Gikam.getComp('core-schedule-task-config-list-grid').validate(Gikam.getComp('core-schedule-task-config-list-grid').getSelections())) {
            Gikam.getComp('core-schedule-task-config-list-grid').activateRows(IFM_CONTEXT + '/secure/core/module/sys/schedule-task-configs/action/activate');
        }
    },

    deactivateConfig : function() {
        Gikam.getComp('core-schedule-task-config-list-grid').deactivateRows(IFM_CONTEXT + '/secure/core/module/sys/schedule-task-configs/action/deactivate');
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};