coreTest.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-test-edit-list-grid',
            url : coreTest.baseUrl + '/queries/raw',
            columns : coreTest.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-test-edit-list-item-grid' ]);
                }
            },
            deleteFormatter : function(row) {
                return row.testName || row.id;
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('core-test-edit-list-item-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreTest.baseUrl + '/{id}/items/queries', {
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
                        modalTitle : 'CORE.MODULE.SCRIPT.TESTS.MODAL.ADD',
                        fields : coreTest.getBaseInfoFormFields(),
                        url : coreTest.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-test-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-test-edit-list-grid').deleteRows(coreTest.baseUrl);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SCRIPT.TESTS.BUTTON.GENERATE_RESULT',
                icon : 'submit',
                onClick : function() {
                    // 生成结果
                    Gikam.postText(coreTest.baseUrl + '/action/generate-result').done(function() {
                        Gikam.alert('CORE.MODULE.SCRIPT.TESTS.TIP.GENERATE_RESULT_SUCCESS');
                    });

                }
            } ]
        }
    },

    getItemTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.ITEM',
                items : [ {
                    type : 'grid',
                    id : 'core-test-edit-list-item-grid',
                    service : 'coreItemServiceImpl',
                    deleteFormatter : function(row) {
                        return row.itemName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {

                            Gikam.preInsert({
                                modalTitle : 'CORE.MODULE.SCRIPT.TESTS.MODAL.ADD_ITEM',
                                fields : coreTest.getItemFormFields(),
                                url : Gikam.printf(coreTest.baseUrl + '/{id}/items', {
                                    id : Gikam.getComp('core-test-edit-list-grid').getActivedRow().id
                                })
                            }).done(function(id) {
                                Gikam.getComp('core-test-edit-list-item-grid').refresh();
                            });

                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-test-edit-list-item-grid').deleteRows(Gikam.printf(coreTest.baseUrl + '/{id}/items', {
                                id : Gikam.getComp('core-test-edit-list-grid').getActivedRow().id
                            }));
                        }
                    } ],
                    columns : coreTest.getItemListColumns()
                } ]
            } ]
        }
    },

    loadScriptDetailPage : function(itemId, testId) {
        var _this = this;
        Gikam.create('modal', {
            width : 1000,
            height : 600,
            title : 'CORE.MODULE.SCRIPT.TESTS.MODAL.SCRIPT',
            url : coreTest.baseUrl + '/page/item-script-detail' + Gikam.param({
                itemId : itemId,
                testId : testId
            }),
            afterClose : function() {
                Gikam.getComp('core-test-edit-list-item-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-test-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 320,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getItemTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
