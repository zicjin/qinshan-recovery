coreHelpDocument.searchPage = {

    activeTreeId : null,

    getGridParam : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'core-help-document-catalog-search-list-tree',
            name : 'CORE.MODULE.ITEM.HELP_DOCUMENTS.TREE.CATALOG',
            url : coreHelpDocument.baseUrl + '/edit-tree/root',
            service : 'coreHelpDocumentCatalogServiceImpl',
            dbTable : 'T_CORE_HELP_DOCUMENT_CATALOG',
            async : false,
            baseUrl : coreHelpDocument.baseUrl,
            nodeTextField : 'helpDocumentCatalog',
            draggable : false,
            edit : false,
            onAfterDrop : function(drag, parent, previous) {
                Gikam.put(coreHelpDocument.baseUrl + '/action/drag', Gikam.getJsonWrapper({
                    'currentCatalogId' : drag.id,
                    'dropParentCatalogId' : Gikam.isNotEmpty(parent) ? parent.id : "",
                    'dropPreviousCatalogId' : Gikam.isNotEmpty(previous) ? previous.id : ""
                }, [ null, null ]));
            },
            onNodeSelected : function(nodeId) {
                _this.onTreeSelected(nodeId);
                _this.activeTreeId = nodeId;
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.selectNodeById(data[0].id);
                }
            }
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getDocumentGrid() ]
        }
    },

    getDocumentGrid : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.ITEM.HELP_DOCUMENTS.TAB.DOCUMENT',
            items : [ {
                type : 'grid',
                id : 'core-help-document-search-list-grid',
                service : 'coreHelpDocumentServiceImpl',
                dbTable : 'T_CORE_HELP_DOCUMENT',
                readonly : true,
                deleteFormatter : function(row) {
                    return row.ext$.name;
                },
                activateFormatter : function(row) {
                    return row.ext$.name;
                },
                columns : coreHelpDocument.getDocumentListColumns()
            } ]
        };
    },


    onTreeSelected : function(nodeId) {
        var _this = this;
        if (Gikam.isNotEmpty(nodeId)) {
            Gikam.getComp('core-help-document-search-list-grid') && Gikam.getComp('core-help-document-search-list-grid').cleanData();
            Gikam.getAsyncComp('core-help-document-search-list-grid').done(function(grid) {
                grid.refresh({
                    url : Gikam.printf(coreHelpDocument.baseUrl + '/{id}/documents/queries', {
                        id : nodeId
                    }),
                    requestData : {
                        activatedFlag_SEQ: '1',
                    }
                });
            });
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-help-document-catalog-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
