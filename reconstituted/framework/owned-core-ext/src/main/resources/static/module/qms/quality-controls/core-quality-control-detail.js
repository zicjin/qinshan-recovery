coreQualityControl.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            align : 'left',
            items : [{
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                id : 'core-quality-control-detail-back-button',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    var comp = Gikam.getComp('core-quality-control-edit-list-grid');
                    if(!Gikam.isEmpty(comp)) {
                        comp.refresh();
                    }
                }
            }, {
                id : 'core-quality-control-detail-add-button',
                type : 'button',
                text : 'GIKAM.AT.INSERT',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreQualityControl.baseUrl, Gikam.getJsonWrapper({}, ['', [{}]])).done(function(id) {
                        Gikam.getComp('core-quality-control-detail-base-info-form').refresh({
                            url : Gikam.printf(coreQualityControl.baseUrl + '/{id}', {
                                id : id
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                id : 'core-quality-control-detail-delete-button',
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var data = Gikam.getComp('core-quality-control-detail-base-info-form').getData();
                    workspace.window.showMask(true);
                    Gikam.create('confirm', {
                        message : Gikam.propI18N('GIKAM.TIP.CONFIRM_TO_DELETE'),
                        title : Gikam.propI18N('GIKAM.BUTTON.DELETE'),
                        subTitle : data.qcNo,
                        yesText : Gikam.propI18N('GIKAM.BUTTON.YES'),
                        noText : Gikam.propI18N('GIKAM.BUTTON.NO'),
                        onYesClick : function() {
                            Gikam.del(coreQualityControl.baseUrl, Gikam.getJsonWrapper({}, ['', [data]])).done(function() {
                                workspace.window.goBack();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    })
                    workspace.window.closeMask();
                }
            }, {
                id : 'core-quality-control-detail-submit-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-quality-control-detail-base-info-form');
                    if(Gikam.isFalse(_this.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreQualityControl
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-quality-control-edit-list-grid').refresh();
                    });
                }
            }, {
                id : 'core-quality-control-detail-pass-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-quality-control-detail-base-info-form');
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreQualityControl
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-quality-control-audit-list-grid').refresh();
                    });
                }
            }, {
                id : 'core-quality-control-detail-reject-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-quality-control-detail-base-info-form');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreQualityControl
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-quality-control-audit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.QUALITY_CONTROL.BUTTON.PRINT',
                id : 'core-quality-control-detail-print-button',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_QUALITY_CONTROL', {
                        id : _this.param.id
                    });
                }
            }]
        }
    },

    validate : function() {
        var baseInfoForm = Gikam.getComp('core-quality-control-detail-base-info-form');
        var grid = Gikam.getComp('core-quality-control-edit-list-control-item-grid');
        var commentForm = Gikam.getComp('core-quality-control-detail-qm-comment-form');
        switch(this.param.status) {
            case 'detailPageEdit':
                return baseInfoForm.validate();
            case 'registItemsPageReview':
                return grid.validate();
            case 'execDetailPage' :
                return grid.validate();
            case 'groupAuditDetailPage':
                return commentForm.validate();
            default:
                return true;
        }
    },

    getBasicInfoForm : function() {
        return {
            type : 'form',
            columns : 4,
            url : Gikam.printf(coreQualityControl.baseUrl + '/{id}', {id : this.param.id}),
            service : 'coreQualityControlServiceImpl',
            id : 'core-quality-control-detail-base-info-form',
            fields : coreQualityControl.getBaseInfoFormFields()
        }
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [_this.getQmCommentForm()]
        }
    },

    getQmCommentForm : function() {
        var _this = this;
        return {
            title : 'CORE.MODULE.QMS.QUALITY_CONTROL.TITLE.QC_COMMENT',
            items : [{
                type : 'form',
                url : Gikam.printf(coreQualityControl.baseUrl + '/{id}', {
                    id : _this.param.id
                }),
                columns : 3,
                service : 'coreQualityControlServiceImpl',
                id : 'core-quality-control-detail-qm-comment-form',
                fields : coreQualityControl.getQmCommentFormFields()
            }]
        };
    },

    getControlItemGrid : function() {
        var _this = this;
        var columns;
        if(this.param.status === 'registItemsPageReview') {
            columns = coreQualityControl.getRegistControlItemListColumns();
        } else {
            columns = coreQualityControl.getExecuteControlItemListColumns();
        }
        return {
            type : 'grid',
            id : 'core-quality-control-edit-list-control-item-grid',
            service : 'coreQualityControlItemServiceImpl',
            url : Gikam.printf(coreQualityControl.baseUrl + '/{id}/control-items/queries', {
                id : _this.param.id
            }),
            deleteFormatter : function(row) {
                return row.folderNo;
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.getComp('core-quality-control-edit-list-control-item-grid').insert(Gikam.printf(coreQualityControl.baseUrl + '/{id}/control-items', {
                        id : _this.param.id
                    }), [{}]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-quality-control-edit-list-control-item-grid').deleteRows(Gikam.printf(coreQualityControl.baseUrl + '/{id}/control-items', {
                        id : _this.param.id
                    }));
                }
            }],
            columns : columns
        };
    },

    createPage : function() {
        var pageConfig = {
            pageObject : coreQualityControl,
            config : coreQualityControl.pageConfig,
            prior : this.param.status
        };
        var items = [this.getBtnToolbar(), this.getBasicInfoForm()];
        if(this.param.status === 'registItemsPageReview' || this.param.status === 'execDetailPage') {
            items = [this.getBtnToolbar(), this.getBasicInfoForm(), this.getControlItemGrid()]
        }
        if(this.param.status === 'groupAuditDetailPage' || this.param.status === 'auditPageReview' || this.param.status === 'search') {
            items = [this.getBtnToolbar(), this.getBasicInfoForm(), this.getControlItemGrid(), this.getSouthTab()];
        }
        Gikam.create('layout', {
            pageConfig : pageConfig,
            center : {
                items : items
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
