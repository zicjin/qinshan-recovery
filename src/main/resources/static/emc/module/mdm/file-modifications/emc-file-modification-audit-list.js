emcFileModification.auditPage = {
		getGridParam : function() {
			var _this = this;
			return {
	            type: 'grid',
	            id: 'emc-file-modification-audit-list-grid',
	            fill: true,
	            url: emcFileModification.baseUrl + '/queries/tasks',
	            service: 'emcFileModificationService',
	            columns:  'fileConfirm'===_this.param.bpmn_statusCode?emcFileModification.getConfirmListColumns():emcFileModification.getAuditListColumns(),
	            requestData : _this.param,
	            activateFirstRow : true,
	            order : {
	                'createdTime' : 'desc'
	            },
	            onRowActive : function(index, row){
	                Gikam.getComp('emc-file-modification-audit-list-uploader').setOptions({
	                    bizId : row.id
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
							Gikam.viewReport('emc_FILE_MODIFY ', Gikam.getComp('emc-file-modification-audit-list-grid').getSelections());
						}
					} ]
	            }]
			}
		},

	    pass : function() {
	        var grid = Gikam.getComp('emc-file-modification-audit-list-grid');
	        Gikam.create('workflow').pass({
	            data : grid.getSelections(),
	            pageObject : emcFileModification
	        }).done(function() {
	            grid.refresh();
	        });
	    },

	    reject : function() {
	        var grid = Gikam.getComp('emc-file-modification-audit-list-grid');
	        Gikam.create('workflow').reject({
	            data : grid.getSelections(),
	            pageObject : emcFileModification
	        }).done(function() {
	            grid.refresh();
	        });
	    },

	    getAttachmentGridParam : function() {
	        return {
	            type : 'uploader',
	            id : 'emc-file-modification-audit-list-uploader',
	            dbTable : 'T_DINGAN_FILE_MODIFY',
	            readonly : true
	        }
	    },

	    create : function() {
	        Gikam.create('layout', {
	            id : 'emc-file-modification-audit-list-layout',
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

	    init : function(param) {
			if ('fileAudit'===param.bpmn_statusCode){
				param.auditorId_SEQ=workspace.user.id;
			}else if ('fileConfirm'===param.bpmn_statusCode){
				param.confirmerId_SEQ=workspace.user.id;
			}
	    	this.param = param;
	        this.create();
	    }
};
