var coreRoundingRule = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/rounding-rules',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_ROUNDING_RULE.ID',
            field : 'id',
            type : 'link',
            width : 110
        }, {
            title : 'T_CORE_ROUNDING_RULE.LOCKED',
            field : 'locked',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'locked'
            },
            width : 100
        }, {
            title : 'T_CORE_ROUNDING_RULE.RULENAME',
            field : 'ruleName',
            width : 200
        }, {
            title : 'T_CORE_ROUNDING_RULE.ROUNDINGREMARK',
            field : 'remark',
            width : 500,
        }, Gikam.status.getSuspendColumn() ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ROUNDING_RULE.ID',
            validators : [ 'notEmpty', 'strLength[1,10]', 'remote[/secure/core/module/sys/rounding-rules/{id}/action/validate-unique]' ],
            preInsert : true,
        }, {
            field : 'ruleName',
            title : 'T_CORE_ROUNDING_RULE.RULENAME',
            validators : [ 'notEmpty', 'strLength[1,32]' ],
            preInsert : true,
        }, {
            field : 'remark',
            title : 'T_CORE_ROUNDING_RULE.REMARK',
            type : 'textarea',
            validators : [ 'notEmpty', 'strLength[1,256]' ],
            colspan : 2,
            rows : 4,
            preInsert : true,
        }, {
            field : 'script',
            title : 'T_CORE_ROUNDING_RULE.SCRIPT',
            type : 'textarea',
            colspan : 2,
            rows : 20,
            height : 300,
            validators : [ 'notEmpty' ],
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            title : 'T_CORE_ROUNDING_RULE.ID',
            field : 'id',
            width : 90
        }, {
            title : 'T_CORE_ROUNDING_RULE.RULENAME',
            field : 'ruleName',
            width : 110
        }, {
            title : 'T_CORE_ROUNDING_RULE.PARAM',
            field : 'param',
            editor : true,
            width : 160,
            sort : false,
            filter : false
        }, {
            title : 'T_CORE_ROUNDING_RULE.ROUNDINGREMARK',
            field : 'remark',
            width : 400
        } ]
    },
    
    getCalculateFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ROUNDING_RULE.ID',
            readonly : true
        }, {
            field : 'originalResult',
            title : 'T_CORE_ROUNDING_RULE.ORIGINALRESULT',
            validators : [ 'notEmpty','number' ],
        }, {
            field : 'param',
            title : 'T_CORE_ROUNDING_RULE.PARAM',
            validators : [ 'notEmpty' ],
        }, {
            field : 'revisionResult',
            title : 'T_CORE_ROUNDING_RULE.REVISIONRESULT',
        } ]
    },
});