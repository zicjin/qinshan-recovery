coreLesTemplate.weighingEditListPage = {
    grid : null,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-les-template-weighing-list-grid',
            columns : coreLesTemplate.getWeighingListColumns(),
            toolbar : [],
            onRendered : function() {
                _this.grid = this;
                this.loadData([]);
            },
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
                        Gikam.getLastModal().close(_this.grid.getData());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        var exist_rows = _this.grid.getData();
                        var rows = exist_rows && exist_rows.length ? (exist_rows.unshift({}), exist_rows) : [ {} ];
                        _this.grid.selectedRowIndex = [];
                        _this.grid.loadData(rows);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        var select_rows = _this.grid.getSelections();
                        var exist_rows = _this.grid.getData();
                        outflag :
                        for (var i = exist_rows.length - 1; i >= 0; i--) {
                            for (var j = select_rows.length - 1; j >= 0; j--) {
                                if (JSON.stringify(exist_rows[i]) == JSON.stringify(select_rows[j])) {
                                    exist_rows.splice(i, 1);
                                    select_rows.splice(j, 1);
                                    continue outflag;
                                }
                            }
                        }
                        _this.grid.selectedRowIndex = [];
                        _this.grid.loadData(exist_rows);
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.LES_TEMPLATES.BUTTON.CONNECT',
                    icon : 'synchro',
                    id : 'connect',
                    onRendered : function() {
                        if (serialListener && serialListener.equipt) {
                            this.hide();
                        }
                    },
                    onClick : function() {
                        _this.collectionPage();
                    }
                }, {
                    type : 'button',
                    text : 'CORE.MODULE.ITEM.LES_TEMPLATES.BUTTON.DISCONNECT',
                    id : 'disconnect',
                    icon : 'break',
                    onRendered : function() {
                        if (serialListener && !serialListener.equipt) {
                            this.hide();
                        }
                    },
                    onClick : function() {
                        _this.disconnect();
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
                items : [ this.getBtnToolbarParam(), this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    },
    collectionPage : function() {
        Gikam.create('modal', {
            width : Gikam.width.M + 30,
            title : 'CORE.MODULE.ITEM.LES_TEMPLATES.MODAL.CONNECT',
            url : IFM_CONTEXT + '/secure/pharm/module/equipt/equipts/page/collection' + Gikam.param({
                    action : 'coreLesTemplate.weighingEditListPage.action'
                }),
            onAfterClose : function(object) {}
        });
    },
    
    action : function(responseValue, getedValue, updateButton) {
        var grid = Gikam.getComp('core-les-template-weighing-list-grid');
        var row = grid.getActivedRow();
        if (row && responseValue && getedValue) {
            grid.setData({
                index : row.index,
                weight : getedValue,
            })
        } else if (updateButton == true) {
            Gikam.getComp('connect').hide();
            Gikam.getComp('disconnect').show();
        } else if (updateButton == false) {
            Gikam.getComp('connect').show();
            Gikam.getComp('disconnect').hide();
        }
    },
    
    disconnect : function() {
        serialListener.clear();
    },
};