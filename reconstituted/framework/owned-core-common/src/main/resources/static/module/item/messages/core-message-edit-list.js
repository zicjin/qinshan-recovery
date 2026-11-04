coreMessage.editListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-message-edit-list-grid',
            url : coreMessage.baseUrl + '/queries/raw',
            columns : coreMessage.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.messageSubject || row.id;
            },
            onCellClick : function(field, row) {
                field === 'messageSubject' && _this.loadDetailPage(row.id, row.activatedFlag);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.MESSAGES.TAB.ADD_MESSAGES_INFO',
                        url : coreMessage.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-message-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                        return;
                    }
                    var flag = true;
                    Gikam.each(selectedRows, function(index, item) {
                        if (item.activatedFlag == '1') {
                            flag = false;
                            return false;
                        }
                    });
                    if (!flag) {
                        Gikam.alert('CORE.MODULE.ITEM.MESSAGES.TIP.CANNOT_DELETE_MESSAGE_ACTIVATED');
                        return;
                    }
                    Gikam.getComp('core-message-edit-list-grid').deleteRows(coreMessage.baseUrl);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.ITEM.MESSAGES.BUTTON.SEND_MESSAGE',
                icon : 'submit',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-message-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                        return;
                    }
                    var flag = true;
                    Gikam.each(selectedRows, function(index, item) {
                        if (item.activatedFlag == '1') {
                            flag = false;
                            return false;
                        }
                    });
                    if (!flag) {
                        Gikam.alert('CORE.MODULE.ITEM.MESSAGES.TIP.MESSAGE_ACTIVATED');
                        return;
                    }
                    Gikam.confirm('CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM', 'CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM_ACTIVE', function() {
                        workspace.window.showMask(true);
                        Gikam.put(coreMessage.baseUrl + '/action/activate', Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
                            Gikam.getComp('core-message-edit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    });
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
                            modalTitle : 'CORE.MODULE.ITEM.MESSAGES.TAB.ADD_MESSAGES_INFO',
                            url : coreMessage.baseUrl,
                        }).done(function(id) {
                            _this.loadDetailPage(id);
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        var selectedRows = Gikam.getComp('core-message-edit-list-grid').getSelections();
                        if (Gikam.isEmpty(selectedRows)) {
                            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                            return;
                        }
                        var flag = true;
                        Gikam.each(selectedRows, function(index, item) {
                            if (item.activatedFlag == '1') {
                                flag = false;
                                return false;
                            }
                        });
                        if (!flag) {
                            Gikam.alert('CORE.MODULE.ITEM.MESSAGES.TIP.CANNOT_DELETE_MESSAGE_ACTIVATED');
                            return;
                        }
                        Gikam.getComp('core-message-edit-list-grid').deleteRows(coreMessage.baseUrl);
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.MESSAGES.BUTTON.SEND_MESSAGE',
                    icon : 'submit',
                    onClick : function() {
                        var selectedRows = Gikam.getComp('core-message-edit-list-grid').getSelections();
                        if (Gikam.isEmpty(selectedRows)) {
                            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                            return;
                        }
                        var flag = true;
                        Gikam.each(selectedRows, function(index, item) {
                            if (item.activatedFlag == '1') {
                                flag = false;
                                return false;
                            }
                        });
                        if (!flag) {
                            Gikam.alert('CORE.MODULE.ITEM.MESSAGES.TIP.MESSAGE_ACTIVATED');
                            return;
                        }
                        Gikam.confirm('CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM', 'CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM_ACTIVE', function() {
                            workspace.window.showMask(true);
                            Gikam.put(coreMessage.baseUrl + '/action/activate', Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
                                Gikam.getComp('core-message-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        });
                    }
                } ]
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

    loadDetailPage : function(id, activatedFlag) {
        workspace.window.load(coreMessage.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            activatedFlag : activatedFlag
        }));
    },

    init : function() {
        this.create();
    }
};