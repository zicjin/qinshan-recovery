var coreTrain = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/train/trains',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'ext$.theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            type : 'link'
        }, {
            field : 'trainUser',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.TRAIN_USER',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="coreTrain.loadTrainUserSearchPage(' + row.id + ')">培训人员</a>';
            }
        }, {
            field : 'trainMethod',
            title : 'T_CORE_TRAIN.TRAINMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainMethod'
            },
        }, {
            field : 'location',
            title : 'T_CORE_TRAIN.LOCATION'
        }, {
            field : 'trainingMaterial',
            title : 'T_CORE_TRAIN.TRAININGMATERIAL'
        }, {
            field : 'assessmentMethod',
            title : 'T_CORE_TRAIN.ASSESSMENTMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreAssessmentMethod'
            },
        }, {
            field : 'ext$.examname',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.EXAM_NAME'
        }, {
            field : 'evaluation',
            title : 'T_CORE_TRAIN.EVALUATION'
        } ];
    },
    
    getSearchListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getActiveColumn(), {
            field : 'ext$.theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            type : 'link'
        }, {
            field : 'trainUser',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.TRAIN_USER',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="coreTrain.loadTrainUserSearchPage(' + row.id + ')">培训人员</a>';
            }
        }, {
            field : 'trainMethod',
            title : 'T_CORE_TRAIN.TRAINMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainMethod'
            },
        }, {
            field : 'location',
            title : 'T_CORE_TRAIN.LOCATION'
        }, {
            field : 'trainingMaterial',
            title : 'T_CORE_TRAIN.TRAININGMATERIAL'
        }, {
            field : 'assessmentMethod',
            title : 'T_CORE_TRAIN.ASSESSMENTMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreAssessmentMethod'
            },
        }, {
            field : 'ext$.examname',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.EXAM_NAME'
        }, {
            field : 'evaluation',
            title : 'T_CORE_TRAIN.EVALUATION'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'ext$.theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            type : 'choose',
            category : 'train-plan',
            preInsert : true,
            single : true,
            editable : false,
            onAfterChoose : function(rows, i) {
                if (rows) {
                    this.setData({
                        trainPlanId : rows[0].id,
                        ext$ : {
                            theme : rows[0].theme
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    trainPlanId : null,
                    ext$ : {
                        theme : null
                    }
                });
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'trainMethod',
            title : 'T_CORE_TRAIN.TRAINMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreTrainMethod'
            },
            preInsert : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'location',
            title : 'T_CORE_TRAIN.LOCATION',
            validators : [ 'notEmpty', 'strLength(0,90]' ]
        }, {
            field : 'trainTime',
            title : 'T_CORE_TRAIN.TRAINTIME',
            validators : [ 'notEmpty', 'strLength(0,90]' ]
        }, {
            field : 'trainingMaterial',
            title : 'T_CORE_TRAIN.TRAININGMATERIAL',
            validators : [ 'notEmpty', 'strLength(0,90]' ]
        }, {
            field : 'assessmentMethod',
            title : 'T_CORE_TRAIN.ASSESSMENTMETHOD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreAssessmentMethod'
            },
            validators : [ 'notEmpty' ],
            onChange : function(field, value) {
                if (value === 'testOnline') {
                    Gikam.getComp('core-train-detail-user-list-grid').setReadonly();
                } else {
                    Gikam.getComp('core-train-detail-user-list-grid').setReadonly(false);
                }
            }
        }, {
            field : 'ext$.examname',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.EXAM_NAME',
            type : 'choose',
            category : 'exam',
            preInsert : true,
            single : true,
            editable : false,
            onAfterChoose : function(rows, i) {
                if (rows) {
                    this.setData({
                        examId : rows[0].id,
                        ext$ : {
                            examname : rows[0].examName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    examId : null,
                    ext$ : {
                        examname : null
                    }
                });
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'evaluation',
            title : 'T_CORE_TRAIN.EVALUATION',
            validators : [ 'strLength(0,180]' ]
        } ];
    },

    getSignBaseInfoFormFields : function() {
        return [ {
            field : 'ext$.theme',
            title : 'T_CORE_TRAIN_PLAN.THEME',
            readonly : true
        }, {
            field : 'location',
            title : 'T_CORE_TRAIN.LOCATION',
            readonly : true
        }, {
            field : 'trainTime',
            title : 'T_CORE_TRAIN.TRAINTIME',
            readonly : true
        }, {
            field : 'ext$.userid',
            title : 'T_CORE_ORG_USER.USERID',
            validators : ['notEmpty']
        }, {
            field : 'ext$.username',
            title : 'T_CORE_ORG_USER.USERNAME',
            validators : ['notEmpty']
        } ];
    },

    loadTrainUserSearchPage : function(id) {
        Gikam.create('modal', {
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.TRAIN_USER',
            width : 1000,
            url : coreTrain.baseUrl + '/page/user-search' + Gikam.param({
                id : id
            })
        });
    },

    getUserSearchColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.orgname',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.ORGNAME'
        }, {
            field : 'userName',
            title : 'T_CORE_TRAIN_USER.USERNAME'
        } ];
    },

    getUserColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.orgname',
            title : 'CORE.MODULE.TRAIN.TRAIN.TITLE.ORGNAME'
        }, {
            field : 'userName',
            title : 'T_CORE_TRAIN_USER.USERNAME'
        }, {
            field : 'activatedFlag',
            title : 'T_CORE_TRAIN_USER.ACTIVATEDFLAG',
            formatter : function(i, v, row) {
                if (row.activatedFlag === '1') {
                    return Gikam.propI18N('GIKAM.SELECT.YES');
                } else if (row.activatedFlag === '0') {
                    return Gikam.propI18N('GIKAM.SELECT.NO');
                }
            }
        }, {
            field : 'score',
            title : 'T_CORE_TRAIN_USER.SCORE',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.ext$.assessmentmethod === 'testOnline') {
                    return false;
                } else {
                    return true;
                }
            },
            onBeforeUpdate : function() {
                var form = Gikam.getComp('core-train-detail-base-info-form');
                if (Gikam.isEmpty(form.getData().assessmentMethod)) {
                    Gikam.alert('CORE.MODULE.TRAIN.TRAIN.TIP.ASSESSMENTMETHOD_CANNOT_EMPTY');
                    return false;
                }
                return true;
            }
        } ];
    }
});