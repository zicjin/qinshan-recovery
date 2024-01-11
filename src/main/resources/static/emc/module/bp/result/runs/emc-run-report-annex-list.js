emcRun.annexPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-run-edit-list-test-run-grid').getData());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-run-report-file-uploader',
            dbTable : 'T_EMC_RUN',
            bizId : _this.param.id
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoForm() ]
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