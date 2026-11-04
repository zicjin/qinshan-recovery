coreElnTmpl.testTmplPage = {

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
            id : 'core-eln-tmpl-test-info-form',
            url : _this.getCompUrl('testTmplForm'),
            fields : coreElnTmpl.getTestTmplInfoFormFields(),
            autoSave : false,
            columns : 3,
            onRendered : function() {
                _this.testTmplForm = this;
            }
        }
    },

    confirm : function() {
        var _this = this;
        var data = _this.testTmplForm.getData();
        if ((!Gikam.isEmpty(data.elnTmplParam) && _this.isJsonString(data.elnTmplParam)) || Gikam.isEmpty(data.elnTmplParam)) {
            var url = Gikam.printf(coreElnTmpl.baseUrl + '/{id}/versions', {
                id : _this.param.elnTmplId
            });
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ data ] ])).done(function() {
                _this.testTmpl(data.id);
            });
        } else {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.INPUT_JSON_STRING');
        }
    },

    testTmpl : function(versionId) {
        var _this = this;
        var url = IFM_CONTEXT + '/static/plugin/spread-sheets-11.2.6/designer/index/index.html' + Gikam.param({
            operateType : 1,
            versionId : versionId,
            IFM_CONTEXT : IFM_CONTEXT,
            isTest : true,
            elnTmplId : _this.param.elnTmplId
        })
        window.open(url, 'eln');
    },

    urlManager : {
        testTmplForm : coreElnTmpl.baseUrl + '/{id}/versions/{versionId}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-eln-tmpl-test-info-form' : this.getCompUrl('testTmplForm')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.elnTmplId,
            versionId : this.param.id
        });
    },

    isJsonString : function(str) {
        try {
            JSON.parse(str);
            return true
        } catch (err) {
            return false
        }
    }

};