lcdpBizProcess.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'lcdp-biz-process-edit-list-grid',
            url : lcdpBizProcess.baseUrl + '/queries/raw',
            service : 'lcdpBizProcessServiceImpl',
            columns : lcdpBizProcess.getEditListColumns(),
            onCellClick : function (field, row) {
                field === 'previewProcess' && Gikam.bp.openProcessStatus(null, null, row.id);
                field === 'bpCode' && _this.loadDetailPage(row.id, row.activatedFlag == '1');
            },
            deleteFormatter : function (row) {
                return row.bpName || row.id;
            },
            activateFormatter : function (row) {
                return row.bpName || row.id;
            },
            toolbar : [ {
                type : 'form',
                columns : 3,
                fields : [ {
                    title : 'LCDP.MODULE.BPS.FILTER.LATEST_ONLY',
                    field : 'latest_only',
                    type : 'select',
                    value : '1',
                    items : Gikam.getYesOrNoSelectItems()
                } ]
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'LCDP.MODULE.BPS.MODAL.ADD',
                        fields : lcdpBizProcess.getPreInsertFormFields(),
                        url : lcdpBizProcess.baseUrl
                    }).done(function (id) {
                        _this.loadDetailPage(id, false);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function () {
                    var flag = false;
                    var selections = Gikam.getComp('lcdp-biz-process-edit-list-grid').getSelections();
                    selections.forEach(function (bp) {
                        if (bp.activatedFlag === '1') {
                            flag = true;
                        }
                    });
                    if (flag) {
                        Gikam.alert('LCDP.MODULE.BPS.TIP.CANNOT_DELETE_ACTIVED');
                        return;
                    }
                    Gikam.getComp('lcdp-biz-process-edit-list-grid').deleteRows(lcdpBizProcess.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function () {
                    var selections = Gikam.getComp('lcdp-biz-process-edit-list-grid').getSelections();
                    if (selections.length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    if (Gikam.isEmpty(selections[ 0 ].resourceId)) {
                        Gikam.alert("LCDP.MODULE.BPS.TIP.PAGE_NOT_EMPTY");
                        return;
                    }

                    if (Gikam.isEmpty(selections[ 0 ].tableName)) {
                        Gikam.alert("LCDP.MODULE.BPS.TIP.TABLENAME_NOT_EMPTY");
                        return;
                    }

                    if (Gikam.isEmpty(selections[ 0 ].statusField) || Gikam.isEmpty(selections[ 0 ].statusDisplayField) || Gikam.isEmpty(selections[ 0 ].rejectField)) {
                        Gikam.alert("LCDP.MODULE.BPS.TIP.BP_COLUMN_NOT_EMPTY");
                        return;
                    }

                    Gikam.getComp('lcdp-biz-process-edit-list-grid').activateRows(lcdpBizProcess.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.MODIFY',
                icon : 'edit',
                onClick : function () {
                    _this.modify();
                }
            } ]
        }
    },

    modify : function () {
        var selections = Gikam.getComp('lcdp-biz-process-edit-list-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        if (selections[ 0 ].activatedFlag != '1') {
            Gikam.alert('GIKAM.TIP.CHOOSE_ACTIVATED_ITEM');
            return;
        }

        Gikam.put(lcdpBizProcess.baseUrl + '/action/modify', Gikam.getJsonWrapper({}, [ 'lcdpBizProcessServiceImpl', selections ])).done(function () {
            Gikam.getComp('lcdp-biz-process-edit-list-grid').refresh();
        });
    },

    loadDetailPage : function (id, readonly) {
        workspace.window.load(lcdpBizProcess.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            readonly : readonly
        }));
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'lcdp-bp-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
