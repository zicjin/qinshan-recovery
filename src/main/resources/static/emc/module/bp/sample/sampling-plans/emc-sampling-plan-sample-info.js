emcSamplingPlan.sampleInfoPage = {

    getSaveBtn : function() {
        var _this = this;
        return  {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var planSampleDate = Gikam.getComp('emc-sampling-plan-sample-info-paln-sample-date-form').getData();
                    /*var carNum = Gikam.getComp('emc-sampling-plan-sample-info-paln-carnum-form').getData();
                    var useDeptName = Gikam.getComp('emc-sampling-plan-sample-info-paln-usedeptname-form').getData();*/
                    var sampleUserNameSelection = Gikam.getComp('emc-sampling-plan-sample-info-sample-user-edit-list-grid').getSelections();
                    var sceneUserNameSelection = Gikam.getComp('emc-sampling-plan-sample-info-scene-user-edit-list-grid').getSelections();
                    var selections = Gikam.getComp('emc-sampling-plan-edit-list-grid').getSelections();
                    var sceneUserName = "";
                    var sceneUserId = "";
                    var sampleUserName = "";
                    var sampleUserId = "";
                    //只修改计划采样日期和车牌号、站室
                    if (Gikam.isNotEmpty(planSampleDate)&& Gikam.isEmpty(sceneUserNameSelection) && Gikam.isEmpty(sampleUserNameSelection)) {
                        var saveData = selections.map(function(task) {
                            return {
                                id : task.id,
                                planSampleDate : planSampleDate.planSampleDate,
                                /*carNum : carNum.carNum,
                                useDeptName : useDeptName.useDeptName*/
                            };
                        });
                    }
                    if(Gikam.isNotEmpty(sceneUserNameSelection) && Gikam.isEmpty(sampleUserNameSelection)) {
                        //修改现场负责人和采样日期和车牌号、站室
                        var sceneUserName = sceneUserNameSelection[0].userName;
                        var sceneUserId = sceneUserNameSelection[0].id;
                        var saveData = selections.map(function(task) {
                            return {
                                id : task.id,
                                sceneUserName : sceneUserName,
                                sceneUserId : sceneUserId,
                                planSampleDate : planSampleDate.planSampleDate,
                                /*carNum : carNum.carNum,
                                useDeptName : useDeptName.useDeptName*/
                            };
                        });

                    }else if (Gikam.isNotEmpty(sampleUserNameSelection) && Gikam.isEmpty(sceneUserNameSelection)) {
                        //修改采样人和采样日期和车牌号、站室
                        if(Gikam.isNotEmpty(sampleUserNameSelection)){
                            Gikam.each(sampleUserNameSelection, function(index, emp) {
                                if (Gikam.isEmpty(sampleUserName)) {
                                    sampleUserName += emp.userName;
                                } else {
                                    sampleUserName += ',' + emp.userName;
                                }

                                if (Gikam.isEmpty(sampleUserId)) {
                                    sampleUserId += emp.id;
                                } else {
                                  sampleUserId += ',' + emp.id;
                                }
                            });
                        }
                        var saveData = selections.map(function(task) {
                            return {
                                id : task.id,
                                sampleUserName : sampleUserName,
                                sampleUserId : sampleUserId,
                                planSampleDate : planSampleDate.planSampleDate,
                                /*carNum : carNum.carNum,
                                useDeptName : useDeptName.useDeptName*/
                            };
                         });
                    }else if (Gikam.isNotEmpty(sampleUserNameSelection) && Gikam.isNotEmpty(sceneUserNameSelection)) {
                        //现场负责人和采样人和采样日期和车牌号、站室都可修改
                        var sceneUserName = sceneUserNameSelection[0].userName;
                        var sceneUserId = sceneUserNameSelection[0].id;
                        if(Gikam.isNotEmpty(sampleUserNameSelection)){
                            Gikam.each(sampleUserNameSelection, function(index, emp) {
                                if (Gikam.isEmpty(sampleUserName)) {
                                    sampleUserName += emp.userName;
                                } else {
                                    sampleUserName += ',' + emp.userName;
                                }
                                if (Gikam.isEmpty(sampleUserId)) {
                                  sampleUserId += emp.id;
                                } else {
                                  sampleUserId += ',' + emp.id;
                                }
                            });
                        }
                        var saveData = selections.map(function(task) {
                            return {
                                id : task.id,
                                sampleUserName : sampleUserName,
                                sampleUserId : sampleUserId,
                                sceneUserName : sceneUserName,
                                sceneUserId : sceneUserId,
                                planSampleDate : planSampleDate.planSampleDate,
                                /*carNum : carNum.carNum,
                                useDeptName : useDeptName.useDeptName*/

                            };
                        });
                    }

                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcFolderServiceImpl', saveData ])).done(function() {
                        selections.map(function(row) {
                            Gikam.getComp('emc-sampling-plan-edit-list-grid').refreshRowById(row.id);
                        });
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                        Gikam.getLastModal().close();
                    });
                }
            }
    },

    getSceneUserNameGridParam : function() {
        var _this = this, columns = emcSamplingPlan.getSceneUserNameListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.SCENEUSERNAME',
                items : [ {
                    type : 'grid',
                    generalButtonGroup : false,
                    filterOpen : true,
                    id : 'emc-sampling-plan-sample-info-scene-user-edit-list-grid',
                    url : '/secure/emc/module/mdm/emp/emps/queries/choosable',
                    requestData : {
                        status_DIFFER : 'deprecated',
                        targetType : 'samplePlan',
                        sampleOrgId : _this.param.ids.split(',')[0]
                    },
                    order : {
                        'SAMPLEDEPARTMENT' : 'desc',
                        'DEPARTMENTID' : 'asc',
                        'ID' : 'asc'
                    },
                    columns : columns,
                    onRendered : function() {
                        var grid = Gikam.getComp('emc-sampling-plan-sample-info-scene-user-edit-list-grid');
                        grid.refreshGroup({
                            fields : [ 'ext$.departmentid' ],
                            formatter : function(row) {
                                return row.ext$.departmentname;
                            },
                        });
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(_this.param.firstDate)) {
                            Gikam.getComp('emc-sampling-plan-sample-info-paln-sample-date-form').setData({planSampleDate : _this.param.firstDate},false);
                        }
                        if (!Gikam.isEmpty(_this.param.sceneUserName)) {
                            Gikam.getComp('emc-sampling-plan-sample-info-scene-user-edit-list-grid').selectRowById(_this.param.sceneUserName);
                        }
                    }
                }, ]
            } ]
        }
    },

    getSampleUserNameGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.SAMPLEUSERNAME',
                items : [ {
                    type : 'grid',
                    generalButtonGroup : false,
                    filterOpen : true,
                    id : 'emc-sampling-plan-sample-info-sample-user-edit-list-grid',
                    url : '/secure/emc/module/mdm/emp/emps/queries/choosable',
                    requestData : {
                        status_DIFFER : 'deprecated',
                        targetType : 'samplePlan',
                        sampleOrgId : _this.param.ids.split(',')[0]
                    },
                    order : {
                        'SAMPLEDEPARTMENT' : 'desc',
                        'DEPARTMENTID' : 'asc',
                        'ID' : 'asc'
                    },
                    columns : emcSamplingPlan.getSampleUserNameListColumns(),
                    onRendered : function() {
                        var grid = Gikam.getComp('emc-sampling-plan-sample-info-sample-user-edit-list-grid');
                        grid.refreshGroup({
                            fields : [ 'ext$.departmentid' ],
                            formatter : function(row) {
                                return row.ext$.departmentname;
                            },
                        });
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(_this.param.sampleUserName)) {
                            var userIds=_this.param.sampleUserName;
                            var arr=userIds.split(",");
                            arr.forEach(function(item) {
                                Gikam.getComp('emc-sampling-plan-sample-info-sample-user-edit-list-grid').selectRowById(item);
                            });
                        }
                    }
                }, ]
            } ]
        }
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sampling-plan-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 60,
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ {
                            type : 'form',
                            columns:1,
                            id : 'emc-sampling-plan-sample-info-paln-sample-date-form',
                            fields : emcSamplingPlan.getPlanSampleDateColumns(),
                            autoSave : false,
                        }/*,  {
                            type : 'form',
                            columns:1,
                            id : 'emc-sampling-plan-sample-info-paln-carnum-form',
                            fields : emcSamplingPlan.getCarNumColumns(),
                            autoSave : false,
                        },  {
                            type : 'form',
                            columns:1,
                            id : 'emc-sampling-plan-sample-info-paln-usedeptname-form',
                            fields : emcSamplingPlan.getUseDeptNameColumns(),
                            autoSave : false,
                        }*/]
                    },
                    center : {
                        padding : '10px',
                        items : [ this.getSaveBtn() ]
                    }
                } ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        // 现场负责人
                        items : [ this.getSceneUserNameGridParam() ]
                    },
                    center : {
                        // 采样人
                        items : [ this.getSampleUserNameGridParam() ]
                    }
                } ]
            }

        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }

}
