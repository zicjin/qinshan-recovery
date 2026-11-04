codeCategory.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_CODE_CATEGORY', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'lims-code-category-detail-back-button',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.SYS.CODECATEGORIES.TAB.CODE_CATEGORY',
                items : [ {
                    type : 'form',
                    id : 'lims-code-category-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreCodeCategoryServiceImpl',
                    autoSave : true,
                    fields : codeCategory.getBaseInfoFormFields(),
                    onRendered : function() {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'id' ]);
                            this.removeFieldValidator('id', [ 'remote' ]);
                        }
                    }
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : codeCategory.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'lims-code-category-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });

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
        Gikam.create('layout', {
            id : 'lims-code-category-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
