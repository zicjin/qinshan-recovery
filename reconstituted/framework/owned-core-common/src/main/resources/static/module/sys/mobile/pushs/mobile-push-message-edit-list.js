mobilePush.editPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'mobile-push-message-edit-list-grid',
            url: mobilePush.baseUrl + '/queries/raw',
            columns: mobilePush.getEditListColumns(),
            service: 'mobilePushMessageServiceImpl',
            checkOneOnActive: true,
            checkOnActive: false,
            activateFormatter: function (row) {
                return row.title;
            },
            page: true,
            deleteFormatter: function (row) {
                return row.title;
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(["mobile-push-message-edit-list-base-info-form"]);
                }
            },
            onRowActive: function (index, row) {
                console.log("row = ");
                console.log(row);
                _this.onConfigActive(row);
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    Gikam.preInsert({
                        modalTitle: 'CORE.MODULE.SYS.MOBILE_PUSHS.MODAL.ADD',
                        height: 250,
                        fields: mobilePush.getBaseInfoFormFields(),
                        url: mobilePush.baseUrl
                    }).done(function (id) {
                        Gikam.getComp('mobile-push-message-edit-list-grid').refresh();
                    });
                }
            }, {
                type: 'button',
                text: 'CORE.MODULE.SYS.MOBILE_PUSHS.BUTTON.SEND',
                icon: 'submit',
                onClick: function () {
                    var rows = Gikam.getComp('mobile-push-message-edit-list-grid').getSelections();
                    var data = [];
                    if (rows) {
                        rows.forEach(function (item) {
                            if (item['pushFlag'] === "0") {
                                data.push(item);
                            }
                        });
                        if (data.length === 0) {
                            Gikam.alert('CORE.MODULE.SYS.MOBILE_PUSHS.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        var flag = false;
                        data.forEach(function (item) {
                            if (Gikam.isEmpty(item['pushedUserId']) ||
                                Gikam.isEmpty(item['title']) ||
                                Gikam.isEmpty(item['content'])) {
                                flag = true;
                            }
                        });
                        if (flag) {
                            Gikam.alert('CORE.MODULE.SYS.MOBILE_PUSHS.TIP.CHOOSE_EMPTY');
                            return;
                        }
                        Gikam.put(mobilePush.baseUrl + "/action/send", Gikam.getJsonWrapper({}, [ 'mobilePushMessageServiceImpl', data ])).done(function(data) {
                            Gikam.getComp('mobile-push-message-edit-list-grid').refresh();
                        });
                    }
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('mobile-push-message-edit-list-grid').deleteRows(mobilePush.baseUrl);
                }
            }]
        }
    },

    onConfigActive: function (row) {
        var _this = this;
        if (row) {
            this.param = row;
            Gikam.getComp('mobile-push-message-edit-list-base-info-form').refresh({
                url: Gikam.printf(mobilePush.baseUrl + '/{id}', {
                    id: row.id
                })
            });
        }
    },

    getBaseInfoTab: function () {
        var _this = this;
        return {
            panels: [{
                title: 'CORE.MODULE.SYS.MOBILE_PUSHS.TAB.BASE_INFO',
                items: [{
                    type: 'form',
                    columns: 3,
                    id: 'mobile-push-message-edit-list-base-info-form',
                    fields: mobilePush.getBaseInfoFormFields(),
                    service: 'mobilePushMessageServiceImpl',
                    autoSave: true,
                    onUpdated: function () {
                        var grid = Gikam.getComp('mobile-push-message-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                }]
            }],
            type: 'tab'
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'mobile-push-message-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: 400,
                items: [_this.getGridParam()]
            },
            center: {
                items: [_this.getBaseInfoTab()]
            }
        });
    },

    init: function () {
        this.param = null;
        this.create();
    }
};