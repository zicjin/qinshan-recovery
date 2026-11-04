coreOfficeTmpl.testTmplPage = {

    init : function(param) {
        this.param = param;
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getBtnToolbarParam(), this.getTestTmplInfoTab() ]
            }
        });
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        _this.confirm();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },

    testTmplForm : null,
    getTestTmplInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'core-office-tmpl-test-form',
            fields : _this.param.dynamicFields && JSON.parse(decodeURI(_this.param.dynamicFields)),
            autoSave : false,
            columns : 3,
            onRendered : function() {
                _this.testTmplForm = this;
            }
        }
    },

    confirm : function() {
        var _this = this;
        var validate = _this.testTmplForm.validate(null, true);
        if (validate) {
            var data = _this.testTmplForm.getData();
            _this.testTmpl(_this.param.tmplId, data);
        }
    },

    testTmpl : function(tmplId, testData) {
        var _this = this;

        var testJsonWrapper = Gikam.getJsonWrapper({
            officeparam : testData
        }, [ null, [] ]);

        Gikam.getLastModal().window.showMask(true);
        Gikam.postText(Gikam.printf(coreOfficeTmpl.baseUrl + '/{id}/action/test-generate-instance', {
            id : tmplId
        }), testJsonWrapper).done(function(instanceFileUrl) {

            Gikam.getLastModal() && Gikam.getLastModal().window && Gikam.getLastModal().window.closeMask();

            Gikam.openOfficeFileByUrl({
                fileUrl : instanceFileUrl
            })
        }).fail(function() {
           Gikam.getLastModal() && Gikam.getLastModal().window && Gikam.getLastModal().window.closeMask();
        });
    }

};