emcPropertyTmpl.fieldDetailPage = {

    getFieldFormDetailFields : function() {
        var columns = [];
        columns.push({
            title : 'T_EMC_PROPERTY_FIELD.INTERNALFIELD',
            field : 'internalField',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'internalField'
            },
        }, {
            title : 'T_EMC_PROPERTY_FIELD.FIELDNAME',
            field : 'fieldName',
            validators : [ 'strLength[0,33]' ],
        }, {
            title : 'T_EMC_PROPERTY_FIELD.FIELDTYPE',
            field : 'fieldType',
            readOnly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'fieldType'
            }
        }, {
            title : 'T_EMC_PROPERTY_FIELD.REQUIRED',
            field : 'required',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            title : 'T_EMC_PROPERTY_FIELD.ORDERNO',
            field : 'orderNo',
            validators : [ 'integer','strLength[0,6]' ],
        });

        var _this = this;
        var fieldType = _this.param.fieldType;
        if (fieldType === 'fieldContext') { // 文本时添加 默认值, 字段长度
            columns.push({
                title : 'T_EMC_PROPERTY_FIELD.DEFAULTVALUE',
                field : 'defaultValue',
                type : 'textarea',
                width : 1000,
                height : 150,
                validators : [ 'strLength[0,500]' ],
            }, {
                title : 'T_EMC_PROPERTY_FIELD.FIELDLENGTH',
                field : 'fieldLength',
                validators : [ 'number' ],
            });
        } else if (fieldType === 'script') { // 文本时添加 默认值, 字段长度
            columns.push({
                title : 'T_EMC_PROPERTY_FIELD.DEFAULTVALUE',
                field : 'defaultValue',
                type : 'script',
                width : '100%',
                height : 150,
                validators : [ 'strLength[0,500]' ],
            }, {
                title : 'T_EMC_PROPERTY_FIELD.FIELDLENGTH',
                field : 'fieldLength',
                validators : [ 'number' ],
            });
        } else if (fieldType === "fieldContextArea") { // 字段类型文本 文本域 显示字段长度
            columns.push({
                title : 'T_EMC_PROPERTY_FIELD.FIELDLENGTH',
                field : 'fieldLength',
                validators : [ 'number' ],
            });
        } else if (fieldType === "fieldSelect" || fieldType === 'fieldMultiSelect') { // 下拉框显示 系统编码
            columns.push({
                title : 'T_EMC_PROPERTY_FIELD.SYSCODE',
                field : 'sysCode',
                type : 'select',
                search : true,
                category : 'codeCategory',
                onChange : function (field, value, rowIndex, oldValue, text) {
                    if (value) {
                        var url = IFM_CONTEXT + '/secure/core/module/sys/codes' + '/queries/selectable?codeCategoryId=' + value;
                        var items = [];
                        Gikam.getJsonSync(url).done(function (data) {
                            if (data) {
                                for (var code of data) {
                                    items.push({
                                        text : code.codeName,
                                        value : code.code
                                    });
                                }
                            }
                        })
                        Gikam.getComp('emc-property-field-detail-grid').setSelectOptions('defaultValue', items);
                    }
                }
            });
            columns.push({
                title : 'T_EMC_PROPERTY_FIELD.DEFAULTVALUE',
                field : 'defaultValue',
                type : 'select',
                search : true,
                category : 'sys',
                param : {
                    codeCategoryId : '-1'
                },
            })
        }
        return columns;
    },

    baseForm : null,
    getBaseInfoForm : function() {
        var _this = this;

        return {
            type : 'form',
            id : 'emc-property-field-detail-grid',
            fields : emcPropertyTmpl.fieldDetailPage.getFieldFormDetailFields(),
            service : 'emcPropertyFieldServiceImpl',
            url : Gikam.printf(emcPropertyTmpl.baseUrl + '/fields/{itemId}', {
                itemId : _this.param.id
            }),
            onRendered : function() {
                var formData = Gikam.getComp('emc-property-field-detail-grid').getData();
                _this.baseForm = this;
                if (_this.param.id) {
                    this.toFieldsReadonly([ 'fieldType' ]);
                }
            },
            onLoadSuccess : (data)=> {
                if (Gikam.isNotEmpty(data.sysCode)){
                    var url = IFM_CONTEXT + '/secure/core/module/sys/codes' + '/queries/selectable?codeCategoryId=' + data.sysCode;
                    var items = [];
                    Gikam.getJsonSync(url).done(function (data1) {
                        if (Gikam.isNotEmpty(data1)) {
                            for (var code of data1) {
                                items.push({
                                    text : code.codeName,
                                    value : code.code
                                });
                            }
                        }
                    })
                    Gikam.getComp('emc-property-field-detail-grid').setSelectOptions('defaultValue', items);
                }
            }
        }
    },

    urlManager : {
        fieldGrid : emcPropertyTmpl.baseUrl + '/{id}/fields/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-property-field-detail-grid' : this.getCompUrl('fieldGrid'),
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBaseInfoForm() ]
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
