emcSamplingPlan.monitorPropertyPage = {

    getMonitorPointGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-plan-monitor-property-list-grid',
            url : '/secure/emc/module/bp/bp/folders/queries',
            checkOneOnActive : true,
            requestData : _this.param,
            onRowActive : function(index, row) {
                _this.onMonitorPointActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('emc-sampling-plan-monitor-property-form').done(function(grid){
                        grid.cleanData();
                    });
                }
            },
            columns : emcSamplingPlan.getMonitorPointColumns(),
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

    onMonitorPointActive : function(row) {
        var _this = this;
        if (row) {
            _this.getPropertyDetailColumns('emc-sampling-plan-monitor-property-form', row, true);
        }
    },

    /** 批录入 */
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sampling-plan-monitor-property-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = Gikam.getComp('emc-sampling-plan-monitor-property-list-grid').getActivedRow();
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 700,
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
                                    _this.getPropertyDetailColumns('emc-sampling-plan-monitor-property-form', activeRow, true);
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
            id : 'emc-monitor-property-batch-input-form',
            columns: 2,
            titleWidth: 100,
            fields: [],
            autoSave: false
        });

        Gikam.getAsyncComp('emc-monitor-property-batch-input-form').done(function(form) {
            _this.getPropertyDetailColumns('emc-monitor-property-batch-input-form', activeRow);
        });

    },

    /** 点位属性详情 */
    getPropertyDetailColumns : function(id, row, loadingValue) {
        var _this = this;
        var fields = [];
        var url = Gikam.printf('/secure/emc/module/mdm/basemdm/property-tmpls/monitor-tmpls/{monitorTmplId}/property-fields/queries', {
            monitorTmplId : row.monitorTmplId
        });
        Gikam.getJson(url).done(function(data) {
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
                        url : Gikam.printf('/secure/emc/module/mdm/basemdm/property-tmpls/folders/{folderId}/property-data/queries', {
                            folderId : row.id
                        }),
                    });
                }
            });
        });
    },

    getMonitorPropertyGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.MONITORPOINT_PROPERTY',
                items : [ {
                    type : 'form',
                    id : 'emc-sampling-plan-monitor-property-form',
                    fields : [],
                    service : 'emcPropertyDataServiceImpl',
                    columns : 1,
                    autoSave : true,
                }, ]
            } ]
        }
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sampling-plan-monitor-property-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        // 监测点信息
                        items : [ this.getMonitorPointGridParam() ]
                    },
                    center : {
                        // 对应的监测点属性
                        items : [ this.getMonitorPropertyGridParam() ]
                    }
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }

}
