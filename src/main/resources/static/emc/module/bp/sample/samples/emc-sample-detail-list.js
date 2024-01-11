emcSampleDetail.editPage = {

    /** 采样单列表 */
    getGridParam : function() {
        var _this = this, requestData = _this.param;
        return {
            type : 'grid',
            id : 'emc-sample-detail-list-grid',
            url : emcSampleDetail.baseUrl + '/queries/raw',
            columns : emcSampleDetail.getSampleListColumns(),
            requestData : requestData,
            page : true,
            pageSize : 50,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.getSampleDetailColums('emc-sample-detail-form', row, true);
               Gikam.getComp('emc-sample-order-list-grid').refresh({
                   url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/raw',
                   requestData : {
                       sampleId_SEQ : row.id
                   },
               })
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            } ]
        }
    },

    /** 批录入 */
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-detail-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = Gikam.getComp('emc-sample-detail-list-grid').getActivedRow();
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : '60%',
            height : 400,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();

                                var propertyDatas = selections.map(row => {
                                    var data = {};
                                    Object.assign(data, formData);
                                    data['bizId'] = row.id;
                                    return data;
                                });

                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/property-tmpls/property-datas/action/batch-update', Gikam.getJsonWrapper({}, [ '', propertyDatas ])).done(function (data) {
                                    _this.getSampleDetailColums('emc-sample-detail-form', activeRow, true);
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            id : 'emc-sample-detail-batch-input-form',
            columns: 2,
            titleWidth: 100,
            fields: [],
            autoSave: false
        });

        Gikam.getAsyncComp('emc-sample-detail-batch-input-form').done(function(form) {
            _this.getSampleDetailColums('emc-sample-detail-batch-input-form', activeRow);
        });

    },

    /** 采样单详情 */
    getSampleDetailColums : function(id, row, loadingValue) {
        var _this = this;
        var fields = [];

        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + row.id + '/judge-samples/' + row.monitorTmplId).done(function(data) {
            var formData = Gikam.getComp(id);
            var readonly =  _this.param.readonly;
            if (Gikam.isEmpty(readonly)) {
                readonly = false;
            }
            fields = data.rows.map(function(e) {
                if (e.fieldType === 'fieldMultiSelect') {
                    var selectFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'select',
                        multiple : true,
                        category : 'sys',
                        readonly : readonly
                    };
                    selectFields['param'] = {
                        codeCategoryId : e.sysCode
                    };
                    if (e.required === '1') {
                        selectFields['validators'] = [ 'notEmpty' ];
                    }
                    return selectFields;
                } else if (e.fieldType === 'fieldSelect') {
                    var selectFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'select',
                        search : true,
                        category : 'sys',
                        readonly : readonly
                    };
                    selectFields['param'] = {
                        codeCategoryId : e.sysCode
                    };
                    if (e.required === '1') {
                        selectFields['validators'] = [ 'notEmpty' ];
                    }
                    return selectFields;
                } else if (e.fieldType === 'fieldContext') {
                    var contextFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        validators : [],
                        readonly : readonly
                    };
                    if (e.required === '1') {
                        contextFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    if (e.defaultValue) {
                        contextFields['value'] = e.defaultValue;
                    }
                    return contextFields;
                } else if (e.fieldType === 'fieldContextArea') {
                    var contextAreaFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        validators : [],
                        readonly : readonly
                    };
                    if (e.required === '1') {
                        contextAreaFields.validators.push('notEmpty');
                    }
                    if (e.fieldLength) {
                        contextAreaFields.validators.push('strLength[0,' + e.fieldLength + ']');
                    }
                    return contextAreaFields;
                } else if (e.fieldType === 'fieldDate') {
                    var dateFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'date',
                        readonly : readonly
                    };
                    if (e.required === '1') {
                        dateFields['validators'] = [ 'notEmpty' ];
                    }
                    return dateFields;
                }
            });
            formData.options.fields = fields;
            formData.options.titleWidth = 100;
            var panels = [{ fields: fields }];
            formData.cacheSelectItems(panels).done(() => {
                formData.cleanData();
                formData.options.panels = panels;
                formData.validators = {};
                formData.registerValidators();
                if (loadingValue) {
                    formData.refresh({
                        url : Gikam.printf('/secure/emc/module/mdm/basemdm/property-tmpls/property-fields/{id}/queries/property-datas/{tmplId}', {
                            id : row.id, // sampleId --> bizId
                            tmplId : row.ext$.tmplid
                        }),
                    });
                }
            });
        });
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-sample-detail-form',
            fields : [],
            service : 'emcPropertyDataServiceImpl',
        }
    },

    /** 样品列表 */
    getOrderGridParam : function() {
        return {
            type : 'grid',
            id : 'emc-sample-order-list-grid',
            url : '',
            columns : emcSampleDetail.getOrderListColumns(),
            filter : false,
            page : false,
            pageSize : 50,
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : '30%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '55%',
                        items : [ _this.getBaseInfoForm() ]
                    },
                    center : {
                        title : Gikam.propI18N('T_EMC_ORDER.SERVICE_NAME'),
                        items : [ _this.getOrderGridParam() ]
                    }
                } ]
            }
        });

        Gikam.getAsyncComp('emc-sample-detail-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
