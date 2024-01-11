emcProjectPlan.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-plan-edit-list-grid',
            url : emcProject.baseUrl + '/queries/raw',
            columns : emcProjectPlan.getProjectPlanListColumns(),
            service : 'emcProjectServiceImpl',
            requestData : {
                source_SEQ : 'plan'
            },
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var tab = Gikam.getComp('emc-project-plan-edit-list-base-info-tab');
                    tab.removePanel('emc-project-plan-edit-list-base-info-panel2');
                    tab.removePanel('emc-project-plan-edit-list-base-info-panel3');
                    tab.revertPanel('emc-project-plan-edit-list-base-info-panel1');

                    Gikam.getComp('emc-project-plan-edit-list-base-info-form1').cleanData();
                    Gikam.getComp('emc-schemeedit-audit-list-grid').cleanData();
                    Gikam.getComp('emc-project-plan-edit-list-uploader').grid.cleanData();
                    tab.showPanel('emc-schemeedit-audit-list-grid-panel');
                    Gikam.getComp('emc-project-plan-edit-list-director-grid1').cleanData();
                    Gikam.getComp('emc-project-plan-edit-list-director-grid2').cleanData();
                    Gikam.getComp('emc-project-plan-edit-list-director-grid3').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.PROJECT_ADD',
                        height : 300,
                        fields : emcProject.getPreInsertFields(),
                        url : emcProject.baseUrl + '/plans',
                        onFormRendered : function(form) {
                            this.options.id = 'emc-project-edit-list-preinsert-form';
                            this.hideFields([ 'sample' ]);
                        }
                    }).done(function(id) {
                        Gikam.getComp('emc-project-plan-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.PROJECT.PLANS.BUTTON.SET_SCHEDULE',
                icon : 'edit',
                onClick : function() {
                    _this.setSchedule();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.PROJECT.PLANS.BUTTON.STOP_SCHEDULE',
                icon : 'cancel',
                onClick : function() {
                    _this.stopSchedule();
                }
            }, {
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.MORE',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                    icon : 'submit',
                    onClick : function() {
                        _this.submit();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPY',
                    icon : 'copy',
                    hidden : false,
                    onClick : function() {
                        Gikam.create('modal', {
                            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.COPY_PROJECT',
                            url : emcProjectPlan.baseUrl + '/page/copy-plan'
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('emc-project-plan-edit-list-grid').deleteRows(emcProject.baseUrl);
                    }
                } ]
            } ]
        }
    },

    setSchedule : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-project-plan-edit-list-grid').getSelections();
        if (rows.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var schedule = rows[0];
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PLANS.TITLE.SET_SCHEDULE',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/plans/page/schedule' + Gikam.param({
                id : schedule.id,
                startTime : schedule.startTime,
                endTime : schedule.endTime,
                projName : schedule.projName,
                cron : schedule.cron,
            }),
        });
    },

    stopSchedule : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-project-plan-edit-list-grid').getSelections();
        if (rows.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        workspace.window.showMask(true);
        Gikam.put(emcProject.baseUrl + '/schedules/stop', Gikam.getJsonWrapper.apply(null, [ null, [ null, rows ] ])).done(function() {
            Gikam.getComp('emc-project-plan-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    copyProject : function() {
        var selections = Gikam.getComp('emc-project-plan-edit-list-grid').getSelections();
        if (selections.length === 1) {
            var url = Gikam.printf(emcProject.baseUrl + '/{id}/plans/copy', {
                id : selections[0].id
            });
            Gikam.getComp('emc-project-plan-edit-list-grid').insert(url, selections);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-project-plan-edit-list-grid');

        grid.processRows({
            url : emcProject.baseUrl + '/plans/submit',
            confirm : {
                msg : 'EMC.MODULE.BP.PROJECT.PROJECTS.TIP.CONFIRM_SUBMIT',
                title : 'GIKAM.TIP.CONFIRMSUBMIT',
            },
            formatter : function(row) {
                return row.projName;
            },
            method : 'put'
        }).done(function() {
            grid.refresh();
        });
    },

    pointSetting : function(row) {
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
        var tab = Gikam.getComp('emc-project-plan-edit-list-base-info-tab');

        _this.param.projId = row.id;
        _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
        _this.param.testOrgId = row.testOrgId;

        tab.show();
        var _form = Gikam.getComp('emc-project-edit-list-base-info-form1');
        if (row) {
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.projId = row.id;
            _this.param.testOrgId = row.testOrgId;
            var formStyle = row.ext$.formstyle;
            if (formStyle === 'form1') {
                tab.removePanel('emc-project-plan-edit-list-base-info-panel2');
                tab.removePanel('emc-project-plan-edit-list-base-info-panel3');
                tab.revertPanel('emc-project-plan-edit-list-base-info-panel1');
                tab.showPanel('emc-schemeedit-audit-list-grid-panel');
                Gikam.getAsyncComp('emc-project-plan-edit-list-base-info-form1').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                _form = Gikam.getComp('emc-project-plan-edit-list-base-info-form1');
            } else if (formStyle === 'form2') {
                tab.removePanel('emc-project-plan-edit-list-base-info-panel1');
                tab.removePanel('emc-project-plan-edit-list-base-info-panel3');
                tab.revertPanel('emc-project-plan-edit-list-base-info-panel2');
                tab.showPanel('emc-schemeedit-audit-list-grid-panel');
                Gikam.getAsyncComp('emc-project-plan-edit-list-base-info-form2').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                _form = Gikam.getComp('emc-project-plan-edit-list-base-info-form2');
            } else {
                tab.removePanel('emc-project-plan-edit-list-base-info-panel1');
                tab.removePanel('emc-project-plan-edit-list-base-info-panel2');
                tab.revertPanel('emc-project-plan-edit-list-base-info-panel3');
                tab.showPanel('emc-schemeedit-audit-list-grid-panel');
                Gikam.getAsyncComp('emc-project-plan-edit-list-base-info-form3').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                _form = Gikam.getComp('emc-project-plan-edit-list-base-info-form3');
            }

            var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/projects/queries/selectable';
            url += Gikam.param({
                bizCategoryId : row.bizCategoryId,
                activatedFlag : '1',
            });
            Gikam.getJson(url).done(function(items) {
                _form.setSelectOptions('projName', items.map(function(item) {
                    return {
                        text : item.projName,
                        value : item.projName
                    };
                }), true);
            });

            Gikam.getComp('emc-project-plan-edit-list-uploader').setOptions({
                bizId : row.id,
            });
            Gikam.getAsyncComp('emc-schemeedit-audit-list-grid').done(function(grid) {
                grid.setOptions({
                    requestData : {
                        projId_SEQ : row.id
                    }
                });
                grid.refreshColumns(_this.param.editMonitorInfo === '1' ? emcProjScheme.getSchemeEditListColumns1() : emcProjScheme.getSchemeEditListColumns());
                grid.refresh({
                    url : emcProjScheme.baseUrl + '/queries/raw'
                });
            });

            _this._monitorPointType = '';
            _this._sampleOrgId = '';
            _this._sampleOrgName = '';
            _this._selectId.splice(0, _this._selectId.length);
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-project-plan-edit-list-base-info-tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-plan-edit-list-base-info-panel1',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-plan-edit-list-base-info-form1',
                            fields : emcProject.getBaseInfoFormFields1(),
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-plan-edit-list-director-grid1' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-plan-edit-list-director-tab1',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-plan-edit-list-director-panel1',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-plan-edit-list-director-grid1',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    columns : emcProject.getProjectDirectorListColumns(),
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add-row',
                                        onClick : function() {
                                            _this.insertDirectorRow('emc-project-plan-edit-list-base-info-form1', 'emc-project-plan-edit-list-director-grid1');
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            _this.deleteDirectorRow('emc-project-plan-edit-list-director-grid1');
                                        }
                                    } ],
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-plan-edit-list-base-info-panel2',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-plan-edit-list-base-info-form2',
                            fields : emcProject.getBaseInfoFormFields2(),
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-plan-edit-list-director-grid2' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-plan-edit-list-director-tab2',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-plan-edit-list-director-panel2',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-plan-edit-list-director-grid2',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    columns : emcProject.getProjectDirectorListColumns(),
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add-row',
                                        onClick : function() {
                                            _this.insertDirectorRow('emc-project-plan-edit-list-base-info-form2', 'emc-project-plan-edit-list-director-grid2');
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            _this.deleteDirectorRow('emc-project-plan-edit-list-director-grid2');
                                        }
                                    } ],
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.BASE_INFO',
                id : 'emc-project-plan-edit-list-base-info-panel3',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '60%',
                        items : [ {
                            type : 'form',
                            columns : 3,
                            service : 'emcProjectServiceImpl',
                            id : 'emc-project-plan-edit-list-base-info-form3',
                            fields : emcProject.getBaseInfoFormFields3(),
                            onLoadSuccess : function(data) {
                                Gikam.refreshGrids({
                                    'emc-project-plan-edit-list-director-grid3' : emcProject.baseUrl + '/{id}/directors/queries',
                                }, {
                                    id : data.id
                                });
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            id : 'emc-project-plan-edit-list-director-tab3',
                            fill : true,
                            width : Gikam.width.XL,
                            panels : [ {
                                id : 'emc-project-plan-edit-list-director-panel3',
                                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.PROJECT_DIRECTOR',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-plan-edit-list-director-grid3',
                                    filterOpen : false,
                                    service : 'emcProjectDirectorServiceImpl',
                                    page : false,
                                    columns : emcProject.getProjectDirectorListColumns(),
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add-row',
                                        onClick : function() {
                                            _this.insertDirectorRow('emc-project-plan-edit-list-base-info-form3', 'emc-project-plan-edit-list-director-grid3');
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            _this.deleteDirectorRow('emc-project-plan-edit-list-director-grid3');
                                        }
                                    } ],
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TAB.PROJ_SCHEME',
                id : 'emc-schemeedit-audit-list-grid-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'grid',
                            id : 'emc-schemeedit-audit-list-grid',
                            url : '',
                            columns : _this.param.editMonitorInfo === '1' ? emcProjScheme.getSchemeEditListColumns1() : emcProjScheme.getSchemeEditListColumns(),
                            service : 'emcProjSchemeServiceImpl',
                            checkOnActive : true,
                            deleteFormatter : function(row) {
                                return row.folderNo || row.id;
                            },
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
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    var projId = _this.param.projId;
                                    var activeRow = Gikam.getComp('emc-project-plan-edit-list-grid').getActivedRow();
                                    if (Gikam.isEmpty(activeRow)) {
                                        Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_REQUIRED");
                                        return;
                                    }

                                    var editMonitorInfo = _this.param.editMonitorInfo;
                                    var testOrgId = _this.param.testOrgId;
                                    _this.addProj(projId, editMonitorInfo, testOrgId);
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INITIALIZ',
                                icon : 'add',
                                onClick : function() {
                                    var projId = _this.param.projId;
                                    if (Gikam.isEmpty(projId)) {
                                        Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_REQUIRED");
                                        return;
                                    }

                                    var testOrgId = _this.param.testOrgId;
                                    _this.initAddProj(projId, testOrgId);
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    Gikam.getComp('emc-schemeedit-audit-list-grid').deleteRows(emcProjScheme.baseUrl);
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.COPY_SHARE_OR_POINT',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.COPY_SHARE',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.copyShare();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.COPY_POINT',
                                    icon : 'copy',
                                    onClick : function() {
                                        var testOrgId = _this.param.testOrgId;
                                        _this.copyPoint(testOrgId);
                                    }
                                }, ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.EDIT',
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
                            }, ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-proj-order-edit-list-grid',
                            url : _this.getOrderUrl(),
                            columns : emcProjScheme.getOrderEditListColumns(),
                            service : 'emcProjOrderServiceImpl',
                            toolbar : [ {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.UPDATE_TEST',
                                icon : 'edit',
                                onClick : function() {
                                    _this.updateItemGroup();
                                }
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.BATCH_UPDATE_NUM',
                                icon : 'edit',
                                onClick : function() {
                                    _this.batchUpdateSampleNum();
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.SUBCONTRACTOR',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.ADD_SUBCONTRACTOR',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addSubcontractor();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DEL_SUBCONTRACTOR',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.delSubcontractor();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DATASHARING',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.ADD_DATASHARING',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addDataSharing();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.DEL_DATASHARING',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.delDataSharing();
                                    }
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-plan-edit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                } ]
            } ]
        }
    },

    addSubcontractor : function() {
        var selections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : 300,
                height : 300,
                title : Gikam.choose.getConfig('subcontractor').title,
                url : Gikam.choose.getConfig('subcontractor').url + Gikam.param({
                    single : true,
                }),
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.subcontractorId = data[0].id;
                        });
                        Gikam.put('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
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

    delSubcontractor : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (selections.length > 0) {
            selections.map(function(item) {
                item.subcontractorId = '';
                item.subPackageNo = '';
            });
            workspace.window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/' + _this.param.projId, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    addDataSharing : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (selections.length > 0) {
            selections.map(function(item) {
                item.dataSharing = 1;
            });
            workspace.window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/' + _this.param.projId, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    delDataSharing : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (selections.length > 0) {
            selections.map(function(item) {
                item.dataSharing = 0;
            });
            workspace.window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/' + _this.param.projId, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    initAddProj : function(projId, testOrgId) {
        if (Gikam.isEmpty(testOrgId)) {
            Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_TESTORGID");
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.INITIALIZ_SCHEME',
            width : 1500,
            height : 600,
            url : emcProjScheme.baseUrl + '/page/init-scheme' + Gikam.param({
                projId : projId,
                testOrgId : testOrgId
            }),
            onAfterClose : function(data) {
                Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
            }
        });
    },

    _monitorPointType : '',
    _sampleOrgId : '',
    _sampleOrgName : '',
    _selectId : [],
    addProj : function(projId, editMonitorInfo, testOrgId) {
        var _this = this;
        var url = emcProjScheme.baseUrl;
        if (editMonitorInfo == '1') {// 不选择监测点
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                width : 1000,
                height : 580,
                url : emcProjScheme.baseUrl + '/page/select-monitor-tmpl' + Gikam.param({
                    editMonitorInfo : editMonitorInfo
                }),
                onAfterClose : function(tmlData) {
                    if (!Gikam.isEmpty(tmlData)) {
                        var chooseSchemeData = {
                            projId : projId,
                            monitorTmplId : tmlData[0].monitorTmplId
                        }

                        workspace.window.showMask(true);
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', [ chooseSchemeData ] ], [ 'emcProjOrderServiceImpl', tmlData ])).done(function() {
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITORING_POINT',
                width : 800,
                height : 500,
                url : emcProjScheme.baseUrl + '/page/select-monitoring-point' + Gikam.param({
                    testOrgId : testOrgId,
                    monitorPointType : _this._monitorPointType,
                    sampleOrgId : _this._sampleOrgId,
                    sampleOrgName : _this._sampleOrgName,
                    selectIds : _this._selectId
                }),
                onAfterClose : function(pointData) {
                    if (!Gikam.isEmpty(pointData)) {
                        _this._selectId.splice(0, _this._selectId.length);
                        pointData.map(function(item) {
                            _this._selectId.push(item.id);
                        });
                        _this._monitorPointType = pointData[0].monitorPointType;
                        _this._sampleOrgId = pointData[0].sampleDepartmentId;
                        _this._sampleOrgName = pointData[0].sampleDepartment;
                        Gikam.create('modal', {
                            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                            width : 1000,
                            height : 580,
                            url : emcProjScheme.baseUrl + '/page/select-monitor-tmpl' + Gikam.param({
                                editMonitorInfo : editMonitorInfo,
                                monitorPointCategoryId : pointData[0].monitorPointCategoryId
                            }),
                            onAfterClose : function(tmlData) {
                                if (!Gikam.isEmpty(tmlData)) {
                                    if (tmlData == 'PREVIOUS') {
                                        emcProject.schemeEditPage.addProj(projId, editMonitorInfo, testOrgId);
                                    } else {
                                        var chooseSchemeData = pointData.map(function(item) {
                                            return {
                                                projId : projId,
                                                monitorPointCategoryId : item.monitorPointCategoryId,
                                                monitorPointCategoryName : item.ext$.templatename,
                                                monitorPointId : item.id,
                                                monitorPointNo : item.monitorPointNo,
                                                monitorPointName : item.monitorPointName,
                                                monitorTmplId : tmlData[0].monitorTmplId,
                                                sampleOrgId : item.sampleDepartmentId
                                            }
                                        });

                                        var chooseOrderData = tmlData.map(function(item) {
                                            return {
                                                testId : item.testId,
                                                methodId : item.methodId,
                                                itemGroupId : item.itemGroupId,
                                                itemGroup : item.itemGroup,
                                                schemeName : item.schemeName
                                            }
                                        });
                                        workspace.window.showMask(true);
                                        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', chooseSchemeData ], [ 'emcProjOrderServiceImpl', chooseOrderData ])).done(function() {
                                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                                            _this._monitorPointType = '';
                                            _this._sampleOrgId = '';
                                            _this._sampleOrgName = '';
                                            _this._selectId.splice(0, _this._selectId.length);
                                        }).always(function() {
                                            workspace.window.closeMask();
                                        });
                                    }
                                } else {
                                    _this._monitorPointType = '';
                                    _this._sampleOrgId = '';
                                    _this._sampleOrgName = '';
                                    _this._selectId.splice(0, _this._selectId.length);
                                }
                            }
                        });
                    }
                }
            });
        }
    },

    copyShare : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_SHARE',
                width : 300,
                height : 200,
                url : emcProjScheme.baseUrl + '/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProjScheme.baseUrl + '/action/copy';
                        Gikam.post(url, Gikam.getJsonWrapper({
                            copies : data.copyShare
                        }, [ '', gridData ])).always(function() {
                            workspace.window.closeMask();
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copyPoint : function(testOrgId) {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_POINT',
            width : 800,
            height : 600,
            url : emcProjScheme.baseUrl + '/page/select-monitoring-point' + Gikam.param({
                testOrgId : testOrgId,
                monitorPointCategoryId : gridData[0].monitorPointCategoryId
            }),
            onAfterClose : function(pointData) {
                if (!Gikam.isEmpty(pointData)) {
                    workspace.window.showMask(true);
                    var chooseSchemeData = pointData.map(function(item) {
                        return {
                            monitorPointCategoryId : item.monitorPointCategoryId,
                            monitorPointCategoryName : item.ext$.templatename,
                            monitorPointId : item.id,
                            monitorPointNo : item.monitorPointNo,
                            monitorPointName : item.monitorPointName
                        }
                    });

                    var url = emcProjScheme.baseUrl + '/action/copy-point'
                    Gikam.post(url, Gikam.getJsonWrapper({}, [ '', gridData ], [ '', chooseSchemeData ])).always(function() {
                        Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    updateCycleAndFrequency : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_CYCLE_FREQUENCY',
                width : 400,
                height : 200,
                url : emcProjScheme.baseUrl + '/page/update-cycle-and-frequency',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var selectedIds = gridData.map(function(item) {
                            return {
                                id : item.id
                            }
                        });
                        Gikam.put(emcProjScheme.baseUrl + '/action/update-frequency', Gikam.getJsonWrapper({
                            cycle : data.cycle,
                            frequency : data.frequency
                        }, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
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

    batchUpdateSampleNum : function() {
        var gridData = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        var activeRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_SAMPLENUM',
                width : 400,
                height : 200,
                url : emcProjScheme.baseUrl + '/page/proj-order-batch-update',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        gridData.map(function(item) {
                            item.sampleNum = data.sampleNum;
                        });
                        Gikam.put(emcProjScheme.baseUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
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
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_TEST',
                width : 1000,
                height : 580,
                url : emcProjScheme.baseUrl + '/page/proj-order-update-item-group' + Gikam.param({
                    monitorTmplId : gridData[0].monitorTmplId,
                    projSchemeId : gridData[0].id
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
                        var url = emcProjScheme.baseUrl + '/action/proj-orders/update-item-group';
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

    updateIndex : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            var ids = "";
            gridData.map(function(item, i) {
                ids += gridData[i].id + ',';
            });
            ids = ids.substring(0, ids.length - 1);
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.UPDATE_INDEX',
                width : 1500,
                height : 650,
                url : emcProjScheme.baseUrl + '/page/update-index' + Gikam.param({
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
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
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
                        Gikam.put(emcProjScheme.baseUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
                            Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
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

    insertDirectorRow : function(form, grid) {
        var formData = Gikam.getComp(form).getData();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('emp').title,
            url : Gikam.choose.getConfig('emp').url + Gikam.param({
                targetType : 'insertDirector',
                projectId : formData.id
            }),
            width : 310,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        empId : item.id,
                        projectId : formData.id
                    }
                });
                Gikam.getComp(grid).insert(emcProject.baseUrl + '/directors', chooseData);
            }
        });
    },

    deleteDirectorRow : function(grid) {
        var url = emcProject.baseUrl + '/directors';
        Gikam.getComp(grid).deleteRows(url);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-plan-edit-list-layout',
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

    init : function() {
        this.param = {};
        this.create();
    }
};
