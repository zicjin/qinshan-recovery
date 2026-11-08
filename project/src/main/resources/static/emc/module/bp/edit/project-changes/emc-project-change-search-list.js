emcProjectChange.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-change-search-list-grid',
            url : emcProjectChange.baseUrl + '/queries/searchable',
            columns : emcProjectChange.getEditListColumns(),
            service : 'emcProjectChangeServiceImpl',
            page : true,
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            requestData : {
                targetType : 'projEdit',
                org_filter : '1',
                processStatus_SEQ : 'done'
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-project-change-search-list-base-info-form", "emc-project-change-search-list-order-grid", "emc-project-change-search-list-order-item-group-grid", "emc-project-change-search-list-director-grid", "emc-pcs-schemeedit-search-list-grid", "emc-pcs-proj-order-edit-list-grid" ]);
                    Gikam.getComp('emc-project-change-search-list-base-info-tab').removePanel('emc-project-change-search-list-order-panel');
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : []
        }
    },

    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-change-search-list-base-info-tab');
        tab.show();
        if (row) {
            if(_this.param === undefined) {
                _this.param = {};
            }
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.sampleOrgId = row.ext$.sampleorgid;
            _this.param.projId = row.id;
            // 使用受检单位来过滤数据
            _this.param.testOrgId = row.testOrgId;

            if (row.sample === "1") {
                tab.removePanel('emc-project-change-search-list-order-panel');
                tab.showPanel('emc-project-change-search-list-base-info-panel');
                if (tab.options.panels.some(function (panel) { return panel.id === 'emc-project-sample-subpackage-info-panel'; })) {
                    tab.removePanel('emc-project-sample-subpackage-info-panel');
                }
                tab.revertPanel('emc-pcs-scheme-edit-panel');
            } else {
                tab.revertPanel('emc-project-change-search-list-order-panel');
                tab.removePanel('emc-pcs-scheme-edit-panel');
            }

            Gikam.getAsyncComp('emc-project-change-search-list-base-info-form').done(function (form) {
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/projects/queries/selectable';
                url += Gikam.param({
                    bizCategoryId : row.bizCategoryId,
                    activatedFlag : '1',
                });
                Gikam.getJson(url).done(function(items) {
                    form.setSelectOptions('projName', items.map(function(item) {
                        return {
                            text : item.projName,
                            value : item.projName
                        };
                    }), true);
                });

                form.refresh({
                    url : Gikam.printf(emcProjectChange.baseUrl + '/{id}', {
                        id : row.id
                    }),
                });
            });

            if (row.sample === "1") {
                Gikam.getAsyncComp('emc-pcs-schemeedit-search-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id
                        }
                    });
                    grid.refreshColumns(_this.param.editMonitorInfo === '1' ? emcProjectChange.getSchemeEditListColumns1() : emcProjectChange.getSchemeEditListColumns());
                    grid.refresh({
                        url : emcProjectChange.schemeUrl + '/queries/raw',
                        order : {
                            'MONITORTMPLID' : 'asc',
                            'FOLDERNO' : 'asc'
                        }
                    });
                });
                Gikam.getAsyncComp('emc-project-change-search-list-sample-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            org_filter : '1',
                        }
                    });
                    grid.refreshColumns(emcProjectChange.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            } else {
                Gikam.getComp('emc-project-change-search-list-order-grid').refresh({
                    url : Gikam.printf(emcProjectChange.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });
                Gikam.getAsyncComp('emc-project-change-search-list-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            org_filter : '1',
                        }
                    });
                    grid.refreshColumns(emcProjectChange.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            }


            Gikam.getComp('emc-project-change-search-list-uploader').setOptions({
                bizId : row.id,
            });

            // 查询合同附件
            Gikam.getAsyncComp('emc-project-change-search-list-contract-uploader').done(function (uploader) {
                Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts/queries', Gikam.getJsonWrapper({
                    f : {
                        contractNo_SEQ : row.ext$.contractno
                    }
                }, [])).done(function (data) {
                    if (!Gikam.isEmpty(data.rows)) {
                        let targetIds = ['T_CRM_OPPORTUNITY_CONTRACT$' + data.rows[0].id, 'T_CRM_OPPORTUNITY_MAIN$' + data.rows[0].opportunityId];
                        uploader.grid.refresh({
                            requestData : {
                                targetId : '',
                                targetId_IN : targetIds.join()
                            },
                            url : uploader.options.searchUrl
                        })
                    }
                });
            });
        }
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-change-search-list-order-item-group-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/edit/project-change/orders/{id}/item-groups/queries', {
                    id : row.id
                })
            });
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-project-change-search-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-change-search-list-base-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectChangeServiceImpl',
                            id : 'emc-project-change-search-list-base-info-form',
                            panels : [
                                {title : '合同信息',fields : emcProjectChange.getBaseInfoFormFields11()},
                                {title : '委托单位信息',fields : emcProjectChange.getBaseInfoFormFields12()},
                                {title : '受检单位',fields : emcProjectChange.getBaseInfoFormFields13()},
                                {title : '项目信息',fields : emcProjectChange.getBaseInfoFormFields14()},
                            ],
                            readonly : true,
                            onUpdated : function() {
                                var grid = Gikam.getComp('emc-project-change-search-list-grid');
                                grid.refreshRowById(grid.getActivedRow().id);
                            },
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-change-search-list-director-grid' : emcProjectChange.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-change-search-list-director-tab',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-change-search-list-director-panel',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-change-search-list-director-grid',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [ ],
                                    columns : emcProjectChange.getProjectDirectorListColumns()
                                } ]
                            } ]

                        } ]
                    }
                } ]

            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                id : 'emc-project-change-search-list-order-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-change-search-list-order-grid',
                            columns : emcProjectChange.getOrderColumns(),
                            service : 'emcOrderChangeServiceImpl',
                            page :true,
                            readonly : true,
                            deleteFormatter : function(row) {
                                return row.orderNo || row.id;
                            },
                            order : {
                                'orderNo' : 'asc',
                            },
                            onBeforeUpdate  : function(row, keys) {
                                if (row && row.sendDate) {
                                    let sendDate = new Date(row.sendDate);
                                    let now = new Date();
                                    if (sendDate.getTime() > now.getTime()) {
                                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SEND_DATE_CAN_NOT_OVER_NOW');
                                        return false;
                                    }
                                }
                            },
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-project-change-search-list-order-item-group-grid' ]);
                                }
                            },
                            onRendered : function() {
                                this.hideColumns([ 'qcCategoryCode' ]);
                            },
                            onRowActive : function(index, row) {
                                _this.onOrderActive(row);
                            },
                            toolbar : [ ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-change-search-list-order-item-group-grid',// 样品分析项组
                            columns : emcProjectChange.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupChangeServiceImplImpl',
                            readonly : true,
                            toolbar : [ ]
                        } ]
                    }
                } ]
            }, _this.getMonitorSchemePanel(), {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-change-search-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true,
                } ]
            }, {
                title : '合同附件',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-change-search-list-contract-uploader',
                    dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                    readonly : true,
                } ]
            } ]
        }
    },

    /** 监测方案 */
    getMonitorSchemePanel : function () {
        var _this = this;
        return {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TAB.PROJ_SCHEME',
            id : 'emc-pcs-scheme-edit-panel',
            items : [ {
                type : 'layout',
                north : {
                    height : '50%',
                    items : [ {
                        type : 'grid',
                        id : 'emc-pcs-schemeedit-search-list-grid',
                        url : '',
                        columns : emcProjectChange.getSchemeEditListColumns(),
                        service : 'emcProjSchemeChangeServiceImpl',
                        checkOnActive : false,
                        readonly : true,
                        deleteFormatter : function(row) {
                            return row.folderNo || row.id;
                        },
                        onLoadSuccess : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                this.activeRowByIndex(1);
                            } else {
                                Gikam.cleanCompData([ 'emc-pcs-proj-order-edit-list-grid' ]);
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
                        toolbar : [ ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-pcs-proj-order-edit-list-grid',
                        url : _this.getOrderUrl(),
                        columns : emcProjectChange.getOrderEditListColumns(),
                        service : 'emcProjOrderChangeServiceImpl',
                        readonly : true,
                        group : {
                            fields : [ "schemeName"],
                            formatter : function(row) {
                                return row.schemeName;
                            }
                        },
                        toolbar : [ ]
                    } ]
                }
            } ]
        }
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-pcs-proj-order-edit-list-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getOrderUrl(row.id),
                    order : {
                        schemeName : 'asc'
                    }
                });
            });
        }
    },

    getOrderUrl : function(projSchemeId) {
        if (projSchemeId) {
            return Gikam.printf(emcProjectChange.schemeUrl + '/{id}/proj-orders/queries', {
                id : projSchemeId
            })
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-change-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '23%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
