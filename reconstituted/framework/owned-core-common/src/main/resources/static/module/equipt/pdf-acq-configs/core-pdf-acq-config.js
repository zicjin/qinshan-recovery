var corePdfAcqConfig = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'configName',
            title : 'T_CORE_PDF_ACQ_CONFIG.CONFIGNAME',
            width : 250,
            type : 'link'
        }, {
            field : 'ext$.filename',
            title : 'T_CORE_PDF_ACQ_CONFIG.FILENAME',
        }, {
            field : 'createdByName',
            title : 'T_CORE_PDF_ACQ_CONFIG.CREATEDBYNAME',
            width : 250
        }, {
            field : 'createdTime',
            title : 'T_CORE_PDF_ACQ_CONFIG.CREATEDTIME',
            width : 250
        } ];
    },

    getPdfAcqPreprocessFields : function() {
        return [ {
            field : 'keyword',
            title : 'T_CORE_PDF_ACQ_PREPROCESS.KEYWORD',
            validators : [ 'strLength[0,60]' ],
            preInsert : true,
        }, {
            field : 'operation',
            title : 'T_CORE_PDF_ACQ_PREPROCESS.OPERATION',
            type : 'select',
            category : 'file-acq-preprocess-operation',
            preInsert : true,
        }, {
            field : 'replaceStr',
            title : 'T_CORE_PDF_ACQ_PREPROCESS.REPLACESTR',
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getAcqPreprocessListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'keyword',
            title : 'T_CORE_PDF_ACQ_PREPROCESS.KEYWORD',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'operation',
            title : 'T_CORE_PDF_ACQ_PREPROCESS.OPERATION',
            editor : true,
            width : 120,
            type : 'select',
            category : 'file-acq-preprocess-operation'
        }, {
            field : 'replaceStr',
            title : 'T_CORE_PDF_ACQ_PREPROCESS.REPLACESTR',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getChooseListColumns : function(isSingle) {
        return [ {
            field : 'configName',
            title : 'T_CORE_PDF_ACQ_CONFIG.CONFIGNAME',
            width : 250
        }, {
            field : 'ext$.filename',
            title : 'T_CORE_PDF_ACQ_CONFIG.FILENAME',
        }, {
            field : 'createdByName',
            title : 'T_CORE_PDF_ACQ_CONFIG.CREATEDBYNAME',
            width : 250
        }, {
            field : 'createdTime',
            title : 'T_CORE_PDF_ACQ_CONFIG.CREATEDTIME',
            width : 250
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'configName',
            preInsert : true,
            title : 'T_CORE_PDF_ACQ_CONFIG.CONFIGNAME',
            validators : [ 'strLength(0,90]' ],
        } ];
    },

    getTestResultColumns : function() {
        return [ {
            field : 'sampleId',
            title : 'T_CORE_PDF_ACQ_RESULT.SAMPLEID',
            width : 250
        }, {
            field : 'itemName',
            title : 'T_CORE_PDF_ACQ_RESULT.ITEMNAME',
            width : 250
        }, {
            field : 'result',
            title : 'T_CORE_PDF_ACQ_RESULT.RESULT',
            width : 250
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/equipt/pdf-acq-configs'

});
