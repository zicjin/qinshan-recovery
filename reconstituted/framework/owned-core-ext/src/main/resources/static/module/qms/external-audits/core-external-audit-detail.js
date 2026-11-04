coreExternalAudit.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            align : 'left',
            items : [{
                id : 'core-external-audit-detail-back-button',
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    var comp = Gikam.getComp('core-external-audit-edit-list-grid');
                    if(!Gikam.isEmpty(comp)) {
                        comp.refresh();
                    }
                }
            }, {
                id : 'core-external-audit-detail-add-button',
                type : 'button',
                text : 'GIKAM.AT.INSERT',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreExternalAudit.baseUrl, Gikam.getJsonWrapper({}, ['', [{}]])).done(function(id) {
                        Gikam.getComp('core-external-audit-detail-base-info-form').refresh({
                            url : Gikam.printf(coreExternalAudit.baseUrl + '/{id}', {
                                id : id
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                id : 'core-external-audit-detail-delete-button',
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var data = Gikam.getComp('core-external-audit-detail-base-info-form').getData();
                    workspace.window.showMask(true);
                    Gikam.create('confirm', {
                        message : Gikam.propI18N('GIKAM.TIP.CONFIRM_TO_DELETE'),
                        title : Gikam.propI18N('GIKAM.BUTTON.DELETE'),
                        subTitle : data.auditNo,
                        yesText : Gikam.propI18N('GIKAM.BUTTON.YES'),
                        noText : Gikam.propI18N('GIKAM.BUTTON.NO'),
                        onYesClick : function() {
                            Gikam.del(coreExternalAudit.baseUrl, Gikam.getJsonWrapper({}, ['', [data]])).done(function() {
                                workspace.window.goBack();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    })
                    workspace.window.closeMask();
                }
            }, {
                id : 'core-external-audit-detail-submit-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-external-audit-detail-base-info-form');
                    if(Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreExternalAudit
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-external-audit-edit-list-grid').refresh();
                        Gikam.getComp('core-external-audit-edit-list-ncr-grid').cleanData();
                        Gikam.getComp('core-external-audit-edit-list-group-grid').cleanData();
                    });
                }
            }, {
                id : 'core-external-audit-detail-pass-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-external-audit-detail-base-info-form');
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreExternalAudit
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-external-audit-audit-list-grid').refresh();
                        Gikam.getComp('core-external-audit-audit-list-ncr-grid').cleanData();
                        Gikam.getComp('core-external-audit-audit-list-group-grid').cleanData();
                    });
                }
            }, {
                id : 'core-external-audit-detail-reject-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-external-audit-detail-base-info-form');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreExternalAudit
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-external-audit-audit-list-grid').refresh();
                        Gikam.getComp('core-external-audit-audit-list-ncr-grid').cleanData();
                        Gikam.getComp('core-external-audit-audit-list-group-grid').cleanData();
                    });
                }
            },{
                type : 'button',
                id : 'core-external-audit-detail-print-button',
                text : 'CORE.MODULE.QMS.EXTERNAL_AUDIT.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_EXTERNAL_AUDIT', {
                        id : _this.param.id
                    });
                }
            }]
        }
    },


    getBaseInfoTab : function() {
        return {
            type : 'form',
            columns : 3,
            url : Gikam.printf(coreExternalAudit.baseUrl + '/{id}', {id : this.param.id}),
            service : 'coreExternalAuditServiceImpl',
            id : 'core-external-audit-detail-base-info-form',
            autoSave : true,
            service : 'coreExternalAuditServiceImpl',
            fields : coreExternalAudit.getBaseInfoFormFields()
        }
    },

    createPage : function() {
        var pageConfig = {
            pageObject : coreExternalAudit,
            config : coreExternalAudit.pageConfig,
            prior : this.param.status
        };
        Gikam.create('layout', {
            pageConfig : pageConfig,
            center : {
                items : [this.getBtnToolbar(), this.getBaseInfoTab()]
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
