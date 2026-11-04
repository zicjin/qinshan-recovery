//@ sourceURL=core-file-acq-config.js
var coreFileAcqConfig = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'configDesc',
            title : 'T_CORE_FILE_ACQ_CONFIG.CONFIGDESC',
            editor : true,
            width : 120,
            validators : [ 'notEmpty', 'strLength[0,40]' ]
        }, {
            field : 'dataFormat',
            title : 'T_CORE_FILE_ACQ_CONFIG.DATAFORMAT',
            editor : true,
            type : 'select',
            width : 120,
            category : 'file-acq-config-data-format',
            validators : [ 'notEmpty' ]
        }, {
            field : 'sampleNoFormat',
            title : 'T_CORE_FILE_ACQ_CONFIG.SAMPLENOFORMAT',
            editor : true,
            type : 'select',
            width : 120,
            category : 'file-acq-config-sampleno-format',
            validators : [ 'notEmpty' ],
        }, {
            field : 'rowSeparator',
            title : 'T_CORE_FILE_ACQ_CONFIG.ROWSEPARATOR',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'paragraphSeparator',
            title : 'T_CORE_FILE_ACQ_CONFIG.PARAGRAPHSEPARATOR',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'itemNameSource',
            title : 'T_CORE_FILE_ACQ_CONFIG.ITEMNAMESOURCE',
            editor : true,
            type : 'select',
            category : 'file-acq-itemname-source',
            width : 140
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'configDesc',
            width : 120,
            title : 'T_CORE_FILE_ACQ_CONFIG.CONFIGDESC',
        }, {
            field : 'dataFormat',
            title : 'T_CORE_FILE_ACQ_CONFIG.DATAFORMAT',
            type : 'select',
            width : 120,
            category : 'file-acq-config-data-format',
        }, {
            field : 'sampleNoFormat',
            title : 'T_CORE_FILE_ACQ_CONFIG.SAMPLENOFORMAT',
            type : 'select',
            width : 120,
            category : 'file-acq-config-sampleno-format',
        }, {
            field : 'rowSeparator',
            width : 120,
            title : 'T_CORE_FILE_ACQ_CONFIG.ROWSEPARATOR'
        }, {
            field : 'paragraphSeparator',
            width : 120,
            title : 'T_CORE_FILE_ACQ_CONFIG.PARAGRAPHSEPARATOR'
        }, {
            field : 'itemNameSource',
            width : 120,
            title : 'T_CORE_FILE_ACQ_CONFIG.ITEMNAMESOURCE',
            type : 'select',
            category : 'file-acq-itemname-source'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'configDesc',
            title : 'T_CORE_FILE_ACQ_CONFIG.CONFIGDESC',
            validators : [ 'notEmpty', 'strLength[0,40]' ],
            preInsert : true
        }, {
            field : 'dataFormat',
            title : 'T_CORE_FILE_ACQ_CONFIG.DATAFORMAT',
            type : 'select',
            category : 'file-acq-config-data-format',
            preInsert : true
        }, {
            field : 'sampleNoFormat',
            title : 'T_CORE_FILE_ACQ_CONFIG.SAMPLENOFORMAT',
            type : 'select',
            category : 'file-acq-config-sampleno-format',
            preInsert : true
        }, {
            field : 'rowSeparator',
            title : 'T_CORE_FILE_ACQ_CONFIG.ROWSEPARATOR',
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'paragraphSeparator',
            title : 'T_CORE_FILE_ACQ_CONFIG.PARAGRAPHSEPARATOR',
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'itemNameSource',
            title : 'T_CORE_FILE_ACQ_CONFIG.ITEMNAMESOURCE',
            validators : [ 'strLength[0,36]' ]
        }, ];
    },

    getFileContentFields : function() {
        return [ {
            field : 'content',
            title : 'T_CORE_FILE_ACQ_CONFIG.CONTENT',
            type : 'ckeditor',
            readonly : true,
        } ]
    },

    getAcqParamListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'field',
            title : 'T_CORE_FILE_ACQ_PARAM.FIELD',
            editor : true,
            width : 120,
            type : 'select',
            validators : [ 'notEmpty' ],
            category : 'file-acq-param-field'
        }, {
            field : 'beginSymbol',
            title : 'T_CORE_FILE_ACQ_PARAM.BEGINSYMBOL',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'endSymbol',
            title : 'T_CORE_FILE_ACQ_PARAM.ENDSYMBOL',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getAcqParamFields : function() {
        return [ {
            field : 'field',
            title : 'T_CORE_FILE_ACQ_PARAM.FIELD',
            preInsert : true,
            type : 'select',
            category : 'file-acq-param-field'
        }, {
            field : 'beginSymbol',
            title : 'T_CORE_FILE_ACQ_PARAM.BEGINSYMBOL',
            editor : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'endSymbol',
            title : 'T_CORE_FILE_ACQ_PARAM.ENDSYMBOL',
            editor : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getAcqPreprocessListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'keyword',
            title : 'T_CORE_FILE_ACQ_PREPROCESS.KEYWORD',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'operation',
            title : 'T_CORE_FILE_ACQ_PREPROCESS.OPERATION',
            editor : true,
            width : 120,
            type : 'select',
            category : 'file-acq-preprocess-operation'
        }, {
            field : 'replaceStr',
            title : 'T_CORE_FILE_ACQ_PREPROCESS.REPLACESTR',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getAcqPreprocessFields : function() {
        return [ {
            field : 'keyword',
            title : 'T_CORE_FILE_ACQ_PREPROCESS.KEYWORD',
            validators : [ 'strLength[0,60]' ],
            preInsert : true,
        }, {
            field : 'operation',
            title : 'T_CORE_FILE_ACQ_PREPROCESS.OPERATION',
            type : 'select',
            category : 'file-acq-preprocess-operation',
            preInsert : true,
        }, {
            field : 'replaceStr',
            title : 'T_CORE_FILE_ACQ_PREPROCESS.REPLACESTR',
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getAcqResultParamListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'itemName',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.ITEMNAME',
            editor : true,
            width : 140,
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'resultColNo',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.RESULTCOLNO',
            editor : true,
            type : 'number',
            width : 120,
            isInteger : true,
            hasArrow : true,
            range : '[0,99)'
        }, {
            field : 'sampleNoColNo',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.SAMPLENOCOLNO',
            editor : true,
            width : 120,
            type : 'number',
            isInteger : true,
            hasArrow : true,
            range : '[0,99)'
        }, {
            field : 'itemColNo',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.ITEMCOLNO',
            editor : true,
            width : 120,
            type : 'number',
            isInteger : true,
            hasArrow : true,
            range : '[0,99)'
        }, {
             field : 'repeatNoColNo',
             title : 'T_CORE_FILE_ACQ_RESULT_PARAM.REPEATNOCOLNO',
             editor : true,
             width : 120,
             type : 'number',
             isInteger : true,
             hasArrow : true,
             range : '[0,99)'
         } ];
    },

    getAcqResultParamFields : function() {
        return [ {
            field : 'itemName',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.ITEMNAME',
            preInsert : true,
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'resultColNo',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.RESULTCOLNO',
            type : 'number',
            isInteger : true,
            hasArrow : true,
            range : '[0,99)'
        }, {
            field : 'sampleNoColNo',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.SAMPLENOCOLNO',
            type : 'number',
            isInteger : true,
            hasArrow : true,
            range : '[0,99)'
        }, {
            field : 'itemColNo',
            title : 'T_CORE_FILE_ACQ_RESULT_PARAM.ITEMCOLNO',
            type : 'number',
            isInteger : true,
            hasArrow : true,
            range : '[0,99)'
        } ];
    },

    getAcqResultListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'sampleNo',
            width : 250,
            title : 'T_CORE_FILE_ACQ_RESULT.SAMPLENO'
        }, {
            field : 'itemName',
            width : 270,
            title : 'T_CORE_FILE_ACQ_RESULT.ITEMNAME'
        }, {
            field : 'acqResult',
            width : 270,
            title : 'T_CORE_FILE_ACQ_RESULT.ACQRESULT'
        }, {
            field : 'repeatNo',
            width : 50,
            title : 'T_CORE_FILE_ACQ_RESULT.REPEATNO'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/equipt/file-acq-configs'
});
