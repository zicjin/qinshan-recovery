emcMonitorTmpl.editPage = {

    getTmplGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-tmpl-edit-list-grid',
            url : emcMonitorTmpl.baseUrl + '/queries/raw',
            columns : emcMonitorTmpl.getEditListColumns(),
            service : 'emcMonitorTmplServiceImpl',
            requestData : {
                orgId_SB :  workspace.user.ext$.parentorgid,
            },
            order : {
                orderNo : 'asc'
            },
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.monitorTmplName || row.id;
            },
            activateFormatter : function(row) {
                return row.monitorTmplName || row.id;
            },
            deactivateFormatter : function(row) {
                return row.monitorTmplName || row.id;
            },
            onRendered : function() {
                var grid = Gikam.getComp('emc-monitor-tmpl-edit-list-grid');
            	console.log(workspace.user.isAdmin);
                if(!workspace.user.isAdmin){
                	console.log('true');
                	 Gikam.getComp('emc-monitor-tmpl-copy-all').hide();
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls' + '/{id}/costs/queries', {
                    id : row.id
                })
                Gikam.getAsyncComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').done(function (grid) {
                    grid.refresh({
                        url : url
                    });
                })
                _this.onTmplActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-monitor-tmpl-edit-list-grid').insert(emcMonitorTmpl.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copyTmpl();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                hidden : true,
                onClick : function() {
                    Gikam.getComp('emc-monitor-tmpl-edit-list-grid').deleteRows(emcMonitorTmpl.baseUrl);
                }
            }, {
                type : 'button',
                text : '管理员初始化复制监测模板数据',
                id : 'emc-monitor-tmpl-copy-all',
                icon : 'edit',
                onClick : function () {
                    _this.copyAllMonitorToOrg();
                }
            } ]
        }
    },

    onTmplActive : function(row) {
        var _this = this;
        Gikam.getAsyncComp('emc-monitor-tmpl-edit-list-scheme-grid').done(function (grid) {
            grid.refresh({
                url : _this.getSchemeUrl(row.id),
            });
        })
    },

    getbaseInfoUrl : function(id) {
        if (id) {
            return Gikam.printf(emcMonitorTmpl.baseUrl + '/{id}', {
                id : id
            })
        }
    },
    getSchemeUrl : function(tmplId) {
        if (tmplId) {
            return Gikam.printf(emcMonitorTmpl.baseUrl + '/{id}/schemes/queries', {
                id : tmplId
            })
        }
    },
    getItemGroupUrl : function(schemeId) {
        if (schemeId) {
            return Gikam.printf(emcMonitorTmpl.baseUrl + '/schemes/{id}/item-groups/queries', {
                id : schemeId
            })
        }
    },

    copyTmpl : function() {
        var selections = Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_ONE_COPE');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CONFIRM_COPY_TMPL', function() {
            var url = emcMonitorTmpl.baseUrl + '/action/copy';
            Gikam.getComp('emc-monitor-tmpl-edit-list-grid').insert(url, selections);
        });
    },

    getSchemeGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TAB.SCHEME',
                items : [ {
                    type : 'grid',
                    id : 'emc-monitor-tmpl-edit-list-scheme-grid',
                    url : _this.getSchemeUrl(),
                    columns : emcMonitorTmpl.getSchemeListColumns(),
                    page : true,
                    group : {
                        fields : [ 'schemeName' ],
                        formatter : function(row) {
                            return row.schemeName;
                        },
                        expand : true
                    },
                    rowStyleFormatter : function(row) {
                        if (row.ext$.testmethodflag !='1'){
                                return 'background-color : #f0f019';
                        }
                    },
                    service : 'emcMonitorSchemeServiceImpl',
                    checkOnActive : true,
                    order : {
                        costSortNum : 'asc',
                        schemeName : 'asc',
                        testmethodflag :'asc',
                        enableflag :'asc',
                    },
                    deleteFormatter : function(row) {
                        return row.schemeName || row.id;
                    },
                    onCellClick : function(field, row) {
                        field === 'schemeName' && _this.loadSchemeDetailPage(row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addScheme();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.removeScheme();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copySchemeTo();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInput();
                        }
                    }, {
                        type : 'button',
                        text : '停用方案',
                        icon : 'add',
                        onClick : function() {
                            _this.disableSch();
                        }
                    }, {
                        type : 'button',
                        text : '启用方案',
                        icon : 'add',
                        onClick : function() {
                            _this.enableSch();
                        }
                    }, {
                        type : 'button',
                        text : '同步单项SKU',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncSingleSku();
                        }
                    }, {
                        type : 'button',
                        text : '同步费用信息到',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncPriceInfoTo();
                        }
                    } ]
                } ]
            } ]
        }
    },

    addScheme : function() {
        var schemeData = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getData();
        var activeRow = Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getActivedRow();

        var schemeName = "";

        if (schemeData.length === 0) {
            schemeName = 'default';
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.MODAL.MONITOR_SCHEME',
            url : emcMonitorTmpl.baseUrl + '/page/scheme-addition' + Gikam.param({
                schemeName : schemeName,
                monitorTmplId : activeRow.id,
                orgId :  workspace.user.ext$.parentorgid,
                schemeType : 'testCapacity',
            }),
            onAfterClose : function(scheme) {
                if (Gikam.isNotEmpty(scheme) && scheme !== undefined) {
                    // 自动生成监测费用记录
                    if (scheme.schemeName !== 'default') {
                        Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls' + '/costs', Gikam.getJsonWrapper({}, ['', [scheme]])).done(function () {
                            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').refresh();
                        });
                    }
                }
                Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
            }
        });
    },

    copySchemeTo : function() {
        let selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let activedMonitorTmpl = Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getActivedRow();

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('monitorTmpl').title,
            url : Gikam.choose.getConfig('monitorTmpl').url + Gikam.param({
                id_DIFFER : activedMonitorTmpl.id,
                orgId_SB : workspace.user.ext$.parentorgid,
                monitorTmplGroupId_SEQ : activedMonitorTmpl.monitorTmplGroupId
            }),
            width : Gikam.choose.getConfig('monitorTmpl').width,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcMonitorTmpl.baseUrl + '/schemes/action/copy-to', Gikam.getJsonWrapper({}, [ 'emcMonitorTmplServiceImpl', rows ], [ 'emcMonitorSchemeServiceImpl', selections ])).always(function(str) {
                    workspace.window.closeMask();
                });
            }
        });
    },

    loadSchemeDetailPage : function(row) {
        var activeRow = Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getActivedRow(),id = row.id,schemeName = row.schemeName;

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.MODAL.MONITOR_SCHEME',
            url : emcMonitorTmpl.baseUrl + '/page/scheme-detail' + Gikam.param({
                monitorTmplId : activeRow.id,
                schemeName : encodeURIComponent(schemeName),
                monitorSchemeId : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
            }
        });

    },

    loadSamplingCostPage : function(monitorSchemeId) {
        var monitorTmpl = Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getActivedRow();
        if (!monitorTmpl) {
            return;
        }

        console.info(monitorTmpl, monitorSchemeId)

        Gikam.create('modal', {
            title : '采样费用',
            url : emcMonitorTmpl.baseUrl + '/page/sampling-cost' + Gikam.param({
                monitorSchemeId_SEQ : monitorSchemeId,
                monitorTmplId : monitorTmpl.id
            })
        });

    },

    removeScheme : function() {
        var data = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getData();
        var selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        var dataNotOnlyDefault = false;
        var selectionsHavingDefault = false;
        data.some(function(item) {
            if (!(item.schemeName === 'default')) {
                dataNotOnlyDefault = true;
                return true;
            }
        });
        selections.some(function(item) {
            if (item.schemeName === 'default') {
                selectionsHavingDefault = true;
                return true;
            }
        });
/*        if (dataNotOnlyDefault && selectionsHavingDefault) {
            Gikam.alert("EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.DELETE_OTHER_SCHEME");
            return;
        }*/
        url = Gikam.printf(emcMonitorTmpl.baseUrl + '/{id}/schemes', {
            id : Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getActivedRow().id
        });
        Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').deleteRows(url).done(function () {
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').refresh();
        });
    },

    syncSingleSku : function() {
        let selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let validSelections = selections.filter(item => item.schemeName === 'default');
        if (Gikam.isEmpty(validSelections)) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_DEFAULT_SCHEME');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/erp/sku/action/push', Gikam.getJsonWrapper({}, [ '', selections ] )).done(function() {
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    syncPriceInfoTo : function() {
        let selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        let validSelections = selections.filter(item => item.schemeName === 'default');
        if (Gikam.isEmpty(validSelections)) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_DEFAULT_SCHEME');
            return;
        }

        let activedMonitorTmpl = Gikam.getComp('emc-monitor-tmpl-edit-list-grid').getActivedRow();

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('monitorTmpl').title,
            url : Gikam.choose.getConfig('monitorTmpl').url + Gikam.param({
                id_DIFFER : activedMonitorTmpl.id,
                orgId_SB : workspace.user.ext$.parentorgid
            }),
            width : Gikam.choose.getConfig('monitorTmpl').width,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(emcMonitorTmpl.baseUrl + '/schemes/action/sync-price-info-to', Gikam.getJsonWrapper({}, [ 'emcMonitorTmplServiceImpl', rows ], [ 'emcMonitorSchemeServiceImpl', selections ])).always(function(str) {
                    workspace.window.closeMask();
                });
            }
        });
    },

    getSchemeCostGridParam : function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TAB.SCHEME_COST',
                items: [{
                    type : 'grid',
                    id : 'emc-monitor-tmpl-edit-list-scheme-cost-grid',
                    url : '',
                    service : 'emcMonitorSchemeCostServiceImpl',
                    columns : emcMonitorTmpl.getSchemeCostEditListColumns(),
                    order : {
                        sortNum : 'asc',
                        schemeName : 'asc'
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'T_EMC_MONITOR_TMPL.ORDERCATEGORYCODE.BUTTON.EDIT_SCHEMENAME',
                        icon : 'edit',
                        onClick : function() {
                            _this.editSchemeName();
                        }
                    }, {
                        type : 'button',
                        text : '同步套餐SKU',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncPackageSku();
                        }
                    }]
                }]
            }]
        }
    },

    syncPackageSku : function() {
        let selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/erp/sku/action/push-package', Gikam.getJsonWrapper({}, [ '', selections ] )).done(function() {
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 批录入
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        };
        var row =selections[0];
        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.ext$.defaultflag = formData.ext$.defaultflag;
                                    item.period = formData.period;
                                    item.techPrice = formData.techPrice;
                                    item.salePrice = formData.salePrice;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/' + row.id, Gikam.getJsonWrapper({}, [ 'emcMonitorSchemeServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcMonitorTmpl.getBatchInputFields(),
            autoSave: false
        });

    },


     enableSch : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        };
        //把选择的监测方案的enableflag变成1
        Gikam.postText(emcMonitorTmpl.baseUrl + '/schemes/action/enable', Gikam.getJsonWrapper({}, ['EmcMonitorSchemeService',selections])).done(function() {
        	Gikam.alert("启用成功");
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').refresh();
        }).always(function () {
        });
     },

     disableSch : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        };
        //把选择的监测方案的enableflag变成1
        Gikam.postText(emcMonitorTmpl.baseUrl + '/schemes/action/disable', Gikam.getJsonWrapper({}, ['EmcMonitorSchemeService',selections])).done(function() {
        	Gikam.alert("停用成功");
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
            Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').refresh();
        }).always(function () {
        });
    },


    copyAllMonitorToOrg : function() {
    	//选择分公司
        Gikam.create('modal', {
            title : '选择复制到的公司',
            url : '/secure/core/module/mdm/orgs/page/choose-list'+ Gikam.param({
            	parentId_SEQ : '05',
            	orgcategory_SEQ : 'company',
            }),
            onAfterClose : function(data) {
            	//将三个表的数据复制以监测能力库为主表
            	console.log(data);
            	console.log(data[0].id);
                Gikam.postText(emcMonitorTmpl.baseUrl+'/copy-all-monitor-to-org', Gikam.getJsonWrapper(
                		{
                            f : {
                                orgId : data[0].id
                            }
                        }, [])).done(function(data) {
                        	alert("生成完成");
                    Gikam.getComp('emc-monitor-tmpl-edit-list-grid').refresh();
                }).always(function() {});
                Gikam.getComp('emc-monitor-tmpl-edit-list-grid').refresh();

            },
            height: '95%'
        })
    },

    editSchemeName: function() {
        var _this = this;
        var selections = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').getSelections();
        if(selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_MAX_ITEM_IS_ONE');
            return;
        }
        var row =selections[0];
        var modal = Gikam.create('modal', {
            title: "EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.EDIT_SCHEMENAME",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                                var formData = form.getData();
                                console.log(formData.schemeName);
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/' + 'schemes/action/edit-schemes-name', Gikam.getJsonWrapper({
                                    f : {
                                    	schemeName: formData.schemeName
                                    }
                                }, [ 'emcMonitorSchemeCostServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-grid').refresh();
                                    Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });

                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcMonitorTmpl.getSchemeNameFields(),
            autoSave: false,
            onRendered : function() {
                var rows = Gikam.getComp('emc-monitor-tmpl-edit-list-scheme-cost-grid').getSelections();
                this.setData({schemeName : rows[0].schemeName},false);
            }


        });

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-monitor-tmpl-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '240',
                items : [ this.getTmplGridParam() ]
            },
            center : {
                items : [{
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ this.getSchemeGridParam() ]
                    },
                    center : {
                        items : [ this.getSchemeCostGridParam() ]
                    }
                }]
            }
        });
    },

    init : function() {
        this.create();
    }
};
