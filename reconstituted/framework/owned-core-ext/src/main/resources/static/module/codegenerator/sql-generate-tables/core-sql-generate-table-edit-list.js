coreSqlGenerateTable.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-sql-generate-table-edit-list-grid',
            url : coreSqlGenerateTable.baseUrl + '/queries/raw',
            columns : coreSqlGenerateTable.getEditListColumns(),
            page : false,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-sql-generate-table-edit-list-add-field-grid', 'core-sql-generate-table-edit-list-remove-field-grid' ]);
                }
            },
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('core-sql-generate-table-edit-list-add-field-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreSqlGenerateTable.baseUrl + '/{id}/fields/queries', {
                            id : row.id
                        }),
                        requestData : {
                            type_SEQ : 'add'
                        }
                    });
                });

                Gikam.getAsyncComp('core-sql-generate-table-edit-list-remove-field-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreSqlGenerateTable.baseUrl + '/{id}/fields/queries', {
                            id : row.id
                        }),
                        requestData : {
                            type_SEQ : 'remove'
                        }
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.CODEGENERATOR.SQLGENERATETABLES.MODAL.ADD',
                        fields : coreSqlGenerateTable.getBaseInfoFormFields(),
                        url : coreSqlGenerateTable.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-sql-generate-table-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-sql-generate-table-edit-list-grid').deleteRows(coreSqlGenerateTable.baseUrl);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.CODEGENERATOR.SQLGENERATETABLES.BUTTON.SQL_GENERATE',
                icon : 'export',
                onClick : function() {
                    _this.generateSql();
                }
            } ]
        }
    },

    getAddFieldGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.CODEGENERATOR.SQLGENERATETABLES.TAB.ADD_FIELD',
                items : [ {
                    type : 'grid',
                    id : 'core-sql-generate-table-edit-list-add-field-grid',
                    page : false,
                    service : 'coreSqlGenerateFieldServiceImpl',
                    deleteFormatter : function(row) {
                        return row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            Gikam.getComp('core-sql-generate-table-edit-list-add-field-grid').insert(Gikam.printf(coreSqlGenerateTable.baseUrl + '/{id}/fields', {
                                id : Gikam.getComp('core-sql-generate-table-edit-list-grid').getActivedRow().id
                            }), [ {
                                type : 'add',
                                fieldType : 'varchar'
                            } ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-sql-generate-table-edit-list-add-field-grid').deleteRows(Gikam.printf(coreSqlGenerateTable.baseUrl + '/{id}/fields', {
                                id : Gikam.getComp('core-sql-generate-table-edit-list-grid').getActivedRow().id
                            }));
                        }
                    } ],
                    columns : coreSqlGenerateTable.getAddFieldListColumns()
                } ]
            } ]
        };
    },

    getRemoveFieldGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.CODEGENERATOR.SQLGENERATETABLES.TAB.REMOVE_FIELD',
                items : [ {
                    type : 'grid',
                    id : 'core-sql-generate-table-edit-list-remove-field-grid',
                    service : 'coreSqlGenerateFieldServiceImpl',
                    page : false,
                    deleteFormatter : function(row) {
                        return row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            Gikam.getComp('core-sql-generate-table-edit-list-remove-field-grid').insert(Gikam.printf(coreSqlGenerateTable.baseUrl + '/{id}/fields', {
                                id : Gikam.getComp('core-sql-generate-table-edit-list-grid').getActivedRow().id
                            }), [ {
                                type : 'remove'
                            } ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-sql-generate-table-edit-list-remove-field-grid').deleteRows(Gikam.printf(coreSqlGenerateTable.baseUrl + '/{id}/fields', {
                                id : Gikam.getComp('core-sql-generate-table-edit-list-grid').getActivedRow().id
                            }));
                        }
                    } ],
                    columns : coreSqlGenerateTable.getRemoveFieldListColumns()
                } ]
            } ]
        };
    },

    generateSql : function() {

        var tableGrid = Gikam.getComp('core-sql-generate-table-edit-list-grid');

        var selections = tableGrid.getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (!(tableGrid.validate() && Gikam.getComp('core-sql-generate-table-edit-list-add-field-grid').validate() && Gikam.getComp('core-sql-generate-table-edit-list-remove-field-grid').validate())) {
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(coreSqlGenerateTable.baseUrl + '/action/generate-sql', Gikam.getJsonWrapper(null, [ '', selections ])).done(function(sqlData) {
            coreSqlGenerateTable.generateSql = JSON.parse(sqlData);

            Gikam.create('modal', {
                width : '60%',
                title : 'CORE.MODULE.CODEGENERATOR.SQLGENERATETABLES.MODAL.GENERATE_SQL',
                url : coreSqlGenerateTable.baseUrl + '/page/sql'
            });
        }).always(function() {
            workspace.window.closeMask();
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-sql-generate-table-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '30%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ this.getAddFieldGrid() ]
                    },
                    center : {
                        items : [ this.getRemoveFieldGrid() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
