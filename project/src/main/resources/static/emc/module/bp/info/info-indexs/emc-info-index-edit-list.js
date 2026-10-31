emcInfoIndex.editPage = {

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
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.INFO.INFOINDEXS.MODAL.ADD_INDEX',
                        fields : emcInfoIndex.getBaseInfoFormFields(),
                        url : emcInfoIndex.baseUrl,
                        width : 350,
                        height : 190,
                        titleWidth : 65
                    }).done(function(id) {
                        Gikam.getComp('emc-info-index-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-index-edit-list-grid').deleteRows(emcInfoIndex.baseUrl);
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
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-info-index-edit-list-category-grid').activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-info-index-edit-list-category-index-grid')) {
                        Gikam.getComp('emc-info-index-edit-list-category-index-grid').cleanData();
                    }
                    if (Gikam.getComp('emc-info-index-edit-list-category-section-grid')) {
                        Gikam.getComp('emc-info-index-edit-list-category-section-grid').cleanData();
                    }
                }
            },
            deleteFormatter : function(row) {
                return row.category;
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-info-index-edit-list-category-index-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcInfoIndex.baseUrl + '/{categoryId}/categoryindexs/queries', {
                            categoryId : row.id
                        }),
                    });
                });
                Gikam.getAsyncComp('emc-info-index-edit-list-category-section-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcInfoIndex.baseUrl + '/{categoryId}/categorysections/queries', {
                            categoryId : row.id
                        }),
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var indexRow = _this.getIndexRow();
                    if (!indexRow) {
                        Gikam.alert("EMC.MODULE.BP.INFO.INFOINDEXS.TIP.ADD_CATEGORY_UNSELECTED");
                        return;
                    }
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.INFO.INFOINDEXS.MODAL.ADD_CATEGORY',
                        fields : emcInfoIndex.getCategoryBaseInfoFormFields(),
                        url : Gikam.printf(emcInfoIndex.baseUrl + '/{indexId}/categorys', {
                            indexId : indexRow.id
                        }),
                        width : 350,
                        height : 150,
                        titleWidth : 40
                    }).done(function(id) {
                        Gikam.getComp('emc-info-index-edit-list-category-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-index-edit-list-category-grid').deleteRows(emcInfoIndex.baseUrl + '/categorys');
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.INFO.INFOINDEXS.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var categoryRow = _this.getCategoryRow();
                    if (!categoryRow) {
                        Gikam.alert("EMC.MODULE.BP.INFO.INFOINDEXS.TIP.COPY_CATEGORY_UNSELECTED");
                        return;
                    }
                    var url = Gikam.printf(emcInfoIndex.baseUrl + '/{categoryId}/categorys/copy', {
                        categoryId : categoryRow.id
                    });
                    Gikam.getComp('emc-info-index-edit-list-category-grid').insert(url).done(function(id) {
                        Gikam.getComp('emc-info-index-edit-list-category-grid').refresh();
                    });
                }
            } ]
        }
    },

    // 指标值
    getCategoryIndexGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-index-edit-list-category-index-grid',
            service : 'emcCategoryIndexServiceImpl',
            columns : emcInfoIndex.getCategoryIndexEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-info-index-edit-list-category-index-grid').activeRowByIndex(0);
                }
            },
            deleteFormatter : function(row) {
                return row.itemName;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var categoryRow = _this.getCategoryRow();
                    if (!categoryRow) {
                        Gikam.alert("EMC.MODULE.BP.INFO.INFOINDEXS.TIP.ADD_CATEGORY_INDEX_UNSELECTED");
                        return;
                    }
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.INFO.INFOINDEXS.MODAL.ADD_CATEGORY_INDEX',
                        fields : emcInfoIndex.getCategoryIndexBaseInfoFormFields(),
                        url : Gikam.printf(emcInfoIndex.baseUrl + '/{categoryId}/categoryindexs', {
                            categoryId : categoryRow.id
                        }),
                        width : 400,
                        height : 190,
                        titleWidth : 80
                    }).done(function(id) {
                        Gikam.getComp('emc-info-index-edit-list-category-index-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-index-edit-list-category-index-grid').deleteRows(emcInfoIndex.baseUrl + '/categoryindexs');
                }
            } ]
        }
    },

    // 断面属性
    getCategorySectionGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-index-edit-list-category-section-grid',
            service : 'emcCategorySectionServiceImpl',
            columns : emcInfoIndex.getCategorySectionEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-info-index-edit-list-category-section-grid').activeRowByIndex(0);
                }
            },
            deleteFormatter : function(row) {
                return row.monitorPointName;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var categoryRow = _this.getCategoryRow();
                    if (!categoryRow) {
                        Gikam.alert("EMC.MODULE.BP.INFO.INFOINDEXS.TIP.ADD_CATEGORY_SECTION_UNSELECTED");
                        return;
                    }
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.INFO.INFOINDEXS.MODAL.ADD_CATEGORY_SECTION',
                        fields : emcInfoIndex.getCategorySectionBaseInfoFormFields(),
                        url : Gikam.printf(emcInfoIndex.baseUrl + '/{categoryId}/categorysections', {
                            categoryId : categoryRow.id
                        }),
                        width : 400,
                        height : 150,
                        titleWidth : 60
                    }).done(function(id) {
                        Gikam.getComp('emc-info-index-edit-list-category-section-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-index-edit-list-category-section-grid').deleteRows(emcInfoIndex.baseUrl + '/categorysections');
                }
            } ]
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
            renderTo : workspace.window.$dom,
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
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.INFO.INFOINDEXS.TAB.CATEGORY',
                                items : [ _this.getCategoryGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.INFO.INFOINDEXS.TAB.CATEGORYINDEX',
                                items : [ _this.getCategoryIndexGridParam() ]
                            }, {
                                title : 'EMC.MODULE.BP.INFO.INFOINDEXS.TAB.CATEGORYSECTION',
                                items : [ _this.getCategorySectionGridParam() ]
                            } ]
                        } ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
