emcAdmArea.choosePage = {

    getButtonParam: function () {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.CONFIRM',
            icon: 'select',
            align: 'right',
            onClick: function () {
                var form = Gikam.getComp('emc-adm-area-choose-list-form');
                var data = form.getData();
                var result = '';
                var shengText = data.shengText;
                if (Gikam.isEmpty(data.shengText)) {
                    Gikam.alert('未选择省份！');
                    return;
                }
                result = result + shengText;
                var shiText = data.shiText;
                if (!Gikam.isEmpty(data.shiText)) {
                    result = result + '/' + shiText;
                    var quText = data.quText;
                    if (!Gikam.isEmpty(data.quText)) {
                        result = result + '/' + quText;
                    }
                }
                Gikam.getLastModal().close(result);
            }
        }
    },

    getGridParam: function () {
        var _this = this;
        return {
            type: 'form',
            id: 'emc-adm-area-choose-list-form',
            columns: 3,
            fields: emcAdmArea.getChooseFields(),
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-adm-area-choose-list-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            north: {
                height: '30',
                items: [this.getButtonParam()]
            },
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
