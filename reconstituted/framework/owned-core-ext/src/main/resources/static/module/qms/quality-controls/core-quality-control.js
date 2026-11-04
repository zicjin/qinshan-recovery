var coreQualityControl = Gikam.getPageObject({

    getListColumns : function() {
        return [{
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreQualityControl.workflow.dbTable), {
            field : 'qcNo',
            title : 'T_CORE_QUALITY_CONTROL.QCNO',
            type : 'link'
        }, {
            field : 'qcType',
            title : 'T_CORE_QUALITY_CONTROL.QCTYPE',
            type : 'select',
            category : 'qc-type-category'
        }, {
            field : 'qcCorpName',
            title : 'T_CORE_QUALITY_CONTROL.QCCORPNAME'
        }, {
            field : 'carryDate',
            title : 'T_CORE_QUALITY_CONTROL.CARRYDATE'
        }, {
            field : 'qcContent',
            title : 'T_CORE_QUALITY_CONTROL.QCCONTENT'
        }, {
            field : 'qcBasis',
            title : 'T_CORE_QUALITY_CONTROL.QCBASIS'
        }, {
            field : 'qcResult',
            title : 'T_CORE_QUALITY_CONTROL.QCRESULT'
        }, {
            field : 'remark',
            title : 'T_CORE_QUALITY_CONTROL.REMARK'
        }];
    },

    loadDetailPage : function(id, status) {
        workspace.window.load(this.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            status : status
        }));
    },

    getBaseInfoFormFields : function() {
        return [{
            field : 'qcNo',
            title : 'T_CORE_QUALITY_CONTROL.QCNUMBERORDER',
            readonly : true,
            validators : ['notEmpty', 'strLength[0,12]']
        }, {
            field : 'qcType',
            type : 'select',
            firstBlank : false,
            title : 'T_CORE_QUALITY_CONTROL.QCTYPE',
            category : 'qc-type-category',
            validators : ['notEmpty', 'strLength[0,20]']
        }, {
            field : 'ext$.itemname',
            title : 'T_CORE_QUALITY_CONTROL.QCPLAN',
            type : 'choose',
            single : true,
            category : 'qc-plan',
            targetFields : [{
                'ext$.itemname' : 'itemName'
            }, {
                qcPlanId : 'id'
            }]
        }, {
            field : 'carryDate',
            type : 'date',
            title : 'T_CORE_QUALITY_CONTROL.CARRYDATE',
            validators : ['notEmpty']
        }, {
            field : 'qcCorpName',
            title : 'T_CORE_QUALITY_CONTROL.QCCORPNAME',
            validators : ['notEmpty', 'strLength[0,90]']
        }, {
            field : 'qcContent',
            title : 'T_CORE_QUALITY_CONTROL.QCCONTENT',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'qcBasis',
            title : 'T_CORE_QUALITY_CONTROL.QCBASIS',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'qcDeadline',
            type : 'date',
            title : 'T_CORE_QUALITY_CONTROL.QCDEADLINE',
            validators : ['notEmpty']
        }, {
            field : 'reviewUserName',
            title : 'T_CORE_QUALITY_CONTROL.REVIEWUSERNAME',
            type : 'choose',
            category : 'user',
            colspan : 3,
            onAfterChoose : function(rows) {
                var users = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        users[index] = row.userName;
                    });
                    this.setData({
                        reviewUserName : users.join('，')
                    });
                }
            },
            targetFields : [{
                reviewUserId : 'id'
            }],
            validators : ['notEmpty']
        }, {
            field : 'leaderUserName',
            type : 'choose',
            category : 'user',
            onAfterChoose : function(rows) {
                var users = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        users[index] = row.userName;
                    });
                    this.setData({
                        leaderUserName : users.join('，')
                    });
                }
            },
            title : 'T_CORE_QUALITY_CONTROL.LEADERUSERNAME',
            targetFields : [{
                leaderUserId : 'id'
            }],
            validators : ['notEmpty']
        }, {
            field : 'remark',
            title : 'T_CORE_QUALITY_CONTROL.REMARK',
            validators : ['strLength[0,200]'],
            type : 'textarea',
            height : 100,
            colspan : 4
        }];
    },

    getQmCommentFormFields : function() {
        return [{
            field : 'evnCondition',
            title : 'T_CORE_QUALITY_CONTROL.EVNCONDITION',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'resourceConfig',
            title : 'T_CORE_QUALITY_CONTROL.RESOURCECONFIG',
            validators : ['notEmpty', 'strLength[0,1333]']
        }, {
            field : 'planFeasibility',
            title : 'T_CORE_QUALITY_CONTROL.PLANFEASIBILITY',
            validators : ['notEmpty', 'strLength[0,90]']
        }, {
            field : 'qcResult',
            title : 'T_CORE_QUALITY_CONTROL.QCRESULT',
            validators : ['notEmpty', 'strLength[0,1333]'],
            type : 'ckeditor',
            colspan : 3,
            height : 400
        }];
    },

    getRegistControlItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.ORDERNO',
            editor : true
        }, {
            field : 'testParam',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTPARAM',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'testUserName',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user',
            onAfterChoose : function(rows, rowIndex) {
                var users = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        users[index] = row.userName;
                    });
                    this.setData({
                        index : rowIndex,
                        testUserName : users.join('，')
                    });
                }
            },
            targetFields : [{
                testUserId : 'id'
            }],
            validators : ['notEmpty']
        }, {
            field : 'equNo',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.EQUNO',
            editor : true
        }, {
            field : 'standardMtrl',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.STANDARDMTRL',
            editor : true
        }, {
            field : 'testStandard',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTSTANDARD',
            editor : true
        }, {
            field : 'matName',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.MATNAME',
            editor : true
        }, {
            field : 'folderNo',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.FOLDERNO',
            editor : true
        }, {
            field : 'testReq',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTREQ',
            editor : true,
        }, {
            field : 'remark',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.REMARK',
            editor : true
        }];
    },

    getExecuteControlItemListColumns : function() {
        return [{
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.ORDERNO',
        }, {
            field : 'testParam',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTPARAM',
            validators : ['notEmpty']
        }, {
            field : 'testUserName',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTUSERNAME',
            type : 'choose',
            category : 'user',
            onAfterChoose : function(rows, rowIndex) {
                var users = [];
                if(Gikam.isNotEmpty(rows)) {
                    Gikam.each(rows, function(index, row) {
                        users[index] = row.userName;
                    });
                    this.setData({
                        index : rowIndex,
                        testUserName : users.join('，')
                    });
                }
            },
            targetFields : [{
                testUserId : 'id'
            }],
            validators : ['notEmpty']
        }, {
            field : 'equNo',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.EQUNO',
        }, {
            field : 'standardMtrl',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.STANDARDMTRL',
        }, {
            field : 'testStandard',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTSTANDARD',
        }, {
            field : 'matName',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.MATNAME',
        }, {
            field : 'folderNo',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.FOLDERNO',
            editor : true
        }, {
            field : 'testResult',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTRESULT',
            editor : true,
            validators : ['notEmpty']
        }, {
            field : 'testReq',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.TESTREQ',
        }, {
            field : 'remark',
            title : 'T_CORE_QUALITY_CONTROL_ITEM.REMARK',
            editor : true
        }];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/quality-controls',

    workflow : {
        dbTable : 'T_CORE_QUALITY_CONTROL',
        columns : [{
            field : 'id',
            title : 'T_CORE_QUALITY_CONTROL.ID'
        }]
    }

});
