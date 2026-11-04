coreLog.searchPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-log-search-list-grid',
            url : coreLog.baseUrl + '/queries/searchable',
            generalButtonGroup : ['refresh','config'],
            genericQueryFields : [ {
                field : 'name'
            }, {
                field : 'module'
            } ],
            columns : coreLog.getSearchListColumns(),
            onCellClick : function(field, row) {
                if(field === 'content'){
                    Gikam.getText(Gikam.printf(coreLog.baseUrl + "/{id}/columns/{column}", {
                        id : row.id,
                        column : 'content'
                    })).done(function(content) {
                        var modal = Gikam.create('modal', {
                            id : 'core-log-content-modal',
                            title : 'T_CORE_LOG.CONTENT',
                            showCloseBtn : true
                        });

                        Gikam.create('layout', {
                            renderTo : modal.window.$dom,
                            center : {
                                items : [ {
                                    type : 'codeEditor',
                                    id : 'core-log-content-layout',
                                    language : 'json',
                                    height : '100%',
                                    value : content
                                } ]
                            }
                        });
                    })

                }

            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};