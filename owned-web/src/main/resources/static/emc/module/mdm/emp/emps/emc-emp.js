var emcEmp = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/emp/emps',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_EMC_EMP.ID',
            field : 'id'
        }, {
            title : 'T_EMC_EMP.USERNAME',
            field : 'userName'
        }, {
            title : '部门',
            field : 'ext$.departmentname'
        }, {
            title : '岗位',
            field : 'station',
        },{
            title : '状态',
            field : 'ext$.status',
            type : 'select',
            category : 'sys',
            width: 100,
            param : {
                codeCategoryId : 'userStatus'
            }
        }, {
            title : 'T_EMC_EMP.FILTERTEST',
            field : 'filterTest',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true
        }, {
            title : '是否报告审核人',
            field : 'reportAudit',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true
        }, {
            title : '是否报告签发人',
            field : 'reportIssue',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_EMC_EMP.ID',
            field : 'id',
            readonly : true
        }, {
            title : 'T_EMC_EMP.USERNAME',
            validators : [ 'strLength[0,21]' ],
            field : 'userName'
        }, {
            title : 'T_EMC_EMP.BIRTHDAY',
            field : 'birthday',
            type : 'date',
            onChange : function(field, value, rowIndex, oldValue, text) {
                var _this = emcEmp.editPage;
                var empRow = _this.empGrid.getActivedRow();
                if (Gikam.isEmpty(empRow)) {
                    return;
                }
                if(!Gikam.isEmpty(value)) {
                    var birthyear=value.substr(0,4);
                    var currYear=new Date().getFullYear();
                    var age=parseInt(currYear)-parseInt(value);
                    Gikam.getComp('emc-emp-detail-base-info-form').setData({
                        id : empRow.id,
                        age : age,
                        birthday : value,
                    });
                }
            }
        }, {
            title : 'T_EMC_EMP.AGE',
            field : 'age',
            readonly : true
        }, {
            title : 'T_EMC_EMP.SEX',
            field : 'sex',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sex',
            }
        }, {
            title : 'T_EMC_EMP.IDCARD',
            validators : [ 'strLength[0,21]' ],
            field : 'ext$.idcard'
        }, {
            title : 'T_EMC_EMP.DEGREE',
            validators : [ 'strLength[0,21]' ],
            field : 'ext$.degree'
        }, {
            title : 'T_EMC_EMP.GRADUATIONTIME',
            validators : [ 'strLength[0,21]' ],
            field : 'ext$.graduationtime',
            type : 'date',
        }, {
            field : 'ext$.departmentname',
            title : 'T_EMC_EMP.DEPARTMENTNAME',
            readonly : true
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_EMP.ORGNAME',
            readonly : true
        }, {
            title : 'T_EMC_EMP.PROFESSIONALTITLE',
            field : 'professionalTitle'
        }, {
            title : 'T_EMC_EMP.MAJOR',
            field : 'major',
            validators : [ 'strLength[0,12]' ],
        }, {
            title : 'T_EMC_EMP.WORKYEAR',
            validators : [ 'strLength[0,21]' ],
            titleWidthAuto : true,
            field : 'ext$.workyear'
        }, {
            title : 'T_EMC_EMP.POSTYEAR',
            validators : [ 'strLength[0,21]' ],
            field : 'ext$.postyear'
        }, {
            title : 'T_EMC_EMP.POSITION',
            field : 'position'
        }, {
            title : '报告编制人',
            field : 'editReport',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            title : '报告审核人',
            field : 'reportAudit',
            type : 'select',
            category : 'sys',
            param : {
            codeCategoryId : 'yesorno'
            },
        }, {
            title : '报告签发人',
            field : 'reportIssue',
            type : 'select',
            category : 'sys',
            param : {
            codeCategoryId : 'yesorno'
            },
        }, {
            title : 'T_EMC_EMP.ORDERQCUSER',
            field : 'orderQcUser',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.transportcontracter',
            title : 'T_EMC_EMP.TRANSPORTCONTRACTER',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            title : 'T_EMC_EMP.PROJECTQCUSER',
            field : 'projectQcUser',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            title : 'T_EMC_EMP.ISEQUIPTMANAGER',
            field : 'isEquiptManager',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            title : 'T_EMC_EMP.QUOTER',
            field : 'quoter',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            title : '岗位',
            field : 'station',
        }, {
        //     title : 'T_EMC_EMP.ERPGBID',
        //     field : 'erpGbId',
        // }, {
        //     title : 'T_EMC_EMP.ERPJOBNUMBER',
        //     field : 'erpJobNumber',
        // }, {}, {
            title : 'T_EMC_EMP.REMARK',
            field : 'remark',
            type : 'textarea',
            validators : [ 'strLength[0,170]' ],
            colspan : 2
        }, {
            title : 'T_EMC_EMP.ELECTRONICSIGNATURE',
            field : 'electronicSignature',
            type : 'image',
            validators : [ 'strLength[0,85]' ],
            height : 60,
            onClick : function() {

                var _this = emcEmp.editPage;
                var empRow = _this.empGrid.getActivedRow();
                if (Gikam.isEmpty(empRow)) {
                    return;
                }
                _this.uploadElectronicSignature();

            }
        }, {}, {}, {
            title : 'T_EMC_EMP.PIC',
            field : 'pic',
            type : 'image',
            validators : [ 'strLength[0,85]' ],
            height : 60,
            onClick : function() {

                var _this = emcEmp.editPage;
                var empRow = _this.empGrid.getActivedRow();
                if (Gikam.isEmpty(empRow)) {
                    return;
                }
                _this.uploadEmpPicture();

            }
        }, {}, {} ]
    },

    getQualListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : '证件号',
            field : 'qualNo',
            editor : true,
        }, {
            title : 'T_EMC_EMP_QUAL.QUALNAME',
            field : 'qualName',
            editor : true,
        }, {
            title : 'T_EMC_EMP_QUAL.QUALORG',
            field : 'qualOrg',
            editor : true,
        }, {
            field : 'authDate',
            title : 'T_EMC_CERT.AUTHDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'expireDate',
            title : 'T_EMC_CERT.EXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'warningDays',
            title : 'T_EMC_CERT.WARNINGDAYS',
            editor : true,
            validators : [ 'number', 'strLength[0,20]' ],
        }, {
            title : 'T_EMC_EMP_QUAL.FILE',
            field : 'ext$.filename',
            type : 'link'
        } ]
    },

    getTestListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_EMC_EMP_TEST.TESTID',
            field : 'ext$.testcategory',
        }, {
            title : 'T_EMC_EMP_TEST.TESTNAME',
            field : 'ext$.testname'
        } ]
    },

    getCredentialsListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_EMC_EMPCREDENTIALS.CERTNO',
            field : 'certNo',
        }, {
            title : 'T_EMC_EMPCREDENTIALS.CERTNAME',
            field : 'certName'
        }, {
            title : 'T_EMC_EMPCREDENTIALS.CREATEDTIME',
            field : 'authDate',
            type : 'date',
        }, {
            title : 'T_EMC_EMPCREDENTIALS.EXPIREDATE',
            field : 'expireDate'
        } ]
    },

    getMethodListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'type',
            title : '类别',
            editor : true,
            validators : [ 'strLength[0,22]' ],
        },{
            field : 'ext$.testcategoryid',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTCATEGORYID',
            width : 130,
            type : 'select',
            category : 'testCategory'
        },{
            field : 'ext$.testclass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        },{
            title : 'T_EMC_EMPMETHOD.TESTNAME',
            field : 'ext$.testname',
        }, {
            title : 'T_EMC_EMPMETHOD.METHODNAME',
            field : 'ext$.methodname'
        }, {
            title : 'T_EMC_EMPMETHOD.REMARK',
            field : 'remark',
            editor : true,
            validators : [ 'strLength[0,170]' ],
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            title : 'T_EMC_EMP.ID',
            field : 'id',
        }, {
            title : 'T_EMC_EMP.USERNAME',
            field : 'userName',
        } ];
    },

    getReportChooseListColumns : function() {
        return [ {
            title : 'T_EMC_EMP.ID',
            field : 'id',
        }, {
            title : 'T_EMC_EMP.USERNAME',
            field : 'userName',
        }, {
            title : 'T_EMC_EMP.UNFINISHED_PROJECT_NUM',
            field : 'ext$.unfinishedprojectnum',
        }  ];
    },

    getTrainListColumns : function(){

        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : '培训开始时间',
            field : 'trainBeginTime',
            type : 'date',
            editor : true,
        }, {
            title : '培训结束时间',
            field : 'trainEndTime',
            type : 'date',
            editor : true,
        }, {
            title : 'T_EMC_EMP_TRAIN.TRAINCONTENT',
            field : 'trainContent',
            editor : true,
        }, {
            title : 'T_EMC_EMP_TRAIN.TRAINRESULT',
            validators : [ 'strLength[0,21]' ],
            field : 'trainResult',
            editor : true,
        }, {
            title : 'T_EMC_EMP_TRAIN.COURSECODE',
            validators : [ 'strLength[0,21]' ],
            field : 'courseCode',
            editor : true,
        }, {
            title : 'T_EMC_EMP_TRAIN.TRAINDEPT',
            validators : [ 'strLength[0,21]' ],
            field : 'trainDept',
            editor : true,
        }, {
            title : 'T_EMC_EMP_TRAIN.CREATEDTIME',
            field : 'createdTime'
        } ]
    }
});
