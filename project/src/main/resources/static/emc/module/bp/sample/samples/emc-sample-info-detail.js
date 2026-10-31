emcSample.sampleInfoDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        var readonly =  _this.param.readonly;
        if (Gikam.isEmpty(readonly)) {
            readonly = false;
        }
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'back',
                hide : readonly,
                onClick : function() {
                    Gikam.getLastModal().close();
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
            var readonly =  _this.param.readonly;
            if (Gikam.isEmpty(readonly)) {
                readonly = false;
            }
            data.rows.sort((item1, item2) => parseInt(item1.orderNo) - parseInt(item2.orderNo));

            fields = data.rows.map(function(e) {
                if (e.fieldType == 'fieldMultiSelect') {
                    var selectFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'select',
                        multiple : true,
                        category : 'sys',
                        readonly : false,
                        editor : true,
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
                        editor : true,
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
                        readonly : readonly
                    };
                    if (e.required == '1') {
                        contextFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    if (e.defaultValue) {
                        contextFields['value'] = e.defaultValue;
                    }
                    return contextFields;
                } else if (e.fieldType == 'fieldContextArea') {
                    var contextAreaFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        validators : [],
                        readonly : readonly
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
                        readonly : readonly
                    };
                    if (e.required == '1') {
                        dateFields['validators'] = [ 'notEmpty' ];
                    }
                    return dateFields;
                }
            })
            formData.options.fields = fields;
            formData.options.titleWidthAuto=true;
            var panels = [{ fields: fields }]
            formData.cacheSelectItems(panels).done(() => {
                formData.cleanData();
                formData.options.panels = panels;
                formData.validators = {};
                formData.registerValidators();
                formData.refresh({
                    url : Gikam.printf('/secure/emc/module/mdm/basemdm/property-tmpls/property-fields/{id}/queries/property-datas/{tmplId}', {
                        id : _this.param.id, // sampleId --> bizId
                        tmplId : _this.param.tmplId
                    }),
                });
            });
        });
    },

    baseForm : null,
    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-sample-info-detail-form',
            fields : [],
            service : 'emcPropertyDataServiceImpl',
            onRendered : function(data){
                this.disableAutoSave();
                _this.getSampleDetailColums();
                this.enableAutoSave();
            },
            onLoadSuccess:function(){
                this.setOptions({ url : ''});
            }
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
    }
}
