emcInvoice.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-invoice-edit-list-grid',
            url : emcInvoice.baseUrl + '/queries/raw',
            requestData : {
               createdById_SEQ : workspace.user.id
            },
            columns : emcInvoice.getEditListColumns(),
            service : 'emcInvoiceServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                   /* for (var i = 0; i < data.length; i++) {
                        if (data[i].ext$.copyflag != '1') {
                            Gikam.getComp('emc-invoice-edit-list-grid').toFieldsReadonly(data[i].gridIndex - 1 ,['invoiceAmount'])
                        }
                    }*/
                } else {
                    Gikam.cleanCompData([ 'emc-invoice-returned-edit-list-grid' ]);
                    var uploader = Gikam.getComp('emc-invoice-returned-file-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onUpdated : function(data, fields) {
                if (data.contractId != undefined) {
                    Gikam.getComp('emc-invoice-edit-list-grid').refreshRowById(data.id);
                }
                if (data.invoiceAmount != '' && data.invoiceAmount != undefined) {
                    var activedRow = Gikam.getComp('emc-invoice-edit-list-grid').getActivedRow();
                    var invoiceAmount = data.invoiceAmount - 0 ;
                    var uninvoicedAmount = activedRow.ext$.uninvoicedamount - 0;
                    if (uninvoicedAmount == '' || uninvoicedAmount == undefined || invoiceAmount > uninvoicedAmount) {
                        Gikam.alert('开票金额不能大于未剩余开票金额');
                        Gikam.getComp('emc-invoice-edit-list-grid').setData({
                            id : data.id,
                            invoiceAmount : ''
                        });
                    } else {
                        Gikam.confirm('EMC.MODULE.MDM.INVOICES.TIP.IS_SAVE','EMC.MODULE.MDM.INVOICES.TIP.SURE_TO_SAVE',
                            function (){
                                Gikam.getComp('emc-invoice-edit-list-grid').setData({
                                    id : data.id,
                                    ext$ : {
                                        copyflag : '0'
                                    }
                                })
                                Gikam.getComp('emc-invoice-edit-list-grid').toFieldsReadonly(activedRow.index ,['invoiceAmount'])
                            },
                            function (){
                                Gikam.getComp('emc-invoice-edit-list-grid').setData({
                                    id : data.id,
                                    invoiceAmount : ''
                                })
                            }
                        )
                    }
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-invoice-returned-edit-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcInvoice.baseUrl + '/{id}/returns/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.getComp('emc-invoice-returned-file-list-uploader').setOptions({
                    bizId : row.id
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.INVOICES.MODAL.ADD',
                        fields : emcInvoice.getEditListColumns(),
                        url : emcInvoice.baseUrl,
                        height : 250
                    }).done(function (){
                        Gikam.getComp('emc-invoice-edit-list-grid').refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var selections = Gikam.getComp('emc-invoice-edit-list-grid').getSelections();
                    if (selections.length != 1) {
                        Gikam.alert('EMC.MODULE.MDM.INVOICES.TIP.SELECT_ONE');
                        return ;
                    }
                    Gikam.getComp('emc-invoice-edit-list-grid').insert(emcInvoice.baseUrl,[selections[0]]);
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-invoice-edit-list-grid').deleteRows(emcInvoice.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            } ]
        }
    },

    getReturnedTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.INVOICES.TAB.RECORD',
                items : [ {
                    type : 'grid',
                    id : 'emc-invoice-returned-edit-list-grid',
                    service : 'emcReturnedServiceImpl',
                    full : true,
                    deleteFormatter : function(row) {
                        return row.id;
                    },
                    onLoadSuccess : function(data){
                        if (!Gikam.isEmpty(data)) {
                            var id = data[0].id;
                            var index = 0;
                            for (var i = 0; i < data.length; i++) {
                                if ( data[i].id > id ) {
                                    id = data[i].id;
                                    index = data[i].gridIndex
                                }
                            }
                          for (var i = 0; i < data.length; i++) {
                                if (data[i].id != id) {
                                    Gikam.getComp('emc-invoice-returned-edit-list-grid').toFieldsReadonly(data[i].gridIndex - 1 ,['returnedAmount'])
                                }
                            }
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var activedId = Gikam.getComp('emc-invoice-edit-list-grid').getActivedRow().id
                            Gikam.preInsert({
                                modalTitle : 'T_EMC_RETURNED.SERVICE_NAME',
                                fields : emcInvoice.getListPreInsertColumns(),
                                url : Gikam.printf(emcInvoice.baseUrl + '/{id}/returns', {
                                    id : activedId
                                })
                            }).done(function(id) {
                                Gikam.getComp('emc-invoice-returned-edit-list-grid').refresh();
                                Gikam.getComp('emc-invoice-edit-list-grid').refreshRowById(activedId);
                            });
                        }
                    }],
                    columns : emcInvoice.getListColumns()
                } ]
            },{
                title : 'EMC.MODULE.MDM.INVOICES.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-invoice-returned-file-list-uploader',
                    dbTable : 'T_EMC_INVOICE'
                } ]
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-invoice-edit-list-grid');
        var selections = grid.getSelections();
        if (!grid.validate(selections)) {
            return;
        }
        Gikam.create('workflow').submit({
            data : selections,
            pageObject : emcInvoice
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-invoice-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getReturnedTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
