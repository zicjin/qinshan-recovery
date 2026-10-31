var emcSealRule = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'activedFlag',
            title : 'T_EMC_SEAL_RULE.ACTIVEDFLAG',
            editor : true,
            firstBlank : false,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 90
        }, {
            field : 'name',
            title : 'T_EMC_SEAL_RULE.NAME',
            editor : true
        }, {
            field : 'field',
            title : 'T_EMC_SEAL_RULE.FIELD',
            editor : true
        }, {
            field : 'businessCategory',
            title : 'T_EMC_SEAL_RULE.BUSINESSCATEGORY',
            editor : true
        }, {
            field : 'laboratory',
            title : 'T_EMC_SEAL_RULE.LABORATORY',
            editor : true
        } ];
    },

    getRuleDetailsListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sealGroupNo',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALGROUPNO',
            editor : true
        }, {
            field : 'sealGroupName',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALGROUPNAME',
            editor : true
        }, {
            field : 'sealNo',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALNO',
            editor : true
        }, {
            field : 'sealName',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALNAME',
            editor : true
        }, {
            field : 'pageType',
            title : 'T_EMC_SEAL_RULE_DETAILS.PAGETYPE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'sealRulePageType'
            }
        }, {
            field : 'fixedPageNum',
            title : 'T_EMC_SEAL_RULE_DETAILS.FIXEDPAGENUM',
            editor : true
        }, {
            field : 'customPageNum',
            title : 'T_EMC_SEAL_RULE_DETAILS.CUSTOMPAGENUM',
            editor : true
        }, {
            field : 'xvalue',
            title : 'T_EMC_SEAL_RULE_DETAILS.XVALUE',
            editor : true
        }, {
            field : 'yvalue',
            title : 'T_EMC_SEAL_RULE_DETAILS.YVALUE',
            editor : true
        }, {
            field : 'ext$.filename',
            title : 'T_EMC_SEAL_RULE_DETAILS.FILENAME',
            type : 'link'
        } ];
    },

    getRuleDetailsChooseListColumns : function() {
        return [ {
            field : 'ext$.name',
            title : 'T_EMC_SEAL_RULE.NAME'
        }, {
            field : 'ext$.field',
            title : 'T_EMC_SEAL_RULE.FIELD'
        }, {
            field : 'ext$.businesscategory',
            title : 'T_EMC_SEAL_RULE.BUSINESSCATEGORY'
        }, {
            field : 'ext$.laboratory',
            title : 'T_EMC_SEAL_RULE.LABORATORY'
        }, {
            field : 'sealGroupNo',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALGROUPNO'
        }, {
            field : 'sealGroupName',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALGROUPNAME'
        }, {
            field : 'sealNo',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALNO'
        }, {
            field : 'sealName',
            title : 'T_EMC_SEAL_RULE_DETAILS.SEALNAME'
        }, {
            field : 'pageType',
            title : 'T_EMC_SEAL_RULE_DETAILS.PAGETYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'sealRulePageType'
            }
        }, {
            field : 'fixedPageNum',
            title : 'T_EMC_SEAL_RULE_DETAILS.FIXEDPAGENUM'
        }, {
            field : 'customPageNum',
            title : 'T_EMC_SEAL_RULE_DETAILS.CUSTOMPAGENUM'
        }, {
            field : 'xvalue',
            title : 'T_EMC_SEAL_RULE_DETAILS.XVALUE'
        }, {
            field : 'yvalue',
            title : 'T_EMC_SEAL_RULE_DETAILS.YVALUE'
        }, {
            field : 'ext$.filename',
            title : 'T_EMC_SEAL_RULE_DETAILS.FILENAME',
            type : 'link'
        }, ];
    },

    getRuleChooseListColumns : function() {
        return [ {
            field : 'name',
            title : 'T_EMC_SEAL_RULE.NAME'
        }, {
            field : 'field',
            title : 'T_EMC_SEAL_RULE.FIELD'
        }, {
            field : 'businessCategory',
            title : 'T_EMC_SEAL_RULE.BUSINESSCATEGORY'
        }, {
            field : 'laboratory',
            title : 'T_EMC_SEAL_RULE.LABORATORY'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/seal-rules'
});
