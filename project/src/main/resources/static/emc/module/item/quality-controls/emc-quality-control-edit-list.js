emcQualityControl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-control-edit-list-grid',
            url : emcQualityControl.baseUrl + '/queries/raw',
            service : 'emcQualityControlServiceImpl',
            columns : emcQualityControl.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.qualityControlCode;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    // Gikam.cleanCompData([ 'emc-quality-control-edit-list-quality-control-form', 'emc-quality-control-edit-list-implementation-form', 'emc-quality-control-edit-list-result-form' ]);
                    var uploader = Gikam.getComp('emc-quality-control-edit-list-uploader');
                    uploader.setOptions({
                        bizId : ''
                    });
                    uploader.grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onGridActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        fields : emcQualityControl.getEditListColumns(),
                        url : emcQualityControl.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-quality-control-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quality-control-edit-list-grid').deleteRows(emcQualityControl.baseUrl);
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
                    Gikam.viewReport('quailtiyControlPlan', Gikam.getComp('emc-quality-control-edit-list-grid').getSelections());
                }
       	 }  ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-quality-control-edit-list-grid');
        var selections = grid.getSelections();
        if (!grid.validate(selections)) {
            return;
        }
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcQualityControl
        }).done(function() {
            grid.refresh();
        });
    },
    
    onGridActive : function(row) {
        /* var url = Gikam.printf(emcQualityControl.baseUrl + '/{id}', {
            id : row.id
        });
        Gikam.getAsyncComp('emc-quality-control-edit-list-quality-control-form').done(function(form) {
            form.refresh({
                url : url,
            }, true);
        });
        Gikam.getAsyncComp('emc-quality-control-edit-list-implementation-form').done(function(form) {
            form.refresh({
                url : url,
            });
        });
        Gikam.getAsyncComp('emc-quality-control-edit-list-result-form').done(function(form) {
            form.refresh({
                url : url,
            });
        });*/
        var uploader = Gikam.getComp('emc-quality-control-edit-list-uploader');
        uploader.setOptions({
            bizId : row.id
        })
        uploader.grid.refresh();
    },
    
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-quality-control-edit-list-base-info-tab',
            fill : true,
            panels : [ /*{
                title : 'EMC.MODULE.ITEM.QUALITYCONTROL.TAB.QC',
                items : [ {
                    type : 'form',
                    id : 'emc-quality-control-edit-list-quality-control-form',
                    service : 'emcQualityControlServiceImpl',
                    fields : emcQualityControl.getQualityControlFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.QUALITYCONTROL.TAB.IMPLEMENTATION',
                items : [ {
                    type : 'form',
                    id : 'emc-quality-control-edit-list-implementation-form',
                    titleWidth : 160,
                    service : 'emcQualityControlServiceImpl',
                    columns : 1,
                    fields : emcQualityControl.getImplementationFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.QUALITYCONTROL.TAB.RESULT',
                items : [ {
                    type : 'form',
                    id : 'emc-quality-control-edit-list-result-form',
                    titleWidth : 160,
                    service : 'emcQualityControlServiceImpl',
                    columns : 1,
                    fields : emcQualityControl.getResultFormFields()
                } ]
            },*/ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-quality-control-edit-list-uploader',
                    dbTable : 'T_EMC_QUALITY_CONTROL',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-quality-control-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '25%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
