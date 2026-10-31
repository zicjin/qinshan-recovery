emcEquipVerification.searchPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-equip-verification-search-list-grid',
            url: emcEquipVerification.baseUrl + '/queries/searchable',
            columns: emcEquipVerification.getSearchListColumns(),
            filterOpen : true,
            page: true,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.FILE.ATTACHMENT',
                icon: 'upload',
                onClick: function () {
                    _this.uploadSampleFile();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('EMC_EQ_VERIFICATION', Gikam.getComp('emc-equip-verification-search-list-grid').getSelections());
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('emc-equip-verification-search-list-grid').deleteRows(emcEquipVerification.baseUrl);
                }
            } ]
        }
    },

    uploadSampleFile: function () {
        var selections = Gikam.getComp('emc-equip-verification-search-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: 'GIKAM.FILE.ATTACHMENT',
            url: '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id: selections[0].id,
                table: 'T_EMC_SAMPLE',
                readonly: true
            })
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-verification-search-list-layout',
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
