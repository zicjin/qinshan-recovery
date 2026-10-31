var emcInfoConfig = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'configName',
            title : 'T_EMC_INFO_CONFIG.CONFIGNAME',
            editor : true,
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'display',
            title : 'T_EMC_INFO_CONFIG.DISPLAY',
            type : 'select',
            search : true,
            width : 70,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_INFO_CONFIG.ORDERNO',
            type : 'number',
            width : 75,
            hasArrow : true,
            editor : true
        }, ];
    },

    getCategoryListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'configName',
            title : 'T_EMC_INFO_CONFIG.CONFIGNAME',
            editor : true,
            preInsert : true,
            validators : [ 'strLength [0,40]', 'notEmpty' ]
        }, {
            field : 'infoTmplId',
            title : 'T_EMC_INFO_CONFIG.INFOTMPLID',
            type : 'select',
            search : true,
            category : 'infoTmpl',
            preInsert : true,
            editor : true
        }, {
            field : 'display',
            title : 'T_EMC_INFO_CONFIG.DISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            width : 70,
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_INFO_CONFIG.ORDERNO',
            type : 'number',
            hasArrow : true,
            width : 75,
            editor : true
        } ];
    },

    getReportListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'configName',
            title : 'T_EMC_INFO_CONFIG.CONFIGNAME',
            editor : true,
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'display',
            title : 'T_EMC_INFO_CONFIG.DISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            width : 70,
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'url',
            title : 'T_EMC_INFO_CONFIG.URL',
            editor : true,
            validators : [ 'strLength [0,40]' ]
        }, {
            field : 'orderNo',
            title : 'T_EMC_INFO_CONFIG.ORDERNO',
            type : 'number',
            width : 75,
            hasArrow : true,
            editor : true,
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/info/info-configs',

});
