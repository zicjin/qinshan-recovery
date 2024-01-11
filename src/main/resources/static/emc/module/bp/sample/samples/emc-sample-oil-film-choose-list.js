emcSample.oilFilmChoosePage = {

    getGridParam : function() {
      var _this = this;
      var columns = emcSample.getOilFilmColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-sampling-oil-film-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/result/oil-films/queries/raw',
            page : true,
            filterOpen : true,
            requestData : requestData,
            columns : columns,
            onRendered : function() {
                this.hideColumns([ 'projId']);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-sampling-oil-film-choose-list-grid').getSelections());
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