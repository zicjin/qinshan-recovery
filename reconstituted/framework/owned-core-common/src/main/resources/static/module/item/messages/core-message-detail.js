coreMessage.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        // 首页消息详情弹窗不加载按钮栏
        if (_this.param.source == 'query') {
            return;
        }

        return {
            type : 'btnToolbar',
            id : 'core-message-detail-btntool-bar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.ITEM.MESSAGES.BUTTON.SEND_MESSAGE',
                icon : 'appoint-person',
                onClick : function() {
                    var messageData = Gikam.getComp('core-message-detail-base-info-form').getData();

                    Gikam.confirm('CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM', 'CORE.MODULE.ITEM.MESSAGES.TIP.CONFIRM_ACTIVE', function() {
                        workspace.window.showMask(true);
                        Gikam.put(coreMessage.baseUrl + '/action/activate', Gikam.getJsonWrapper(null, [ '', [ messageData ] ])).done(function(r) {
                            workspace.window.goBack();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_MESSAGE', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                id : 'core-message-detail-back-button',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.ITEM.MESSAGES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-message-detail-base-info-form',
                    url : this.getCompUrl('baseInfoForm'),
                    service : 'coreMessageServiceImpl',
                    autoSave : true,
                    columns : 1,
                    fields : coreMessage.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(_this.param.receipentId)) { // 传来收件ID则签收消息
                            workspace.window.showMask(true);
                            Gikam.put(coreMessage.baseUrl + '/action/receive', Gikam.getJsonWrapper(null, [ '', [ {
                                id : _this.param.receipentId
                            } ] ])).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }
                } ]
            } ]
        }
    },

    getReceipentTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-message-detail-grid-tab',
            onRendered : function() {
                if (!Gikam.isEmpty(_this.param.source)) { // 收件人查看消息隐藏收件人列表
                    this.removePanel('core-message-detail-receipent-panel');
                }
            },
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.ITEM.MESSAGES.TAB.RECEIPENTNAME',
                id : 'core-message-detail-receipent-panel',
                items : [ {
                    type : 'simpleGrid',
                    id : 'core-message-detail-receipent-list-grid',
                    url : this.getCompUrl('messageReceipentGrid'),
                    service : 'coreMessageReceipentServiceImpl',
                    columns : coreMessage.getReceipentEditListColumns(),
                    deleteFormatter : function(row) {
                        return row.receipentName || row.receipentId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertReceipentRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteReceipentRow();
                        }
                    } ]
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.MESSAGES.TAB.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-message-detail-uploader',
                    dbTable : coreMessage.dbTable,
                    bizId : _this.param.id,
                    mutliple : false
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreMessage.baseUrl + '/{id}',
        messageReceipentGrid : coreMessage.baseUrl + '/{id}/receipents/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-message-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-message-detail-receipent-list-grid' : this.getCompUrl('messageReceipentGrid')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    insertReceipentRow : function() {
        var _this = coreMessage.detailPage;
        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.MESSAGES.TITLE.RECEIPENTNAME_CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + '?target_unassigned=1&targetTable=T_CORE_MESSAGE_RECEIPENT&targetUserIdColumn=RECEIPENTID&targetFilterColumn=MESSAGEID&targetFilterValue=' + _this.param.id,
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            receipentId : item.id,
                            receipentName : item.userName
                        }
                    });
                    Gikam.getComp('core-message-detail-receipent-list-grid').insert(_this.buildUrlByMainForm(), chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteReceipentRow : function() {
        var _this = coreMessage.detailPage;
        Gikam.getComp('core-message-detail-receipent-list-grid').deleteRows(_this.buildUrlByMainForm()).done(function() {
        });
    },

    buildUrlByMainForm : function() {
        var messageData = Gikam.getComp('core-message-detail-base-info-form').getData();
        return Gikam.printf(coreMessage.baseUrl + '/{id}/receipents', {
            id : messageData.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            id : 'core-message-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getReceipentTabParam() ]
            },
            renderTo : this.param.source == 'query' ? Gikam.getLastModal().window.$dom : workspace.window.$dom,
            readonly : this.param.activatedFlag == '1' ? true : false
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}