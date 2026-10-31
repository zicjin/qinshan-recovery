emcReportTmpl.lineEditPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 1,
            service : 'emcReportTmplLineServiceImpl',
            id : 'emc-report-tmpl-line-edit-list-form',
            fields : emcReportTmpl.getBaseInfoFormFields(),
        }
    },
    
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-report-tmpl-line-edit-list-form').getData();
                    formData.id = _this.param.id;
                    Gikam.getLastModal().close(formData);
                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoTab(), this.getBtnToolbar() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
