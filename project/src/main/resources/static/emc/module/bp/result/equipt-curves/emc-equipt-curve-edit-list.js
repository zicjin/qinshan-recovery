emcEquiptCurve.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-curve-edit-list-grid',
            url : emcEquiptCurve.baseUrl + '/queries/raw',
            columns : emcEquiptCurve.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-equipt-curve-edit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-curve-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptCurve.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.RESULT.EQUIPTCURVES.MODAL.ADD',
                        fields : emcEquiptCurve.getBaseInfoFormFields(),
                        url : emcEquiptCurve.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-equipt-curve-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-curve-edit-list-grid').deleteRows(emcEquiptCurve.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.EQUIPTCURVES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptCurveServiceImpl',
                    id : 'emc-equipt-curve-edit-list-base-info-form',
                    fields : emcEquiptCurve.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-curve-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-curve-edit-list-layout',
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
