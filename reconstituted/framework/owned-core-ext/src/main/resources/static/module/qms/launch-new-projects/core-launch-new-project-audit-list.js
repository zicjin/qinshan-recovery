coreLaunchNewProject.auditPage = {

    getToolBarButtons : function() {
        var _this = this;
        return [{
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
            id : 'core-launch-new-project-audit-list-submit-button',
            icon : 'submit',
            onClick : function() {
                _this.submit();
            }
        }, {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
            id : 'core-launch-new-project-audit-list-reject-button',
            icon : 'reject',
            onClick : function() {
                _this.reject();
            }
        }];
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-launch-new-project-audit-list-grid',
            url : coreLaunchNewProject.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreLaunchNewProject.getListColumns(),
            onCellClick : function(field, row) {
                _this.cellClick(field, row);
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('core-launch-new-project-attachment-panel-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : _this.getToolBarButtons()
        }
    },

    cellClick : function(field, row) {
        var _this = this;
        var comps = _this.compManager();
        if(!_this.isEnable) {
            switch(_this.param.bpmn_statusCode) {
                case 'applyOrgReview': {
                    comps.applyForm.toFieldsEdit(['groupReviewAdvice', 'groupReviewResult']);
                    break;
                }
                case 'qmReview': {
                    comps.applyForm.toFieldsEdit(['qmAdvice']);
                    break;
                }
                case 'regist': {
                    comps.detailTab.showPanel('core-launch-new-project-item-panel');
                    break;
                }
                case 'execute': {
                    comps.detailTab.showPanel('core-launch-new-project-item-panel');
                    comps.itemForm.toFieldsEdit(['carryTestContent', 'carryTestResult']);
                    break;
                }
                case 'executeOrgReview': {
                    comps.detailTab.showPanel('core-launch-new-project-audit-panel');
                    comps.executeOrgReviewForm.toFieldsEdit(['reviewContent', 'reviewImprovedContent', 'reviewTechnicalAdvice', 'reviewQmAdvice',
                        'reviewRemark']);
                    break;
                }
                case 'methodConfirm': {
                    comps.detailTab.showPanel('core-launch-new-project-method-confirm-panel');
                    comps.methodConfirmForm.toFieldsEdit(['confirmStandardName', 'confirmStandardNo', 'confirmContent', 'confirmResult',
                        'confirmQmAdvice', 'confirmLeaderAdvice', 'confirmRemark']);
                    break;
                }
                default:
                    break;
            }
            _this.isEnable = true;
        }
        _this.refreshComps(row.id);
    },

    refreshComps : function(id) {
        var _this = this;
        var comps = _this.compManager();
        var itemUrl = coreLaunchNewProject.baseUrl + '/{id}/new-project-items/queries';
        var url = coreLaunchNewProject.baseUrl + '/{id}';
        comps.itemGrid.refresh({
            url : Gikam.printf(itemUrl, {
                id : id
            })
        });
        comps.applyForm.refresh({
            url : Gikam.printf(url, {
                id : id
            })
        });
        comps.itemForm.refresh({
            url : Gikam.printf(url, {
                id : id
            })
        });
        comps.executeOrgReviewForm.refresh({
            url : Gikam.printf(url, {
                id : id
            })
        });
        comps.methodConfirmForm.refresh({
            url : Gikam.printf(url, {
                id : id
            })
        });
    },

    setCompsReadOnly : function() {
        var _this = this;
        var comps = _this.compManager();
        comps.auditListGrid.refresh();
        comps.applyForm.toFieldsReadonly(['groupReviewAdvice', 'groupReviewResult']);
        comps.applyForm.cleanData();
        comps.itemForm.cleanData();
        comps.itemGrid.cleanData();
        comps.executeOrgReviewForm.cleanData();
        comps.executeOrgReviewForm.toFieldsReadonly(['reviewContent', 'reviewImprovedContent', 'reviewTechnicalAdvice', 'reviewQmAdvice',
            'reviewRemark']);
        comps.methodConfirmForm.cleanData();
        comps.methodConfirmForm.toFieldsReadonly(['confirmStandardName', 'confirmStandardNo', 'confirmContent', 'confirmResult',
            'confirmQmAdvice', 'confirmLeaderAdvice', 'confirmRemark']);
        _this.isEnable = false;
    },

    submit : function() {
        var _this = this;
        var comps = _this.compManager();
        Gikam.create('workflow').pass({
            data : comps.auditListGrid.getSelections(),
            pageObject : coreLaunchNewProject
        }).done(function() {
            _this.setCompsReadOnly();
        });
    },

    reject : function() {
        var _this = this;
        var comps = _this.compManager();
        Gikam.create('workflow').reject({
            data : comps.auditListGrid.getSelections(),
            pageObject : coreLaunchNewProject
        }).done(function() {
            comps.auditListGrid.refresh();
        });
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreLaunchNewProject,
            config : coreLaunchNewProject.pageConfig,
            prior : _this.param.bpmn_statusCode
        }
        Gikam.create('layout', {
            id : 'core-launch-new-project-audit-list-layout',
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig,
            west : {
                width : '50%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [coreLaunchNewProject.getBtnToolbar('core-launch-new-project-audit-list-grid'),coreLaunchNewProject.getDetailTab()]
            },
        });
    },

    compManager : function() {
        return {
            auditListGrid : Gikam.getComp('core-launch-new-project-audit-list-grid'),
            applyForm : Gikam.getComp('core-launch-new-project-apply-form'),
            detailTab : Gikam.getComp('core-launch-new-project-detail-tab'),
            itemGrid : Gikam.getComp('core-launch-new-project-item-panel-grid'),
            itemForm : Gikam.getComp('core-launch-new-project-item-panel-Form'),
            addItemBtn : Gikam.getComp('core-launch-new-project-add-research-config-button'),
            delItemBtn : Gikam.getComp('core-launch-new-project-delete-research-config-button'),
            itemUploader : Gikam.getComp('core-launch-new-project-item-panel-uploader'),
            executeOrgReviewForm : Gikam.getComp('core-launch-new-project-audit-form'),
            methodConfirmForm : Gikam.getComp('core-launch-new-project-method-confirm-form'),
        }
    },

    init : function(param) {
        this.isEnable = false;
        this.param = param;
        this.create();
    }

};
