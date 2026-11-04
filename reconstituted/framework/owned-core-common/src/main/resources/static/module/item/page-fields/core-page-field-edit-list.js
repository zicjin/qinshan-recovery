corePageField.editPage = {

    getPageFieldParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-page-field-edit-list-grid',
            service : 'corePageFieldServiceImpl',
            url : corePageField.baseUrl + '/queries/raw',
            columns : corePageField.getPageFieldColumns(),
            page : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.PAGE_FIELDS.MODAL.PAGEFIELD_ADD',
                        fields : corePageField.getPageFieldColumns(),
                        url : corePageField.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-page-field-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-page-field-edit-list-grid').deleteRows(corePageField.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var gridRow = Gikam.getComp('core-page-field-edit-list-grid').getActivedRow();

                    if (!gridRow) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
                        return;
                    }

                    Gikam.viewAudit('T_CORE_PAGE_FIELD', gridRow.id);
                }
            } ],
            deleteFormatter : function(row) {
                return row.pfName || row.id;
            },
            onLoadSuccess : function(rows) {
                if (Gikam.isNotEmpty(rows)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['core-page-field-comp-edit-list-grid', 'core-page-field-comp-field-edit-list-grid']);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-page-field-comp-edit-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(corePageField.baseUrl + '/{pfId}/comps/queries', {
                            pfId : row.id
                        })
                    })
                });
            }
        }
    },
    
    changeCompCategory : function(index) {
        var row = Gikam.getComp('core-page-field-comp-edit-list-grid').getData()[index];
        this.getPageCompParam().onRowActive(index, row);
    },
    
    getPageCompParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-page-field-comp-edit-list-grid',
            service : 'corePageFieldCompServiceImpl',
            columns : corePageField.getPageCompColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var pageFieldRow = Gikam.getComp('core-page-field-edit-list-grid').getActivedRow();
                    if (!pageFieldRow) {
                        return;
                    }
                    Gikam.create('modal', {
                        url : IFM_CONTEXT + Gikam.choose.getConfig('table').url,
                        width : 600,
                        title : 'CORE.MODULE.SYS.TABLES.MODAL.CHOOSE',
                        onAfterClose : function(rows) {
                            if (rows) {
                                var chooseData = rows.map(function(item) {
                                    return {
                                        tableName : item.id
                                    }
                                });

                                var insertCompUrl = Gikam.printf(corePageField.baseUrl + '/{id}/comps', {
                                    id : pageFieldRow.id
                                });
                                Gikam.getComp('core-page-field-comp-edit-list-grid').insert(insertCompUrl, chooseData).done(function() {});
                            }
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var pageFieldRow = Gikam.getComp('core-page-field-edit-list-grid').getActivedRow();
                    if (!pageFieldRow) {
                        return;
                    }
                    Gikam.getComp('core-page-field-comp-edit-list-grid').deleteRows(Gikam.printf(corePageField.baseUrl + '/{pfId}/comps', {
                        pfId : pageFieldRow.id
                    }));
                }
            } ],
            deleteFormatter : function(row) {
                return row.compId || row.id;
            },
            onLoadSuccess : function(rows) {
                if (Gikam.isNotEmpty(rows)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('core-page-field-comp-field-edit-list-grid').done(function(grid) {
                        grid.cleanData();
                    });
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-page-field-comp-field-edit-list-grid').done(function(grid){
                    var pageCompFields = corePageField[ row.compCategory === 'form' ? 'getPageFormFieldColumns' : 'getPageGridFieldColumns']();
                    grid.refreshColumns(pageCompFields).done(function() {
                        grid.refresh({
                            url : Gikam.printf(corePageField.baseUrl + '/comps/{pfcId}/fields/queries', {
                                pfcId : row.id
                            })
                        });
                    });
                });
            }
        }
    },
    
    getPageCompFieldParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : false,
            id : 'core-page-field-comp-field-edit-list-grid',
            service : 'corePageFieldCompFieldServiceImpl',
            columns : corePageField.getPageGridFieldColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addField();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.deleteField();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.ORGS.BUTTON.MOVE_UP',
                icon : 'move-up',
                onClick : function() {
                    _this.moveUp();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.ORGS.BUTTON.MOVE_DOWN',
                icon : 'move-down',
                onClick : function() {
                    _this.moveDown();
                }
            } ],
            deleteFormatter : function(row) {
                return row.fieldName || row.id;
            }
        }
    },
    
    addField : function() {
        var pageCompRow = Gikam.getComp('core-page-field-comp-edit-list-grid').getActivedRow();
        if (!pageCompRow) {
            return;
        }
        
        Gikam.create('modal', {
            url : IFM_CONTEXT + Gikam.choose.getConfig('table-column').url + '?table=' + pageCompRow.tableName,
            width : 600,
            title : 'CORE.MODULE.SYS.TABLES.FIELDS.MODAL.CHOOSE',
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            fieldName : item.columnName
                        }
                    });

                    var insertFieldUrl = Gikam.printf(corePageField.baseUrl + '/comps/{pfcId}/fields', {
                        pfcId : pageCompRow.id
                    });
                    Gikam.getComp('core-page-field-comp-field-edit-list-grid').insert(insertFieldUrl, chooseData).done(function() {});
                }
            }
        });
    },
    
    deleteField : function() {
        var pageCompRow = Gikam.getComp('core-page-field-comp-edit-list-grid').getActivedRow();
        if (!pageCompRow) {
            return;
        }
        var deleteFieldUrl = Gikam.printf(corePageField.baseUrl + '/comps/{pfcId}/fields', {
            pfcId : pageCompRow.id
        });
        Gikam.getComp('core-page-field-comp-field-edit-list-grid').deleteRows(deleteFieldUrl);
    },
    
    moveUp : function() {
        var selections = Gikam.getComp('core-page-field-comp-field-edit-list-grid').getSelections();
        if (selections.length === 1) {
            var swapUrl = corePageField.baseUrl + '/comps/' + Gikam.getComp('core-page-field-comp-edit-list-grid').getActivedRow().id + '/fields/action/swap/{firstId}/{secondId}';
            Gikam.getComp('core-page-field-comp-field-edit-list-grid').moveUp(swapUrl);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    moveDown : function() {
        var selections = Gikam.getComp('core-page-field-comp-field-edit-list-grid').getSelections();
        if (selections.length === 1) {
            var swapUrl = corePageField.baseUrl + '/comps/' + Gikam.getComp('core-page-field-comp-edit-list-grid').getActivedRow().id + '/fields/action/swap/{firstId}/{secondId}';
            Gikam.getComp('core-page-field-comp-field-edit-list-grid').moveDown(swapUrl);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            west : {
                width : 300,
                items : [ _this.getPageFieldParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 300,
                        items : [ _this.getPageCompParam() ]
                    },
                    center : {
                        items : [ _this.getPageCompFieldParam() ]
                    }
                } ]
            },
            renderTo : workspace.window.$dom,
        });
    },

    init : function() {
        this.create();
    }
};
