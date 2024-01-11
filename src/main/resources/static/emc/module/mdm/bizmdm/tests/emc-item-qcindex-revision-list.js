emcTestMethod.qcindexRevisionListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.QC_INDEX',
                items : [ {
                    type : 'grid',
                    id : 'emc-item-qc-index-list-grid',
                    service : 'emcItemQcIndexServiceImpl',
                    url : _this.getItemQcIndexUrl(_this.param.itemId),
                    deleteFormatter : function(row) {
                        return row.decisionCategory || row.id;
                    },
                    columns : emcTestMethod.getQcIndexListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertQcIndexRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQcIndexRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyQcIndex();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyQcIndexTo();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {
                            Gikam.viewAudit('T_EMC_ITEM_QCINDEX', Gikam.getComp('emc-item-qc-index-list-grid').getSelections());
                        }
                    } ],
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.ITEM_REVISION',
                items : [ {
                    type : 'grid',
                    id : 'emc-item-revision-list-grid',
                    service : 'emcItemRevisionServiceImpl',
                    url : _this.getItemRevisionUrl(_this.param.itemId),
                    deleteFormatter : function(row) {
                        return row.revisionRule || row.id;
                    },
                    columns : emcTestMethod.getRevisionListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertRevisionRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteRevisionRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyRevision();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyRevisionTo();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEWAUDIT',
                        icon : 'view-audit',
                        onClick : function() {
                            Gikam.viewAudit('T_EMC_ITEM_REVISION', Gikam.getComp('emc-item-revision-list-grid').getSelections());
                        }
                    } ],
                } ]
            } ]
        }
    },

    copyQcIndex : function() {
        var _this = this;

        var selections = Gikam.getComp('emc-item-qc-index-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/qc-indexes', {
            itemId : _this.param.itemId
        });

        Gikam.getComp('emc-item-qc-index-list-grid').insert(url, selections);
    },

    copyQcIndexTo : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-item-qc-index-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        selections.map(function(item) {
            item.itemName = _this.param.itemName;
        });
        var url = emcTestMethod.baseUrl + '/items/qc-indexes/copy-to';

        Gikam.create('modal', {
            width : 1200,
            height : 600,
            title : Gikam.choose.getConfig('testItem').title,
            url : emcTestMethod.baseUrl + '/page/item-choose-list'+ Gikam.param({
                'testMethodId_order' : 'desc'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, [ 'emcItemQcIndexServiceImpl', selections ], [ 'emcItemServiceImpl', rows ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    /** 复制分段修约 */
    copyRevision : function() {
        var selections = Gikam.getComp('emc-item-revision-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var url = emcTestMethod.baseUrl + '/items/revisions/action/copy';
        Gikam.getComp('emc-item-revision-list-grid').insert(url, selections);
    },

    /** 复制分段修约到其他分析项 */
    copyRevisionTo : function() {
        var selections = Gikam.getComp('emc-item-revision-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var url = emcTestMethod.baseUrl + '/items/revisions/action/copy-to';
        Gikam.create('modal', {
            width : 1200,
            height : 600,
            title : Gikam.choose.getConfig('testItem').title,
            url : emcTestMethod.baseUrl + '/page/item-choose-list'+ Gikam.param({
                'testMethodId_order' : 'desc'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper(null, [ 'emcItemRevisionServiceImpl', selections ], [ 'emcItemServiceImpl', rows ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    insertQcIndexRow : function() {
        var _this = this;
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/qc-indexes', {
            itemId : _this.param.itemId
        });

        Gikam.getComp('emc-item-qc-index-list-grid').insert(url, [ {
            itemId : _this.param.itemId
        } ]);
    },

    deleteQcIndexRow : function() {
        var _this = this;
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/qc-indexes', {
            itemId : _this.param.itemId
        });
        Gikam.getComp('emc-item-qc-index-list-grid').deleteRows(url);
    },

    insertRevisionRow : function() {
        var _this = this;
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/revisions', {
            itemId : _this.param.itemId
        });

        Gikam.getComp('emc-item-revision-list-grid').insert(url, [ {
            itemId : _this.param.itemId
        } ]);
    },

    deleteRevisionRow : function() {
        var _this = this;
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/revisions', {
            itemId : _this.param.itemId
        });
        Gikam.getComp('emc-item-revision-list-grid').deleteRows(url);
    },

    getItemQcIndexUrl : function(itemId) {
        if (itemId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/qc-indexes/queries', {
                itemId : itemId
            })
        }
    },

    getItemRevisionUrl : function(itemId) {
        if (itemId) {
            return Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/revisions/queries', {
                itemId : itemId
            })
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
