emcMtlPerCheck.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-per-check-audit-list-grid',
            url : emcMtlPerCheck.baseUrl + '/queries/raw',
            service : 'emcMtlPerCheckServiceImpl',
            dbTable : 'T_EMC_MTL_PER_CHECK',
            columns : emcMtlPerCheck.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-mtl-per-check-audit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-mtl-per-check-audit-list-base-info-form').done(function(form) {
                	form.setReadonly(true);
                	form.refresh({
                        url : Gikam.printf(emcMtlPerCheck.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

            },
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.MTLPERCHECKS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcMtlPerCheckServiceImpl',
                    dbTable : 'T_EMC_MTL_PER_CHECK',
                    id : 'emc-mtl-per-check-audit-list-base-info-form',
                    fields : emcMtlPerCheck.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-mtl-per-check-audit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-mtl-per-check-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
