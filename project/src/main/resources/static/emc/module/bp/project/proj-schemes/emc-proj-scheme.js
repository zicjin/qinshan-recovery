var emcProjScheme = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes',

    getEditListColumns : function() {
        return [ {
            field : 'into',
            title : 'T_EMC_PROJ_SCHEME.INTO',
            width : 70,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcProject.schemeEditPage.getInto(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '进入'
                });
            }
        }, {
            field : 'monitorPointCategoryName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTTYPE'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINT'
        }, {
            field : 'itemGroups',
            title : 'T_EMC_PROJ_SCHEME.INDEXNAME'
        }, {
            field : 'cycle',
            width : 70,
            title : 'T_EMC_PROJ_SCHEME.CYCLE'
        }, {
            field : 'frequency',
            width : 70,
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY'
        }, {
            field : 'orgName',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID'
        } ];
    },

    getEditListColumns1 : function() {
        return [ {
            field : 'into',
            title : 'T_EMC_PROJ_SCHEME.INTO',
            width : 70,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcProject.schemeAuditPage.getInto(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '进入'
                });
            }
        }, {
            field : 'monitorPointCategoryName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTTYPE'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINT'
        }, {
            field : 'itemGroups',
            title : 'T_EMC_PROJ_SCHEME.INDEXNAME'
        }, {
            field : 'cycle',
            width : 70,
            title : 'T_EMC_PROJ_SCHEME.CYCLE'
        }, {
            field : 'frequency',
            width : 70,
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY'
        }, {
            field : 'orgName',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID'
        } ];
    },

    getSchemeEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortNum',
            title : 'T_EMC_PROJ_SCHEME.SORTNUM',
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            type : 'choose',
            category : 'monitorPoint',
            single : true,
            cleanable : false,
            onBeforeChoose : function() {
                var formData = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                var testOrgId = formData.testOrgId ? formData.testOrgId : '-';
                return {
                    clientId_SEQ : testOrgId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorPointId : rows[0].id,
                        monitorPointNo : rows[0].monitorPointNo,
                        monitorPointName : rows[0].monitorPointName,
                        monitorPointCategoryId : rows[0].monitorPointCategoryId,
                        monitorPointCategoryName : rows[0].ext$.templatename
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointId : '',
                    monitorPointNo : '',
                    monitorPointName : '',
                    monitorPointCategoryId : '',
                    monitorPointCategoryName : ''
                });
            },
            editor : true
        }, {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            range : '[1,24]',
            isInteger : true,
            width : 85,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
            range : '[1,24]',
            isInteger : true,
            width : 85,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'sampleOrgId',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            editor : true,
            type : 'select',
            search : true,
            category : 'org',
            param : {
                sampleDepartment_SEQ : '1'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'richText',
            editor : true
        } ];
    },

    getSchemeEditListColumns1 : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortNum',
            title : 'T_EMC_PROJ_SCHEME.SORTNUM',
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID',
            editable : true,
