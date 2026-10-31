emcCorrection.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-correction-search-list-grid',
            url : emcCorrection.baseUrl + '/queries/searchable',
            columns : emcCorrection.getSearchListColumns(),
            onLoadSuccess : function(data) {
                var grid = this;
                if (!Gikam.isEmpty(data)) {
                    grid.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-correction-search-list-form');
                    if(form) {
                        form.cleanData();
                    }
                    var uploader = Gikam.getComp('emc-correction-search-list-uploader');
                    if(uploader) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-correction-search-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcCorrection.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }, true);
                });
                Gikam.getAsyncComp('emc-correction-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
            }
        }
    },

    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-correction-search-list-form',
            fields : emcCorrection.getDetailFormColumns('search'),
            columns : 2,
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(_this.getGridData())) {
                    this.cleanData();
                }
            }
        };
    },
    
    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-correction-search-list-uploader',
            dbTable : 'T_EMC_CORRECTION',
            readonly : true
        }
    },

    getGridActiveRow : function() {
        var grid = Gikam.getComp('emc-correction-search-list-grid');
        return grid && grid.getActivedRow();
    },
    
    getGridData : function() {
        var grid = Gikam.getComp('emc-correction-search-list-grid');
        return grid && grid.getData();
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-correction-search-list-layout',
            renderTo : workspace.window.$dom,
            
            west : {
                width : 250,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.CORRECTIONS.TAB.DETAIL',
                        items : [ _this.getFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.CORRECTIONS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
