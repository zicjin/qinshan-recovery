emcReport.undoRunPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-undo-run-grid',
            url : Gikam.printf(emcReport.baseUrl + '/queries/run/{projId}',{
                projId : _this.param.projId
            }),
            columns : emcReport.getRunListColumns(),
            toolbar : [{
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.UNDO',
                icon : 'submit',
                onClick : function() {
                    _this.undoRun();
                }
            }]
        }
    },

    undoRun : function (){
        var _this = this;
        var grid = Gikam.getComp('emc-report-undo-run-grid');
        var selections = grid.getSelections();
        if (selections.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.put(emcReport.baseUrl + '/undoRun/'+ _this.param.projId, Gikam.getJsonWrapper({}, [ '', selections])).done(function (){
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-undo-run-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                height : "50%",
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
