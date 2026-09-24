emcPurchApply.editPage = {
		//采购单信息
		getGridParam : function() {
			var _this = this;
			return {
	            type: 'grid',
	            id: 'emc-purch-apply-edit-list-grid',
	            fill: true,
	            url: emcPurchApply.baseUrl + '/queries/raw',
	            service: 'emcPurchApplyService',
	            columns: emcPurchApply.getEditListColumns(),
	            activateFirstRow : true,
	            order : {
	                'createdTime' : 'desc'
	            },
	            onLoadSuccess : function(data) {
	                if (!Gikam.isEmpty(data)) {
	                    Gikam.getComp('emc-purch-apply-edit-list-grid').activeRowByIndex(0);
	                } else {
	                    if (Gikam.getComp('emc-purch-detail-edit-list-grid')) {
	                        Gikam.getComp('emc-purch-detail-edit-list-grid').cleanData();
	                    }
	                    if (Gikam.getComp('emc-purch-apply-edit-list-uploader')) {
	                        Gikam.getComp('emc-purch-apply-edit-list-uploader').grid.cleanData();
	                    }
	                }
	            },
	            onRowActive : function(index, row){
	                Gikam.getComp('emc-purch-apply-edit-list-uploader').setOptions({
	                    bizId : row.id
	                });
	                Gikam.getComp('emc-purch-detail-edit-list-grid').refresh({
	                        url : _this.getPurchDetailUrl(row.id)
	                    });
	            },
	            toolbar: [{
	                type: 'btnGroup',
	                items: [{
	                    type: 'button',
	                    text: 'GIKAM.BUTTON.INSERT',
	                    icon: 'add',
	                    onClick: function () {
	                        Gikam.preInsert({
	                            modalTitle : 'EMC.MODULE.MDM.PURCHAPPLIES.MODAL.ADD',
	                            fields : emcPurchApply.getPurchAddColumns(),
	                            url : emcPurchApply.baseUrl
	                        }).done(function(id) {
	                            Gikam.getComp('emc-purch-apply-edit-list-grid').refresh();
	                        });
	                    }
	                }, {
	                    type: 'button',
	                    text: 'GIKAM.BUTTON.DELETE',
	                    icon: 'remove',
	                    onClick: function () {
	                        Gikam.getComp('emc-purch-apply-edit-list-grid').deleteRows(emcPurchApply.baseUrl);
	                    }
	                }, {
	                    type: 'button',
	                    text: 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
	                    icon: 'submit',
	                    onClick: function () {
	                        _this.submit();
	                    }
	                }, {
						type : 'button',
						text : 'GIKAM.BUTTON.PREVIEW',
						icon : 'print',
						onClick : function() {
							Gikam.viewReport('emc_PURCH ', Gikam.getComp('emc-purch-apply-edit-list-grid').getSelections());
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
	                    id : 'emc-purch-detail-edit-list-grid',
	                    service : 'emcPurchDetailServiceImpl',
	                    columns : emcPurchApply.getPurchDetailColumns(),
	                    toolbar : [ {
	                        type : 'button',
	                        text : 'GIKAM.BUTTON.INSERT',
	                        icon : 'add',
	                        onClick : function() {
	                        	_this.insertPurchDetailRow();
	                        }
	                    }, {
	                        type : 'button',
	                        text : 'GIKAM.BUTTON.DELETE',
	                        icon : 'remove',
	                        onClick : function() {
	                        	_this.deletePurchDetailRow();
	                        }
	                    }, {
							type : 'button',
							text : 'GIKAM.BUTTON.EXCEL_EXPORT',
							icon : 'edit',
							onClick : function() {
								Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
									templateCode : 'emcPurchapplyExportTemplate',
									service : 'emcPurchapplyExportTemplate'
								})).done(function(fileUri) {
									var fileUrl = IFM_CONTEXT + fileUri;
									Gikam.emcDownloadFile(fileUrl);
								});
							}
						}, {
							type : 'button',
							text : 'GIKAM.BUTTON.EXCEL_IMPORT',
							icon : 'edit',
							onClick : function() {
								var selections = Gikam.getComp('emc-purch-apply-edit-list-grid').getSelections();
								if (selections.length === 1) {
									Gikam.importExcel({
										url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcPurchapplyImportFileFunction',
										bizId : selections[0].id,
										dbTable : 'T_DINGAN_PURCH_DETAIL',
										onImportSuccess : function(files) {
											Gikam.getComp('emc-purch-detail-edit-list-grid').refresh();
										}
									});
								} else {
									Gikam.alert('EMC.MODULE.MDM.PURCHAPPLIES.MODAL.CHOOSE_ONE');
								}
							}
						} ]
					}]
				}]
			}
		},

	    submit : function() {
	        var grid = Gikam.getComp('emc-purch-apply-edit-list-grid');
	        Gikam.create('workflow').submit({
	            data : grid.getSelections(),
	            pageObject : emcPurchApply
	        }).done(function() {
	            grid.refresh();
	        });
	    },

	    getAttachmentGridParam : function() {
	        return {
	            type : 'uploader',
	            id : 'emc-purch-apply-edit-list-uploader',
	            dbTable : 'T_DINGAN_PURCH'
	        }
	    },

	    getPurchDetailUrl : function(purchId) {
	        if (purchId) {
	            return Gikam.printf(emcPurchApply.baseUrl+'/{id}/queries', {
	                id : purchId
	            })
	        }
	    },

	    insertPurchDetailRow : function() {
	        var activeRow = Gikam.getComp('emc-purch-apply-edit-list-grid').getActivedRow();
	        var url = Gikam.printf(emcPurchApply.baseUrl + '/{id}/insert', {
	            id : activeRow.id
	        });

	        Gikam.getComp('emc-purch-detail-edit-list-grid').insert(url, [ {
	            id : activeRow.id
	        } ]);
	    },

	    deletePurchDetailRow : function() {
	        var url = Gikam.printf(emcPurchApply.baseUrl + '/deletePurchDetail');
	        Gikam.getComp('emc-purch-detail-edit-list-grid').deleteRows(url);
	    },

	    create : function() {
	        Gikam.create('layout', {
	            id : 'emc-purch-apply-edit-list-layout',
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
