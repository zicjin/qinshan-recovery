//@ sourceURL=core-data-center.js
var coreDataCenter = Gikam.getPageObject({

    getRoleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_ROLE.ROLENAME',
            field : 'ext$.rolename',
            width : 250
        } ]
    },

    getReportChooseListColumns : function() {
        return [ {
            title : 'T_CORE_DATA_CENTER.REPORTNAME',
            field : 'nodeName',
            type : 'tree',
            width : 300
        } ]
    },

    getDataCenterFormFields : function() {
        return [ {
            title: 'T_CORE_DATA_CENTER.REPORTNAME',
            field: 'nodeName',
            width : 250,
            type : 'select',
            search : true,
            firstBlank : false,
            category : 'data-center-report',
            onChange: function(field, value, rowIndex, oldValue, text){
                if (Gikam.isEmpty(value)) {
                    return;
                }

                Gikam.getComp('core-data-center-search-list-preview-iframe').refresh({
                    src : value
                });
            }
        } ]
    },

    getParamListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'parameterName',
            title : 'T_CORE_DATA_CENTER_PARAM.PARAMETERNAME',
            width : 340
        }, {
            field : 'parameterValue',
            title : 'T_CORE_DATA_CENTER_PARAM.PARAMETERVALUE',
            editor : true,
            width : 340
        }, {
            field : 'defaultValue',
            title : 'T_CORE_DATA_CENTER_PARAM.DEFAULTVALUE',
            editor : true
        } ];
    },

    getParamFormFields : function() {
        return [ {
            field : 'parameterName',
            title : 'T_CORE_DATA_CENTER_PARAM.PARAMETERNAME',
            validators : [ 'strLength[0,120]', 'notEmpty' ],
            preInsert : true,
        }, {
            field : 'parameterValue',
            title : 'T_CORE_DATA_CENTER_PARAM.PARAMETERVALUE',
            validators : [ 'strLength[0,120]' ],
            preInsert : true,
        }, {
            field : 'defaultValue',
            title : 'T_CORE_DATA_CENTER_PARAM.DEFAULTVALUE',
            validators : [ 'strLength[0,120]' ],
            preInsert : true,
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/data-centers'
});
