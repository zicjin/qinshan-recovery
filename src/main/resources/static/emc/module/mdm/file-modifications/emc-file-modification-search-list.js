emcFileModification.searchPage = {
	getGridParam : function() {
		var _this = this;
		return {
			type: 'grid',
			id: 'emc-file-modification-edit-list-grid',
			fill: true,
			url: emcFileModification.baseUrl + '/queries',
			service: 'emcFileModificationService',
			columns: emcFileModification.getSearchListColumns(),
			activateFirstRow : true,
			order : {
				'createdTime' : 'desc'
			},
			onRowActive : function(index, row){
				Gikam.getComp('emc-file-modification-search-list-uploader').setOptions({
					bizId : row.id
				});
			},
			toolbar : [ {
				type : 'button',
				text : 'GIKAM.BUTTON.PREVIEW',
				icon : 'print',
				onClick : function() {
					Gikam.viewReport('emc_FILE_MODIFY ', Gikam.getComp('emc-file-modification-edit-list-grid').getSelections());
				}
			}, {
				type: 'button',
				text: 'GIKAM.BUTTON.DELETE',
				icon: 'remove',
				onClick: function () {
					Gikam.getComp('emc-file-modification-edit-list-grid').deleteRows(emcFileModification.baseUrl);
				}
			} ]
		}
	},

	getAttachmentGridParam : function() {
		return {
			type : 'uploader',
			id : 'emc-file-modification-search-list-uploader',
			dbTable : 'T_DINGAN_FILE_MODIFY',
			readonly : true
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
