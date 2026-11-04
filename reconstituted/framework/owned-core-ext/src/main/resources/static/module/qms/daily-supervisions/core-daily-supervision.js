var coreDailySupervision = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreDailySupervision.workflow.dbTable), {
            field : 'dailyNo',
            title : 'T_CORE_DAILY_SUPERVISION.DAILYNO'
        }, {
            field : 'createdByName',
            title : 'T_CORE_DAILY_SUPERVISION.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_DAILY_SUPERVISION.CREATEDTIME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'remark',
            title : 'T_CORE_DAILY_SUPERVISION.REMARK',
            validators : [ 'strLength[0,200]' ],
            type : 'textarea',
            height : 70
        } ];
    },

    getSupervisionItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'estimatedTime',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.ESTIMATEDTIME',
            editor : false,
            type : 'date'
        }, {
            field : 'auditName',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.AUDITNAME',
            validators : [ 'notEmpty', 'strLength[0,100]' ],
            editor : false,
            type : 'select',
            category : 'dailySupervisionAuditName'
        }, {
            field : 'auditUserId',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.AUDITUSERNAME',
            validators : [ 'notEmpty', 'strLength[0,20]' ],
            editor : true,
            type : 'select',
            category : 'user',
            search : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').setData({
                    index : rowIndex,
                    auditUserName : text
                });
            }
        }, {
            field : 'beAuditOrgId',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.BEAUDITORGNAME',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            editor : true,
            type : 'select',
            category : 'org',
            search : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                Gikam.getComp('core-daily-supervision-edit-list-supervision-item-grid').setData({
                    index : rowIndex,
                    beAuditOrgName : text
                });
            }
        }, {
            field : 'remark',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.REMARK',
            editor : true
        } ];
    },

    getSupervisionConListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'auditName',
            title : 'T_CORE_DAILY_SUPERVISION_CON.AUDITNAME',
            editor : false,
            type : 'select',
            category : 'dailySupervisionAuditName'
        }, {
            field : 'auditResult',
            title : 'T_CORE_DAILY_SUPERVISION_CON.AUDITRESULT',
            validators : [ 'notEmpty' ],
            editor : true,
            type : 'select',
            category : 'yesorno'
        }, {
            field : 'auditContent',
            title : 'T_CORE_DAILY_SUPERVISION_CON.AUDITCONTENT',
            validators : [ 'notEmpty', 'strLength[0,400]' ],
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('core-daily-supervision-edit-list-supervision-con-grid');
                var validate = true;
                Gikam.each(grid.getData(), function(index, row) {
                    if (index != rowIndex && value == row.auditContent) {
                        validate = false;
                        return;
                    }
                })
                if (!validate) {
                    grid.setData({
                        index : rowIndex,
                        auditContent : oldValue
                    });
                    grid.refreshRowByIndex(rowIndex);
                    Gikam.alert('CORE.MODULE.QMS.DAILY_SUPERVISION.HAS_SANME_AUDITCONTENT');
                }
            }
        }, {
            field : 'remark',
            title : 'T_CORE_DAILY_SUPERVISION_CON.REMARK',
            editor : true
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreDailySupervision.workflow.dbTable), {
            field : 'dailyNo',
            title : 'T_CORE_DAILY_SUPERVISION.DAILYNO'
        }, {
            field : 'createdByName',
            title : 'T_CORE_DAILY_SUPERVISION.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_DAILY_SUPERVISION.CREATEDTIME'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreDailySupervision.workflow.dbTable), {
            field : 'dailyNo',
            title : 'T_CORE_DAILY_SUPERVISION.DAILYNO'
        }, {
            field : 'createdByName',
            title : 'T_CORE_DAILY_SUPERVISION.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_DAILY_SUPERVISION.CREATEDTIME'
        } ];
    },

    getInsertDailySupervisionFields : function() {
        return [ {
            field : 'estimatedTime',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.ESTIMATEDTIME',
            preInsert : true,
            type : 'date'
        }, {
            field : 'auditName',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.AUDITNAME',
            preInsert : true,
            type : 'select',
            category : 'dailySupervisionAuditName'
        }, {
            field : 'auditUserId',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.AUDITUSERID'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.AUDITUSERNAME',
            preInsert : true,
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [{ auditUserId : 'id' }, { auditUserName : 'userName' }]
        }, {
            field : 'beAuditOrgId',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.BEAUDITORGID'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_DAILY_SUPERVISION_ITEM.BEAUDITORGNAME',
            preInsert : true,
            type : 'choose',
            category : 'org',
            single : true,
            targetFields : [{ beAuditOrgId : 'id' }, { beAuditOrgName : 'orgName' }]
        } ];
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/daily-supervisions',

    workflow : {
        dbTable : 'T_CORE_DAILY_SUPERVISION',
        columns : [ {
            field : 'id',
            title : 'T_CORE_DAILY_SUPERVISION.ID'
        } ]
    },

    columnUnEditable : function(colunms, editable) {
        Gikam.each(colunms, function(index, colunm){
            if (Gikam.isEmpty(colunm.checkbox)) {
                colunm.editor = editable;
            }
        });
        return colunms;
    },
    
    fieldReadOnly : function(fields, readOnly) {
        Gikam.each(fields, function(index, field){
            if (Gikam.isEmpty(field.checkbox)) {
                field.readonly = readOnly;
            }
        });
        return fields;
    },
    
    getAuditNameByCode : function(code) {
        var category;
        var url = '/secure/core/module/sys/codes/queries/selectable?codeCategoryId=dailySupervisionAuditName&code='+code;
        Gikam.getTextSync(url).done(function(item){
            category = Gikam.parseJson(item)[0].codeName;
        })
        return category;
    }
});
