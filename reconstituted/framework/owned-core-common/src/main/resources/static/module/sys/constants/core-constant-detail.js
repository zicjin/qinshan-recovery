coreConstant.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
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
            width : Gikam.width.M,
            panels : [ {
                title : 'CORE.MODULE.SYS.CONSTANTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-constant-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreConstantServiceImpl',
                    columns : 1,
                    fields : coreConstant.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    createPage : function() {
        var _this = this;

        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });

        return this;
    },

    urlManager : {
        baseInfoForm : coreConstant.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-constant-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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

    init : function(param) {
        this.param = param;

        this.createPage();
    }
}