/*            type : 'choose',
            category : 'monitorPoint',
            editable : true,
            cleanable : false,
            single : true,
            onBeforeChoose : function() {
                var formData = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                var testOrgId = formData.testOrgId ? formData.testOrgId : '-';
                return {
                    clientId_SEQ : testOrgId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorPointId : rows[0].id,
                        monitorPointNo : rows[0].monitorPointNo,
                        monitorPointName : rows[0].monitorPointName,
                        monitorPointCategoryId : rows[0].monitorPointCategoryId,
                        monitorPointCategoryName : rows[0].ext$.templatename
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointId : '',
                    monitorPointNo : '',
                    monitorPointName : '',
                    monitorPointCategoryId : '',
                    monitorPointCategoryName : ''
                });
            },*/
            editor : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            type : 'choose',
            category : 'monitorPoint',
            editable : true,
            cleanable : false,
            single : true,
            onBeforeChoose : function() {
                var formData = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                var testOrgId = formData.testOrgId ? formData.testOrgId : '-';
                return {
                    clientId_SEQ : testOrgId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorPointId : rows[0].id,
                        monitorPointNo : rows[0].monitorPointNo,
                        monitorPointName : rows[0].monitorPointName,
                        monitorPointCategoryId : rows[0].monitorPointCategoryId,
                        monitorPointCategoryName : rows[0].ext$.templatename
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointId : '',
                    monitorPointNo : '',
                    monitorPointName : '',
                    monitorPointCategoryId : '',
                    monitorPointCategoryName : ''
                });
            },
            editor : true
        }, {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            type : 'number',
            range : '[1,24]',
            isInteger : true,
            width : 55,
            hasArrow : true,
            editor : true
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
            range : '[1,24]',
            isInteger : true,
            width : 55,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'sampleOrgId',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            editor : true,
            type : 'select',
            search : true,
            category : 'org',
            param : {
                sampleDepartment_SEQ : '1'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'richText',
            editor : true
        } ];
    },

    getMonitoringPointFormFields : function() {
        return [ {
            field : 'monitorPointType',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTTYPE',
            type : 'select',
            category : 'filterPointCategory',
            param : {
                clientId : emcProjScheme.selectPointPage.param.testOrgId == '-' ? undefined : emcProjScheme.selectPointPage.param.testOrgId,
                monitorPointCategoryId : emcProjScheme.selectPointPage.param.monitorPointCategoryId
            },
            onChange : function(name, value, index) {
                if (!Gikam.isEmpty(value)) {
                    var _form = this;
                    Gikam.getComp('emc-select-monitoring-point-list-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/monitor-points/queries/choosable',
                        requestData : {
                            monitorPointCategoryId_SEQ : _form.getData().monitorPointType,
                            clientId_SEQ : emcProjScheme.selectPointPage.param.testOrgId == '-' ? undefined : emcProjScheme.selectPointPage.param.testOrgId
                        }
                    });
                } else {
                    Gikam.getComp('emc-select-monitoring-point-list-grid').cleanData();
                }
            }
        }, {
            field : 'sampleDepartmentId',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            type : 'select',
            category : 'org',
            param : {
                sampleDepartment_SEQ : '1',
                parentid_SEQ : workspace.user.ext$.parentorgid
            },
        } ];
    },

    getMonitoringPointListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME'
        }, {
            field : 'ext$.templatename',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTTYPE',
        }, {
            field : 'monitorPlace',
            title : 'T_EMC_PROJ_SCHEME.MONITORPLACE'
        } ];
    },

    getShowAllFormFields : function() {
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value) {
                var _form = Gikam.getComp('emc-select-monitor-tmpl-form');
                var schemeName = _form.getData().monitorSchemeId_SEQ;
                var monitorTmplId = _form.getData().monitorTmplId_SEQ;
                _form.setData({
                    showFlag : value
                });
                if (Gikam.isEmpty(schemeName) || Gikam.isEmpty(monitorTmplId)) {
                    return;
                }
                if (value === '1') {
                    var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
                    leftGrid.setOptions({
                        url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
                        requestData : {
                            schemeName_SEQ : schemeName,
                            monitorTmplId_SEQ : monitorTmplId,
                            defaultFlag_SEQ : '',
                            activatedflag_SEQ : '1',
                        }
                    });
                    leftGrid.refresh();
                } else {
                    var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
                    leftGrid.setOptions({
                        url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
                        requestData : {
                            schemeName_SEQ : schemeName,
                            monitorTmplId_SEQ : monitorTmplId,
                            defaultFlag_SEQ : '1',
                            activatedflag_SEQ : '1',
                        }
                    });
                    leftGrid.refresh();
                }
            },
        } ]
    },

    getFilterMonitorTmplFormFields : function() {
        return [ {
            title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUP',
            field : 'monitorTmplGroupId_SEQ',
            type : 'select',
            category : 'monitorTmplGroup',
            param : {
                filter_monitorPointCategoryId : emcProjScheme.selectTmplPage.param.monitorPointCategoryId,
                filter_activatedTmpl : 1,
            },
            firstBlank : false,
            onChange : function(field, value) {
                Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame').setLeftData([]);
                var _form = this;
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/monitor-point-categories/tmpls/queries/selectable';
                if (!Gikam.isEmpty(value)) {

                    if(Gikam.isEmpty(emcProjScheme.selectTmplPage.param.editMonitorInfo)){
                        url += Gikam.param({
                            monitorPointCategoryId : emcProjScheme.selectTmplPage.param.monitorPointCategoryId,
                            ACTIVATEDFLAG_SEQ : '1',
                            orgId: workspace.user.ext$.parentorgid
                        });
                    }else{
                        url += Gikam.param({
                            monitorTmplGroupId : value,
                            ACTIVATEDFLAG_SEQ : '1',
                            orgId: workspace.user.ext$.parentorgid
                        });
                    }

                    Gikam.getJson(url).done(function(items) {
                        _form.setSelectOptions('monitorTmplId_SEQ', items.map(function(item) {
                            return {
                                text : item.ext$.monitortmplname,
                                value : item.monitorTmplId
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                monitorTmplId_SEQ : items[0].monitorTmplId
                            });
                        } else {
                            _form.setData({
                                monitorTmplId_SEQ : "",
                                monitorSchemeId_SEQ : ""
                            });
                        }
                    });
                } else {
                    _form.setSelectOptions('monitorTmplId_SEQ', []);
                    _form.setSelectOptions('monitorSchemeId_SEQ', []);
                }
            },
            // onDataChange : function(field, value, index, row, text) {
            //     Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame').setLeftData([]);
            //     var _form = this;
            //     var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/monitor-point-categories/tmpls/queries/selectable';
            //     if (!Gikam.isEmpty(value)) {
            //
            //         if(Gikam.isEmpty(emcProjScheme.selectTmplPage.param.editMonitorInfo)){
            //             url += Gikam.param({
            //                 monitorPointCategoryId : emcProjScheme.selectTmplPage.param.monitorPointCategoryId,
            //                 ACTIVATEDFLAG_SEQ : '1',
            //             });
            //         }else{
            //             url += Gikam.param({
            //                 monitorTmplGroupId : value,
            //                 ACTIVATEDFLAG_SEQ : '1',
            //             });
            //         }
            //
            //         Gikam.getJson(url).done(function(items) {
            //             _form.setSelectOptions('monitorTmplId_SEQ', items.map(function(item) {
            //                 return {
            //                     text : item.ext$.monitortmplname,
            //                     value : item.monitorTmplId
            //                 };
            //             }), true);
            //             if (items.length > 0) {
            //                 _form.setData({
            //                     monitorTmplId_SEQ : items[0].monitorTmplId
            //                 });
            //             } else {
            //                 _form.setData({
            //                     monitorTmplId_SEQ : "",
            //                     monitorSchemeId_SEQ : ""
            //                 });
            //             }
            //         });
            //     } else {
            //         _form.setSelectOptions('monitorTmplId_SEQ', []);
            //         _form.setSelectOptions('monitorSchemeId_SEQ', []);
            //     }
            // }
        }, {
            title : 'T_EMC_MPC_TMPL.MONITORTMPLNAME',
            field : 'monitorTmplId_SEQ',
            type : 'select',
            firstBlank : false,
            // onChange : function(field, value) {
            //     var _form = this;
            //     var monitorTmplId = _form.getData().monitorTmplId_SEQ;
            //     var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/selectable';
            //     if (!Gikam.isEmpty(value)) {
            //         url += Gikam.param({
            //             monitorTmplId : value,
            //             enableflag :'1'
            //         });
            //         Gikam.getJson(url).done(function(items) {
            //             _form.setSelectOptions('monitorSchemeId_SEQ', items.map(function(item) {
            //                 return {
            //                     text : item.schemeName,
            //                     value : item.schemeName
            //                 };
            //             }), true);
            //             if (_form.getData().showFlag === '1') {
            //                 var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
            //                 leftGrid.setOptions({
            //                     url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
            //                     requestData : {
            //                         schemeName_SEQ : "default",
            //                         monitorTmplId_SEQ : value,
            //                         activatedflag_SEQ : '1',
            //                         testmethodflag_SEQ :'1'
            //                     }
            //                 });
            //                 leftGrid.refresh();
            //             } else {
            //                 var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
            //                 leftGrid.setOptions({
            //                     url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
            //                     requestData : {
            //                         schemeName_SEQ : "default",
            //                         monitorTmplId_SEQ : value,
            //                         defaultFlag_SEQ : '1',
            //                         activatedflag_SEQ : '1',
            //                         testmethodflag_SEQ :'1'
            //                     }
            //                 });
            //                 leftGrid.refresh();
            //             }
            //             if (items.length > 0) {
            //                 _form.setData({
            //                     monitorSchemeId_SEQ : "default"
            //                 });
            //             } else {
            //                 _form.setData({
            //                     monitorSchemeId_SEQ : ""
            //                 });
            //             }
            //         });
            //     }
            // },
            onDataChange : function(field, value, index, row, text) {
                var _form = this;
                var monitorTmplId = _form.getData().monitorTmplId_SEQ;
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/selectable';
                if (!Gikam.isEmpty(value)) {
                    url += Gikam.param({
                        monitorTmplId : value,
                        enableflag :'1'
                    });
                    Gikam.getJson(url).done(function(items) {
                        _form.setSelectOptions('monitorSchemeId_SEQ', items.map(function(item) {
                            return {
                                text : item.schemeName,
                                value : item.schemeName
                            };
                        }), true);
                        if (_form.getData().showFlag === '1') {
                            var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
                            leftGrid.setOptions({
                                url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
                                requestData : {
                                    schemeName_SEQ : "default",
                                    monitorTmplId_SEQ : value,
                                    activatedflag_SEQ : '1',
                                    testmethodflag_SEQ :'1'
                                }
                            });
                            leftGrid.refresh();
                        } else {
                            var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
                            leftGrid.setOptions({
                                url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
                                requestData : {
                                    schemeName_SEQ : "default",
                                    monitorTmplId_SEQ : value,
                                    defaultFlag_SEQ : '1',
                                    activatedflag_SEQ : '1',
                                    testmethodflag_SEQ :'1'
                                }
                            });
                            leftGrid.refresh();
                        }
                        if (items.length > 0) {
                            _form.setData({
                                monitorSchemeId_SEQ : "default"
                            });
                        } else {
                            _form.setData({
                                monitorSchemeId_SEQ : ""
                            });
                        }
                    });
                }
            }
        }, {
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
            field : 'monitorSchemeId_SEQ',
            type : 'select',
            firstBlank : false,
            onChange : function() {
                var _form = this;
                var schemeName = _form.getData().monitorSchemeId_SEQ;
                var monitorTmplId = _form.getData().monitorTmplId_SEQ;
                if (_form.getData().showFlag === '1') {
                    var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
                    leftGrid.setOptions({
                        url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
                        requestData : {
                            schemeName_SEQ : schemeName,
                            monitorTmplId_SEQ : monitorTmplId,
                            defaultFlag_SEQ : '',
                            activatedflag_SEQ : '1',
                        }
                    });
                    leftGrid.refresh();
                } else {
                    var leftGrid = emcProjScheme.getShuttleFrameLeftGrid();
                    leftGrid.setOptions({
                        url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
                        requestData : {
                            schemeName_SEQ : schemeName,
                            monitorTmplId_SEQ : monitorTmplId,
                            defaultFlag_SEQ : '1',
                            activatedflag_SEQ : '1',
                        }
                    });
                    leftGrid.refresh();
                }
            },
            /*onDataChange : function(field, value, index, row, text) {
                var _form = this;
                var schemeName = _form.getData().monitorSchemeId_SEQ;
                var monitorTmplId = _form.getData().monitorTmplId_SEQ;
                if (_form.getData().showFlag === '1') {
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable', Gikam.getJsonWrapper({
                        f : {
                            schemeName_SEQ : schemeName,
                            monitorTmplId_SEQ : monitorTmplId,
                            activatedflag_SEQ : '1',
                        },
                        n : -1
                    })).done(function(row) {
                        emcProjScheme.setShuttleFrameData(row.rows);
                    });
                } else {
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable', Gikam.getJsonWrapper({
                        f : {
                            schemeName_SEQ : schemeName,
                            monitorTmplId_SEQ : monitorTmplId,
                            defaultFlag_SEQ : '1',
                            activatedflag_SEQ : '1',
                        },
                        n : -1
                    })).done(function(row) {
                        emcProjScheme.setShuttleFrameData(row.rows);
                    });
                }
            }*/
        } ];
    },

    setShuttleFrameData : function(leftData) {
        emcProjScheme.selectTmplPage.setShuttleFrameData(leftData);
    },
    getShuttleFrameLeftGrid : function() {
       return  emcProjScheme.selectTmplPage.shuttleFrame.leftGrid;
    },
    getMonitorTmplListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testclass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        },{
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            }
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME',
            width : 100
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        } , {
            field : 'ext$.methodscope',
            title : '方法范围',
        },{
            field : 'ext$.trialscope',
            title : '适用范围',
        },{
            field : 'ext$.authentication',
            title : 'CMA认证',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'ext$.approval',
            title : 'CNAS认可',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }];
    },

    getOrderEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortNum',
            title : 'T_EMC_PROJ_ORDER.SORTNUM',
        }, {
            field : 'sampleNum',
            title : 'T_EMC_PROJ_ORDER.SAMPLENUM',
            range : '[1,100]',
            isInteger : true,
            width : 85,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'schemeName',
            title : 'T_EMC_PROJ_ORDER.SCHEMENAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODID',
            type : 'choose',
            category : 'monitorSchemeMethod',
            cleanable : false,
            single : true,
            onBeforeChoose : function(index) {
                var activedRow = Gikam.getComp('emc-proj-order-edit-list-grid').getActivedRow();
                var schemeActivedRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                return {
                    monitorTmplId_SEQ : schemeActivedRow.monitorTmplId,
                    schemeName_SEQ : activedRow.schemeName,
                    testId_SEQ : activedRow.testId,
                    itemGroup_SEQ : activedRow.itemGroup
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        methodId : rows[0].methodId,
                        ext$ : {
                            testmethodbasis : rows[0].ext$.testmethodbasis,
                            methodname : rows[0].ext$.methodname
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    methodId : '',
                    ext$ : {
                        testmethodbasis : '',
                        methodname : ''
                    }
                })
            },
            editor : true
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width : 130
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'ext$.stdname',
            title : 'T_EMC_PROJ_ORDER.INDEXCATEGORYID'
        }, {
            field : 'ext$.indexname',
            title : 'T_EMC_PROJ_ORDER.INDEXNAME',
            type : 'choose',
            category : 'indexCategory',
            single : true,
            cleanable : false,
            onBeforeChoose : function() {
                return {
                    proj_order_assigned : '1'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        indexId : rows[0].id,
                        indexCategoryId : rows[0].indexCategoryId,
                        ext$ : {
                            stdname : rows[0].ext$.stdname,
                            indexname : rows[0].indexName,
                            index1 : rows[0].index1,
                            index2 : rows[0].index2,
                            index3 : rows[0].index3
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    indexId : '',
                    indexCategoryId : '',
                    ext$ : {
                        stdname : '',
                        indexname : '',
                        index1 : '',
                        index2 : '',
                        index3 : ''
                    }
                })
            },
            editor : true
        }, {
            field : 'indexId',
            title : 'T_EMC_PROJ_ORDER.INDEXID'
        }, {
            field : 'ext$.index1',
            title : 'T_EMC_PROJ_ORDER.INDEXID1'
        }, {
            field : 'ext$.index2',
            title : 'T_EMC_PROJ_ORDER.INDEXID2'
        }, {
            field : 'ext$.index3',
            title : 'T_EMC_PROJ_ORDER.INDEXID3'
        }, {
            field : 'sampleFee',
            title : 'T_EMC_PROJ_ORDER.SAMPLEFEE',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'analysisFee',
            title : 'T_EMC_PROJ_ORDER.ANALYSISFEE',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_PROJ_ORDER.SUBPACKAGENO'
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_PROJ_ORDER.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-proj-order-edit-list-grid');
                var activedRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(value)) {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        projSchemeId : grid.getData()[rowIndex].projSchemeId,
                        subcontractorId : value
                    } ];
                    Gikam.put('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                } else {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        projSchemeId : grid.getData()[rowIndex].projSchemeId,
                        subcontractorId : '',
                        subPackageNo : ''
                    } ];
                    Gikam.put(emcProject.baseUrl + '/' + activedRow.projId, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                }
            }
        }, {
            field : 'dataSharing',
            title : 'T_EMC_PROJ_ORDER.DATASHARING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        },];
    },

    getCopyShareInfoFormFields : function() {
        return [ {
            field : 'copyShare',
            title : 'T_EMC_PROJ_SCHEME.COPYSHARE',
            validators : [ 'integer' ]
        } ];
    },

    getUpdateCycleFrequencyInfoFormFields : function() {
        return [ {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            validators : [ 'integer' ]
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
            validators : [ 'integer' ]
        } ];
    },

    getBatchUpdateInfoFormFields : function() {
        return [ {
            field : 'sampleNum',
            title : 'T_EMC_PROJ_ORDER.SAMPLENUM',
            isInteger : true,
            range : '[1,]',
            hasArrow : true,
            type : 'number'
        } ];
    },

    getOrderUpdateIndexListColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'sampleNum',
            title : 'T_EMC_PROJ_ORDER.SAMPLENUM'
        }, {
            field : 'ext$.stdname',
            title : 'T_EMC_PROJ_ORDER.INDEXCATEGORYID'
        }, {
            field : 'ext$.indexname',
            title : 'T_EMC_PROJ_ORDER.INDEXNAME',
            type : 'choose',
            category : 'indexCategory',
            onBeforeChoose : function() {
                return {
                    monitorTmplGroupId_SEQ : Gikam.getComp('emc-proj-scheme-edit-list-grid').getSelections()[0].ext$.monitortmplgroupid
                }
            },
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        indexId : rows[0].id,
                        indexCategoryId : rows[0].indexCategoryId,
                        ext$ : {
                            stdname : rows[0].ext$.stdname,
                            indexname : rows[0].indexName,
                            index1 : rows[0].index1,
                            index2 : rows[0].index2,
                            index3 : rows[0].index3
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    indexId : '',
                    indexCategoryId : '',
                    ext$ : {
                        stdname : '',
                        indexname : '',
                        index1 : '',
                        index2 : '',
                        index3 : ''
                    }
                })
            },
            editor : true
        }, {
            field : 'indexId',
            title : 'T_EMC_PROJ_ORDER.INDEXID'
        }, {
            field : 'ext$.index1',
            title : 'T_EMC_PROJ_ORDER.INDEXID1'
        }, {
            field : 'ext$.index2',
            title : 'T_EMC_PROJ_ORDER.INDEXID2'
        }, {
            field : 'ext$.index3',
            title : 'T_EMC_PROJ_ORDER.INDEXID3'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'schemeName',
            title : 'T_EMC_PROJ_ORDER.SCHEMENAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODID'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'sampleFee',
            title : 'T_EMC_PROJ_ORDER.SAMPLEFEE'
        }, {
            field : 'analysisFee',
            title : 'T_EMC_PROJ_ORDER.ANALYSISFEE'
        } ];
    },

    // 点位信息-按测试更换指标-指标分析项表
    getUpdateIndexIndexSetColumns : function() {
        return [ {
            field : 'evalIndex',
            title : 'T_EMC_INDEX_SET.EVALINDEX',
            preInsert : true,
            validators : [ 'strLength[0,10]' ],
            editor : true,
        }, {
            field : 'ext$.indexitemname',
            title : 'T_EMC_INDEX_SET.INDEXITEMID',
            type : 'choose',
            category : 'indexItem',
            single : true,
            editor : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        indexItemId : rows[0].id,
                        ext$ : {
                            indexitemname : rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    indexItemId : '',
                    ext$ : {
                        indexitemname : '',
                    }
                });
            },
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_INDEX_SET.INDEXLOWLIMIT',
            validators : [ 'strLength[0,30]' ],
            editor : true,
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_INDEX_SET.INDEXHIGHLIMIT',
            validators : [ 'strLength[0,30]' ],
            editor : true,
        }, ];
    },

    // 点位信息-按测试更换指标-指标表
    getUpdateIndexOrderIndexColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'id',
            title : 'T_EMC_ORDER_INDEX.ID'
        }, {
            field : 'indexName',
            title : 'T_EMC_ORDER_INDEX.INDEXNAME'
        }, {
            field : 'index1',
            title : 'T_EMC_ORDER_INDEX.INDEX1'
        }, {
            field : 'index2',
            title : 'T_EMC_ORDER_INDEX.INDEX2'
        }, {
            field : 'index3',
            title : 'T_EMC_ORDER_INDEX.INDEX3'
        }, {
            field : 'reportDisplayName',
            title : 'T_EMC_ORDER_INDEX.REPORTDISPLAYNAME'
        } ];
    },

    getUpdateIndexSchemeListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
        }, {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
        }, {
            field : 'sampleOrgId',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            type : 'select',
            search : true,
            category : 'org',
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
        } ];
    },

    getUpdateIndexOrderColumns : function() {
        return [ {
            checkbox : true
        },{
            index : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'schemeName',
            title : 'T_EMC_PROJ_ORDER.SCHEMENAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'ext$.stdname',
            title : 'T_EMC_PROJ_ORDER.INDEXCATEGORYID'
        }, {
            field : 'ext$.indexname',
            title : 'T_EMC_PROJ_ORDER.INDEXNAME',
        }, {
            field : 'indexId',
            title : 'T_EMC_PROJ_ORDER.INDEXID'
        }, {
            field : 'ext$.index1',
            title : 'T_EMC_PROJ_ORDER.INDEXID1'
        }, {
            field : 'ext$.index2',
            title : 'T_EMC_PROJ_ORDER.INDEXID2'
        }, {
            field : 'ext$.index3',
            title : 'T_EMC_PROJ_ORDER.INDEXID3'
        }, ];
    },

    getIndexShowAllField: function () {
        return [{
            title: '未选择标准',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '0',
            refreshGrid : false,
            onChange: function (field, value, index) {
                var grid = Gikam.getComp('emc-update-index-proj-order-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            indexId_ISNULL: '1',
                        }
                    }).done(function () {
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            indexId_ISNULL: '',
                        }
                    }).done(function () {
                    });
                }
            },
        }]
    },

});
