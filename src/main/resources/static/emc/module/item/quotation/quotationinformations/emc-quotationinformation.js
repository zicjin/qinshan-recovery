var emcQuotationinformation = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'type',
            title : 'T_EMC_QUOTATION_INFORMATION.TYPE'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_QUOTATION_INFORMATION.MONITORPOINTNAME'
        }, {
            field : 'testName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTNAME'
        }, {
            field : 'testMethodName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMETHODNAME'
        }, {
            field : 'testSamplePrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTSAMPLEPRICE'
        }, {
            field : 'testAnalysisPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTANALYSISPRICE'
        }, {
            field : 'testDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTDISCOUNT'
        }, {
            field : 'testMonitorNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMONITORNUM'
        }, {
            field : 'testDayNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTDAYNUM'
        }, {
            field : 'testFrequency',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTFREQUENCY'
        }, {
            field : 'addPriceName',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDPRICENAME'
        }, {
            field : 'addUnitPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDUNITPRICE'
        }, {
            field : 'addNumber',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDNUMBER'
        }, {
            field : 'addPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDPRICE'
        }, {
            field : 'totalAnalysisPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALANALYSISPRICE'
        }, {
            field : 'totalSamplePrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALSAMPLEPRICE'
        }, {
            field : 'totalAddPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALADDPRICE'
        }, {
            field : 'totalPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALPRICE'
        }, {
            field : 'totalDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALDISCOUNT'
        }, {
            field : 'totalTaxAmount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALTAXAMOUNT'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_QUOTATION_INFORMATION.ID',
            preInsert : true
        }, {
            field : 'type',
            title : 'T_EMC_QUOTATION_INFORMATION.TYPE',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_QUOTATION_INFORMATION.MONITORPOINTNAME',
            validators : [ 'strLength[0,50]' ]
        }, {
            field : 'testName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTNAME',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'testMethodName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMETHODNAME',
            validators : [ 'strLength[0,100]' ]
        }, {
            field : 'testSamplePrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTSAMPLEPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'testAnalysisPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTANALYSISPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'testDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTDISCOUNT',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'testMonitorNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMONITORNUM',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'testDayNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTDAYNUM',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'testFrequency',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTFREQUENCY',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'addPriceName',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDPRICENAME',
            validators : [ 'strLength[0,30]' ]
        }, {
            field : 'addUnitPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDUNITPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'addNumber',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDNUMBER',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'addPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'totalAnalysisPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALANALYSISPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'totalSamplePrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALSAMPLEPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'totalAddPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALADDPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'totalPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'totalDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALDISCOUNT',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'totalTaxAmount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALTAXAMOUNT',
            validators : [ 'strLength[0,11]' ]
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quotation/quotationinformations'
});
