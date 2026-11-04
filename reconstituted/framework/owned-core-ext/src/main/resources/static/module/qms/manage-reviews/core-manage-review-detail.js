coreManageReview.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [{
                type : 'button',
                id : 'core-qms-manage-review-detail-back-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    var comp = Gikam.getComp('core-manage-review-edit-list-grid');
                    if (!Gikam.isEmpty(comp)) {
                        comp.refresh();
                    }
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-add-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreManageReview.baseUrl, Gikam.getJsonWrapper({}, [ '', [{}] ])).done(function(item) {
                        Gikam.getComp('core-manage-review-detail-base-info-form').refresh({
                            url : Gikam.printf(coreManageReview.baseUrl + '/{id}', {
                                id : item
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-delete-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var data = Gikam.getComp('core-manage-review-detail-base-info-form').getData();
                    workspace.window.showMask(true);
                    Gikam.del(coreManageReview.baseUrl, Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                        workspace.window.goBack();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-submit-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-manage-review-detail-base-info-form');
                    if (Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreManageReview
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-pass-button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-manage-review-detail-base-info-form');
                    if (Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreManageReview
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-reject-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-manage-review-detail-base-info-form');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreManageReview
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-manage-review-detail-print-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_MANAGE_REVIEW', {
                        id : _this.param.id
                    });
                }
            } ]
        }
    },
    
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'MODULE.QMS.MANAGEREVIEWS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreManageReviewServiceImpl',
                    id : 'core-manage-review-detail-base-info-form',
                    fields : _this.getInfoByProcessStatus(),
                    columns : 3
                } ]
            } ]
        }
    },
    
    getInfoByProcessStatus : function(){
        var _this = this;
        var fields;
        switch (_this.processStatus) {
        case 'draft':
        case 'draft_rejected':
        case 'draft_withdrawed':
        case 'draft_undo':
            fields = coreManageReview.getBaseInfoFormFields();
            break;
        case 'approve':
        case 'approve_rejected':
            if (_this.statusCode == 'approve1' || _this.statusCode == 'approve2') {
                fields = coreManageReview.getAuditInfoFormFields1();
            } else if (_this.statusCode == 'editReport') {
                fields = coreManageReview.getAuditInfoFormFields2();
            } else {
                fields = coreManageReview.getAuditInfoFormFields3();
            }
            break;
        case 'done':
            fields = coreManageReview.getSearchInfoFormFields();
            break;
        default :
            fields = coreManageReview.getBaseInfoFormFields();
        };
        return fields;
    },
    
    urlManager : {
        baseInfoForm : coreManageReview.baseUrl + '/{id}'
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    getStatusCode : function(){
        var statusCode, processStatus;
        var _this = this;
        var url = coreManageReview.baseUrl + '/{id}/info/status';
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
    
    createPage : function() {
        var _this = this;
        var pageConfig = {
                bizId : _this.param.id,
                pageObject : coreManageReview,
                config : _this.pageConfig,
                prior : _this.param.sourcePage
        }

        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.getStatusCode();
        this.createPage();
    }
}
