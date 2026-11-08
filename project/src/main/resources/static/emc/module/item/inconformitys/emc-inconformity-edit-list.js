emcInconformity.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            toolbarWrap : true,
            id : 'emc-inconformity-edit-list-grid',
            url : emcInconformity.baseUrl + '/queries/raw',
            service : 'emcInconformityServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcInconformity.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var form = Gikam.getComp('emc-inconformity-edit-list-form');
                    if (form) {
                        form.cleanData();
                        form.setOptions({
                            url : ''
                        });
                    }
                    var uploader = Gikam.getComp('emc-inconformity-edit-list-uploader');
                    if (uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : ''
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-inconformity-edit-list-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcInconformity.baseUrl + "/{id}", {
                            id : row.id
                        }),
                    });
                });
                Gikam.getAsyncComp('emc-inconformity-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
            },
            deleteFormatter : function(row) {
                return row.qualityControlNo;
            }
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-inconformity-edit-list-grid').insert(emcInconformity.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-inconformity-edit-list-grid').deleteRows(emcInconformity.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('inConformity', Gikam.getComp('emc-inconformity-search-list-grid').getSelections());
                }
            } ]
        } ];
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
                    id : 'emc-inconformity-edit-list-form',
                    service : 'emcInconformityServiceImpl',
                    columns : 3,
                    fields : emcInconformity.getInconformityFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.INCONFORMITY.TAB.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-inconformity-edit-list-uploader',
                    dbTable : 'T_EMC_INCONFORMITY'
                } ]
            } ]
        }
    },

    submit : function() {
        var _this = this;
        var form = Gikam.getComp('emc-inconformity-edit-list-form');
        var data = form.getData();
        if (!form.validate(data))
            return;
        var grid = Gikam.getComp('emc-inconformity-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcInconformity
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-inconformity-edit-list-layout',
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
