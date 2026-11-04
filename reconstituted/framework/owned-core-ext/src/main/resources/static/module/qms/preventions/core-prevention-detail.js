corePrevention.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            align : 'left',
            items : [{
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                id : 'core-prevention-detail-back-button',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    Gikam.getComp('core-prevention-edit-list-grid').refresh();
                }
            }, {
                id : 'core-prevention-detail-submit-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-prevention-detail-base-info-form');
                    if(Gikam.isFalse(_this.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : corePrevention
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-prevention-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.PREVENTION.BUTTON.PRINT',
                id : 'core-prevention-detail-print-button',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_PREVENTION', {
                        id : _this.param.id
                    });
                }
            }]
        }
    },

    getItemGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-prevention-detail-item-grid',
            service : 'corePreventionItemServiceImpl',
            columns : corePrevention.getDetailPageItemListColumns(),
            url : Gikam.printf(corePrevention.baseUrl + '/{id}/items/queries', {id : _this.param.id}),
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.getComp('core-prevention-detail-item-grid').insert(Gikam.printf(corePrevention.baseUrl + '/{id}/items', {
                        id : _this.param.id
                    }), [{}]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-prevention-detail-item-grid').deleteRows(Gikam.printf(corePrevention.baseUrl + '/{id}/items', {
                        id : _this.param.id
                    }));
                }
            }]
        };
    },

    validate : function() {
        var grid = Gikam.getComp('core-prevention-detail-item-grid');
        switch(this.param.status) {
            case 'detailPageEdit':
                return grid.validate();
            default:
                return true;
        }
    },

    getBasicInfoForm : function() {
        return {
            type : 'form',
            columns : 2,
            url : Gikam.printf(corePrevention.baseUrl + '/{id}', {id : this.param.id}),
            service : 'corePreventionServiceImpl',
            id : 'core-prevention-detail-base-info-form',
            fields : corePrevention.getBaseInfoFormFields()
        }
    },

    createPage : function() {
        var _this = this;
        var pageConfig = {
            pageObject : corePrevention,
            config : corePrevention.pageConfig,
            prior : _this.param.status
        };
        Gikam.create('layout', {
            pageConfig : pageConfig,
            north : {
                height : '35%',
                items : [_this.getBtnToolbar(), _this.getBasicInfoForm()]
            },
            center : {
                items : [_this.getItemGrid()]
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
