var coreNfssiOrder = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'id',
            title : 'T_CORE_NFSSI_ORDER.ID',
            width : 160
        }, {
            field : 'sampleType',
            title : 'T_CORE_NFSSI_ORDER.SAMPLETYPE',
            type : 'select',
            category : 'nfssi-sample-category',
            width : 100
        }, {
            field : 'inspectOrgName',
            title : 'T_CORE_NFSSI_ORDER.INSPECTORGNAME'
        }, {
            field : 'testState',
            title : 'T_CORE_NFSSI_ORDER.TESTSTATE',
            type : 'select',
            category : 'nfssi-order-state'
        }, {
            field : 'nffsiUpdatedTime',
            title : 'T_CORE_NFSSI_ORDER.NFFSIUPDATEDTIME'
        }, {
            field : 'inspectionDate',
            title : 'T_CORE_NFSSI_ORDER.INSPECTIONDATE'
        }, {
            field : 'createdTime',
            title : 'T_CORE_NFSSI_ORDER.CREATEDTIME'
        }, {
            field : 'lastUpdatedTime',
            title : 'T_CORE_NFSSI_ORDER.LASTUPDATEDTIME'
        } ];
    },

    getOrderInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_NFSSI_ORDER.ID',
            validators : [ 'strLength[0,20]' ],
            readonly : true
        }, {
            field : 'createdTime',
            title : 'T_CORE_NFSSI_ORDER.CREATEDTIME',
            readonly : true
        }, {
            field : 'sampleType',
            title : 'T_CORE_NFSSI_ORDER.SAMPLETYPE',
            validators : [ 'strLength[0,20]' ],
            type : 'select',
            category : 'nfssi-sample-category',
            readonly : true
        }, {
            field : 'nffsiUpdatedTime',
            title : 'T_CORE_NFSSI_ORDER.NFFSIUPDATEDTIME',
            readonly : true
        }, {
            field : 'testState',
            title : 'T_CORE_NFSSI_ORDER.TESTSTATE',
            type : 'select',
            category : 'nfssi-order-state',
            readonly : true
        }, {
            field : 'back',
            title : 'T_CORE_NFSSI_ORDER.BACK',
            type : 'select',
            category : 'confirmFlag',
            readonly : true
        }, {
            field : 'inspectOrgName',
            title : 'T_CORE_NFSSI_ORDER.INSPECTORGNAME',
            validators : [ 'strLength[0,85]' ],
            readonly : true
        }, {
            field : 'samplingPlaceOrgName',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGPLACEORGNAME',
            readonly : true
        }, {
            field : 'samplingPlace',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGPLACE',
            readonly : true
        }, {
            field : 'province',
            title : 'T_CORE_NFSSI_ORDER.PROVINCE',
            readonly : true
        }, {
            field : 'city',
            title : 'T_CORE_NFSSI_ORDER.CITY',
            readonly : true
        }, {
            field : 'county',
            title : 'T_CORE_NFSSI_ORDER.COUNTY',
            readonly : true
        }, {
            field : 'samplingUnit',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGUNIT',
            readonly : true
        }, {
            field : 'address',
            title : 'T_CORE_NFSSI_ORDER.ADDRESS',
            readonly : true
        }, {
            field : 'legalPerson',
            title : 'T_CORE_NFSSI_ORDER.LEGALPERSON',
            readonly : true
        }, {
            field : 'postcode',
            title : 'T_CORE_NFSSI_ORDER.POSTCODE',
            readonly : true
        }, {
            field : 'contact',
            title : 'T_CORE_NFSSI_ORDER.CONTACT',
            readonly : true
        }, {
            field : 'contactMobile',
            title : 'T_CORE_NFSSI_ORDER.CONTACTMOBILE',
            readonly : true
        }, {
            field : 'productionLicenseNo',
            title : 'T_CORE_NFSSI_ORDER.PRODUCTIONLICENSENO',
            readonly : true
        }, {
            field : 'sampleName',
            title : 'T_CORE_NFSSI_ORDER.SAMPLENAME',
            readonly : true
        }, {
            field : 'samplingQty',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGQTY',
            readonly : true
        }, {
            field : 'firstCategory',
            title : 'T_CORE_NFSSI_ORDER.FIRSTCATEGORY',
            readonly : true
        }, {
            field : 'secondCategory',
            title : 'T_CORE_NFSSI_ORDER.SECONDCATEGORY',
            readonly : true
        }, {
            field : 'thirdCategory',
            title : 'T_CORE_NFSSI_ORDER.THIRDCATEGORY',
            readonly : true
        }, {
            field : 'fourthCategory',
            title : 'T_CORE_NFSSI_ORDER.FOURTHCATEGORY',
            readonly : true
        }, {
            field : 'sampleSource',
            title : 'T_CORE_NFSSI_ORDER.SAMPLESOURCE',
            readonly : true
        }, {
            field : 'annualSales',
            title : 'T_CORE_NFSSI_ORDER.ANNUALSALES',
            readonly : true
        }, {
            field : 'samplingMethod',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGMETHOD',
            readonly : true
        }, {
            field : 'samplingInstrument',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGINSTRUMENT',
            readonly : true
        }, {
            field : 'specification',
            title : 'T_CORE_NFSSI_ORDER.SPECIFICATION',
            readonly : true
        }, {
            field : 'sampleBatchNo',
            title : 'T_CORE_NFSSI_ORDER.SAMPLEBATCHNO',
            readonly : true
        }, {
            field : 'productDate',
            title : 'T_CORE_NFSSI_ORDER.PRODUCTDATE',
            readonly : true
        }, {
            field : 'qualityGuaranteePeriod',
            title : 'T_CORE_NFSSI_ORDER.QUALITYGUARANTEEPERIOD',
            readonly : true
        }, {
            field : 'samplingKeepCondition',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGKEEPCONDITION',
            readonly : true
        }, {
            field : 'samplePack',
            title : 'T_CORE_NFSSI_ORDER.SAMPLEPACK',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_CORE_NFSSI_ORDER.REMARK',
            colspan : 2,
            readonly : true
        }, {
            field : 'samplingOrgName',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGNAME',
            readonly : true
        }, {
            field : 'samplingDate',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGDATE',
            readonly : true
        }, {
            field : 'samplingMobile',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGMOBILE',
            readonly : true
        }, {
            field : 'samplingOrgContact',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGCONTACT',
            readonly : true
        }, {
            field : 'testPurpose',
            title : 'T_CORE_NFSSI_ORDER.TESTPURPOSE',
            readonly : true
        }, {
            field : 'sampleShap',
            title : 'T_CORE_NFSSI_ORDER.SAMPLESHAP',
            readonly : true
        }, {
            field : 'sampleCategory',
            title : 'T_CORE_NFSSI_ORDER.SAMPLECATEGORY',
            readonly : true
        }, {
            field : 'packageCategory',
            title : 'T_CORE_NFSSI_ORDER.PACKAGECATEGORY',
            readonly : true
        }, {
            field : 'manufacturer',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURER',
            readonly : true
        }, {
            field : 'manufacturerAdress',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURERADRESS',
            readonly : true
        }, {
            field : 'samplingTache',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGTACHE',
            readonly : true
        }, {
            field : 'executiveStandard',
            title : 'T_CORE_NFSSI_ORDER.EXECUTIVESTANDARD',
            readonly : true
        }, {
            field : 'qualityLevel',
            title : 'T_CORE_NFSSI_ORDER.QUALITYLEVEL',
            readonly : true
        }, {
            field : 'sampleLogo',
            title : 'T_CORE_NFSSI_ORDER.SAMPLELOGO',
            readonly : true
        }, {
            field : 'manufacturerContact',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURERCONTACT',
            readonly : true
        }, {
            field : 'manufacturerMobile',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURERMOBILE',
            readonly : true
        }, {
            field : 'receivedDate',
            title : 'T_CORE_NFSSI_ORDER.RECEIVEDDATE',
            readonly : true
        }, {
            field : 'areaCategory',
            title : 'T_CORE_NFSSI_ORDER.AREACATEGORY',
            readonly : true
        }, {
            field : 'manufacturerProvince',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURERPROVINCE',
            readonly : true
        }, {
            field : 'sampleProperty',
            title : 'T_CORE_NFSSI_ORDER.SAMPLEPROPERTY',
            readonly : true
        }, {
            field : 'price',
            title : 'T_CORE_NFSSI_ORDER.PRICE',
            readonly : true
        }, {
            field : 'samplingBaseNum',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGBASENUM',
            readonly : true
        }, {
            field : 'spareNum',
            title : 'T_CORE_NFSSI_ORDER.SPARENUM',
            readonly : true
        }, {
            field : 'samplingOrgAdress',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGADRESS',
            colspan : 2,
            readonly : true
        }, {
            field : 'samplingOrgPostCode',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGPOSTCODE',
            readonly : true
        }, {
            field : 'samplingOrgFax',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGFAX',
            readonly : true
        }, {
            field : 'testPlaceCreditCode',
            title : 'T_CORE_NFSSI_ORDER.TESTPLACECREDITCODE',
            readonly : true
        }, {
            field : 'manufacturerCity',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURERCITY',
            readonly : true
        }, {
            field : 'manufacturerCounty',
            title : 'T_CORE_NFSSI_ORDER.MANUFACTURERCOUNTY',
            readonly : true
        }, {
            field : 'sampleBarCode',
            title : 'T_CORE_NFSSI_ORDER.SAMPLEBARCODE',
            readonly : true
        }, {
            field : 'taskSource',
            title : 'T_CORE_NFSSI_ORDER.TASKSOURCE',
            readonly : true
        }, {
            field : 'testPlaceFax',
            title : 'T_CORE_NFSSI_ORDER.TESTPLACEFAX',
            readonly : true
        }, {
            field : 'entrustContact',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTCONTACT',
            readonly : true
        }, {
            field : 'entrustProductionLicenseNo',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTPRODUCTIONLICENSENO',
            readonly : true
        }, {
            field : 'entrustEnterpriseAdress',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTENTERPRISEADRESS',
            readonly : true
        }, {
            field : 'entrustEnterprise',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTENTERPRISE',
            readonly : true
        }, {
            field : 'importation',
            title : 'T_CORE_NFSSI_ORDER.IMPORTATION',
            readonly : true
        }, {
            field : 'hasEntrustEnterpriseInfo',
            title : 'T_CORE_NFSSI_ORDER.HASENTRUSTENTERPRISEINFO',
            readonly : true
        }, {
            field : 'entrustPhone',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTPHONE',
            readonly : true
        }, {
            field : 'entrustProvince',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTPROVINCE',
            readonly : true
        }, {
            field : 'entrustCity',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTCITY',
            readonly : true
        }, {
            field : 'entrustCountry',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTCOUNTRY',
            readonly : true
        }, {
            field : 'originPlace',
            title : 'T_CORE_NFSSI_ORDER.ORIGINPLACE',
            readonly : true
        }, {
            field : 'licenseType',
            title : 'T_CORE_NFSSI_ORDER.LICENSETYPE',
            readonly : true
        }, {
            field : 'licenseNo',
            title : 'T_CORE_NFSSI_ORDER.LICENSENO',
            readonly : true
        }, {
            field : 'barCode',
            title : 'T_CORE_NFSSI_ORDER.BARCODE',
            readonly : true
        }, {
            field : 'rainbowCode',
            title : 'T_CORE_NFSSI_ORDER.RAINBOWCODE',
            readonly : true
        }, {
            field : 'rainbowCodePath',
            title : 'T_CORE_NFSSI_ORDER.RAINBOWCODEPATH',
            colspan : 2,
            readonly : true
        }, {
            field : 'approvalNo',
            title : 'T_CORE_NFSSI_ORDER.APPROVALNO',
            readonly : true
        }, {
            field : 'funcCategoryCollection',
            title : 'T_CORE_NFSSI_ORDER.FUNCCATEGORYCOLLECTION',
            readonly : true
        }, {
            field : 'inspectOrgName',
            title : 'T_CORE_NFSSI_ORDER.INSPECTORGNAME',
            readonly : true
        }, {
            field : 'inspectionDate',
            title : 'T_CORE_NFSSI_ORDER.INSPECTIONDATE',
            readonly : true
        }, {
            field : 'inspectionOrgContact',
            title : 'T_CORE_NFSSI_ORDER.INSPECTIONORGCONTACT',
            readonly : true
        }, {
            field : 'inspectOrgContactMobile',
            title : 'T_CORE_NFSSI_ORDER.INSPECTORGCONTACTMOBILE',
            readonly : true
        }, {
            field : 'inspectOrgContactEmail',
            title : 'T_CORE_NFSSI_ORDER.INSPECTORGCONTACTEMAIL',
            readonly : true
        }, {
            field : 'inspectSealPerson',
            title : 'T_CORE_NFSSI_ORDER.INSPECTSEALPERSON',
            readonly : true
        }, {
            field : 'inspectSealPersonMobile',
            title : 'T_CORE_NFSSI_ORDER.INSPECTSEALPERSONMOBILE',
            readonly : true
        }, {
            field : 'inspectSealPersonEmail',
            title : 'T_CORE_NFSSI_ORDER.INSPECTSEALPERSONEMAIL',
            readonly : true
        }, {
            field : 'sampler',
            title : 'T_CORE_NFSSI_ORDER.SAMPLER',
            readonly : true
        }, {
            field : 'samplerMobile',
            title : 'T_CORE_NFSSI_ORDER.SAMPLERMOBILE',
            readonly : true
        }, {
            field : 'samplerEmail',
            title : 'T_CORE_NFSSI_ORDER.SAMPLEREMAIL',
            readonly : true
        }, {
            field : 'samplingOrgLevel',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGLEVEL',
            readonly : true
        }, {
            field : 'samplingOrgProvince',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGORGPROVINCE',
            readonly : true
        }, {
            field : 'sampleUrl',
            title : 'T_CORE_NFSSI_ORDER.SAMPLEURL',
            readonly : true
        }, {
            field : 'samplingPicPath',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGPICPATH',
            readonly : true
        }, {
            field : 'networkLicenseNo',
            title : 'T_CORE_NFSSI_ORDER.NETWORKLICENSENO',
            readonly : true
        }, {
            field : 'icpNo',
            title : 'T_CORE_NFSSI_ORDER.ICPNO',
            readonly : true
        }, {
            field : 'networkPlatformProvince',
            title : 'T_CORE_NFSSI_ORDER.NETWORKPLATFORMPROVINCE',
            readonly : true
        }, {
            field : 'networkPlatformCity',
            title : 'T_CORE_NFSSI_ORDER.NETWORKPLATFORMCITY',
            readonly : true
        }, {
            field : 'networkPlatformCounty',
            title : 'T_CORE_NFSSI_ORDER.NETWORKPLATFORMCOUNTY',
            readonly : true
        }, {
            field : 'networkPlatformAdress',
            title : 'T_CORE_NFSSI_ORDER.NETWORKPLATFORMADRESS',
            readonly : true
        }, {
            field : 'notificationPicPath',
            title : 'T_CORE_NFSSI_ORDER.NOTIFICATIONPICPATH',
            readonly : true
        }, {
            field : 'networkPlatformContact',
            title : 'T_CORE_NFSSI_ORDER.NETWORKPLATFORMCONTACT',
            readonly : true
        }, {
            field : 'networkPlatformContactMobile',
            title : 'T_CORE_NFSSI_ORDER.NETWORKPLATFORMCONTACTMOBILE',
            readonly : true
        }, {
            field : 'networkOrderNo',
            title : 'T_CORE_NFSSI_ORDER.NETWORKORDERNO',
            readonly : true
        }, {
            field : 'submit',
            title : 'T_CORE_NFSSI_ORDER.SUBMIT',
            readonly : true
        }, {
            field : 'onlineShopName',
            title : 'T_CORE_NFSSI_ORDER.ONLINESHOPNAME',
            readonly : true
        }, {
            field : 'dateCategory',
            title : 'T_CORE_NFSSI_ORDER.DATECATEGORY',
            readonly : true
        }, {
            field : 'samplingCategory',
            title : 'T_CORE_NFSSI_ORDER.SAMPLINGCATEGORY',
            readonly : true
        }, {
            field : 'entrustProperty',
            title : 'T_CORE_NFSSI_ORDER.ENTRUSTPROPERTY',
            readonly : true
        }, {
            field : 'reportTaskB',
            title : 'T_CORE_NFSSI_ORDER.REPORTTASKB',
            readonly : true
        }, {
            field : 'reportTaskA',
            title : 'T_CORE_NFSSI_ORDER.REPORTTASKA',
            readonly : true
        } ];
    },

    getReportListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'state',
            title : 'T_CORE_NFSSI_REPORT.STATE',
            type : 'select',
            category : 'success'
        }, {
            field : 'id',
            title : 'T_CORE_NFSSI_REPORT.ID',
            type : 'link'
        }, {
            field : 'orderId',
            title : 'T_CORE_NFSSI_REPORT.ORDERID',
            width : 160
        }, {
            field : 'inspectOrgContact',
            title : 'T_CORE_NFSSI_REPORT.INSPECTORGCONTACT'
        }, {
            field : 'inspectOrgContactMobile',
            title : 'T_CORE_NFSSI_REPORT.INSPECTORGCONTACTMOBILE'
        }, {
            field : 'inspectOrgContactEmail',
            title : 'T_CORE_NFSSI_REPORT.INSPECTORGCONTACTEMAIL'
        }, {
            field : 'sealSampler',
            title : 'T_CORE_NFSSI_REPORT.SEALSAMPLER'
        }, {
            field : 'sealSamplerEmail',
            title : 'T_CORE_NFSSI_REPORT.SEALSAMPLEREMAIL'
        }, {
            field : 'sealSamplerMobile',
            title : 'T_CORE_NFSSI_REPORT.SEALSAMPLERMOBILE'
        }, {
            field : 'superviseReportRemark',
            title : 'T_CORE_NFSSI_REPORT.SUPERVISEREPORTREMARK'
        }, {
            field : 'riskReportRemark',
            title : 'T_CORE_NFSSI_REPORT.RISKREPORTREMARK'
        }, {
            field : 'errorInfo',
            title : 'T_CORE_NFSSI_REPORT.ERRORINFO'
        }, {
            field : 'createdTime',
            title : 'T_CORE_NFSSI_REPORT.CREATEDTIME'
        } ];
    },

    getOrderTestItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'itemName',
            title : 'T_CORE_NFSSI_ORDER_TEST_ITEM.ITEMNAME'
        }, {
            field : 'itemType',
            title : 'T_CORE_NFSSI_ORDER_TEST_ITEM.ITEMTYPE'
        }, {
            field : 'funcCategory',
            title : 'T_CORE_NFSSI_ORDER_TEST_ITEM.FUNCCATEGORY'
        } ];
    },

    getVerifyReasonListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'stdMinLimitUnit',
            title : 'T_CORE_NFSSI_VERIFY_REASON.STDMINLIMITUNIT',
            width : 160
        }, {
            field : 'stdMinLimit',
            title : 'T_CORE_NFSSI_VERIFY_REASON.STDMINLIMIT',
            width : 140
        }, {
            field : 'maxLimit',
            title : 'T_CORE_NFSSI_VERIFY_REASON.MAXLIMIT'
        }, {
            field : 'stdMaxLimit',
            title : 'T_CORE_NFSSI_VERIFY_REASON.STDMAXLIMIT',
            width : 140
        }, {
            field : 'stdMaxLimitUnit',
            title : 'T_CORE_NFSSI_VERIFY_REASON.STDMAXLIMITUNIT',
            width : 160
        }, {
            field : 'judgeStandard',
            title : 'T_CORE_NFSSI_VERIFY_REASON.JUDGESTANDARD',
            type : 'richText'
        }, {
            field : 'minLimitUnit',
            title : 'T_CORE_NFSSI_VERIFY_REASON.MINLIMITUNIT'
        } ];
    },

    getTestReasonListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'remark',
            title : 'T_CORE_NFSSI_TEST_REASON.REMARK'
        }, {
            field : 'inspectStandard',
            title : 'T_CORE_NFSSI_TEST_REASON.INSPECTSTANDARD',
            type : 'richText'
        }, {
            field : 'resultUnit',
            title : 'T_CORE_NFSSI_TEST_REASON.RESULTUNIT'
        }, {
            field : 'shortInspectStandard',
            title : 'T_CORE_NFSSI_TEST_REASON.SHORTINSPECTSTANDARD'
        }, {
            field : 'stdCheckLimitUnit',
            title : 'T_CORE_NFSSI_TEST_REASON.STDCHECKLIMITUNIT',
            width : 140
        }, {
            field : 'stdCheckLimit',
            title : 'T_CORE_NFSSI_TEST_REASON.STDCHECKLIMIT'
        }, {
            field : 'reportTaskA',
            title : 'T_CORE_NFSSI_TEST_REASON.REPORTTASKA'
        }, {
            field : 'reportTaskB',
            title : 'T_CORE_NFSSI_TEST_REASON.REPORTTASKB'
        }, {
            field : 'necessary',
            title : 'T_CORE_NFSSI_TEST_REASON.NECESSARY'
        } ];
    },

    getAddNfssiOrderFormFields : function() {
        return [ {
            field : 'receivedStartTime',
            title : 'CORE.MODULE.NFSSI.RECEIVE_START_TIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'receivedEndTime',
            title : 'CORE.MODULE.NFSSI.RECEIVE_END_TIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'sampleType',
            title : 'T_CORE_NFSSI_ORDER.SAMPLETYPE',
            type : 'select',
            category : 'nfssi-sample-category',
            validators : [ 'notEmpty' ]
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/nfssi/orders'
});
