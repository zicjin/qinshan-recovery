var emcSampleSet = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptName',
            title : 'T_EMC_SAMPLE_SET.EQUIPTNAME',
            editor : true
        }, {
            field : 'rate',
            title : 'T_EMC_SAMPLE_SET.RATE',
            editor : true
        }, {
            field : 'equiptType',
            title : 'T_EMC_SAMPLE_SET.EQUIPTTYPE',
            editor : true
        }, {
            field : 'standardSource',
            title : 'T_EMC_SAMPLE_SET.STANDARDSOURCE',
            editor : true
        }, {
            field : 'describe',
            title : 'T_EMC_SAMPLE_SET.DESCRIBE',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE_SET.REMARK',
            editor : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'equiptName',
            title : 'T_EMC_SAMPLE_SET.EQUIPTNAME',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'rate',
            title : 'T_EMC_SAMPLE_SET.RATE',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'equiptType',
            title : 'T_EMC_SAMPLE_SET.EQUIPTTYPE',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'standardSource',
            title : 'T_EMC_SAMPLE_SET.STANDARDSOURCE',
            validators : [ 'strLength[0,333]' ]
        }, {
            field : 'describe',
            title : 'T_EMC_SAMPLE_SET.DESCRIBE',
            validators : [ 'strLength[0,333]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE_SET.REMARK',
            validators : [ 'strLength[0,333]' ]
        }, ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        },  {
            field : 'equiptName',
            title : 'T_EMC_SAMPLE_SET.EQUIPTNAME'
        }, {
            field : 'rate',
            title : 'T_EMC_SAMPLE_SET.RATE'
        }, {
            field : 'equiptType',
            title : 'T_EMC_SAMPLE_SET.EQUIPTTYPE'
        }, {
            field : 'standardSource',
            title : 'T_EMC_SAMPLE_SET.STANDARDSOURCE'
        }, {
            field : 'describe',
            title : 'T_EMC_SAMPLE_SET.DESCRIBE'
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE_SET.REMARK'
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'equiptName',
            title : 'T_EMC_SAMPLE_SET.EQUIPTNAME'
        }, {
            field : 'rate',
            title : 'T_EMC_SAMPLE_SET.RATE'
        }, {
            field : 'equiptType',
            title : 'T_EMC_SAMPLE_SET.EQUIPTTYPE'
        }, {
            field : 'standardSource',
            title : 'T_EMC_SAMPLE_SET.STANDARDSOURCE'
        }, {
            field : 'describe',
            title : 'T_EMC_SAMPLE_SET.DESCRIBE'
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE_SET.REMARK'
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample-sets'
});
