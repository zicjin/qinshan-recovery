emcComplaint.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-complaint-edit-list-grid',
            url : emcComplaint.baseUrl + '/queries/raw',
            columns : emcComplaint.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-complaint-edit-list-grid').activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-complaint-edit-list-form')) {
                        Gikam.getComp('emc-complaint-edit-list-form').cleanData();
                        Gikam.getComp('emc-complaint-edit-list-form').setOptions({
                            url : ''
                        });
                    }
                    if (Gikam.getComp('emc-complaint-edit-list-uploader')) {
                        Gikam.getComp('emc-complaint-edit-list-uploader').grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-complaint-edit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcComplaint.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

                Gikam.getComp('emc-complaint-edit-list-uploader').setOptions({
                    bizId : row.id
                });

            },
            deleteFormatter : function(row) {
                return row.qualityControlNo;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-complaint-edit-list-grid').insert(emcComplaint.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-complaint-edit-list-grid').deleteRows(emcComplaint.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('complaint', Gikam.getComp('emc-complaint-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    getFormParam : function() {
        return {
            type : 'form',
            id : 'emc-complaint-edit-list-form',
            service : 'emcComplaintServiceImpl',
            fields : emcComplaint.getDetailFormFields(),
            columns : 3
        };
    },

    getAttachmentGridParam : function() {
        return {
            type : 'uploader',
            id : 'emc-complaint-edit-list-uploader',
            dbTable : 'T_EMC_COMPLAINT'
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-complaint-edit-list-grid');
        var selections = grid.getSelections();
        Gikam.create('workflow').submit({
            data : selections,
            pageObject : emcComplaint
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-complaint-edit-list-layout',
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
