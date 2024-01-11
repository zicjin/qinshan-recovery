emcProject.updateOrderIndexPage = {

    getOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-order-grid',
            url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                id : _this.param.projId
            }),
            columns : emcProject.getOrderColumns(),
            service : 'emcOrderServiceImpl',
            checkOnActive : true,
            readonly : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-update-index-order-item-group-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onOrderActive(row);
            },
            requestData : {
                ID_IN : _this.param.ids
            }
        };
    },

    onOrderActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-update-index-order-item-group-grid').done(function (grid) {
                grid.refresh({
                    url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                        id : row.id
                    })
                });
            })
        }
    },

    getOrderItemGroupGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-order-item-group-grid',
            columns : emcProject.getOrderItemGroupColumns(),
            service : 'emcOrderItemGroupServiceImpl',
            readonly : true,
            onLoadSuccess : function(data) {
            	if(Gikam.isEmpty(data)){
            		return;
            	}
                var url = '/secure/emc/module/mdm/bizmdm/index-categories/queries/select-by-order';
                var allOrders = Gikam.getComp('emc-update-index-order-grid').getData();
                Gikam.post(url, Gikam.getJsonWrapper({}, [ '', allOrders ])).done(function(items) {
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

        var orderSelections = Gikam.getComp('emc-update-index-order-grid').getSelections();
        if (Gikam.isEmpty(orderSelections)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.TIP.NO_ORDER_SELECTED');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/update-order-index', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', orderSelections ], [ 'emcOrderIndexServiceImpl', orderIndexData ])).done(function () {

        }).always(function() {
            workspace.window.closeMask();
        });
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
                                title : 'EMC.MODULE.BP.PROJECT.TITLE.ORDER_INFO',
                                items : [ this.getOrderGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.TITLE.TEST_INFO',
                                items : [ this.getOrderItemGroupGridParam() ]
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
