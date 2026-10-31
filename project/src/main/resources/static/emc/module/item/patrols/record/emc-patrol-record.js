var emcPatrolRecord = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'patrolDate',
            title : 'T_EMC_PATROL.PATROLDATE',
        }, {
            field : 'patrolWeekday',
            title : 'T_EMC_PATROL.PATROLWEEKDAY',
        }, {
            field : 'patrolTime',
            title : 'T_EMC_PATROL.PATROLTIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'patrolUserName',
            title : 'T_EMC_PATROL.PATROLUSERID',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    // Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().patrolUserId
                    // =rows[0].id;

                    this.setData({
                        id : Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().id,
                        patrolUserId : rows[0].id,
                        patrolUserName : rows[0].userName,

                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().id,
                    patrolUserId : '',
                    patrolUserName : '',

                });
            },

        }, {
            field : 'problem',
            title : 'T_EMC_PATROL.PROBLEM',
            editor : true,
            validators : [ 'strLength[0, 540]' ],
        }, {
            field : 'handleUserNames',
            title : 'T_EMC_PATROL.HANDLEUSERNAMES',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    // Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().patrolUserId
                    // =rows[0].id;
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().id,
                        handleUserIds : ids,
                        handleUserNames : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-patrol-edit-list-grid').getActivedRow().id,
                    handleUserIds : '',
                    handleUserNames : ""
                });
            },
        }, {
            field : 'handleReback',
            title : 'T_EMC_PATROL.HANDLEREBACK',
            editor : false,
        }, {
            field : 'handleResult',
            title : 'T_EMC_PATROL.HANDLERESULT',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'handleResult'
            }
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getWorkflowColumn(emcPatrol.workflow.dbTable), {
            field : 'patrolDate',
            title : 'T_EMC_PATROL.PATROLDATE'
        }, {
            field : 'patrolWeekday',
            title : 'T_EMC_PATROL.PATROLWEEKDAY'
        }, {
            field : 'patrolTime',
            title : 'T_EMC_PATROL.PATROLTIME'
        }, {
            field : 'patrolUserId',
            title : 'T_EMC_PATROL.PATROLUSERID'
        }, {
            field : 'problem',
            title : 'T_EMC_PATROL.PROBLEM'
        }, {
            field : 'handleUserIds',
            title : 'T_EMC_PATROL.HANDLEUSERIDS'
        }, {
            field : 'handleUserNames',
            title : 'T_EMC_PATROL.HANDLEUSERNAMES'
        }, {
            field : 'handleReback',
            title : 'T_EMC_PATROL.HANDLEREBACK'
        }, {
            field : 'handleResult',
            title : 'T_EMC_PATROL.HANDLERESULT'
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'patrolDate',
            title : 'T_EMC_PATROL.PATROLDATE'
        }, {
            field : 'patrolWeekday',
            title : 'T_EMC_PATROL.PATROLWEEKDAY'
        }, {
            field : 'patrolTime',
            title : 'T_EMC_PATROL.PATROLTIME'
        }, {
            field : 'patrolUserId',
            title : 'T_EMC_PATROL.PATROLUSERID'
        }, {
            field : 'problem',
            title : 'T_EMC_PATROL.PROBLEM'
        }, {
            field : 'handleUserIds',
            title : 'T_EMC_PATROL.HANDLEUSERIDS'
        }, {
            field : 'handleUserNames',
            title : 'T_EMC_PATROL.HANDLEUSERNAMES'
        }, {
            field : 'handleReback',
            title : 'T_EMC_PATROL.HANDLEREBACK'
        }, {
            field : 'handleResult',
            title : 'T_EMC_PATROL.HANDLERESULT'
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'patrolDate',
            title : 'T_EMC_PATROL.PATROLDATE'
        }, {
            field : 'patrolWeekday',
            title : 'T_EMC_PATROL.PATROLWEEKDAY'
        }, {
            field : 'patrolTime',
            title : 'T_EMC_PATROL.PATROLTIME'
        }, {
            field : 'patrolUserId',
            title : 'T_EMC_PATROL.PATROLUSERID'
        }, {
            field : 'problem',
            title : 'T_EMC_PATROL.PROBLEM'
        }, {
            field : 'handleUserIds',
            title : 'T_EMC_PATROL.HANDLEUSERIDS'
        }, {
            field : 'handleUserNames',
            title : 'T_EMC_PATROL.HANDLEUSERNAMES'
        }, {
            field : 'handleReback',
            title : 'T_EMC_PATROL.HANDLEREBACK'
        }, {
            field : 'handleResult',
            title : 'T_EMC_PATROL.HANDLERESULT'
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/emc-patrols',

    workflow : {
        dbTable : 'T_EMC_PATROL',
        columns : [ {
            field : 'id',
            title : 'T_EMC_PATROL.ID'
        } ]
    }
});
