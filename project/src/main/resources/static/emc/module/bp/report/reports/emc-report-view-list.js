emcReport.reportViewPage = {
		getGridParam : function() {
	        var _this = this;
	        return {
	            type : 'grid',
	            id : 'emc-report-view-list',
	            url :IFM_CONTEXT + '/secure/core/module/mdm/orgs/queries/raw',
	            columns : emcReport.getOrgEditListColumns(),
	            requestData : {
	                id_SEQ: workspace.user.ext$.parentorgid
	            },
                onLoadSuccess : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    } else {
                        Gikam.getComp('emc-report-view-list').cleanData();
                    }
                },
	            toolbar : [ {
                    type : 'button',
                    text : '查看',
                    icon : 'print',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-report-view-list').getSelections();
                    	if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        Gikam.reportData = selections;
                        Gikam.create('modal', {
                            title : 'EMC.MODULE.SYS.REPORTS.MODAL.REPORT',
                            url : IFM_CONTEXT + '/secure/core/module/sys/report-configs/page/preview' + Gikam.param({
                                reportConfigId : 'reportViewSearch',
                                
                            })
                        });
                        
                    }
                }]


	        }
	    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : '/emc-report-view-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [  this.getGridParam() ]
            }

        });
    },
    init : function(param) {
        this.param = param;
        this.create();
    }
};
