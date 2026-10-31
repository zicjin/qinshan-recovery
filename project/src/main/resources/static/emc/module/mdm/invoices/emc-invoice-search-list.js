emcInvoice.searchPage = {

    getGridParam : function() {
        var _this = this;
        var requestData = {};
        if (_this.param.personal == 1) {
            requestData = {
                createdById_SEQ : workspace.user.id
            }
        }
        return {
            type : 'grid',
            id : 'emc-invoice-search-list-grid',
            url : emcInvoice.baseUrl + '/queries',
            requestData : requestData,
            page : true,
            columns : emcInvoice.getSearchListColumns(_this.param.personal),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-invoice-returned-search-list-grid']);
                    var uploader = Gikam.getComp('emc-invoice-returned-file-search-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-invoice-returned-search-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcInvoice.baseUrl + '/{id}/returns/queries', {
                            id : row.id
                        })
                    });
                });

                Gikam.getComp('emc-invoice-returned-file-search-list-uploader').setOptions({
                    bizId : row.id
                });

            },
        }
    },

    getReturnedTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.INVOICES.TAB.RECORD',
                items : [{
                    type: 'grid',
                    id: 'emc-invoice-returned-search-list-grid',
                    service: 'emcReturnedServiceImpl',
                    full: true,
                    page: true,
                    deleteFormatter: function (row) {
                        return row.id;
                    },
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            var id = data[0].id;
                            var index = 0;
                            for (var i = 0; i < data.length; i++) {
                                if (data[i].id > id) {
                                    id = data[i].id;
                                    index = data[i].gridIndex
                                }
                            }
                            for (var i = 0; i < data.length; i++) {
                                if (data[i].id != id) {
                                    Gikam.getComp('emc-invoice-returned-search-list-grid').toFieldsReadonly(data[i].gridIndex - 1, ['returnedAmount'])
                                }
                            }
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var activedId = Gikam.getComp('emc-invoice-search-list-grid').getActivedRow().id
                            Gikam.preInsert({
                                modalTitle : 'T_EMC_RETURNED.SERVICE_NAME',
                                fields : emcInvoice.getListPreInsertColumns(),
                                url : Gikam.printf(emcInvoice.baseUrl + '/{id}/returns', {
                                    id : activedId
                                })
                            }).done(function(id) {
                                Gikam.getComp('emc-invoice-returned-search-list-grid').refresh();
                                Gikam.getComp('emc-invoice-search-list-grid').refreshRowById(activedId);
                            });
                        }
                    }],
                    /*, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('emc-invoice-returned-search-list-grid').deleteRows(emcInvoice.baseUrl + '/returns');
                        }
                    } */
                    columns : emcInvoice.getListColumns()
                } ]
            },{
                title : 'EMC.MODULE.MDM.INVOICES.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-invoice-returned-file-search-list-uploader',
                    dbTable : 'T_EMC_INVOICE',
                    readOnly : false
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-invoice-search-list-layout',
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
