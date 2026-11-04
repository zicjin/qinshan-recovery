modifiedUser.editPage = {
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'modified-user-edit-list-tab',
            onActived : function(data) {
                if (data.index == 0) {
                    Gikam.getComp('modified-user-edit-list-raw-grid').refresh();
                } else if (data.index == 1) {
                    Gikam.getComp('modified-user-edit-list-activated-grid').refresh({url:modifiedUser.baseUrl + '/queries/activated'});
                } else {
                    Gikam.getComp('modified-user-edit-list-executed-grid').refresh({url:modifiedUser.baseUrl + '/queries/executed'});
                }
            },
            onRendered : function() {
            },
            panels : [ {
                title : 'CORE.MODULE.MODIFICATION_USER.TAB.EDIT_LIST',
                items : [ {
                    type : 'grid',
                    id : 'modified-user-edit-list-raw-grid',
                    url : modifiedUser.baseUrl + '/queries/raw',
                    service : 'coreModifiedUserServiceImpl',
                    columnsFill : false,
                    deleteFormatter : function(row) {
                        return row.userName || '';
                    },
                    activateFormatter : function(row) { 
                        return row.userName || '';
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        id : 'modified-user-edit-list-raw-grid-insert-button',
                        onClick : function() {
                            _this.insert();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        id : 'modified-user-edit-list-raw-grid-delete-button',
                        onClick : function() {
                            Gikam.getComp('modified-user-edit-list-raw-grid').deleteRows(modifiedUser.baseUrl);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.ACTIVE',
                        icon : 'enable',
                        id : 'modified-user-edit-list-raw-grid-activate-button',
                        onClick : function() {
                            Gikam.getComp('modified-user-edit-list-raw-grid').activateRows(modifiedUser.baseUrl + '/action/activate');
                        }
                    } ],
                    columns : modifiedUser.getEditListColumns(),
                    onCellClick : function(field, row) {
                        field === 'userId' && _this.loadDetailPage(row.id, true);
                    }
                } ]
            }, {
                title : 'CORE.MODULE.MODIFICATION_USER.TAB.ACTIVATED_LIST',
                items : [ {
                    type : 'grid',
                    id : 'modified-user-edit-list-activated-grid',
                    columnsFill : false,
                    activateFormatter : function(row) {
                        return row.userName || '';
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DEACTIVE',
                        icon : 'disable',
                        id : 'modified-user-edit-list-raw-grid-deactivate-button',
                        onClick : function() {
                            Gikam.getComp('modified-user-edit-list-activated-grid').deactivateRows(modifiedUser.baseUrl + '/action/deactivate');
                        }
                    } ],
                    columns : modifiedUser.getActivatedListColumns(),
                    onCellClick : function(field, row) {
                        field === 'userId' && _this.loadDetailPage(row.id);
                    }
                } ]
            }, {
                title : 'CORE.MODULE.MODIFICATION_USER.TAB.EXECUTED_LIST',
                items : [ {
                    type : 'grid',
                    id : 'modified-user-edit-list-executed-grid',
                    columns : modifiedUser.getActivatedListColumns(),
                    columnsFill : false,
                    onCellClick : function(field, row) {
                        field === 'userId' && _this.loadDetailPage(row.id);
                    }
                } ]
            } ]
        }
    },
    
    activateRows : function() {
        Gikam.getComp('modified-user-edit-list-raw-grid').processRows({
            url : modifiedUser.baseUrl + '/action/activate',
            confirm : {
                msg : 'CORE.MODULE.MDM.MODIFIED_USERS.TIP.CONFIRM_TO_ACTIVATE_FOLLOWING_USERS', // 确认弹出框中左侧的信息
                title : 'CORE.MODULE.MDM.MODIFIED_USERS.TIP.CONFIRM_TO_ACTIVATE', // 确认弹出框中的标题
            },
            formatter : function(row) {
                return row.id;
            },
            method : 'put'
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    insert : function() {
        var _this = this;
        
        Gikam.create('modal', {
            title : 'CORE.MODULE.MODIFICATION_USER.TITLE.USER_CHOOSE',
            url : IFM_CONTEXT + '/secure/core/module/mdm/users/page/choose-list' + Gikam.param({
                single : '1',
                target_filter : encodeURI(JSON.stringify({
                    type : 'unassigned',
                    targetTable : 'T_CORE_M_USER',
                    targetMatchColumn : 'USERID',
                    thisMatchColumn : 'ID',
                    filter : [ {
                        targetFilterColumn : 'ACTIVATEDFLAG',
                        targetFilterValue : '0',
                    } ]
                }))
            }),
            onAfterClose : function(rows) {
                if (rows) {
                    var insertUrl = IFM_CONTEXT + '/secure/core/module/mdm/m-users/source-users/' + rows[0].id;
                    
                    Gikam.postText(insertUrl).done(function(data) {
                        Gikam.getComp('modified-user-edit-list-raw-grid').refresh();
                        
                        _this.loadDetailPage(data, true);
                    })
                }
            }
        });
    },
    
    loadDetailPage : function(id, edit) {
        Gikam.create('modal', {
            title : 'CORE.MODULE.MODIFICATION_USER.TITLE.DETAIL',
            url : modifiedUser.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                edit : edit
            }),
            onAfterClose : function() {
                if (edit) {
                    Gikam.getComp('modified-user-edit-list-raw-grid').refresh();
                }
            }
        });
    },

    init : function() {
        workspace.window.removeTitle();
        this.create();
    }
};