emcIndexItem.editPage = {

    getItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-index-item-edit-list-grid',
            url : emcIndexItem.baseUrl + '/queries/raw',
            columns : emcIndexItem.getItemListColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.indexItemName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onTestItemActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.INDEXITEMS.MODAL.INDEX_ITEM_ADD',
                        fields : emcIndexItem.getBaseInfoFormFields(),
                        url : emcIndexItem.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-index-item-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-index-item-edit-list-grid').deleteRows(emcIndexItem.baseUrl);
                }
            } ]
        }
    },

    onTestItemActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-index-item-edit-list-test-item-grid').refresh({
                url : _this.getTestItemUrl(row.id),
            });
        }
    },
    getTestItemUrl : function(itemId) {
        if (itemId) {
            return Gikam.printf(emcIndexItem.baseUrl + '/{id}/test-items/queries', {
                id : itemId
            })
        }
    },
    getTestItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-index-item-edit-list-test-item-grid',
            url : _this.getTestItemUrl(),
            columns : emcIndexItem.getTestItemListColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.ext$.testmethodid || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addTestItem();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.removeTestItem();
                }
            }, ]
        }
    },

    addTestItem : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-index-item-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.INDEXITEMS.TIP.CHOOSE_ONE');
            return;
        }
        var url = Gikam.printf(emcIndexItem.baseUrl + '/{id}/test-items', {
            id : activeRow.id
        });
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testItem').title,
            url : Gikam.choose.getConfig('testItem').url + Gikam.param({
                target_unassigned : '1',
                //itemAliasName_CISC : activeRow.indexItemName,
                targetTable : 'T_EMC_INDEX_TEST_ITEM',
                targetItemIdColumn : 'ITEMID',
                targetFilterColumn : 'INDEXITEMID',
                targetFilterValue : activeRow.id,
                reportdisplay_SEQ : '1'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        itemId : item.id,
                        testId : item.ext$.testid,
                        methodId : item.ext$.methodid,
                        indextestmethodId : item.testMethodId,
                    }
                });
                Gikam.getComp('emc-index-item-edit-list-test-item-grid').insert(url, chooseData);
            },
        });
    },

    removeTestItem : function() {
        Gikam.getComp('emc-index-item-edit-list-test-item-grid').deleteRows(Gikam.printf(emcIndexItem.baseUrl + '/{id}/test-items', {
            id : Gikam.getComp('emc-index-item-edit-list-grid').getActivedRow().id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-index-item-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '214',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.INDEXITEMS.TAB.BASE_INFO',
                        items : [ _this.getItemGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.INDEXITEMS.TAB.TEST',
                        items : [ _this.getTestItemGridParam() ]
                    } ]
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
