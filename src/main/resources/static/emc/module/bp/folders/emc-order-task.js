var emcOrderTask = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks',

    getChooseListColumns : function() {
        return [ {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_RUN.PROJNO',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
        }, {
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
        }, {
            field : 'ext$.samplebegintime',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
        }, {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            editor : true,
            validators : [ 'strLength[0, 32]' ]
        }, {
            field : 'ext$.parentorderno',
            title : 'T_EMC_ORDER_TASK.PARENTORDERNO',
        }, {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        } ];
    },

    getAtlasResultColumns : function() {
        return [ {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, ];
    },

    getAtlasColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'name',
            title : 'T_CORE_FILE.NAME',
            type : 'link'
        }, {
            width: 100,
            title: '操作',
            formatter: function(i, v, r) {
                let str = '<div style= "display: flex; justify-content: space-between ">';
                str += `<a href="javascript:;" onclick="Gikam.download('${Gikam.IFM_CONTEXT}${r.downloadUrl}','${r.name}')" key = "download" style="flex: 1;text-align: center;">下载</a>`;
                str += '</div>';
                return str;
            }
        } ];
    },

});
