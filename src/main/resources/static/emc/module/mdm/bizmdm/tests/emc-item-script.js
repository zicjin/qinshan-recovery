emcTestMethod.itemScriptPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_LIMS_TESTMETHODITEM', _this.param.id);
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

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'LIMS.MODULE.BIZMDM.TESTS.TAB.CALCFORMULA',
                items : [ {
                    type : 'form',
                    id : 'lims-test-method-item-calc-formula-detail-base-info-form',
                    columns : 1,
                    fields : emcTestMethod.getCalcFormulaFormFields(),
                    url : Gikam.printf(emcTestMethod.baseUrl + '/method-items/{id}', {
                        id : _this.param.id
                    }),
                    onRendered : function() {
                        this.setData({
                            calcFormulaTestId : _this.param.testId,
                            calcFormulaTestMethodId : _this.param.testMethodId,
                            calcFormulaTestName : _this.param.testName,
                            calcFormulaMethodId : _this.param.methodId,
                            calcFormulaMethodNo : _this.param.methodNo
                        });
                    },
                } ]
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    save : function() {
        var formData = Gikam.getComp('lims-test-method-item-calc-formula-detail-base-info-form').getData();

        Gikam.getLastModal().window.showMask(true);
        Gikam.put(emcTestMethod.baseUrl + '/' + formData.testId, Gikam.getJsonWrapper(null, [ 'limsTestMethodItemServiceImpl', [ formData ] ])).done(function(r) {
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });

    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}