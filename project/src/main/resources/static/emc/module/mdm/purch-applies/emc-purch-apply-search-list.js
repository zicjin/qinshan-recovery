emcPurchApply.searchPage = {
		//采购单信息
		getGridParam : function() {
			var _this = this;
			return {
	            type: 'grid',
	            id: 'emc-purch-apply-search-list-grid',
	            fill: true,
	            url: emcPurchApply.baseUrl + '/queries',
	            service: 'emcPurchApplyService',
	            columns: emcPurchApply.getSearchListColumns(),
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
				toolbar : [ {
					type : 'button',
					text : 'GIKAM.BUTTON.PREVIEW',
					icon : 'print',
					onClick : function() {
						Gikam.viewReport('emc_PURCH', Gikam.getComp('emc-purch-apply-search-list-grid').getSelections());
					}
				}, {
					type: 'button',
					text: 'GIKAM.BUTTON.DELETE',
					icon: 'remove',
					onClick: function () {
						Gikam.getComp('emc-purch-apply-search-list-grid').deleteRows(emcPurchApply.baseUrl);
					}
				} ]
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
	                    columns : emcPurchApply.getSearchPurchDetailColumns()
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

	    init : function() {
	        this.create();
	    }
};
