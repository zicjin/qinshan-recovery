var emcLeaveReplenish = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/leave-replenishs',

    getBaseInfoFormFields : function() {
        return [ Gikam.status.getBpmnColumn(emcLeaveReplenish.workflow.dbTable), {
            field : 'applicant',
            title : 'T_EMC_LEAVE_REPLENISH.APPLICANT',
            preInsert : true,
            readonly : true,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_REPLENISH.ORGDUTY',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_REPLENISH.STARTDATE',
            preInsert : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_REPLENISH.ENDDATE',
            preInsert : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_REPLENISH.DAYS',
            preInsert : true,
            validators : [ 'notEmpty', 'number' ],
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveDays',
                codeClassId : 'biz'
            },
        } ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcLeaveReplenish.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_LEAVE_REPLENISH.ID',
            width : 100,
            validators : [ 'notEmpty', 'strLength[0,18]' ]
        }, {
            field : 'applicant',
            title : 'T_EMC_LEAVE_REPLENISH.APPLICANT',
            width : 100,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_REPLENISH.ORGDUTY',
            width : 160,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_REPLENISH.STARTDATE',
            width : 120,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_REPLENISH.ENDDATE',
            width : 120,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_REPLENISH.DAYS',
            width : 100,
            editor : true,
            validators : [ 'notEmpty', 'number' ],
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveDays',
                codeClassId : 'biz'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_LEAVE_REPLENISH.REMARK',
            width : 200,
            editor : true,
            validators : [ 'strLength[0,300]' ]
        }

        ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcLeaveReplenish.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_LEAVE_REPLENISH.ID',
            width : 100,
            validators : [ 'notEmpty', 'strLength[0,18]' ]
        }, {
            field : 'applicant',
            title : 'T_EMC_LEAVE_REPLENISH.APPLICANT',
            width : 100,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_REPLENISH.ORGDUTY',
            width : 160,
            type : 'text',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_REPLENISH.STARTDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_REPLENISH.ENDDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_REPLENISH.DAYS',
            width : 100,
            type : 'number',
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'remark',
            title : 'T_EMC_LEAVE_REPLENISH.REMARK',
            width : 200,
            validators : [ 'strLength[0,300]' ]
        } ];
    },

    getSearchListColumns : function(operType) {
        return [ Gikam.status.getBpmnColumn(emcLeaveReplenish.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_LEAVE_REPLENISH.ID',
            width : 100,
            validators : [ 'notEmpty', 'strLength[0,18]' ]
        }, {
            field : 'applicant',
            title : 'T_EMC_LEAVE_REPLENISH.APPLICANT',
            width : 100,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_REPLENISH.ORGDUTY',
            width : 160,
            type : 'text',
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_REPLENISH.STARTDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_REPLENISH.ENDDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_REPLENISH.DAYS',
            width : 100,
            type : 'number',
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'remark',
            title : 'T_EMC_LEAVE_REPLENISH.REMARK',
            width : 200,
            validators : [ 'strLength[0,300]' ]
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_LEAVE_REPLENISH',
        columns : [ {
            field : 'id',
            title : 'T_EMC_LEAVE_REPLENISH.ID'
        } ]
    }
});
