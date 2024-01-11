var emcFileApply = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/file/file-applies',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcFileApply.workflow.dbTable), {
            field : 'applyNo',
            title : 'T_EMC_FILE_APPLY.APPLYNO',
        }, {
            field : 'fileNo',
            title : 'T_EMC_FILE_APPLY.FILENO',
        }, {
            field : 'ext$.filename',
            title : 'T_EMC_FILE.FILENAME',
        }, {
            field : 'reason',
            title : 'T_EMC_FILE_APPLY.REASON',
            width : '400',
            editor : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_FILE_APPLY.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_EMC_FILE_APPLY.CREATEDTIME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'fileNo',
            title : 'T_EMC_FILE_APPLY.FILENO',
            type : 'choose',
            category : 'file',
            single : true,
            preInsert : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        fileId : rows[0].id,
                        fileNo : rows[0].fileNo,
                        ext$ : {
                            filename : rows[0].fileName
                        }
                    });
                }
            },
            onClean : function(index) {
                this.setData({
                    index : index,
                    fileId : '',
                    fileNo : '',
                    ext$ : {
                        filename : rows[0].fileName
                    }
                });
            }
        }, {
            field : 'applyNo',
            title : 'T_EMC_FILE_APPLY.APPLYNO',
            validators : [ 'remote[' + emcFileApply.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,36]' ],
            preInsert : true
        } ];
    },
    
    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcFileApply.workflow.dbTable), {
            field : 'applyNo',
            title : 'T_EMC_FILE_APPLY.APPLYNO',
        }, {
            field : 'fileNo',
            title : 'T_EMC_FILE_APPLY.FILENO',
        }, {
            field : 'ext$.filename',
            title : 'T_EMC_FILE.FILENAME',
        }, {
            field : 'reason',
            title : 'T_EMC_FILE_APPLY.REASON',
            width : '400'
        }, {
            field : 'createdByName',
            title : 'T_EMC_FILE_APPLY.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_EMC_FILE_APPLY.CREATEDTIME'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            index : true
        }, Gikam.status.getBpmnColumn(emcFileApply.workflow.dbTable), {
            field : 'applyNo',
            title : 'T_EMC_FILE_APPLY.APPLYNO',
        }, {
            field : 'fileNo',
            title : 'T_EMC_FILE_APPLY.FILENO',
            type : 'link'
        }, {
            field : 'ext$.filename',
            title : 'T_EMC_FILE.FILENAME',
        }, {
            field : 'reason',
            title : 'T_EMC_FILE_APPLY.REASON',
            width : '400'
        }, {
            field : 'createdByName',
            title : 'T_EMC_FILE_APPLY.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_EMC_FILE_APPLY.CREATEDTIME'
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_FILE_APPLY',
        columns : [ {
            field : 'id',
            title : 'T_EMC_FILE_APPLY.ID'
        } ]
    }

});
