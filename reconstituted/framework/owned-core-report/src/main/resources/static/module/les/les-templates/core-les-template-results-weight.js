coreLesTemplate.resultsWeightPage = {

    create : function() {
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                items : [ this.getBtnToolbarParam() ]
            },
            west : {
                width : '50%',
                items : [ this.getEquiptGridParam() ]
            },
            center : {
                items : [ this.getResultGridParam() ]
            }
        });
    },

    getResultGridParam : function() {
        return {
            type : 'grid',
            id : 'core-les-template-results-weight-result-grid',
            url : Gikam.printf(IFM_CONTEXT + '/secure/pharm/module/sp/sp-results/{unitId}/suitability-units/queries', {
                unitId : this.param.unitId
            }),
            columns : coreLesTemplate.getResultColumns(this.param.type)
        };
    },

    getEquiptGridParam : function() {
        return {
            type : 'grid',
            url : pharmEquipt.baseUrl + '/queries/raw',
            id : 'pharm-collection-equipt-grid',
            columns : pharmEquipt.getEquiptCollectionEquiptList()
        };
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
                        _this.saveResults().done(function() {
                            Gikam.getLastModal().close();
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            }, {
                type : 'btnGroup',
                items : [ {
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
                        pharmEquipt.collectionPage.connectEquipt(Gikam.getComp('pharm-collection-equipt-grid').getSelections());
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
                        serialListener.clear();
                    }
                } ]
            } ]
        }
    },
    
    saveResults : function() {
        var data = Gikam.getComp('core-les-template-results-weight-result-grid').getData().filter(function(item) {
            return Gikam.isNotEmpty(item.revisionResult);
        }).map(function(item) {
            return {
                id : item.id,
                revisionResult : item.revisionResult
            }
        });
        if (Gikam.isEmpty(data)) {
            return $.Deferred().resolve();
        } else {
            workspace.window.showMask(true);
            return Gikam.put(Gikam.printf(IFM_CONTEXT + '/secure/pharm/module/sp/sp-results/{unitId}/suitability-units', {
                unitId : this.param.unitId
            }), Gikam.getJsonWrapper(null, ['', data])).always(function() {
                workspace.window.closeMask();
            });
        }
        
    },

    setWeightValue : function(responseValue, getedValue, updateButton) {
        var grid = Gikam.getComp('core-les-template-results-weight-result-grid');
        var row = grid.getActivedRow();
        if (row && responseValue && getedValue) {
            grid.setData({
                index : row.index,
                revisionResult : getedValue,
            })
        }
        if (updateButton) {
            Gikam.getComp('connect').hide(), Gikam.getComp('disconnect').show();
        } else if (updateButton === false) {
            Gikam.getComp('connect').show(), Gikam.getComp('disconnect').hide();
        }
    },

    init : function(param) {
        this.param = param;
        pharmEquipt.collectionPage.param = {
            action : 'coreLesTemplate.resultsWeightPage.setWeightValue'
        };
        this.create();
    }
};