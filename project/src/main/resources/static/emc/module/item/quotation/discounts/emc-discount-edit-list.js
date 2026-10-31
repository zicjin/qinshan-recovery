emcDiscount.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-discount-edit-list-grid',
            url : emcDiscount.baseUrl + '/queries/raw',
            columns : emcDiscount.getEditListColumns(),
            service : 'emcDiscountServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-discount-edit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-discount-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcDiscount.baseUrl + '/{id}', {
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
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.DISCOUNTS.MODAL.ADD',
                        url : emcDiscount.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-discount-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-discount-edit-list-grid').deleteRows(emcDiscount.baseUrl);
                }
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-discount-edit-list-layout',
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
