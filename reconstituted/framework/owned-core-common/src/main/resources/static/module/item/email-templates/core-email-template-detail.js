coreEmailTemplate.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_EMAIL_TEMPLATE', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
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
        return {
            type : 'tab',
            width : Gikam.width.M,
            panels : [ {
                title : 'CORE.MODULE.ITEM.EMAILS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-email-template-detail-base-info-form',
                    url : this.getCompUrl('baseInfoForm'),
                    service : 'coreEmailTemplateServiceImpl',
                    fields : coreEmailTemplate.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreEmailTemplate.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-email-template-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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

    save : function(callback, showLoading, param) {
        workspace.window.save({
            url : coreEmailTemplate.baseUrl + '/' + this.param.id,
            page : this,
            showLoading : showLoading
        }).done(function() {
            callback && callback(param);
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