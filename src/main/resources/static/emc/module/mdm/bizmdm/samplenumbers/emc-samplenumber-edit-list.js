emcSamplenumber.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-samplenumber-edit-list-grid',
            service : 'emcSamplenumberServiceImpl',
            dbTable : 'T_EMC_SAMPLENUMBER',
            columns : emcSamplenumber.getEditListColumns(),
	        requestData : {
	            orgId_SB : workspace.user.ext$.parentorgid
	        },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-samplenumber-edit-list-base-info-form' ]);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                	var selections = Gikam.getComp('emc-report-config-detail-line-grid').getSelections();
                	if (selections.length === 1) {
                        Gikam.preInsert({
                            modalTitle : 'EMC.MODULE.MDM.BIZMDM.SAMPLENUMBERS.MODAL.ADD',
                            fields : emcSamplenumber.getBaseInfoFormFields(),
                            url : emcSamplenumber.baseUrl,
                            onFormRendered : function(form) {
                                setTimeout(() => {
                                    this.setData({reportconfiglineid : selections[0].id},false);
                                }, 10)
                            }
                        }).done(function(id) {
                            Gikam.getComp('emc-samplenumber-edit-list-grid').refresh();
                        });
                        Gikam.getComp('emc-project-edit-list-grid').insert(url, selections);
                    } else {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-samplenumber-edit-list-grid').deleteRows(emcSamplenumber.baseUrl);
                }
            } ]
        }
    },

    getReportGridParam : function() {
    	var _this = this;
        return {
        	type : 'grid',
            id : 'emc-report-config-detail-line-grid',
            url : IFM_CONTEXT + '/secure/core/module/sys/report-configs/lines/queries',
            columns : emcSamplenumber.getReportListColumns(),
            requestData : {
                REPORTCONFIGID_IN : 'samplingReport,samplingNumber,sampling,rasproject,deliveringLabel,xianchanglianxi,envRecord,stdSol'
            },
            service : 'coreReportConfigLineServiceImpl',
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                }
            },
            checkOnActive : true,
            columnsFill : true,
            onRowActive : function(index, row) {
            	Gikam.getAsyncComp('emc-samplenumber-edit-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                        	reportconfiglineid_SEQ : row.id
                        }
                    });
                    grid.refresh({
                        url : emcSamplenumber.baseUrl + '/queries/raw',
                    });
                });
            },
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.SAMPLENUMBERS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcSamplenumberServiceImpl',
                    dbTable : 'T_EMC_SAMPLENUMBER',
                    id : 'emc-samplenumber-edit-list-base-info-form',
                    fields : emcSamplenumber.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-samplenumber-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-samplenumber-edit-list-layout',
            renderTo : workspace.window.$dom,
            west :{
            	width : '36%',
            	items : [ this.getReportGridParam() ]
            },
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
