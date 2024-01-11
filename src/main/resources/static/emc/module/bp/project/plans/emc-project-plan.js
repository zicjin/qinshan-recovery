var emcProjectPlan = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/plans',

    getProjectPlanListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 100,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            width : 80,
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory'
        }, {
            field : 'nextTaskDate',
            title : 'T_EMC_PROJECT.NEXTTASKDATE'
        }, {
            field : 'startTime',
            title : 'T_EMC_PROJECT.STARTTIME',
            type : 'dateTime'
        }, {
            field : 'endTime',
            title : 'T_EMC_PROJECT.ENDTIME',
            type : 'dateTime'
        }, {
            field : 'cron',
            title : 'T_EMC_PROJECT.CRON',
            type : 'cron',
        } ];
    },

    getScheduleInfoFormFields : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            readonly : true,
        }, {
            field : 'startTime',
            title : 'T_EMC_PROJECT.STARTTIME',
            type : 'dateTime'
        }, {
            field : 'endTime',
            title : 'T_EMC_PROJECT.ENDTIME',
            type : 'dateTime'
        }, {
            field : 'cron',
            title : 'T_EMC_PROJECT.CRON',
            type : 'cron',
            validators : [ 'notEmpty' ],
        } ]
    },
    
    getPreInsertFields : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'projName',
                codeClassId : 'biz'
            },
            validators : [ 'strLength[0,42]' ],
            preInsert : true,
        }, {
            field : 'taskDate',
            title : 'T_EMC_PROJECT.TASKDATE',
            type : 'date',
            preInsert : true
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            preInsert : true
        } ];
    },

    getProjectQueryFormFields : function() {
        return [ {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            validators : [ 'strLength[0,42]' ],
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        createdById : rows[0].id,
                        createdByName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    createdById : '',
                    createdByName : '',
                });
            },
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'monitorTmplId',
            title : 'T_EMC_ORDER_TASK.ORDERCATEGORY',
            type : 'select',
            search : true,
            category : 'monitorTmpl',
        }, {
            field : 'taskYear',
            title : 'T_EMC_PROJECT.YEAR',
        }, {
            field : 'taskMonth',
            title : 'T_EMC_PROJECT.MONTH',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true
        } ];
    },

    getCopyFormFields : function() {
        return [ {
            field : 'taskDate',
            title : 'T_EMC_PROJECT.COPYTOTASKDATE',
            type : 'date',
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            validators : [ 'notEmpty' ]
        }, {
            field : 'copyQty',
            title : 'T_EMC_PROJECT.COPYQTY',
            value : 1,
            validators : [ 'notEmpty', 'integer', 'numRange[1,20]' ],
            type : 'number',
            hasArrow : true,
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            validators : [ 'notEmpty' ]
        }, ];
    },

});
