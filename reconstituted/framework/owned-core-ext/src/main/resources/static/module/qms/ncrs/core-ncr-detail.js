coreNcr.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            align : 'left',
            items : [{
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                id : 'core-ncr-detail-back-button',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    var comp = Gikam.getComp('core-ncr-edit-list-grid');
                    if(!Gikam.isEmpty(comp)) {
                        comp.refresh();
                    }
                }
            }, {
                id : 'core-ncr-detail-add-button',
                type : 'button',
                text : 'GIKAM.AT.INSERT',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreNcr.baseUrl, Gikam.getJsonWrapper({}, ['', [{}]])).done(function(id) {
                        Gikam.getComp('core-ncr-detail-base-info-form').refresh({
                            url : Gikam.printf(coreNcr.baseUrl + '/{id}', {
                                id : id
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                id : 'core-ncr-detail-delete-button',
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var data = Gikam.getComp('core-ncr-detail-base-info-form').getData();
                    workspace.window.showMask(true);
                    Gikam.create('confirm', {
                        message : Gikam.propI18N('GIKAM.TIP.CONFIRM_TO_DELETE'),
                        title : Gikam.propI18N('GIKAM.BUTTON.DELETE'),
                        subTitle : data.ncrNo,
                        yesText : Gikam.propI18N('GIKAM.BUTTON.YES'),
                        noText : Gikam.propI18N('GIKAM.BUTTON.NO'),
                        onYesClick : function() {
                            Gikam.del(coreNcr.baseUrl, Gikam.getJsonWrapper({}, ['', [data]])).done(function() {
                                workspace.window.goBack();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    })
                    workspace.window.closeMask();
                }
            }, {
                id : 'core-ncr-detail-submit-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-ncr-detail-base-info-form');
                    if(_this.param.status !== 'search' && Gikam.isFalse(form.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreNcr
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-ncr-edit-list-grid').refresh();
                    });
                }
            }, {
                id : 'core-ncr-detail-pass-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    var form = Gikam.getComp('core-ncr-detail-base-info-form');
                    Gikam.create('workflow').pass({
                        data : [form.getData()],
                        pageObject : coreNcr
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-ncr-audit-list-grid').refresh();
                    });
                }
            }, {
                id : 'core-ncr-detail-reject-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    var form = Gikam.getComp('core-ncr-detail-base-info-form');
                    Gikam.create('workflow').reject({
                        data : [form.getData()],
                        pageObject : coreNcr
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-ncr-audit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.NCR.BUTTON.PRINT',
                id : 'core-ncr-detail-print-button',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_NCR', {
                        id : _this.param.id
                    });
                }
            }]
        }
    },

    getBasicInfoForm : function() {
        return {
            type : 'form',
            columns : 3,
            url : Gikam.printf(coreNcr.baseUrl + '/{id}', {id : this.param.id}),
            service : 'coreNcrServiceImpl',
            id : 'core-ncr-detail-base-info-form',
            fields : coreNcr.getBaseInfoFormFields()
        }
    },

    getSouthTab : function() {
        return {
            type : 'tab',
            id : 'core-ncr-detail-south-tab',
            panels : [{
                title : 'CORE.MODULE.QMS.NCR.TITLE.DEALING_NCR',
                items : [{
                    type : 'form',
                    columns : 3,
                    titleWidth : 130,
                    url : Gikam.printf(coreNcr.baseUrl + '/{id}', {id : this.param.id}),
                    service : 'coreNcrServiceImpl',
                    id : 'core-ncr-detail-deal-form',
                    fields : coreNcr.getDealFormFields()
                }]
            }]
        }
    },

    createPage : function() {
        var pageConfig = {
            pageObject : coreNcr,
            config : coreNcr.pageConfig,
            prior : this.param.status
        };
        var items = [];
        if(this.param.status == 'detailPageEdit' || this.param.status == 'detailPageReview') {
            items = [this.getBtnToolbar(), this.getBasicInfoForm()];
        } else {
            items = [this.getBtnToolbar(), this.getBasicInfoForm(), this.getSouthTab()];
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
        this.param = param
        this.createPage();
    }
}
