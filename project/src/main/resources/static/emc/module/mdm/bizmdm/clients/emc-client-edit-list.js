emcClient.editPage = {

    ready : false,
    getClientGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-client-edit-list-grid',
            url : emcClient.baseUrl + '/queries/raw',
            columns : emcClient.getClientListColumns(),
            page : true,
            deleteFormatter : function(row) {
                return row.clientName || row.clientNo || row.id;
            },
            onLoadSuccess : function(data) {
                // 如不准备成功，监测点管理-属性值 会报错
                if (!_this.ready) {
                    _this.ready = true;
                    return;
                }
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onClientActive(row);
            },
            onCellClick : function(field, row) {
                field === 'clientNo' && _this.loadDetailPage(row.id);
            },
            toolbar : [
//            {
//                type : 'button',
//                text : 'GIKAM.BUTTON.INSERT',
//                icon : 'add',
//                onClick : function() {
//                    Gikam.preInsert({
//                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.CLIENT_ADD',
//                        fields : emcClient.getPreInsertFormFields(),
//                        url : emcClient.baseUrl
//                    }).done(function(id) {
//                        _this.loadDetailPage(id);
//                    });
//                }
//            },
            {
               type: 'button',
               text: '新增客户(CRM)',
               icon: 'add',
               onClick: function () {
                   // 添加客户
                   window.open("https://www.fxiaoke.com/XV/UI/Home#paasapp/list/=/appId_CRM/AccountObj");
               }
           },{
              type: 'button',
              text: '按客户同步',
              icon: 'add',
              onClick: function () {
              var modal = Gikam.create('modal', {
                     title : "客户名称",
                     width : 400,
                     height : 200
                 });
                 var layout = Gikam.create('layout', {
                     renderTo : modal.window.$dom,
                     center : {
                         items : [{
                             type : 'btnToolbar',
                             items : [{
                                 type : 'button',
                                 text : 'GIKAM.BUTTON.CONFIRM',
                                 icon : 'select',
                                 onClick : function () {
                                        debugger;
                                       var wrapper = Gikam.getJsonWrapper({"contractName":form.getData().contractName }, [ '', [] ]);
                                         workspace.window.showMask(true);
                                           Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/oa-orgs/sync-byname', wrapper).done(function() {

                                           }).always(function() {
                                               workspace.window.closeMask();
                                                modal.close();
                                           });
                                 }
                             }, {
                                 type : 'button',
                                 text : 'GIKAM.BUTTON.CANCEL',
                                 icon : 'cancel',
                                 onClick : function () {
                                     modal.close();
                                 }
                             }]
                         }]
                     }
                 });
                   var form = Gikam.create('form', {
                        renderTo: layout.options.center.$dom,
                        columns: 1,
                        titleWidth: 80,
                        fields: [{
                            field: 'contractName',
                            title: '客户名称',
                        }],
                    });
          }
      },

            {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-client-edit-list-grid').deleteRows(emcClient.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                        templateCode : 'emcClientExportTemplate',
                        service : 'emcClientExportTemplate'
                    })).done(function(fileUri) {
                        var fileUrl = IFM_CONTEXT + fileUri;
                        Gikam.emcDownloadFile(fileUrl);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_IMPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.importExcel({
                        url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcClientImportFileFunction',
                        dbTable : 'T_EMC_CLIENT',
                        onImportSuccess : function(files) {
                            Gikam.getComp('emc-client-edit-list-grid').refresh();
                        }
                    });
                }
            } ]
        }
    },

    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.CONTACT',
                items : [ {
                    type : 'layout',
                    center : {
                        width : 600,
                        items : [ {
                            type : 'grid',
                            id : 'emc-client-edit-list-contact-grid',
                            service : 'emcClientContactServiceImpl',
                            url : _this.getClientContactUrl(),
                            columns : emcClient.getClientContactColumns(),
                            deleteFormatter : function(row) {
                                return row.lxrName || row.id;
                            },
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                }
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    var activedRow = Gikam.getComp('emc-client-edit-list-grid').getActivedRow();
                                    var url = Gikam.printf(emcClient.baseUrl + '/{id}/contacts', {
                                        id : activedRow.id
                                    });
                                    Gikam.getComp('emc-client-edit-list-contact-grid').insert(url, [ {} ]);
                                },
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    _this.deleteContact();
                                }
                            }, ]
                        } ]
                    },
                }, ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.MONITOR_POINT',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        width : '80%',
                        items : [ {
                            type : 'layout',
                            id : 'emc-client-edit-list-monitor-layout',
                            center : {
                                width : '80%',
                                // 监测点
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-client-edit-list-monitor-point-grid',
                                    url : _this.getMonitorPointUrl(),
                                    columns : emcClient.getMonitorPointColumns(),
                                    service : 'emcMonitorPointServiceImpl',
                                    deleteFormatter : function(row) {
                                        return row.monitorPointName || row.monitorPointNo || row.id;
                                    },
                                    onRowActive : function(index, row) {
                                        // 判空
                                        Gikam.refreshGrids({
                                            'emc-client-edit-list-monitor-point-scheme-grid' : emcClient.baseUrl + '/monitor-points/{monitorPointId}/schemes/queries',
                                        }, {
                                            monitorPointId : row.id,
                                        });
                                        emcClient.editPage.getMonitorPropertyColums(row.id);
                                    },
                                    onCellClick : function(field, row) {
                                        field === 'ext$.indexname' && _this.loadIndexDetailPage(row.indexId);
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add',
                                        onClick : function() {
                                            var activedRow = Gikam.getComp('emc-client-edit-list-grid').getActivedRow();
                                            Gikam.preInsert({
                                                modalTitle : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.ADD_MONITOR_POINT',
                                                height : 300,
                                                fields : emcClient.getMonitorPointFormFields(),
                                                url : emcClient.baseUrl + '/' + activedRow.id +'/monitor-points',
                                            }).done(function(id) {
                                                Gikam.getComp('emc-client-edit-list-monitor-point-grid').refresh();
                                            });
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.COPY',
                                        icon : 'copy',
                                        onClick : function() {
                                            _this.copyMonitorPoint();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            _this.deleteMonitorPoint();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.SET_INDEX',
                                        icon : 'enable',
                                        onClick : function() {
                                            _this.setIndex();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.COPY_SCHEME',
                                        icon : 'copy',
                                        onClick : function() {
                                            _this.copyScheme();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.COPY_INDEX',
                                        icon : 'copy',
                                        onClick : function() {
                                            _this.copyIndex();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.BUTTON.CONFIG_COMPARISON',
                                        icon : 'copy',
                                        onClick : function() {
                                            _this.configComparison();
                                        }
                                    } ]
                                } ]
                            },
                            east : {
                                width : '20%',
                                title : '监测点属性',
                                // collapse：true,
                                // 监测点属性
                                items : [ this.getMonitorProperty() ]
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.MONITOR_POINT_SCHEME',
                            id : 'emc-client-edit-list-monitor-point-scheme-grid',
                            service : 'emcMonitorPointSchemeServiceImpl',
                            url : _this.getMonitorPointSchemeUrl(),
                            deleteFormatter : function(row) {
                                return row.schemeName || row.id;
                            },
                            columns : emcClient.getMonitorPointSchemeColumns(),
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    _this.insertMonitorPointScheme();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    _this.deleteMonitorPointScheme();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.BUTTON.BATCH_UPDATE_NUM',
                                icon : 'edit',
                                onClick : function() {
                                    _this.batchUpdateSampleNum();
                                }
                            } ],
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-client-edit-list-uploader',
                    filter : false,
                    dbTable : 'T_EMC_CLIENT',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            }, ]
        }
    },

    onClientActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-client-edit-list-monitor-point-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getMonitorPointUrl(row.id)
                });
            });
            Gikam.getAsyncComp('emc-client-edit-list-contact-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getClientContactUrl(row.id)
                });
            });
            Gikam.getAsyncComp('emc-client-edit-list-uploader').done(function(grid) {
                grid.setOptions({
                    bizId : row.id
                });
            });
        }
    },

    getMonitorProperty : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-client-edit-list-monitor-property-form',
            fields : [],
            url : '',
            service : 'emcPropertyDataServiceImpl',
            columns : 1,
            autoSave : true,
        }
    },

    getMonitorPropertyColums : function(monitorPointId) {
        var columns = [];
        var _this = this;

        var fields = [];
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/property-fields/queries/' + monitorPointId).done(function(data) {
            var formData = Gikam.getComp('emc-client-edit-list-monitor-property-form');
            fields = data.rows.map(function(e) {
                if (e.fieldType == 'fieldMultiSelect') {
                    var selectFields = {
                        field : e.internalField,
                        title : e.fieldName,
                        type : 'select',
                        multiple : true,
                        category : 'sys',
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
                        type : 'select',
                        search : true,
                        category : 'sys',
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
            var panels = [{ fields: fields }]
            formData.cacheSelectItems(panels).done(() => {
                formData.cleanData();
                formData.options.panels = panels;
                formData.validators = {};
                formData.registerValidators();
                formData.refresh({
                    url : '/secure/emc/module/mdm/basemdm/property-tmpls/monitor-points/queries/property-datas/' + monitorPointId,
                });
            });
        });
    },

    deleteContact : function() {
        var url = Gikam.printf(emcClient.baseUrl + '/contacts');
        Gikam.getComp('emc-client-edit-list-contact-grid').deleteRows(url);
    },

    deleteMonitorPoint : function() {
        var url = Gikam.printf(emcClient.baseUrl + '/monitor-points');
        Gikam.getComp('emc-client-edit-list-monitor-point-grid').deleteRows(url);
    },

    deleteMonitorPointScheme : function() {
        var row = Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').getActivedRow();
        var url = Gikam.printf(emcClient.baseUrl + '/monitor-points/schemes');
        Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').deleteRows(url);
    },

    copyMonitorPoint : function() {
        var _this = this;
        var selections = Gikam.getComp("emc-client-edit-list-monitor-point-grid").getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var url = emcClient.baseUrl + '/monitor-points/copy';
        workspace.window.showMask(true);
        Gikam.postSync(url, Gikam.getJsonWrapper({},[ 'emcMonitorPointServiceImpl', selections ]));
        Gikam.getAsyncComp('emc-client-edit-list-monitor-point-grid').done(function(grid) {
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });

    },

    batchUpdateSampleNum : function() {
        var selections = Gikam.getComp("emc-client-edit-list-monitor-point-scheme-grid").getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activeRow = Gikam.getComp('emc-client-edit-list-monitor-point-grid').getActivedRow();
        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_SAMPLENUM",
            width : 400,
            height : 180,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CONFIRM',
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                selections.map(function (item) {
                                    item.sampleNum = form.options.data.sampleNum
                                });
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcClient.baseUrl + "/" + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcMonitorPointSchemeServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close()
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CANCEL',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcClient.getBatchUpdateSampleNumFields(),
            autoSave: false
        });
    },

    getBaseInfoUrl : function(id) {
        if (id) {
            return Gikam.printf(emcClient.baseUrl + '/{id}', {
                id : id
            })
        }
    },

    getClientContactUrl : function(id) {
        if (id) {
            return Gikam.printf(emcClient.baseUrl + '/{id}/contacts/queries', {
                id : id
            })
        }
    },

    getMonitorPointUrl : function(objectId) {
        if (objectId) {
            return Gikam.printf(emcClient.baseUrl + '/{id}/monitor-points/queries', {
                id : objectId
            })
        }
    },

    getMonitorPointSchemeUrl : function(id) {
        if (id) {
            return Gikam.printf(emcClient.baseUrl + '/monitor-points/{monitorPointId}/schemes/queries', {
                monitorPointId : id
            })
        }
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.BASE_INFO',
            height : 400,
            url : emcClient.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-client-edit-list-grid').refresh();
            }
        });
    },

    loadIndexDetailPage : function(id) {
        Gikam.create('modal', {
            height : 400,
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/page/order-index-detail' + Gikam.param({
                id : id,
            })
        });
    },

    insertMonitorPointScheme : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-client-edit-list-monitor-point-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (Gikam.isEmpty(selections[0].monitorPointCategoryId)) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.CLIENTS.TIP.NO_MONITORPOINTCATEGORY');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.MONITOR_POINT_SCHEME_ADD',
            width : '45%',
            url : Gikam.choose.getConfig('monitorScheme').url + Gikam.param({
                target_unassigned : '1',
                targetTable1 : 'T_EMC_MONITOR_POINT_SCHEME',
                targetSchemeIdColumn : 'SCHEMEID',
                targetFilterColumn1 : 'MONITORPOINTID',
                targetFilterValue1 : selections[0].id,

                target_assigned : '1',
                targetTable2 : 'T_EMC_MPC_TMPL',
                targetMatchColumn : "MONITORTMPLID",
                thisMatchColumn : "MONITORTMPLID",
                targetFilterColumn2 : 'MONITORPOINTCATEGORYID',
                targetFilterValue2 : selections[0].monitorPointCategoryId
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var originalTestIds = Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').getData().map(item => item.ext$.testid);
                var newTestIds = rows.map(item => item.testId + "");

                var existed = newTestIds.some(id => originalTestIds.indexOf(id) !== -1);

                if (existed) {
                    Gikam.alert('EMC.MODULE.MDM.BIZMDM.CLIENTS.TIP.TEST_EXISTED');
                    return;
                }

                var url = Gikam.printf(emcClient.baseUrl + '/monitor-points/{monitorPointId}/schemes', {
                    monitorPointId : selections[0].id,
                });
                Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').insert(url, rows);
            }
        });
    },

    setIndex : function() {
        var selections = Gikam.getComp('emc-client-edit-list-monitor-point-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.INDEX_SET',
            url : Gikam.choose.getConfig('indexCategory').url + Gikam.param({
                single : true,
            }),
            width : '68%',
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcClient.baseUrl + '/monitor-points/{monitorPointId}/index-set', {
                    monitorPointId : selections[0].id,
                });
                var chooseData = [ {
                    id : selections[0].id,
                    indexId : rows[0].id,
                } ]
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper({}, [ null, chooseData ])).done(function(data) {
                    Gikam.getComp('emc-client-edit-list-monitor-point-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    copyIndex : function() {
        var selections = Gikam.getComp('emc-client-edit-list-monitor-point-grid').getSelections();

        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (selections[0].indexId == undefined) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.CLIENTS.TIP.NO_INDEX');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.COPY_INDEX',
            width : '60%',
            url : Gikam.choose.getConfig('monitorPoint').url + Gikam.param({
                id_DIFFER : selections[0].id,
            }),

            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcClient.baseUrl + '/monitor-points/{monitorPointId}/index-cope', {
                    monitorPointId : selections[0].id,
                });
                var chooseData = rows.map(function(item) {
                    return {
                        id : item.id,
                        indexId : selections[0].indexId,
                    }
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper({}, [ null, chooseData ])).done(function(data) {
                    Gikam.getComp('emc-client-edit-list-monitor-point-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
        });

    },

    /** 配置比对信息 */
    configComparison : function() {
        var selections = Gikam.getComp('emc-client-edit-list-monitor-point-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.CONFIG_COMPARISON',
            width : '80%',
            url : IFM_CONTEXT + "/secure/emc/module/mdm/bizmdm/comparisons/page/edit-list" + Gikam.param({
                monitorPointId_SEQ : selections[0].id,
            })
        });
    },

    // 复制方案
    copyScheme : function() {
        var selections = Gikam.getComp('emc-client-edit-list-monitor-point-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (Gikam.isEmpty(selections[0].monitorPointCategoryId)) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.CLIENTS.TIP.NO_MONITORPOINTCATEGORY');
            return;
        }
        if (Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').getData().length == 0) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.CLIENTS.TIP.NO_SCHEME');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.COPY_SCHEME',
            width : '60%',
            url : Gikam.choose.getConfig('monitorPoint').url + Gikam.param({
                monitorPointCategoryId_SEQ : selections[0].monitorPointCategoryId,
                id_DIFFER : selections[0].id,
            }),

            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcClient.baseUrl + '/monitor-points/{monitorPointId}/scheme-cope', {
                    monitorPointId : selections[0].id,
                });
                Gikam.getComp('emc-client-edit-list-monitor-point-scheme-grid').insert(url, rows);
            },
        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-client-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ this.getClientGridParam() ]
            },
            center : {
                items : [ this.getOtherGridParam() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
