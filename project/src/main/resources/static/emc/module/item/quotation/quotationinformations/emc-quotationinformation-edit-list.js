emcQuotationinformation.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quotationinformation-edit-list-grid',
            url : emcQuotationinformation.baseUrl + '/queries/raw',
            columns : emcQuotationinformation.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-quotationinformation-edit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-quotationinformation-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcQuotationinformation.baseUrl + '/{id}', {
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
                        modalTitle : 'EMC.MODULE.ITEM.QUOTATION.QUOTATIONINFORMATIONS.MODAL.ADD',
                        fields : emcQuotationinformation.getBaseInfoFormFields(),
                        url : emcQuotationinformation.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-quotationinformation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quotationinformation-edit-list-grid').deleteRows(emcQuotationinformation.baseUrl);
                }
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-quotationinformation-edit-list-layout',
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
