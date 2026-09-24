var emcRunEquiptCrves = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/equipt-crves',
    getRunEquiptCrvesEditListColumns : function() {
        return [{
            checkbox : true
        }, {
            index : true
        },{
            field : 'sdReserveConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDRESERVECONCENTRATION',
            editor: true
        },{
            field : 'ext$.internalnumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.INTERNALNUMBER',
            editor: true
        },{
            field : 'solId',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SOLID',
            type: 'link',
            editor: false,
            readOnly : true,
            editable :false
        },{
            field : 'sdUseFluidConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDUSEFLUIDCONCENTRATION',
            editor: true
        },{
            field : 'curveFactorFixedTo',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CURVEFACTORFIXEDTO',
            editor: true
        },{
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            width : 250,
            category : 'testMethod',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getActivedRow();
                var methodId = data.methodId;
                return {
                	methodId_SEQ : methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        testMethodId : rows[0].id,
                        testName : rows[0].ext$.testname,
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    testMethodId : '',
                    testName : ''
                })
            },

            editor : true
        },{
            field : 'itemGroup',
            title : '分析项组',
            type : 'choose',
            width : 250,
            category : 'testItemGroup',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var activeRow = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getActivedRow();
                var methodId = activeRow.methodId;
                var testname = activeRow.testName;
                return {
                	methodId_SEQ : methodId,
                	testName_SEQ : testname
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        itemGroupId : rows[0].id,
                        itemGroup : rows[0].itemGroup
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    itemGroupId : '',
                    itemGroup : ''
                })
            },

            editor : true
        },{
            field : 'expirationTime',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.EXPIRATIONTIME',
            type : 'date',
            editor: true
        },{
            field : 'addDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDDATE',
            type : 'date',
            editor: true
        },{
            field : 'setDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SETDATE',
            type : 'date',
            editor: true
        },{
            field : 'adder',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDER',
            editor: true,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
	            onAfterChoose : function(rows) {
	                if (!Gikam.isEmpty(rows)) {
	                    this.setData({
	                        id : this.getActivedRow().id,
	                    	adderId : rows[0].id,
	                        adder : rows[0].userName,
	                    });
	                }
	            },
	            onClean : function() {
	                this.setData({
	                    id : this.getActivedRow().id,
	                    adderId : '',
	                	adder : '',
	                });
	            },
            }, {
                field : 'ext$.sdsolutionvolumeunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUMEUNIT',
                width : 250,
                editor: true
            }, {
                field : 'ext$.sdconcentrationunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATIONUNIT',
                width : 250,
                editor: true
            }, {
                field : 'stdSubstanceContentUnit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTUNIT',
                width : 250,
                editor: true
            }, {
                field : 'stdSubstanceContentVolUnit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOLUNIT',
                width : 250,
                editor: true
            },{
                field : 'methodName',
                title : 'T_EMC_TEST_METHOD.METHODNAME',
                type : 'choose',
                width : 250,
                category : 'method',
                single : true,
                editor : true,
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            index : index,
                            methodId : rows[0].id,
                            methodName : rows[0].methodName
                        
                        })
                    }
                },
                onClean : function() {
                    this.setData({
                        id : this.getActivedRow().id,
                        methodId : '',
                        methodName : ''

                    })
                },
            }, {

                field : 'ext$.equiptname',
                title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
                single : true,
                type : 'choose',
                editor : true,
                category : 'equipt',
                onBeforeChoose : function() {
                    return {
                        org_filter : '1',
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            id : this.getActivedRow().id,

                            ext$ : {
                                equiptid : rows[0].id,
                                equiptname : rows[0].equiptName
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        id : this.getActivedRow().id,

                        ext$ : {
                            equiptid : '',         	
                            equiptname : ''
                        }
                    });
                }
            
            },{
                field : 'ext$.aslope',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ASLOPE',
                editor: true
            },{
                field : 'ext$.bintercept',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.BINTERCEPT',
                editor: true
            },{
                field : 'ext$.rrelatedfactor',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.RRELATEDFACTOR',
                editor: true
            }
        ];
    },
    
    getRunEquiptCrvesHistoryEditListColumns : function() {
        return [{
            checkbox : true
        }, {
            index : true
        },{
            field : 'sdReserveConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDRESERVECONCENTRATION',
            editor: true
        },{
            field : 'ext$.internalnumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.INTERNALNUMBER',
            editor: true
        },{
            field : 'solId',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SOLID',
            type: 'link',
            editor: false,
            readOnly : true,
            editable :false
        },{
            field : 'sdUseFluidConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDUSEFLUIDCONCENTRATION',
            editor: true
        },{
            field : 'curveFactorFixedTo',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CURVEFACTORFIXEDTO',
            editor: true
        },{
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            width : 250,
            category : 'testMethod',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getActivedRow();
                var methodId = data.methodId;
                return {
                	methodId_SEQ : methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        testMethodid : rows[0].id,
                    	testName : rows[0].ext$.testname,
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    testMethodid : '',
                    testName : ''
                })
            },

            editor : true
        },{
            field : 'itemGroup',
            title : '分析项组',
            type : 'choose',
            width : 250,
            category : 'testItemGroup',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var activeRow = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getActivedRow();
                var testMethodId = activeRow.testMethodId;
                if(testMethodId !=null && testMethodId != ""&& typeof testMethodId != 'undefined'){
                	return {
                		testMethodId_SEQ : testMethodId
                    }
                }else{
                	return {
                		testMethodId_SEQ : "9"
                    }
                }
            },
            
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        itemGroupId : rows[0].id,
                        itemGroup : rows[0].itemGroup
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    itemGroupId : '',
                    itemGroup : ''
                })
            },
            editor : true
        },{
            field : 'expirationTime',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.EXPIRATIONTIME',
            type : 'date',
            editor: true
        },{
            field : 'addDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDDATE',
            type : 'date',
            editor: true
        },{
            field : 'setDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SETDATE',
            type : 'date',
            editor: true
        },{
            field : 'adder',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDER',
            editor: true,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
	            onAfterChoose : function(rows) {
	                if (!Gikam.isEmpty(rows)) {
	                    this.setData({
	                        id : this.getActivedRow().id,
	                    	adderId : rows[0].id,
	                        adder : rows[0].userName,
	                    });
	                }
	            },
	            onClean : function() {
	                this.setData({
	                    id : this.getActivedRow().id,
	                    adderId : '',
	                	adder : '',
	                });
	            },
            }, {
                field : 'ext$.sdsolutionvolumeunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUMEUNIT',
                width : 250,
                editor: true
            }, {
                field : 'ext$.sdconcentrationunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATIONUNIT',
                width : 250,
                editor: true
            }, {
                field : 'stdSubstanceContentUnit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTUNIT',
                width : 250,
                editor: true
            }, {
                field : 'stdSubstanceContentVolUnit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOLUNIT',
                width : 250,
                editor: true
            },{
                field : 'methodName',
                title : 'T_EMC_TEST_METHOD.METHODNAME',
                type : 'choose',
                width : 250,
                category : 'method',
                single : true,
                editor : true,
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            index : index,
                            methodId : rows[0].id,
                            methodName : rows[0].methodName
                        })
                    }
                },
                onClean : function() {
                    this.setData({
                        id : this.getActivedRow().id,
                        methodId : '',
                        methodName : ''
                    })
                },
            }, {

                field : 'ext$.equiptname',
                title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
                single : true,
                type : 'choose',
                editor : true,
                category : 'equipt',
                onBeforeChoose : function() {
                    return {
                        org_filter : '1',
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            id : this.getActivedRow().id,

                            ext$ : {
                                equiptid : rows[0].id,
                                equiptname : rows[0].equiptName
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        id : this.getActivedRow().id,

                        ext$ : {
                            equiptid : '',         	
                            equiptname : ''
                        }
                    });
                }
            
            },{
                field : 'ext$.aslope',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ASLOPE',
                editor: true
            },{
                field : 'ext$.bintercept',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.BINTERCEPT',
                editor: true
            },{
                field : 'ext$.rrelatedfactor',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.RRELATEDFACTOR',
                editor: true
            },{
                field : 'isAbandon',
                title : '是否废弃',
                editor :true,
                type : 'select',
                search : true,
                category : 'sys',
                param : {
                    codeCategoryId : 'yesorno',
                },
            }
        ];
    },

    getRunEquiptCrvesDetailListColumns : function() {
        return [/*{
            field : 'sdSubstances',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSUBSTANCES',
            editor: true
        },*/{
            field : 'sdReserveConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDRESERVECONCENTRATION',
            editor: true
        },{
            field : 'ext$.internalnumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.INTERNALNUMBER',
            editor: true
        },{
            field : 'solId',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SOLID',
            width : 260,
            onChange : function(field, value, index) {
                var activeRow = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getActivedRow();
                console.log(activeRow);
                Gikam.postText(emcRunEquiptCrves.baseUrl + '/change-solid', Gikam.getJsonWrapper({
                    f :{
                    	solId : value,
                    }}, [ 'PfAnalyticalEquiptCrvesService', [activeRow]]));
            }
        },{
            field : 'sdUseFluidConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDUSEFLUIDCONCENTRATION',
            editor: true
        },{
            field : 'curveFactorFixedTo',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CURVEFACTORFIXEDTO',
            editor: true
        },{
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            width : 250,
            category : 'testMethod',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getActivedRow();
                var methodId = data.methodId;
                return {
                	methodId_SEQ : methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        testMethodId : rows[0].id,
                    	testName : rows[0].ext$.testname,
                    })
                }
            },
            onClean : function() {
                this.setData({
                	testMethodId : '',
                	testName : ''

                })
            },

            editor : true
        },{
            field : 'expirationTime',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.EXPIRATIONTIME',
            type : 'date',
            width : 300,
            editor: true
        }, {
            field : 'ext$.sdsolutionvolumeunit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUMEUNIT',
            width : 250,
            editor: true
        }, {
            field : 'ext$.sdconcentrationunit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATIONUNIT',
            width : 250,
            editor: true
        }, {
            field : 'stdSubstanceContentUnit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTUNIT',
            width : 250,
            editor: true
        }, {
            field : 'stdSubstanceContentVolUnit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOLUNIT',
            width : 250,
            editor: true
        },{
            field : 'itemGroup',
            title : '分析项组',
            type : 'choose',
            width : 250,
            category : 'testItemGroup',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var selections = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getSelections();
                var testMethodId = selections[0].testMethodId;
                if(testMethodId !=null && testMethodId != ""&& typeof testMethodId != 'undefined'){
                	return {
                		testMethodId_SEQ : testMethodId
                    }
                }else{
                	return {
                		testMethodId_SEQ : "9"
                    }
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                       itemGroupId : rows[0].id,
                        itemGroup : rows[0].itemGroup
                    })
                }
            },
            onClean : function() {
                this.setData({
                    itemGroupId : '',
                    itemGroup : ''
                })
            },

            editor : true
        },{
            field : 'ext$.aslope',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ASLOPE',
            editor: true
        },{
            field : 'ext$.bintercept',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.BINTERCEPT',
            editor: true
        },{
            field : 'ext$.rrelatedfactor',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.RRELATEDFACTOR',
            editor: true
        }];
    },
    
    getRunEquiptCrvesParamListColumns : function() {
        return [{
            checkbox : true
        },{
            field : 'analysisNumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ANALYSISNUMBER',
            editor: true

        },{
            field : 'sdSubstances',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSUBSTANCES',
            editor: true
        },{
            field : 'sdSolutionVolume',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUME',
            editor: true
        },{
            field : 'sdConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATION',
            editor: true
        },{
            field : 'remark',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.REMARK',
            editor: true
        },{
            field : 'stdSubstanceContent',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENT',
            editor: true
        },{
            field : 'stdSubstanceContentVol',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOL',
            editor: true
        },{
            field : 'ext$.peakareaassayvalue',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.PEAKAREAASSAYVALUE',
            editor: true
        },{
            field : 'qcType',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.QCTYPE',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            multiple :true,
            category : 'sys',
            param : {
                codeCategoryId : 'QCType'
            },
        }];
    },
    
    getEquiptCrvesBatchInputFields : function() {
        return [{
            field : 'sdSubstances',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSUBSTANCES',
            editor: true
        },{
            field : 'sdSolutionVolume',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUME',
            editor: true
        },{
            field : 'sdConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATION',
            editor: true
        },{
            field : 'remark',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.REMARK',
            editor: true
        },{
            field : 'stdSubstanceContent',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENT',
            editor: true
        },{
            field : 'stdSubstanceContentVol',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOL',
            editor: true
        },{
            field : 'ext$.peakareaassayvalue',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.PEAKAREAASSAYVALUE',
            editor: true
        },{
            field : 'qcType',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.QCTYPE',
            editor : true,
            type : 'select',
            search : true,
            multiple :true,
            category : 'sys',
            param : {
                codeCategoryId : 'QCType'
            },
        }];
    },
 
    getRunEquiptCrvesHistoryDetailListColumns : function() {
        return [/*{
            field : 'sdSubstances',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSUBSTANCES',
            editor: true
        },*/{
            field : 'sdReserveConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDRESERVECONCENTRATION',
            editor: true
        },{
            field : 'ext$.internalnumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.INTERNALNUMBER',
            editor: true
        },{
            field : 'solId',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SOLID',
            width : 260,
            onChange : function(field, value, index) {
                var activeRow = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getActivedRow();
                console.log(activeRow);
                Gikam.postText(emcRunEquiptCrves.baseUrl + '/change-solid', Gikam.getJsonWrapper({
                    f :{
                    	solId : value,
                    }}, [ 'PfAnalyticalEquiptCrvesService', [activeRow]]));
            }
        },{
            field : 'sdUseFluidConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDUSEFLUIDCONCENTRATION',
            editor: true
        },{
            field : 'curveFactorFixedTo',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CURVEFACTORFIXEDTO',
            editor: true
        },{
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            width : 250,
            category : 'testMethod',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getActivedRow();
                var methodId = data.methodId;
                return {
                	methodId_SEQ : methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        testMethodId : rows[0].id,
                    	testName : rows[0].ext$.testname,
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testMethodId : '',
                    testName : ''
                })
            },

            editor : true
        },{
            field : 'expirationTime',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.EXPIRATIONTIME',
            type : 'date',
            width : 300,
            editor: true
        }, {
            field : 'ext$.sdsolutionvolumeunit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUMEUNIT',
            width : 250,
            editor: true
        }, {
            field : 'ext$.sdconcentrationunit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATIONUNIT',
            width : 250,
            editor: true
        }, {
            field : 'stdSubstanceContentUnit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTUNIT',
            width : 250,
            editor: true
        }, {
            field : 'stdSubstanceContentVolUnit',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOLUNIT',
            width : 250,
            editor: true
        } ,{
            field : 'itemGroup',
            title : '分析项组',
            type : 'choose',
            width : 250,
            category : 'testItemGroup',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
                var testMethodId = selections[0].testMethodId;
                if(testMethodId !=null && testMethodId != ""&& typeof testMethodId != 'undefined'){
                	return {
                		testMethodId_SEQ : testMethodId
                    }
                }else{
                	return {
                		testMethodId_SEQ : "9"
                    }
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                       itemGroupId : rows[0].id,
                        itemGroup : rows[0].itemGroup
                    })
                }
            },
            onClean : function() {
                this.setData({
                    itemGroupId : '',
                    itemGroup : ''
                })
            },


            editor : true
        },{
            field : 'ext$.aslope',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ASLOPE',
            editor: true
        },{
            field : 'ext$.bintercept',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.BINTERCEPT',
            editor: true
        },{
            field : 'ext$.rrelatedfactor',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.RRELATEDFACTOR',
            editor: true
        }];
    },
    
    getRunEquiptCrvesHistoryParamListColumns : function() {
        return [{
            checkbox : true
        },{
            field : 'analysisNumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ANALYSISNUMBER',
            editor: true

        },{
            field : 'sdSubstances',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSUBSTANCES',
            editor: true
        },{
            field : 'sdSolutionVolume',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUME',
            editor: true
        },{
            field : 'sdConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATION',
            editor: true
        },{
            field : 'remark',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.REMARK',
            editor: true
        },{
            field : 'stdSubstanceContent',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENT',
            editor: true
        },{
            field : 'stdSubstanceContentVol',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.STDSUBSTANCECONTENTVOL',
            editor: true
        },{
            field : 'ext$.peakareaassayvalue',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.PEAKAREAASSAYVALUE',
            editor: true
        },{
            field : 'qcType',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.QCTYPE',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            multiple :true,
            category : 'sys',
            param : {
                codeCategoryId : 'QCType'
            },
        }];
    },
    
    getCrvesNumFields : function() {
        return[{
               field : 'crvesNums',
               title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CRVESNUMS',
               editor: true,
               validators : [ 'number', 'notEmpty' ]
        }];
    },
       
   
    getBatchInputFields : function() {
        return [{
            checkbox : true
        }, {
            index : true
        },{
            field : 'sdReserveConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDRESERVECONCENTRATION',
            width : 250,
            editor: true
        },{
            field : 'ext$.internalnumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.INTERNALNUMBER',
            width : 250,
            editor: true
        },{
            field : 'sdUseFluidConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDUSEFLUIDCONCENTRATION',
            width : 250,
            editor: true
        },{
            field : 'curveFactorFixedTo',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CURVEFACTORFIXEDTO',
            width : 250,
            editor: true
        },{
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            width : 250,
            category : 'testMethod',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getSelections();
                var methodId = data[0].methodId;
                return {
                	methodId_SEQ : methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        testMethodId : rows[0].id,
                    	testName : rows[0].ext$.testname,
                    })
                }
            },
            onClean : function() {
                this.setData({
                	testMethodId : '',
                	testName : ''
                })
            },

            editor : true
        },{
            field : 'expirationTime',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.EXPIRATIONTIME',
            type : 'date',
            width : 250,
            editor: true
        }/*,{
            field : 'addDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDDATE',
            type : 'date',
            width : 250,
            editor: true
        }*/,{
            field : 'setDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SETDATE',
            type : 'date',
            editor: true
        },{
            field : 'adder',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDER',
            editor: true,
            preInsert : true,
            type : 'choose',
            width : 250,
            category : 'user-simple',
            single : true,
/*            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getActivedRow();
                    return {
                        departmentid_SEQ : activedRow.createdByOrgId
                    }
                
            },*/
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	adderId : rows[0].id,
                        adder : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    adderId : '',
                	adder : '',
                });
            },
            }, {
                field : 'ext$.sdsolutionvolumeunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUMEUNIT',
                width : 250,
                editor: true
            }, {
                field : 'ext$.sdconcentrationunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATIONUNIT',
                width : 250,
                editor: true
            },{
                field : 'methodName',
                title : 'T_EMC_TEST_METHOD.METHODNAME',
                type : 'choose',
                width : 250,
                category : 'method',
                single : true,
                editor : true,
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            index : index,
                            methodId : rows[0].id,
                            methodName : rows[0].methodName
                        })
                    }
                },
                onClean : function() {
                    this.setData({
                    	methodId : '',
                    	methodName : ''
                    })
                },
            }, {

                field : 'ext$.equiptname',
                title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
                single : true,
                type : 'choose',
                editor : true,
                category : 'equipt',
                onBeforeChoose : function() {
                    return {
                        org_filter : '1',
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({

                            ext$ : {
                                equiptid : rows[0].id,
                                equiptname : rows[0].equiptName
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        ext$ : {
                            equiptid : '',         	
                            equiptname : ''
                        }
                    });
                }
            
            },{
                field : 'itemGroup',
                title : '分析项组',
                type : 'choose',
                width : 250,
                category : 'testItemGroup',
                single : true,
                editable :true,
                onBeforeChoose : function() {
                    var selections = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getSelections();
                    var testMethodId = selections[0].testMethodId;
                    if(testMethodId !=null && testMethodId != ""&& typeof testMethodId != 'undefined'){
                    	return {
                    		testMethodId_SEQ : testMethodId
                        }
                    }else{
                    	return {
                    		testMethodId_SEQ : "9"
                        }
                    }
                },
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            index : index,
                            itemGroupId : rows[0].id,
                            itemGroup : rows[0].itemGroup
                        })
                    }
                },
                onClean : function() {
                    this.setData({
                        itemGroupId : '',
                        itemGroup : ''
                    })
                },


                editor : true
            },{
                field : 'ext$.aslope',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ASLOPE',
                editor: true
            },{
                field : 'ext$.bintercept',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.BINTERCEPT',
                editor: true
            },{
                field : 'ext$.rrelatedfactor',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.RRELATEDFACTOR',
                editor: true
            }
        ];
    },
    
    getHistoryBatchInputFields : function() {
        return [{
            checkbox : true
        }, {
            index : true
        },{
            field : 'sdReserveConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDRESERVECONCENTRATION',
            width : 250,
            editor: true
        },{
            field : 'ext$.internalnumber',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.INTERNALNUMBER',
            width : 250,
            editor: true
        },{
            field : 'sdUseFluidConcentration',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDUSEFLUIDCONCENTRATION',
            width : 250,
            editor: true
        },{
            field : 'curveFactorFixedTo',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.CURVEFACTORFIXEDTO',
            width : 250,
            editor: true
        },{
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            width : 250,
            category : 'testMethod',
            single : true,
            editable :true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
                var methodId = data[0].methodId;
                return {
                	methodId_SEQ : methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        testMethodId : rows[0].id,
                    	testName : rows[0].ext$.testname,
                    })
                }
            },
            onClean : function() {
                this.setData({
                	testMethodId : '',
                	testName : ''
                })
            },

            editor : true
        },{
            field : 'expirationTime',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.EXPIRATIONTIME',
            type : 'date',
            width : 250,
            editor: true
        }/*,{
            field : 'addDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDDATE',
            type : 'date',
            width : 250,
            editor: true
        }*/,{
            field : 'setDate',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SETDATE',
            type : 'date',
            editor: true
        },{
            field : 'adder',
            title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ADDER',
            editor: true,
            preInsert : true,
            type : 'choose',
            width : 250,
            category : 'user-simple',
            single : true,
/*            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-run-equiptcrves-edit-list-grid').getActivedRow();
                    return {
                        departmentid_SEQ : activedRow.createdByOrgId
                    }
                
            },*/
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	adderId : rows[0].id,
                        adder : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    adderId : '',
                	adder : '',
                });
            },
            }, {
                field : 'ext$.sdsolutionvolumeunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDSOLUTIONVOLUMEUNIT',
                width : 250,
                editor: true
            }, {
                field : 'ext$.sdconcentrationunit',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.SDCONCENTRATIONUNIT',
                width : 250,
                editor: true
            },{
                field : 'methodName',
                title : 'T_EMC_TEST_METHOD.METHODNAME',
                type : 'choose',
                width : 250,
                category : 'method',
                single : true,
                editor : true,
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            index : index,
                            methodId : rows[0].id,
                            methodName : rows[0].methodName
                        })
                    }
                },
                onClean : function() {
                    this.setData({
                    	methodId : '',
                    	methodName : ''
                    })
                },
            }, {

                field : 'ext$.equiptname',
                title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
                single : true,
                type : 'choose',
                editor : true,
                category : 'equipt',
                onBeforeChoose : function() {
                    return {
                        org_filter : '1',
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({

                            ext$ : {
                                equiptid : rows[0].id,
                                equiptname : rows[0].equiptName
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        ext$ : {
                            equiptid : '',         	
                            equiptname : ''
                        }
                    });
                }
            
            },{
                field : 'itemGroup',
                title : '分析项组',
                type : 'choose',
                width : 250,
                category : 'testItemGroup',
                single : true,
                editable :true,
                onBeforeChoose : function() {
                    var selections = Gikam.getComp('emc-run-history-equiptcrves-list-grid').getSelections();
                    var testMethodId = selections[0].testMethodId;
                    if(testMethodId !=null && testMethodId != ""&& typeof testMethodId != 'undefined'){
                    	return {
                    		testMethodId_SEQ : testMethodId
                        }
                    }else{
                    	return {
                    		testMethodId_SEQ : "9"
                        }
                    }
                },
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            index : index,
                            itemGroupId : rows[0].id,
                            itemGroup : rows[0].itemGroup
                        })
                    }
                },
                onClean : function() {
                    this.setData({
                    	itemGroupId : '',
                        itemGroup : ''
                    })
                },


                editor : true
            },{
                field : 'ext$.aslope',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.ASLOPE',
                editor: true
            },{
                field : 'ext$.bintercept',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.BINTERCEPT',
                editor: true
            },{
                field : 'ext$.rrelatedfactor',
                title : 'T_EMC_ANALYTICAL_EQUIPT_CRVES.RRELATEDFACTOR',
                editor: true
            }
        ];
    },
    
    getPrepareShowAllField : function() {
        var flag = true;
        var selections = Gikam.getComp('emc-run-edit-list-test-run-grid').getSelections();
        var _this = this;
        var runId = selections[0].id;  
        var methodId = selections[0].methodId;
        var testids = "";
        var itemgroups ="";
        //批下测试还要对分析项组进行筛选,找到批下的分析项组
        Gikam.postText(emcRunEquiptCrves.baseUrl + '/select-run-testid', Gikam.getJsonWrapper({
            f :{
            	runId : runId,
            }})).done(function(data) {
            	testids = data          
            });
        Gikam.postText(emcRunEquiptCrves.baseUrl + '/select-run-itemgroup', Gikam.getJsonWrapper({
            f :{
            	runId : runId,
            }})).done(function(data) {
            	itemGroupIds = data          
            });
        return [ {
            title : '显示批下测试',
            field : 'showFlag',
            refreshGrid : false,
            type : 'simpleCheckbox',
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-run-history-equiptcrves-list-grid');
                var showFlags = value;
                if (showFlags == '1') {
                	grid.refresh({
                        requestData : {
                            methodId_IN : methodId,
                        	testMethodId_IN :testids,
                        	itemGroupId_IN :itemGroupIds
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                	grid.refresh({
                        requestData : {
                            methodId_IN : '',
                            testMethodId_IN :'',
                            itemGroupId_IN :''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },
    
    workflow : {
        dbTable : 'T_EMC_RUN',
        columns : [ {
            field : 'id',
            title : 'T_EMC_RUN.ID'
        } ]
    }

});
