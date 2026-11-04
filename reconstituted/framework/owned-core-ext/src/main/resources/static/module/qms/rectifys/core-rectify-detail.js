coreRectify.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            align : 'left',
            items : [{
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                id : 'core-rectify-detail-back-button',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    Gikam.getComp('core-rectify-edit-list-grid').refresh();
                }
            }, {
                id : 'core-rectify-detail-submit-button',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var form = Gikam.getComp('core-rectify-detail-base-info-form');
                    if(Gikam.isFalse(_this.validate()))
                        return;
                    Gikam.create('workflow').submit({
                        data : [form.getData()],
                        pageObject : coreRectify
                    }).done(function() {
                        workspace.window.goBack();
                        Gikam.getComp('core-rectify-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.RECTIFY.BUTTON.PRINT',
                id : 'core-rectify-detail-print-button',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('QMS_RECTIFY', {
                        id : _this.param.id
                    });
                }
            }]
        }
    },

    validate : function() {
        var grid = Gikam.getComp('core-rectify-detail-item-grid');
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
            url : Gikam.printf(coreRectify.baseUrl + '/{id}', {id : this.param.id}),
            service : 'coreRectifyServiceImpl',
            id : 'core-rectify-detail-base-info-form',
            fields : coreRectify.getBaseInfoFormFields()
        }
    },

    getItemGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-rectify-detail-item-grid',
            service : 'coreRectifyItemServiceImpl',
            columns : coreRectify.getDetailPageItemListColumns(),
            url : Gikam.printf(coreRectify.baseUrl + '/{id}/items/queries', {id : _this.param.id}),
            toolbar : [{
                type : 'button',
                text : 'CORE.MODULE.QMS.RECTIFY.RECTIFY_ITEM.BUTTON.ADD',
                icon : 'add-row',
                onClick : function() {
                    Gikam.getComp('core-rectify-detail-item-grid').insert(Gikam.printf(coreRectify.baseUrl + '/{id}/items', {
                        id : _this.param.id
                    }), [{}]);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.RECTIFY.RECTIFY_ITEM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-rectify-detail-item-grid').deleteRows(Gikam.printf(coreRectify.baseUrl + '/{id}/items', {
                        id : _this.param.id
                    }));
                }
            }]
        };
    },

    createPage : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreRectify,
            config : coreRectify.pageConfig,
            prior : this.param.status
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
