emcEquipCheck.searchPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-equip-check-search-list-grid',
            url: emcEquipCheck.baseUrl + '/queries/searchable',
            columns: emcEquipCheck.getSearchListColumns(),
            filterOpen : true,
            page: true,
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-equip-check-part-search-list-grid', 'emc-equip-check-data-search-list-grid']);
                }
            },
            onRowActive: function (index, row) {
                Gikam.getAsyncComp('emc-equip-check-part-search-list-grid').done(function (form) {
                    form.refresh({
                        url: Gikam.printf(emcEquipCheck.baseUrl + '/{id}/equiptParts/queries', {
                            id: row.id
                        })
                    });
                });
                Gikam.getAsyncComp('emc-equip-check-data-search-list-grid').done(function (form) {
                    form.refresh({
                        url: Gikam.printf(emcEquipCheck.baseUrl + '/{id}/equiptDatas/queries', {
                            id: row.id
                        })
                    });
                });
            },
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
                    Gikam.viewReport('EMC_EQ_CHECK', Gikam.getComp('emc-equip-check-search-list-grid').getSelections());
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('emc-equip-check-search-list-grid').deleteRows(emcEquipCheck.baseUrl);
                }
            } ]
        }
    },

    getInfoParam: function () {
        var _this = this;
        return {
            type: 'tab',
            panels: [{
                title: 'T_EMC_EQUIP_PART.SERVICE_NAME',
                items: [{
                    type: 'grid',
                    id: 'emc-equip-check-part-search-list-grid',
                    columns: emcEquipCheck.getPartListColumns(),
                    readonly: true
                }]
            }, {
                title: 'T_EMC_EQUIP_DATA.SERVICE_NAME',
                items: [{
                    type: 'grid',
                    id: 'emc-equip-check-data-search-list-grid',
                    columns: emcEquipCheck.getDataListColumns(),
                    readonly: true
                }]
            }]
        }
        return {
            type: 'tab',
            fill: true,
            panels: panels
        }
    },

    uploadSampleFile: function () {
        var selections = Gikam.getComp('emc-equip-check-search-list-grid').getSelections();
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

    loadDetailPage: function (id) {
        workspace.window.load(emcEquipCheck.baseUrl + '/page/detail' + Gikam.param({
            id: id
        }));
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-check-search-list-layout',
            renderTo: workspace.window.$dom,
            north: {
                height: '50%',
                items: [this.getGridParam()]
            },
            center: {
                items: [this.getInfoParam()]
            }
        });
    },

    init: function () {
        this.create();
    }
};
