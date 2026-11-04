coreUserTrain.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [{
                type : 'button',
                id : 'core-qms-user-train-detail-back-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack({
                        refresh: false
                    }).done(function () {
                        var comp = Gikam.getComp('core-user-train-edit-list-grid');
                        if (!Gikam.isEmpty(comp)) {
                            comp.refresh();
                        }
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-user-train-detail-add-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreUserTrain.baseUrl, Gikam.getJsonWrapper({}, [ '', [{}] ])).done(function(item) {
                        Gikam.getComp('core-user-train-detail-base-info-form').refresh({
                            url : Gikam.printf(coreUserTrain.baseUrl + '/{id}', {
                                id : item
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            },  {
                type : 'button',
                id : 'core-qms-user-train-detail-delete-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var data = Gikam.getComp('core-user-train-detail-base-info-form').getData();
                    workspace.window.showMask(true);
                    Gikam.del(coreUserTrain.baseUrl, Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                        workspace.window.goBack();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-user-train-detail-submit-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-user-train-detail-base-info-form');
                    if (Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreUserTrain
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-user-train-detail-pass-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-user-train-detail-base-info-form');
                    if (Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreUserTrain
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-user-train-detail-reject-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-user-train-detail-base-info-form');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreUserTrain
                    }).done(function() {
                        workspace.window.goBack();
                    });
                }
            }, {
                type : 'button',
                id : 'core-qms-user-train-detail-print-button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_EMP_TRAIN', {
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
                title : 'QMS.USERTRAINS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreUserTrainServiceImpl',
                    id : 'core-user-train-detail-base-info-form',
                    fields : _this.getInfoByProcess(),
                    columns : 3
                } ]
            } ]
        }
    },

    getInfoByProcess : function(){
        var _this = this;
        var fields;
        switch (_this.processStatus) {
        case 'draft':
        case 'draft_rejected':
        case 'draft_withdrawed':
        case 'draft_undo':
            fields = coreUserTrain.getBaseInfoFormFields();
            break;
        case 'approve' :
        case 'approve_rejected' :
            fields = _this.statusCode == 'evaluate' ? coreUserTrain.getEvluateInfoFormFields() : coreUserTrain.getAuditInfoFormFields();
            break;
        case 'done':
            fields = coreUserTrain.getSearchInfoFormFields();
            break;
        default :
            fields = coreUserTrain.getBaseInfoFormFields();
        };
        return fields;
    },

    getStatusCode : function(){
        var statusCode, processStatus;
        var _this = this;
        var url = coreUserTrain.baseUrl + '/{id}/info/status';
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

    urlManager : {
        baseInfoForm : coreUserTrain.baseUrl + '/{id}',
    },

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
                pageObject : coreUserTrain,
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
