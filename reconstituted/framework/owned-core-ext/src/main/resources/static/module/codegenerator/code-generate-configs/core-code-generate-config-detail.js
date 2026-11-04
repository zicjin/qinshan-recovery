coreCodeGenerateConfig.detailPage = {

    getBaseBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.BUTTON.GENERATE',
                icon : 'export',
                onClick : function() {
                    _this.save(_this.generateCode);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TAB.MAIN_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    id : 'core-code-generate-config-detail-base-info-form',
                    fields : coreCodeGenerateConfig.getBaseInfoFormFields(),
                    service : 'coreCodeGenerateConfigServiceImpl',
                    onRendered : function() {
                        var checkContent = [ {
                            checked : 1,
                            text : '编制',
                            value : 'edit'
                        }, {
                            checked : 0,
                            text : '详情',
                            value : 'detail'
                        }, {
                            checked : 0,
                            text : '审核',
                            value : 'audit'
                        }, {
                            checked : 0,
                            text : '查询',
                            value : 'search'
                        }, {
                            checked : 0,
                            text : '选择',
                            value : 'choose'
                        } ];
                        this.loadData({
                            func : JSON.stringify(checkContent)
                        });
                    }
                } ]
            } ]
        }
    },

    getTableGridTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TAB.TABLE_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-code-generate-config-detail-table-grid',
                    url : _this.getCompUrl('tableGrid'),
                    page : false,
                    columns : coreCodeGenerateConfig.getTableListColumns(),
                    service : 'coreCodeGenerateConfigTableServiceImpl',
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ "core-code-generate-config-detail-field-grid" ]);
                        }
                    },
                    onRowActive : function(index, row) {

                        Gikam.getAsyncComp('core-code-generate-config-detail-field-grid').done(function(grid) {
                            grid.refresh({
                                url : coreCodeGenerateConfig.baseUrl + '/fields/queries',
                                requestData : {
                                    tableId_SEQ : row.id
                                }
                            });
                        });

                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.save(_this.insertTableRow);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.save(_this.deleteTableRow);
                        }
                    } ],
                    deleteFormatter : function(row) {
                        return row.tableName || row.id;
                    }
                } ]
            } ]
        }
    },

    getFieldGridTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TAB.FIELD_INFO',
                items : [ {
                    type : 'grid',
                    page : false,
                    id : 'core-code-generate-config-detail-field-grid',
                    columns : coreCodeGenerateConfig.getFieldListColumns(),
                    service : 'coreCodeGenerateConfigFieldServiceImpl',
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.save(_this.insertFieldRow);
                        }
                    }, {
                        type : 'button',
                        text : 'ID',
                        icon : 'add-row',
                        onClick : function() {
                            _this.save(function() {
                                _this.insertFieldsByType("id");
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.BUTTON.CREATOR',
                        icon : 'add-row',
                        onClick : function() {
                            _this.save(function() {
                                _this.insertFieldsByType("creator");
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.BUTTON.AUDITOR',
                        icon : 'add-row',
                        onClick : function() {
                            _this.save(function() {
                                _this.insertFieldsByType("auditor");
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.save(_this.deleteFieldRow);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-code-generate-config-detail-field-grid').moveUp(coreCodeGenerateConfig.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-code-generate-config-detail-field-grid').moveDown(coreCodeGenerateConfig.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    } ],
                    deleteFormatter : function(row) {
                        return row.field || row.id;
                    }
                } ]
            } ]
        }
    },

    getBottomLayout : function() {
        return {
            type : 'layout',
            west : {
                width : "40%",
                items : [ this.getTableGridTab() ]
            },
            center : {
                items : [ this.getFieldGridTab() ]
            }
        };

    },

    insertTableRow : function() {
        var formData = Gikam.getComp('core-code-generate-config-detail-base-info-form').getData();

        Gikam.getComp('core-code-generate-config-detail-table-grid').insert(Gikam.printf(coreCodeGenerateConfig.baseUrl + '/{id}/tables', {
            id : formData.id
        }), [ {
            type : 'main'
        } ]);
    },

    deleteTableRow : function() {
        var formData = Gikam.getComp('core-code-generate-config-detail-base-info-form').getData();

        Gikam.getComp('core-code-generate-config-detail-table-grid').deleteRows(Gikam.printf(coreCodeGenerateConfig.baseUrl + '/{id}/tables', {
            id : formData.id
        }));
    },

    insertFieldRow : function() {
        var activeRow = Gikam.getComp('core-code-generate-config-detail-table-grid').getActivedRow();

        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.SELECT_TABLE');
            return;
        }

        Gikam.getComp('core-code-generate-config-detail-field-grid').insert(coreCodeGenerateConfig.baseUrl + '/fields', [ {
            tableId : activeRow.id,
            require : 1,
            fieldType : 'varchar',
            dataRequire : 1,
            mainDisplay : 1,
            detailDisplay : 1
        } ]);
    },

    deleteFieldRow : function() {
        Gikam.getComp('core-code-generate-config-detail-field-grid').deleteRows(coreCodeGenerateConfig.baseUrl + '/fields');
    },

    getFieldsByType : function(type) {

        var manager = {
            creator : [ {
                field : 'createdById',
                fieldName : '制单人编码',
                fieldType : 'varchar',
                fieldLength : 36,
                require : 1,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            }, {
                field : 'createdByName',
                fieldName : '制单人名称',
                fieldType : 'varchar',
                fieldLength : 60,
                require : 1,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            }, {
                field : 'createdTime',
                fieldName : '制单时间',
                fieldType : 'datetime',
                require : 1,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            }, {
                field : 'createdByOrgId',
                fieldName : '制单人单位编码',
                fieldType : 'varchar',
                fieldLength : 36,
                require : 1,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            }, {
                field : 'createdByOrgName',
                fieldName : '制单人单位名称',
                fieldType : 'varchar',
                fieldLength : 270,
                require : 1,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            } ],

            auditor : [ {
                field : 'processStatus',
                fieldName : '流程状态',
                fieldType : 'varchar',
                fieldLength : 36,
                require : 1,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            } ],

            id : [ {
                field : 'id',
                fieldName : '主键',
                fieldType : 'number',
                require : 0,
                dataRequire : 1,
                mainDisplay : 0,
                detailDisplay : 0
            } ]
        }

        return manager[type];
    },

    insertFieldsByType : function(type) {
        var _this = this;
        var activeRow = Gikam.getComp('core-code-generate-config-detail-table-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.SELECT_TABLE');
            return;
        }
        var fields = _this.getFieldsByType(type);
        Gikam.each(fields, function(i, item) {
            item.tableId = activeRow.id;
        });

        Gikam.getComp('core-code-generate-config-detail-field-grid').insert(coreCodeGenerateConfig.baseUrl + '/fields', fields);
    },

    generateCode : function() {
        var _this = this;

        var formData = Gikam.getComp('core-code-generate-config-detail-base-info-form').getData();
        if (!Gikam.isEmpty(formData.menuId)) {

            var menuIdLength = formData.menuId.length

            if (menuIdLength % 2 != 0) {
                Gikam.alert('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.MENUID_LENGTH_ERROR');
                return;
            }
        }

        if (!Gikam.isEmpty(formData.parentMenuId)) {

            var parentMenuIdLength = formData.parentMenuId.length

            if (parentMenuIdLength % 2 != 0) {
                Gikam.alert('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.PARENT_MENUID_LENGTH_ERROR');
                return;
            }
        }

        var tableData = Gikam.getComp('core-code-generate-config-detail-table-grid').getData();
        if (Gikam.isEmpty(tableData)) {
            Gikam.alert('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.INPUT_TABLE');
            return;
        }

        var validateFlag = true;

        Gikam.each(tableData, function(index, table) {
            if (table.mainOrSub == 'sub' && Gikam.isEmpty(table.foreignField)) {
                Gikam.alert(table.tableName + Gikam.propI18N('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.INPUT_FOREIGN_FIELD'));
                validateFlag = false;
                return false;
            }
        });

        if (!validateFlag) {
            return;
        }

        var fieldData = Gikam.getComp('core-code-generate-config-detail-field-grid').getData();
        if (Gikam.isEmpty(fieldData)) {
            Gikam.alert('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.INPUT_FIELD');
            return;
        }

        var invalidFields = '';

        Gikam.each(fieldData, function(index, item) {
            if (item.fieldType === 'varchar' && Gikam.isEmpty(item.fieldLength)) {
                invalidFields += item.field + ',';
            }
        });

        if (invalidFields.length > 0) {
            Gikam.alert(Gikam.propI18N('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.VARCHAR') + invalidFields.substring(0, invalidFields.length - 1) + Gikam.propI18N('CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.TIP.VARCHAR_LENGTH'));
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(Gikam.printf(coreCodeGenerateConfig.baseUrl + '/{id}/action/code-generate', {
            id : formData.id
        })).done(function(url) {
            Gikam.download(url);
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : 300,
                items : [ this.getBaseBtnToolbar(), this.getBaseInfoTab() ]
            },
            west : {
                width : "40%",
                items : [ this.getTableGridTab() ]
            },
            center : {
                items : [ this.getFieldGridTab() ]
            },
            renderTo : workspace.window.$dom
        });
    },

    urlManager : {
        baseInfoForm : coreCodeGenerateConfig.baseUrl + '/{id}',
        tableGrid : coreCodeGenerateConfig.baseUrl + '/{id}/tables/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-code-generate-config-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-code-generate-config-detail-table-grid' : this.getCompUrl('tableGrid')
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

    save : function(callback) {
        workspace.window.save({
            url : coreCodeGenerateConfig.baseUrl + (Gikam.isEmpty(this.param.id) ? '' : '/' + this.param.id),
            page : this,
            showLoading : true
        }).done(function() {
            callback && callback();
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }

}