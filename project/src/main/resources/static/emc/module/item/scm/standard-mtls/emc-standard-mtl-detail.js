emcStandardMtl.detailPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            titleWidth : 120,
            url : _this.getCompUrl('baseInfoForm'),
            service : 'emcStandardMtlServiceImpl',
            id : 'emc-standard-mtl-detail-base-info-form',
            fields : emcStandardMtl.getBaseInfoFormFields()
        }
    },

    urlManager : {
        baseInfoForm : emcStandardMtl.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-standard-mtl-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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
                pageObject : emcStandardMtl,
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
