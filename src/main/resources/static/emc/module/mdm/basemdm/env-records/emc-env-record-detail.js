emcEnvRecord.detailPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            url : _this.getCompUrl('baseInfoForm'),
            service : 'emcEnvRecordServiceImpl',
            id : 'emc-env-record-detail-base-info-form',
            fields : emcEnvRecord.getDetailFormColumns(),
            columns : 3
        };
    },

    urlManager : {
        baseInfoForm : emcEnvRecord.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-env-record-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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

    save : function(callback, showLoading) {
        workspace.window.save({
            url : emcEnvRecord.baseUrl + (Gikam.isEmpty(this.param.id) ? '' : '/' + this.param.id + '/v/' + Gikam.getComp('emc-env-record-detail-base-info-form').getData().version),
            page : this,
            showLoading : showLoading
        }).done(function() {
            callback && callback();
        });
    },
    
    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-env-record-detail-layout',
            center : {
                items : [ this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
