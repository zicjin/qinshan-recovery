var emcTrainCourse = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/train-courses',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'courseCode',
            title : 'T_EMC_TRAIN_COURSE.COURSECODE'
        }, {
            field : 'courseName',
            title : 'T_EMC_TRAIN_COURSE.COURSENAME',
            editor : true,
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'trainCategory',
            title : 'T_EMC_TRAIN_COURSE.TRAINCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'trainCategory',
            },
            editor : true
        }, {
            field : 'trainContentCategory',
            title : 'T_EMC_TRAIN_COURSE.TRAINCONTENTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'trainContentCategory',
            },
            editor : true
        }, {
            field : 'trainHour',
            title : 'T_EMC_TRAIN_COURSE.TRAINHOUR',
            editor : true,
            validators : [ 'integer' ]
        }, {
            field : 'lecturer',
            title : 'T_EMC_TRAIN_COURSE.LECTURER',
            editor : true,
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'fee',
            title : 'T_EMC_TRAIN_COURSE.FEE',
            editor : true,
            validators : [ 'number' ]
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {}, {}, {}, {}, {
            field : 'remark',
            title : 'T_EMC_TRAIN_COURSE.REMARK',
            type : 'textarea',
            height : 100,
            validators : [ 'strLength[0,180]' ]
        }, ];
    },

    getTestMethodListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TC_TEST_METHOD.METHODNAME'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TC_TEST_METHOD.TESTNAME'
        }, ];
    },

});
