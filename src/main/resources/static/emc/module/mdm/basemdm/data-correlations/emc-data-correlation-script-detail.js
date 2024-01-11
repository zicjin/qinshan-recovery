emcDataCorrelation.scriptDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var formData = Gikam.getComp('emc-data-correlation-script-detail-form').getData();
                    // form在字段值为空的时候，不会获取到该字段，需要自己设置属性值
                    if(!formData.hasOwnProperty('relateScript')) {
                        formData.relateScript = '';
                    }
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcDataCorrelationServiceImpl', [ formData ] ])).done(function(id) {
                        Gikam.getComp('emc-data-correlation-edit-list-grid').refreshRowById(formData.id);
                        Gikam.getLastModal().close();
                    }).always(function() {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BASEMDM.CORRELATIONS.BUTTION.HELP',
                icon : 'move-down',
                onClick : function() {
                    window.open(IFM_CONTEXT + '/计算公式说明文档.docx');
                }
            } ]
        }
    },

    getScriptForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-data-correlation-script-detail-form',
            columns : 1,
            fields : emcDataCorrelation.getScriptFormFields(),
            url : Gikam.printf(emcDataCorrelation.baseUrl + '/{id}', {
                id : _this.param.id
            }),
            service : 'emcDataCorrelationServiceImpl',
            autoSave : false
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getScriptForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}