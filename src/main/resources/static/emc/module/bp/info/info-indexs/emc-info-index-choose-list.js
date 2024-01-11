//@ sourceURL=emc-info-index-choose-list.js
emcInfoIndex.choosePage = {
        // 评价指标
        getGridParam : function() {
            var _this = this;
            return {
                type : 'grid',
                id : 'emc-info-index-edit-list-grid',
                url : emcInfoIndex.baseUrl + '/queries/raw',
                service : 'emcInfoIndexServiceImpl',
                columns : emcInfoIndex.getEditListColumns(),
                onLoadSuccess : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        Gikam.getComp('emc-info-index-edit-list-grid').activeRowByIndex(0);
                    } else if (Gikam.getComp('emc-info-index-edit-list-category-grid')) {
                        Gikam.getComp('emc-info-index-edit-list-category-grid').cleanData();
                    }
                },
                deleteFormatter : function(row) {
                    return row.indexName;
                },
                onRowActive : function(index, row) {
                    Gikam.getAsyncComp('emc-info-index-edit-list-category-grid').done(function(grid) {
                        grid.refresh({
                            url : Gikam.printf(emcInfoIndex.baseUrl + '/{indexId}/categorys/queries', {
                                indexId : row.id
                            }),
                        });
                    });
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-info-index-edit-list-category-grid').getSelections();
                        if (selections.length != 1) {
                            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_DATA");
                            return;
                        }
                        var activeRow = Gikam.getComp('emc-info-index-edit-list-grid').getActivedRow();
                        selections[0].indexName = activeRow.indexName; 
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
            }
        },

        // 分类
        getCategoryGridParam : function() {
            var _this = this;
            return {
                type : 'grid',
                id : 'emc-info-index-edit-list-category-grid',
                service : 'emcInfoIndexCategoryServiceImpl',
                columns : emcInfoIndex.getCategoryEditListColumns(),
            }
        },


        getIndexRow : function() {
            var grid = Gikam.getComp('emc-info-index-edit-list-grid');
            if (!grid || !grid.getActivedRow())
                return null;
            return grid.getActivedRow();
        },

        getCategoryRow : function() {
            var grid = Gikam.getComp('emc-info-index-edit-list-category-grid');
            if (!grid || !grid.getActivedRow())
                return null;
            return grid.getActivedRow();
        },

        create : function() {
            var _this = this;
            Gikam.create('layout', {
                id : 'emc-info-index-edit-list-layout',
                renderTo : Gikam.getLastModal().window.$dom,
                west : {
                    width : 300,
                    items : [ {
                        type : 'tab',
                        fill : true,
                        panels : [ {
                            title : 'EMC.MODULE.BP.INFO.INFOINDEXS.TAB.INDEX',
                            items : [ _this.getGridParam() ]
                        } ]
                    } ]
                },
                center : {
                     items : [ {
                         type : 'tab',
                         fill : true,
                         panels : [ {
                             title : 'EMC.MODULE.BP.INFO.INFOINDEXS.TAB.CATEGORY',
                             items : [ _this.getCategoryGridParam() ]
                         } ]
                     } ]
                }
            });
        },

        init : function(param) {
            this.param = param;
            this.create();
        }
};