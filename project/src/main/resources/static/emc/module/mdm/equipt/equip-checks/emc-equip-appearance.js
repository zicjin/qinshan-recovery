var emcEquipAppearance = Gikam.getPageObject({

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'description',
            title: 'T_EMC_EQUIP_APPEARANCE.DESCRIPTION',
            width: 300,
            preInsert: true
        }];
    },

    getChooseListColumns: function () {
        return [{
            field: 'description',
            title: 'T_EMC_EQUIP_APPEARANCE.DESCRIPTION',
            width: 300
        }];
    },

    baseUrl: IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equip-appearances'
});
