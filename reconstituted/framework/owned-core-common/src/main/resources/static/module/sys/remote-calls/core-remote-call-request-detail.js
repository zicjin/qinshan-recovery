coreRemoteCallConfig.requestDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;

        return {
            type : 'form',
            id : 'core-remote-call-request-detail-base-info-form',
            service : 'coreRemoteCallRequestServiceImpl',
            url : Gikam.printf(coreRemoteCallConfig.baseUrl + '/configs/requests/{id}', {
                id : _this.param.id
            }),
            columns : 1,
            fields : coreRemoteCallConfig.getRemoteCallRequestFields(),
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};