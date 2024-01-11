emcProject.schemeQueryPage = {

    getGridParam : function() {
        var _this = this;
        _this.param.orgId_SB = workspace.user.ext$.parentorgid;
        return {
            type : 'grid',
            id : 'emc-project-query-list-grid',
            url : emcProject.baseUrl + '/queries',
            columns : emcProject.getAuditListColumns(),
            service : 'emcProjectServiceImpl',
            checkOnActive : true,
            filterOpen : true,
            requestData : {
                orgId_SB : workspace.user.ext$.parentorgid,
                targetType : 'schemeQuery'
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var tab = Gikam.getComp('emc-project-query-list-base-info-tab');
                    tab.removePanel('emc-project-query-list-base-info-panel2');
                    tab.removePanel('emc-project-query-list-base-info-panel3');
                    tab.revertPanel('emc-project-query-list-base-info-panel1');

                    Gikam.getComp('emc-project-query-list-base-info-form1').cleanData();
                    Gikam.getComp('emc-schemeaudit-query-list-grid').cleanData();
                    Gikam.getComp('emc-project-query-list-order-grid').cleanData();
                    Gikam.getComp('emc-project-query-list-uploader').grid.cleanData();
                    tab.showPanel('emc-schemeaudit-query-list-grid-panel');
                    Gikam.getComp('emc-schemeaudit-query-list-director-grid1').cleanData();
                    Gikam.getComp('emc-schemeaudit-query-list-director-grid2').cleanData();
                    Gikam.getComp('emc-schemeaudit-query-list-director-grid3').cleanData();

                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-project-query-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-project-query-list-grid');
                    Gikam.viewAudit('T_EMC_PROJECT', grid.getSelections());
                }
            }]
        }
    },
    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-query-list-base-info-tab');
        tab.show();
        if (row) {
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.projId = row.id;
            _this.param.testOrgId = row.testOrgId;
            var formStyle = row.ext$.formstyle;
            if (formStyle === 'form1') {
                tab.removePanel('emc-project-query-list-base-info-panel2');
                tab.removePanel('emc-project-query-list-base-info-panel3');
                tab.revertPanel('emc-project-query-list-base-info-panel1');
                Gikam.getAsyncComp('emc-project-query-list-base-info-form1').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
            } else if (formStyle === 'form2') {
                tab.removePanel('emc-project-query-list-base-info-panel1');
                tab.removePanel('emc-project-query-list-base-info-panel3');
                tab.revertPanel('emc-project-query-list-base-info-panel2');
                Gikam.getAsyncComp('emc-project-query-list-base-info-form2').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
            } else {
                tab.removePanel('emc-project-query-list-base-info-panel1');
                tab.removePanel('emc-project-query-list-base-info-panel2');
                tab.revertPanel('emc-project-query-list-base-info-panel3');
                Gikam.getAsyncComp('emc-project-query-list-base-info-form3').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
            }
            if (row.sample === "1") {
                tab.revertPanel('emc-schemeaudit-query-list-grid-panel');
                tab.removePanel('emc-project-query-list-order-panel');
            } else {
                tab.revertPanel('emc-project-query-list-order-panel');
                tab.removePanel('emc-schemeaudit-query-list-grid-panel');
            }
            Gikam.getComp('emc-project-query-list-uploader').setOptions({
                bizId : row.id,
            });
            Gikam.getComp('emc-schemeaudit-query-list-grid').setOptions({
                requestData : {
                    projId_SEQ : row.id
                },
            });
            Gikam.getAsyncComp('emc-project-query-list-order-grid').done(function(grid) {
                grid.refresh({
                    url :  Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });
            });
            /*Gikam.getComp('emc-project-query-list-order-grid').setOptions({
                url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                    id : row.id
                })
            });*/
            Gikam.getComp('emc-schemeaudit-query-list-grid').refresh({
                url : emcProjScheme.baseUrl + '/queries/raw'
            });

            // 查询报告发送信息
            Gikam.getAsyncComp('emc-schemeaudit-report-send-info-grid').done(function (grid) {
                grid.refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/report/reports/report-send-info/queries',
                    requestData : {
                        projId_SEQ : row.id
                    }
                })
            });

            // 查询其他费用信息
            Gikam.getAsyncComp('emc-schemeaudit-other-fee-grid').done(function (grid) {
                grid.refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/project/other-fees/queries',
                    requestData : {
                        projId_SEQ : row.id
                    }
                })
            });
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-project-query-list-base-info-tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-query-list-base-info-panel1',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-query-list-base-info-form1',
                            fields : emcProject.getBaseInfoFormFields1(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-schemeaudit-query-list-director-grid1' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-schemeaudit-query-list-director-tab1',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-schemeaudit-query-list-director-panel1',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-query-list-director-grid1',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    readonly : true,
                                    columns : emcProject.getProjectDirectorListColumns()
                                } ]
                            }, {
                                id : 'emc-schemeaudit-report-send-info-panel',
                                title : '报告发送信息',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-report-send-info-grid',
                                    filterOpen : false,
                                    service : 'emcReportSendInfoServiceImpl',
                                    page : false,
                                    readonly : true,
                                    toolbar : [],
                                    columns : emcProject.getReportSendInfoListColumns()
                                } ]
                            }, {
                                id : 'emc-schemeaudit-other-fee-panel',
                                title : '其他费用信息',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-other-fee-grid',
                                    filterOpen : false,
                                    service : 'emcProjectOtherFeeServiceImpl',
                                    page : false,
                                    readonly : true,
                                    columns : emcProject.getProjectOtherFeeListColumns()
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-query-list-base-info-panel2',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-query-list-base-info-form2',
                            fields : emcProject.getBaseInfoFormFields2(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-schemeaudit-query-list-director-grid2' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-schemeaudit-query-list-director-tab2',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-schemeaudit-query-list-director-panel2',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-query-list-director-grid2',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    readonly : true,
                                    columns : emcProject.getProjectDirectorListColumns()
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-query-list-base-info-panel3',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-query-list-base-info-form3',
                            fields : emcProject.getBaseInfoFormFields3(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-schemeaudit-query-list-director-grid3' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-schemeaudit-query-list-director-tab3',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-schemeaudit-query-list-director-panel3',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-query-list-director-grid3',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    readonly : true,
                                    columns : emcProject.getProjectDirectorListColumns()
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TAB.PROJ_SCHEME',
                id : 'emc-schemeaudit-query-list-grid-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-schemeaudit-query-list-grid',
                            url : '',
                            columns : _this.param.editMonitorInfo === '1' ? emcProjScheme.getSchemeEditListColumns1() : emcProjScheme.getSchemeEditListColumns(),
                            service : 'emcProjSchemeServiceImpl',
                            checkOnActive : true,
                            deleteFormatter : function(row) {
                                return row.folderNo || row.id;
                            },
                            readonly : _this.getHidden(),
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(1);
                                } else {
                                    Gikam.getComp('emc-proj-order-edit-list-grid').cleanData();
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
                            toolbar : [
                                {
                                    type : 'dropDownMenu',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.EDIT',
                                    hidden : true,
                                    items : [ {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_CYCLEFREQUENCY',
                                        icon : 'edit',
                                        onClick : function() {
                                            _this.updateCycleAndFrequency();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INDEX',
                                        icon : 'edit',
                                        onClick : function() {
                                            _this.updateIndex();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_ORG',
                                        icon : 'edit',
                                        onClick : function() {
                                            _this.updateOrg();
                                        }
                                    } ]
                                }
                            ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-proj-order-edit-list-grid',
                            url : _this.getOrderUrl(),
                            columns : emcProjScheme.getOrderEditListColumns(),
                            service : 'emcProjOrderServiceImpl',
                            readonly : _this.getHidden(),
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_TEST',
                                icon : 'edit',
                                hidden : true,
                                onClick : function() {
                                    _this.updateItemGroup();
                                }
                            }]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                id : 'emc-project-query-list-order-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-query-list-order-grid',
                            columns : emcProject.getOrderColumns(),
                            service : 'emcOrderServiceImpl',
                            page :true,
                            readonly : true,
                            deleteFormatter : function(row) {
                                return row.orderNo || row.id;
                            },
                            order : {
                                'orderNo' : 'asc',
                            },
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-project-query-list-order-item-group-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                _this.onOrderActive(row);
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-query-list-order-item-group-grid',// 样品分析项组
                            readonly : true,
                            columns : emcProject.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupServiceImpl',
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-query-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true
                } ]
            } ]
        }
    },

    updateCycleAndFrequency : function() {
        var gridData = Gikam.getComp('emc-schemeaudit-query-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_CYCLE_FREQUENCY',
                width : 400,
                height : 200,
                url : emcProject.schemeUrl + '/page/update-cycle-and-frequency',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var selectedIds = gridData.map(function(item) {
                            return {
                                id : item.id
                            }
                        });
                        Gikam.put(emcProject.schemeUrl + '/action/update-frequency', Gikam.getJsonWrapper({
                            cycle : data.cycle,
                            frequency : data.frequency
                        }, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-schemeaudit-query-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateIndex : function() {
        var gridData = Gikam.getComp('emc-schemeaudit-query-list-grid').getSelections();
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function(item, i) {
                ids += gridData[i].id + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_INDEX',
                width : '90%',
                height : '90%',
                url : emcProject.schemeUrl + '/page/update-index' + Gikam.param({
                    ids : ids,
                    monitorTmplGroupId : gridData[0].ext$.monitortmplgroupid
                }),
                onAfterClose : function() {
                    Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateOrg : function() {
        var gridData = Gikam.getComp('emc-schemeaudit-query-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('org-simple').title,
                width : 480,
                height : 150,
                url : '/secure/core/module/mdm/orgs/page/select-list',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        gridData.map(function(item) {
                            item.sampleOrgId = data.sampleOrgId;
                        });
                        Gikam.put(emcProject.schemeUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-schemeaudit-query-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    updateItemGroup : function() {
        var gridData = Gikam.getComp('emc-schemeaudit-query-list-grid').getSelections();
        if (gridData.length > 0) {
            var array = new Array();
            for(j = 0,len = gridData.length; j < len; j++) {
                var ext1 = gridData[j].ext$;
                var monitortmplname = ext1.monitortmplname;
                var object1 =new Object();
                object1.monitortmplname = monitortmplname;
                array.push(object1);
            }
            var mnamesame;
            for(i = 0,len=array.length; i < len; i++){
                if(array[0].monitortmplname != array[i].monitortmplname){
                    mnamesame = false;
                    break;
                }
            }
            if(mnamesame  == false){
                Gikam.alert('GIKAM.TIP.MISMATCH_MONITOR_TMPLNAME');
                return;
            }
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_TEST',
                width : 1000,
                height : 580,
                url : emcProject.schemeUrl + '/page/proj-order-update-item-group' + Gikam.param({
                    monitorTmplId : gridData[0].monitorTmplId,
                    projSchemeId : gridData[0].id,
                    testmethodflag :'1',
                }),
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var orderData = data.map(function(item) {
                            return {
                                testId : item.testId,
                                methodId : item.methodId,
                                itemGroupId : item.itemGroupId,
                                itemGroup : item.itemGroup,
                                schemeName : item.schemeName
                            }
                        });
                        var url = emcProject.schemeUrl + '/action/proj-orders/update-item-group';
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', gridData ], [ 'emcProjOrderServiceImpl', orderData ])).always(function() {
                            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-query-list-order-item-group-grid').refresh({
                url : Gikam.printf('/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                    id : row.id
                })
            });
        }
    },

    getOrderUrl : function(projSchemeId) {
        if (projSchemeId) {
            return Gikam.printf(emcProjScheme.baseUrl + '/{id}/proj-orders/queries', {
                id : projSchemeId
            })
        }
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-proj-order-edit-list-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getOrderUrl(row.id)
                });
            });
        }
    },

    getHidden : function() {
        return true;
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-query-list-layout',
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
