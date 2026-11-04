lcdpBizProcess.scriptEdit = {

    getBtnToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                hidden : Gikam.isTrue(_this.param.readonly),
                onClick : function () {
                    var formData = Gikam.getComp('lcdp-biz-process-script-edit-form').getData();

                    workspace.window.showMask(true);
                    Gikam.put(lcdpBizProcess.baseUrl + '/action/save-script', Gikam.getJsonWrapper({
                        scriptType : _this.param.scriptType
                    }, [ 'lcdpBizProcessNodeActionServiceImpl', [ formData ] ])).done(function (id) {
                        Gikam.getLastModal().close();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'form',
            url : Gikam.printf(lcdpBizProcess.baseUrl + '/node-actions/{bpActionId}/{scriptType}/queries', {
                bpActionId : _this.param.actionId,
                scriptType : _this.param.scriptType
            }),
            service : 'lcdpBizProcessNodeActionServiceImpl',
            id : 'lcdp-biz-process-script-edit-form',
            columns : 1,
            autoSave : false,
            fields : lcdpBizProcess.getScriptEditFormFields(),
            onRendered : function () {
                var formFields = lcdpBizProcess.getScriptEditFormFields();
                if (_this.param.scriptType == 'action') {
                    formFields = formFields.concat([ {
                        field : 'actionScript',
                        type : 'codeEditor',
                        language : 'java',
                        readonly : Gikam.isTrue(_this.param.readonly),
                        height : 560
                    } ]);
                } else if (_this.param.scriptType == 'checkJs') {
                    formFields = formFields.concat([ {
                        field : 'checkScriptJs',
                        type : 'codeEditor',
                        language : 'javascript',
                        readonly : Gikam.isTrue(_this.param.readonly),
                        height : 560
                    } ]);
                } else if (_this.param.scriptType == 'checkJava') {
                    formFields = formFields.concat([ {
                        field : 'checkScriptJava',
                        type : 'codeEditor',
                        language : 'java',
                        readonly : Gikam.isTrue(_this.param.readonly),
                        height : 560
                    } ]);
                }
                Gikam.getComp('lcdp-biz-process-script-edit-form').refreshFields(formFields);
            }
        }
    },

    createPage : function () {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function (param) {
        this.param = param;
        this.createPage();
    }
}
