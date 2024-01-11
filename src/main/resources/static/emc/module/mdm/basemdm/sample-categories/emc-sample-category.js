var emcSampleCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORYGROUP'
        }, {
            field : 'monitorTmplName',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORY'
        }, {
            field : 'ext$.ordercategorycode',
            title : 'T_EMC_MONITOR_TMPL.ORDERCATEGORYCODE',
            editor : true,
            validators : [ 'strLength[0,6]' ],
        }, {
            field : 'monitorBasis',
            title : 'T_EMC_MONITOR_TMPL.MONITORBASIS',
            editor : true,
        }, {
            field : 'reportConfigLineId',
            title : 'T_EMC_MONITOR_TMPL.REPORTCONFIGLINE',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            editor : true,
            param : {
                reportConfigId : 'samplingReport'
            }
        }, {
            field : 'receiveReportLineId',
            title : 'T_EMC_MONITOR_TMPL.RECEIVEREPORTLINEID',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingNumber'
            },
            editor : true,
        }, {
            field : 'monitorInfo',
            title : 'T_EMC_MONITOR_TMPL.MONITORINFO',
            editor : true,
        }, {
            field : 'propertyTmplId',
            title : 'T_EMC_MONITOR_TMPL.PROPERTYTMPLID',
            editor : true,
            type : 'select',
            search : true,
            category : 'propertyTmpl',
            param : {
                templateUse : 'sampleProperty',
            }
        }, {
            field : 'ext$.pointpropertytmplid',
            title : 'T_EMC_MONITOR_TMPL.POINTPROPERTYTMPLID',
            editor : true,
            type : 'select',
            search : true,
            category : 'propertyTmpl',
            param : {
                templateUse : 'monitorPointCategory',
            }
        } ];
    },

    getChooseableColumns : function() {
        return [ {
            field : 'monitorTmplName',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORY'
        }, {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORYGROUP'
        } ];
    },

});
