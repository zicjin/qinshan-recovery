coreUser.editPage = {

    getGridParam : function() {
        
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-user-edit-list-grid',
            url : coreUser.baseUrl + '/queries/raw',
            dbTable : 'T_CORE_USER',
            service : 'coreUserServiceImpl',
            columns : coreUser.getEditListColumns(),
            columnsFill : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.MDM.USERS.TAB.ADD',
                        fields : coreUser.getBaseInfoFormFields(),
                        url : coreUser.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.USERS.BUTTON.CHANGE_PASSWORD',
                icon : 'edit',
                onClick : function() {
                    _this.changePassWord();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.USERS.BUTTON.DEPRECATE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deprecateRows();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.USERS.BUTTON.RESET_PASSWORD',
                icon : 'default',
                onClick : function() {
                    _this.resetPassword();
                }
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        }
    },

    deprecateRows : function() {
        Gikam.getComp('core-user-edit-list-grid').processRows({
            url : coreUser.baseUrl + '/action/deprecate',
            confirm : {
                msg : 'CORE.MODULE.MDM.USERS.TIP.CONFIRM_TO_DEPRECATE_FOLLOWING_USERS', // 确认弹出框中左侧的信息
                title : 'CORE.MODULE.MDM.USERS.TIP.CONFIRM_TO_DEPRECATE', // 确认弹出框中的标题
            },
            formatter : function(row) {
                return row.id;
            },
            method : 'put'
        });
    },

    changePassWord : function() {

        var selectData = Gikam.getComp('core-user-edit-list-grid').getSelections();

        if (Gikam.isEmpty(selectData) || selectData.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : '修改密码',
            width : 400,
            height : 400,
            url : coreUser.baseUrl + '/page/changePassword' + Gikam.param({
                userId : selectData[0].id,
                dataFrom : 'gird'
            })
        });

    },

    resetPassword : function() {

        Gikam.getComp('core-user-edit-list-grid').processRows({
            url : coreUser.baseUrl + '/action/password-reset',
            confirm : {
                msg : 'CORE.MODULE.MDM.USERS.TIP.CONFIRM_TO_RESET_FOLLOWING_USERS',
                title : 'CORE.MODULE.MDM.USERS.TIP.CONFIRM_TO_RESET',
            },
            formatter : function(row) {
                return row.id;
            },
            method : 'put'
        }).done(function() {
            Gikam.alert('CORE.MODULE.MDM.USERS.TIP.RESET_PASSWORD_SUCCESS');
        });

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
        workspace.window.load(coreUser.baseUrl + '/page/detail' + Gikam.param({
            id : encodeURIComponent(id)
        }));
    },

    init : function() {
        workspace.window.removeTitle();
        this.create();
    }
};