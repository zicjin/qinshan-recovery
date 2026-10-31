emcEquipCheck.editPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-equip-check-edit-list-grid',
            url: emcEquipCheck.baseUrl + '/queries/raw',
            columns: emcEquipCheck.getEditListColumns(),
            service: 'EmcEquipCheckServiceImpl',
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-equip-check-part-edit-list-grid', 'emc-equip-check-data-edit-list-grid']);
                }
            },
            onRowActive: function (index, row) {
                Gikam.getAsyncComp('emc-equip-check-part-edit-list-grid').done(function (form) {
                    form.refresh({
                        url: Gikam.printf(emcEquipCheck.baseUrl + '/{id}/equiptParts/queries', {
                            id: row.id
                        })
                    });
                });
                Gikam.getAsyncComp('emc-equip-check-data-edit-list-grid').done(function (form) {
                    form.refresh({
                        url: Gikam.printf(emcEquipCheck.baseUrl + '/{id}/equiptDatas/queries', {
                            id: row.id
                        })
                    });
                });
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    Gikam.preInsert({
                        modalTitle: 'EMC.MODULE.MDM.EQUIPT.EQUIPTCHECKS.MODAL.ADD',
                        fields: emcEquipCheck.getEditListColumns(),
                        url: emcEquipCheck.baseUrl
                    }).done(function (id) {
                        Gikam.getComp('emc-equip-check-edit-list-grid').refresh();
                    });
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('emc-equip-check-edit-list-grid').deleteRows(emcEquipCheck.baseUrl);
                }
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon: 'submit',
                onClick: function () {
                    _this.submit();
                }
            }, {
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
                    Gikam.viewReport('EMC_EQ_CHECK', Gikam.getComp('emc-equip-check-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    getBaseInfoTab: function () {
        var _this = this;
        return {
            type: 'tab',
            panels: [
                // 仪器部件信息
                {
                    title: 'T_EMC_EQUIP_PART.SERVICE_NAME',
                    items: [{
                        type: 'grid',
                        id: 'emc-equip-check-part-edit-list-grid',
                        columns: emcEquipCheck.getPartListColumns(),
                        service: 'EmcEquipPartServiceImpl',
                        deleteFormatter: function (row) {
                            return row.name || row.id;
                        },
                        toolbar: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.INSERT',
                            icon: 'add',
                            onClick: function () {
                                _this.insertPartRow();
                            }
                        }, {
                            type: 'button',
                            text: 'GIKAM.BUTTON.DELETE',
                            icon: 'remove-row',
                            onClick: function () {
                                Gikam.getComp('emc-equip-check-part-edit-list-grid').deleteRows(emcEquipCheck.baseUrl + '/equiptParts');
                            }
                        }]
                    }]
                },
                // 仪器资料信息
                {
                    title: 'T_EMC_EQUIP_DATA.SERVICE_NAME',
                    items: [{
                        type: 'grid',
                        id: 'emc-equip-check-data-edit-list-grid',
                        columns: emcEquipCheck.getDataListColumns(),
                        service: 'EmcEquipDataServiceImpl',
                        deleteFormatter: function (row) {
                            return row.name || row.id;
                        },
                        toolbar: [{
                            type: 'button',
                            text: 'GIKAM.BUTTON.INSERT',
                            icon: 'add',
                            onClick: function () {
                                _this.insertDataRow();
                            }
                        }, {
                            type: 'button',
                            text: 'GIKAM.BUTTON.DELETE',
                            icon: 'remove-row',
                            onClick: function () {
                                Gikam.getComp('emc-equip-check-data-edit-list-grid').deleteRows(emcEquipCheck.baseUrl + '/equiptDatas');
                            }
                        }]
                    }]
                }
            ]
        }
    },

    insertPartRow: function () {
        var activedPartRow = Gikam.getComp('emc-equip-check-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedPartRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcEquipCheck.baseUrl + '/{id}/equiptParts', {
            id: activedPartRow.id
        });
        Gikam.getComp('emc-equip-check-part-edit-list-grid').insert(url, [{
            id: activedPartRow.id
        }]);
    },

    insertDataRow: function () {
        var activedDataRow = Gikam.getComp('emc-equip-check-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedDataRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcEquipCheck.baseUrl + '/{id}/equiptDatas', {
            id: activedDataRow.id
        });
        Gikam.getComp('emc-equip-check-data-edit-list-grid').insert(url, [{
            id: activedDataRow.id
        }]);
    },

    uploadSampleFile: function () {
        var selections = Gikam.getComp('emc-equip-check-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: 'GIKAM.FILE.ATTACHMENT',
            url: '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id: selections[0].id,
                table: 'T_EMC_SAMPLE'
            })
        });
    },

    submit: function () {
        var grid = Gikam.getComp('emc-equip-check-edit-list-grid');
        if (!grid.validate(grid.getSelections())) {
            return;
        }
        Gikam.create('workflow').submit({
            data: grid.getSelections(),
            pageObject: emcEquipCheck
        }).done(function () {
            grid.refresh();
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-check-edit-list-layout',
            renderTo: workspace.window.$dom,
            north: {
                height: '50%',
                items: [this.getGridParam()]
            },
            center: {
                items: [this.getBaseInfoTab()]
            }
        });
    },

    init: function () {
        this.create();
    }
};
