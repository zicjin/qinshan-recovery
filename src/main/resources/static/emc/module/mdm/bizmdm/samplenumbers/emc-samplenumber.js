var emcSamplenumber = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_SAMPLENUMBER.ORGID',
            editor : false
        }, {
            field : 'numbercode',
            title : 'T_EMC_SAMPLENUMBER.NUMBERCODE',
            editor : true
        }, {
            field : 'effectiveDate',
            title : 'T_EMC_SAMPLENUMBER.EFFECTIVEDATE',
            editor : true
        } ];
    },
    
    getReportListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'reportName',
            title : 'T_EMC_SAMPLENUMBER.REPORTCONFIGLINEID',
            editor : false
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'orgName',
            title : 'T_EMC_SAMPLENUMBER.ORGID',
            preInsert : true,
            category : 'org-simple',
            type : 'choose',
            single : true,
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'company'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
                        orgId : rows[0].id,                        
                        orgName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
            	this.setData({
                    orgId : '',
                    orgName : ''
                });
            },
        }, {
            field : 'reportName',
            title : 'T_EMC_SAMPLENUMBER.REPORTCONFIGLINEID',
            //preInsert : true,
            category : 'report-config',
            type : 'choose',
            single : true,
            onBeforeChoose : function(rows) {
                return {
                	REPORTCONFIGID_SEQ : 'samplingReport'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
                		reportconfiglineid : rows[0].id,                        
                		reportName : rows[0].reportName,
                    });
                }
            },
            onClean : function() {
            	this.setData({
            		reportconfiglineid : '',
            		reportName : ''
                });
            },
        },{
            field : 'numbercode',
            title : 'T_EMC_SAMPLENUMBER.NUMBERCODE',
            preInsert : true
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/samplenumbers'
});
