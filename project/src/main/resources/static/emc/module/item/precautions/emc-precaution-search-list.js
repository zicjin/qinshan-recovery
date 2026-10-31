emcPrecaution.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-precaution-search-list-grid',
            url : emcPrecaution.baseUrl + '/queries/searchable',
            columns : emcPrecaution.getSearchListColumns(),
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            onLoadSuccess : function(data) {
                var grid = this;
                if (!Gikam.isEmpty(data)) {
                    grid.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-precaution-search-list-form');
                    if(form) {
                        form.cleanData();
                    }
                    var uploader = Gikam.getComp('emc-precaution-search-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-precaution-search-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcPrecaution.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }, true);
                });
                Gikam.getAsyncComp('emc-precaution-search-list-uploader').done(function(uploader) {
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
            id : 'emc-precaution-search-list-form',
            fields : emcPrecaution.getDetailFormColumns('search'),
            columns : 2,
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(_this.getGridData())) {
                    this.cleanData();
                }
            },
        };
    },

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-precaution-search-list-uploader',
            dbTable : 'T_EMC_PRECAUTION',
            readonly : true
        }
    },

    getGridActiveRow : function() {
        var grid = Gikam.getComp('emc-precaution-search-list-grid');
        return grid && grid.getActivedRow();
    },

    getGridData : function() {
        var grid = Gikam.getComp('emc-precaution-search-list-grid');
        return grid && grid.getData();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-precaution-search-list-layout',
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
                        title : 'EMC.MODULE.ITEM.PRECAUTIONS.TAB.DETAIL',
                        items : [ _this.getFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.PRECAUTIONS.TAB.ATTACHMENT',
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
