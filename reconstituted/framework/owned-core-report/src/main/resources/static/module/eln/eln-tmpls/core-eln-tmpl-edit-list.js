coreElnTmpl.editPage = {

    init : function(param) {
        this.param = param;
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '40%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getRightTab() ]
            }
        });
    },

    tmplGrid : null,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-eln-tmpl-edit-list-grid',
            url : coreElnTmpl.baseUrl + '/queries/raw',
            columns : coreElnTmpl.getEditListColumns(),
            service : 'coreElnTmplServiceImpl',
            deleteFormatter : function(row) {
                return row.elnTmplName || row.id;
            },
            activateFormatter : function(row) {
                return row.elnTmplName || row.id;
            },
            onRendered : function() {
                _this.tmplGrid = this;
            },
            onRowActive : function(index, row) {
                _this.onElnTmplActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-eln-tmpl-ds-list-grid", "core-eln-tmpl-ver-list-grid" ]);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.preInsert();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.tmplGrid.deleteRows(coreElnTmpl.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {

                    var selectedRows = _this.tmplGrid.getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_ELN_TMPL', selectedRows[0].id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXPORT',
                icon : 'export',
                onClick : function() {
                    var gridComp = Gikam.getComp('core-eln-tmpl-edit-list-grid');
                    var selectedRows = gridComp.getSelections();

                    Gikam.expdp('T_CORE_ELN_TMPL', selectedRows);
                }
            } ]
        }
    },

    onElnTmplActive : function(row) {
        var _this = this;

        Gikam.getComp('core-eln-tmpl-ds-list-grid').setOptions({
            url : Gikam.printf(coreElnTmpl.baseUrl + '/{id}/dses/queries/raw', {
                id : row.id
            })
        });

        Gikam.getComp('core-eln-tmpl-ver-list-grid').setOptions({
            url : Gikam.printf(coreElnTmpl.baseUrl + '/{id}/versions/queries/raw', {
                id : row.id
            })
        });

        Gikam.getComp('core-eln-tmpl-ds-list-grid').refresh();
        Gikam.getComp('core-eln-tmpl-ver-list-grid').refresh();

    },

    getRightTab : function() {

        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.DS_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-eln-tmpl-ds-list-grid',
                    service : 'coreElnTmplDsServiceImpl',
                    page : false,
                    deleteFormatter : function(row) {
                        return row.ext$.elndsname || row.id;
                    },
                    onRendered : function() {
                        _this.dsGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertDs();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteDs();
                        }
                    } ],
                    columns : coreElnTmpl.getDsEditListColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.VERSION_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-eln-tmpl-ver-list-grid',
                    service : 'coreElnTmplVersionServiceImpl',
                    page : false,
                    onRendered : function() {
                        _this.versionGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertVersion();
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.ITEM.ELN.TMPLS.BUTTON.EDIT_ELN_TMPL',
                        icon : 'edit',
                        onClick : function() {
                            _this.editTmpl();
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.ITEM.ELN.TMPLS.BUTTON.TEST_ELN',
                        icon : 'enable',
                        onClick : function() {
                            _this.testTmpl();
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.ITEM.ELN.TMPLS.BUTTON.COPY_TMPL',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyTmpl();
                        }
                    } ],
                    columns : coreElnTmpl.getVersionEditListColumns(),
                } ]
            } ]
        }

    },

    loadDetailPage : function(id) {
        workspace.window.load(coreElnTmpl.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            process : this.param.process
        }));
    },

    insertDs : function() {
        var _this = this;

        var elnTmplId = _this.tmplGrid.getActivedRow().id;

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('core-eln-ds').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('core-eln-ds').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_ELN_TMPL_DS',
                targetDsIdColumn : 'ELNDSID',
                targetFilterColumn : 'ELNTMPLID',
                targetFilterValue : elnTmplId,
                SCOPE_SEQ : 'protect',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        elnDsId : item.id,
                        elnDsName : item.elnDsName,
                        elnTmplId : elnTmplId,
                    }
                });
                _this.dsGrid.insert(Gikam.printf(coreElnTmpl.baseUrl + '/{id}/dses', {
                    id : elnTmplId
                }), chooseData);
            }
        });
    },

    deleteDs : function() {
        var _this = this;
        _this.dsGrid.deleteRows(Gikam.printf(coreElnTmpl.baseUrl + '/{id}/dses', {
            id : _this.tmplGrid.getActivedRow().id
        }));
    },

    editTmpl : function() {
        var _this = this;

        if (parent.isOpenEln == '1') {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.CLOSE_ELN');
            return;
        }
        var versionRows = _this.versionGrid.getSelections();
        if (versionRows.length != 1) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.SELECT_ONE_VERSION');
            return;
        }
        var url = IFM_CONTEXT + '/static/plugin/spread-sheets-11.2.6/designer/index/index.html' + Gikam.param({
            operateType : 0,
            versionId : versionRows[0].id,
            elnTmplId : _this.tmplGrid.getActivedRow().id,
            IFM_CONTEXT : IFM_CONTEXT
        })
        window.open(url, 'eln');
    },

    testTmpl : function() {
        var _this = this;
        if (parent.isOpenEln == '1') {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.CLOSE_ELN');
            return;
        }
        var versionRows = _this.versionGrid.getSelections();
        if (versionRows.length != 1) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.SELECT_ONE');
            return;
        }
        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.TEST_TMPL_INFO',
            url : coreElnTmpl.baseUrl + '/page/test-tmpl' + Gikam.param({
                id : versionRows[0].id,
                elnTmplId : _this.tmplGrid.getActivedRow().id
            }),
            width : 800,
            height : 300
        });
    },

    copyTmpl : function() {
        var _this = this;
        var versionRows = _this.versionGrid.getSelections();
        if (versionRows.length != 1) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.SELECT_ONE');
            return;
        }

        var url = Gikam.printf(coreElnTmpl.baseUrl + '/{id}/versions/{versionId}/action/copy', {
            id : _this.tmplGrid.getActivedRow().id,
            versionId : versionRows[0].id
        });

        workspace.window.showMask(true);
        Gikam.postText(url).done(function() {
            // 复制完成，刷新ELN模板Grid，获取新数据
            _this.tmplGrid.refresh();
            workspace.window.closeMask();
        });
    },

    insertVersion : function() {
        var _this = this;
        var elnTmplId = _this.tmplGrid.getActivedRow().id;

        _this.versionGrid.insert(Gikam.printf(coreElnTmpl.baseUrl + '/{id}/versions', {
            id : elnTmplId
        }), [ {
            elnTmplId : elnTmplId
        } ]);
    },

    preInsert : function() {
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.TMPL_INSERT',
            fields : coreElnTmpl.getBaseInfoFormFields(),
            param : {},
            url : coreElnTmpl.baseUrl,
            height : 200,
        }).done(function(id) {
            _this.tmplGrid.refresh();
        });
    }

};
