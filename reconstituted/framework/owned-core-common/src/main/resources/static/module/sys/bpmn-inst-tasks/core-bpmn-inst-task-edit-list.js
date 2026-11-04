coreBpmnInstTask.editListPage = {
    getSearchListParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-bpmn-inst-task-search-list-grid',
            filterOpen : false,
            page : false,
            url : coreBpmnInstTask.baseUrl + '/queries',
            columns : coreBpmnInstTask.getSearchListColumns(),
            checkOnActive : true,
            genericQuery : false,
            filter : false,
            onRendered : function() {
            },
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
               var instTaskId = row.id;
               
               _this.instTaskId = instTaskId;
               
               if (!Gikam.isEmpty(instTaskId)) {
                   Gikam.getComp('core-bpmn-inst-task-user-list-grid').refresh({
                       url : coreBpmnInstTask.baseUrl + '/' + instTaskId + '/bpmn-inst-task-users/queries'
                   });
                   
                   Gikam.getComp('core-bpmn-inst-task-role-list-grid').refresh({
                       url : coreBpmnInstTask.baseUrl + '/' + instTaskId + '/bpmn-inst-task-roles/queries'
                   });
               }
            },
            onBeforeRefresh : function(requestData) {
            }
        }
    },

    getInstTaskUserTab : function() {
        var _this = this;
        return {
            id : 'core-bpmn-inst-task-user-list-grid',
            type : 'grid',
            filter : false,
            checkOnActive : false,
            page : false,
            columns : coreBpmnInstTask.getTaskUserListColumns(),
            onRendered : function() {
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.INSERT_AUDITOR',
                icon : 'add',
                onClick : function() {
                    if (_this.instTaskId) {
                        _this.insertAuditor();
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.DELETE_AUDITOR',
                icon : 'remove',
                onClick : function() {
                    if (_this.instTaskId) {
                        _this.deleteAuditor();
                    }
                }
            } ]
        }
    },
    
    insertAuditor : function() {
        var _this = this;
        
        var filterJson = JSON.stringify({
            type : 'unassigned',
            targetTable : 'T_CORE_BPMN_INST_TASK_USER',
            targetMatchColumn : 'USERID',
            filter : [{
                targetFilterColumn : 'INSTTASKID',
                targetFilterValue : this.instTaskId
            }]
        });
        
        Gikam.create('modal', {
            title : 'CORE.MODULE.MDM.USERS.MODAL.CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + Gikam.param({target_filter : encodeURI(filterJson)}),
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            userId : item.id
                        }
                    });
                    
                    var insertAuditorUrl = Gikam.printf(coreBpmnInstTask.baseUrl + '/{id}/bpmn-inst-task-users', {
                        id : _this.instTaskId
                    });
                    
                    Gikam.postText(insertAuditorUrl, Gikam.getJsonWrapper(null, ['', chooseData])).done(function() {
                        Gikam.getComp('core-bpmn-inst-task-user-list-grid').refresh();
                    });
                }
            }
        });
    },
    
    deleteAuditor : function() {
        var _this = this;
        
        var grid = Gikam.getComp('core-bpmn-inst-task-user-list-grid');
        
        var selectedRows = grid.getSelections();
        if (selectedRows.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        
        var deleteAuditorUrl = Gikam.printf(coreBpmnInstTask.baseUrl + '/{id}/bpmn-inst-task-users', {
            id : _this.instTaskId
        });
        
        Gikam.del(deleteAuditorUrl, Gikam.getJsonWrapper(null, [null, selectedRows])).done(function() {
            Gikam.getComp('core-bpmn-inst-task-user-list-grid').refresh();
        });
    },
    
    getInstTaskRoleTab : function() {
        var _this = this;
        return {
            id : 'core-bpmn-inst-task-role-list-grid',
            type : 'grid',
            filter : false,
            checkOnActive : false,
            page : false,
            columns : coreBpmnInstTask.getTaskRoleListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.INSERT_ROLE',
                icon : 'add',
                onClick : function() {
                    if (_this.instTaskId) {
                        _this.insertRole();
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.DELETE_ROLE',
                icon : 'remove',
                onClick : function() {
                    if (_this.instTaskId) {
                        _this.deleteRole();
                    }
                }
            } ]
        }
    },

    insertRole : function() {
        var _this = this;
        
        var json = Gikam.printf("{'type':'unassigned','targetTable':'T_CORE_BPMN_INST_TASK_ROLE','targetMatchColumn':'ROLEID','filter':[{'targetFilterColumn':'INSTTASKID','targetFilterValue':{id}}]}", {
            id : _this.instTaskId
        });
        
        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.ROLES.MODAL.CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + '?target_filter=' + encodeURI(json),
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            roleId : item.id
                        }
                    });
                    
                    var insertRoleUrl = Gikam.printf(coreBpmnInstTask.baseUrl + '/{id}/bpmn-inst-task-roles', {
                        id : _this.instTaskId
                    });
                    
                    Gikam.postText(insertRoleUrl, Gikam.getJsonWrapper(null, ['', chooseData])).done(function() {
                        Gikam.getComp('core-bpmn-inst-task-role-list-grid').refresh();
                    });
                }
            }
        });
    },
    
    deleteRole : function() {
        var _this = this;
        
        var grid = Gikam.getComp('core-bpmn-inst-task-role-list-grid');
        
        var selectedRows = grid.getSelections();
        if (selectedRows.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        
        var deleteRoleUrl = Gikam.printf(coreBpmnInstTask.baseUrl + '/{id}/bpmn-inst-task-roles', {
            id : _this.instTaskId
        });
        
        Gikam.del(deleteRoleUrl, Gikam.getJsonWrapper(null, [null, selectedRows])).done(function() {
            Gikam.getComp('core-bpmn-inst-task-role-list-grid').refresh();
        });
    },
    
    create : function() {
        var $window = Gikam.getLastModal().window;
        if (Gikam.isEmpty($window)) {
            $window = workspace.window;
        }
        
        var _this = this;
        Gikam.create('layout', {
            id : 'core-bpmn-inst-task-search-list-layout',
            renderTo : $window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : "40%",
                        items : [ _this.getSearchListParam() ]
                    },
                    center : {
                        width : '30%',
                        items : [ _this.getInstTaskUserTab() ]
                    },
                    east : {
                        width : '30%',
                        items : [ _this.getInstTaskRoleTab() ]
                    }
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
