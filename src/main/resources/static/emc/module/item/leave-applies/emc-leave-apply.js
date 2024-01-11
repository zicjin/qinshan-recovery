var emcLeaveApply = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/leave-applies',

    getBaseInfoFormFields : function() {
        return [ Gikam.status.getBpmnColumn(emcLeaveApply.workflow.dbTable), {
            field : 'applicant',
            title : 'T_EMC_LEAVE_APPLY.APPLICANT',
            preInsert : true,
            readonly : true,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'type',
            title : 'T_EMC_LEAVE_APPLY.TYPE',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveapplyform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_APPLY.ORGDUTY',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_APPLY.STARTDATE',
            preInsert : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_APPLY.ENDDATE',
            preInsert : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_APPLY.DAYS',
            preInsert : true,
            validators : [ 'notEmpty', 'number' ]
        } ];
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcLeaveApply.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_LEAVE_APPLY.ID',
            width : 100,
            type : 'link',
            validators : [ 'notEmpty', 'strLength[0,18]' ]
        }, {
            field : 'applicant',
            title : 'T_EMC_LEAVE_APPLY.APPLICANT',
            width : 80,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'type',
            title : 'T_EMC_LEAVE_APPLY.TYPE',
            width : 80,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveapplyform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_APPLY.ORGDUTY',
            width : 120,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_APPLY.STARTDATE',
            width : 120,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_APPLY.ENDDATE',
            width : 120,
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_APPLY.DAYS',
            width : 80,
            editor : true,
            type : 'number',
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'needCredentials',
            title : 'T_EMC_LEAVE_APPLY.NEEDCREDENTIALS',
            width : 80,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            validators : [ 'notEmpty' ],
            onChange : function(field, value) {
                var grid = Gikam.getComp('emc-leave-apply-edit-list-grid');
                var data = grid.getActivedRow();
                var rowIndex = grid.getCheckedRowIndex();
                if (value !== '1') {
                    grid.setData({
                        index : rowIndex[0],
                        id : data.id,
                        needCredentials : value,
                        credentials : ''
                    });
                }
            }
        }, {
            field : 'credentials',
            title : 'T_EMC_LEAVE_APPLY.CREDENTIALS',
            width : 120,
            editor : true,
            validators : [ 'strLength[0,60]' ],
            onBeforeEditorRender : function() {
                var grid = Gikam.getComp('emc-leave-apply-edit-list-grid');
                var data = grid.getActivedRow();
                var rowIndex = grid.getCheckedRowIndex();
                if (data && data.needCredentials === '1') {
                    return true;
                } else {
                    return false;
                }
            }
        }, {
            field : 'visitPlace',
            title : 'T_EMC_LEAVE_APPLY.VISITPLACE',
            width : 100,
            editor : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'stayDays',
            title : 'T_EMC_LEAVE_APPLY.STAYDAYS',
            width : 100,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'remark',
            title : 'T_EMC_LEAVE_APPLY.REMARK',
            width : 200,
            editor : true,
            validators : [ 'strLength[0,300]' ]
        }

        ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcLeaveApply.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_LEAVE_APPLY.ID',
            width : 100,
            type : 'link',
            validators : [ 'notEmpty', 'strLength[0,18]' ]
        }, {
            field : 'applicant',
            title : 'T_EMC_LEAVE_APPLY.APPLICANT',
            width : 100,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'type',
            title : 'T_EMC_LEAVE_APPLY.TYPE',
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveapplyform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_APPLY.ORGDUTY',
            width : 120,
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_APPLY.STARTDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_APPLY.ENDDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_APPLY.DAYS',
            width : 80,
            type : 'number',
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'needCredentials',
            title : 'T_EMC_LEAVE_APPLY.NEEDCREDENTIALS',
            width : 80,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'credentials',
            title : 'T_EMC_LEAVE_APPLY.CREDENTIALS',
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'visitPlace',
            title : 'T_EMC_LEAVE_APPLY.VISITPLACE',
            width : 100,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'stayDays',
            title : 'T_EMC_LEAVE_APPLY.STAYDAYS',
            width : 80,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'remark',
            title : 'T_EMC_LEAVE_APPLY.REMARK',
            width : 200,
            validators : [ 'strLength[0,300]' ]
        } ];
    },

    getSearchListColumns : function(operType) {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcLeaveApply.workflow.dbTable), {
            field : 'id',
            title : 'T_EMC_LEAVE_APPLY.ID',
            width : 100,
            type : 'link',
            validators : [ 'notEmpty', 'strLength[0,18]' ]
        }, {
            field : 'applicant',
            title : 'T_EMC_LEAVE_APPLY.APPLICANT',
            width : 100,
            value : workspace.user.userName,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'type',
            title : 'T_EMC_LEAVE_APPLY.TYPE',
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveapplyform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_APPLY.ORGDUTY',
            width : 140,
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_APPLY.STARTDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_APPLY.ENDDATE',
            width : 120,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_APPLY.DAYS',
            width : 90,
            type : 'number',
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'needCredentials',
            title : 'T_EMC_LEAVE_APPLY.NEEDCREDENTIALS',
            width : 90,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'credentials',
            title : 'T_EMC_LEAVE_APPLY.CREDENTIALS',
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'visitPlace',
            title : 'T_EMC_LEAVE_APPLY.VISITPLACE',
            width : 120,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'stayDays',
            title : 'T_EMC_LEAVE_APPLY.STAYDAYS',
            width : 90,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'revertDate',
            title : 'T_EMC_LEAVE_APPLY.REVERTDATE',
            width : 90,
            type : 'date',
            editor : operType === 'replenish' ? true : false,
            onBeforeEditorRender : function() {
                if (operType !== 'replenish') {
                    return false;
                }
                var grid = Gikam.getComp('emc-leave-apply-search-list-grid');
                var data = grid.getActivedRow();
                if (data && data.processStatus === 'done') {
                    return true;
                }
                return false;
            }
        }, {
            field : 'remark',
            title : 'T_EMC_LEAVE_APPLY.REMARK',
            width : 200,
            validators : [ 'strLength[0,300]' ]
        } ];
    },

    /**
     * 根据操作类型获取详情页面字段
     */
    getDetailFormColumns : function(operType) {
        var detailFormColumns = emcLeaveApply.getDetailFormColumnsFirst(operType);
        if(operType === 'search' || operType === 'replenish') {
            detailFormColumns = detailFormColumns.concat(emcLeaveApply.getDetailFormColumnsSecond(operType));
        }
        detailFormColumns = detailFormColumns.concat(emcLeaveApply.getDetailFormColumnsThree(operType));
        return detailFormColumns;
    },
    
    getDetailFormColumnsFirst : function(operType) {
        return [ {
            field : 'applicant',
            title : 'T_EMC_LEAVE_APPLY.APPLICANT',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'applicant'),
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'type',
            title : 'T_EMC_LEAVE_APPLY.TYPE',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'type'),
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'leaveapplyform',
                codeClassId : 'biz'
            },
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'orgDuty',
            title : 'T_EMC_LEAVE_APPLY.ORGDUTY',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'orgDuty'),
            validators : [ 'notEmpty', 'strLength[0,90]' ]
        }, {
            field : 'startDate',
            title : 'T_EMC_LEAVE_APPLY.STARTDATE',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'startDate'),
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_LEAVE_APPLY.ENDDATE',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'endDate'),
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'days',
            title : 'T_EMC_LEAVE_APPLY.DAYS',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'days'),
            validators : [ 'notEmpty', 'number' ]
        }, {
            field : 'needCredentials',
            title : 'T_EMC_LEAVE_APPLY.NEEDCREDENTIALS',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'needCredentials'),
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            validators : [ 'notEmpty' ],
            onChange : function(field, value) {
                var form = Gikam.getComp('emc-leave-apply-detail-base-info-form');
                if (value === '1') {
                    if(!emcLeaveApply.isReadonlyDetail(operType, 'credentials'))
                        form.toFieldsEdit([ 'credentials' ]);
                } else {
                    form.toFieldsReadonly([ 'credentials' ]);
                    form.setData({
                        needCredentials : value,
                        credentials : ''
                    }, true);
                }
            }
        }, {
            field : 'credentials',
            title : 'T_EMC_LEAVE_APPLY.CREDENTIALS',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'credentials'),
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'visitPlace',
            title : 'T_EMC_LEAVE_APPLY.VISITPLACE',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'visitPlace'),
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'stayDays',
            title : 'T_EMC_LEAVE_APPLY.STAYDAYS',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'stayDays'),
            validators : [ 'number' ]
        } ];
    },
    
    getDetailFormColumnsSecond : function(operType) {
        return [ {
            field : 'revertDate',
            title : 'T_EMC_LEAVE_APPLY.REVERTDATE',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'revertDate'),
            type : 'date'
        } ];
    },
    
    getDetailFormColumnsThree : function(operType) {
        return [ {
            field : 'remark',
            title : 'T_EMC_LEAVE_APPLY.REMARK',
            editor : true,
            readonly : emcLeaveApply.isReadonlyDetail(operType, 'remark'),
            validators : [ 'strLength[0,300]' ]
        } ];
    },

    /**
     * 根据操作类型判断详情页面的一般字段是否只读
     */
    isReadonlyDetail : function(operType, field) {
        if (field === 'applicant') {
            return true;
        } else if(field === 'credentials' && operType === 'edit') {
            return false;
        } else if(field === 'revertDate' && operType === 'replenish') {
            return false;
        } else if (operType === 'edit') {
            return false;
        } else {
            return true;
        }
    },

    workflow : {
        dbTable : 'T_EMC_LEAVE_APPLY',
        columns : [ {
            field : 'id',
            title : 'T_EMC_LEAVE_APPLY.ID'
        } ]
    }

});
