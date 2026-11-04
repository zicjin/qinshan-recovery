lcdpApiIntegration.editPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'lcdp-api-integration-edit-list-grid',
            url : lcdpApiIntegration.baseUrl + '/queries/raw',
            service : 'lcdpApiIntegrationServiceImpl',
            dbTable : 'T_LCDP_API',
            columns : lcdpApiIntegration.getEditListColumns(),
            onLoadSuccess : function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'lcdp-api-integration-edit-list-base-info-form', 'lcdp-api-integration-edit-list-field-grid' ]);
                }
            },
            deleteFormatter : function (row) {
                return row.apiName || row.id;
            },
            activateFormatter : function (row) {
                return row.apiName || row.id;
            },
            deactivateFormatter : function (row) {
                return row.apiName || row.id;
            },
            onRowActive : function (index, row) {

                //测试panel
                var testForm = Gikam.getComp('lcdp-api-integration-test-base-info-form');

                testForm.cleanData();

                //字段显隐
                if (row.apiType === 'Restful') {
                    testForm.showFields([ 'testReqBody' ])
                    testForm.hideFields([ 'testOperation', 'testParams' ])
                } else {
                    testForm.hideFields([ 'testReqBody' ])
                    testForm.showFields([ 'testOperation', 'testParams' ])
                }

                //接口响应字段panel 显隐
                if (row.callType === 'outer') {
                    Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').removePanel('lcdp-api-integration-edit-list-field-panel')
                    if (row.apiType === 'WebService') {
                        Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').revertPanel('lcdp-api-integration-method-definition-panel')
                        Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').revertPanel('lcdp-api-integration-entity-definition-panel')
                    } else {
                        Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').removePanel('lcdp-api-integration-method-definition-panel')
                        Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').removePanel('lcdp-api-integration-entity-definition-panel')
                    }
                } else {
                    Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').revertPanel('lcdp-api-integration-edit-list-field-panel')
                    Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').removePanel('lcdp-api-integration-method-definition-panel')
                    Gikam.getComp('lcdp-api-integration-edit-list-base-info-tab').removePanel('lcdp-api-integration-entity-definition-panel')
                }


                //soap配置设置
                var method = Gikam.getComp('lcdp-api-integration-method-info-form')
                var result = Gikam.getComp('lcdp-api-integration-method-result-form')
                var param = Gikam.getComp('lcdp-api-integration-method-param-grid')
                var entity = Gikam.getComp('lcdp-api-integration-entity-grid')

                //刷新数据
                Gikam.getAsyncComp('lcdp-api-integration-edit-list-base-info-form').done(function (form) {
                    Gikam.getJson(Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}', {
                        id : row.id
                    })).done(function (formData) {

                        form.refresh({
                            url : Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}', {
                                id : row.id
                            })
                        });

                        if (Gikam.isNotEmpty(formData.soapConfig)) {
                            var soapConfig = JSON.parse(formData.soapConfig)

                            lcdpApiIntegration.soapConfig = soapConfig

                            method.loadData({operationName : soapConfig.webService.webMethod.operationName})
                            result.loadData(soapConfig.webService.webMethod.webResult)
                            param.loadData(soapConfig.webService.webMethod.webParam)
                            entity.loadData(soapConfig.entityConfig)
                        } else {

                            lcdpApiIntegration.soapConfig.entityConfig = []
                            lcdpApiIntegration.soapConfig.webService.webMethod = {
                                operationName : '',
                                webResult : {},
                                webParam : []
                            }

                            method.cleanData()
                            result.cleanData()
                            param.cleanData()
                            entity.cleanData()
                        }

                        var data = result.getData()
                        var value = data.paramType
                        var items = []
                        if (value === 'bean') {
                            items = lcdpApiIntegration.soapConfig.entityConfig.map(function (v) {
                                return {text : v.entityName, value : v.entityName}
                            })
                        } else if (value === 'simple') {
                            items = lcdpApiIntegration.simpleDefinition.map(function (v) {
                                return {text : v, value : v}
                            })
                        }

                        result.setSelectOptions('paramDefinition', items)
                        form.setReadonly(row.activatedFlag === '1')
                    });
                });

                Gikam.getAsyncComp('lcdp-api-integration-list-uploader').done(function (uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    })
                });

                testForm.refresh({
                    url : Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}', {
                        id : row.id
                    })
                });

                Gikam.getAsyncComp('lcdp-api-integration-edit-list-field-grid').done(function (grid) {
                    grid.refresh({
                        url : Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}/fields/queries', {
                            id : row.id
                        })
                    });
                });

                //只读设置
                if (row.activatedFlag === '1') {
                    Gikam.getComp('lcdp-api-integration-edit-list-field-grid').hideToolbar()
                    param.hideToolbar()
                    entity.hideToolbar()
                } else {
                    Gikam.getComp('lcdp-api-integration-edit-list-field-grid').showToolbar()
                    param.showToolbar()
                    entity.showToolbar()
                }
                Gikam.getComp('lcdp-api-integration-edit-list-field-grid').setReadonly(row.activatedFlag === '1')
                Gikam.getComp('lcdp-api-integration-list-uploader').setToolbarVisible(row.activatedFlag != '1')
                method.setReadonly(row.activatedFlag === '1')
                result.setReadonly(row.activatedFlag === '1')
                param.setReadonly(row.activatedFlag === '1')
                entity.setReadonly(row.activatedFlag === '1')

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'LCDP.MODULE.API_INTEGRATIONS.MODAL.ADD',
                        id : 'lcdp-api-integration-edit-list-preInsert',
                        fields : lcdpApiIntegration.getPreInsertFormFields(),
                        url : lcdpApiIntegration.baseUrl
                    }).done(function (id) {
                        Gikam.getComp('lcdp-api-integration-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function () {
                    Gikam.getComp('lcdp-api-integration-edit-list-grid').deleteRows(lcdpApiIntegration.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function () {
                    var selections = Gikam.getComp('lcdp-api-integration-edit-list-grid').getSelections();

                    if (selections.length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    if (selections[ 0 ].callType === 'outer') {
                        if (Gikam.isEmpty(selections[ 0 ].scriptMethodPath)) {
                            Gikam.alert("LCDP.MODULE.API_INTEGRATION.TIP.METHOD_NOT_EMPTY");
                            return;
                        }

                        if (Gikam.isEmpty(selections[ 0 ].apiUrl)) {
                            Gikam.alert("LCDP.MODULE.API_INTEGRATION.TIP.URL_NOT_EMPTY");
                            return;
                        }

                        if (selections[ 0 ].apiType === 'WebService') {
                            //soap配置校验
                            var entity = Gikam.getComp('lcdp-api-integration-entity-grid')
                            var param = Gikam.getComp('lcdp-api-integration-method-param-grid')

                            if (!entity.validate() || !param.validate()) {
                                return;
                            }
                        }

                        var valid = true;

                        Gikam.postSync(Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}/action/validate-url', {
                            id : selections[ 0 ].id
                        }), Gikam.getJsonWrapper({
                            url : selections[ 0 ].apiUrl
                        })).done(function (data) {
                            if (!data.valid) {
                                Gikam.alert("LCDP.MODULE.API_INTEGRATION.TIP.URL_DISABLE");
                                valid = data.valid
                            }
                        })

                        if (!valid) {
                            return;
                        }
                    }

                    Gikam.getComp('lcdp-api-integration-edit-list-grid').activateRows(lcdpApiIntegration.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function () {
                    Gikam.getComp('lcdp-api-integration-edit-list-grid').deactivateRows(lcdpApiIntegration.baseUrl + '/action/deactivate');
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'tab',
            id : 'lcdp-api-integration-edit-list-base-info-tab',
            panels : [ {
                title : 'LCDP.MODULE.API_INTEGRATIONS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'lcdpApiIntegrationServiceImpl',
                    dbTable : 'T_LCDP_API',
                    id : 'lcdp-api-integration-edit-list-base-info-form',
                    autoSave : true,
                    fields : lcdpApiIntegration.getBaseInfoFormInvokeFields(),
                    onUpdated : function () {
                        var grid = Gikam.getComp('lcdp-api-integration-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                    onLoadSuccess : function () {
                        lcdpApiIntegration.setOuterUrl()
                    }
                } ]
            }, _this.getFieldGrid(), _this.getTestTab(), _this.getWSEntityDefinition(), _this.getWSMethodDefinition(), _this.getAttachmentTab() ]
        }
    },

    getFieldGrid : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.API_INTEGRATIONS.TAB.FIELD',
            id : 'lcdp-api-integration-edit-list-field-panel',
            items : [ {
                type : 'grid',
                id : 'lcdp-api-integration-edit-list-field-grid',
                service : 'lcdpApiFieldServiceImpl',
                dbTable : 'T_LCDP_API_FIELD',
                deleteFormatter : function (row) {
                    return row.apiField || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function () {
                        Gikam.getComp('lcdp-api-integration-edit-list-field-grid').insert(Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}/fields', {
                            id : Gikam.getComp('lcdp-api-integration-edit-list-grid').getActivedRow().id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function () {
                        Gikam.getComp('lcdp-api-integration-edit-list-field-grid').deleteRows(Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}/fields', {
                            id : Gikam.getComp('lcdp-api-integration-edit-list-grid').getActivedRow().id
                        }));
                    }
                } ],
                columns : lcdpApiIntegration.getFieldListColumns()
            } ]
        };
    },

    getWSMethodDefinition : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.API_INTEGRATIONS.TAB.METHODDEFINITION',
            id : 'lcdp-api-integration-method-definition-panel',
            items : [ {
                type : 'form',
                id : 'lcdp-api-integration-method-info-form',
                autoSave : true,
                fields : lcdpApiIntegration.getWSMethodColumn(),
                onBeforeUpdate : function (data) {
                    lcdpApiIntegration.soapConfig.webService.webMethod.operationName = data.operationName
                    _this.updatedWebServiceConfig()
                }
            }, {
                type : 'form',
                id : 'lcdp-api-integration-method-result-form',
                panels : [ {
                    title : '方法返回参数',
                    fields : lcdpApiIntegration.getMethodResultColumn(),
                } ],
                autoSave : true,
                onBeforeUpdate : function (data) {
                    var form = Gikam.getComp('lcdp-api-integration-method-result-form')
                    var data = form.getData()
                    lcdpApiIntegration.soapConfig.webService.webMethod.webResult = data
                    _this.updatedWebServiceConfig()
                }
            }, {
                type : 'grid',
                id : 'lcdp-api-integration-method-param-grid',
                autoSave : true,
                columns : lcdpApiIntegration.getMethodParamColumn(),
                onBeforeUpdate : function () {
                    var grid = Gikam.getComp('lcdp-api-integration-method-param-grid')
                    var data = grid.getData()
                    lcdpApiIntegration.soapConfig.webService.webMethod.webParam = data
                    _this.updatedWebServiceConfig()
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function () {
                        var grid = Gikam.getComp('lcdp-api-integration-method-param-grid')
                        var data = grid.getData()
                        data.push({
                            name : '',
                            paramType : '',
                            paramDefinition : '',
                            partName : '',
                            targetName : ''
                        })
                        grid.loadData(data)
                        lcdpApiIntegration.soapConfig.webService.webMethod.webParam = data
                        _this.updatedWebServiceConfig()
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function () {
                        var grid = Gikam.getComp('lcdp-api-integration-method-param-grid')
                        var data = grid.getData()
                        var selections = grid.getSelections()

                        var finalData = data.filter(function (row) {
                            return selections.every(function (value) {
                                return row.index != value.index
                            })
                        })

                        if (Gikam.isEmpty(finalData)) {
                            grid.cleanData()
                        } else {
                            grid.loadData(finalData)
                        }

                        _this.updatedWebServiceConfig()
                    }
                } ]
            } ]
        };
    },

    getWSEntityDefinition : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.API_INTEGRATIONS.TAB.ENTITYDEFINITION',
            id : 'lcdp-api-integration-entity-definition-panel',
            items : [ {
                type : 'grid',
                id : 'lcdp-api-integration-entity-grid',
                columns : lcdpApiIntegration.getEntityColumn(),
                editorInvisible : true,
                onBeforeUpdate : function () {
                    var grid = Gikam.getComp('lcdp-api-integration-entity-grid')
                    var data = grid.getData()
                    lcdpApiIntegration.soapConfig.entityConfig = data
                    _this.updatedWebServiceConfig()
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function () {
                        var grid = Gikam.getComp('lcdp-api-integration-entity-grid')
                        var data = grid.getData()
                        data.push({
                            entityName : '',
                            entityExample : ''
                        })
                        grid.loadData(data)
                        lcdpApiIntegration.soapConfig.entityConfig = data
                        _this.updatedWebServiceConfig()
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function () {
                        var grid = Gikam.getComp('lcdp-api-integration-entity-grid')
                        var data = grid.getData()
                        var selections = grid.getSelections()

                        var finalData = data.filter(function (row) {
                            return selections.every(function (value) {
                                return row.index != value.index
                            })
                        })

                        if (Gikam.isEmpty(finalData)) {
                            grid.cleanData()
                        } else {
                            grid.loadData(finalData)
                        }

                        lcdpApiIntegration.soapConfig.entityConfig = finalData

                        _this.updatedWebServiceConfig()
                    }
                } ]
            } ]
        };
    },

    updatedWebServiceConfig : function () {
        var form = Gikam.getComp('lcdp-api-integration-test-base-info-form')

        var data = form.getData();

        data.soapConfig = JSON.stringify(lcdpApiIntegration.soapConfig)

        form.setData(data, true)
    },

    getTestTab : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.API_INTEGRATIONS.TAB.TEST',
            id : 'lcdp-api-integration-edit-list-test-panel',
            items : [ _this.getTestToolbar(), _this.getTestForm() ]
        }
    },

    getTestForm : function () {
        var _this = this;
        return {
            type : 'form',
            service : 'lcdpApiIntegrationServiceImpl',
            dbTable : 'T_LCDP_API',
            id : 'lcdp-api-integration-test-base-info-form',
            autoSave : true,
            fields : lcdpApiIntegration.getTestFormFields()
        }
    },

    getTestToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.EXECUTE',
                iconType : 'start',
                margin : '16px 0 0 0',
                color : '#29CF68FF',
                onClick : function () {
                    var form = Gikam.getComp('lcdp-api-integration-test-base-info-form')
                    var baseForm = Gikam.getComp('lcdp-api-integration-edit-list-base-info-form')
                    var param = baseForm.getData()
                    if (param.apiType === 'WebService' && !form.validate() || !baseForm.validate()) {
                        return;
                    }

                    var data = form.getData()

                    var req = {
                        body : data.testReqBody,
                        operation : data.testOperation,
                        args : data.testParams
                    }

                    form.openMask()

                    Gikam.post(Gikam.printf(lcdpApiIntegration.baseUrl + '/{apiCode}/action/test', {
                        apiCode : param.apiCode
                    }), JSON.stringify(req)).done(function (res) {
                        data.response = JSON.stringify(res, null, '\t')
                        form.setData(data, false)
                    }).fail(function (info) {
                        try {
                            var error = JSON.parse(info.responseText)
                            data.response = error.message
                        } catch (e) {
                            data.response = info.responseText
                        }
                        form.setData(data, false)
                    }).always(function () {
                        form.closeMask()
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                iconType : 'check',
                margin : '16px 0 0 0',
                color : '#29CF68FF',
                onClick : function () {
                    var form = Gikam.getComp('lcdp-api-integration-test-base-info-form')

                    var modal = Gikam.create('modal', {
                        id : 'lcdp-api-integration-test-modal',
                        title : 'T_LCDP_API.RESPONSE',
                        showCloseBtn : true,
                    });
                    Gikam.create('layout', {
                        renderTo : modal.window.$dom,
                        center : {
                            items : [ {
                                type : 'codeEditor',
                                id : 'core-form-test',
                                language : 'json',
                                height : '100%',
                                value : form.getData().response
                            } ]
                        }
                    });
                }
            } ]
        }
    },

    getAttachmentTab : function () {
        var _this = this;
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'lcdp-api-integration-list-uploader',
                dbTable : 'T_LCDP_API'
            } ]
        }
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'lcdp-api-integration-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '55%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
