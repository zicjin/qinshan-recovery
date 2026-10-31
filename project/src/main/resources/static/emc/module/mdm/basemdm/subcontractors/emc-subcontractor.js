var emcSubcontractor = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/subcontractors',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'subcontractorName',
            title : 'T_EMC_SUBCONTRACTOR.SUBCONTRACTORNAME',
            validators : [ 'strLength[0,40]' ],
            editor : true,
        }, {
            field : 'account',
            title : 'T_EMC_SUBCONTRACTOR.ACCOUNT',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'taxNo',
            title : 'T_EMC_SUBCONTRACTOR.TAXNO',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'bank',
            title : 'T_EMC_SUBCONTRACTOR.BANK',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'address',
            title : 'T_EMC_SUBCONTRACTOR.ADDRESS',
            validators : [ 'strLength[0,40]' ],
            editor : true,
        }, {
            field : 'post',
            title : 'T_EMC_SUBCONTRACTOR.POST',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'phone',
            title : 'T_EMC_SUBCONTRACTOR.PHONE',
            validators : [ 'phone' ],
            editor : true,
        }, {
            field : 'fax',
            title : 'T_EMC_SUBCONTRACTOR.FAX',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'contactor',
            title : 'T_EMC_SUBCONTRACTOR.CONTACTOR',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'remark',
            title : 'T_EMC_SUBCONTRACTOR.REMARK',
            validators : [ 'strLength[0,180]' ],
            editor : true,
        }, {
            field : 'receiver',
            title : 'T_EMC_SUBCONTRACTOR.RECEIVER',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'monitorAbility',
            title : 'T_EMC_SUBCONTRACTOR.MONITORABILITY',
            validators : [ 'strLength[0,20]' ],
            editor : true,
        }, {
            field : 'qualitySystem',
            title : 'T_EMC_SUBCONTRACTOR.QUALITYSYSTEM',
            validators : [ 'strLength[0,40]' ],
            editor : true,
        }, {
            field : 'qualification',
            title : 'T_EMC_SUBCONTRACTOR.QUALIFICATION',
            validators : [ 'strLength[0,40]' ],
            editor : true,
        }, {
            field : 'startDate',
            title : 'T_EMC_SUBCONTRACTOR.STARTDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'validDate',
            title : 'T_EMC_SUBCONTRACTOR.VALIDDATE',
            type : 'date',
            editor : true,
        } ];
    },

    getOrderTaskListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_SUBCONTRACT_RECORD.ORDERNO',
            width : 200,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_SUBCONTRACT_RECORD.TESTNAME'
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_SUBCONTRACT_RECORD.TESTID',
            width : 100,
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_SUBCONTRACT_RECORD.METHODNAME'
        }, {
            field : 'ext$.analysisorgname',
            title : 'T_EMC_SUBCONTRACT_RECORD.ANALYSISORGNAME'
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'subcontractorName',
            title : 'T_EMC_SUBCONTRACTOR.SUBCONTRACTORNAME',
            width : 300,
        }, ];
    },

});
