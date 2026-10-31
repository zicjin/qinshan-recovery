emcCorrection.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-correction-edit-list-grid',
            url : emcCorrection.baseUrl + '/queries/raw',
            columns : emcCorrection.getEditListColumns(),
            service : 'emcCorrectionServiceImpl',
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            onLoadSuccess : function(data) {
                var grid = this;
                if (!Gikam.isEmpty(data)) {
                    grid.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-correction-edit-list-form');
                    if(form) {
                        form.cleanData();
                    }
                    var uploader = Gikam.getComp('emc-correction-edit-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-correction-edit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcCorrection.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }, true);
                });
                Gikam.getAsyncComp('emc-correction-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
            },
            deleteFormatter : function(row) {
                return row.correctionCode;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var url = Gikam.printf(emcCorrection.baseUrl);
                    Gikam.getComp('emc-correction-edit-list-grid').insert(url, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-correction-edit-list-grid').deleteRows(emcCorrection.baseUrl);
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

    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-correction-edit-list-form',
            service : 'emcCorrectionServiceImpl',
            fields : emcCorrection.getDetailFormColumns('edit'),
            columns : 2,
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(_this.getGridData())) {
                    this.cleanData();
                }
            },
            onUpdated : function() {
                var grid = Gikam.getComp('emc-correction-edit-list-grid');
                grid.refreshRowById(grid.getActivedRow().id);
            }
        };
    },

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-correction-edit-list-uploader',
            dbTable : 'T_EMC_CORRECTION',
        }
    },
    
    getGridActiveRow : function() {
        var grid = Gikam.getComp('emc-correction-edit-list-grid');
        return grid && grid.getActivedRow();
    },

    getGridData : function() {
        var grid = Gikam.getComp('emc-correction-edit-list-grid');
        return grid && grid.getData();
    },
    
    submit : function() {
        var form = Gikam.getComp('emc-correction-edit-list-form');
        var data = form.getData();
        if (!form.validate(data))
            return;
        var grid = Gikam.getComp('emc-correction-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcCorrection
        }).done(function() {
            grid.refresh();
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-correction-edit-list-layout',
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
