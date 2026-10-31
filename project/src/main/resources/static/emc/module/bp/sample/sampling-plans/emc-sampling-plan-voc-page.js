emcSamplingPlan.vocPage = {
    getVocPageGridParam : function() {
       var _this = this;
       var columns = Gikam.isEmpty(_this.param?.orderWeight) ?
           emcSamplingPlan.getVocPageColumns() : emcSamplingPlan.getVocPageColumns1();

       return {
            type : 'grid',
            id : 'emc-sampling-plan-voc-page-grid',
            service : 'PfVocWeightingServiceImpl',
            columns : columns,
            filterOpen : true,
            page : true,
	        requestData : {
	            orgId_SEQ : workspace.user.ext$.parentorgid,
	        },
            order: {
            	vocNumber: 'asc'
            },
            toolbar:[ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                	_this.addVoc();
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-sampling-plan-voc-page-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/voc')
                },

            },{
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.BATCH_UPDATE_VOC',
                icon : 'edit',
                onClick : function() {
                    _this.batchUpdateVoc();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('voc', Gikam.getComp('emc-sampling-plan-voc-page-grid').getSelections());
                }
            } 	
            ],
                        
            url : '/secure/emc/module/bp/voc/queries',
        }
    },
    
    addVoc : function() {
	        Gikam.create('modal', {
	            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.ADD_VOC',
	            width : 400,
	            height : 200,
	            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/sampling-plans/page/add-voc',
	            onAfterClose : function(data) {
	                var addnum = data.addnum;
	                if(addnum == null || addnum == ''){
	                	return;
	                }
	                workspace.window.showMask(true);
	                var url = IFM_CONTEXT + '/secure/emc/module/bp/voc/add-voc';
	                Gikam.post(url, Gikam.getJsonWrapper({
	                    f : {
	                    	addnum: addnum
	                    }
	                }, [])).always(function()
	                {                    
	                	workspace.window.closeMask(true);
	                	Gikam.getComp('emc-sampling-plan-voc-page-grid').refresh();
	                });
	                }
	            
	        });
	    
    },
 
	batchUpdateVoc : function() {
        var grid = Gikam.getComp('emc-sampling-plan-voc-page-grid');
        var rows = grid.getSelections();
        var row = rows[0];
        if (!Gikam.isEmpty(rows)) {
            Gikam.create('modal', {
                width : '60%',
                height : 313,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.BUTTON.BATCH_UPDATE_VOC',
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/sampling-plans/page/batch-update-voc',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        rows.map(function(item) {
                        	item.weighingUserName = data.weighingUserName;
                        	item.emptyBottleWeight = data.emptyBottleWeight;
                        	item.weightTime = data.weightTime;
                        	item.projNo = data.projNo;
                        });
                        Gikam.put(emcSamplingPlan.baseUrl + '/' + row.id, Gikam.getJsonWrapper({}, [ 'PfVocWeightingServiceImpl', rows ])).done(function() {
                            rows.map(function(item) {
                                grid.refreshRowById(item.id);
                            });
                            //grid.refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
        }
    },
           
    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getVocPageGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
