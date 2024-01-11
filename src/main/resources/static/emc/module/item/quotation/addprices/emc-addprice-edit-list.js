emcAddprice.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-addprice-edit-list-grid',
            url : emcAddprice.baseUrl + '/queries/raw',
            columns : emcAddprice.getEditListColumns(),
            service : 'emcAddpriceServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-addprice-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcAddprice.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.ADDPRICES.MODAL.ADD',
                        url : emcAddprice.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-addprice-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-addprice-edit-list-grid').deleteRows(emcAddprice.baseUrl);
                }
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-addprice-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                width : 300,
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
