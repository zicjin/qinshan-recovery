coreCustomSrtSurvey.choosePage = {

    getGridParam : function() {
        var _this = this,
            columns = coreCustomSrtSurvey.getChooseListColumns(_this.param.single);
        return {
            type : 'grid',
            id : 'core-custom-srt-survey-choose-list-grid',
            url : coreCustomSrtSurvey.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-custom-srt-survey-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-srt-survey-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
