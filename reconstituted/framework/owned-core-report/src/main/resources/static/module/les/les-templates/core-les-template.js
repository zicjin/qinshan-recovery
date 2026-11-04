var coreLesTemplate = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/les/les-templates',

    getBaseInfoFormFields : function() {
        return []
    },

    getPreInsertFormFields : function() {
        return [ {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENAME',
            field : 'templateName',
            preInsert : true,
            validators : [ 'notEmpty' ]
        }, {
            title : 'T_CORE_LES_TEMPLATE.LESTEMPLATECATEGORY',
            field : 'lesTemplateCategory',
            type : 'select',
            category : 'les-template-category',
            preInsert : true
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENO',
            field : 'templateNo',
            width : 150,
            type : 'link'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENAME',
            width : 150,
            field : 'templateName'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATESTATUS',
            field : 'templateStatus',
            width : 80,
            type : 'select',
            category : 'les-template-status'
        }, {
            title : 'T_CORE_LES_TEMPLATE.LESTEMPLATECATEGORY',
            field : 'lesTemplateCategory',
            type : 'select',
            category : 'les-template-category'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATEVERSION',
            width : 80,
            field : 'templateVersion'
        }, {
            title : 'T_CORE_LES_TEMPLATE.CREATEDBYNAME',
            field : 'createdByName'
        }, {
            title : 'T_CORE_LES_TEMPLATE.CREATEDTIME',
            field : 'createdTime'
        }, {
            field : 'approvedByName',
            width : 100,
            title : 'T_CORE_LES_TEMPLATE.APPROVEDBYNAME'
        }, {
            field : 'approveDate',
            width : 100,
            title : 'T_CORE_LES_TEMPLATE.APPROVEDATE'
        } ]
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENO',
            field : 'templateNo',
            width : 150,
            type : 'link'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENAME',
            width : 150,
            field : 'templateName'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATESTATUS',
            field : 'templateStatus',
            width : 80,
            type : 'select',
            category : 'les-template-status'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATEVERSION',
            field : 'templateVersion',
            width : 80
        }, {
            title : 'T_CORE_LES_TEMPLATE.CREATEDBYNAME',
            field : 'createdByName'
        }, {
            title : 'T_CORE_LES_TEMPLATE.CREATEDTIME',
            field : 'createdTime'
        }, {
            field : 'approvedByName',
            width : 100,
            title : 'T_CORE_LES_TEMPLATE.APPROVEDBYNAME'
        }, {
            field : 'approveDate',
            width : 100,
            title : 'T_CORE_LES_TEMPLATE.APPROVEDATE'
        } ]
    },

    getChooseListColumns : function(isSingle) {
        var columns = [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENO',
            field : 'templateNo',
            width : 150
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATENAME',
            field : 'templateName'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATESTATUS',
            field : 'templateStatus',
            width : 150,
            type : 'select',
            category : 'les-template-status'
        }, {
            title : 'T_CORE_LES_TEMPLATE.TEMPLATEVERSION',
            field : 'templateVersion'
        }, {
            title : 'T_CORE_LES_TEMPLATE.CREATEDBYNAME',
            field : 'createdByName'
        }, {
            title : 'T_CORE_LES_TEMPLATE.CREATEDTIME',
            field : 'createdTime'
        }, {
            field : 'approvedByName',
            width : 100,
            title : 'T_CORE_LES_TEMPLATE.APPROVEDBYNAME'
        }, {
            field : 'approveDate',
            width : 100,
            title : 'T_CORE_LES_TEMPLATE.APPROVEDATE'
        } ];

        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },

    getUsedOrgListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_LES_TEMPLATE_ORG.USEDORGID',
            field : 'usedOrgId'
        }, {
            title : 'CORE.MODULE.LES.LES_TEMPLATES.TITLE.USEDORGNAME',
            field : 'ext$.usedorgname'
        } ]
    },

    getWeighingListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_LES_TEMPLATE.SUBSTANCENAME',
            field : 'substanceName',
            editor : true,
        }, {
            title : 'T_CORE_LES_TEMPLATE.WEIGHT',
            field : 'weight',
            search : true
        }, {
            title : 'T_CORE_LES_TEMPLATE.REMARK',
            field : 'remark',
            editor : true,
        } ]
    },

    getResultColumns : function(type) {
        return [ {
            checkbox : true
        }, {
            field : 'orderNumber',
            title : type === 'SystemFlexibility' ? 'T_CORE_LES_TEMPLATE.LESUNITNO' : 'T_CORE_LES_TEMPLATE.LESBATCHNO'
        }, {
            field : 'testName',
            title : 'T_CORE_LES_TEMPLATE.LESUNITNAME'
        }, {
            field : 'testItemName',
            title : 'T_CORE_LES_TEMPLATE.TESTITEMNAME'
        }, {
            field : 'revisionResult',
            title : 'T_CORE_LES_TEMPLATE.REVISIONRESULT'
        } ]
    },

    workflow : {
        dbTable : 'T_CORE_LES_TEMPLATE',
    }

});