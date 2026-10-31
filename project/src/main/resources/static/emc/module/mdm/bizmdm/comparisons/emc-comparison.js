var emcComparison = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/mdm/bizmdm/comparisons",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_COMPARISON.MONITORPOINTNAME',
        }, {
            field : 'testId',
            title : 'T_EMC_COMPARISON.TESTID',
        }, {
            field : 'testName',
            title : 'T_EMC_COMPARISON.TESTNAME',
        }, {
            field : 'compareEquipmentName',
            title : 'T_EMC_COMPARISON.COMPAREEQUIPMENTNAME',
            editor : true
        }, {
            field : 'compareEquipmentNo',
            title : 'T_EMC_COMPARISON.COMPAREEQUIPMENTNO',
            editor : true
        }, {
            field : 'compareEquipmentModel',
            title : 'T_EMC_COMPARISON.COMPAREEQUIPMENTMODEL',
            editor : true
        }, {
            field : 'monitorMethod',
            title : 'T_EMC_COMPARISON.MONITORMETHOD',
            editor : true
        }, {
            field : 'lowLimit',
            title : 'T_EMC_COMPARISON.LOWLIMIT',
            editor : true
        }, {
            field : 'measureRange',
            title : 'T_EMC_COMPARISON.MEASURERANGE',
            editor : true
        }, {
            field : 'manufacturer',
            title : 'T_EMC_COMPARISON.MANUFACTURER',
            editor : true
        }];
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            field : 'testName',
            title : 'T_EMC_COMPARISON.TESTNAME',
            validators : [ 'strLength[0, 660]' ],
            preInsert : true,
            type : 'choose',
            category : 'test',
            single : false,
            onBeforeChoose : function() {
                return {
                    testName_CISC : '比对'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var testIds = rows.map(function (item) {
                        return item.id;
                    }).join();
                    var testNames = rows.map(function (item) {
                        return item.testName
                    }).join();
                    this.setData({
                        ext$ : {
                            ids : testIds,
                        },
                        testName : testNames,
                        monitorPointId : _this.editPage.param.monitorPointId_SEQ
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        ids : '',
                    },
                    testName : ''
                });
            },
        } ];
    },

    getResultEditListColumns : function () {
        return [ {
            field : 'testName',
            title : 'T_EMC_COMPARISON_RESULT.TESTNAME',
        }, {
            field : 'result',
            title : 'T_EMC_COMPARISON_RESULT.RESULT',
            editor : true
        } ]
    }
})
