coreFileType.searchPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'core-file-type-search-list-grid',
            url : coreFileType.baseUrl + '/queries/raw',
            service : 'CoreFileTypeServiceImpl',
            autoSave : false,
            columns : coreFileType.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                    var grid = Gikam.getComp('core-file-type-search-list-grid');
                } else {
                    Gikam.cleanCompData([ 'core-file-type-search-list-category-grid', 'core-file-type-search-list-category-role-auth-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-type-search-list-category-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileType.baseUrl + '/{id}/categorys/queries', {
                            id : row.id
                        })
                    });
                });
            }
        }
    },
    
    getCategoryGrid : function() {
        return {
            type : 'grid',
            id : 'core-file-type-search-list-category-grid',
            service : 'coreFileCategoryServiceImpl',
            autoSave : false,
            columns : coreFileType.getCategorySearchListColumns(),
            height : 300,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                    var grid = Gikam.getComp('core-file-type-search-list-category-grid');
                    Gikam.each(grid.getData(), function(index, row) {
                        grid.toFieldsReadonly(index, ['sortor', 'activatedFlag']);
                    });
                } else {
                    Gikam.cleanCompData([ 'core-file-type-search-list-category-role-auth-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-file-type-search-list-category-role-auth-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreFileType.baseUrl + '/{id}/category-role-auths/queries', {
                            id : row.id
                        })
                    });
                });
            }
        };
    },
    
    getCategoryRoleAuthGrid : function() {
        return {
            type : 'grid',
            id : 'core-file-type-search-list-category-role-auth-grid',
            service : 'coreFileCategoryRoleAuthServiceImpl',
            autoSave : false,
            columns : coreFileType.getCategoryRoleAuthSearchListColumns(),
            height : 300,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                    var grid = Gikam.getComp('core-file-type-search-list-category-role-auth-grid');
                    Gikam.each(grid.getData(), function(index, row) {
                        grid.toFieldsReadonly(index, ['sortor', 'printFlag', 'readFlag']);
                    });
                }
            },
        };
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-file-type-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '45%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getCategoryGrid(), this.getCategoryRoleAuthGrid() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
