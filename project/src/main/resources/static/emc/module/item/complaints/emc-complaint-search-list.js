emcComplaint.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-complaint-search-list-grid',
            url : emcComplaint.baseUrl + '/queries/searchable',
            columns : emcComplaint.getSearchListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-complaint-search-list-grid').activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-complaint-search-list-form')) {
                        Gikam.getComp('emc-complaint-search-list-form').cleanData();
                    }

                    if (Gikam.getComp('emc-complaint-search-list-uploader')) {
                        Gikam.getComp('emc-complaint-search-list-uploader').grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-complaint-search-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcComplaint.baseUrl + '/{id}', {
                            id : row.id
                        })

                    });
                });
                Gikam.getComp('emc-complaint-search-list-uploader').setOptions({
                    bizId : row.id
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('complaint', Gikam.getComp('emc-complaint-search-list-grid').getSelections());
                }
            
            } ]
        }
    },

    getFormParam : function() {
        return {
            type : 'form',
            id : 'emc-complaint-search-list-form',
            fields : emcComplaint.getDetailFormFields(),
            columns : 3,
            readonly : true
        };
    },

    getAttachmentGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-complaint-search-list-uploader',
            dbTable : 'T_EMC_COMPLAINT',
            readonly : true
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-complaint-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 225,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.COMPLAINTS.TAB.DETAIL',
                        items : [ _this.getFormParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.COMPLAINTS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
