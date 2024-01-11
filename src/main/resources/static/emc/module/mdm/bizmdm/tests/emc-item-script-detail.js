emcTestMethod.scriptDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var formData = Gikam.getComp('emc-item-script-detail-base-info-form').getData();
                    // form在字段值为空的时候，不会获取到该字段，需要自己设置属性值
                    if(!formData.hasOwnProperty('script')) {
                        formData.script = '';
                    }
                    workspace.window.showMask(true);
                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcItemServiceImpl', [ formData ] ])).done(function(id) {
                        Gikam.getComp('emc-test-item-list-grid').refreshRowById(formData.id);
                        Gikam.getLastModal().close();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.HELP',
                icon : 'move-down',
                onClick : function() {
                    window.open(IFM_CONTEXT + '/计算公式说明文档.docx');
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-item-script-detail-base-info-form',
            columns : 1,
            fields : emcTestMethod.getScriptFormFields(),
            url : Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}', {
                itemId : _this.param.id
            }),
            service : 'emcItemServiceImpl',
            autoSave : false,
            onRendered : function() {
                var activeRow = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                this.setData({
                    scriptTestId : _this.param.testId,
                    scriptTestMethodId : _this.param.testMethodId,
                    scriptTestName : activeRow.ext$.testname,
                    scriptMethodId : _this.param.methodId,
                    scriptMethodName : activeRow.ext$.methodname,
                });
            },
        };
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

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
