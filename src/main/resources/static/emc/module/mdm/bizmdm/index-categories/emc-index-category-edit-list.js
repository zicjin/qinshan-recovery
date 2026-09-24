emcIndexCategory.editPage = {

    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-index-category-edit-list-grid',
            url: emcIndexCategory.baseUrl + '/queries/raw',
            columns: emcIndexCategory.getEditListColumns(),
            service: 'emcIndexCategoryServiceImpl',
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    return this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-order-index-detail-field-grid').cleanData();
                    Gikam.getComp('emc-index-set-detail-field-grid').cleanData();
                }
            },
            onRowActive: function (index, row) {
                _this.onIndexCategoryActive(row);
            },
            deleteFormatter: function (row) {
                return row.stdName || row.id;
            },
            activateFormatter: function (row) {
                return row.stdName || row.id;
            },
            deactivateFormatter: function (row) {
                return row.stdName || row.id;
            },
            onCellClick: function (field, row) {
                field === 'ext$.filename' && row.ext$.filename != null && Gikam.openFile(row.ext$.fileid, false);
            },
            toolbar: _this.getIndexCategoryButton()
        }
    },

    getIndexCategoryButton: function () {
        var _this = this;
        return [{
            type: 'button',
            text: 'GIKAM.BUTTON.INSERT',
            icon: 'add',
            onClick: function () {
                Gikam.preInsert({
                    modalTitle: 'EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.MODAL.ADD',
                    fields: emcIndexCategory.getBaseInfoFormFields(),
                    url: emcIndexCategory.baseUrl
                }).done(function (id) {
                    Gikam.getComp('emc-index-category-edit-list-grid').refresh();
                });
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.DELETE',
            icon: 'remove',
            onClick: function () {
                Gikam.getComp('emc-index-category-edit-list-grid').deleteRows(emcIndexCategory.baseUrl);
            }
        }, {
            type: 'button',
            text: 'EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.BUTTON.SYNC',
            icon: 'refresh',
            onClick: function () {
                var selections = Gikam.getComp('emc-index-category-edit-list-grid').getSelections();
                if (selections.length === 0) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                workspace.window.showMask(true);
                Gikam.post(emcIndexCategory.baseUrl + '/estand-datas/action/sync', Gikam.getJsonWrapper({}, ['', selections])).always(function () {
                    workspace.window.closeMask();
                    Gikam.getComp('emc-index-category-edit-list-grid').refresh();
                });
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.COPY',
            icon: 'copy',
            onClick: function () {
                var grid = Gikam.getComp('emc-index-category-edit-list-grid');
                var selections = grid.getSelections();
                if (selections.length === 0) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                var modal = Gikam.create('modal', {
                    title: "添加",
                    width : 300,
                    height : 150,
                });
                var layout = Gikam.create('layout', {
                    renderTo: modal.window.$dom,
                    center: {
                        items: [ {
                            type: 'btnToolbar',
                            items: [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.CONFIRM',
                                class : 'blue',
                                onClick : function() {
                                    if (form.validate()) {
                                        Gikam.getLastModal().window.showMask(true);

                                        var url = Gikam.printf(emcIndexCategory.baseUrl + "/copy");
                                        Gikam.post(url, Gikam.getJsonWrapper({name:form.getData().name}, ['emcIndexCategoryServiceImpl', selections])).done(function () {
                                            grid.refresh();
                                        }).always(function() {
                                            Gikam.getLastModal().window.closeMask();
                                            modal.close();
                                        });
                                    }
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.CANCEL',
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
                    fields: [ {
                        field : 'name',
                        title : '名称',
                        validators : [ 'notEmpty'],
                    }],
                    autoSave: false
                });

            }
        }, {
            type: 'dropDownMenu',
            text: '附件',
            icon: 'upload',
            items: [{
                type: 'button',
                text: 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                icon: 'upload',
                onClick: function () {
                    _this.uploadFile();
                }
            }, {
                type: 'button',
                text: 'GIKAM.FILE.BUTTON.DELETE_FILE',
                icon: 'file-remove',
                onClick: function () {
                    _this.deleteFile();
                }
            }]
        }];
    },

    uploadFile: function () {
        var rows = Gikam.getComp('emc-index-category-edit-list-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f: {
                    targetId: 'T_EMC_INDEX_CATEGORY$' + bizId
                },
                n: -1,
                s: 50
            })).done(function (r) {
                if (r.total > 0) {
                    Gikam.confirm('GIKAM.TIP.FILE_OVERRIDE', 'GIKAM.TIP.FILE_OVERRIDE', function () {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, ['', r.rows])).done(function (r) {
                            Gikam.create('simpleUploader', {
                                id: 'emc-index-category-edit-list-uploader',
                                dbTable: 'T_EMC_INDEX_CATEGORY',
                                bizId: bizId,
                                multiple: false,
                                onAfterClose: function () {
                                    Gikam.getComp('emc-index-category-edit-list-grid').refresh()
                                }
                            });
                        });
                    })
                } else {
                    Gikam.create('simpleUploader', {
                        id: 'emc-index-category-edit-list-uploader',
                        dbTable: 'T_EMC_INDEX_CATEGORY',
                        bizId: bizId,
                        multiple: false,
                        onAfterClose: function () {
                            Gikam.getComp('emc-index-category-edit-list-grid').refresh()
                        }
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    deleteFile: function () {
        var _this = this;
        var grid = Gikam.getComp('emc-index-category-edit-list-grid');
        var rows = grid.getSelections();
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('GIKAM.TIP.FILE_DELETE', 'GIKAM.TIP.IF_FILE_DELETE', function () {
            workspace.window.showMask(true);
            var ids = rows.map(function (item) {
                return {
                    id: item.ext$.fileid
                }
            });
            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, ['', ids])).done(function () {
                grid.refresh()
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    downloadFile: function (fileId) {
        Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + fileId).done(function (coreFile) {
            Gikam.emcDownloadFile(IFM_CONTEXT + coreFile.downloadUrl);
        });
    },

    onIndexCategoryActive: function (row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-order-index-detail-field-grid').done(function (grid) {
                grid.refresh({
                    url: _this.getOrderIndexUrl(row.id),
                });
            })
        }
    },

    onFieldParamActive: function (row) {
        var _this = this;
        if (row) {
            var targetComp = Gikam.getComp('emc-index-set-detail-field-grid')
            var indexCategoryRow = Gikam.getComp('emc-index-category-edit-list-grid').getActivedRow();
            if (!Gikam.isEmpty(indexCategoryRow.propertyTmplId)) {
                // 根据标准设置，查询属性设置字段信息
                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/index-sets/' + indexCategoryRow.propertyTmplId).done(function (columns) {
                    debugger
                    var addColumns = columns.rows.map(function (items) {
                        if (items.fieldType == 'fieldMultiSelect') {
                            var selectFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                editor: true,
                            };
                            selectFields['param'] = {
                                codeCategoryId: items.sysCode
                            };
                            if (items.required == '1') {
                                selectFields['validators'] = ['notEmpty'];
                            }
                            return selectFields;
                        } else if (items.fieldType == 'fieldSelect') {
                            var selectFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                editor: true,
                            };
                            selectFields['param'] = {
                                codeCategoryId: items.sysCode
                            };
                            if (items.required == '1') {
                                selectFields['validators'] = ['notEmpty'];
                            }
                            return selectFields;
                        } else if (items.fieldType == 'fieldContext') {
                            var contextFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                validators: [],
                                editor: true,
                            };
                            if (items.required == '1') {
                                contextFields.validators.push('notEmpty');
                            }
                            if (items.fieldLength) {
                                contextFields.validators.push('strLength[0,' + items.fieldLength + ']');
                            }
                            if (items.defaultValue) {
                                contextFields['value'] = items.defaultValue;
                            }
                            return contextFields;
                        } else if (items.fieldType == 'fieldContextArea') {
                            var contextAreaFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                validators: [],
                                editor: true,
                            };
                            if (items.required == '1') {
                                contextAreaFields.validators.push('notEmpty');
                            }
                            if (items.fieldLength) {
                                contextAreaFields.validators.push('strLength[0,' + items.fieldLength + ']');
                            }
                            return contextAreaFields;
                        } else if (items.fieldType == 'fieldDate') {
                            var dateFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                type: 'date',
                                editor: true,
                            };
                            if (items.required == '1') {
                                dateFields['validators'] = ['notEmpty'];
                            }
                            return dateFields;
                        }
                    })
                    var targetColumns = emcIndexCategory.getIndexSetColumns().concat(addColumns);
                    targetComp.refreshColumns(targetColumns);
                    if (indexCategoryRow.ext$.templatename == '昼夜评价') {
                        targetComp.hideColumns(['indexLowLimit', 'indexHighLimit', 'textIndexValue']);
                    }
                    _this.propertyDataColumns = addColumns;

                    // 指标设置数据
                    targetComp.refresh({
                        url: emcIndexCategory.baseUrl + '/order-indexes/' + row.id + '/index-sets/queries'
                    });
                });
            }
        }
    },

    getOrderIndexUrl: function (id) {
        if (id) {
            return Gikam.printf(emcIndexCategory.baseUrl + '/{id}/order-indexes/queries', {
                id: id
            })
        }
    },

    getIndexSetUrl: function (id) {
        if (id) {
            return Gikam.printf(emcIndexCategory.baseUrl + '/{id}/index-sets/queries', {
                id: id
            })
        }
    },

    // 样品指标
    getFieldParam: function () {
        var _this = this;
        return {
            type: 'tab',
            width: Gikam.width.M,
            fill: true,
            panels: [{
                title: 'EMC.MODULE.MDM.BIZMDM.ORDERINDEXES.TAB.BASE_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-order-index-detail-field-grid',
                    service: 'emcOrderIndexServiceImpl',
                    url: _this.getCompUrl('fieldGrid'),
                    checkOnActive: true,
                    order: {
                        index1: 'asc'
                    },
                    group: {
                        groupCheckbox: false,
                        fields: ['index1'],
                        formatter: function (row) {
                            return row.index1;
                        }
                    },
                    onCellClick: function (field, row) {
                        //field === 'indexName' && emcIndexCategory.loadOrderIndexDetail(row.id);
                    },
                    deleteFormatter: function (row) {
                        return row.indexName || row.id;
                    },
                    onLoadSuccess: function (data) {
                        if (data && data.length > 0) {
                            return this.activeRowByIndex(1);
                        }
                        Gikam.getComp('emc-index-set-detail-field-grid').cleanData();
                    },
                    onRowActive: function (index, row) {
                        if (!row._groupRow || row._groupRow == undefined) {
                            _this.onFieldParamActive(row);
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.INSERT',
                        icon: 'add',
                        onClick: function () {
                            var formData = Gikam.getComp('emc-index-category-edit-list-grid').getActivedRow();
                            emcIndexCategory.loadOrderIndexDetail(formData.id, 1);
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.DELETE',
                        icon: 'remove-row',
                        onClick: function () {
                            var formData = Gikam.getComp('emc-index-category-edit-list-grid').getActivedRow();
                            var url = Gikam.printf(emcIndexCategory.baseUrl + '/{id}/order-indexes', {
                                id: formData.id
                            });
                            Gikam.getComp('emc-order-index-detail-field-grid').deleteRows(url);
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.COPY',
                        icon: 'copy',
                        onClick: function () {
                            _this.copyOrderIndex();
                        }
                    }],
                    columns: emcIndexCategory.getOrderIndexColumns(),
                }]
            }]
        }
    },

    deleteOrderIndex: function () {
        var url = Gikam.printf(emcIndexCategory.baseUrl + '/order-indexes');
        Gikam.getComp('emc-order-index-detail-field-grid').deleteRows(url);
    },

    // 复制样品指标
    copyOrderIndex: function () {
        var selections = Gikam.getComp('emc-order-index-detail-field-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var url = emcIndexCategory.baseUrl + '/order-indexes/action/copy';
        Gikam.getComp('emc-order-index-detail-field-grid').insert(url, selections);
    },

    // 指标设置
    getIndexSetColumns: function () {
        var columns = [];
        columns.push({
            checkbox: true
        }, {
            field: 'evalIndex',
            title: 'T_EMC_INDEX_SET.EVALINDEX',
            editor: true,
        }, {
            field: 'ext$.indexItemName',
            title: 'T_EMC_INDEX_SET.INDEXITEMID',
            type: 'choose',
            category: 'indexItem',
            editor: true,
            onAfterChoose: function (rows) {
                if (rows) {
                    this.setData({
                        indexItemId: rows[0].id,
                        ext$: {
                            indexItemName: rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    indexItemId: '',
                    ext$: {
                        indexItemName: '',
                    }
                });
            },
        });

        columns.push({
            field: 'ext$.fieldname',
            title: 'T_EMC_PROPERTY_FIELD.FIELDNAME',
        });

        return columns;
    },

    // 指标设置
    getIndexSetParam: function () {
        var _this = this;
        return {
            type: 'tab',
            width: Gikam.width.M,
            fill: true,
            panels: [{
                title: 'EMC.MODULE.MDM.BIZMDM.INDEXSET.TAB.BASE_INFO',
                items: [{
                    type: 'grid',
                    id: 'emc-index-set-detail-field-grid',
                    service: 'emcIndexSetServiceImpl',
                    checkOnActive: true,
                    deleteFormatter: function (row) {
                        return row.evalIndex || row.id;
                    },
                    onRendered: function () {
                        var indexCategoryActivedRow = Gikam.getComp('emc-index-category-edit-list-grid').getActivedRow();
                        if (indexCategoryActivedRow.ext$.templatename == '昼夜评价') {
                            this.hideColumns(['indexLowLimit', 'indexHighLimit', 'textIndexValue']);
                        }
                    },
                    onUpdated: function (row, key) {
                        // key 0 是 当前行的 id, key n 是当前修改的列的 field
                        // id 对应 propertyData 表的 bizId
                        var newKey = key[1].replace("ext$.", "");
                        if (key.length === 2) {
                            var saveData = {};
                            saveData['bizId'] = row.id
                            saveData[Object.keys(row.ext$)[0]] = Object.values(row.ext$)[0]
                            workspace.window.showMask(true);
                            Gikam.put('/secure/emc/module/mdm/basemdm/property-tmpls/1', Gikam.getJsonWrapper({}, ['emcPropertyDataServiceImpl', [saveData]])).done(function (id) {
                                Gikam.getComp('emc-index-set-detail-field-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                            });
                        }
                    },
                    toolbar: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.INSERT',
                        icon: 'add',
                        onClick: function () {
                            var indexCategoryActivedRow = Gikam.getComp('emc-index-category-edit-list-grid').getActivedRow();
                            if (!indexCategoryActivedRow) {
                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.TIP.CHOOSE_ORDER_INDEX');
                                return;
                            }

                            var activedRow = Gikam.getComp('emc-order-index-detail-field-grid').getActivedRow();
                            if (!activedRow || activedRow._groupRow) {
                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.TIP.CHOOSE_ORDER_INDEX');
                                return;
                            }

                            var formData = Gikam.getComp('emc-index-category-edit-list-grid').getActivedRow();
                            var url = Gikam.printf(emcIndexCategory.baseUrl + '/{indexId}/{indexCategoryId}/{tmplId}/index-sets', {
                                indexId: activedRow.id,
                                indexCategoryId: formData.id,
                                tmplId: indexCategoryActivedRow.propertyTmplId
                            });
                            Gikam.create('modal', {
                                width: 400,
                                height: '80%',
                                title: Gikam.choose.getConfig('indexItem').title,
                                url: Gikam.choose.getConfig('indexItem').url + Gikam.param({
                                    indexId: activedRow.id
                                }),
                                onAfterClose: function (rows) {
                                    if (!Gikam.isEmpty(rows)) {
                                        var chooseData = rows.map(function (item) {
                                            return {
                                                indexItemId: item.id,
                                                ext$: {
                                                    indexitemname: item.indexItemName
                                                }
                                            };
                                        });
                                        Gikam.getComp('emc-index-set-detail-field-grid').insert(url, chooseData);
                                    }
                                }
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.DELETE',
                        icon: 'remove-row',
                        onClick: function () {
                            _this.deleteIndexSet();
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.BATCHUPDADE',
                        icon: 'edit',
                        onClick: function () {
                            _this.indexSetBatchUpdade();
                        }
                    }],
                    columns: emcIndexCategory.getIndexSetColumns(),
                }]
            }]
        }
    },

    indexSetBatchUpdade: function () {
        var selections = Gikam.getComp('emc-index-set-detail-field-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCHUPDADE",
            width: '25%',
            height: '25%'
        });

        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        class: 'blue',
                        onClick: function () {
                            if (form.validate()) {
                                selections.forEach(function (row) {
                                    row.unit = form.options.data.unit;
                                })
                                Gikam.getLastModal().window.showMask(true);

                                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcIndexSetServiceImpl', selections])).done(function (data) {
                                    Gikam.getComp('emc-index-set-detail-field-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick: function () {
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
            fields: emcIndexCategory.getIndexSetBatchUpdadeFields(),
            autoSave: false,
        });

    },

    deleteIndexSet: function () {
        var formData = Gikam.getComp('emc-order-index-detail-field-grid').getActivedRow();
        var url = Gikam.printf(emcIndexCategory.baseUrl + '/index-sets');
        Gikam.getComp('emc-index-set-detail-field-grid').deleteRows(url);
    },

    // 同步指标项目
    synIndexItem: function () {
        var _this = emcIndexCategory.editPage;

        var selections = Gikam.getComp('emc-index-category-edit-list-grid').getSelections();

        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.put(emcIndexCategory.baseUrl + '/actions/sync', Gikam.getJsonWrapper({}, [null, selections])).done(function (data) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.SYNC.SUCCESS')
        }).always(function () {
            workspace.window.closeMask();
        });
    },

    urlManager: {
        fieldGrid: emcIndexCategory.baseUrl + '/{id}/order-indexes/queries'
    },

    getCompUrl: function (name, id) {
        if (Gikam.isEmpty(id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id: id
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-test-category-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: 640,
                items: [this.getGridParam()]
            },
            center: {
                items: [{
                    type: 'layout',
                    north: {
                        height: '50%',
                        items: [this.getFieldParam()]
                    },
                    center: {
                        items: [this.getIndexSetParam()]
                    }
                }]
            }
        });
    },

    init: function () {
        this.create();
    }
};
