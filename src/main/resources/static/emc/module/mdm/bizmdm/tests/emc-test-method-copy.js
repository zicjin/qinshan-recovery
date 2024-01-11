emcTestMethod.copyPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-test-method-copy-form').getData();
                    var url = emcTestMethod.baseUrl + '/action/copy';
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.post(url, Gikam.getJsonWrapper(null, [ 'emcTestMethodServiceImpl', [ formData ] ])).always(function(r) {
                        if (r.status == 200) {
                            Gikam.getLastModal().close();
                        }
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
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
            type : 'form',
            id : 'emc-test-method-copy-form',
            fields : emcTestMethod.getCopyFormFields(),
            columns : 1,
            onRendered : function() {
                if (_this.param.id) {
                    var activeRow = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                    this.setData({
                        id : _this.param.id,
                        testCategoryId : _this.param.testCategoryId,
                        testId : _this.param.testId,
                        methodId : _this.param.methodId,
                        ext$ : {
                            testcategory : _this.param.testCategory,
                            testname : activeRow.ext$.testname,
                            methodname: decodeURIComponent(_this.param.methodname),
                        },
                    });
                }
            }
        }
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