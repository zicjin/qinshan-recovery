coreCode.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_CODE', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
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
            panels : [ {
                title : 'CORE.MODULE.SYS.CODES.TAB.BASEINFO',
                items : [ {
                    type : 'form',
                    id : 'core-code-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreCodeServiceImpl',
                    fields : coreCode.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        if (data.ext$.codeclassid === 'sys') {
                            this.toFieldsReadonly([ 'codeCategoryId' ]);
                        }
                    }
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreCode.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-code-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
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