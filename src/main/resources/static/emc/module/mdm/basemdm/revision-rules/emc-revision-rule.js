var emcRevisionRule = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/revision-rules',

    getChooseListColumns : function() {
        return [ {
            width : 76,
            title : 'T_EMC_REVISION_RULE.ID',
            field : 'id',
            formatter : function(index, x, row) {
                if (row.id == 'NONE') {
                    return '无修约规则';
                }
                return row.id;
            }
        }, {
            width : 110,
            title : 'T_EMC_REVISION_RULE.EFFECTIVEDIGIT',
            field : 'effectiveDigit',
            editor : true,
            onEditorInitReadonly : function(row) {
                if(row.id == 'NONE'){
                    return false;
                }
            },
        }, {
            title : 'T_EMC_REVISION_RULE.RULEDESC',
            field : 'ruleDesc',
            //type : 'ckeditorText',
            editor : true,
            //readonly : true,
            onEditorInitReadonly : function(row) {
                if(row.id == 'NONE'){
                    return false;
                }
            },
        } ]
    },

});