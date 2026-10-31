var emcEquiptCaliResult = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-cali-results',
    baseUrl2 : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-cali-result-norms',

    workflow: {
        dbTable: 'T_EMC_EQUIPT_CALI_RESULT',
        columns: [{
            field: 'id',
            title: 'T_EMC_EQUIPT_CALI_RESULT.ID'
        }]
    },

    getCaliEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcEquiptCaliResult.workflow.dbTable),{
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_CALI_RESULT.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_CALI_RESULT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_CALI_RESULT.SPECIFICATION'
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIDATE',
            type : 'date',
            editor: 'true',
            onChange : function (field, value, index) {
                var data = this.getData()[index];
                if (data.cycle && value) {
                    var cycleExpArray = ['0-0-1', '0-0-7', '0-1-0', '0-2-0', '0-3-0', '0-4-0', '0-6-0', '1-0-0', '2-0-0', '3-0-0', '5-0-0'];
                    var tempDate = new Date(value);
                    var cycleExp = cycleExpArray[parseInt(data.cycle) - 1]
                    if (cycleExp) {
                        var cycleValues = cycleExp.split('-');
                        var cycleYear = cycleValues[0];
                        var cycleMonth = cycleValues[1];
                        var cycleDay = cycleValues[2];
                        tempDate.setFullYear(tempDate.getFullYear() + parseInt(cycleYear));
                        tempDate.setMonth(tempDate.getMonth() + parseInt(cycleMonth));
                        tempDate.setDate(tempDate.getDate() + parseInt(cycleDay));
                        tempDate.setDate(tempDate.getDate()-1);
                        var newEndDate = Gikam.DateUtils.formatter(tempDate, 'yyyy-MM-dd');
                        this.setData({
                            index : index,
                            id : data.id,
                            maintDate : value,
                            validTime : newEndDate
                        })
                    }
                }
            },
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CYCLE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'cycle',
            },
            onChange : function (field, value, index) {
                var data = this.getData()[index];
                if (data.maintDate && value) {
                    var cycleExpArray = ['0-0-1', '0-0-7', '0-1-0', '0-2-0', '0-3-0', '0-4-0', '0-6-0', '1-0-0', '2-0-0', '3-0-0', '5-0-0'];
                    var tempDate = new Date(data.maintDate);
                    var cycleExp = cycleExpArray[parseInt(value) - 1]
                    if (cycleExp) {
                        var cycleValues = cycleExp.split('-');
                        var cycleYear = cycleValues[0];
                        var cycleMonth = cycleValues[1];
                        var cycleDay = cycleValues[2];
                        tempDate.setFullYear(tempDate.getFullYear() + parseInt(cycleYear));
                        tempDate.setMonth(tempDate.getMonth() + parseInt(cycleMonth));
                        tempDate.setDate(tempDate.getDate() + parseInt(cycleDay));
                        tempDate.setDate(tempDate.getDate()-1);
                        var newEndDate = Gikam.DateUtils.formatter(tempDate, 'yyyy-MM-dd');

                        this.setData({
                            index : index,
                            id : data.id,
                            cycle : value,
                            validTime : newEndDate
                        })
                    }
                }
            },
        }, {
            field : 'unit',
            title : 'T_EMC_EQUIPT_CALI_RESULT.UNIT',
            type : 'select',
            editor: true,
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
        }, {
            field : 'validTime',
            title : 'T_EMC_EQUIPT_CALI_RESULT.VALIDTIME',
            type : 'date',
            editor : true,
        }, {
            field : 'reportCategory',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REPORTCATEGORY',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId: 'certificatetype',
            },
        }, {
            field : 'certNo',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CERTNO',
            editor : true,
        }, {
            field : 'caliReason',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIREASON',
            editor : true,
            multiple : true,
            type: 'select',
            category: 'sys',
            search: true,
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REMARK',
            editor : true,
        }, {
            field : 'caliResult',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIRESULT',
            editor : true
        }, {
            field : 'claim',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CLAIM',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 85,
            editor : true,
        }, {
            field : 'uncertainty',
            title : 'T_EMC_EQUIPT_CALI_RESULT.UNCERTAINTY',
            editor : true,
        }, {
            field : 'contentResult',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CONTENTRESULT',
            editor : true,
            type : 'select',
            category : 'sys',
            multiple : true,
            param : {
                codeCategoryId : 'contentResult',
            },
        }, {
            field : 'reviewerName',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REVIEWER',
            editor : true,
            type : 'choose',
            validators : ['notEmpty'],
            single : true,
            category : 'user-simple',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-cali-result-edit-list-grid').getActivedRow().id,
                        reviewerId : rows[0].id,
                        reviewerName : rows[0].userName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    reviewerId : '',
                    reviewerName : '',
                });
            }

        }, {
            field : 'reviewDate',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REVIEWDATE',
            type : 'date',
            editor : true,
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcEquiptCaliResult.workflow.dbTable),{
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_CALI_RESULT.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_CALI_RESULT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_CALI_RESULT.SPECIFICATION'
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIDATE',
            type : 'date'
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CYCLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            }
        }, {
            field : 'unit',
            title : 'T_EMC_EQUIPT_CALI_RESULT.UNIT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
        }, {
            field : 'validTime',
            title : 'T_EMC_EQUIPT_CALI_RESULT.VALIDTIME',
            type : 'date'
        }, {
            field : 'reportCategory',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REPORTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'certificatetype'
            }
        }, {
            field : 'certNo',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CERTNO',
        }, {
            field : 'caliReason',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIREASON',
            type: 'select',
            multiple : true,
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'caliResult',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIRESULT'
        }, {
            field : 'claim',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CLAIM',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 85
        }, {
            field : 'uncertainty',
            title : 'T_EMC_EQUIPT_CALI_RESULT.UNCERTAINTY',
        }, {
            field : 'contentResult',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CONTENTRESULT',
            type : 'select',
            category : 'sys',
            multiple : true,
            param : {
                codeCategoryId : 'contentResult'
            }
        }, {
            field : 'reviewerName',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REVIEWER'
        }, {
            field : 'reviewDate',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REVIEWDATE'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcEquiptCaliResult.workflow.dbTable),{
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_CALI_RESULT.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_CALI_RESULT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_CALI_RESULT.SPECIFICATION'
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIDATE',
            type : 'date'
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CYCLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            }
        }, {
            field : 'unit',
            title : 'T_EMC_EQUIPT_CALI_RESULT.UNIT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
        }, {
            field : 'validTime',
            title : 'T_EMC_EQUIPT_CALI_RESULT.VALIDTIME',
            type : 'date'
        }, {
            field : 'reportCategory',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REPORTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'certificatetype'
            }
        }, {
            field : 'certNo',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CERTNO',
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REMARK',
        }, {
            field : 'caliReason',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIREASON',
            type: 'select',
            multiple : true,
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'caliResult',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CALIRESULT'
        }, {
            field : 'claim',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CLAIM',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 85
        }, {
            field : 'uncertainty',
            title : 'T_EMC_EQUIPT_CALI_RESULT.UNCERTAINTY',
        }, {
            field : 'contentResult',
            title : 'T_EMC_EQUIPT_CALI_RESULT.CONTENTRESULT',
            type : 'select',
            category : 'sys',
            multiple : true,
            param : {
                codeCategoryId : 'contentResult'
            }
        }, {
            field : 'reviewerName',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REVIEWER'
        }, {
            field : 'reviewDate',
            title : 'T_EMC_EQUIPT_CALI_RESULT.REVIEWDATE'
        } ];
    },

    getNormEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testId',
            title : 'T_EMC_EQUIPT_CALI_RESULT_NORM.TESTID'
        }, {
            field : 'testName',
            title : 'T_EMC_EQUIPT_CALI_RESULT_NORM.TESTNAME'
        }, {
            field : 'testCategoryDesc',
            title : 'T_EMC_EQUIPT_CALI_RESULT_NORM.TESTCATEGORYDESC'
        }, {
            field : 'methodName',
            title : 'T_EMC_EQUIPT_CALI_RESULT_NORM.METHODNAME'
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_EQUIPT_CALI_RESULT_NORM.TESTMETHODBASIS'
        } ];
    },


});
