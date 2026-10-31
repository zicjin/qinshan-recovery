emcEquiptPerCheck.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-per-check-edit-list-grid',
            url : emcEquiptPerCheck.baseUrl + '/queries/raw',
            columns : emcEquiptPerCheck.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var uploader = Gikam.getComp('emc-equipt-per-check-edit-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                    Gikam.cleanCompData([ 'emc-equipt-per-check-edit-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-per-check-edit-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptPerCheck.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });

                Gikam.getAsyncComp('emc-equipt-per-check-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.addCheck();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-per-check-edit-list-grid').deleteRows(emcEquiptPerCheck.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('DINGAN_EQPER_CHECK', Gikam.getComp('emc-equipt-per-check-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-equipt-per-check-edit-list-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (!grid.validate(selections)) {
            return;
        }
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcEquiptPerCheck
        }).done(function() {
            grid.refresh();
        });
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
                    id : 'emc-equipt-per-check-edit-list-base-info-form',
                    fields : emcEquiptPerCheck.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-per-check-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            },{
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.ANNEX',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-per-check-edit-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK',
                } ]
            } ]
        }
    },

    addCheck : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.EQUIPT',
            url : Gikam.choose.getConfig('equiptCali').url + Gikam.param({
                //single : true,
                pageType : 'duringCheck'
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(row) {
                    return {
                        equiptId : row.id,
                        equiptNo : row.equiptId,
                        equiptName : row.equiptName,
                        specification : row.specification,
                    };
                });
                Gikam.getComp('emc-equipt-per-check-edit-list-grid').insert(emcEquiptPerCheck.baseUrl, chooseData);
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-per-check-edit-list-layout',
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

    init : function() {
        this.create();
    }
};
