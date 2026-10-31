var emcEquiptCurve = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptCurveNo',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTCURVENO'
        }, {
            field : 'slope',
            title : 'T_EMC_EQUIPT_CURVE.SLOPE',
            editor : true
        }, {
            field : 'intercept',
            title : 'T_EMC_EQUIPT_CURVE.INTERCEPT',
            editor : true
        }, {
            field : 'coefficient',
            title : 'T_EMC_EQUIPT_CURVE.COEFFICIENT',
            editor : true
        }/*,{
            field : 'testName',
            title : 'T_EMC_EQUIPT_CURVE.TESTNAME',
            preInsert : true,
            editor : true,
            type : 'choose',
            // single : true,
            category : 'test',
            onBeforeChoose : function() {
                var testIds =  Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().testId;
                return {
                    id_IN : testIds
                }
            },
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var testIds = '';
                    var testNames = '';
                    rows.map(function(item, i) {
                        testIds += rows[i].id + ',';
                        testNames += rows[i].testName + ',';
                    });
                    this.setData({
                        index : index,
                        testNo : testIds.substring(0, testIds.length - 1),
                        testName : testNames.substring(0, testNames.length - 1)
                    });
                }
            },
            onClean : function(r,v,i) {
                this.setData({
                    index : i,
                    testNo : '',
                    testName : '',
                });
            },
        }*/,{
            field : 'confDate',
            title : 'T_EMC_EQUIPT_CURVE.CONFDATE',
            editor : true,
            type : 'date'
        },{
            field : 'analysisDate',
            title : 'T_EMC_EQUIPT_CURVE.ANALYSISDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'expireDate',
            title : 'T_EMC_EQUIPT_CURVE.EXPIREDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        },{
            field : 'markingName',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGNAME',
            validators : [ 'notEmpty' ],
            editor : true
        },{
            field : 'markingConcent',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGCONCENT',
            validators : [ 'notEmpty' ],
            editor : true
        },{
            field : 'solConf',
            title : 'T_EMC_EQUIPT_CURVE.SOLCONF',
            type : 'richText',
            validators : [ 'notEmpty' ],
            editor : true
        }, {
            field : 'diluent',
            title : 'T_EMC_EQUIPT_CURVE.DILUENT',
            editor : true
        }, {
            field : 'constantVolume',
            title : 'T_EMC_EQUIPT_CURVE.CONSTANTVOLUME',
            editor : true
        }, {
            field : 'constantVolumeUnit',
            title : 'T_EMC_EQUIPT_CURVE.CONSTANTVOLUMEUNIT',
            type : 'choose',
            category : 'unit',
            single : true,
            editor : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        constantVolumeUnit : rows[0].unitName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    constantVolumeUnit : ''
                });
            }
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CURVE.REMARK',
            type : 'richText',
            editor : true
        } ];
    },

    getPreInsertListColumns : function(){
        return [ {
            checkbox : true
        }, {
            field : 'equiptCurveNo',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTCURVENO'
        },{
            field : 'testName',
            title : 'T_EMC_EQUIPT_CURVE.TESTNAME',
            preInsert : true,
            editor : true,
            type : 'choose',
            // single : true,
            category : 'test',
            onBeforeChoose : function() {
                var runGrid = Gikam.getComp('emc-run-edit-list-test-run-grid');
                if(Gikam.isEmpty(runGrid)){
                    var runFormData =  Gikam.getComp('emc-run-input-detail-form').getData();
                    return {
                        id_IN : runFormData.testId
                    }
                }else {
                    var testIds =  Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().testId;
                    return {
                        id_IN : testIds
                    }
                }

            },
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var testIds = '';
                    var testNames = '';
                    rows.map(function(item, i) {
                        testIds += rows[i].id + ',';
                        testNames += rows[i].testName + ',';
                    });
                    this.setData({
                        index : index,
                        testNo : testIds.substring(0, testIds.length - 1),
                        testName : testNames.substring(0, testNames.length - 1)
                    });
                }
            },
            onClean : function(r,v,i) {
                this.setData({
                    index : i,
                    testNo : '',
                    testName : '',
                });
            },
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_EQUIPT_CURVE.ID',
            preInsert : true
        }, {
            field : 'equiptCurveNo',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTCURVENO',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'curveConcentration',
            title : 'T_EMC_EQUIPT_CURVE.CURVECONCENTRATION',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'concentrationUnit',
            title : 'T_EMC_EQUIPT_CURVE.CONCENTRATIONUNIT',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'volume',
            title : 'T_EMC_EQUIPT_CURVE.VOLUME',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'volumeUnit',
            title : 'T_EMC_EQUIPT_CURVE.VOLUMEUNIT',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTID'
        }, {
            field : 'confDate',
            title : 'T_EMC_EQUIPT_CURVE.CONFDATE'
        }, {
            field : 'expireDate',
            title : 'T_EMC_EQUIPT_CURVE.EXPIREDATE'
        }, {
            field : 'markingNo',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGNO',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'markingName',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGNAME',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'markingConcent',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGCONCENT',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'markingUnitName',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGUNITNAME',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'markingValidate',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGVALIDATE'
        }/*, {
            field : 'markingManufacturer',
            title : 'T_EMC_EQUIPT_CURVE.MARKINGMANUFACTURER',
            validators : [ 'strLength[0,80]' ]
        }*/, {
            field : 'useConcent',
            title : 'T_EMC_EQUIPT_CURVE.USECONCENT',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'useConcentUnitName',
            title : 'T_EMC_EQUIPT_CURVE.USECONCENTUNITNAME',
            validators : [ 'strLength[0,20]' ]
        }/*, {
            field : 'colorVolume',
            title : 'T_EMC_EQUIPT_CURVE.COLORVOLUME',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'solConf',
            title : 'T_EMC_EQUIPT_CURVE.SOLCONF',
            validators : [ 'strLength[0,180]' ]
        }*/, {
            field : 'curveCondition',
            title : 'T_EMC_EQUIPT_CURVE.CURVECONDITION',
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'diluent',
            title : 'T_EMC_EQUIPT_CURVE.DILUENT',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'constantVolume',
            title : 'T_EMC_EQUIPT_CURVE.CONSTANTVOLUME',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'constantVolumeUnit',
            title : 'T_EMC_EQUIPT_CURVE.CONSTANTVOLUMEUNIT',
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'curveConfigText',
            title : 'T_EMC_EQUIPT_CURVE.CURVECONFIGTEXT',
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT_CURVE.STATUS',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CURVE.REMARK',
            validators : [ 'strLength[0,90]' ]
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptCurveNo',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTCURVENO',
            type : 'link',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTID',
            width : 100
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CURVE.EQUIPTNAME'
        },{
            field : 'testName',
            title : 'T_EMC_EQUIPT_CURVE.TESTNAME',
            width : 100,
            type : 'choose',
            category : 'test',
            editor : true,
            onBeforeChoose : function() {
                var testIds =  Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().testId;
                return {
                    id_IN : testIds
                }
            },
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var testIds = '';
                    var testNames = '';
                    rows.map(function(item, i) {
                        testIds += rows[i].id + ',';
                        testNames += rows[i].testName + ',';
                    });
                    this.setData({
                        index : index,
                        testNo : testIds.substring(0, testIds.length - 1),
                        testName : testNames.substring(0, testNames.length - 1)
                    });
                }
            },
            onClean : function(r,v,i) {
                this.setData({
                    index : i,
                    testNo : '',
                    testName : '',
                });
            },
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CURVE.CREATEDBYNAME',
            width : 100
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_CURVE.CREATEDTIME',
            width : 100
        }, {
            field : 'confDate',
            title : 'T_EMC_EQUIPT_CURVE.CONFDATE',
            width : 100
        },{
            field : 'analysisDate',
            title : 'T_EMC_EQUIPT_CURVE.ANALYSISDATE',
            width : 100
        }, {
            field : 'expireDate',
            title : 'T_EMC_EQUIPT_CURVE.EXPIREDATE',
            width : 100
        } ];
    },

    getEquiptCurvesConfigEditListColumns : function(){
        return [ {
            checkbox : true
        }, {
            field : 'curveConcentration',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.CURVECONCENTRATION',
            editor : true
        }, {
            field : 'concentrationUnit',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.CONCENTRATIONUNIT',
            editor : true,
            single : true,
            type : 'choose',
            category : 'unit',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        concentrationUnit : rows[0].unitName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    concentrationUnit : ''
                });
            }
        }, {
            field : 'volume',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.VOLUME',
            editor : true
        }, {
            field : 'volumeUnit',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.VOLUMEUNIT',
            editor : true,
            single : true,
            type : 'choose',
            category : 'unit',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        volumeUnit : rows[0].unitName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    volumeUnit : ''
                });
            }
        }, {
            field : 'strength',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.STRENGTH',
            editor : true
        }, {
            field : 'itemName',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.ITEMNAME',
            editor : true
        } ];
    },

    getCurvesLiquidConfigListColumns : function(){
        return [ {
            checkbox : true
        }, {
            field : 'curveConfigText',
            title : 'T_EMC_EQUIPT_CURVE.CURVECONFIGTEXT',
            width : '600',
            type : 'richText',
            validators : [ 'strLength[0, 1000]' ],
            editor : true
        } ];
    },

    getBatchUnitInfoFormFields : function(){
        return [ {
            field : 'concentrationUnit',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.CONCENTRATIONUNIT',
            preInsert: true,
            single : true,
            type : 'choose',
            category : 'unit',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        concentrationUnit : rows[0].unitName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    concentrationUnit : ''
                });
            }
        }, {
            field : 'volumeUnit',
            title : 'T_EMC_EQUIPT_CURVE_CONFIG.VOLUMEUNIT',
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'unit',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        volumeUnit : rows[0].unitName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    volumeUnit : ''
                });
            }
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/equipt-curves'
});
