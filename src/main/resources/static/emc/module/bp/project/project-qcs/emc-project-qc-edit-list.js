emcProjectQc.editPage = {

    // 列表布局参数
    getProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-qc-edit-list-grid',
            url : emcProjectQc.projectBaseUrl + '/queries/tasks',
            columns : emcProjectQc.getProjectListColumns(),
            requestData : {                
                org_filter : '1',
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-project-edit-list-base-info-form", "emc-project-edit-list-order-grid", "emc-project-edit-list-order-item-group-grid", "emc-project-edit-list-director-grid", "emc-schemeedit-audit-list-grid", "emc-proj-order-edit-list-grid" ]);
                    Gikam.getComp('emc-project-edit-list-base-info-tab').removePanel('emc-project-edit-list-order-panel');
                    Gikam.cleanCompData([ "emc-project-qc-order-category-edit-list-grid", "emc-project-qc-line-edit-list-grid" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'submit',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-project-qc-edit-list-grid').getSelections());
                }
            } ]
        }
    },

    /** 基本信息页签 */
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-edit-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-edit-list-base-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-edit-list-base-info-form',
                            fields : emcProject.getBaseInfoFormFields1(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-edit-list-director-grid' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-edit-list-director-tab',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-edit-list-director-panel',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-edit-list-director-grid',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    columns : emcProject.getProjectDirectorListColumns(),
                                    readonly : true,
                                } ]
                            } ]

                        } ]
                    }
                } ]

            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                id : 'emc-project-edit-list-order-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-edit-list-order-grid',
                            columns : emcProject.getOrderColumns(),
                            readonly : true,
                            service : 'emcOrderServiceImpl',
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-project-edit-list-order-item-group-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                _this.onOrderActive(row);
                            },
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-edit-list-order-item-group-grid',// 样品分析项组
                            columns : emcProject.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupServiceImpl',
                            readonly : true,
                        } ]
                    }
                } ]
            }, _this.getMonitorSchemePanel(), _this.getQcPlanPanel(), {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-edit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true
                } ]
            } ]
        }
    },

    /** 监测方案 */
    getMonitorSchemePanel : function () {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TAB.PROJ_SCHEME',
            id : 'emc-project-scheme-edit-panel',
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-schemeedit-audit-list-grid',
                        url : '',
                        columns : emcProjScheme.getSchemeEditListColumns(),
                        service : 'emcProjSchemeServiceImpl',
                        checkOnActive : false,
                        readonly : true,
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(1);
                            } else {
                                Gikam.cleanCompData([ 'emc-proj-order-edit-list-grid' ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            _this.onSchemeActive(row);
                        },
                        onRendered : function() {
                            this.hideColumns([ 'ext$.monitortmplname' ]);
                        },
                        group : {
                            fields : [ 'ext$.monitortmplname' ],
                            formatter : function(row) {
                                return Gikam.propI18N('T_EMC_PROJ_SCHEME.ORDERCATEGORY') + ':' + row.ext$.monitortmplname;
                            }
                        },
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-proj-order-edit-list-grid',
                        columns : emcProjScheme.getOrderEditListColumns(),
                        service : 'emcProjOrderServiceImpl',
                        readonly : true,
                    } ]
                }
            } ]
        }
    },

    getQcPlanPanel : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.PROJECT.PROJECTQCS.TITLE.QC_PLAN',
            id : 'emc-project-qc-edit-list-panel',
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-project-qc-order-category-edit-list-grid',
                        columns : emcProjectQc.getQcListColumns(),
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(0);
                            } else {
                                Gikam.cleanCompData([ "emc-project-qc-line-edit-list-grid" ]);
                            }
                        },
                        onRowActive : function(index, row) {
                            _this.renderQcLineGrid(row);
                        },
                    } ]
                },
                center : {
                    items : [ _this.getQcLineList() ]
                }
            } ]
        }
    },

    /** 选中项目 */
    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-edit-list-base-info-tab');
        if (row) {
            var formStyle = row.ext$.formstyle;
            if (row.sample === "1") {
                tab.removePanel('emc-project-edit-list-order-panel');
                tab.showPanel('emc-project-edit-list-base-info-panel');
                tab.revertPanel('emc-project-scheme-edit-panel');
            } else {
                tab.revertPanel('emc-project-edit-list-order-panel');
                tab.removePanel('emc-project-scheme-edit-panel');
            }
            if (formStyle === 'form1') {
                Gikam.getAsyncComp('emc-project-edit-list-base-info-form').done(function (form) {
                    form.refreshFields(emcProject.getBaseInfoFormFields1()).done(function () {
                        form.setReadonly(true);
                    });
                });
            } else if (formStyle === 'form2') {
                Gikam.getAsyncComp('emc-project-edit-list-base-info-form').done(function (form) {
                    form.refreshFields(emcProject.getBaseInfoFormFields2()).done(function () {
                        form.setReadonly(true);
                    });
                });
            } else {
                Gikam.getAsyncComp('emc-project-edit-list-base-info-form').done(function (form) {
                    form.refreshFields(emcProject.getBaseInfoFormFields3()).done(function () {
                        form.setReadonly(true);
                    });
                });
            }

            Gikam.getAsyncComp('emc-project-edit-list-base-info-form').done(function (form) {
                form.refresh({
                    url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                        id : row.id
                    }),
                });
            });

            Gikam.getComp('emc-project-qc-order-category-edit-list-grid').refresh({
                url : Gikam.printf(emcProjectQc.baseUrl + '/{id}/order-categories/queries', {
                    id : row.id
                })
            });

            if (row.sample === "1") {
                Gikam.getAsyncComp('emc-schemeedit-audit-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id
                        }
                    });
                    //grid.refreshColumns(_this.param.editMonitorInfo === '1' ? emcProjScheme.getSchemeEditListColumns1() : emcProjScheme.getSchemeEditListColumns());
                    grid.refresh({
                        url : emcProjScheme.baseUrl + '/queries/raw',
                        order : {
                            'MONITORTMPLID' : 'asc',
                            'FOLDERNO' : 'asc'
                        }
                    });
                });
            } else {
                Gikam.getComp('emc-project-edit-list-order-grid').refresh({
                    url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });
            }

            Gikam.getComp('emc-project-edit-list-uploader').setOptions({
                bizId : row.id,
            });
        }
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                    id : row.id
                })
            });
        }
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-proj-order-edit-list-grid').done(function(grid) {
                grid.refresh({
                    url : Gikam.printf(emcProjScheme.baseUrl + '/{id}/proj-orders/queries', {
                        id : row.id
                    })
                });
            });
        }
    },

    getQcTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-qc-edit-list-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTQCS.TITLE.QC_PLAN',
                id : 'emc-project-qc-edit-list-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-project-qc-order-category-edit-list-grid',
                    columns : emcProjectQc.getQcListColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ "emc-project-qc-line-edit-list-grid" ]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.renderQcLineGrid(row);
                    },
                } ]
            } ]
        }
    },

    getQcLineList : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-qc-line-edit-list-grid',
            columns : emcProjectQc.getQcLineListColumns(),
            service : 'emcProjectQcLineServiceImpl',
            onUpdated : function(data, fields, index) {
                data['qcQty'] = data['colVal'];
                Gikam.put(emcProjectQc.baseUrl + '/instant', Gikam.getJsonWrapper({}, [ 'emcProjectQcLineServiceImpl', [ data ] ]));
            },
        }
    },

    /** 渲染表格 */
    renderQcLineGrid : function(row) {
        var grid = Gikam.getComp('emc-project-qc-line-edit-list-grid');

        var baseUrl = emcProjectQc.baseUrl + '/lines';
        // 获取动态列表头字段
        Gikam.post(baseUrl + '/table-heads/queries', Gikam.getJsonWrapper({}, [ '', [ row ] ])).done(function(columns) {
            var oldColumns = emcProjectQc.getQcLineListColumns();

            var condArr = [];
            columns.forEach(function(column) {
                column['width'] = 130;
                column['editor'] = true;
                column['sort'] = false;
                column['type'] = 'number';
                column['hasArrow'] = true;
                column['range'] = '[0, 100]';
                condArr.push(column);
            });

            var newColumns = oldColumns.slice(0).concat(condArr);
            grid.refreshColumns(newColumns);

            // 查询数据
            Gikam.post(baseUrl + '/table-rows/queries', Gikam.getJsonWrapper({}, [ '', [ row ] ])).done(function(data) {
                grid.loadData(data);
            });
        });

    },

    /** 通过 */
    pass : function() {
        var grid = Gikam.getComp('emc-project-qc-edit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcProject
        }).done(function() {
            grid.refresh();
        });
    },

    /** 拒绝 */
    reject : function() {
        var grid = Gikam.getComp('emc-project-qc-edit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcProject
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-qc-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '23%',
                items : [ _this.getProjectGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab()]
            },
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
