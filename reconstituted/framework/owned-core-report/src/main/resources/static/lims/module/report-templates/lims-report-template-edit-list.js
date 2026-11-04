limsReportTemplate.editPage = {

    getGridParam: function () {
        workspace.window.showMask(1);
        var obj = {};
        // 获取模板类型 的下拉数组
        Gikam.getTextSync(IFM_CONTEXT + '/secure/core/module/sys/codes/queries/selectable?codeCategoryId=templateType')
            .done(function (res) {
                if (res) {
                    var codeArr = JSON.parse(res)

                    var items = codeArr.map(function (item) {

                        obj[item.code] = item.codeName;
                    });
                }
            }).fail(function () {
            workspace.window.closeMask(1);
        });

        workspace.window.closeMask(1);


        var _this = this;
        var baseGridToolbar = _this.getBaseGridToolbar();
        var gridToolbar = _this.appendGridToolbar(baseGridToolbar);
        return {
            type: 'grid',
            id: _this.idManager.limsReportTemplateEditGrid,
            url: limsReportTemplate.baseUrl + '/queries/raw',
            columns: limsReportTemplate.getEditListColumns(),
            service: 'limsReportTemplateService',
            confirmField: 'templateName',
            group: {
                fields: ['templateType'],
                groupCheckbox: true,
                formatter: function (row) {
                    return `模板类型：${ obj[row.templateType] || '空'}`
                }
            },
            onCellClick: function (field, row) {
                field === 'templateFileName' && Gikam.openFile(row.templateFileId, false);
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([_this.idManager.limsReportTemplateTemplateSourceGrid]);
                    Gikam.getAsyncComp(_this.idManager.limsReportTemplateTemplateFileUploader).done(function (uploader) {
                        uploader.setOptions({
                            bizId: null
                        })
                        uploader.grid.cleanData();
                    })
                }
            },
            onRowActive: function (index, row) {
                Gikam.getAsyncComp(_this.idManager.limsReportTemplateTemplateSourceGrid).done(function (grid) {
                    grid.refresh({
                        url: Gikam.printf(limsReportTemplate.baseUrl + '/{id}/template-sources/queries', {
                            id: row.id
                        })
                    });
                });

                Gikam.getAsyncComp(_this.idManager.limsReportTemplateTemplateFileUploader).done(function (uploader) {
                    uploader.setOptions({
                        bizId: row.id
                    });
                });
            },
            toolbar: gridToolbar
        }
    },

    getBaseGridToolbar: function () {
        var _this = this;
        return [{
            type: 'button',
            text: 'GIKAM.BUTTON.INSERT',
            icon: 'add',
            onClick: function () {
                Gikam.preInsert({
                    modalTitle: 'LIMS.MODULE.REPORTTEMPLATES.MODAL.ADD',
                    fields: limsReportTemplate.getAddFormFields(),
                    url: limsReportTemplate.baseUrl
                }).done(function (id) {
                    Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).refresh();
                });
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.ACTIVE',
            icon: 'enable',
            onClick: function () {
                Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).activateRows(limsReportTemplate.baseUrl + '/action/activate');
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.DEACTIVE',
            icon: 'disable',
            onClick: function () {
                Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).deactivateRows(limsReportTemplate.baseUrl + '/action/deactivate');
            }
        }, {
            type: 'button',
            text: 'LIMS.MODULE.REPORTTEMPLATES.BUTTON.COPY',
            icon: 'copy',
            onClick: function () {
                _this.copyTemplate();
            }
        }, {
            type: 'button',
            text: 'GIKAM.BUTTON.DELETE',
            icon: 'remove-row',
            onClick: function () {
                Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).deleteRows(limsReportTemplate.baseUrl);
            }
        }, {
            type: 'dropDownMenu',
            text: 'LIMS.MODULE.REPORTTEMPLATES.BUTTON.DATA_TRANSPORT',
            icon: 'revert',
            items: [{
                type: 'button',
                text: 'GIKAM.BUTTON.EXPORT',
                icon: 'export',
                color: 'primary',
                onClick: function () {
                    _this.exportData();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.IMPORT',
                icon: 'material-to-check',
                color: 'warning',
                onClick: function () {
                    _this.importData();
                }
            }]
        }, {
            type: 'button',
            // text : 'LIMS.MODULE.REPORTTEMPLATES.BUTTON.TEST-CREATE',
            text: 'LIMS.MODULE.REPORTTEMPLATES.BUTTON.VIEW',
            icon: 'preview',
            onClick: function () {
                _this.createReport();
            }
        }]
    },

    appendGridToolbar: function (toolbarArray) {
        return toolbarArray;
    },

    /**
     * 报告模板数据导出
     */
    exportData: function () {
        var _this = this;
        var templateGrid = Gikam.getComp(_this.idManager.limsReportTemplateEditGrid);
        var selectTemplateList = templateGrid.getSelections();
        var exportFunction = function () {
            workspace.window.showMask();
            Gikam.postText(limsReportTemplate.baseUrl + '/action/export-zip', Gikam.getJsonWrapper(null, [null, selectTemplateList])).done(function (downloadUrl) {
                Gikam.download(downloadUrl);
            }).always(function () {
                workspace.window.closeMask();
            })
        }
        if (Gikam.isEmpty(selectTemplateList)) {
            Gikam.confirm('LIMS.MODULE.REPORTTEMPLATES.TAB.EXPORT_ALL_CONFIRM', 'LIMS.MODULE.REPORTTEMPLATES.TIP.EXPORT_ALL_CONFIRM', function () {
                exportFunction();
            })
        } else {
            exportFunction();
        }
    },

    importData: function () {
        var _this = this;
        // 培训计划  数据导入
        Gikam.create('simpleUploader', {
            dbTable: 'T_LIMS_REPORT_TEMPLATE',
            bizId: "TEMPLATE_IMPORT",
            accept: 'zip',
            onAfterClose: function (files) {
                var url = Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/{service}', {
                    id: files[0].id,
                    service: 'LimsReportTemplateImportFunction'
                });
                workspace.window.showMask();
                Gikam.postText(url).done(function (result) {
                    Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).refresh();
                }).always(function () {
                    workspace.window.closeMask();
                })
            }
        });
    },

    copyTemplate: function () {
        var _this = this;
        Gikam.create('modal', {
            title: Gikam.choose.getConfig('report-template-choose').title,
            url: Gikam.choose.getConfig('report-template-choose').url + Gikam.param({
                single: true,
            }),
            onAfterClose: function (rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var selectTemplate = rows[0];
                var templateName = selectTemplate.templateName;
                var remark = selectTemplate.remark;
                Gikam.preInsert({
                    modalTitle: 'LIMS.MODULE.REPORTTEMPLATES.BUTTON.COPY',
                    fields: [{
                        field: 'templateName',
                        title: 'T_LIMS_REPORT_TEMPLATE.TEMPLATENAME',
                        preInsert: true,
                        value: templateName,
                        validators: ['notEmpty']
                    }, {
                        field: 'remark',
                        title: 'T_LIMS_REPORT_TEMPLATE.REMARK',
                        preInsert: true,
                        value: remark,
                        validators: ['notEmpty']
                    }],
                    url: limsReportTemplate.baseUrl + '/action/copy',
                    param: {
                        id: selectTemplate.id,
                        templateType: selectTemplate.templateType,
                    }
                }).done(function (id) {
                    Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).refresh();
                });
            }

        })
    },

    createReport: function () {
        var _this = this;
        var templateGrid = Gikam.getComp(_this.idManager.limsReportTemplateEditGrid);
        var selections = templateGrid.getSelections();
        if (Gikam.isEmpty(selections) || selections.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var template = selections[0];
        if (Gikam.isEmpty(template.param)) {
            var url = Gikam.printf(limsReportTemplate.baseUrl + '/{id}/action/generator', {
                id: template.id
            });
            workspace.window.showMask();
            Gikam.postText(url).done(function (fileId) {
                workspace.window.closeMask();
                Gikam.openFile(fileId, false);
            }).always(function () {
                workspace.window.closeMask();
            });
        } else {
            var paramArray = template.param.split(',');
            var defaultValue = Gikam.isEmpty(template.defaultValue) ? [] : template.defaultValue.split(',');
            var fields = paramArray.map(function (value, index) {
                var defaultV = '';
                if (defaultValue[index]) {
                    defaultV = defaultValue[index];
                }
                return {
                    field: value,
                    title: value,
                    value: defaultV,
                    validators: ['strLength(0, 100]']
                }
            });
            var height = 110 + 40 * paramArray.length;
            var simpleModal = Gikam.createSimpleModal({
                title: 'LIMS.MODULE.REPORTTEMPLATES.TAB.INPUT_PARAM',
                width: 300,
                height: height,
                showMaxBtn: false,
                bodyStyle: {
                    padding: "0 16px"
                },
                toolbarFormatter: function () {
                    return [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        icon: 'select',
                        onClick: function () {
                            if (Gikam.isFalse(form.validate())) {
                                return;
                            }
                            var url = Gikam.printf(limsReportTemplate.baseUrl + '/{id}/action/generator', {
                                id: template.id
                            });
                            Gikam.getLastModal().window.showMask();
                            Gikam.postText(url, Gikam.getJsonWrapper({
                                f: form.getData()
                            })).done(function (fileId) {
                                Gikam.getLastModal().close();
                                Gikam.openFile(fileId, false);
                            }).always(function () {
                                Gikam.getLastModal() && Gikam.getLastModal().window.closeMask();
                            })
                        }
                    }];
                }
            });

            var form = Gikam.create('form', {
                columns: 1,
                titleWidth: 80,
                fields: fields,
                renderTo: simpleModal.layout.options.center.$dom
            });
        }
    },

    uploadTemplateFile: function (id, index, dbTable) {
        var _this = this;
        Gikam.create('simpleUploader', {
            dbTable: dbTable,
            bizId: id,
            fill: true,
            multiple: false,
            onUploadSuccess: function (data) {
                Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).setData({
                    id: Number(id),
                    ///index : index,
                    templateFileId: data[0].id,
                    templateFileName: data[0].name
                }, true);
            }
        });
    },

    downloadFile: function (fileId) {
        var _this = this;
        Gikam.getJson(Gikam.printf(limsReportTemplate.fileUrl + '/{id}', {
            id: fileId
        })).done(function (file) {
            Gikam.download(file.downloadUrl);
        });
    },

    getBaseInfoTab: function () {
        var _this = this;
        return {
            type: 'tab',
            panels: [_this.getTemplateSourceGrid(), _this.getTemplateTagUploader()]
        }
    },

    getTemplateSourceGrid: function () {
        var _this = this;
        return {
            title: 'LIMS.MODULE.REPORTTEMPLATES.TAB.TEMPLATE_SOURCE',
            items: [{
                type: 'grid',
                id: _this.idManager.limsReportTemplateTemplateSourceGrid,
                service: 'limsReportTemplateSourceServiceImpl',
                confirmField: 'sourceCode',
                onCellDbClick: function (field, row) {
                    field === 'sourceCode' && _this.showSourceDetail(row);
                },
                toolbar: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.INSERT',
                    icon: 'add-row',
                    onClick: function () {
                        var activatedRow = Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).getActivedRow();
                        if (Gikam.isEmpty(activatedRow)) {
                            return;
                        }
                        Gikam.preInsert({
                            modalTitle: 'GIKAM.BUTTON.INSERT',
                            fields: limsReportTemplate.getTemplateSourceFormFields(),
                            url: Gikam.printf(limsReportTemplate.baseUrl + '/{id}/template-sources', {
                                id: activatedRow.id
                            })
                        }).done(function (id) {
                            Gikam.getComp(_this.idManager.limsReportTemplateTemplateSourceGrid).refresh();
                            _this.showSourceDetail({
                                id: id
                            })
                        });
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.DELETE',
                    icon: 'remove-row',
                    onClick: function () {
                        var activatedRow = Gikam.getComp(_this.idManager.limsReportTemplateEditGrid).getActivedRow();
                        if (Gikam.isEmpty(activatedRow)) {
                            return;
                        }
                        Gikam.getComp(_this.idManager.limsReportTemplateTemplateSourceGrid).deleteRows(Gikam.printf(limsReportTemplate.baseUrl + '/{id}/template-sources', {
                            id: activatedRow.id
                        }));
                    }
                }, {
                    type: 'caption',
                    text: '双击 数据源编码进入详情界面操作',
                    color: 'red'
                }],
                columns: limsReportTemplate.getTemplateSourceListColumns()
            }]
        };
    },

    showSourceDetail: function (row) {
        var _this = this;
        Gikam.create('modal', {
            url: limsReportTemplate.baseUrl + '/page/sources/detail' + Gikam.param({
                id: row.id
            }),
            title: Gikam.propI18N('LIMS.MODULE.REPORTTEMPLATES.TAB.SOURCE_SQL_OPTION'),
            onAfterClose: function () {
                Gikam.getComp(_this.idManager.limsReportTemplateTemplateSourceGrid).refreshRowById(row.id);
            }
        })
    },

    /**
     *  模板附件
     */
    getTemplateTagUploader: function () {
        var _this = this;
        return {
            title: 'GIKAM.FILE.ATTACHMENT',
            items: [{
                type: 'uploader',
                id: _this.idManager.limsReportTemplateTemplateFileUploader,
                accept: ['image', 'docx'],
                dbTable: 'T_LIMS_REPORT_TEMPLATE',
                service: 'coreFileService',
                bizCategory: 'templateTagFile',
                formatterGridColumns: function (columns) {
                    columns.splice(2, 0, {
                        field: 'ext',
                        title: 'LIMS.MODULE.REPORTTEMPLATES.TIP.TAGNAME',
                        editor: true,
                        validators: ['notEmpty', 'strLength(0, 60]']
                    });
                    return columns;
                },
            }]
        }
    },

    idManager: {
        limsReportTemplateEditLayout: 'lims-report-template-edit-list-layout',
        limsReportTemplateEditGrid: 'lims-report-template-edit-list-grid',
        limsReportTemplateTemplateSourceGrid: 'lims-report-template-edit-list-template-source-grid',
        limsReportTemplateTemplateFileUploader: 'lims-report-template-edit-list-template-file-uploader'
    },
    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: _this.idManager.limsReportTemplateEditLayout,
            renderTo: workspace.window.$dom,
            west: {
                width: '45%',
                items: [this.getGridParam()]
            },
            center: {
                items: [this.getBaseInfoTab()]
            }
        });
    },

    init: function () {
        this.create();
    }
};
