emcArea.detailPage = {

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
                title : 'EMC.MODULE.MDM.BASEMDM.AREAS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcAreaServiceImpl',
                    id : 'emc-area-detail-base-info-form',
                    columns : 2,
                    fields : emcArea.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : emcArea.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-area-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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
