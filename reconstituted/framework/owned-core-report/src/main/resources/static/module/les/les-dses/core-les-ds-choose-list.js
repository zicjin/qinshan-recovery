coreLesDs.choosePage = {

    getDsChooseGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-les-ds-choose-list-grid',
            url : coreLesDs.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : coreLesDs.getDsChooseListColumns(_this.param.dsSingle),
            page : false,
            filterOpen : true,
            activeOnCheck : true,
            onRowActive : function(index, row) {
                var lesDsParam = _this.param.params;
                var dsFieldGrid = Gikam.getComp('core-les-ds-field-choose-list-grid');
                
                dsFieldGrid.setOptions({
                    url : Gikam.printf(coreLesDs.baseUrl + '/{id}/fields/queries', {
                        id : row.id
                    })
                });
                if (Gikam.isEmpty(lesDsParam)) {
                    dsFieldGrid.refresh();
                } else {
                    var selectedLesDsFieldsId = null;
                    var lesDsParamObj = JSON.parse(lesDsParam);
                    for (var i = 0; i < lesDsParamObj.length; i++) {
                        if (lesDsParamObj[i].lesDsId == row.id) {
                            selectedLesDsFieldsId = lesDsParamObj[i].ids;
                            break;
                        }
                    }
                    if (Gikam.isEmpty(selectedLesDsFieldsId)) {
                        dsFieldGrid.refresh();
                    } else {
                        dsFieldGrid.refresh().done(function() {
                            for (var i = 0; i < selectedLesDsFieldsId.length; i++) {
                                dsFieldGrid.selectRowById(selectedLesDsFieldsId[i]);
                            }
                        });
                    }
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-les-ds-field-choose-list-grid' ]);
                }
            }
        }
    },

    getDsFieldChooseGridParam : function() {
        return {
            type : 'grid',
            page : true,
            id : 'core-les-ds-field-choose-list-grid',
            page : false,
            columns : coreLesDs.getDsFieldChooseListColumns(this.param.dsFieldSingle)
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
                        var selections = Gikam.getComp('core-les-ds-field-choose-list-grid').getSelections();
                        selections.forEach(function(item) {
                            item.lesDsName = Gikam.getComp('core-les-ds-choose-list-grid').getSelections()[0].lesDsName;
                        });
                        Gikam.getLastModal().close(selections);
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
            west : {
                width : '50%',
                items : [ _this.getBtnToolbarParam(), _this.getDsChooseGridParam() ]
            },
            center : {
                items : [ _this.getDsFieldChooseGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};