emcProject.projOrderChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcProject.getProjOrderChooseColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-proj-order-choose-list-grid',
            requestData : requestData,

            columns : columns,
            filterOpen : true,
            page : true,
            url : IFM_CONTEXT +'/secure/emc/module/bp/project/proj-schemes/'+_this.param.projId+'/proj-orders/project/queries',
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let projOrder = Gikam.getComp('emc-proj-order-choose-list-grid').getSelections();
                    Gikam.getLastModal().close({
                        projOrder : projOrder
                    });
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
                width : '55%',
                items : [ _this.getGridParam() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
