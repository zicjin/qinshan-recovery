emcMtlApply.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-apply-edit-list-grid',
            url : emcMtlApply.baseUrl + '/queries/raw',
            service : 'emcMtlApplyServiceImpl',
            columns : emcMtlApply.getEditListColumns(),
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            deleteFormatter : function(row) {
                return row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addMtl();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-mtl-apply-edit-list-grid').deleteRows(emcMtlApply.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_MTL_APPLY', Gikam.getComp('emc-mtl-apply-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    addMtl : function() {
        debugger
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.MTLAPPLIES.MODAL.ADD',
            url : Gikam.choose.getConfig('mtlStock').url + Gikam.param({
                stockQty_NG : 0,
                single : false
            }),
            width : Gikam.choose.getConfig('mtlStock').width,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(row) {
                    return {
                        mtlId : row.mtlId,
                        applyQty : 0,
                        stockId : row.id
                    };
                });
                Gikam.getComp('emc-mtl-apply-edit-list-grid').insert(emcMtlApply.baseUrl, chooseData);
            }
        });
    },

    submit : function() {
        var grid = Gikam.getComp('emc-mtl-apply-edit-list-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (!grid.validate(selections)) {
            return;
        }

        var validation = selections.every(function (row) {
            return parseFloat(row.ext$.stockqty) >= row.applyQty;
        });
        if (!validation) {
            Gikam.alert('EMC.MODULE.ITEM.MTLAPPLIES.TIP.APPLY_OVER_STOCK');
            return;
        }

        var lowerLimitValid = selections.every(function (row) {
            return row.applyQty > 0;
        });
        if (!lowerLimitValid) {
            Gikam.alert('EMC.MODULE.ITEM.MTLAPPLIES.TIP.APPLY_MUST_GREATER_THAN_ZERO');
            return;
        }

        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcMtlApply
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-mtl-apply-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
