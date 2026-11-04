//@ sourceURL=core-code-edit-list.js
coreCode.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-code-edit-list-code-category-grid',
            url : coreCode.codeCategoryUrl + '/queries/raw',
            service : 'coreCodeCategoryServiceImpl',
            columns : coreCode.getCodeCategoryListColumns(),
            deleteFormatter : function (row) {
                return row.codeCategoryName;
            },
            onLoadSuccess : function (data) {
                if (Gikam.isNotEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-code-edit-list-grid" ]);
                }
            },
            onRowActive : function (index, row) {
                Gikam.getAsyncComp('core-code-edit-list-grid').done(function (grid) {
                    grid.refresh({
                        url : coreCode.baseUrl + '/queries',
                        requestData : {
                            codeCategoryId_EQ : row.id
                        }
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.CODECATEGORIES.TAB.ADD',
                        fields : coreCode.getBaseInfoFormFields(),
                        height : 300,
                        url : coreCode.codeCategoryUrl
                    }).done(function (id) {
                        Gikam.getComp('core-code-edit-list-code-category-grid').refresh()
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function () {
                    Gikam.getComp('core-code-edit-list-code-category-grid').deleteRows(coreCode.codeCategoryUrl);
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        return {
            type : 'tab',
            panels : [
                {
                    title : "CORE.MODULE.SYS.CODES.TAB.CODE",
                    items : [ {
                        type : 'grid',
                        id : 'core-code-edit-list-grid',
                        service : 'coreCodeServiceImpl',
                        columns : coreCode.getCodeListColumns(),
                        deleteFormatter : function (row) {
                            return row.codeName;
                        },
                        toolbar : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.INSERT',
                            icon : 'add-row',
                            onClick : function () {
                                Gikam.getComp('core-code-edit-list-grid').insert(coreCode.baseUrl, [{
                                     codeCategoryId :Gikam.getComp('core-code-edit-list-code-category-grid').getActivedRow().id,
                                }]);
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.DELETE',
                            icon : 'remove-row',
                            onClick : function () {
                                Gikam.getComp('core-code-edit-list-grid').deleteRows(coreCode.baseUrl);
                            }
                        } ]
                    } ]
                }
            ]
        };
    },


    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-code-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 800,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },


    init : function () {
        workspace.window.removeTitle();
        this.create();
    }
};