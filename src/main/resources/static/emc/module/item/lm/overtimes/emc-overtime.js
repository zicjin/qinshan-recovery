var emcOvertime = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOvertime.workflow.dbTable), {
            field : 'orgName',
            title : 'T_EMC_OVERTIME.ORGNAME',
        }, {
            field : 'userName',
            title : 'T_EMC_OVERTIME.USERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        id : Gikam.getComp('emc-overtime-edit-list-grid').getActivedRow().id,
                        userId : rows[0].id,
                        userName : rows[0].userName,
                        orgId : rows[0].orgId,
                        orgName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-overtime-edit-list-grid').getActivedRow().id,
                    userId : '',
                    userName : '',
                    orgId : '',
                    orgName : '',

                });
            },
        },

        {
            field : 'beginTime',
            title : 'T_EMC_OVERTIME.BEGINTIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'endTime',
            title : 'T_EMC_OVERTIME.ENDTIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'reason',
            title : 'T_EMC_OVERTIME.REASON',
            editor : true,
            validators : [ 'strLength[0, 1080]' ],
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOvertime.workflow.dbTable), {
            field : 'orgName',
            title : 'T_EMC_OVERTIME.ORGNAME',
        }, {
            field : 'userName',
            title : 'T_EMC_OVERTIME.USERNAME',
        },
        {
            field : 'beginTime',
            title : 'T_EMC_OVERTIME.BEGINTIME',
        }, {
            field : 'endTime',
            title : 'T_EMC_OVERTIME.ENDTIME',
        }, {
            field : 'reason',
            title : 'T_EMC_OVERTIME.REASON',
        } ];
    },
    
    
    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOvertime.workflow.dbTable), {
            field : 'orgName',
            title : 'T_EMC_OVERTIME.ORGNAME',
        }, {
            field : 'userName',
            title : 'T_EMC_OVERTIME.USERNAME',
        },

        {
            field : 'beginTime',
            title : 'T_EMC_OVERTIME.BEGINTIME',
        }, {
            field : 'endTime',
            title : 'T_EMC_OVERTIME.ENDTIME',
        }, {
            field : 'reason',
            title : 'T_EMC_OVERTIME.REASON',

        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/lm/overtimes',

    workflow : {
        dbTable : 'T_EMC_OVERTIME',
        columns : [ {
            field : 'id',
            title : 'T_EMC_OVERTIME.ID'
        } ]
    }
});
