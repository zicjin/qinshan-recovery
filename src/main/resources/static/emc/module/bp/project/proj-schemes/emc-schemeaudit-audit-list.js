emcProject.schemeAuditPage = {

    getGridParam : function() {
        var _this = this;
        _this.param.orgId_SB = workspace.user.ext$.parentorgid;
        return {
            type : 'grid',
            id : 'emc-project-audit-list-grid',
            url : emcProject.baseUrl + '/queries/tasks',
            columns : emcProject.getAuditListColumns(),
            service : 'emcProjectServiceImpl',
            checkOnActive : true,
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var tab = Gikam.getComp('emc-project-audit-list-base-info-tab');
                    tab.removePanel('emc-project-audit-list-base-info-panel2');
                    tab.removePanel('emc-project-audit-list-base-info-panel3');
                    tab.revertPanel('emc-project-audit-list-base-info-panel1');

                    Gikam.getComp('emc-project-audit-list-base-info-form1').cleanData();
                    Gikam.getComp('emc-schemeaudit-audit-list-grid').cleanData();
                    Gikam.getComp('emc-project-audit-list-order-grid').cleanData();
                    Gikam.getComp('emc-project-audit-list-uploader').grid.cleanData();
                    tab.showPanel('emc-schemeaudit-audit-list-grid-panel');
                    Gikam.getComp('emc-schemeaudit-audit-list-director-grid1').cleanData();
                    Gikam.getComp('emc-schemeaudit-audit-list-director-grid2').cleanData();
                    Gikam.getComp('emc-schemeaudit-audit-list-director-grid3').cleanData();

                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
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
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.MORE',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.PREVIEW',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('rasproject', Gikam.getComp('emc-project-audit-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.VIEWAUDIT',
                    icon : 'view-audit',
                    onClick : function() {
                        var grid = Gikam.getComp('emc-project-audit-list-grid');
                        Gikam.viewAudit('T_EMC_PROJECT', grid.getSelections());
                    }
                } ]
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-project-audit-list-grid');
        var selections = grid.getSelections();
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcProject
        }).done(function() {
            grid.refresh();
            let projNos = selections.map(item => item.projNo).join();
            Gikam.alert(projNos + ' 项目已提交，正在生成业务数据...');
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-project-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcProject
        }).done(function() {
            grid.refresh();
        });
    },

    pointSetting : function() {
        var row = Gikam.getComp('emc-project-audit-list-grid').getActivedRow();
        if (Gikam.isEmpty(row.testOrgId)) {
            Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_TESTORGID");
            return;
        }

        Gikam.create('modal', {
            width : 1200,
            height : 600,
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/page/monitor-point-edit-list' + Gikam.param({
                testOrgId : row.testOrgId
            })
        });
    },

    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-audit-list-base-info-tab');
        tab.show();
        if (row) {
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.projId = row.id;
            _this.param.testOrgId = row.testOrgId;
            var formStyle = row.ext$.formstyle;
            if (formStyle === 'form1') {
                tab.removePanel('emc-project-audit-list-base-info-panel2');
                tab.removePanel('emc-project-audit-list-base-info-panel3');
                tab.revertPanel('emc-project-audit-list-base-info-panel1');
                Gikam.getAsyncComp('emc-project-audit-list-base-info-form1').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
            } else if (formStyle === 'form2') {
                tab.removePanel('emc-project-audit-list-base-info-panel1');
                tab.removePanel('emc-project-audit-list-base-info-panel3');
                tab.revertPanel('emc-project-audit-list-base-info-panel2');
                Gikam.getAsyncComp('emc-project-audit-list-base-info-form2').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
            } else {
                tab.removePanel('emc-project-audit-list-base-info-panel1');
                tab.removePanel('emc-project-audit-list-base-info-panel2');
                tab.revertPanel('emc-project-audit-list-base-info-panel3');
                Gikam.getAsyncComp('emc-project-audit-list-base-info-form3').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
            }
            if (row.sample === "1") {
                tab.revertPanel('emc-schemeaudit-audit-list-grid-panel');
                tab.removePanel('emc-project-audit-list-order-panel');
            } else {
                tab.revertPanel('emc-project-audit-list-order-panel');
                tab.removePanel('emc-schemeaudit-audit-list-grid-panel');
                tab.showPanel('emc-project-audit-list-order-panel');
            }
            Gikam.getComp('emc-project-audit-list-uploader').setOptions({
                bizId : row.id,
            });
            Gikam.getComp('emc-schemeaudit-audit-list-grid').setOptions({
                requestData : {
                    projId_SEQ : row.id
                },
            });
            Gikam.getAsyncComp('emc-project-audit-list-order-grid').done(function(grid) {
                grid.refresh({
                    url :  Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });
            });
            /*Gikam.getComp('emc-project-audit-list-order-grid').setOptions({
                url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                    id : row.id
                })
            });*/
            Gikam.getComp('emc-schemeaudit-audit-list-grid').refresh({
                url : emcProjScheme.baseUrl + '/queries/raw'
            });

            // 如果是计划合同任务，则隐藏修改监测方案或者样品的按钮
            if (Gikam.isEmpty(row.splitContractId)) {
                Gikam.getComp('emc-schemeaudit-edit-scheme-button').hide();
                Gikam.getComp('emc-schemeaudit-edit-test-button').hide();
                Gikam.getComp('emc-schemeaudit-edit-order-index-button').hide();
                Gikam.getComp('emc-schemeaudit-edit-order-test-button').hide();
            } else {
                Gikam.getComp('emc-schemeaudit-edit-scheme-button').show();
                Gikam.getComp('emc-schemeaudit-edit-test-button').show();
                Gikam.getComp('emc-schemeaudit-edit-order-index-button').show();
                Gikam.getComp('emc-schemeaudit-edit-order-test-button').show();
            }

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
            id : 'emc-project-audit-list-base-info-tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-audit-list-base-info-panel1',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-audit-list-base-info-form1',
                            fields : emcProject.getBaseInfoFormFields1(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-schemeaudit-audit-list-director-grid1' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-schemeaudit-audit-list-director-tab1',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-schemeaudit-audit-list-director-panel1',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-audit-list-director-grid1',
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
                id : 'emc-project-audit-list-base-info-panel2',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-audit-list-base-info-form2',
                            fields : emcProject.getBaseInfoFormFields2(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-schemeaudit-audit-list-director-grid2' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-schemeaudit-audit-list-director-tab2',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-schemeaudit-audit-list-director-panel2',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-audit-list-director-grid2',
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
                id : 'emc-project-audit-list-base-info-panel3',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-audit-list-base-info-form3',
                            fields : emcProject.getBaseInfoFormFields3(),
                            readonly : true,
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-schemeaudit-audit-list-director-grid3' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-schemeaudit-audit-list-director-tab3',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-schemeaudit-audit-list-director-panel3',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-schemeaudit-audit-list-director-grid3',
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
                id : 'emc-schemeaudit-audit-list-grid-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-schemeaudit-audit-list-grid',
                            url : '',
                            columns : _this.param.editMonitorInfo === '1' ? emcProjScheme.getSchemeEditListColumns1() : emcProjScheme.getSchemeEditListColumns(),
                            service : 'emcProjSchemeServiceImpl',
                            checkOnActive : true,
                            order : {
                                'sortNum' : 'asc',
                            },
                            deleteFormatter : function(row) {
                                return row.folderNo || row.id;
                            },
                            readonly : _this.getHidden(),
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(1);
                                } else {
                                    Gikam.cleanCompData(['emc-proj-order-edit-list-grid']);
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
                                    id : 'emc-schemeaudit-edit-scheme-button',
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
                            order : {
                                'sortNum' : 'asc'
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_TEST',
                                id : 'emc-schemeaudit-edit-test-button',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateItemGroup();
                                }
                            }]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                id : 'emc-project-audit-list-order-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-audit-list-order-grid',
                            columns : emcProject.getOrderColumns(),
                            service : 'emcOrderServiceImpl',
                            page :true,
                            readonly : true,
                            deleteFormatter : function(row) {
                                return row.orderNo || row.id;
                            },
                            order : {
                                'orderNum' : 'asc',
                                'orderNo' : 'asc',
                            },
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-project-audit-list-order-item-group-grid' ]);
                                }
                            },
                            onRowActive : function(index, row) {
                                _this.onOrderActive(row);
                            },
                            toolbar : [{
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INDEX',
                                id : 'emc-schemeaudit-edit-order-index-button',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateOrderIndex();
                                }
                            }]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-audit-list-order-item-group-grid',// 样品分析项组
                            readonly : true,
                            columns : emcProject.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupServiceImpl',
                            order : {
                                'sortNum' : 'asc'
                            },
                            toolbar : [{
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT_TEST',
                                id : 'emc-schemeaudit-edit-order-test-button',
                                icon : 'add',
                                onClick : function() {
                                    _this.editTest();
                                }
                            }]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-audit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true
                } ]
            } ]
        }
    },

    updateCycleAndFrequency : function() {
        var gridData = Gikam.getComp('emc-schemeaudit-audit-list-grid').getSelections();
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
                            Gikam.getComp('emc-schemeaudit-audit-list-grid').refresh();
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
        var gridData = Gikam.getComp('emc-schemeaudit-audit-list-grid').getSelections();
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
        var gridData = Gikam.getComp('emc-schemeaudit-audit-list-grid').getSelections();
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
                            Gikam.getComp('emc-schemeaudit-audit-list-grid').refresh();
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
        var gridData = Gikam.getComp('emc-schemeaudit-audit-list-grid').getSelections();
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

    updateOrderIndex : function() {
        let selections = Gikam.getComp('emc-project-audit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let ids = selections.map(item => item.id).join();

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_INDEX',
            width : '90%',
            height : '90%',
            url : emcProject.baseUrl + '/page/update-order-index' + Gikam.param({
                ids : ids,
                projId : selections[0].projId
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-project-audit-list-order-grid').refresh();
            }
        });
    },

    editTest : function() {
        let selections = Gikam.getComp('emc-project-audit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let orderIds = selections.map(item => item.id).join();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.TEST_EDIT',
            width : '70%',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/page/order-item-group-edit-list' + Gikam.param({
                monitorTmplId : selections[0].monitorTmplId,
                orderCategory : selections[0].orderCategory,
                orderIds : orderIds
            }),
            onAfterClose : function(chooseData) {
                if (!Gikam.isEmpty(chooseData)) {
                    workspace.window.showMask(true);
                    Gikam.put('/secure/emc/module/bp/bp/orders/action/edit', Gikam.getJsonWrapper(null, [ 'emcOrderServiceImpl', selections ], [ 'emcOrderItemGroupServiceImpl', chooseData ])).done(function() {
                        Gikam.getComp('emc-project-audit-list-order-item-group-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    onOrderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-project-audit-list-order-item-group-grid').refresh({
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
        if (this.param.bpmn_statusCode == 'schemeaudit') {
            return true;
        } else {
            return false;
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-audit-list-layout',
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
