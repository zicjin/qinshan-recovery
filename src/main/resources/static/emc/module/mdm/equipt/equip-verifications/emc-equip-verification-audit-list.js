emcEquipVerification.auditPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-equip-verification-audit-list-grid',
            url: emcEquipVerification.baseUrl + '/queries/tasks',
            columns: emcEquipVerification.getAuditListColumns(),
            requestData : _this.param,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon: 'pass',
                onClick: function () {
                    _this.pass();
                }
            }, {
                type: 'button',
                text: 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon: 'reject',
                onClick: function () {
                    _this.reject();
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
                    /*DINGAN_EQ_VERIFICATION  DINGAN都替换成了EMC*/
                    Gikam.viewReport('EMC_EQ_VERIFICATION', Gikam.getComp('emc-equip-verification-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    uploadSampleFile: function () {
        var selections = Gikam.getComp('emc-equip-verification-audit-list-grid').getSelections();
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

    pass: function () {
        var grid = Gikam.getComp('emc-equip-verification-audit-list-grid');
        var gridData = Gikam.getComp('emc-equip-verification-audit-list-grid').getSelections();
        var url = emcEquipVerification.baseUrl + '/equipt-plan';
        Gikam.create('workflow').pass({
            data: grid.getSelections(),
            pageObject: emcEquipVerification
        }).done(function () {
            Gikam.post(url,Gikam.getJsonWrapper({}, [ '', gridData ]))
            grid.refresh();
        });
    },

    reject: function () {
        var grid = Gikam.getComp('emc-equip-verification-audit-list-grid');
        Gikam.create('workflow').reject({
            data: grid.getSelections(),
            pageObject: emcEquipVerification
        }).done(function () {
            grid.refresh();
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-equip-verification-audit-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getGridParam()]
            }
        });
    },

    init: function (param) {
        param.confirmerId_SEQ=workspace.user.id;
        this.param = param;
        this.create();
    }
};
