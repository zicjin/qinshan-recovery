emcTestMethod.copyByCategoryPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-test-method-copy-by-category-form').getData();
                    var selections = Gikam.getComp('emc-test-method-edit-list-grid').getSelections();
                    console.log(formData);

                    console.log(selections);
                    var url = emcTestMethod.baseUrl + '/action/copy-by-category';
                    Gikam.getLastModal().window.showMask(true);

                    Gikam.postText(url, Gikam.getJsonWrapper({
                        f : {
                            testCategoryId : formData.testCategoryId,
                        }
                    }, [ 'emcTestMethodServiceImpl', selections])).always(function() {
                            Gikam.getLastModal().close();
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
            id : 'emc-test-method-copy-by-category-form',
            fields : emcTestMethod.getCopyByCategoryFormFields(),
            columns : 1,
            onRendered : function() {
                if (_this.param.id) {
                    var activeRow = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                    this.setData({
                        id : _this.param.id,
                        testCategoryId : _this.param.testCategoryId,
                        ext$ : {
                            testcategory : _this.param.testCategory,
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