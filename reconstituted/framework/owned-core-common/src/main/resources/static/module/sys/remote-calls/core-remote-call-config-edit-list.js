//@ sourceURL=core-remote-call-config-edit-list.js
coreRemoteCallConfig.editPage = {

    getRemoteCallConfigEditList : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-remote-call-config-edit-list-grid',
            url : coreRemoteCallConfig.baseUrl + '/queries',
            columns : coreRemoteCallConfig.getRemoteCallConfigListColumns(),
            checkOnActive : true,
            onLoadSuccess : function(data) {
                if (Gikam.isNotEmpty(data) && data.length > 0) {
                    this.activeRowByIndex(0);
                } else if (Gikam.isEmpty(data) && Gikam.isNotEmpty(Gikam.getComp('core-remote-call-config-edit-list-request-grid'))) {
                    Gikam.getComp('core-remote-call-config-edit-list-request-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.refreshGrids({
                    'core-remote-call-config-edit-list-request-grid' : coreRemoteCallConfig.baseUrl + '/configs/{id}/requests/queries',
                }, {
                    id : row.id
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.REMOTE_CALLS.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activate();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.REMOTE_CALLS.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.deactivate();
                }
            } ],
        }
    },
    getRemoteCallRequestEditList : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-remote-call-config-edit-list-request-grid',
            columns : coreRemoteCallConfig.getRemoteCallRequestListColumns(),
            checkOnActive : true,
            onLoadSuccess : function(data) {
                if (Gikam.isNotEmpty(data) && data.length > 0) {
                    this.activeRowByIndex(0);
                } else if (Gikam.isEmpty(data) && Gikam.isNotEmpty(Gikam.getComp('core-remote-call-config-edit-list-response-grid'))) {
                    Gikam.getComp('core-remote-call-config-edit-list-response-grid').cleanData();
                }
            },
            requestData : {
                requestDateTime_DEQ : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd')
            },
            onRowActive : function(index, row) {
                Gikam.refreshGrids({
                    'core-remote-call-config-edit-list-response-grid' : coreRemoteCallConfig.baseUrl + '/configs/requests/{id}/responses/queries',
                }, {
                    id : row.id
                });
            },
            toolbar : [ {
                type : 'form',
                fields : [ {
                    field : 'requestDateTime_DEQ',
                    type : 'date',
                    value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd')
                } ]
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.REMOTE_CALLS.BUTTON.RESET',
                icon : 'refresh',
                onClick : function() {
                    _this.resetRequest();
                }
            } ],
            onCellClick : function(field, row) {
                field === 'requestData' && _this.loadRequestDataDetailPage(row.id);
            },
        }
    },
    getRemoteCallResponseEditList : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-remote-call-config-edit-list-response-grid',
            onCellClick : function(field, row) {
                field === 'log' && _this.loadReponseLogPage(row.log);
            },
            onRendered : function() {

            },
            columns : coreRemoteCallConfig.getRemoteCallResponseListColumns(),
        }
    },

    loadReponseLogPage : function(log) {

        var modal = Gikam.create('modal', {
            title : 'T_CORE_REMOTE_CALL_RESP.LOG',
            width : 1100,
            height : 550
        });

        var form = Gikam.create('form', {
            renderTo : modal.window.$dom,
            titleWidth : 0,
            fields : [ {
                field : 'log',
                type : 'textarea',
                value : log,
                height : 500
            } ],
            columns : 1
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ _this.getRemoteCallConfigEditList() ]
            },

            center : {
                items : [ {
                    type : 'layout',

                    north : {
                        height : '50%',
                        items : [ _this.getRemoteCallRequestEditList() ]
                    },

                    center : {
                        items : [ _this.getRemoteCallResponseEditList() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    },

    activate : function() {
        var selections = Gikam.getComp('core-remote-call-config-edit-list-grid').getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.put(coreRemoteCallConfig.baseUrl + '/action/unsuspend', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
            Gikam.getComp('core-remote-call-config-edit-list-grid').refresh();
        });
    },

    deactivate : function() {
        var selections = Gikam.getComp('core-remote-call-config-edit-list-grid').getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.put(coreRemoteCallConfig.baseUrl + '/action/suspend', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
            Gikam.getComp('core-remote-call-config-edit-list-grid').refresh();
        });
    },

    resetRequest : function() {
        var selections = Gikam.getComp('core-remote-call-config-edit-list-request-grid').getSelections();
        var activeRow = Gikam.getComp('core-remote-call-config-edit-list-grid').getActivedRow();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.put(coreRemoteCallConfig.baseUrl + '/configs/requests/action/reset', Gikam.getJsonWrapper({
            synch : activeRow.synch
        }, [ '', selections ])).done(function() {
            Gikam.getComp('core-remote-call-config-edit-list-request-grid').refresh();
        });
    },

    loadRequestDataDetailPage : function(id) {
        var _this = this;

        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.REMOTE_CALLS.TITLE.REQUEST_DETAIL',
            url : coreRemoteCallConfig.baseUrl + '/page/request-detail' + Gikam.param({
                id : id
            }),
        });
    },
};