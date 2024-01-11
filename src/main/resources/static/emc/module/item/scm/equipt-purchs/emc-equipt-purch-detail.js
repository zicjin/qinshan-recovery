emcEquiptPurch.detailPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            titleWidth : 120,
            url : _this.getCompUrl('baseInfoForm'),
            service : 'emcEquiptPurchServiceImpl',
            id : 'emc-equipt-purch-detail-base-info-form',
            fields : emcEquiptPurch.getBaseInfoFormFields()
        }
    },

    urlManager : {
        baseInfoForm : emcEquiptPurch.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-equipt-purch-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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
                items : [ this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
            pageConfig : {
                pageObject : emcEquiptPurch,
                bizId : this.param.id
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
