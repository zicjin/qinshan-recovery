emcEquiptFileScript.detailPage = {

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
            panels : [ {
                title : 'EMC.MODULE.EQUIPT.FILE_SCRIPTS.TAB.ADD',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    id : 'emc-equipt-file-script-detail-base-info-form',
                    columns : 1,
                    service : 'emcEquiptFileScriptServiceImpl',
                    autoSave : true,
                    fields : emcEquiptFileScript.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : emcEquiptFileScript.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-equipt-file-script-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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