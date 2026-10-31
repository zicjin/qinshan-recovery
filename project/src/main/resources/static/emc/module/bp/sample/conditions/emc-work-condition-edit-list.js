emcWorkCondition.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-work-condition-edit-list-grid',
            url : emcWorkCondition.baseUrl + '/queries/raw',
            columns : emcWorkCondition.getEditListColumns(),
            service : 'emcWorkConditionServiceImpl',
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-work-condition-edit-list-gas-form', 'emc-work-condition-edit-list-water-form', 'emc-work-condition-edit-list-sw-form', 'emc-work-condition-line-edit-list-grid' ]);
                }
            },
            deleteFormatter : function(row) {
                return row.sqlNo || row.id;
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-work-condition-edit-list-gas-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcWorkCondition.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('emc-work-condition-edit-list-water-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcWorkCondition.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('emc-work-condition-edit-list-sw-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcWorkCondition.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                Gikam.getComp('emc-work-condition-line-edit-list-grid').refresh({
                    url : emcWorkCondition.baseUrl + '/lines/queries',
                    requestData: {
                        conditionId_SEQ : row.id
                    }
                })
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-work-condition-edit-list-grid').insert(emcWorkCondition.baseUrl, [{ projId : _this.param.projId_SEQ }]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-work-condition-edit-list-grid').deleteRows(emcWorkCondition.baseUrl);
                }
            } ]
        }
    },

    getGridLineParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-work-condition-line-edit-list-grid',
            columns : emcWorkCondition.getLineListColumns(),
            service : 'emcWorkConditionLineServiceImpl',
            deleteFormatter : function(row) {
                return row.production || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    let activedRow = Gikam.getComp('emc-work-condition-edit-list-grid').getActivedRow();
                    if (Gikam.isEmpty(activedRow)) {
                        return;
                    }
                    Gikam.getComp('emc-work-condition-line-edit-list-grid').insert(emcWorkCondition.baseUrl + '/action/insert-line', [{ conditionId : activedRow.id }]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-work-condition-line-edit-list-grid').deleteRows(emcWorkCondition.baseUrl + '/action/delete-line');
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '废气',
                items : [ {
                    type : 'form',
                    service : 'emcWorkConditionServiceImpl',
                    id : 'emc-work-condition-edit-list-gas-form',
                    fields : emcWorkCondition.getGasFormFields(),
                    columns : 2,
                } ]
            }, {
                title : '废水',
                items : [ {
                    type : 'form',
                    service : 'emcWorkConditionServiceImpl',
                    id : 'emc-work-condition-edit-list-water-form',
                    fields : emcWorkCondition.getWaterFormFields(),
                    columns : 2
                } ]
            }, {
                title : '固废',
                items : [ {
                    type : 'form',
                    service : 'emcWorkConditionServiceImpl',
                    id : 'emc-work-condition-edit-list-sw-form',
                    fields : emcWorkCondition.getSolidWasteFormFields(),
                    columns : 2
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-work-condition-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : '50%',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [this.getGridParam()]
                    },
                    center : {
                        items: [this.getGridLineParam()]
                    }
                } ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
