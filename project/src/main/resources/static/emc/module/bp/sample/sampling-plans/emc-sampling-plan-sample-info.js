emcSamplingPlan.sampleInfoPage = {

    getSaveBtn: function () {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.SAVE',
            icon: 'save',
            onClick: function () {
                var planSampleDate = Gikam.getComp('emc-sampling-plan-sample-info-paln-sample-date-form').getData();
                var sceneUserNameSelection = Gikam.getComp('emc-sampling-plan-sample-info-scene-user-edit-list-grid').getSelections();
                var sampleUserNameSelection = Gikam.getComp('emc-sampling-plan-sample-info-sample-user-edit-list-grid').getSelections();

                var saveData = {
                    sceneUserId: sceneUserNameSelection[0].id,
                    sceneUserName: sceneUserNameSelection[0].userName,
                    sampleUserId: sampleUserNameSelection.map(e => e.id).join(','),
                    sampleUserName: sampleUserNameSelection.map(e => e.userName).join(','),
                    planSampleDate: planSampleDate.planSampleDate,
                };
                Gikam.getLastModal().close(saveData);
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
                        sampleOrgId : (Gikam.isEmpty(_this.param.ids) ? '' : _this.param.ids.split(',')[0])
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
                            Gikam.getComp('emc-sampling-plan-sample-info-paln-sample-date-form').setData({
                                planSampleDate: _this.param.firstDate,
                            }, false);
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
                        sampleOrgId : (Gikam.isEmpty(_this.param.ids) ? '' : _this.param.ids.split(',')[0])
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
                        width : '80%',
                        items : [ {
                            type : 'form',
                            columns:2,
                            id : 'emc-sampling-plan-sample-info-paln-sample-date-form',
                            fields : emcSamplingPlan.getPlanSampleDateColumns(),
                            autoSave : false,
                            titleWidthAuto : true,
                        }]
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
