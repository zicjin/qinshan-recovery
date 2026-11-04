coreApplyComplaint.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-print-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_APPLY_COMPLAINT', {
                        id : _this.param.id
                    });
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        var columns = _this.getInfoByProcess();
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.QMS.APPLYCOMPLAINTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : Gikam.printf(coreApplyComplaint.baseUrl + '/{id}', {
                        id : this.param.id
                    }),
                    service : 'coreApplyComplaintServiceImpl',
                    id : 'core-apply-complaint-detail-base-info-form',
                    fields : columns,
                    columns : 3,
                    titleWidth : 120
                } ]
            },{
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-custom-survey-edit-list-uploader',
                    dbTable : 'T_CORE_CUSTOM_SURVEY',
                    bizId : _this.param.id,
                    onBeforeDelete : function() {
                        var rows = this.grid.data;
                        var canDelete = true;
                        Gikam.each(rows, function(index, row) {
                            if (workspace.user.id != row.createdById) {
                                canDelete = false;
                                Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                                return;
                            }
                        })
                        return canDelete;
                   }
                } ]
            }]
        }
    },

    createPage : function() {
        var _this = this;
        var pageConfig = {
                bizId : _this.param.id,
                pageObject : coreApplyComplaint,
                config : _this.pageConfig,
                prior : _this.param.sourcePage
        };
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig
        });
        return this;
    },

    getInfoByProcess : function(){
        var _this = this;
        var fields;
        switch (_this.processStatus) {
        case 'draft':
        case 'draft_rejected':
        case 'draft_withdrawed':
        case 'draft_undo':
            fields = coreApplyComplaint.getBaseInfoFormFields();
            break;
        case 'approve' :
        case 'approve_rejected' :
            fields = coreApplyComplaint.getAuditInfoFormFields(_this.statusCode);
            break;
        case 'done':
            fields = coreApplyComplaint.getBaseInfoFormFields();
            break;
        default :
            fields = coreApplyComplaint.getBaseInfoFormFields();
        };
        return fields;
    },
    
    getStatusCode : function(){
        var statusCode, processStatus;
        var _this = this;
        var url = coreApplyComplaint.baseUrl + '/{id}/info/status';
        workspace.window.showMask(true);
        Gikam.getTextSync(Gikam.printf(url, {
            id : _this.param.id
        })).done(function(item) {
            var statusDto = Gikam.parseJson(item);
            statusCode = statusDto.auditStatus.statusCode;
            processStatus = statusDto.auditStatus.processStatus;
        }).always(function() {
            workspace.window.closeMask();
        });
        this.statusCode = statusCode;
        this.processStatus = processStatus;
    },

    ProcessStatus : '',

    statusCode : '',
    init : function(param) {
        this.param = param;
        this.getStatusCode();
        this.createPage();
    }
}
