emcEquiptPerCheck.prinAuditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-per-check-prin-audit-list-grid',
            url : emcEquiptPerCheck.baseUrl + '/queries/tasks',
            columns : emcEquiptPerCheck.getPrinAuditListColumns(),
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var uploader = Gikam.getComp('emc-equipt-per-check-prin-audit-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                    Gikam.cleanCompData([ 'emc-equipt-per-check-prin-audit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-per-check-prin-audit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptPerCheck.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('emc-equipt-per-check-prin-audit-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
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
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('DINGAN_EQPER_CHECK', Gikam.getComp('emc-equipt-per-check-prin-audit-list-grid').getSelections());
                }
            } ]
        }
    },
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTPERCHECKS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptPerCheckServiceImpl',
                    id : 'emc-equipt-per-check-prin-audit-list-base-info-form',
                    fields : emcEquiptPerCheck.getBaseInfoFormPrinFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-per-check-prin-audit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            },{
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.ANNEX',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-per-check-prin-audit-uploader',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK',
                } ]
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-equipt-per-check-prin-audit-list-grid');
        var gridData =  Gikam.getComp('emc-equipt-per-check-prin-audit-list-grid').getSelections();
        var url = emcEquiptPerCheck.baseUrl + '/per-check-equipt-plan';
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcEquiptPerCheck
        }).done(function() {
            Gikam.post(url,Gikam.getJsonWrapper({}, [ '', gridData ]))
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-equipt-per-check-prin-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcEquiptPerCheck
        }).done(function() {
            grid.refresh();
        });
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-per-check-prin-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        param.principal_SEQ=workspace.user.id;
        this.param = param;
        this.create();
    }
};
