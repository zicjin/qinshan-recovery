var coreUserTrain = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreUserTrain.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO',
            type : 'select',
            category : 'annual-plan'
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            type : 'select',
            category : 'userTrainCategory'
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME'
        }, {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            type : 'link'
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            type : 'dateTime'
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            type : 'dateTime'
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME'
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE'
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            validators : ['notEmpty', 'strLength[0,15]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO',
            validators : [ 'strLength[0,15]' ],
            type : 'select',
            category : 'annual-plan',
            firstBlank : false,
            param : {
                planCategory : 'empTrainPlan'
            },
            onChange : function(field, value, rowIndex, oldValue, text) {
                var comp = Gikam.getComp('core-user-train-detail-base-info-form');
                workspace.window.showMask(true);
                Gikam.post(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/emp-train-plan/queries', Gikam.getJsonWrapper({
                    f : {
                        annualPlanId_SEQ : value
                    },
                })).done(function(item) {
                    var data = Gikam.parseJson(item);
                    comp.setData({'index' : comp.getData().id, 'annualPlanId': item.rows[0].annualPlanId}, true);
                    comp.setData({'index' : comp.getData().id, 'trainPlace': item.rows[0].trainPlace}, true);
                    comp.setData({'index' : comp.getData().id, 'trainContent': item.rows[0].trainContent}, true);
                    comp.setData({'index' : comp.getData().id, 'classHour': item.rows[0].period}, true);
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            validators : ['notEmpty', 'strLength[0,10]' ],
            type : 'select',
            category : 'userTrainCategory'
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME',
            validators : ['notEmpty', 'strLength[0,170]' ]
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            type : 'dateTime',
            validators : ['notEmpty']
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            type : 'dateTime',
            validators : ['notEmpty']
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR',
            validators : ['notEmpty', 'number' ]
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME',
            validators : ['notEmpty', 'strLength[0,21]' ]
        }, {
            field : 'speakerOrgName',
            title : 'T_CORE_USER_TRAIN.SPEAKERORGNAME',
            validators : ['notEmpty', 'strLength[0,85]' ]
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE',
            validators : ['notEmpty', 'strLength[0,42]' ],
            colspan : 3
        }, {
            field : 'traineeName',
            title : 'T_CORE_USER_TRAIN.TRAINEENAME',
            validators : ['notEmpty', 'strLength[0,682]' ],
            colspan : 3,
            type : 'choose',
            category : 'mult-user',
            single : false,
            onBeforeChoose : function(index) {
                var comp = Gikam.getComp('core-user-train-detail-base-info-form');
                var ids = comp.getData().traineeId;
                if (Gikam.isNotEmpty(ids)) {
                    return {
                        id_DIFFER : ids.split(','),
                    };
                }
            },
            onClean : function(index) {
                var form = Gikam.getComp('core-user-train-detail-base-info-form');
                form.setData({'index' : index, 'traineeId': ''}, true);
                form.setData({'index' : index, 'traineeName': ''}, true);
            },
            onAfterChoose : function(rows, index) {
                var comp = Gikam.getComp('core-user-train-detail-base-info-form');
                var ids = comp.getData().traineeId;
                var names = comp.getData().traineeName;
                var idList = new Array();
                var nameList = new Array();
                if (Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row){
                        idList[index] = row.id;
                        nameList[index] = row.userName;
                    });
                    comp.setData({'index' : index, 'traineeId': (Gikam.isEmpty(ids)?'':ids + ',') + idList.join(',')}, true);
                    comp.setData({'index' : index, 'traineeName': (Gikam.isEmpty(names)?'':names + ',') + nameList.join(',')}, true);
                }
            }
        }, {
            field : 'trainContent',
            title : 'T_CORE_USER_TRAIN.TRAINCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'trainMethod',
            title : 'T_CORE_USER_TRAIN.TRAINMETHOD',
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK',
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreUserTrain.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO'
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            type : 'select',
            category : 'userTrainCategory'
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME'
        }, {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            type : 'link'
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            type : 'dateTime'
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            type : 'dateTime'
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME'
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE'
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK'
        } ];
    },

    getAuditInfoFormFields : function() {
        return [ {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            validators : ['notEmpty', 'strLength[0,12]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO',
            validators : [ 'strLength[0,12]' ],
            readonly : true,
            type : 'choose',
            category : 'annual-plan'
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            validators : ['notEmpty', 'strLength[0,10]' ],
            readonly : true,
            type : 'select',
            category : 'userTrainCategory'
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,170]' ]
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            readonly : true,
            type : 'dateTime',
            validators : ['notEmpty']
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            readonly : true,
            type : 'dateTime',
            validators : ['notEmpty']
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR',
            readonly : true,
            validators : ['notEmpty', 'number' ]
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,21]' ]
        }, {
            field : 'speakerOrgName',
            title : 'T_CORE_USER_TRAIN.SPEAKERORGNAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,85]' ]
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,42]' ],
            colspan : 3
        }, {
            field : 'traineeName',
            title : 'T_CORE_USER_TRAIN.TRAINEENAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,682]' ],
            colspan : 3
        }, {
            field : 'trainContent',
            title : 'T_CORE_USER_TRAIN.TRAINCONTENT',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'trainMethod',
            title : 'T_CORE_USER_TRAIN.TRAINMETHOD',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK',
            readonly : true,
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getEvluateInfoFormFields : function() {
        return [ {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            validators : ['notEmpty', 'strLength[0,12]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO',
            validators : [ 'strLength[0,12]' ],
            readonly : true,
            type : 'choose',
            category : 'annual-plan'
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            validators : ['notEmpty', 'strLength[0,10]' ],
            readonly : true,
            type : 'select',
            category : 'userTrainCategory'
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,170]' ]
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            readonly : true,
            type : 'dateTime',
            validators : ['notEmpty']
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            readonly : true,
            type : 'dateTime',
            validators : ['notEmpty']
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR',
            readonly : true,
            validators : ['notEmpty', 'number' ]
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,21]' ]
        }, {
            field : 'speakerOrgName',
            title : 'T_CORE_USER_TRAIN.SPEAKERORGNAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,85]' ]
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,42]' ],
            colspan : 3
        }, {
            field : 'traineeName',
            title : 'T_CORE_USER_TRAIN.TRAINEENAME',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,682]' ],
            colspan : 3
        }, {
            field : 'trainContent',
            title : 'T_CORE_USER_TRAIN.TRAINCONTENT',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'trainMethod',
            title : 'T_CORE_USER_TRAIN.TRAINMETHOD',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'trainEvaluation',
            title : 'T_CORE_USER_TRAIN.TRAINEVALUATION',
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK',
            readonly : true,
            validators : [ 'strLength[0,170]' ],
            colspan : 3
        } ];
    },
    
    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreUserTrain.workflow.dbTable), {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO'
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            type : 'select',
            category : 'userTrainCategory'
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME'
        }, {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            type : 'link'
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            type : 'dateTime'
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            type : 'dateTime'
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME'
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE'
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK'
        } ];
    },

    getSearchInfoFormFields : function() {
        return [ {
            field : 'trainNo',
            title : 'T_CORE_USER_TRAIN.TRAINNO',
            validators : ['notEmpty', 'strLength[0,12]' ],
            readonly : true
        }, {
            field : 'annualPlanNo',
            title : 'T_CORE_USER_TRAIN.ANNUALPLANNO',
            validators : [ 'strLength[0,12]' ],
            type : 'choose',
            category : 'annual-plan',
            readonly : true
        }, {
            field : 'trainCategory',
            title : 'T_CORE_USER_TRAIN.TRAINCATEGORY',
            validators : ['notEmpty', 'strLength[0,10]' ],
            type : 'select',
            category : 'userTrainCategory',
            readonly : true
        }, {
            field : 'courseName',
            title : 'T_CORE_USER_TRAIN.COURSENAME',
            validators : ['notEmpty', 'strLength[0,170]' ],
            readonly : true
        }, {
            field : 'startDate',
            title : 'T_CORE_USER_TRAIN.STARTDATE',
            type : 'dateTime',
            validators : ['notEmpty'],
            readonly : true
        }, {
            field : 'endDate',
            title : 'T_CORE_USER_TRAIN.ENDDATE',
            type : 'dateTime',
            validators : ['notEmpty'],
            readonly : true
        }, {
            field : 'classHour',
            title : 'T_CORE_USER_TRAIN.CLASSHOUR',
            validators : ['notEmpty', 'number' ],
            readonly : true
        }, {
            field : 'speakerName',
            title : 'T_CORE_USER_TRAIN.SPEAKERNAME',
            validators : ['notEmpty', 'strLength[0,21]' ],
            readonly : true
        }, {
            field : 'speakerOrgName',
            title : 'T_CORE_USER_TRAIN.SPEAKERORGNAME',
            validators : ['notEmpty', 'strLength[0,85]' ],
            readonly : true
        }, {
            field : 'trainPlace',
            title : 'T_CORE_USER_TRAIN.TRAINPLACE',
            validators : ['notEmpty', 'strLength[0,42]' ],
            colspan : 3,
            readonly : true
        }, {
            field : 'traineeName',
            title : 'T_CORE_USER_TRAIN.TRAINEENAME',
            validators : ['notEmpty', 'strLength[0,682]' ],
            colspan : 3,
            readonly : true
        }, {
            field : 'trainContent',
            title : 'T_CORE_USER_TRAIN.TRAINCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'trainMethod',
            title : 'T_CORE_USER_TRAIN.TRAINMETHOD',
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor',
            readonly : true
        }, {
            field : 'trainEvaluation',
            title : 'T_CORE_USER_TRAIN.TRAINEVALUATION',
            validators : ['notEmpty', 'strLength[0,1333]' ],
            colspan : 3,
            type : 'ckeditor'
        }, {
            field : 'remark',
            title : 'T_CORE_USER_TRAIN.REMARK',
            validators : [ 'strLength[0,170]' ],
            colspan : 3,
            readonly : true
        } ];
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/user-trains',

    workflow : {
        dbTable : 'T_CORE_USER_TRAIN',
        columns : [ {
            field : 'id',
            title : 'T_CORE_USER_TRAIN.ID'
        } ]
    }

});
