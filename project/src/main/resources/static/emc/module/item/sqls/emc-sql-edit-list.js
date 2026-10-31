emcSql.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sql-edit-list-grid',
            url : emcSql.baseUrl + '/queries/raw',
            columns : emcSql.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-sql-edit-list-base-info-form' ]);
                }
            },
            deleteFormatter : function(row) {
                return row.sqlNo || row.id;
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-sql-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcSql.baseUrl + '/{id}', {
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
                        modalTitle : 'EMC.MODULE.ITEM.SQLS.MODAL.ADD',
                        fields : emcSql.getBaseInfoFormFields(),
                        url : emcSql.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-sql-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-sql-edit-list-grid').deleteRows(emcSql.baseUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.SQLS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcSqlServiceImpl',
                    id : 'emc-sql-edit-list-base-info-form',
                    fields : emcSql.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-sql-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                    columns : 1
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sql-edit-list-layout',
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
