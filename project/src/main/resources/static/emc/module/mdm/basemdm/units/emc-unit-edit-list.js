emcUnit.editPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-unit-edit-list-grid',
            columns : emcUnit.getEditListColumns(),
            url : emcUnit.baseUrl + '/queries/raw',
            service : 'emcUnitServiceImpl',
            page : true,
            deleteFormatter : function(row) {
                return row.unitName || row.id;
            },
            onCellClick : function(field, row, index) {
                field === 'unitName' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    return this.activeRowByIndex(0);
                }
                Gikam.getComp('emc-unit-detail-convert-grid').cleanData();
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-unit-detail-convert-grid').refresh({
                        url : Gikam.printf(emcUnit.baseUrl + '/{id}/converts/queries', {
                            id : row.id
                        })
                    });

                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.UNITS.TAB.UNIT_ADD',
                        fields : emcUnit.getBaseInfoFormFields(),
                        url : emcUnit.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-unit-edit-list-grid').deleteRows(emcUnit.baseUrl);
                }
            } ]
        };
    },

    getUnitConvertGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.UNITS.TAB.UNIT_CONVERT',
                items : [ {
                    type : 'grid',
                    id : 'emc-unit-detail-convert-grid',
                    service : 'emcUnitConvertServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.convertunitname || row.id;
                    },
                    columns : emcUnit.getConvertListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            let unit = Gikam.getComp('emc-unit-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(unit)) {
                                return;
                            }

                            Gikam.getComp('emc-unit-detail-convert-grid').insert(emcUnit.baseUrl + '/action/insert-unit-convert', [ {unitId : unit.id} ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-unit-detail-convert-grid').deleteRows(emcUnit.baseUrl + '/action/delete-unit-convert');
                        }
                    } ]
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-unit-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '45%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getUnitConvertGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcUnit.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
}
