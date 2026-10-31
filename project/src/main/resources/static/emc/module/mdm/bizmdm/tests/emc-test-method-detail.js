emcTestMethod.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
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
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.DETAIL',
                items : [ {
                    type : 'form',
                    id : 'emc-test-method-detail-form',
                    fields : emcTestMethod.getBaseInfoFormFields(),
                    columns : 3,
                    service : 'emcTestMethodServiceImpl',
                    url : Gikam.printf(emcTestMethod.baseUrl + '/{id}', {
                        id : _this.param.id
                    }),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data.id)) {
                            this.toFieldsReadonly([ 'ext$.methodname' ]);
                        }
                    }
                } ]
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoForm() ]
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
