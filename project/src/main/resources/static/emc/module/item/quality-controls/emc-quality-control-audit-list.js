emcQualityControl.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-control-audit-list-grid',
            url : emcQualityControl.baseUrl + '/queries/tasks',
            columns : emcQualityControl.getEditListColumns(),
            requestData : _this.param,
            readonly: true,
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
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('quailtiyControlPlan', Gikam.getComp('emc-quality-control-audit-list-grid').getSelections());
                }
       	 } ]
        }
    },
    
    onGridActive : function(row) {
        /*var qcForm = Gikam.getComp('emc-quality-control-edit-list-quality-control-form');
        var implementationForm = Gikam.getComp('emc-quality-control-edit-list-implementation-form');
        var resultForm = Gikam.getComp('emc-quality-control-edit-list-result-form');
        var url = Gikam.printf(emcQualityControl.baseUrl + '/{id}', {
            id : row.id
        });
        qcForm.refresh({url : url}, true);
        implementationForm.refresh({url : url}, true);
        resultForm.refresh({url : url}, true);*/
        var uploader = Gikam.getComp('emc-quality-control-edit-list-uploader');
        uploader.setOptions({bizId : row.id})
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
                    readonly : true,
                    id : 'emc-quality-control-edit-list-quality-control-form',
                    service : 'emcQualityControlServiceImpl',
                    fields : emcQualityControl.getQualityControlFormFields()
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.QUALITYCONTROL.TAB.IMPLEMENTATION',
                items : [ {
                    type : 'form',
                    readonly : true,
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
                    readonly : true,
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
    
    pass : function() {
        var grid = Gikam.getComp('emc-quality-control-audit-list-grid');
        var selections = grid.getSelections();
        if (!grid.validate(selections)) {
            return;
        }
        Gikam.create('workflow').pass({
            data : selections,
            pageObject : emcQualityControl
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-quality-control-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcQualityControl
        }).done(function() {
            grid.refresh();
        });
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
