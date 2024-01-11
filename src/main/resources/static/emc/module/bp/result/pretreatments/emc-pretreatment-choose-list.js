emcPretreatment.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-pretreatment-choose-list-grid',
            service : 'emcPretreatmentServiceImpl',
            columns : emcPretreatment.getEditListColumns(),
            url : emcPretreatment.baseUrl + '/queries/raw',
            page : true,
            filterOpen : true,
            readonly : true,
	        requestData : {
	            orgId_SEQ : workspace.user.ext$.parentorgid,
                status_SEQ : '0',
                sampleReceiverFlag_SEQ : '1',
                id_DIFFER : _this.param.pretreatmentId
	        },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-pretreatment-choose-list-grid').getSelections());
                }
            } ],
            group : {
                fields : [ 'ext$.ordercategory' ],
                formatter : function(row) {
                    return row.ext$.ordercategory || '-';
                }
            }
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
