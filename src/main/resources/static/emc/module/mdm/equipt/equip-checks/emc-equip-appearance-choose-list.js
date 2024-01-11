emcEquipAppearance.choosePage = {

    getGridParam: function () {
        var _this = this, columns = emcEquipAppearance.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio: true
        }) : columns.unshift({
            checkbox: true
        });
        return {
            type: 'grid',
            id: 'emc-equip-appearance-choose-list-grid',
            url: emcEquipAppearance.baseUrl + '/queries/choosable',
            requestData: _this.param,
            columns: columns,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.CONFIRM',
                icon: 'select',
                onClick: function () {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equip-appearance-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }]
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-appearance-choose-list-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            center: {
                items: [this.getGridParam()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
};
