emcEquipAppearance.editPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-equip-appearance-edit-list-grid',
            url: emcEquipAppearance.baseUrl + '/queries/raw',
            columns: emcEquipAppearance.getEditListColumns(),
            service: 'EmcEquipAppearanceServiceImpl',
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    Gikam.preInsert({
                        modalTitle: 'EMC.MODULE.MDM.EQUIPT.EQUIPTCHECKS.MODAL.ADD',
                        fields: emcEquipAppearance.getEditListColumns(),
                        url: emcEquipAppearance.baseUrl
                    }).done(function () {
                        Gikam.getComp('emc-equip-appearance-edit-list-grid').refresh();
                    });
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('emc-equip-appearance-edit-list-grid').deleteRows(emcEquipAppearance.baseUrl);
                }
            }]
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-appearance-edit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getGridParam()]
            }
        });
    },

    init: function () {
        this.create();
    }
};
