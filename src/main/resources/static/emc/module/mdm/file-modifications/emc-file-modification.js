var emcFileModification = Gikam.getPageObject({

	baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/file-modifications',
	fileUrl : IFM_CONTEXT + '/secure/emc/module/mdm/file/files',

	getEditListColumns : function () {
		return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcFileModification.workflow.dbTable), {
        	field : 'fileId',
        	title : 'T_DINGAN_FILE_MODIFY.FILEID'
		}, {
        	field : 'fileName',
        	title : 'T_DINGAN_FILE_MODIFY.FILENAME',
            editor : true
		}, {
        	field : 'modifyingContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYINGCONTENT',
        	editor : true
		}, {
        	field : 'modifiedContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFIEDCONTENT',
        	editor : true
		}, {
        	field : 'modifyReason',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYREASON',
        	editor : true
		}, {
        	field : 'implementDate',
        	title : 'T_DINGAN_FILE_MODIFY.IMPLEMENTDATE',
        	type : 'date',
        	editor : true
		}, {
			field : 'auditorName',
			title : 'T_DINGAN_FILE_MODIFY.AUDITORNAME',
			editor : true,
			validators : ['notEmpty'],
			category : 'emp',
			type : 'choose',
			single : true,
			onAfterChoose : function(rows) {
				if (!Gikam.isEmpty(rows)) {
					this.setData({
						id : Gikam.getComp('emc-file-modification-edit-list-grid').getActivedRow().id,
						auditorName : rows[0].userName,
						auditorId : rows[0].id
					});
				}
			},
			onClean : function() {
				this.setData({
					id : Gikam.getComp('emc-file-modification-edit-list-grid').getActivedRow().id,
					auditorName : '',
					auditorId : ''
				});
			}
		}];
	},

	getFileInputColumns : function() {
		return [{
        	field : 'fileId',
        	title : 'T_DINGAN_FILE_MODIFY.FILEID',
        	preInsert : true,
        	readonly : true
		}, {
        	field : 'fileName',
        	title : 'T_DINGAN_FILE_MODIFY.FILENAME',
        	type : 'choose',
        	category : 'fileManagement',
        	single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        fileId : rows[0].fileNo,
                        fileName : rows[0].fileName,
                    });
                }
            },
        	preInsert : true
		}, {
        	field : 'modifyingContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYINGCONTENT',
        	preInsert : true
		}, {
        	field : 'modifiedContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFIEDCONTENT',
        	preInsert : true
		}, {
        	field : 'modifyReason',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYREASON',
        	preInsert : true
		}, {
        	field : 'implementDate',
        	title : 'T_DINGAN_FILE_MODIFY.IMPLEMENTDATE',
        	type : 'date',
        	preInsert : true
		} ];
	},

	getAuditListColumns : function() {
		return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcFileModification.workflow.dbTable), {
        	field : 'fileId',
        	title : 'T_DINGAN_FILE_MODIFY.FILEID'
		}, {
        	field : 'fileName',
        	title : 'T_DINGAN_FILE_MODIFY.FILENAME'
		}, {
        	field : 'modifyingContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYINGCONTENT'
		}, {
        	field : 'modifiedContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFIEDCONTENT'
		}, {
        	field : 'modifyReason',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYREASON'
		}, {
        	field : 'implementDate',
        	title : 'T_DINGAN_FILE_MODIFY.IMPLEMENTDATE',
        	type : 'date'
		}, {
			field : 'confirmerName',
			title : 'T_DINGAN_FILE_MODIFY.CONFIRMERNAME',
			editor : true,
			validators : ['notEmpty'],
			category : 'emp',
			type : 'choose',
			single : true,
			onAfterChoose : function(rows) {
				if (!Gikam.isEmpty(rows)) {
					this.setData({
						id : Gikam.getComp('emc-file-modification-audit-list-grid').getActivedRow().id,
						confirmerName : rows[0].userName,
						confirmerId : rows[0].id
					});
				}
			},
			onClean : function() {
				this.setData({
					id : Gikam.getComp('emc-file-modification-audit-list-grid').getActivedRow().id,
					confirmerName : '',
					confirmerId : ''
				});
			}
		}];
	},

	getConfirmListColumns : function() {
		return [{
			checkbox: true
		}, Gikam.status.getBpmnColumn(emcFileModification.workflow.dbTable), {
			field : 'fileId',
			title : 'T_DINGAN_FILE_MODIFY.FILEID'
		}, {
			field : 'fileName',
			title : 'T_DINGAN_FILE_MODIFY.FILENAME'
		}, {
			field : 'modifyingContent',
			title : 'T_DINGAN_FILE_MODIFY.MODIFYINGCONTENT'
		}, {
			field : 'modifiedContent',
			title : 'T_DINGAN_FILE_MODIFY.MODIFIEDCONTENT'
		}, {
			field : 'modifyReason',
			title : 'T_DINGAN_FILE_MODIFY.MODIFYREASON'
		}, {
			field : 'implementDate',
			title : 'T_DINGAN_FILE_MODIFY.IMPLEMENTDATE',
			type : 'date'
		}];
	},

	getSearchListColumns : function() {
		return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcFileModification.workflow.dbTable), {
        	field : 'fileId',
        	title : 'T_DINGAN_FILE_MODIFY.FILEID'
		}, {
        	field : 'fileName',
        	title : 'T_DINGAN_FILE_MODIFY.FILENAME'
		}, {
        	field : 'modifyingContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYINGCONTENT'
		}, {
        	field : 'modifiedContent',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFIEDCONTENT'
		}, {
        	field : 'modifyReason',
        	title : 'T_DINGAN_FILE_MODIFY.MODIFYREASON'
		}, {
        	field : 'implementDate',
        	title : 'T_DINGAN_FILE_MODIFY.IMPLEMENTDATE',
        	type : 'date'
		}];
	},

	getChooseListColumns : function() {
		return [{
			field : 'fileNo',
			title : 'T_EMC_FILE.FILENO',
			readonly : true
		}, {
			field : 'fileName',
			title : 'T_EMC_FILE.FILENAME'
		}]
	},

    workflow : {
        dbTable : 'T_DINGAN_FILE_MODIFY',
        columns : [ {
            field : 'id',
            title : 'T_DINGAN_FILE_MODIFY.ID'
        } ]
    }
});
