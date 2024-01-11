emcProjScheme.updateIndexPage = {

    getProjSchemeGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-proj-scheme-grid',
            url : emcProjScheme.baseUrl + '/queries/raw',
            columns : emcProjScheme.getUpdateIndexSchemeListColumns(),
            service : 'emcProjSchemeServiceImpl',
            checkOnActive : true,
            editor : false,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(1);
                } else {
                    Gikam.getComp('emc-update-index-proj-order-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onSchemeActive(row);
            },
            onRendered : function() {
                this.hideColumns([ 'ext$.monitortmplname' ]);
            },
            group : {
                fields : [ 'ext$.monitortmplname' ],
                formatter : function(row) {
                    return Gikam.propI18N('T_EMC_PROJ_SCHEME.ORDERCATEGORY') + ':' + row.ext$.monitortmplname;
                }
            },
            requestData : {
                ID_IN : _this.param.ids
            }
        };
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-update-index-proj-order-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getOrderUrl(row.id)
                });
            });
        }
    },

    getOrderUrl : function(projSchemeId) {
        if (projSchemeId) {
            return Gikam.printf(emcProjScheme.baseUrl + '/{id}/proj-orders/queries', {
                id : projSchemeId
            })
        }
    },

    getProjOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-proj-order-grid',
            url : _this.getOrderUrl(),
            editor : false,
            columns : emcProjScheme.getUpdateIndexOrderColumns(),
            service : 'emcProjOrderServiceImpl',
            onLoadSuccess : function(data) {
            	if(Gikam.isEmpty(data)){
            		return;
            	}
                var url = '/secure/emc/module/mdm/bizmdm/index-categories/queries/selectByProjScheme';
                var allProjSchemes = Gikam.getComp('emc-update-index-proj-scheme-grid').getData();
                var projSchemes = [];
                allProjSchemes.map(function(item) {

                    if( Gikam.isNumber(item.id) ){
                    	projSchemes.push(item);
                    }
                });
                Gikam.post(url, Gikam.getJsonWrapper({}, [ '', projSchemes ])).done(function(items) {
                	Gikam.getComp('emc-update-index-index-category-select-form').setSelectOptions('indexCategoryId', items.rows.map(function(item) {
                        return {
                            text : item.stdName,
                            value : item.id
                        };
                    }), true);
                });
            },
        };
    },

    // 指标类别下拉框
    getOrderIndexSelectForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-update-index-index-category-select-form',
            columns : 1,
            titleWidthAuto : true,
            fields : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.INDEX_CATEGORY',
                field : 'indexCategoryId',
                type : 'select',
                search : true,
                onChange : function(field, value) {
                    if (!Gikam.isEmpty(value)) {
                        Gikam.getAsyncComp('emc-update-index-order-index-grid').done(function(grid) {
                            grid.refresh({
                                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/{id}/order-indexes/queries', {
                                    id : value
                                }),
                            });
                        });
                    } else {
                        var indexSetGrid = Gikam.getComp('emc-update-index-index-set-grid');
                        if (!Gikam.isEmpty(indexSetGrid)) {
                            indexSetGrid.cleanData();
                        }
                    }
                }
            } ]
        };
    },

    // 指标表
    getOrderIndexGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-order-index-grid',
            columns : emcProjScheme.getUpdateIndexOrderIndexColumns(),
            service : 'emcOrderIndexServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-update-index-order-index-grid').activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-update-index-index-set-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-update-index-index-set-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/order-indexes/{indexId}/index-sets/queries', {
                            indexId : row.id
                        }),
                    });
                });

            },
            toolbar : [ this.getOrderIndexSelectForm(), {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'edit',
                onClick : function() {
                    _this.updateOrderIndex();
                }
            } ]
        };
    },

    // 指标分析项表
    getIndexSetGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-index-set-grid',
            columns : emcProjScheme.getUpdateIndexIndexSetColumns(),
            service : 'emcIndexSetServiceImpl'
        };
    },

    // 更换指标
    updateOrderIndex : function() {
        var orderIndexData = Gikam.getComp('emc-update-index-order-index-grid').getSelections();
        if (Gikam.isEmpty(orderIndexData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_INDEX');
            return;
        }

        var projSchemeSelections = Gikam.getComp('emc-update-index-proj-scheme-grid').getSelections();

        if (Gikam.isEmpty(projSchemeSelections)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_FOLDER');
            return;
        } else {
            var selectedIds = projSchemeSelections.map(function(item) {
                if (!Gikam.isEmpty(item.id)) {
                    return {
                        id : item.id
                    }
                }
            });
        }

        if (Gikam.isEmpty(selectedIds)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_FOLDER');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postSync(emcProjScheme.baseUrl + '/action/update-order-index', Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', selectedIds ], [ 'emcOrderIndexServiceImpl', orderIndexData ])).always(function() {
            workspace.window.closeMask();
        });
        Gikam.getComp('emc-update-index-proj-scheme-grid').refresh();

    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : '50%',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.FOLDER',
                                items : [ this.getProjSchemeGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.SCHEME',
                                items : [ this.getProjOrderGridParam() ]
                            } ]
                        } ]
                    }
                } ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.INDEX',
                                items : [ this.getOrderIndexGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.INDEX_ITEM',
                                items : [ this.getIndexSetGridParam() ]
                            } ]
                        } ]
                    }
                } ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};
