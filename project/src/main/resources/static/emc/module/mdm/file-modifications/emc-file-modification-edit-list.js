emcFileModification.editPage = {
		getGridParam : function() {
			var _this = this;
			return {
	            type: 'grid',
	            id: 'emc-file-modification-edit-list-grid',
	            fill: true,
	            url: emcFileModification.baseUrl + '/queries/raw',
	            service: 'emcFileModificationService',
	            columns: emcFileModification.getEditListColumns(),
	            activateFirstRow : true,
	            order : {
	                'createdTime' : 'desc'
	            },
	            onLoadSuccess : function(data) {
	                if (!Gikam.isEmpty(data)) {
	                    Gikam.getComp('emc-file-modification-edit-list-grid').activeRowByIndex(0);
	                } else {
	                    if (Gikam.getComp('emc-file-modification-edit-list-uploader')) {
	                        Gikam.getComp('emc-file-modification-edit-list-uploader').grid.cleanData();
	                    }
	                }
	            },
	            onRowActive : function(index, row){
	                Gikam.getComp('emc-file-modification-edit-list-uploader').setOptions({
	                    bizId : row.id
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
	                            modalTitle : 'EMC.MODULE.MDM.FILEMODIFICATIONS.MODAL.FILEINPUT',
	                            fields : emcFileModification.getFileInputColumns(),
	                            url : emcFileModification.baseUrl,
								displayReadonly : false
	                        }).done(function(id) {
	                            Gikam.getComp('emc-file-modification-edit-list-grid').refresh();
	                        });
	                    }
	                }, {
	                    type: 'button',
	                    text: 'GIKAM.BUTTON.DELETE',
	                    icon: 'remove',
	                    onClick: function () {
	                        Gikam.getComp('emc-file-modification-edit-list-grid').deleteRows(emcFileModification.baseUrl);
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
							Gikam.viewReport('emc_FILE_MODIFY ', Gikam.getComp('emc-file-modification-edit-list-grid').getSelections());
						}
					} ]
	            }]
			}
		},

	    submit : function() {
	        var grid = Gikam.getComp('emc-file-modification-edit-list-grid');
	        Gikam.create('workflow').submit({
	            data : grid.getSelections(),
	            pageObject : emcFileModification
	        }).done(function() {
	            grid.refresh();
	        });
	    },

	    getAttachmentGridParam : function() {
	        return {
	            type : 'uploader',
	            id : 'emc-file-modification-edit-list-uploader',
	            dbTable : 'T_DINGAN_FILE_MODIFY'
	        }
	    },

	    create : function() {
	        Gikam.create('layout', {
	            id : 'emc-file-modification-edit-list-layout',
	            renderTo : workspace.window.$dom,
	            west : {
	            	width : 850,
	                items : [ this.getGridParam() ]
	            },
	            center : {
	                items : [ {
	                    type : 'tab',
	                    fill : true,
	                    panels : [ {
	                        title : 'EMC.MODULE.MDM.FILEMODIFICATIONS.TAB.ATTACHMENT',
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
