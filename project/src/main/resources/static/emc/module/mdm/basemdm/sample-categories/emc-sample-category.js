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
            field : 'ext$.elntmplname',
            title : 'ELN模板',
            editor : true,
            type : 'choose',
            category : 'eln-tmpl',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        elnTmplId : rows[0].id,
                        ext$ : {
                            elntmplname : rows[0].elnTmplName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    elnTmplId : '',
                    ext$ : {
                        elntmplname : ''
                    }
                });
            },
        },{
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
