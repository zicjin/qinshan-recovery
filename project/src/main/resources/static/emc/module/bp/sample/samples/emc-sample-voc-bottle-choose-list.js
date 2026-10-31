emcSample.vocBottleChoosePage = {

    getGridParam : function() {
/*        var selections = Gikam.getComp('emc-sample-edit-list-project-sample-grid').getSelections();
*/        var _this = this;
/*        var projNo = selections[0].projNo;
*/      var columns = emcSample.getVocBottleColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-sampling-voc-bottle-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/voc/queries',
            page : true,
            filterOpen : true,
            requestData : requestData,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-sampling-voc-bottle-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
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