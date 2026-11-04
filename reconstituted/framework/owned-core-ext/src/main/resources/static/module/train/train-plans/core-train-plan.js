var coreTrainPlan = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/train/train-plans',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, coreTrainPlan.getStatusColumn(coreTrainPlan.workflow.dbTable), {
            field : 'trainCategory',
            title : 'T_CORE_TRAIN_PLAN.TRAINCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainCategory',
                codeClassId : 'biz'
            },
        }, {
            field : 'theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            type : 'link'
        }, {
            field : 'trainTime',
            title : 'T_CORE_TRAIN_PLAN.TRAINTIME',
        }, {
            field : 'trainScope',
            title : 'T_CORE_TRAIN_PLAN.TRAINSCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainScope'
            },
        }, {
            field : 'lecturer',
            title : 'T_CORE_TRAIN_PLAN.LECTURER'
        }, {
            field : 'trainPlanUser',
            title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.TRAIN_PLAN_USER',
            sort : false,
            filter : false,
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="coreTrainPlan.loadTrainPlanUserSearchPage(' + row.id + ')">培训人员</a>';
            }
        }, {
            field : 'remark',
            title : 'T_CORE_TRAIN_PLAN.REMARK'
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true,
        }, coreTrainPlan.getStatusColumn(coreTrainPlan.workflow.dbTable), {
            field : 'trainCategory',
            title : 'T_CORE_TRAIN_PLAN.TRAINCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainCategory',
                codeClassId : 'biz'
            }
        }, {
            field : 'theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            type : 'link'
        }, {
            field : 'trainTime',
            title : 'T_CORE_TRAIN_PLAN.TRAINTIME',
        }, {
            field : 'trainScope',
            title : 'T_CORE_TRAIN_PLAN.TRAINSCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainScope'
            },
        }, {
            field : 'lecturer',
            title : 'T_CORE_TRAIN_PLAN.LECTURER'
        }, {
            field : 'trainPlanUser',
            title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.TRAIN_PLAN_USER',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="coreTrainPlan.loadTrainPlanUserSearchPage(' + row.id + ')">培训人员</a>';
            }
        }, {
            field : 'remark',
            title : 'T_CORE_TRAIN_PLAN.REMARK'
        } ];
    },

    getChooseListColumns : function() {
        return [ coreTrainPlan.getStatusColumn(coreTrainPlan.workflow.dbTable), {
            field : 'trainCategory',
            title : 'T_CORE_TRAIN_PLAN.TRAINCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainCategory',
                codeClassId : 'biz'
            },
        }, {
            field : 'theme',
            title : 'T_CORE_TRAIN_PLAN.THEME'
        }, {
            field : 'trainTime',
            title : 'T_CORE_TRAIN_PLAN.TRAINTIME',
        }, {
            field : 'trainScope',
            title : 'T_CORE_TRAIN_PLAN.TRAINSCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainScope'
            }
        }, {
            field : 'lecturer',
            title : 'T_CORE_TRAIN_PLAN.LECTURER'
        }, {
            field : 'trainPlanUser',
            title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.TRAIN_PLAN_USER',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="coreTrainPlan.loadTrainPlanUserSearchPage(' + row.id + ')">培训人员</a>';
            }
        }, {
            field : 'remark',
            title : 'T_CORE_TRAIN_PLAN.REMARK'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'trainCategory',
            title : 'T_CORE_TRAIN_PLAN.TRAINCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainCategory',
                codeClassId : 'biz'
            },
            preInsert : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'trainScope',
            title : 'T_CORE_TRAIN_PLAN.TRAINSCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainScope'
            },
            preInsert : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            colspan : 3,
            preInsert : true,
            validators : [ 'notEmpty', 'strLength(0,180]' ]
        }, {
            field : 'trainTime',
            title : 'T_CORE_TRAIN_PLAN.TRAINTIME',
        }, {
            field : 'lecturer',
            title : 'T_CORE_TRAIN_PLAN.LECTURER',
        }, {
            field : 'remark',
            title : 'T_CORE_TRAIN_PLAN.REMARK',
            type : 'textarea',
            colspan : 3
        } ];
    },

    getUserColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.orgname',
            title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.ORGNAME',
        }, {
            field : 'userName',
            title : 'T_CORE_TRAIN_PLAN_USER.USERNAME',
        } ];
    },

    workflow : {
        dbTable : 'T_CORE_TRAIN_PLAN',
        columns : [ {
            field : 'id',
            title : 'T_CORE_TRAIN_PLAN.ID'
        } ]
    },

    loadTrainPlanUserSearchPage : function(id) {
        Gikam.create('modal', {
            title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.TRAIN_PLAN_USER',
            width : 1000,
            url : coreTrainPlan.baseUrl + '/page/user-search' + Gikam.param({
                id : id
            })
        });
    },

    getStatusColumn : function(dbTable) {
        return {
            field : 'status_SEQ',
            title : 'GIKAM.BPMN.FIELD.TITLE.STATUS',
            sort : false,
            type : 'select',
            search : 'true',
            items : [ {
                text : '',
                value : ''
            }, {
                text : Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.DRAFT'),
                value : 'draft'
            }, {
                text : Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.APPROVE'),
                value : 'approve'
            }, {
                text : Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.DRAFT_REJECTED'),
                value : 'draft_rejected'
            }, {
                text : Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.TO_IMPLEMENT'),
                value : 'to_implement'
            }, {
                text : Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.IMPLEMENTED'),
                value : 'implemented'
            } ],
            width : 100,
            formatter : function(index, value, row, text) {
                if (row['status'] === 'draft') {
                    return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.DRAFT') + '</a>';
                } else if (row['status'] === 'approve') {
                    return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.APPROVE') + '</a>';
                } else if (row['status'] === 'draft_rejected') {
                    return '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.DRAFT_REJECTED') + '</a>';
                } else if (row['status'] === 'to_implement') {
                    return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.TO_IMPLEMENT') + '</a>';
                } else if (row['status'] === 'implemented') {
                    return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('CORE.MODULE.TRAIN.TRAIN_PLAN.TITLE.STATUS.IMPLEMENTED') + '</a>';
                }
                return '';
            }
        }
    }
});