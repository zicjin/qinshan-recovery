coreReportConfig.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_REPORT_CONFIG', _this.param.id);
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
            width : Gikam.width.XL,
            panels : [ {
                title : 'CORE.MODULE.SYS.REPORTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-report-config-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreReportConfigServiceImpl',
                    autoSave : true,
                    fields : coreReportConfig.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'id' ]);
                            this.removeFieldValidator('id', [ 'remote' ]);
                        }
                    }
                } ]
            } ]
        }
    },

    getCoreReportConfigLineTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'CORE.MODULE.SYS.REPORTS.TAB.LINE',
                items : [ {
                    type : 'grid',
                    id : 'core-report-config-detail-line-grid',
                    url : _this.getCompUrl("lineListGrid"),
                    requestData : {
                        filter_bizCategory : 'master',
                    },
                    service : 'coreReportConfigLineServiceImpl',
                    deleteFormatter : function(row) {
                        return row.reportName || row.id;
                    },
                    activateFormatter : function(row) {
                        return row.reportName || row.id;
                    },
                    checkOnActive : true,
                    onRowActive : function(index, row) {
                        _this.userPermissionGrid.refresh({
                            url : _this.getCompUrl("userPermissionGrid"),
                            requestData : {
                                'targetId_SEQ' : row.id
                            }
                        });
                        _this.rolePermissionGrid.refresh({
                            url : _this.getCompUrl('rolePermissionGrid'),
                            requestData : {
                                'targetId_SEQ' : row.id
                            }
                        });
                    },
                    onLoadSuccess : function(rows) {
                        var _this = this;
                        rows.forEach(function(item, index) {
                            if (item.reportCategory !== 'dataCenter') {
                                _this.toFieldsReadonly(index, [ 'dataCenterReportName' ]);
                            }
                        });
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertCoreReportConfigLine();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            if (_this.param.id) {
                                var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                                if (Gikam.isEmpty(selectrows)) {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                    return;
                                }
                                _this.deleteCoreReportConfigLineRow();
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.ACTIVE',
                        icon : 'enable',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            for (var i = 0; i < selectrows.length; i++) {
                                if (selectrows[i].activatedFlag == '1') {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_ACTIVATE_FAILURE');
                                    return;
                                }
                                if ((selectrows[i].ext$.filename === '' || selectrows[i].ext$.filename === null || selectrows[i].ext$.filename === undefined) && selectrows[i].reportCategory != 'dataCenter') {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_VALID_ATTACHMENT_FAILURE');
                                    return;
                                }
                                if (selectrows[i].reportCategory === 'dataCenter' && (Gikam.isEmpty(selectrows[i].dataCenterReportId) || Gikam.isEmpty(selectrows[i].dataCenterReportUrl))) {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_VALID_DATA_CENTER_FAILURE');
                                    return;
                                }
                            }
                            _this.activateCoreReportConfigLineRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DEACTIVE',
                        icon : 'disable',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            for (var i = 0; i < selectrows.length; i++) {
                                if (selectrows[i].activatedFlag == '0') {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_DEACTIVATE_FAILURE');
                                    return;
                                }
                            }
                            _this.deactivateCoreReportConfigLineRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                        icon : 'upload',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            if (selectrows.lenth > 1) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_MULTI_CHOOSE_FAILURE');
                                return;
                            }
                            _this.uploaderOnly();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.DOWNLOAD_FILE',
                        icon : 'move-down',
                        onClick : function() {
                            _this.downloadFile();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEW',
                        icon : 'browse',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            if (selectrows.length > 1) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_MULTI_CHOOSE_VIEW');
                                return;
                            }
                            _this.view();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-report-config-detail-line-grid').moveUp(coreReportConfig.baseUrl + '/lines/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-report-config-detail-line-grid').moveDown(coreReportConfig.baseUrl + '/lines/action/swap/{firstId}/{secondId}')
                        }
                    } ],
                    columns : coreReportConfig.getLineListColumns(),
                } ]
            } ]
        }
    },

    getPermissionTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'CORE.MODULE.SYS.REPORTS.TAB.USER_PERMISSION',
                items : [ _this.getUserPermissionGridParam() ]
            }, {
                title : 'CORE.MODULE.SYS.REPORTS.TAB.ROLE_PERMISSION',
                items : [ _this.getRolePermissionGridParam() ]
            } ]
        }
    },

    userPermissionGrid : null,
    getUserPermissionGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-report-config-detail-user-permission-grid',
            requestData : {
                'permissionTypeId_SEQ' : 'T_CORE_REPORT_CONFIG_LINE'
            },
            deleteFormatter : function(row) {
                return row.userId || row.id;
            },
            onRendered : function() {
                _this.userPermissionGrid = this;
            },
            genericQuery : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.insertUserPermission();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteUserPermission();
                }
            } ],
            columns : coreReportConfig.getUserPermissionListColumns()
        }
    },

    rolePermissionGrid : null,
    getRolePermissionGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-report-config-detail-role-permission-grid',
            requestData : {
                'permissionTypeId_SEQ' : 'T_CORE_REPORT_CONFIG_LINE'
            },
            deleteFormatter : function(row) {
                return row.ext$.rolename || row.id;
            },
            onRendered : function() {
                _this.rolePermissionGrid = this;
            },
            genericQuery : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.insertRolePermission();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteRolePermission();
                }
            } ],
            columns : coreReportConfig.getRolePermissionListColumns()
        }
    },

    insertUserPermission : function() {
        var _this = coreReportConfig.detailPage;

        var activedRow = Gikam.getComp('core-report-config-detail-line-grid').getActivedRow();
        if (!activedRow) {
            Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.CHOOSE_LINE");
            return;
        }

        var reportLineId = activedRow.id;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('mult-user').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_USER_PERMISSION',
                targetUserIdColumn : 'USERID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : reportLineId,
                targetFilterColumn1 : 'PERMISSIONTYPEID',
                targetFilterValue1 : 'T_CORE_REPORT_CONFIG_LINE'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var chooseData = rows.map(function(item) {
                    return {
                        userId : item.id,
                        permissionTypeId : 'T_CORE_REPORT_CONFIG_LINE',
                        targetId : reportLineId
                    }
                });
                _this.userPermissionGrid.insert(IFM_CONTEXT + '/secure/core/module/mdm/user-permissions', chooseData);
            }
        });
    },

    deleteUserPermission : function() {
        coreReportConfig.detailPage.userPermissionGrid.deleteRows(IFM_CONTEXT + '/secure/core/module/mdm/user-permissions');
    },

    insertRolePermission : function() {
        var _this = coreReportConfig.detailPage;

        var activedRow = Gikam.getComp('core-report-config-detail-line-grid').getActivedRow();
        if (!activedRow) {
            Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.CHOOSE_LINE");
            return;
        }

        var reportLineId = activedRow.id;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('role').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_ROLE_PERMISSION',
                targetRoleIdColumn : 'ROLEID',
                thisMatchColumn : 'ID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : reportLineId,
                targetFilterColumn1 : 'PERMISSIONTYPEID',
                targetFilterValue1 : 'T_CORE_REPORT_CONFIG_LINE'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var chooseData = rows.map(function(item) {
                    return {
                        roleId : item.id,
                        permissionTypeId : 'T_CORE_REPORT_CONFIG_LINE',
                        targetId : reportLineId
                    }
                });
                _this.rolePermissionGrid.insert(IFM_CONTEXT + '/core/module/sys/role-permissions', chooseData);
            }
        });
    },

    deleteRolePermission : function() {
        coreReportConfig.detailPage.rolePermissionGrid.deleteRows(IFM_CONTEXT + '/core/module/sys/role-permissions');
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getCoreReportConfigLineTab(), _this.getPermissionTab() ]
            },
            renderTo : workspace.window.$dom
        });

        return this;
    },

    urlManager : {
        baseInfoForm : coreReportConfig.baseUrl + '/{id}',
        lineListGrid : coreReportConfig.baseUrl + '/{id}/lines/queries',
        userPermissionGrid : IFM_CONTEXT + '/secure/core/module/mdm/user-permissions/queries',
        rolePermissionGrid : IFM_CONTEXT + '/core/module/sys/role-permissions/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-report-config-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-report-config-detail-line-grid' : this.getCompUrl('lineListGrid'),
            'core-report-config-detail-user-permission-grid' : this.getCompUrl('userPermissionGrid'),
            'core-report-config-detail-role-permission-grid' : this.getCompUrl('rolePermissionGrid')
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

    // 上传附件
    uploaderOnly : function() {
        var _this = this;
        var selectedRow = Gikam.getComp('core-report-config-detail-line-grid').getSelections()[0];
        var bizId = selectedRow.id;
        var reportConfigLineNo = selectedRow.reportConfigLineNo;

        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_REPORT_CONFIG_LINE$' + bizId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.rows.length > 0) {
                Gikam.confirm('GIKAM.FILE.BUTTON.UPLOAD_FILE', 'CORE.MODULE.SYS.REPORTS.TIP.LINE_ATTACHMENT_EXISTS', function() {
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        var uploader = Gikam.create('simpleUploader', {
                            id : 'core-report-config-line-uploader',
                            dbTable : 'T_CORE_REPORT_CONFIG_LINE',
                            bizId : bizId,
                            bizCategory : 'master',
                            onBeforeUpload : function() {
                                this.setUploadData({
                                    scope : 'finereport',
                                    ext : reportConfigLineNo
                                })
                            },
                            onAfterClose : function() {
                                Gikam.getComp('core-report-config-detail-line-grid').refresh();
                            }
                        });
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'core-report-config-line-uploader',
                    dbTable : 'T_CORE_REPORT_CONFIG_LINE',
                    bizId : bizId,
                    bizCategory : 'master',
                    onBeforeUpload : function() {
                        this.setUploadData({
                            scope : 'finereport',
                            ext : reportConfigLineNo
                        })
                    },
                    onAfterClose : function() {
                        Gikam.getComp('core-report-config-detail-line-grid').refresh();
                    }
                });
            }
        });
    },

    // 下载附件
    downloadFile : function() {
        var selections = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var fileUrl = '/files/finereport/' + selections[0].reportConfigLineNo + '/' + encodeURIComponent(selections[0].ext$.filename);
        Gikam.download(Gikam.getFileContextUrl(fileUrl), selections[0].ext$.filename);
    },

    insertCoreReportConfigLine : function() {
        var _this = this;
        var modal = Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.REPORTS.MODAL.ADD_LINE',
            width : 400,
            height : 400
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [ {
                    type : 'form',
                    id : 'core-report-config-line-pre-insert-form',
                    columns : 1,
                    fields : coreReportConfig.getLinePreInsertFormFields()
                }, {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        'class' : 'blue',
                        onClick : function() {
                            var preInsertForm = Gikam.getComp('core-report-config-line-pre-insert-form');
                            var preInsertFormData = preInsertForm.getData();
                            var reportConfigLineNo = preInsertFormData.reportConfigLineNo;
                            var reg = /^[a-zA-Z0-9_]+$/;

                            if (!reg.test(reportConfigLineNo)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.REPORTCONFIGLINENO_CAN_ONLY_CONTAIN_ENGLISH_LETTERS_AND_NUMBERS');
                                return;
                            }

                            if (preInsertForm.validate()) {
                                _this.doInsertCoreReportConfigLine(preInsertFormData);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },

    doInsertCoreReportConfigLine : function(preInsertFormData) {
        var _this = this;

        var reportConfigLineNo = preInsertFormData.reportConfigLineNo;

        var lineInsertUrl = Gikam.printf(coreReportConfig.baseUrl + '/{id}/' + 'lines', {
            id : _this.param.id
        });

        var data = Gikam.getJsonWrapper({}, [ 'coreReportConfigLineServiceImpl', [ preInsertFormData ] ]);

        Gikam.getLastModal().window.showMask();
        Gikam.postText(lineInsertUrl, data).done(function(id) {
            Gikam.getLastModal().close();
            Gikam.getComp('core-report-config-detail-line-grid').refresh();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    deleteCoreReportConfigLineRow : function() {
        var lineDeleteUrl = coreReportConfig.baseUrl + '/lines';
        Gikam.getComp('core-report-config-detail-line-grid').deleteRows(lineDeleteUrl);
    },

    activateCoreReportConfigLineRow : function() {
        var lineActivateUrl = coreReportConfig.baseUrl + '/lines/action/activate';
        Gikam.getComp('core-report-config-detail-line-grid').activateRows(lineActivateUrl);
    },

    deactivateCoreReportConfigLineRow : function() {
        var lineDeactivateUrl = coreReportConfig.baseUrl + '/lines/action/deactivate';
        Gikam.getComp('core-report-config-detail-line-grid').deactivateRows(lineDeactivateUrl);
    },

    view : function() {
        var _this = this;
        var reportConfigLine = Gikam.getComp('core-report-config-detail-line-grid').getSelections()[0];

        if (Gikam.isEmpty(reportConfigLine.ext$.filename) && reportConfigLine.reportCategory != 'dataCenter') {
            Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.VIEW_REPORT_MUST_UPLOAD_FILES_FIRST');
            return;
        }

        if (reportConfigLine.reportCategory === 'dataCenter' && (Gikam.isEmpty(reportConfigLine.dataCenterReportId) || Gikam.isEmpty(reportConfigLine.dataCenterReportUrl))) {
            Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.VIEW_REPORT_MUST_CHOOSE_DATA_CENTER_REPORT_ID');
            return;
        }

        var param = null;
        var paramValueJson = null;
        if (reportConfigLine.param != null) {
            param = (reportConfigLine.param).replace(/{[\w]+}/g, function(match) {
                return match.toLocaleLowerCase();
            });

            if (reportConfigLine.defaultParam && _this.isJsonString(reportConfigLine.defaultParam.replace(/\'/g, '"'))) {
                paramValueJson = JSON.parse(reportConfigLine.defaultParam.replace(/\'/g, '"'));
                // 将key值转成小写
                for ( var key in paramValueJson) {
                    paramValueJson[key.toLocaleLowerCase()] = paramValueJson[key];
                    if (key.toLocaleLowerCase() != key) {
                        delete (paramValueJson[key]);
                    }
                }
            }
        }

        var reportPath = null;
        if (reportConfigLine.reportCategory === 'dataCenter' && !Gikam.isEmpty(reportConfigLine.dataCenterReportUrl)) { // 数据中心报表
            reportPath = reportConfigLine.dataCenterReportUrl;
        } else { // 帆软报表
            reportPath = reportConfigLine.reportConfigLineNo + '/' + encodeURIComponent(reportConfigLine.ext$.filename);
        }
        var reportUrl = Gikam.getViewReportUrl(reportConfigLine.reportCategory, reportPath, param, paramValueJson);

        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.REPORTS.TITLE.REPORTVIEW',
            iframe : true,
            src : reportUrl,
            width : Gikam.width.L
        });
    },

    popFrFileView : function(v, r) {
        Gikam.create('modal', {
            url : coreReportConfig.frServerUrl + v
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    },

    loadSubReportPage : function(index) {
        var grid = Gikam.getComp('core-report-config-detail-line-grid');
        var row = grid.getData()[index];

        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.REPORTS.TITLE.SUB_REPORT_CONFIG',
            height : 400,
            width : 600,
            url : coreReportConfig.baseUrl + '/page/sub-report-edit-list' + Gikam.param({
                id : row.id,
            }),
        });
    },

    isJsonString : function(str) {
        try {
            JSON.parse(str);
            return true
        } catch (err) {
            return false
        }
    }
};