var emcSamplingPlan = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders',

    getProjectListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            formatter : function(i, v ,r) {
                var value = '<a href="javascript:;">' + v + '</a>';
                if(r.ext$.urgent === '1'){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + value;
                }else{
                    return value;
                }
            },
        }, {
            field : 'erpOrderNumber',
            title : 'T_EMC_PROJECT.ERPORDERNUMBER',
        }, {
            field : 'projName',
            title : 'T_EMC_FOLDER.PROJNAME',
        } ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }/*, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
            formatter : function(i, v ,r) {
                var value = '<a href="javascript:;">' + v + '</a>';
                if(r.ext$.urgent === '1'){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + value;
                }else{
                    return value;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_FOLDER.PROJNAME',
        }*/, {
            field : 'ext$.testorgname',
            title : 'T_EMC_FOLDER.TESTORGNAME',
            width : 250,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY',
            width : 250,
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'monitorPoint',
            single : true,
            onBeforeChoose : function() {
                return {
                    filter_projectId : this.getActivedRow().projId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorPointName : rows[0].monitorPointName,
                        monitorPointNo : rows[0].monitorPointNo,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointName : '',
                    monitorPointNo : '',
                });
            },
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            editor : true,
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ]
        }/*, {
            field : 'ext$.carnum',
            title : 'T_EMC_FOLDER.CARNUM',
            editor : true,
        }, {
            field : 'ext$.usedeptname',
            title : 'T_EMC_FOLDER.USEDEPTNAME',
            editor : true,
        }*/, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'emp',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    status_DIFFER : 'deprecated',
                    targetType : 'samplePlan',
                    sampleOrgId : this.getData()[index].sampleOrgId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        sceneUserId : rows[0].id,
                        sceneUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    sceneUserId : '',
                    sceneUserName : '',
                });
            },
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'emp',
            onBeforeChoose : function(index) {
                return {
                    status_DIFFER : 'deprecated',
                    targetType : 'samplePlan',
                    sampleOrgId : this.getData()[index].sampleOrgId,
                    sampleUserId : this.getData()[index].sampleUserId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleUserIds = [];
                    var sampleUserNames = [];
                    Gikam.each(rows, function(index, item) {
                        sampleUserIds.push(item.id);
                        sampleUserNames.push(item.userName);
                    });
                    this.setData({
                        index : index,
                        sampleUserId : sampleUserIds.join(','),
                        sampleUserName : sampleUserNames.join(','),
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    sampleUserId : '',
                    sampleUserName : '',
                });
            },
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
            editor : true
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            editor : true
        }, {
            field : 'river',
            title : 'T_EMC_FOLDER.RIVER',
            editor : true
        }, {
            field : 'tributary',
            title : 'T_EMC_FOLDER.TRIBUTARY',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK',
            editor : true
        } ];
    },

    getSampleInfoFields : function() {
        return [ {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        sceneUserName : rows[0].userName,
                        sceneUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    sceneUserName : '',
                    sceneUserId : ''
                });
            },
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows) {
                var sampleUserNames = [];
                var sampleUserIds = [];
                Gikam.each(rows, function(index, item) {
                    sampleUserNames.push(item.userName);
                    sampleUserIds.push(item.id);
                });
                this.setData({
                    sampleUserName : sampleUserNames.join(','),
                    sampleUserIds : sampleUserIds.join(',')
                });
            },
            onClean : function() {
                this.setData({
                    sampleUserName : '',
                    sampleUserIds : ''
                });
            },
        } ];
    },

    getPlanSampleDateColumns : function() {
        return [ {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            type : 'date',
            validators : [ 'notEmpty' ],
            editor : true
        } ];
    },

    getCarNumColumns : function() {
        return [ {
            field : 'carNum',
            title : 'T_EMC_CAR.CARNUM',
            validators : [ 'strLength[0, 12]' ],
            editor : true
        }];
    },

    getUseDeptNameColumns : function() {
        return [ {
            field : 'useDeptName',
            title : 'T_EMC_CAR.USEDEPT',
            editor : true
        }];
    },

    getSceneUserNameListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_USER.ID'
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME'
        } ]
    },

    getSampleUserNameListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_USER.ID'
        }, {
            field : 'userName',
            title : 'T_CORE_USER.USERNAME'
        } ]
    },

    getMonitorPointColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
        }, {
            field : 'monitorPointCategoryId',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORYID'
        } ]
    },

    getQueryListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            type : 'choose',
            category : 'monitorPoint',
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            type : 'date',
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            type : 'choose',
            category : 'emp',
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            type : 'choose',
            category : 'emp',
        } ];
    },

    /** 采样安排 - 样品 */
    getSamplePlanOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 70,
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 70,
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 90,
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            width : 100,
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO',
            width : 80,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, ];
    },

    /** 采样安排 - 容器信息 */
    getSamplePrepareOrderContainerColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderContainerNo',
            title : 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width : 150
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
            width : 150,
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
            width : 100,
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE',
            width : 100,
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION',
            width : 100,
        }, {
            field : 'ext$.sampleweight',
            title : 'T_EMC_ORDER_TASK.SAMPLEWEIGHT',
            width : 100,
            editor : true
        } ];
    },

    /** 批录修改容器信息 */
    getBatchInputContainerColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
            colspan : 1,
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
            colspan : 1,
            type : 'choose',
            category : 'containerCategory',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        containerCategoryId : rows[0].id,
                        containerCategoryCode : rows[0].containerCategoryId,
                        containerCategoryName : rows[0].containerCategoryName,
                    });
                }
            },
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
            colspan : 1,
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE',
            colspan : 1,
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION'
        } ];
    },

    getOrderTaskEquiptColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 110,
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
            width : 110,
        }, {
            field : 'ext$.equiptsid',
            title : 'T_EMC_ORDER_TASK.EQUIPTID',
            width : 90,
        }, {
            field : 'ext$.factoryno',
            title : 'T_EMC_ORDER_TASK.FACTORYNO',
            width : 110,
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_ORDER_TASK.EQUIPTNAME',
            width : 110,
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_ORDER_TASK.SPECIFICATION',
            width : 110,
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_ORDER_TASK.ANALYSISUSERNAME',
            width : 110,
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME',
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
        }, {
            field : 'assistEquiptName',
            title : 'T_EMC_ORDER_TASK.ASSISTEQUIPTID',
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_ORDER_TASK.PARTAKEUSERNAME',
        },{
            field : 'ext$.assignstatus',
            title : 'T_EMC_ORDER_TASK.ASSIGNSTATUS',
            type : 'select',
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_ORDER.REMARK',
        } ];
    },

    getAddVocColumns : function() {
        return [ {
            field : 'addnum',
            title : 'T_EMC_VOC_WEIGHTING.ADDVOC',
            validators : [ 'integer' ]
        } ];
    },

    getVocPageColumns : function() {
        return [ {
            checkbox : true
        },{
            field : 'vocNumber',
            title : 'T_EMC_VOC_WEIGHTING.VOCNUMBER'
        },{
            field : 'weighingUserName',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHINGUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                    	weighingUserId : rows[0].id,
                    	weighingUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    weighingUserId : '',
                	weighingUserName : '',
                });
            },
        },{
            field : 'emptyBottleWeight',
            title : 'T_EMC_VOC_WEIGHTING.EMPTYBOTTLEWEIGHT',
            editor : true,
        },{
            field : 'weightTime',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHTTIME',
            editor : true,
            type : 'date',
        },{
            field : 'projNo',
            title : 'T_EMC_VOC_WEIGHTING.PROJNO',
            width: 350,
            editor : true,
            editable : true,
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
                        targetType: 'samplePlaning',
        	            orgId_SB : workspace.user.ext$.parentorgid,
                        sample_DIFFER : '0',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }];
    },

    getVocPageColumns1 : function() {
        return [ {
            checkbox : true
        },{
            field : 'vocNumber',
            title : 'T_EMC_VOC_WEIGHTING.VOCNUMBER'
        },{
            field : 'weighingUserName',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHINGUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                    	weighingUserId : rows[0].id,
                    	weighingUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    weighingUserId : '',
                	weighingUserName : '',
                });
            },
        },{
            field : 'emptyBottleWeight',
            title : 'T_EMC_VOC_WEIGHTING.EMPTYBOTTLEWEIGHT',
            editor : true,
        },{
            field : 'weightTime',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHTTIME',
            editor : true,
            type : 'date',
        },{
            field : 'projNo',
            title : 'T_EMC_VOC_WEIGHTING.PROJNO',
            width: 350,
            editor : true,
            editable : true,
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
        	            orgId_SB : workspace.user.ext$.parentorgid,
                        sample_SEQ : '0',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }];
    },

    getBatchUpdateVocFormFields : function() {
        return [ {
            field : 'weighingUserName',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHINGUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	weighingUserId : rows[0].id,
                    	weighingUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    weighingUserId : '',
                	weighingUserName : '',
                });
            },
        },{
            field : 'emptyBottleWeight',
            title : 'T_EMC_VOC_WEIGHTING.EMPTYBOTTLEWEIGHT',
            editor : true,
        },{
            field : 'weightTime',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHTTIME',
            editor : true,
            type : 'date',
        },{
            field : 'projNo',
            title : 'T_EMC_VOC_WEIGHTING.PROJNO',
            width: 350,
            editor : true,
            editable : true,
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
                        targetType: 'samplePlaning',
        	            orgId_SB : workspace.user.ext$.parentorgid,
                        sample_DIFFER : '0',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }];
    },

    getAddSampleBottleColumns : function() {
        return [ {
            field : 'addnum',
            title : 'T_EMC_VOC_WEIGHTING.ADDSAMPLEBOTTLE',
            validators : [ 'integer' ]
        } ];
    },

    getBatchUpdateSampleBottleFormFields : function() {
        return [ {
            field : 'weighingUserName',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHINGUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        weighingUserId : rows[0].id,
                        weighingUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    weighingUserId : '',
                    weighingUserName : '',
                });
            },
        },{
            field : 'emptyBottleWeight',
            title : 'T_EMC_VOC_WEIGHTING.EMPTYBOTTLEWEIGHT',
            editor : true,
        },{
            field : 'weightTime',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHTTIME',
            editor : true,
            type : 'date',
        },{
            field : 'projNo',
            title : 'T_EMC_VOC_WEIGHTING.PROJNO',
            width: 350,
            editor : true,
            editable : true,
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
                    targetType: 'samplePlaning',
                    orgId_SB : workspace.user.ext$.parentorgid,
                    sample_DIFFER : '0',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }];
    },

    getBatchUpdateVocFormFields : function() {
        return [ {
            field : 'weighingUserName',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHINGUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        weighingUserId : rows[0].id,
                        weighingUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    weighingUserId : '',
                    weighingUserName : '',
                });
            },
        },{
            field : 'emptyBottleWeight',
            title : 'T_EMC_VOC_WEIGHTING.EMPTYBOTTLEWEIGHT',
            editor : true,
        },{
            field : 'weightTime',
            title : 'T_EMC_VOC_WEIGHTING.WEIGHTTIME',
            editor : true,
            type : 'date',
        },{
            field : 'projNo',
            title : 'T_EMC_VOC_WEIGHTING.PROJNO',
            width: 350,
            editor : true,
            editable : true,
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
                    targetType: 'samplePlaning',
                    orgId_SB : workspace.user.ext$.parentorgid,
                    sample_DIFFER : '0',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }];
    },

    getSampleBottlePageColumns : function() {
        return [ {
            checkbox : true
        },{
            field : 'sampleBottleNo',
            title : 'T_EMC_SAMPLE_BOTTLE.SAMPLEBOTTLENO'
        },{
            field : 'weighingUserName',
            title : 'T_EMC_SAMPLE_BOTTLE.WEIGHINGUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        weighingUserId : rows[0].id,
                        weighingUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    weighingUserId : '',
                    weighingUserName : '',
                });
            },
        },{
            field : 'emptyBottleWeight',
            title : 'T_EMC_SAMPLE_BOTTLE.EMPTYBOTTLEWEIGHT',
            editor : true,
        },{
            field : 'weightTime',
            title : 'T_EMC_SAMPLE_BOTTLE.WEIGHTTIME',
            editor : true,
            type : 'date',
        },{
            field : 'projNo',
            title : 'T_EMC_SAMPLE_BOTTLE.PROJNO',
            width: 350,
            editor : true,
            editable : true,
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgId_SB : workspace.user.ext$.parentorgid,
                    sample_SEQ : '0',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }];
    },

    getSampleBottleColumns : function() {
        return [ {
            field : 'sampleBottleNo',
            title : 'T_EMC_SAMPLE_BOTTLE.SAMPLEBOTTLENO'
        },{
            field : 'projNo',
            title : 'T_EMC_SAMPLE_BOTTLE.PROJNO'
        }, {
            field : 'emptyBottleWeight',
            title : 'T_EMC_SAMPLE_BOTTLE.EMPTYBOTTLEWEIGHT'
        }];
    },

    workflow : {
        dbTable : 'T_EMC_FOLDER',
        columns : [ {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO'
        } ]
    }

});
