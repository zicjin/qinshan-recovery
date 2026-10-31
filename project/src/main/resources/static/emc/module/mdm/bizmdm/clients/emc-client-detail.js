emcClient.detailPage = {

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-client-detail-form',
            fields : emcClient.getBaseInfoFormFields(),
            columns : 4,
            service : 'emcClientServiceImpl',
            url : Gikam.printf(emcClient.baseUrl + '/{id}', {
                id : _this.param.id
            }),
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBaseInfoForm() ]
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
