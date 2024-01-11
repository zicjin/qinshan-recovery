var emcCheckProject = Gikam.getPageObject({

    getFolderListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO'
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_FOLDER.PROJNAME'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_FOLDER.TESTORGNAME'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 120,
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE'
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE'
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE'
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK'
        }, ];
    },

    getFolderOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO',
            width : 120,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            editor : true
        }, ];
    },

    getOrderTaskColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'status',
            title : 'T_EMC_ORDER_TASK.STATUS',
            width : 100,
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP'
        }, {
            field : 'analysisOrgName',
            title : 'T_EMC_ORDER_TASK.ORG',
            width : 200,
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME'
        }, {
            field : 'schemeName',
            title : 'T_EMC_ORDER_TASK.SCHEMENAME'
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_ORDER_TASK.SUBPACKAGENO',
            width : 100,
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_ORDER_TASK.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
        } ];
    },

});
