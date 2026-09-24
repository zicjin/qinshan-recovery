emcOrderPreparations.editPage = {
		getGridParam : function() {
	        var _this = this;
	        requestData = this.param;
	        return {
	            type : 'grid',
	            id : 'emc-order-preparations-edit-list-grid',
                url :IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries/choosable',
            	requestData:{
            		targetType : 'analysis',
                    org_filter : '1',
            	},
	            columns : emcOrderPreparations.getOrderPreparationsEditListColumns(),
	            service : 'emcRunServiceImpl',
	            page : true,
	            deleteFormatter : function(row) {
	                return row.projName || row.id;
	            },
	            onLoadSuccess : function(data) {
	                if (!Gikam.isEmpty(data)) {
	                    this.activeRowByIndex(0);
	                } else {
	                    Gikam.cleanCompData(["emc-order-preparations-edit-list-order-grid"]);
	                    Gikam.getComp('emc-order-preparations-edit-list-base-info-tab');
	                }
	            },
	            onRowActive : function(index, row) {
	                _this.onProjectActive(row);
	            },

	        }
	    },

	    getBaseInfoTab : function() {
	        var _this = this;
	        return {
	            type : 'tab',
	            fill : true,
	            id : 'emc-order-preparations-edit-list-base-info-tab',
	            panels : [   {
	                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
	                id : 'emc-order-preparations-edit-list-order-panel',
	                items : [ {
	                    type : 'layout',
	                    center : {
	                        items : [ {
	                            type : 'grid',
	                            id : 'emc-order-preparations-edit-list-order-grid',
	                            columns : emcOrderPreparations.getOrderPreparationsColumns(),
	                            onCellClick : function(field, row) {
	                                field === 'id' && _this.loadRunDetail(row.id);
	                            },
	                            service : 'emcOrderServiceImpl',
	                            deleteFormatter : function(row) {
	                                return row.orderNo || row.id;
	                            },

	                            onLoadSuccess : function(data) {
	                                if (!Gikam.isEmpty(data)) {
	                                    this.activeRowByIndex(0);
	                                }
	                            },
	                            toolbar :[
	                            {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.PREVIEW',
                                    icon : 'print',
                                    onClick : function() {
                                        Gikam.viewReport('sampleKeep', Gikam.getComp('emc-order-preparations-edit-list-order-grid').getSelections());
                                    }
	                            },{
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.BATCH_INPUT',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.batchUpdateOrder();
	                                        }
	                                    }            	
	                                ]
	                        } ]
	                    },
	                } ]
	            }]
	        }
	    },
        
	    onProjectActive : function(row) {
	        var _this = this;
	        var tab = Gikam.getComp('emc-order-preparations-edit-list-base-info-tab');
	        tab.show();
	        if (row) {
	            if(_this.param == undefined) {
	                _this.param = {};
	            }
	            console.log(this.param.projId);
	            console.log(row.id);
	            _this.param.projId = row.id;
	            tab.revertPanel('emc-order-preparations-edit-list-order-panel');
	                Gikam.getComp('emc-order-preparations-edit-list-order-grid').refresh({
	                    url : Gikam.printf('/secure/emc/module/bp/project/projects/{id}/orders/queries', {
	                        id : row.id
	                    })
	                });
	        }
	    },

	    batchUpdateOrder : function() {
	        var selections = Gikam.getComp('emc-order-preparations-edit-list-order-grid').getSelections();
	        if (selections.length > 0) {
	            var activedRow = Gikam.getComp('emc-order-preparations-edit-list-grid').getActivedRow();
	            Gikam.create('modal', {
	                width : 600,
	                height : 400,
	                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
	             url : emcOrderPreparations.baseUrl + '/page/batch-update-order',

	                onAfterClose : function(data) {
	                    if (!Gikam.isEmpty(data)) {
	                        workspace.window.showMask(true);
	                        selections.map(function(item) {
	                            item.ext$.airdrytype = data.ext$.airdrytype;
	                            item.ext$.dryingtemp = data.ext$.dryingtemp;
	                            item.ext$.grindmode = data.ext$.grindmode;
	                            item.ext$.samplecontainer = data.ext$.samplecontainer;
	                            item.ext$.wetweight = data.ext$.wetweight;
	                            item.ext$.dryweight = data.ext$.dryweight;
	                            item.ext$.impurityweight = data.ext$.impurityweight;
	                            item.ext$.granularity02 = data.ext$.granularity02;
	                            item.ext$.granularity025 = data.ext$.granularity025;
	                            item.ext$.granularity015 = data.ext$.granularity015;
	                            item.ext$.discardweight = data.ext$.discardweight;
	                        });
	                        Gikam.put(emcOrderPreparations.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
	                            Gikam.getComp('emc-order-preparations-edit-list-order-grid').refresh();
	                        }).always(function() {
	                            workspace.window.closeMask();
	                        });
	                    }
	                }
	            });
	        } else {
	            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
	        }
	    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-order-preparations-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '23%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },
    init : function(param) {
        this.param = param;
        this.create();
    }
};
