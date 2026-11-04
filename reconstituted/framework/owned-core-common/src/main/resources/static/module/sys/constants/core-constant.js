var coreConstant = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/constants',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_CONSTANT.ID',
            readonly : true,
        }, {
            field : 'constantValue',
            title : 'T_CORE_CONSTANT.CONSTANTVALUE'
        }, {
            field : 'constantDesc',
            title : 'T_CORE_CONSTANT.CONSTANTDESC',
            type : 'textarea',
            readonly : true
        }, {
            field : 'classfication',
            title : 'T_CORE_CONSTANT.CLASSFICATION',
            readonly : true
        } ]
    },

    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_CONSTANT.ID',
            field : 'id',
            search : true,
            width : 300
        }, {
            field : 'constantValue',
            title : 'T_CORE_CONSTANT.CONSTANTVALUE',
            editor : true,
            search : true,
            validators : [ 'strLength[0,1500]' ],
            width : 300
        }, {
            field : 'constantDesc',
            title : 'T_CORE_CONSTANT.CONSTANTDESC',
            width : 500
        }, {
            field : 'classfication',
            title : 'T_CORE_CONSTANT.CLASSFICATION',
            search : true
        } ]
    }
});