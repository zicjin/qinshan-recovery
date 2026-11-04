coreLoginLog.onlinePage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-login-online-list-grid',
            url : coreLoginLog.baseUrl + '/queries/online',
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.OFFLINE',
                icon : 'remove',
                onClick : function() {
                    var _this = this, options = {
                        url : coreLoginLog.baseUrl + '/action/offline', // 请求的地址
                        confirm : {
                            msg : 'GIKAM.TIP.CONFIRM_TO_OFFLINE_MSG', // 确认弹出框中左侧的信息
                            title : 'GIKAM.TIP.CONFIRM_TO_OFFLINE_TITLE', // 确认弹出框中的标题
                        },
                        formatter : function(row) {
                            return row.ext$.username + '（' + row.loginTime + '）';
                        },
                        method : 'put', // 调用的Html请求的方法
                    };

                    return Gikam.getComp('core-login-online-list-grid').processRows(options);
                }
            } ],
            columns : coreLoginLog.getOnlineListColumns()
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
                    text : 'GIKAM.BUTTON.OFFLINE',
                    icon : 'remove',
                    onClick : function() {
                        var _this = this, options = {
                            url : coreLoginLog.baseUrl + '/action/offline', // 请求的地址
                            confirm : {
                                msg : 'GIKAM.TIP.CONFIRM_TO_OFFLINE_MSG', // 确认弹出框中左侧的信息
                                title : 'GIKAM.TIP.CONFIRM_TO_OFFLINE_TITLE', // 确认弹出框中的标题
                            },
                            formatter : function(row) {
                                return row.ext$.username + '（' + row.loginTime + '）';
                            },
                            method : 'put', // 调用的Html请求的方法
                        };

                        return Gikam.getComp('core-login-online-list-grid').processRows(options);
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

    init : function() {
        this.create();
    }
};