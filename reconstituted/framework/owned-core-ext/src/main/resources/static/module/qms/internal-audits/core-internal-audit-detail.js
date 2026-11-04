coreInternalAudit.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                id : 'core-qms-internal-audit-detail-back-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    var comp = Gikam.getComp('core-internal-audit-edit-list-grid');
                    if (!Gikam.isEmpty(comp)) {
                        comp.refresh();
                    }
                }
            }, {
                type : 'button',
                id : 'core-qms-internal-audit-detail-add-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreInternalAudit.baseUrl, Gikam.getJsonWrapper({}, [ '', [{}] ])).done(function(item) {
                        Gikam.getComp('core-internal-audit-detail-base-info-form').refresh({
                            url : Gikam.printf(coreInternalAudit.baseUrl + '/{id}', {
                                id : item
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-internal-audit-detail-delete-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var data = Gikam.getComp('core-internal-audit-detail-base-info-form').getData();
                    workspace.window.showMask(true);
                    Gikam.del(coreInternalAudit.baseUrl, Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                        workspace.window.goBack();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-internal-audit-detail-submit-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-internal-audit-detail-base-info-form');
                    if (Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreInternalAudit
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-internal-audit-detail-pass-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-internal-audit-detail-base-info-form');
                    if (Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreInternalAudit
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-internal-audit-detail-reject-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-internal-audit-detail-base-info-form');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreInternalAudit
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-internal-audit-detail-print-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_INTERNAL_AUDIT', {
                        id : _this.param.id
                    });
                }
            }]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'MODULE.QMS.INTERNALAUDITS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreInternalAuditServiceImpl',
                    id : 'core-internal-audit-detail-base-info-form',
                    fields : _this.getInfoByProcessStatus(),
                    columns : 3
                } ]
            } ]
        }
    },
    
    urlManager : {
        baseInfoForm : coreInternalAudit.baseUrl + '/{id}'
    },
    
    getInfoByProcessStatus : function(){
        var _this = this;
        var fields;
        switch (_this.processStatus) {
        case 'draft':
        case 'draft_rejected':
        case 'draft_withdrawed':
        case 'draft_undo':
            fields = coreInternalAudit.getBaseInfoFormFields();
            break;
        case 'approve':
        case 'approve_rejected':
            if (_this.statusCode == 'approve1' || _this.statusCode == 'approve2') {
                fields = coreInternalAudit.getAuditInfoFormFields1();
            } else if (_this.statusCode == 'editReport') {
                fields = coreInternalAudit.getAuditInfoFormFields2();
            } else {
                fields = coreInternalAudit.getAuditInfoFormFields3();
            }
            break;
        case 'done':
            fields = coreInternalAudit.getAuditInfoFormFields3();
            break;
        default :
            fields = coreInternalAudit.getBaseInfoFormFields();
        };
        return fields;
    },
    
    getStatusCode : function(){
        var statusCode, processStatus;
        var _this = this;
        var url = coreInternalAudit.baseUrl + '/{id}/info/status';
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
    
    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        var _this = this;
        var pageConfig = {
                bizId : _this.param.id,
                pageObject : coreInternalAudit,
                config : _this.pageConfig,
                prior : _this.param.sourcePage
        }
        
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
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
