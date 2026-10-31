var emcEquipt = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID'
        }, {
            field : 'ext$.assetno',
            title : '资产编号',
            editor : true,
            validators : [ 'strLength[0,22]' ]
        },{
            field : 'ext$.assetid',
            title : '资产ID',
        },{
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'maintenanceUserName',
            title : 'T_EMC_EQUIPT.MAINTENANCEUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var userIds = [], userNames = [];
                    rows.map(function(row) {
                        userIds.push(row.id);
                        userNames.push(row.userName);
                    });
                    this.setData({
                        index : index,
                        maintenanceUserId : userIds.join(),
                        maintenanceUserName : userNames.join()
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    maintenanceUserId : '',
                    maintenanceUserName : ''
                });
            },
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
            width : 85
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type : 'select',
            search : true,
            category : 'equiptCategory',
        }, {
            field : 'ext$.useddepartmentname',
            title : 'T_EMC_EQUIPT.USEDDEPARTMENTID',
        }, {
            field : 'sharing',
            title : 'T_EMC_EQUIPT.SHARING',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 85
        }, {
            field : 'maintain',
            title : 'T_EMC_EQUIPT.MAINTAIN',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            width : 85
        }, {
            field : 'sourceMethod',
            title : 'T_EMC_EQUIPT.SOURCEMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'sourceMethod',
            },
            width : 110
        }, {
            field : 'sourceDate',
            title : 'T_EMC_EQUIPT.SOURCEDATE',
            width : 100
        }, {
            field : 'equiptBelong',
            title : 'T_EMC_EQUIPT.EQUIPTBELONG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptBelong',
            },
            width : 85
        }];
    },

    getPreInsertFields : function () {
        return [ {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
            validators : [ 'unique[' + emcEquipt.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,30]' ],
            preInsert : true,
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'equiptCategory',
        // }, {
        //     field : 'ext$.useddepartmentname',
        //     title : 'T_EMC_EQUIPT.USEDDEPARTMENTID',
        //     preInsert : true,
        //     category : 'org-simple',
        //     type : 'choose',
        //     single : true,
        //     validators : [ 'strLength[0,10]' ],
        //     onBeforeChoose : function(rows) {
        //         return {
        //             orgCategory_SEQ : 'department'
        //         }
        //     },
        //     onAfterChoose : function(rows) {
        //         if (!Gikam.isEmpty(rows)) {
        //             this.setData({
        //                 usedDepartmentId : rows[0].id,
        //                 ext$ : {
        //                     useddepartmentname : rows[0].orgName,
        //                 }
        //             });
        //         }
        //     },
        //     onClean : function() {
        //         this.setData({
        //             usedDepartmentId : '',
        //             ext$ : {
        //                 useddepartmentname : ''
        //             }
        //         });
        //     },
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
            validators : [ 'strLength[0,10]' ]
        } ]
    },

    getEquiptInsertFields : function () {
        return [{
            fields : [{
                field : 'equiptCategoryId',
                title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
                type : 'select',
                search : true,
                category : 'equiptCategory',
                validators : [ 'notEmpty', ],
                onChange : function (field, value, rowIndex, oldValue, text) {
                    if ('采样设备'===text||'实验'===text||'现场'===text) {
                        Gikam.getComp('emc-equipt-edit-list-insert-form').hidePanelById('emc-equipt-edit-list-insert-form-equiptId');
                        Gikam.getComp('emc-equipt-edit-list-insert-form').removeFieldValidator( 'equiptId' , ['notEmpty']);
                    }else {
                        Gikam.getComp('emc-equipt-edit-list-insert-form').showPanelById('emc-equipt-edit-list-insert-form-equiptId');
                        Gikam.getComp('emc-equipt-edit-list-insert-form').addFieldValidator( 'equiptId' , ['notEmpty']);
                    }
                },
            }]
        },{
            id : 'emc-equipt-edit-list-insert-form-equiptId',
            fields: [{
                field : 'equiptId',
                title : 'T_EMC_EQUIPT.EQUIPTID',
                validators : [ 'unique[' + emcEquipt.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,30]' ],
            }]
        }, {
            fields: [{
                field : 'equiptName',
                title : 'T_EMC_EQUIPT.EQUIPTNAME',
                validators : [ 'strLength[0,42]' ]
            }]
        }]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
            validators : [ 'remote[' + emcEquipt.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,16]' ],
            preInsert : true
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'equiptEnglishName',
            title : 'T_EMC_EQUIPT.EQUIPTENGLISHNAME',
            validators : [ 'strLength[0,128]' ],
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
            validators : [ 'strLength[0,32]' ],
        }, {
            field : 'manufacturer',
            title : 'T_EMC_EQUIPT.MANUFACTURER',
            validators : [ 'strLength[0,85]' ]
        }, {
            field : 'factoryDate',
            title : 'T_EMC_EQUIPT.FACTORYDATE',
            type : 'date'
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT.FACTORYNO',
            validators : [ 'strLength[0,32]' ]
        }, {
            field : 'price',
            title : 'T_EMC_EQUIPT.PRICE',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'purchaseDate',
            title : 'T_EMC_EQUIPT.PURCHASEDATE',
            type : 'date'
        }, {
            field : 'arrivalDate',
            title : 'T_EMC_EQUIPT.ARRIVALDATE',
            type : 'date'
        }, {
            field : 'openDate',
            title : 'T_EMC_EQUIPT.OPENDATE',
            type : 'date'
        }, {
            field : 'installDate',
            title : 'T_EMC_EQUIPT.INSTALLDATE',
            type : 'date'
        }, {
            field : 'installUser',
            title : 'T_EMC_EQUIPT.INSTALLUSER',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'acceptDate',
            title : 'T_EMC_EQUIPT.ACCEPTDATE',
            type : 'date'
        }, {
            field : 'usedDate',
            title : 'T_EMC_EQUIPT.USEDDATE',
            type : 'date'
        }, {
            field : 'ext$.useddepartmentname',
            title : 'T_EMC_EQUIPT.USEDDEPARTMENTID',
            category : 'org-simple',
            type : 'choose',
            single : true,
            validators : [ 'strLength[0,10]' ],
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-equipt-edit-list-detail-form').setData({
                        usedDepartmentId : rows[0].id,
                        ext$ : {
                            useddepartmentname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-equipt-edit-list-detail-form').setData({
                    usedDepartmentId : '',
                    ext$ : {
                        useddepartmentname : ''
                    }
                });
            },
        }, {
            field : 'usedDepostId',
            title : 'T_EMC_EQUIPT.USEDDEPOSTID',
            category : 'org-simple',
            type : 'choose',
            single : true,
            validators : [ 'strLength[0,10]' ],
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'post'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-equipt-edit-list-detail-form').setData({
                        usedDepostId : rows[0].id,
                        ext$ : {
                            usedDepostName : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-equipt-edit-list-detail-form').setData({
                    usedDepostId : '',
                    ext$ : {
                        usedDepostName : ''
                    }
                });
            },
        }, {
            field : 'usedUserName',
            title : 'T_EMC_EQUIPT.USEDUSERNAME',
            category : 'shuttle-user',
            type : 'choose',
            validators : [ 'strLength[0,42]' ],
            onBeforeChoose : function() {
                return {
                    firstInputUserIds : this.getData().usedUserId,
                    parentorgid_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var usedUserIds = [], usedUserNames = [];
                    rows.map(function(row) {
                        usedUserIds.push(row.id);
                        usedUserNames.push(row.userName);
                    });

                    var userData = {
                        id : this.getData().id,
                        usedUserId : usedUserIds.join(),
                        usedUserName : usedUserNames.join()
                    };
                    this.setData(userData);
                }
            },
            onClean : function() {
                var userData = {
                    id : this.getData().id,
                    usedUserId : '',
                    usedUserName : ''
                };
                this.setData(userData); // 保存仪器使用人员ID
            },
        }, {
            field : 'ext$.maintainusername',
            title : 'T_EMC_EQUIPT.MAINTAINUSERID',
            type : 'choose',
            category : 'user-simple',
            single : true,
            validators : [ 'strLength[0,10]' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        maintainUserId : rows[0].id,
                        ext$ : {
                            maintainusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    maintainUserId : '',
                    ext$ : {
                        maintainusername : ''
                    }
                });
            },
        }, {
            field : 'address',
            title : 'T_EMC_EQUIPT.ADDRESS',
            validators : [ 'strLength[0,85]' ]
        }, {
            field : 'ext$.keepusername',
            title : 'T_EMC_EQUIPT.KEEPUSERID',
            type : 'choose',
            category : 'user-simple',
            single : true,
            validators : [ 'strLength[0,10]' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        keepUserId : rows[0].id,
                        ext$ : {
                            keepusername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    keepUserId : '',
                    ext$ : {
                        keepusername : ''
                    }
                });
            },
        }, {
            field : 'state',
            title : 'T_EMC_EQUIPT.STATE',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'func',
            title : 'T_EMC_EQUIPT.FUNC',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'tecIndex',
            title : 'T_EMC_EQUIPT.TECINDEX',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'testRange',
            title : 'T_EMC_EQUIPT.TESTRANGE',
            validators : [ 'strLength[0,360]' ]
        }, {
            field : 'ext$.accuracylevel',
            title : 'T_EMC_EQUIPT.ACCURACYLEVEL',
            validators : [ 'strLength[0,360]' ]
        }, {
            field : 'ext$.resolve',
            title : 'T_EMC_EQUIPT.RESOLVE',
            validators : [ 'strLength[0,360]' ]
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type : 'select',
            search : true,
            category : 'equiptCategory',
        }, {
            field : 'installOrg',
            title : 'T_EMC_EQUIPT.INSTALLORG',
            validators : [ 'strLength[0,85]' ]
        }, {
            field : 'installPhone',
            title : 'T_EMC_EQUIPT.INSTALLPHONE',
            validators : [ 'phone' ]
        }, {
            field : 'serviceContactName',
            title : 'T_EMC_EQUIPT.SERVICECONTACTNAME',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'servicePhone',
            title : 'T_EMC_EQUIPT.SERVICEPHONE',
            validators : [ 'phone' ]
        }, {
            field : 'sharing',
            title : 'T_EMC_EQUIPT.SHARING',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'maintain',
            title : 'T_EMC_EQUIPT.MAINTAIN',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'sourceMethod',
            title : 'T_EMC_EQUIPT.SOURCEMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'sourceMethod',
            }
        }, {
            field : 'sourceDate',
            title : 'T_EMC_EQUIPT.SOURCEDATE',
            type : 'date'
        }, {
            field : 'equiptBelong',
            title : 'T_EMC_EQUIPT.EQUIPTBELONG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptBelong',
            }
        }, {
            field : 'ext$.room',
            title : 'T_EMC_ENV_RECORD.ROOM',
            preInsert : true,
            validators : [ 'strLength[0,36]' ],
            type : 'choose',
            category : 'room',
            single : true,
            onAfterChoose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                this.setData({
                    index : index,
                    room : rows[0].roomNo,
                    standardTemperature : rows[0].standardTemperature,
                    standardHumidity : rows[0].standardHumidity,
                    thermometerNo : rows[0].thermometerNo,
                    ext$ : {
                        roomid : rows[0].id
                    }
                });
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    room : '',
                    standardTemperature : '',
                    standardHumidity : '',
                    thermometerNo : '',
                    ext$ : {
                        roomid : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            multiple : true,
        },{
            field : 'equiptRecord',
            title : '是否需要检定',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'sceneEquiptNo',
            title : '现场仪器采集编号',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT.REMARK',
            colspan : 4,
            validators : [ 'strLength[0,85]' ]
        },  ];
    },

    getEuiptCertListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'empId',
            title : 'T_EMC_EQUIP_CERT.EMPID',
        }, {
            field : 'userName',
            title : 'T_EMC_EQUIP_CERT.USERNAME',
        }, {
            field : 'userDept',
            title : 'T_EMC_EQUIP_CERT.USERDEPT',
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIP_CERT.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIP_CERT.CREATEDTIME',
        } ];
    },

    getItemFormFields : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testcategorydesc',
            title : 'T_EMC_EQUIPT_TEST_METHOD.TESTCATEGORYID',
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_EQUIPT_TEST_METHOD.TESTNAME',
        }, {
            field : 'testId',
            title : 'T_EMC_EQUIPT_TEST_METHOD.TESTID',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_EQUIPT_TEST_METHOD.METHODID',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_EQUIPT_TEST_METHOD.METHODDESC',
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_EQUIPT_TEST_METHOD.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
        }, {
            field : 'curveNo',
            title : 'T_EMC_RUN.SELECTRUN',
            width : 180,
            type : 'choose',
            category : 'curve',
            onBeforeChoose : function(index) {
                var row = Gikam.getComp('emc-equipt-edit-list-item-grid').getData()[index];
                return {
                    equiptId : row.equiptId,
                    testId : row.testId,
                    methodId : row.methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var curveIds = '';
                    var curveNos = '';
                    rows.map(function(item, i) {
                        curveIds += rows[i].id + ',';
                        curveNos += rows[i].curveNo + ',';
                    });

                    this.setData({
                        index : index,
                        curveId : curveIds.substring(0, curveIds.length - 1),
                        curveNo : curveNos.substring(0, curveNos.length - 1)
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    curveId : '',
                    curveNo : ''
                });
            },
            editor : true
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT.FACTORYNO',
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type : 'select',
            search : true,
            category : 'equiptCategory'
        }, {
            field : 'ext$.useddepartmentname',
            title : 'T_EMC_EQUIPT.USEDDEPARTMENTID',
        }, {
            field : 'sharing',
            title : 'T_EMC_EQUIPT.SHARING',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'maintain',
            title : 'T_EMC_EQUIPT.MAINTAIN',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'sourceMethod',
            title : 'T_EMC_EQUIPT.SOURCEMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'sourceMethod',
            },
        }, {
            field : 'sourceDate',
            title : 'T_EMC_EQUIPT.SOURCEDATE',
        } ];
    },

    getCaliChooseListColumns : function() {
        return [ {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT.FACTORYNO',
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type : 'select',
            search : true,
            category : 'equiptCategory'
        }, {
            field: 'ext$.codename',
            title: 'T_EMC_EQUIPT_MAINT.SERVICE_NAME'
        },{
            field : 'ext$.maintenddate',
            title : 'T_EMC_EQUIPT.MAINTENDDATE',
        }, {
            field : 'ext$.cycle',
            title : 'T_EMC_EQUIPT.CYCLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'cycle',
            },
        }, {
            field : 'ext$.verifyorg',
            title : 'T_EMC_EQUIPT.VERIFYORG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
        }, {
            field : 'ext$.gist',
            title : 'T_EMC_EQUIPT.GIST',
            type : 'select',
            search: true,
            category : 'sys',
            param : {
                codeCategoryId : 'inspectionStandard',
            },
        }, {
            field : 'ext$.resolve',
            title : 'T_EMC_EQUIPT.RESOLVE',
        }];
    },

    getSimpleChooseListColumns : function() {
        return [ {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT.FACTORYNO',
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            }
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type : 'select',
            search : true,
            category : 'equiptCategory'
        }/*,{
            field : 'isOnStock',
            title : 'T_EMC_EQUIPT.ISONSTOCK',
            type : 'select',
            category :  'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        } */,{
            field : 'remark',
            title : 'T_EMC_EQUIPT.REMARK'
        } ];
    },

    getDCUConfFormFields : function() {
        return [ {
            field : 'scriptCategory',
            title : 'T_EMC_EQUIPT.SCRIPTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'scriptCategory',
            },
            onChange : function(field, value, rowIndex, oldValue, text) {
                var scriptDesc = Gikam.getComp('emc-equipt-edit-list-dcu-conf-form').getField('scriptDesc');
                scriptDesc.value = '';
                scriptDesc.options.category = value;
            }
        }, {
            field : 'scriptDesc',
            title : 'T_EMC_EQUIPT.ACQSCRIPTID',
            type : 'choose',
            category : '',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgId_SEQ : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        acqScriptId : rows[0].id,
                        scriptDesc : rows[0].scriptDesc,
                    })
                }
            },
            onClean : function() {
                this.setData({
                    acqScriptId : '',
                    scriptDesc : ''
                });
            },
        }, {
            field : 'scriptDescs',
            title : 'T_EMC_EQUIPT.ACQSCRIPTIDS',
            type : 'choose',
            category : '',
            single : false,
            onBeforeChoose : function() {
                return {
                    orgId_SEQ : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    let acqScriptIds = rows.map(item => item.id).join();
                    let scriptDescs = rows.map(item => item.scriptDesc).join()
                    this.setData({
                        acqScriptIds : acqScriptIds,
                        scriptDescs : scriptDescs
                    })
                }
            },
            onClean : function() {
                this.setData({
                    acqScriptIds : '',
                    scriptDescs : ''
                });
            },
        }, {
            field : 'catalog',
            title : 'T_EMC_EQUIPT.CATALOG',
            validators : [ 'remote[' + IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts/action/validate-unique]', 'strLength[0,25]' ],
        } ]
    },

    getTestItemGridFields : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_EQUIPT_TEST_ITEM.TESTID',
            width : 100,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_EQUIPT_TEST_ITEM.TESTNAME',
        }, {
            field : 'ext$.testclass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_EQUIPT_TEST_ITEM.METHODNAME',
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_EQUIPT_TEST_ITEM.ITEMNAME',
        }, {
            field : 'ext$.itemaliasname',
            title : 'T_EMC_EQUIPT_TEST_ITEM.ITEMALIASNAME',
        }, {
            field : 'matchItemName',
            title : 'T_EMC_EQUIPT_TEST_ITEM.MATCHITEMNAME',
            editor : true,
            validators : [ 'strLength(0,60]' ],
        }, {
            field : 'matchResultName',
            title : 'T_EMC_EQUIPT_TEST_ITEM.MATCHRESULTNAME',
            editor : true,
            validators : [ 'strLength(0,60]' ],
            type : 'choose',
            category : 'easResult',
            single : true,
            onBeforeChoose : function() {
                let data = Gikam.getComp('emc-equipt-edit-list-dcu-conf-form').getData();
                return {
                    scriptId_SEQ : (data && data.acqScriptId) || 1
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        id : this.getActivedRow().id,
                        matchResultName : rows[0].fieldName,
                        easResultId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    matchResultName : '',
                    easResultId : ''
                });
            },
        }, {
            field : 'ext$.unitname',
            title : 'T_EMC_EQUIPT_TEST_ITEM.UNITNAME',
            width : 80,
        } ]
    },

    getCurveListParam : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_CURVE.STATUS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'publishStatus',
            },
            width : 100
        }, {
            field : 'curveNo',
            title : 'T_EMC_CURVE.CURVENO',
            width : 100
        }, {
            field : 'equation',
            title : 'T_EMC_CURVE.EQUATION',
            formatter : function(i, v, row) {
                var intercept = row.intercept;
                var slope = row.slope;
                var coefficient = row.coefficient;
                if (!Gikam.isEmpty(intercept) && !Gikam.isEmpty(slope) && !Gikam.isEmpty(coefficient)) {
                    intercept = intercept.startsWith('-') ? intercept : '+' + intercept;
                    var equation = 'y=' + slope + 'x' + intercept + ',r=' + coefficient;
                    return equation;
                }
            },
            width : 200
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_CURVE.EQUIPTID',
            width : 100
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_CURVE.EQUIPTNAME'
        }, {
            field : 'testName',
            title : 'T_EMC_CURVE.TESTNAME'
        }, {
            field : 'methodName',
            title : 'T_EMC_CURVE.METHODNAME'
        }, {
            field : 'itemName',
            title : 'T_EMC_CURVE.ITEMNAME',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_CURVE.CREATEDBYNAME',
            width : 100
        }, {
            field : 'createdTime',
            title : 'T_EMC_CURVE.CREATEDTIME',
            width : 100
        }, {
            field : 'confDate',
            title : 'T_EMC_CURVE.CONFDATE',
            width : 100
        }, {
            field : 'expireDate',
            title : 'T_EMC_CURVE.EXPIREDATE',
            width : 100
        } ];
    },

    getEquiptPlanSearchColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_EQUIPT.VERIFYORG',
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT.CYCLE',
        }, {
            field : 'maintLastDate',
            title : 'T_EMC_EQUIPT.MAINTLASTDATE',
        }, {
            field : 'maintPlanDate',
            title : 'T_EMC_EQUIPT.MAINTPLANDATE',
        }, {
            field : 'keepUserId',
            title : 'T_EMC_EQUIPT.KEEPUSERID',
        }, {
            field : 'keepUserName',
            title : 'T_EMC_EQUIPT.KEEPUSERNAME',
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT.REMARK',
        }  ]
    },

    getEquiptPreCheckPlanCheckSearchColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'maintLastDate',
            title : 'T_EMC_EQUIPT.MAINTLASTDATE',
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIPT_PLAN.MAINTDATE',
        }, {
            field : 'checkIndex',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKINDEX',
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY',
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
        }, {
            field : 'checkUserId',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKUSERID',
        }, {
            field : 'checkUserName',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKUSERNAMES',
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT.REMARK',
        }  ]
    },

    getMainTainRepairListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT',
            editor : true,
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN',
            editor : true,
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE',
            editor : true,
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT',
            editor : true,
        }];
    },

    getMainTainRepairInsertListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE'
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN',
            validators : [ 'strLength[0,66]' ]
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT',
            validators : [ 'strLength[0,66]' ]
        }];
    },

    getMainTainRepairSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE'
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN',
            validators : [ 'strLength[0,66]' ]
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT',
            validators : [ 'strLength[0,66]' ]
        }];
    },

    getEquiptUserSearchColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'users',
            title : 'T_EMC_EQUIPT.USERS',
        }];
    },


});
