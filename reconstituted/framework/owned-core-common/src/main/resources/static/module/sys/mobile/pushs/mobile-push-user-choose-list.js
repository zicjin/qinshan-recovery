mobilePush.choosePage = {
    getGridParam: function () {
        var _this = this, columns = mobilePush.getChooseListColumns();
        columns.unshift({
            checkbox: true
        });
        return {
            type: 'grid',
            id: 'mobile-push-user-choose-list-grid',
            url: mobilePush.baseUserUrl + '/queries/choosable',
            requestData: _this.param,
            columns: columns,
            filterOpen : true,
            checkOnActive: false
        }
    },

    getBtnToolbarParam: function () {
        var _this = this;
        return {
            type: 'btnToolbar',
            items: [{
                type: 'btnGroup',
                items: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.CONFIRM',
                    icon: 'select',
                    onClick: function () {
                        Gikam.getLastModal().close(Gikam.getComp('mobile-push-user-choose-list-grid').getSelections());
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.CANCEL',
                    icon: 'cancel',
                    onClick: function () {
                        Gikam.getLastModal().close();
                    }
                }]
            }]
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: Gikam.getLastModal().window.$dom,
            center: {
                items: [this.getBtnToolbarParam(), this.getGridParam()]
            }
        });
    },

    init: function () {
        this.create();
    }
};