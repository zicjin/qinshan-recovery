emcEquipVerification.editPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-equip-verification-edit-list-grid',
            url: emcEquipVerification.baseUrl + '/queries/raw',
            columns: emcEquipVerification.getEditListColumns(),
            service: 'EmcEquipVerificationServiceImpl',
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    _this.add();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function () {
                    Gikam.getComp('emc-equip-verification-edit-list-grid').deleteRows(emcEquipVerification.baseUrl);
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
                    Gikam.viewReport('EMC_EQ_VERIFICATION', Gikam.getComp('emc-equip-verification-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    add: function () {
        var _this = this;
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('equiptCali').title,
            url: Gikam.choose.getConfig('equiptCali').url + Gikam.param({
                //single : true,
                pageType : 'equiptVerification'
            }),
            width: Gikam.choose.getConfig('equiptCali').width,
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var equipts = rows.map(function (row) {
                    return {
                        equiptId: row.id,
                        name: row.equiptName,
                        number: row.equiptId,
                        model: row.specification,
                        accuracy: row.accuracy,
                        unit : row.ext$.verifyorg,
                        ext$ : {
                            verificationreason : row.ext$.gist,
                            cycle : row.ext$.cycle,
                        },
                        validity : row.ext$.maintenddate,
                        accuracy : row.ext$.resolve,
                    };
                });
                Gikam.getComp('emc-equip-verification-edit-list-grid').insert(Gikam.printf(emcEquipVerification.baseUrl), equipts)
            }
        });
    },

    uploadSampleFile: function () {
        var selections = Gikam.getComp('emc-equip-verification-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title: 'GIKAM.FILE.ATTACHMENT',
            url: '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                id: selections[0].id,
                table: 'T_EMC_SAMPLE',
            })
        });
    },

    submit: function () {
        var grid = Gikam.getComp('emc-equip-verification-edit-list-grid');
        Gikam.create('workflow').submit({
            data: grid.getSelections(),
            pageObject: emcEquipVerification
        }).done(function () {
            grid.refresh();
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-verification-edit-list-layout',
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
