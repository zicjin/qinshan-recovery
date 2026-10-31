emcInconformity.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-inconformity-search-list-grid',
            url : emcInconformity.baseUrl + '/queries/searchable',
            columns : emcInconformity.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-inconformity-search-list-form');
                    if(form) {
                        form.cleanData();
                    }
                    var uploader = Gikam.getComp('emc-inconformity-search-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-inconformity-search-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcInconformity.baseUrl + "/{id}", {
                            id : row.id
                        }),
                    });
                });
                Gikam.getAsyncComp('emc-inconformity-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
            },
            
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('inConformity', Gikam.getComp('emc-inconformity-edit-list-grid').getSelections());
                }
            } ]

            
        }
    },

    getTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.ITEM.INCONFORMITY.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-inconformity-search-list-form',
                    columns : 3,
                    fields : emcInconformity.getInconformityFieldsNoEdit(),
                } ]
            },  {
                title : 'EMC.MODULE.ITEM.INCONFORMITY.TAB.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-inconformity-search-list-uploader',
                    dbTable : 'T_EMC_INCONFORMITY',
                    readonly : true
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-inconformity-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 250,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
