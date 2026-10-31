emcStdSetCategory.scriptDetailPage = {

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-stand-set-script-detail-form',
            columns : 1,
            fields : _this.getScriptFormFields(),
            service : 'emcStandardSetServiceImpl',
            url : Gikam.printf(emcStdSetCategory.baseUrl + '/stand-sets/{standSetId}', {
                standSetId : _this.param.id
            }),
            onRendered : function() {
                this;
            },
        };
    },

    getScriptFormFields : function() {

        var _this = this;

        var codeCategoryId = _this.param.field + 'Func';

        var fields = [ {
            field : 'func',
            title : 'T_EMC_STANDARD_SET.FUNC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : codeCategoryId
            },
            onChange : function(field, value) {
                var formData = this.getData();

                var script = formData.func + "(); ";

                if (Gikam.isEmpty(formData[_this.param.field])) {
                    formData[_this.param.field] = script;
                } else {
                    formData[_this.param.field] = formData[_this.param.field] + '\r' + script;
                }

                this.setData(formData);

            }
        }, {
            field : _this.param.field,
            title : 'T_EMC_STANDARD_SET.SCRIPT',
            type : 'textarea',
            rows : 10,
            height : 550
        } ];

        return fields;
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoForm() ]
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