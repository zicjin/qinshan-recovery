var emcOaOrgRelation = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'limsOrgCode',
            title : 'T_EMC_OA_ORG_RELATION.LIMSORGCODE'
        }, {
            field : 'oaGqOrgCode',
            title : 'T_EMC_OA_ORG_RELATION.OAGQORGCODE',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'oaCwOrgCode',
            title : 'T_EMC_OA_ORG_RELATION.OACWORGCODE',
            validators : [ 'strLength[0,40]' ],
            editor : true
        } ,{
           field : 'remark',
           title : '备注',
           validators : [ 'strLength[0,120]' ],
           editor : true
       }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'limsOrgCode',
            title : 'T_EMC_OA_ORG_RELATION.LIMSORGCODE',
            preInsert : true,
            type : 'choose',
            category : 'org',
            single : true,
            onBeforeChoose : function() {
                return {
                    target_filter : JSON.stringify({
                        type : 'unassigned',
                        targetTable : "T_EMC_OA_ORG_RELATION",
                        targetMatchColumn : "LIMSORGCODE",
                        thisMatchColumn : "ID",
                    }),
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        limsOrgCode : rows[0].id,
                    });
                }
            }
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/oa-org-relations'
});
