emcSampleEdit.sampleInfoDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var comp = Gikam.getComp('emc-sample-info-detail-form');
                    if (comp.validate()) {
                        var formData = comp.getData();
                        
                        //没填写值的，默认是""
                        var fields = comp.getOptions().fields;
                        fields.map(function(field) {
                            var name = field.field;
                            if(!formData.hasOwnProperty(name)){
                                formData[name]="";
                            }
                        });
                        workspace.window.showMask(true);
                        Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcPropertyDataServiceImpl', [ formData ] ])).done(function(id) {
                            Gikam.getLastModal().close();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            } ]
        }
    },

    getSampleDetailColums : function() {
        var columns = [];
        var _this = this;

        var fields = [];

        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + _this.param.id + '/judge-samples/' + _this.param.monitorTmplId).done(function(data) {
            var formData = Gikam.getComp('emc-sample-info-detail-form');
            fields = data.rows.map(function(e) {
                if (e.fieldType == 'fieldMultiSelect') {
                    var selectFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'comboBox',
                        multiple : true,
                        category : 'sys',
                        readonly : false,
                    };
                    selectFields['param'] = {
                        codeCategoryId : e.sysCode
                    };
                    if (e.required == '1') {
                        selectFields['validators'] = [ 'notEmpty' ];
                    }
                    return selectFields;
                } else if (e.fieldType == 'fieldSelect') {
                    var selectFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'comboBox',
                        search : true,
                        category : 'sys',
                        readonly : false,
                    };
                    selectFields['param'] = {
                        codeCategoryId : e.sysCode
                    };
                    if (e.required == '1') {
                        selectFields['validators'] = [ 'notEmpty' ];
                    }
                    return selectFields;
                } else if (e.fieldType == 'fieldContext') {
                    var contextFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        validators : [],
                    };
                    if (e.required == '1') {
                        contextFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    if (e.defaultValue) {
                        contextFields['value'] = [ e.defaultValue ];
                    }
                    return contextFields;
                } else if (e.fieldType == 'fieldContextArea') {
                    var contextAreaFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        validators : [],
                    };
                    if (e.required == '1') {
                        contextAreaFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextAreaFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    return contextAreaFields;
                } else if (e.fieldType == 'fieldDate') {
                    var dateFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'date',
                    };
                    if (e.required == '1') {
                        dateFields['validators'] = [ 'notEmpty' ];
                    }
                    return dateFields;
                }
            })
            formData.options.fields = fields;
            formData.registerValidators();
            formData.refreshFields([ {
                fields : fields
            } ]);
            Gikam.getComp('emc-sample-info-detail-form').refresh();

        });
    },

    baseForm : null,
    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-sample-info-detail-form',
            fields : [],
            autoSave : false,
            // 展示数据
            url : Gikam.printf('/secure/emc/module/mdm/basemdm/property-tmpls/property-fields/{id}/queries/property-datas/{tmplId}', {
                id : _this.param.id, // sampleId --> bizId
                tmplId : _this.param.tmplId
            }),
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
        emcSampleEdit.sampleInfoDetailPage.getSampleDetailColums();
    }
}
