coreTable.columnChoosePage = {
    getTableColumnGridParam : function() {
        var _this = this, columns = coreTable.getTableColumnChooseListColumns();
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            page : true,
            filter : false,
            id : 'core-table-column-choose-list-grid',
            url : Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/tables/{table}/columns/queries/choosable', {
                table : _this.param.table
            }),
            filterOpen : true,
            columns : columns
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('core-table-column-choose-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getBtnToolbarParam(), this.getTableColumnGridParam() ]
            }
        });
    },

    tableName : null,

    init : function(param) {
        this.param = param;
        if (Gikam.isEmpty(param.table)) {
            Gikam.alert('CORE.MODULE.SYS.TABLES.TIP.CHOOSE_TABLE');
            return;
        }
        this.tableName = param.table;
        this.create();
    }
};