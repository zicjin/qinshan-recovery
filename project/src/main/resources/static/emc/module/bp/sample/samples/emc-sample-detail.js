var emcSampleDetail = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',

    getSampleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 110
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO',
            width : 90
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        } ];
    },

    getOrderListColumns : function () {
        return [ {
            index : true
        }, {
            field : 'ext$.folderno',
            title : 'T_EMC_FOLDER.FOLDERNO',
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
        } ];
    }

});
