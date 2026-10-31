var emcEquiptPerCheckPlan = Gikam.getPageObject({
	
    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt/equipt-per-check',

    getPerCheckListColumns : function() {
        var _this = this;
        return [ {
            checkbox : true
        }, {
            field : 'ext$.codename',
            title : 'T_EMC_EQUIPT_PER_CHECK.MAINTEVENT'
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
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
            title : 'T_EMC_EQUIPT_PER_CHECK.MAINTDATE',
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
            title : 'T_EMC_EQUIPT_PER_CHECK.MAINTENDDATE',
            type : 'date'
        }, {
            field : 'warningDays',
            title : 'T_EMC_EQUIPT_PER_CHECK.WARNINGDAYS',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0, 9]' ],
            isInteger : true,
            type : 'number',
            width : 125
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            editor : true,
            type: 'select',
            category: 'sys',
            multiple : true,
            search: true,
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'checkEquipt',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT',
            editor : true
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY',
            editor : true,
            multiple : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkWay',
            },
        }, {
            field : 'checkUserName',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKUSERNAMES',
            editor : true,
            category : 'user-simple',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        checkUserId : rows[0].id,
                        checkUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    checkUserId : '',
                    checkUserName : ''
                });
            },
        } , {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
            editor : true,
            validators : [ 'strLength[0, 540]' ],
        } ];
    },

    getPerCheckPlanTab : function() {
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.PER_CHECK',
            items : [ {
                type : 'grid',
                id : 'emc-equipt-per-check-plan-edit-list-grid',
                service : 'emcEquiptPerCheckPlanServiceImpl',
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
                                + '&pageType=during' ,
                            onAfterClose : function(data) {
                                if (Gikam.isEmpty(data)) {
                                    return;
                                }
                                this.warningDays = '';
                                Gikam.post( Gikam.printf(emcEquipt.baseUrl + '/{id}/maint-warningdays/queries', {
                                    id : data.maintId
                                }),{}).done(function (row){
                                    Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid').insert(emcEquipt.baseUrl + "/per-check-plan", [ {
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
                        Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid').deleteRows(emcEquipt.baseUrl + '/per-check-plan');
                    }
                } ],
                columns : emcEquiptPerCheckPlan.getPerCheckListColumns()
            } ]
        }

    },

    
    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            checkbox : true
        }, {
            field : 'ext$.codename',
            title : 'T_EMC_EQUIPT_PER_CHECK.MAINTEVENT'
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
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
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.MAINTDATE',
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
        },  {
            field : 'maintEndDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.MAINTENDDATE',
            type : 'date'
        }, {
            field : 'warningDays',
            title : 'T_EMC_EQUIPT_PER_CHECK.WARNINGDAYS',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0, 9]' ],
            isInteger : true,
            type : 'number',
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            editor : true,
            type: 'select',
            category: 'sys',
            multiple : true,
            search: true,
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'checkEquipt',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT',
            editor : true
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY',
            editor : true,
            multiple : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkWay',
            },
        }, {
            field : 'checkUserName',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKUSERNAMES',
            editor : true,
            category : 'user-simple',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        checkUserId : rows[0].id,
                        checkUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    checkUserId : '',
                    checkUserName : ''
                });
            },
        } , {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
            editor : true,
            validators : [ 'strLength[0, 540]' ],
        } ];
    },
});
