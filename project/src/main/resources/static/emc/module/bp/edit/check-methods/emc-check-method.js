var emcCheckMethod = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'status',
            title : 'T_EMC_ORDER_TASK.STATUS',
            width : 80,
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 120,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_FOLDER.PROJNAME',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'ext$.folderno',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 120
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
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
            title : 'T_EMC_ORDER_TASK.SUBPACKAGENO'
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_ORDER_TASK.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
        } ];
    },

});
