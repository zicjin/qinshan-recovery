emcPurchApply.auditPage = {
		//采购单信息
		getGridParam : function() {
			var _this = this;
			return {
	            type: 'grid',
	            id: 'emc-purch-apply-audit-list-grid',
	            fill: true,
	            url: emcPurchApply.baseUrl + '/queries/tasks',
	            service: 'emcPurchApplyService',
	            columns: 'purchConfirm'===_this.param.bpmn_statusCode?emcPurchApply.getConfirmListColumns():emcPurchApply.getAuditListColumns(),
	            requestData : _this.param,
	            activateFirstRow : true,
	            order : {
	                'createdTime' : 'desc'
	            },
	            onRowActive : function(index, row){
	                Gikam.getComp('emc-purch-apply-audit-list-uploader').setOptions({
	                    bizId : row.id
	                });
	                Gikam.getComp('emc-purch-detail-audit-list-grid').refresh({
	                        url : _this.getPurchDetailUrl(row.id)
	                    });
	            },
	            toolbar: [{
	                type: 'btnGroup',
	                items: [{
	                    type: 'button',
	                    text: 'GIKAM.WORKFLOW.BUTTON.PASS',
	                    icon: 'pass',
	                    onClick: function () {
	                    	_this.pass();
	                    }
	                }, {
	                    type: 'button',
	                    text: 'GIKAM.WORKFLOW.BUTTON.REJECT',
	                    icon: 'reject',
	                    onClick: function () {
	                    	_this.reject();
	                    }
	                }, {
						type : 'button',
						text : 'GIKAM.BUTTON.PREVIEW',
						icon : 'print',
						onClick : function() {
							Gikam.viewReport('emc_PURCH ', Gikam.getComp('emc-purch-apply-audit-list-grid').getSelections());
						}
					} ]
	            }]
			}
		},

		//采购明细信息
		getDetailGridParam : function() {
			var _this = this;
			return {
				type : 'tab',
				width : Gikam.width.S,
				panels : [{
					title : 'EMC.MODULE.MDM.PURCHAPPLIES.TAB.PURCH_DETAIL.BASE_INFO',
					items : [{
	                    type : 'grid',
	                    id : 'emc-purch-detail-audit-list-grid',
	                    service : 'emcPurchDetailServiceImpl',
	                    columns : emcPurchApply.getAuditPurchDetailColumns()
					}]
				}]
			}
		},

	    getAttachmentGridParam : function() {
	        return {
	            type : 'uploader',
	            id : 'emc-purch-apply-audit-list-uploader',
	            dbTable : 'T_DINGAN_PURCH',
	            readonly : true
	        }
	    },

	    getPurchDetailUrl : function(purchId) {
	        if (purchId) {
	            return Gikam.printf(emcPurchApply.baseUrl+'/{id}/queries', {
	                id : purchId
	            })
	        }
	    },

	    pass : function() {
	        var grid = Gikam.getComp('emc-purch-apply-audit-list-grid');
	        Gikam.create('workflow').pass({
	            data : grid.getSelections(),
	            pageObject : emcPurchApply
	        }).done(function() {
	            grid.refresh();
	        });
	    },

	    reject : function() {
	        var grid = Gikam.getComp('emc-purch-apply-audit-list-grid');
	        Gikam.create('workflow').reject({
	            data : grid.getSelections(),
	            pageObject : emcPurchApply
	        }).done(function() {
	            grid.refresh();
	        });
	    },

	    create : function() {
	        Gikam.create('layout', {
	            id : 'emc-purch-apply-audit-list-layout',
	            renderTo : workspace.window.$dom,
	            west : {
	            	width : '80%',
	            	items : [ {
	            		type : 'layout',
	            		north : {
	            			height : '50%',
	            			items : [ this.getGridParam() ]
	            		},
	            		center : {
	            			height : '50%',
	            			items : [ this.getDetailGridParam() ]
	            		}
	            	} ]
	            },
	            center : {
	                items : [ {
	                    type : 'tab',
	                    fill : true,
	                    panels : [ {
	                        title : 'EMC.MODULE.MDM.PURCHAPPLIES.TAB.ATTACHMENT',
	                        items : [ this.getAttachmentGridParam() ]
	                    } ]
	                } ]
	            }
	        });
	    },

	    init : function(param) {
			if ('purchAudit'===param.bpmn_statusCode){
				param.auditorId_SEQ=workspace.user.id;
			}else if ('purchConfirm'===param.bpmn_statusCode){
				param.confirmerId_SEQ=workspace.user.id;
			}
	    	this.param = param;
	        this.create();
	    }
};
