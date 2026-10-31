emcSol.solRecordBatchPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-sol-record-batch-form').getData();
                    Gikam.getLastModal().close(formData);
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-sol-record-batch-form',
            fields : emcSol.getPreInsertRecordFields(),
            columns : 1,
            onRendered : function() {
                var grid = Gikam.getComp('emc-sol-detail-record-grid');
                if (Gikam.isEmpty(grid)) {
                    return;
                }
                var selections = grid.getSelections();
                this.setData(selections[0]);
            },
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}