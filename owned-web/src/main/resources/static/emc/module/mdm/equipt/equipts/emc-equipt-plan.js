var emcEquiptPlan = Gikam.getPageObject({

    getMaintPlanListColumns : function() {
        var _this = this;
        return [ {
            checkbox : true
        }, {
            field : 'ext$.codename',
            title : 'T_EMC_EQUIPT_PLAN.MAINTEVENT'
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_EQUIPT_PLAN.VERIFYORG',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
            validators : [ 'notEmpty' ],
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PLAN.CYCLE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
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
                            maintEndDate : newEndDate
                        })
                    }
                }
            },
            width : 100
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIPT_PLAN.MAINTDATE',
            editor : true,
            type : 'date',
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
                            maintEndDate : newEndDate
                        })
                    }
                }
            },
            width : 120
        },  {
            field : 'maintEndDate',
            title : 'T_EMC_EQUIPT_PLAN.MAINTENDDATE',
            type : 'date'
        }, {
            field : 'warningDays',
            title : 'T_EMC_EQUIPT_PLAN.WARNINGDAYS',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0, 9]' ],
            isInteger : true,
            type : 'number',
            width : 125
        }, {
            field : 'gist',
            title : 'T_EMC_EQUIPT_PLAN.GIST',
            editor : true,
            type: 'select',
            search: true,
            category: 'sys',
            multiple : true,
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'certCategory',
            title : 'T_EMC_EQUIPT_PLAN.CERTCATEGORY',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'certificatetype'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PLAN.REMARK',
            editor : true,
            validators : [ 'strLength[0, 540]' ],
        } ];
    },

    getMaintPlanTab : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.PLAN',
            items : [ {
                type : 'grid',
                id : 'emc-equipt-plan-edit-list--grid',
                service : 'emcEquiptPlanServiceImpl',
                page : true,
                deleteFormatter : function(row) {
                    return row.ext$.codename || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.create('modal', {
                            width : 300,
                            height : 250,
                            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.MODAL.PLAN_INSERT',
                            url : emcEquipt.baseUrl + '/page/plan-add?equiptId=' + Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id
                                                    + '&pageType=equipt' ,
                            onAfterClose : function(data) {
                                if (Gikam.isEmpty(data)) {
                                    return;
                                }
                                this.warningDays = '';
                                Gikam.post( Gikam.printf(emcEquipt.baseUrl + '/{id}/maint-warningdays/queries', {
                                    id : data.maintId
                                }),{}).done(function (row){
                                    Gikam.getComp('emc-equipt-plan-edit-list--grid').insert(emcEquipt.baseUrl + "/plans", [ {
                                        equiptId : Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id,
                                        maintId : data.maintId,
                                        warningDays : row
                                    } ]);
                                })

                            }
                        })
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-plan-edit-list--grid').deleteRows(emcEquipt.baseUrl + '/plans');
                    }
                },{
                    type : 'button',
                    text : '上传文件',
                    icon : 'upload',
                    onClick : function() {
                        _this.uploadEquiptPlanFile();
                    }
                }],
                columns : emcEquiptPlan.getMaintPlanListColumns()
            } ]
        }

    },
    
    uploadEquiptPlanFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-equipt-plan-edit-list--grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : emcEquipt.baseUrl + '/page/equipt-plan-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

});
