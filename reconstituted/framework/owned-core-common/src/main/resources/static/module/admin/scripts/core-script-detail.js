coreScript.detailPage = {

    getBtnToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.EXECUTE',
                icon : 'view-audit',
                onClick : function () {
                    var form = Gikam.getComp('core-admin-script-form');

                    var script = form.getData().script;

                    if (Gikam.isNotEmpty(script)) {
                        Gikam.post(coreScript.baseUrl + '/action/execute', Gikam.getJsonWrapper({
                            script : script,
                        })).done(function (r) {
                            form.setData(r);
                        });
                    }
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                items : [ {
                    type : 'form',
                    id : 'core-admin-script-form',
                    panels : coreScript.getScriptPannel(),
                } ]
            } ]
        }
    },

    createPage : function () {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function (param) {
        this.param = param;
        this.createPage();
    }
}