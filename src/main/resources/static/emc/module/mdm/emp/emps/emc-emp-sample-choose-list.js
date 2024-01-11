emcEmp.sampleChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcEmp.getSampleChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-emp-sample-choose-list-grid',
            url : emcEmp.baseUrl + '/sample/queries/choosable',
            requestData : requestData,
            filter : true,
            filterOpen : true,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-emp-sample-choose-list-grid').getSelections());
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : emcEmp.getShowAllField()
            } ],
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
