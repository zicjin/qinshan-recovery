var emcEquiptAcqScript = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-acq-scripts',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'scriptDesc',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.SCRIPTDESC',
            editor : true,
            validators : [ 'notEmpty', 'strLength(0,50]' ],
        }, {
            editor : true,
            field : 'dataFormat',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.DATAFORMAT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptAcqDataFormat',
            }
        }, {
            editor : true,
            field : 'orderNoFormat',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.ORDERNOFORMAT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptAcqOrderNoFormat',
            }
        }, {
            editor : true,
            field : 'testFormat',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.TESTFORMAT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            editor : true,
            field : 'rowDataSeparator',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.ROWDATASEPARATOR',
            validators : [ 'notEmpty', 'strLength(0,6]' ],
        }, {
            editor : true,
            field : 'analysisWay',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.ANALYSISWAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisWay',
            }
        }, {
            editor : true,
            field : 'ext$.diluteacq',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.DILUTEACQ',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            editor : true,
            field : 'ext$.mergedacq',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.MERGEDACQ',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            editor : true,
            field : 'ext$.judgedacq',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.JUDGEDACQ',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            editor : true,
            field : 'source',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.SOURCE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'acqSource',
            }
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'scriptDesc',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.SCRIPTDESC',
        }, {
            field : 'dataFormat',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.DATAFORMAT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptAcqDataFormat',
            }
        }, {
            field : 'rowDataSeparator',
            title : 'T_EMC_EQUIPT_ACQ_SCRIPT.ROWDATASEPARATOR',
        } ]
    },

    getScriptParamListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.fieldname',
            title : 'T_EMC_EQUIPT_ACQ_PARAM.FIELDNAME',
        }, {
            field : 'beginSymbol',
            title : 'T_EMC_EAS_PARAM.BEGINSYMBOL',
            editor : true,
            validators : [ 'notEmpty', 'strLength(0,120]' ],
        }, {
            field : 'endSymbol',
            title : 'T_EMC_EAS_PARAM.ENDSYMBOL',
            editor : true,
            validators : [ 'notEmpty', 'strLength(0,120]' ],
        }, {
            field : 'ext$.field',
            title : 'T_EMC_EQUIPT_ACQ_PARAM.FIELD',
        } ]
    },

    getScriptPreAnalyListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fieldName',
            editor : true,
            title : 'T_EMC_EAS_PREANALY.FIELDNAME',
            validators : [ 'notEmpty', 'strLength(,20]' ],
        }, {
            field : 'fieldSymbol',
            editor : true,
            title : 'T_EMC_EAS_PREANALY.FIELDSYMBOL',
            validators : [ 'notEmpty', 'strLength(,600]' ],
        }, {
            field : 'fieldReplaceSymbol',
            editor : true,
            title : 'T_EMC_EAS_PREANALY.FIELDREPLACESYMBOL',
            validators : [ 'notEmpty', 'strLength(,600]' ],
        }, {
            field : 'preAnalyCategory',
            editor : true,
            title : 'T_EMC_EAS_PREANALY.PREANALYCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptAcqPreAnalyCategory',
            }
        }, {
            field : 'sortNum',
            title : 'T_EMC_EAS_PREANALY.SORTNUM',
            type : "number",
            editor : true,
            hasArrow : true,
        } ]
    },

    getScriptResultListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fieldName',
            editor : true,
            title : 'T_EMC_EAS_RESULT.FIELDNAME',
            validators : [ 'unique[' + IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-acq-scripts/results/action/validate-unique]', 'notEmpty', 'strLength(,60]' ],
        }, {
            field : 'testItemColumnNo',
            editor : true,
            title : 'T_EMC_EAS_RESULT.TESTITEMCOLUMNNO',
            validators : [ 'notEmpty', 'integer' ],
        }, {
            field : 'resultColumnNo',
            editor : true,
            title : 'T_EMC_EAS_RESULT.RESULTCOLUMNNO',
            validators : [ 'notEmpty', 'integer' ],
        }, {
            field : 'shortNoColumnNo',
            editor : true,
            title : 'T_EMC_EAS_RESULT.SHORTNOCOLUMNNO',
            validators : [ 'integer' ],
        }, {
            field : 'dynamicCreating',
            editor : true,
            title : 'T_EMC_EAS_RESULT.DYNAMICCREATING',
            validators : [ 'strLength(,60]' ],
        } ]
    },

    getEasResultChooseListColumns : function() {
        return [ {
            field : 'fieldName',
            title : 'T_EMC_EAS_RESULT.FIELDNAME',
            validators : [ 'unique[' + IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-acq-scripts/results/action/validate-unique]', 'notEmpty', 'strLength(,60]' ],
        }, {
            field : 'testItemColumnNo',
            title : 'T_EMC_EAS_RESULT.TESTITEMCOLUMNNO',
            validators : [ 'notEmpty', 'integer' ],
        }, {
            field : 'resultColumnNo',
            title : 'T_EMC_EAS_RESULT.RESULTCOLUMNNO',
            validators : [ 'notEmpty', 'integer' ],
        }, {
            field : 'shortNoColumnNo',
            title : 'T_EMC_EAS_RESULT.SHORTNOCOLUMNNO',
            validators : [ 'integer' ],
        }, {
            field : 'dynamicCreating',
            title : 'T_EMC_EAS_RESULT.DYNAMICCREATING',
            validators : [ 'strLength(,60]' ],
        } ]
    },

    getTestFields : function() {
        return [ {
            field : 'content',
            title : 'T_EMC_EAS_RESULT.CONTENT',
            type : 'ckeditor',
            readonly : true,
        } ]
    },

    getAnalyResultColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'rowNo',
            title : 'T_EMC_EQUIPT_ACQ_RESULT.ROWNO',
        }, {
            field : 'bizNo',
            title : 'T_EMC_EQUIPT_ACQ_RESULT.BIZNO',
        }, {
            field : 'fieldName',
            title : 'T_EMC_EQUIPT_ACQ_RESULT.FIELDNAME',
        }, {
            field : 'itemName',
            title : 'T_EMC_EQUIPT_ACQ_RESULT.ITEMNAME',
        }, {
            field : 'result',
            title : 'T_EMC_EQUIPT_ACQ_RESULT.RESULT',
        } ]
    }
})
