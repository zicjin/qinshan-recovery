/**
 * 未读消息查询界面
 */
coreMessage.queryPage = {

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.MESSAGES.BUTTON.RECEIVE_ALL',
                    icon : 'finish',
                    onClick : function() {
                        var data = Gikam.getComp('core-message-query-list-grid').getData();
                        if (Gikam.isEmpty(data)) {
                            Gikam.alert("CORE.MODULE.ITEM.MESSAGES.TIP.NO_TORECEIVE_MESSAGE");
                            return;
                        }
                        Gikam.confirm('CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM', 'CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM_RECEIVE_ALL', function() {
                            workspace.window.showMask(true);
                            Gikam.put(coreMessage.baseUrl + '/action/receive', Gikam.getJsonWrapper(null, [ '', data ])).done(function(r) {
                                Gikam.getComp('core-message-query-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        });
                    }
                } ]
            } ]
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-message-query-list-grid',
            url : coreMessage.baseUrl + '/queries/receivable',
            columns : coreMessage.getQueryListColumns(),
            genericQueryFields : [ {
                field : 'ext$.messagesubject'
            } ],
            onCellClick : function(field, row) {
                field === 'ext$.messagesubject' && _this.loadDetailPage(row.messageId, row.id);
            }
        }
    },

    loadDetailPage : function(id, receipentId) {
        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.MESSAGES.TITLE.MESSAGE_DETAIL',
            width : Gikam.width.XL,
            height : 600,
            url : coreMessage.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                activatedFlag : '1',
                receipentId : receipentId,
                source : 'query'
            }),
            onAfterClose : function() {
                Gikam.getComp('core-message-query-list-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getBtnToolbarParam(), this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    },
};
