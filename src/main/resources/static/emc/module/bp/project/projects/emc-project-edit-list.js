emcProject.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-edit-list-grid',
            url : emcProject.baseUrl + '/queries/raw',
            columns : emcProject.getEditListColumns(),
            service : 'emcProjectServiceImpl',
            page : true,
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            requestData : {
                targetType : 'projEdit',
                orgId_SB: workspace.user.ext$.parentorgid
            },
            onBeforeRefresh : function (requestData) {
                var orgName = workspace.user.orgName;
                if(orgName.indexOf("质量部") >-1){
                    requestData['projNo_SE'] = 'Z';
                }else{
                    requestData['projNo_DIFFER'] = 'Z';
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([
                        "emc-project-edit-list-base-info-form",
                        "emc-project-edit-list-order-grid",
                        "emc-project-edit-list-order-item-group-grid",
                        "emc-project-edit-list-director-grid",
                        "emc-schemeedit-audit-list-grid",
                        "emc-proj-order-edit-list-grid",
                        'emc-project-report-send-info-grid']);
                    Gikam.getComp('emc-project-edit-list-base-info-tab').removePanel('emc-project-edit-list-order-panel');
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                //hidden : true,
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.PROJECT_ADD',
                        height : 320,
                        fields : emcProject.getPreInsertFields(),
                        url : emcProject.baseUrl,
                        onFormRendered : function(form) {
                            setTimeout(() => {
                                this.options.id = 'emc-project-edit-list-preinsert-form';
                                this.hideFields([ 'sample' ]);
                                var date = new Date();
                                var time=date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();//表示当前日期
                                this.setData({taskDate : time},false);
                            }, 10)
                        }
                    }).done(function(id) {
                        Gikam.getComp('emc-project-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('rasproject', Gikam.getComp('emc-project-edit-list-grid').getSelections());
                }
            }, {
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.MORE',
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.SUBMITTOREPORT',
                    icon : 'submit',
                    hidden : true,
                    onClick : function() {
                        _this.submitToReport();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.MODIFY_PROJECT_NO',
                    icon : 'edit',
                    hidden : true,
                    onClick : function() {
                        _this.modifyProjectNo();
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPY',
                    icon : 'copy',
                    onClick : function() {
                        Gikam.create('modal', {
                            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.COPY_PROJECT',
                            url : emcProject.baseUrl + '/page/copy-project',
                            onAfterClose : function(script) {
                                Gikam.getComp('emc-project-edit-list-grid').refresh();
                            }
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('emc-project-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/project/projects');
                        /*var selections =  Gikam.getComp('emc-project-edit-list-grid').getSelections();
                        if (Gikam.isEmpty(selections)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        //获取是否可以删除的标识
                        Gikam.postText('/secure/emc/module/bp/project/projects/action/have-write-off', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', selections ])).done(function(data) {
                            console.log(data);
                            if(data == ''){
                                Gikam.getComp('emc-project-edit-list-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/project/projects');
                            }else{
                                Gikam.alert(data+'项目在CRM系统发票未作废或到款认领未删除,不允许删除!');
                            }
                            Gikam.getComp('emc-project-edit-list-grid').refresh();
                        });*/
                    }
                } ]
            } ]
        }
    },

    modifyProjectNo : function() {
        let selections = Gikam.getComp('emc-project-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        let modal = Gikam.create('modal', {
            title: "EMC.MODULE.BP.PROJECT.PROJECTS.TITLE.MODIFY_PROJECT_NO",
            width : 400,
            height : 180,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        class : 'blue',
                        onClick : function() {
                            if (form.validate()) {
                                let data = {
                                    id : selections[0].id,
                                    projNo : form.getData().projNo
                                }
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcProject.baseUrl + '/instant', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ data ] ])).done(function() {
                                    Gikam.getComp('emc-project-edit-list-grid').refresh();
                                }).always(function() {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
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
            fields: [
                {
                    field : 'projNo',
                    title : 'T_EMC_PROJECT.PROJNO',
                    validators : [ 'unique[' + emcProject.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,120]' ],
                    preInsert : true,
                }
            ],
            autoSave: false
        });

        form.setData({
            projNo : selections[0].projNo
        })
    },

    copyProject : function() {
        var selections = Gikam.getComp('emc-project-edit-list-grid').getSelections();
        if (selections.length === 1) {
            var url = Gikam.printf(emcProject.baseUrl + '/{id}/action/copy', {
                id : selections[0].id
            });
            Gikam.getComp('emc-project-edit-list-grid').insert(url, selections);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    copeOrder : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_SHARE',
                width : 300,
                height : 200,
                url : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProject.baseUrl + '/orders/actions/copy';
                        Gikam.post(url, Gikam.getJsonWrapper({
                            copies : data.copyShare
                        }, [ '', selections ])).always(function() {
                            workspace.window.closeMask();
                            Gikam.getComp('emc-project-edit-list-order-grid').refresh();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    copeQcOrder : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            var copyEnabled = true;
            selections.some(function(item) {
                if (item.qcCategoryId) {
                    copyEnabled = false;
                    return true;
                }
            });
            if (!copyEnabled) {
                Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.COPYQCORDERTIP');
                return;
            }
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('qcCategory').title,
                width : '20%',
                height : '50%',
                url : Gikam.choose.getConfig('qcCategory').url + Gikam.param({
                    single : true,
                    useCategory_SEQ : 'scene'
                }),
                onAfterClose : function(rows) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    workspace.window.showMask(true);
                    selections.map(function(item) {
                        item.qcCategoryId = rows[0].id;
                        item.qcCategoryCode = rows[0].qcCategoryId;
                        item.qcName = rows[0].qcName;
                    });

                    Gikam.getComp('emc-project-edit-list-order-grid').insert(emcProject.baseUrl + '/orders/actions/copy-qc', selections);
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
        Gikam.getComp('emc-project-edit-list-order-grid').refresh();
    },


    updateQcCategory : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('qcCategory').title,
            width : '20%',
            height : '50%',
            url : Gikam.choose.getConfig('qcCategory').url + Gikam.param({
                single : true,
                useCategory_SEQ : 'scene'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                selections.forEach(function(item) {
                    item.qcCategoryId = rows[0].id;
                    item.qcCategoryCode = rows[0].qcCategoryId;
                    item.qcName = rows[0].qcName;
                });
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-qc', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections])).done(function() {
                    Gikam.getComp('emc-project-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    emptyQcCategory : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        selections.forEach(function(item) {
            item.qcCategoryId = "";
            item.qcCategoryCode = "N/A";
            item.qcName = "N/A";
        });

        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-qc', Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections])).done(function() {
            Gikam.getComp('emc-project-edit-list-order-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 监测方案上移
    projSchemeMoveUp : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-schemeedit-audit-list-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === 0) {
            return;
        }
        // 要上移的数据
        var upData = rows[0];
        // 要下移的数据
        var downData = grid.getData()[index - 1];
        var tempNo = upData.sortNum;
        upData.sortNum = downData.sortNum;
        downData.sortNum = tempNo;
        var url = emcProject.schemeUrl + '/move';
        workspace.window.showMask(true);
        _this.param.index = index - 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcProjSchemeServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 监测方案下移
    projSchemeMoveDown : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-schemeedit-audit-list-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === grid.getData().length - 1) {
            return;
        }
        // 要下移的数据
        var downData = rows[0];
        // 要上移的数据
        var upData = grid.getData()[index + 1];
        var tempNo = upData.sortNum;
        upData.sortNum = downData.sortNum;
        downData.sortNum = tempNo;
        var url = emcProject.schemeUrl + '/move';
        workspace.window.showMask(true);
        _this.param.index = index + 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcProjSchemeServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 监测方案样品上移
    projOrderMoveUp : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-proj-order-edit-list-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === 0) {
            return;
        }
        // 要上移的数据
        var upData = rows[0];
        // 要下移的数据
        var downData = grid.getData()[index - 1];
        var tempNo = upData.sortNum;
        upData.sortNum = downData.sortNum;
        downData.sortNum = tempNo;
        var url = emcProject.schemeUrl + '/move-proj-order';
        workspace.window.showMask(true);
        _this.param.index = index - 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcProjOrderServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 监测方案样品下移
    projOrderMoveDown : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-proj-order-edit-list-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === grid.getData().length - 1) {
            return;
        }
        // 要下移的数据
        var downData = rows[0];
        // 要上移的数据
        var upData = grid.getData()[index + 1];
        var tempNo = upData.sortNum;
        upData.sortNum = downData.sortNum;
        downData.sortNum = tempNo;
        var url = emcProject.schemeUrl + '/move-proj-order';
        workspace.window.showMask(true);
        _this.param.index = index + 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcProjOrderServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 样品列表上移
    orderMoveUp : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-project-edit-list-order-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === 0) {
            return;
        }
        // 要上移的数据
        var upData = rows[0];
        // 要下移的数据
        var downData = grid.getData()[index - 1];
        var tempNo = upData.orderNum;
        upData.orderNum = downData.orderNum;
        downData.orderNum = tempNo;
        var url = emcProject.baseUrl + '/move-order';
        workspace.window.showMask(true);
        _this.param.index = index - 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 样品列表下移
    orderMoveDown : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-project-edit-list-order-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === grid.getData().length - 1) {
            return;
        }
        // 要下移的数据
        var downData = rows[0];
        // 要上移的数据
        var upData = grid.getData()[index + 1];
        var tempNo = upData.orderNum;
        upData.orderNum = downData.orderNum;
        downData.orderNum = tempNo;
        var url = emcProject.baseUrl + '/move-order';
        workspace.window.showMask(true);
        _this.param.index = index + 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },


    // 样品分析项组上移
    orderItemGroupMoveUp : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-project-edit-list-order-item-group-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === 0) {
            return;
        }
        // 要上移的数据
        var upData = rows[0];
        // 要下移的数据
        var downData = grid.getData()[index - 1];
        var tempNo = upData.sortNum;
        upData.sortNum = downData.sortNum;
        downData.sortNum = tempNo;
        var url = emcProject.baseUrl + '/move-order-item-group';
        workspace.window.showMask(true);
        _this.param.index = index - 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderItemGroupServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    // 样品分析项组下移
    orderItemGroupMoveDown : function() {
        var _this = this;
        var grid = Gikam.getComp('emc-project-edit-list-order-item-group-grid');
        var rows = grid.getSelections();
        if (rows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_OPERATE_DATA');
            return;
        }
        var index = grid.getCheckedRowIndex()[0];
        if (index === grid.getData().length - 1) {
            return;
        }
        // 要下移的数据
        var downData = rows[0];
        // 要上移的数据
        var upData = grid.getData()[index + 1];
        var tempNo = upData.sortNum;
        upData.sortNum = downData.sortNum;
        downData.sortNum = tempNo;
        var url = emcProject.baseUrl + '/move-order-item-group';
        workspace.window.showMask(true);
        _this.param.index = index + 1;
        Gikam.put(url, Gikam.getJsonWrapper(null, [ 'emcOrderItemGroupServiceImpl', [ upData, downData] ])).done(function(){
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    updateOrderIndex : function() {
        let selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
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
                Gikam.getComp('emc-project-edit-list-order-grid').refresh();
            }
        });
    },

    editItem : function() {
        var activedRow = Gikam.getComp('emc-project-edit-list-order-grid').getActivedRow();
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length === 1) {

            var orderItemGroupId = selections[0].id;
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.EDIT_ITEM',
                url : '/secure/emc/module/bp/bp/folders/page/order-item-edit-list' + Gikam.param({
                    itemGroupId : selections[0].itemGroupId,
                    orderItemGroupId : orderItemGroupId
                }),
                width : '53%',
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    editTest : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        var orderIds = "";
        selections.forEach(function(item) {
            orderIds += item.id + ',';
        });
        orderIds = orderIds.substring(0, orderIds.length - 1);
        if (selections.length > 0) {
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
                            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
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

    submit : function() {
        var grid = Gikam.getComp('emc-project-edit-list-grid');
        //这里写校验方法，返回错误信息，不同的返回值，处理成不同的报错信息
        var selections = grid.getSelections();

        workspace.window.showMask(true);
        Gikam.postText(emcProject.baseUrl + '/action/submit-before-check', Gikam.getJsonWrapper({}, [ '', selections ])).done(function(data) {
        	if(data != null && data !== ""){
            	Gikam.alert(data);
            	return;
        	}
            var baseForm = Gikam.getComp('emc-project-edit-list-base-info-form');
            if (!baseForm.validate()) {
                return;
            }
            Gikam.create('workflow').submit({
                data : grid.getSelections(),
                pageObject : emcProject
            }).done(function() {
                grid.refresh();
                //let projNos = selections.map(item => item.projNo).join();
                //Gikam.alert(projNos + ' 项目已提交，正在生成业务数据...');
            });
        }).always(function() {
            workspace.window.closeMask();
        });

    },

    submitToReport : function() {
        var selections = Gikam.getComp('emc-project-edit-list-grid').getSelections();
        if (selections.length > 0) {
            Gikam.confirm('GIKAM.MODAL.SUBMIT', 'GIKAM.TIP.SUBMIT', function() {
                workspace.window.showMask(true);
                Gikam.post(emcProject.baseUrl + '/action/submit-to-report', Gikam.getJsonWrapper({}, [ '', selections ])).always(function() {
                    Gikam.getComp('emc-project-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    onProjectActive : function(row) {
        var _this = this;
        var tab = Gikam.getComp('emc-project-edit-list-base-info-tab');
        tab.show();
        if (row) {
            if(_this.param === undefined) {
                _this.param = {};
            }
            _this.param.editMonitorInfo = row.ext$.editmonitorinfo;
            _this.param.sampleOrgId = row.ext$.sampleorgid;
            _this.param.projId = row.id;
            // 这里使用委托单位来过滤数据，之前是受检单位
            _this.param.testOrgId = row.entrustOrgId;

            if (row.sample === "1") {
                tab.removePanel('emc-project-subpackage-info-panel');
                tab.removePanel('emc-project-edit-list-order-panel');
                tab.showPanel('emc-project-edit-list-base-info-panel');
                tab.removePanel('emc-project-sample-subpackage-info-panel');
                tab.revertPanel('emc-project-scheme-edit-panel');
            } else {
                tab.revertPanel('emc-project-edit-list-order-panel');
                tab.removePanel('emc-project-scheme-edit-panel');
                tab.removePanel('emc-project-subpackage-info-panel');
                tab.removePanel('emc-project-sample-subpackage-info-panel');
            }

            Gikam.getAsyncComp('emc-project-edit-list-base-info-form').done(function (form) {
                _this.addOrRemoveValidator(row);

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
                    url : Gikam.printf(emcProject.baseUrl + '/{id}', {
                        id : row.id
                    }),
                }, true);
            });

            if (row.sample === "1") {
                Gikam.getAsyncComp('emc-schemeedit-audit-list-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id
                        }
                    });
                    grid.refreshColumns(_this.param.editMonitorInfo === '1' ? emcProject.getSchemeEditListColumns1() : emcProject.getSchemeEditListColumns());
                    grid.refresh({
                        url : emcProject.schemeUrl + '/queries/raw',
                        order : {
                            'MONITORTMPLID' : 'asc',
                            'FOLDERNO' : 'asc'
                        }
                    });
                });
                Gikam.getAsyncComp('emc-project-edit-list-sample-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid
                        }
                    });
                    grid.refreshColumns(emcProject.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            } else {
                Gikam.getComp('emc-project-edit-list-order-grid').refresh({
                    url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                        id : row.id
                    })
                });
                Gikam.getAsyncComp('emc-project-edit-list-subpackage-info-grid').done(function(grid) {
                    grid.setOptions({
                        requestData : {
                            projId_SEQ : row.id,
                            subpackageType_SEQ : 'create',
                            orgId_SB: workspace.user.ext$.parentorgid
                        }
                    });
                    grid.refreshColumns(emcProject.getProjectSubpackageListColumns());
                    grid.refresh({
                        url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/queries",
                    });
                });
            }


            Gikam.getComp('emc-project-edit-list-uploader').setOptions({
                bizId : row.id,
            });

            // 查询合同附件
            Gikam.getAsyncComp('emc-project-edit-list-contract-uploader').done(function (uploader) {
                uploader.grid.refresh({
                    requestData : {
                        targetId : '',
                        targetId_SEQ : 'T_EMC_CONTRACT$' + row.ext$.contractid
                    },
                    url : uploader.options.searchUrl
                })
            });

            // 查询报告发送信息
            Gikam.getAsyncComp('emc-project-report-send-info-grid').done(function (grid) {
                grid.refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/report/reports/report-send-info/queries',
                    requestData : {
                        projId_SEQ : row.id
                    }
                })
            });

            // 查询其他费用信息
            Gikam.getAsyncComp('emc-project-other-fee-grid').done(function (grid) {
                grid.refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/bp/project/other-fees/queries',
                    requestData : {
                        projId_SEQ : row.id
                    }
                })
            });

            _this._monitorPointType = '';
            _this._sampleOrgId = '';
            _this._sampleOrgName = '';
            _this._selectId.splice(0, _this._selectId.length);

        }
    },

    addOrRemoveValidator : function(project) {
        Gikam.getAsyncComp('emc-project-edit-list-base-info-form').done(function (form) {
            /*if (project.sample === '1') {
                form.addFieldValidator('ext$.samplinglocation', ['notEmpty']);
                form.addFieldValidator('ext$.inspectedcontact', ['notEmpty']);
                form.addFieldValidator('ext$.inspectedphone', ['notEmpty']);
            } else {
                form.removeFieldValidator('ext$.samplinglocation', ['notEmpty']);
                form.removeFieldValidator('ext$.inspectedcontact', ['notEmpty']);
                form.removeFieldValidator('ext$.inspectedphone', ['notEmpty']);
            }*/
        })
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

    loadSchemeItemGroupPage : function(schemeName) {
        var activeRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.ORDER_ADD',
            width : '70%',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/page/scheme-item-group-choose-list',
            onAfterClose : function(rows) {
                if (rows && rows.length > 0) {
                    workspace.window.showMask(true);
                    Gikam.postText(Gikam.printf(emcProject.baseUrl + '/{id}/orders', {
                        id : activeRow.id
                    }), Gikam.getJsonWrapper(null, [ 'emcOrderServiceImpl', [ {
                        monitorTmplId : rows[0].monitorTmplId,
                        orderCategory : rows[0].monitorTmplName
                    } ] ], [ 'emcOrderItemGroupService', rows ])).done(function(id) {
                        Gikam.getComp('emc-project-edit-list-order-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });

                }

            }
        });
    },

    batchUpdateOrder : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 400,
                height : '70%',
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BATCH_UPDATE_ORDER',
                url : emcProject.baseUrl + '/page/batch-update-order',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.sendDate = data.sendDate;
                            item.sendTime = data.sendTime;
                            item.orderDesc = data.orderDesc;
                            item.remark = data.remark;
                            item.volume = data.ext$.volume;
                            item.packing = data.ext$.packing;
                            item.ext$.sender = data.ext$.sender;
                            item.ext$.checker = data.ext$.checker;
                            item.labelNo = data.labelNo;
                            item.clientOrderName = data.clientOrderName;
                            item.ext$.isblank = data.ext$.isblank;

                        });

                        Gikam.getComp('emc-project-edit-list-base-info-form').setData({
                            sendUsername : data.ext$.sender,
                            sendDate : data.sendDate,
                            ext$ : {
                                senduserid : data.senderId
                            }
                        });

                        Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-order-grid').refresh();
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

    updateMethod : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();

        if (selections.length === 1) {
            var activedRow = Gikam.getComp('emc-project-edit-list-order-grid').getActivedRow();
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('testMethod').title,
                url : Gikam.choose.getConfig('monitorSchemeMethod').url + Gikam.param({
                    single : true,
                    monitorTmplId_SEQ : activedRow.monitorTmplId,
                    schemeName_SEQ : selections[0].schemeName,
                    itemGroup_SEQ : selections[0].itemGroup,
                    methodId_DIFFER : selections[0].methodId
                }),
                width : '30%',
                onAfterClose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.schemeName = rows[0].schemeName;
                            item.testName = rows[0].ext$.testname;
                            item.testId = rows[0].testId;
                            item.testMethodId = rows[0].testMethodId;
                            item.methodId = rows[0].methodId;
                            item.methodName = rows[0].ext$.methodname;
                            item.methodVersionId = rows[0].ext$.methodversionid;
                            item.testMethodBasis = rows[0].ext$.testmethodbasis;
                            item.analysisOrgId = rows[0].ext$.analysisorgid;
                            item.analysisOrgName = rows[0].ext$.analysisorgname;
                            item.inputUserId = rows[0].ext$.inputuserid;
                            item.inputUserName = rows[0].ext$.inputusername;
                        });
                        Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/' + selections[0].id + '/action/edit-method', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    updateContainer : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('containerCategory').title,
                url : Gikam.choose.getConfig('containerCategory').url + Gikam.param({
                    single : true,
                }),
                width : '55%',
                onAfterClose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.containerCategoryId = rows[0].id;
                            item.containerCategoryCode = rows[0].containerCategoryId;
                            item.containerCategoryName = rows[0].containerCategoryName;
                        });
                        Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
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

    updateContainerGroup : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            Gikam.create('modal', {
                width : 400,
                height : 250,
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.UPDATE_CONTAINER_GROUP',
                url : emcProject.baseUrl + '/page/update-order-container-group',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.containerGroup = data.containerGroup;
                            item.fixative = data.fixative;
                            item.storeCondition = data.storeCondition;
                        });
                        Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
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
                            onUpdated : function() {
                                var grid = Gikam.getComp('emc-project-edit-list-grid');
                                grid.refreshRowById(grid.getActivedRow().id);
                            },
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
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add-row',
                                        onClick : function() {
                                            _this.insertDirectorRow();
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            _this.deleteDirectorRow();
                                        }
                                    } ],
                                    columns : emcProject.getProjectDirectorListColumns()
                                } ]
                            }, {
                                id : 'emc-project-report-send-info-panel',
                                title : '报告发送信息',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-report-send-info-grid',
                                    filterOpen : false,
                                    service : 'emcReportSendInfoServiceImpl',
                                    page : false,
                                    deleteFormatter : function(row) {
                                        return row.ext$.username || row.empId || row.id;
                                    },
                                    toolbar : [],
                                    columns : emcProject.getReportSendInfoListColumns()
                                } ]
                            }, {
                                id : 'emc-project-other-fee-panel',
                                title : '其他费用信息',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-other-fee-grid',
                                    filterOpen : false,
                                    service : 'emcProjectOtherFeeServiceImpl',
                                    page : false,
                                    deleteFormatter : function(row) {
                                        return row.feeName || row.id;
                                    },
                                    toolbar : [ {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.INSERT',
                                        icon : 'add-row',
                                        onClick : function() {
                                            let project = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
                                            if (Gikam.isEmpty(project)) {
                                                return
                                            }

                                            Gikam.getComp('emc-project-other-fee-grid').insert('/secure/emc/module/bp/project/other-fees', [ {projId : project.id} ]);
                                        }
                                    }, {
                                        type : 'button',
                                        text : 'GIKAM.BUTTON.DELETE',
                                        icon : 'remove-row',
                                        onClick : function() {
                                            let selections = Gikam.getComp('emc-project-other-fee-grid').getSelections();
                                            let validStatus = selections.some(item => item.status === '1');
                                            if (validStatus) {
                                                Gikam.alert('存在不允许删除的费用类型');
                                                return
                                            }

                                            Gikam.getComp('emc-project-other-fee-grid').deleteRows('/secure/emc/module/bp/project/other-fees');
                                        }
                                    } ],
                                    columns : emcProject.getProjectOtherFeeListColumns()
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
                            service : 'emcOrderServiceImpl',
                            page :true,
                            deleteFormatter : function(row) {
                                return row.orderNo || row.id;
                            },
                            order : {
                                'orderNum' : 'asc',
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
                                    Gikam.cleanCompData([ 'emc-project-edit-list-order-item-group-grid' ]);
                                }
                            },
                            onRendered : function() {
                                this.hideColumns([ 'qcCategoryCode' ]);
                            },
                            onRowActive : function(index, row) {
                                _this.onOrderActive(row);
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    _this.loadSchemeItemGroupPage();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    Gikam.getComp('emc-project-edit-list-order-grid').deleteRows(Gikam.printf(emcProject.baseUrl + '/{id}/orders', {
                                        id : Gikam.getComp('emc-project-edit-list-grid').getActivedRow().id
                                    }));
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.PREVIEW',
                                icon : 'print',
                                onClick : function() {
                                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-project-edit-list-order-grid').getSelections());
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ORDER_QUICK_INPUT',
                                items : [ {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.EXPORT',
                                    icon : 'move-up',
                                    onClick : function() {
                                        _this.orderQuickExport();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'GIKAM.BUTTON.IMPORT',
                                    icon : 'move-down',
                                    onClick : function() {
                                        _this.orderQuickImport();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'GIKAM.BUTTON.MORE',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.BATCH_UPDATE',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.batchUpdateOrder();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPE_NORMAL',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.copeOrder();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.COPE_QC',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.copeQcOrder();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_QC_CATEGORY',
                                    icon : 'copy',
                                    onClick : function() {
                                        _this.updateQcCategory();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EMPTY_QC_CATEGORY',
                                    icon : 'copy',
                                    hidden : true,
                                    onClick : function() {
                                        _this.emptyQcCategory();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.INDEX',
                                    icon : 'edit',
                                    onClick : function() {
                                        _this.updateOrderIndex();
                                    }
                                } ]
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.MOVE_UP',
                                icon : 'move-up',
                                onClick : function() {
                                    _this.orderMoveUp();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.MOVE_DOWN',
                                icon : 'move-down',
                                onClick : function() {
                                    _this.orderMoveDown();
                                }
                            }, ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-project-edit-list-order-item-group-grid',// 样品分析项组
                            columns : emcProject.getOrderItemGroupColumns(),
                            service : 'emcOrderItemGroupServiceImpl',
                            order : {
                                'sortNum' : 'asc'
                            },
                            toolbar : [ {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT_TEST',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.editTest();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_METHOD',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.updateMethod();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.EDIT_ITEM',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.editItem();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.CONTAINER',
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_CONTAINER',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.updateContainer();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.UPDATE_CONTAINER_GROUP',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.updateContainerGroup();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.SUBCONTRACTOR',
                                hidden : true,
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ADD_SUBCONTRACTOR',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addOrderSubcontractor();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DEL_SUBCONTRACTOR',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.delOrderSubcontractor();
                                    }
                                } ]
                            }, {
                                type : 'dropDownMenu',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DATASHARING',
                                hidden : true,
                                items : [ {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ADD_DATASHARING',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.addDataSharing();
                                    }
                                }, {
                                    type : 'button',
                                    text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.DEL_DATASHARING',
                                    icon : 'remove-row',
                                    onClick : function() {
                                        _this.delDataSharing();
                                    }
                                } ]
                            }, {
                                type : 'button',
                                text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.MTLSELECT',
                                icon : 'submit',
                                hidden : true,
                                onClick : function() {
                                    _this.mtlSelect();
                                }
                            }, {
                                type : 'dropDownMenu',
                                text : '分包单',
                                hidden : true,
                                items : [ {
                                    type : 'button',
                                    text : '创建分包单',
                                    icon : 'add',
                                    onClick : function() {
                                        _this.createSubpackage();
                                    }
                                }, {
                                    type : 'button',
                                    text : '添加至分包单',
                                    icon : 'edit',
                                    onClick : function() {
                                        _this.addToSubpackage();
                                    }
                                } ]
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.MOVE_UP',
                                icon : 'move-up',
                                onClick : function() {
                                    _this.orderItemGroupMoveUp();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.MOVE_DOWN',
                                icon : 'move-down',
                                onClick : function() {
                                    _this.orderItemGroupMoveDown();
                                }
                            }, ]
                        } ]
                    }
                } ]
            }, _this.getMonitorSchemePanel(), {
                title : 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-edit-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                } ]
            }, {
                title : '合同附件',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-edit-list-contract-uploader',
                    dbTable : 'T_CRM_OPPORTUNITY_CONTRACT',
                    readonly : true,
                } ]
            }, {
                title : '分包单管理',
                id : 'emc-project-subpackage-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-edit-list-subpackage-info-grid',
                                    columns : emcProject.getProjectSubpackageListColumns(),
                                    requestData : {
                                        subpackageType_SEQ : 'create',
                                        orgId_SB: workspace.user.ext$.parentorgid,
                                    },
                                    service : 'emcSubpackageServiceImpl',
                                    toolbar : _this.getSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-subpackage-edit-list-base-info-form' ]);
                                            Gikam.cleanCompData([ 'emc-project-edit-list-subpackage-order-grid' ]);
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onSubpackageActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : '80%',
                                        items : [ {
                                            type : 'form',
                                            columns : 3,
                                            service : 'emcSubpackageServiceImpl',
                                            id : 'emc-subpackage-edit-list-base-info-form',
                                            fields : emcProject.getSubpackageBaseInfoFormFields(),
                                            onUpdated : function() {
                                                var grid = Gikam.getComp('emc-project-edit-list-subpackage-info-grid');
                                                grid.refreshRowById(grid.getActivedRow().id);
                                            },
                                        } ]
                                    },
                                } ]
                            }

                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-project-edit-list-subpackage-order-grid',
                                    columns : emcProject.getSubpackageOrderColumns(),
                                    service : 'emcProjectServiceImpl',
                                    requestData: {
                                        subpackageflag_SEQ : '1'
                                    },
                                    toolbar : _this.getOrderSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-project-edit-list-subpackage-order-task-grid' ]);
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onSubpackageOrderInfoActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-project-edit-list-subpackage-order-task-grid',
                                    columns : emcProject.getSubpackageOrderTaskColumns(),
                                    service : 'emcSubpackageTaskServiceImpl',
                                    toolbar : [{
                                            type : 'button',
                                            text : '删除',
                                            icon : 'remove',
                                            onClick : function() {
                                                var selections = Gikam.getComp('emc-project-edit-list-subpackage-order-task-grid').getSelections();
                                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/delete-package-task', Gikam.getJsonWrapper({}, [ 'emcSubpackageTaskServiceImpl', selections ])).done(function() {
                                                    Gikam.alert('删除成功');
                                                    Gikam.getComp('emc-project-edit-list-subpackage-order-task-grid').refresh();
                                                });
                                            }
                                        }
                                    ]
                                } ]
                            }
                        } ]
                    }
                } ]
            }, {
                title : '分包单管理',
                id : 'emc-project-sample-subpackage-info-panel',
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    id : 'emc-project-edit-list-sample-subpackage-info-grid',
                                    columns : emcProject.getProjectSubpackageListColumns(),
                                    requestData : {
                                        subpackageType_SEQ : 'create',
                                        orgId_SB: workspace.user.ext$.parentorgid,
                                    },
                                    service : 'emcSubpackageServiceImpl',
                                    toolbar : _this.getSampleSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-project-edit-list-subpackage-folder-grid' ]);
                                            Gikam.cleanCompData([ 'emc-subpackage-sample-edit-list-base-info-form' ]);
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onSampleSubpackageActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : '80%',
                                        items : [ {
                                            type : 'form',
                                            columns : 3,
                                            service : 'emcSubpackageServiceImpl',
                                            id : 'emc-subpackage-sample-edit-list-base-info-form',
                                            fields : emcProject.getSubpackageBaseInfoFormFields(),
                                            onUpdated : function() {
                                                var grid = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid');
                                                grid.refreshRowById(grid.getActivedRow().id);
                                            },
                                        } ]
                                    },
                                } ]
                            },
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-project-edit-list-subpackage-folder-grid',
                                    columns : emcProject.getSubpackageFolderColumns(),
                                    service : 'emcProjSchemeServiceImpl',
                                    toolbar : _this.getProjSchemeSubpackageButton(),
                                    onLoadSuccess : function(data) {
                                        if (!Gikam.isEmpty(data)) {
                                            this.activeRowByIndex(0);
                                        } else {
                                            Gikam.cleanCompData([ 'emc-project-edit-list-subpackage-sample-order-task-grid' ]);
                                        }
                                    },
                                    onRowActive : function(index, row) {
                                        _this.onSampleSubpackageFolderInfoActive(row);
                                    },
                                } ]
                            },
                            center : {
                                items : [ {
                                    type : 'grid',
                                    readonly : true,
                                    id : 'emc-project-edit-list-subpackage-sample-order-task-grid',
                                    columns : emcProject.getSubpackageSampleOrderTaskColumns(),
                                    service : 'emcSubpackageTaskServiceImpl',
                                    toolbar : [{
                                        type : 'button',
                                        text : '删除',
                                        icon : 'remove',
                                        onClick : function() {
                                            var selections = Gikam.getComp('emc-project-edit-list-subpackage-sample-order-task-grid').getSelections();
                                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/delete-package-task', Gikam.getJsonWrapper({}, [ 'emcSubpackageTaskServiceImpl', selections ])).done(function() {
                                                Gikam.alert('删除成功');
                                                Gikam.getComp('emc-project-edit-list-subpackage-sample-order-task-grid').refresh();

                                            });
                                        }
                                    }
                                    ]
                                } ]
                            }
                        } ]
                    }
                } ]
            }  ]
        }
    },

    createSubpackage : function() {
        var orders = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (Gikam.isEmpty(selections) || Gikam.isEmpty(orders)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var project = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
        Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcSubpackage.baseUrl + '/action/create', Gikam.getJsonWrapper({}, [ '', [ project ] ], [ '', orders ], [ '', selections ])).done(function() {
                Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    addToSubpackage : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }


    },

    createSamplingSubpackage : function() {
        var schemes = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        var selections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections) || Gikam.isEmpty(schemes)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var project = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
        Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
            workspace.window.showMask(true);
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create', Gikam.getJsonWrapper({}, [ '', [ project ] ], [ '', schemes ], [ '', selections ])).done(function() {
                Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    addToSamplingSubpackage : function() {
        var selections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }


    },

    /*质控标样选择*/
    mtlSelect : function () {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : '60%',
                height : '50%',
                title : Gikam.choose.getConfig('mtlStock').title,
                url : Gikam.choose.getConfig('mtlStock').url + Gikam.param({
                    single : true,
                    projectFlag : '1',
                }),
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        selections.map(function(item) {
                            item.batchNo = data[0].batchNo;
                        });
                        Gikam.put('/secure/emc/module/bp/project/projects/qcMtlSelect', Gikam.getJsonWrapper({}, [ 'pfOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
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

    /** 样品登记设置分包商 */
    addOrderSubcontractor : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : '20%',
                height : '50%',
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
                        Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
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

    /** 样品登记删除分包商 */
    delOrderSubcontractor : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            selections.map(function(item) {
                item.subcontractorId = '';
                item.subPackageNo = '';
            });
            workspace.window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
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
                        columns : emcProject.getSchemeEditListColumns(),
                        service : 'emcProjSchemeServiceImpl',
                        checkOnActive : false,
                        order : {
                            'sortNum' : 'asc',
                        },
                        deleteFormatter : function(row) {
                            return row.folderNo || row.id;
                        },
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
                        toolbar : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.INSERT',
                            icon : 'add',
                            onClick : function() {
                                var projId = _this.param.projId;
                                var activeRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
                                if (Gikam.isEmpty(activeRow)) {
                                    Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_REQUIRED");
                                    return;
                                }

                                var data = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                                var editMonitorInfo = _this.param.editMonitorInfo;
                                // 这里使用委托单位来过滤数据，之前是受检单位
                                var testOrgId = data.entrustOrgId ? data.entrustOrgId : '-';
                                var sampleOrgId = _this.param.sampleOrgId;
                                _this.addProj(projId, editMonitorInfo, testOrgId, sampleOrgId);
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

                                // 这里使用委托单位来过滤数据，之前是受检单位
                                var data = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                                var testOrgId = data.entrustOrgId;
                                _this.initAddProj(projId, testOrgId);
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.DELETE',
                            icon : 'remove-row',
                            onClick : function() {
                                Gikam.getComp('emc-schemeedit-audit-list-grid').deleteRows(emcProject.schemeUrl);
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
                                hidden:true,
                                onClick : function() {
                                    var data = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                                    var testOrgId = data.entrustOrgId;
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
                        }, {
                            type : 'dropDownMenu',
                            text : 'EMC.MODULE.BP.PROJECT.PROJECTS.BUTTON.ORDER_QUICK_INPUT',
                            items : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.EXPORT',
                                icon : 'move-up',
                                onClick : function() {
                                    _this.schemeQuickExport();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.IMPORT',
                                icon : 'move-down',
                                onClick : function() {
                                    _this.schemeQuickImport();
                                }
                            } ]
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.MOVE_UP',
                            icon : 'move-up',
                            onClick : function() {
                                _this.projSchemeMoveUp();
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.MOVE_DOWN',
                            icon : 'move-down',
                            onClick : function() {
                                _this.projSchemeMoveDown();
                            }
                        }, ]
                    } ]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'emc-proj-order-edit-list-grid',
                        url : _this.getOrderUrl(),
                        columns : emcProject.getOrderEditListColumns(),
                        service : 'emcProjOrderServiceImpl',
                        order : {
                            'sortNum' : 'asc'
                        },
                        group : {
                            fields : [ "schemeName"],
                            formatter : function(row) {
                                return row.schemeName;
                            }
                        },
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
                            hidden : true,
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
                            hidden : true,
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
                        }, {
                            type : 'dropDownMenu',
                            text : '分包单',
                            hidden : true,
                            items : [ {
                                type : 'button',
                                text : '创建分包单',
                                icon : 'add',
                                onClick : function() {
                                    _this.createSamplingSubpackage();
                                }
                            }, {
                                type : 'button',
                                text : '添加至分包单',
                                icon : 'edit',
                                onClick : function() {
                                    _this.addToSamplingSubpackage();
                                }
                            } ]
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.MOVE_UP',
                            icon : 'move-up',
                            onClick : function() {
                                _this.projOrderMoveUp();
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.MOVE_DOWN',
                            icon : 'move-down',
                            onClick : function() {
                                _this.projOrderMoveDown();
                            }
                        }, ]
                    } ]
                }
            } ]
        }
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-proj-order-edit-list-grid').done(function(grid) {
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
            return Gikam.printf(emcProject.schemeUrl + '/{id}/proj-orders/queries', {
                id : projSchemeId
            })
        }
    },

    updateCycleAndFrequency : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
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
                url : emcProject.schemeUrl + '/page/proj-order-batch-update',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        gridData.map(function(item) {
                            item.sampleNum = data.sampleNum;
                        });
                        Gikam.put(emcProject.schemeUrl + '/' + activeRow.id, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', gridData ])).done(function() {
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
                                schemeName : item.schemeName,
                                sortNum : item.sortNum
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
                        Gikam.put(emcProject.schemeUrl + '/' + gridData[0].id, Gikam.getJsonWrapper({}, [ 'emcProjSchemeServiceImpl', gridData ])).done(function() {
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

    copyShare : function() {
        var gridData = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        if (gridData.length > 0) {
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.COPY_SHARE',
                width : 300,
                height : 200,
                url : emcProject.schemeUrl + '/page/copy-share',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        workspace.window.showMask(true);
                        var url = emcProject.schemeUrl + '/action/copy';
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
            url : emcProject.schemeUrl + '/page/select-monitoring-point' + Gikam.param({
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

                    var url = emcProject.schemeUrl + '/action/copy-point'
                    Gikam.post(url, Gikam.getJsonWrapper({}, [ '', gridData ], [ '', chooseSchemeData ])).always(function() {
                        Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },


    _monitorPointType : '',
    _sampleOrgId : '',
    _sampleOrgName : '',
    _selectId : [],
    addProj : function(projId, editMonitorInfo, testOrgId, sampleOrgId) {
        var _this = this;
        var url = emcProject.schemeUrl;
        if (editMonitorInfo === '1') {// 不选择监测点
            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.SELECT_MONITOR_TMPL',
                width : '80%',
                height : '80%',
                url : emcProject.schemeUrl + '/page/select-monitor-tmpl' + Gikam.param({
                    editMonitorInfo : editMonitorInfo
                }),
                onAfterClose : function(tmlData) {
                    if (!Gikam.isEmpty(tmlData)) {
                        var chooseSchemeData = {
                            projId : projId,
                            monitorTmplId : tmlData[0].monitorTmplId,
                            sampleOrgId : sampleOrgId
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
                width : '60%',
                height : '70%',
                url : emcProject.schemeUrl + '/page/select-monitoring-point' + Gikam.param({
                    testOrgId : testOrgId,
                    monitorPointType : _this._monitorPointType,
                    sampleOrgId : _this._sampleOrgId,
                    sampleOrgName : _this._sampleOrgName,
                    selectIds : _this._selectId,
                    defaultOrgId : sampleOrgId
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
                            width : '80%',
                            height : '80%',
                            url : emcProject.schemeUrl + '/page/select-monitor-tmpl' + Gikam.param({
                                editMonitorInfo : editMonitorInfo,
                                monitorPointCategoryId : pointData[0].monitorPointCategoryId
                            }),
                            onAfterClose : function(tmlData) {
                                if (!Gikam.isEmpty(tmlData)) {
                                    if (tmlData == 'PREVIOUS') {
                                        emcProject.editPage.addProj(projId, editMonitorInfo, testOrgId);
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

    initAddProj : function(projId, testOrgId) {
        if (Gikam.isEmpty(testOrgId)) {
            Gikam.alert("EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.PROJECT_TESTORGID");
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.MODAL.INITIALIZ_SCHEME',
            width : 1500,
            height : 600,
            url : emcProject.schemeUrl + '/page/init-scheme' + Gikam.param({
                projId : projId,
                testOrgId : testOrgId
            }),
            onAfterClose : function(data) {
                Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
            }
        });
    },

    orderQuickExport : function() {
        var orderData = Gikam.getComp('emc-project-edit-list-order-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.ORDER_CAN_NOT_EXPORT');
            return;
        }
        var projectId = orderData[0].projId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'pfOrderExportTemplate',
            service : 'pfOrderExportTemplate',
            id : projectId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.download(fileUrl);
        });

    },

    orderQuickImport : function() {
        var orderData = Gikam.getComp('emc-project-edit-list-order-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ORDER_CAN_NOT_IMPORT');
            return;
        }
        var id = orderData[0].projId;

        Gikam.create('simpleUploader', {
            dbTable : 'T_EMC_ORDER',
            bizId : id,
            multiple : false,
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.window.showMask(true);

                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'pfOrderImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function(result) {
                        if (result.url) {
                            Gikam.download(result.url);
                        } else if (result.message) {
                            Gikam.alert(result.message);
                        }
                        Gikam.getComp('emc-project-edit-list-order-grid').refresh();
                    }).always(function() {
                        Gikam.window.closeMask();
                    });
            }
        });
    },

    schemeQuickExport : function() {
        var orderData = Gikam.getComp('emc-schemeedit-audit-list-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('监测方案无数据，无法导出');
            return;
        }
        var projectId = orderData[0].projId;
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}/{id}', {
            templateCode : 'pfProjSchemeExportTemplate',
            service : 'pfProjSchemeExportTemplate',
            id : projectId
        })).done(function(fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.download(fileUrl);
        });

    },

    schemeQuickImport : function() {
        var orderData = Gikam.getComp('emc-schemeedit-audit-list-grid').getData();
        if (Gikam.isEmpty(orderData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ORDER_CAN_NOT_IMPORT');
            return;
        }
        var id = orderData[0].projId;

        Gikam.create('simpleUploader', {
            dbTable : 'T_EMC_PROJ_SCHEME',
            bizId : id,
            multiple : false,
            onAfterClose : function(files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                Gikam.window.showMask(true);

                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'pfProjSchemeImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function() {
                    Gikam.getComp('emc-schemeedit-audit-list-grid').refresh();
                }).always(function() {
                    Gikam.window.closeMask();
                });
            }
        });
    },

    /** 监测方案设置分包商 */
    addSubcontractor : function() {
        var schemeSelections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        var orderSelections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (schemeSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_SCHEME');
            return;
        }
        if (orderSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_ORDER');
            return;
        }
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
                    orderSelections.map(function(item) {
                        item.subcontractorId = data[0].id;
                    });
                    Gikam.post('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/relevance-subcontractor-update', Gikam.getJsonWrapper({},[ 'emcProjSchemeServiceImpl', schemeSelections ], [ 'emcProjOrderServiceImpl', orderSelections ])).done(function() {
                    }).always(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    /** 监测方案删除分包商 */
    delSubcontractor : function() {
        var _this = this;
        var schemeSelections = Gikam.getComp('emc-schemeedit-audit-list-grid').getSelections();
        var orderSelections = Gikam.getComp('emc-proj-order-edit-list-grid').getSelections();
        if (schemeSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_SCHEME');
            return;
        }
        if (orderSelections.length <= 0) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.CHOOSE_AT_LEAST_ONE_ORDER');
            return;
        }
        workspace.window.showMask(true);
        Gikam.post('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/relevance-subcontractor-delete', Gikam.getJsonWrapper({},[ 'emcProjSchemeServiceImpl', schemeSelections ], [ 'emcProjOrderServiceImpl', orderSelections ])).done(function() {
        }).always(function() {
            Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
            workspace.window.closeMask();
        });
    },

    addDataSharing : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            selections.map(function(item) {
                item.dataSharing = 1;
            });
            workspace.window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    delDataSharing : function() {
        var selections = Gikam.getComp('emc-project-edit-list-order-item-group-grid').getSelections();
        if (selections.length > 0) {
            var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
            selections.map(function(item) {
                item.dataSharing = 0;
            });
            workspace.window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    insertDirectorRow : function() {
        var formData = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('emp').title,
            url : Gikam.choose.getConfig('emp').url + Gikam.param({
                targetType : 'insertDirector',
                projectId : formData.id,
                orgId_SB: workspace.user.ext$.parentorgid
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
                Gikam.getComp('emc-project-edit-list-director-grid').insert(emcProject.baseUrl + '/directors', chooseData);
            }
        });
    },

    deleteDirectorRow : function() {
        var url = emcProject.baseUrl + '/directors';
        Gikam.getComp('emc-project-edit-list-director-grid').deleteRows(url).done(function() {
        });
    },

    onSubpackageActive : function(row) {
        //展示分包单下面的样品信息
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-subpackage-edit-list-base-info-form').done(function (form) {
                form.refresh({
                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/{id}', {
                        id : row.id
                    }),
                });
            });
            Gikam.getComp('emc-project-edit-list-subpackage-order-grid').refresh({
                url : Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                    id : row.projId
                })
            });

        }

    },

    onSampleSubpackageActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getAsyncComp('emc-subpackage-sample-edit-list-base-info-form').done(function (form) {
                form.refresh({
                    url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/{id}', {
                        id : row.id
                    }),
                });
            });
            Gikam.getComp('emc-project-edit-list-subpackage-folder-grid').refresh({
                url : emcProject.schemeUrl + '/queries/raw',
                requestData: {
                    projId_SEQ : row.projId,
                    subpackageflag_SEQ : '1'
                }
            });
        }

    },


    onSubpackageOrderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
        console.log(selections);
        if (row) {
            Gikam.getComp('emc-project-edit-list-subpackage-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    orderId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    onSampleSubpackageInfoActive : function(row) {
        //展示分包单下面的点位信息
        var _this = this;
        console.log(row);
        if (row) {
            Gikam.getComp('emc-project-edit-list-subpackage-folder-grid').refresh({
                url : emcProject.schemeUrl + '/queries/raw',
                requestData: {
                    projId_SEQ : row.projId,
                    subpackageflag_SEQ : '1'
                }
            });
        }

    },


    onSubpackageFolderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
        console.log(selections);
        if (row) {
            Gikam.getComp('emc-project-edit-list-subpackage-sample-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    projSchemeId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    onSampleSubpackageFolderInfoActive : function(row) {
        //展示分包单样品下的测试信息
        var _this = this;
        var selections = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections();
        console.log(selections);
        if (row) {
            Gikam.getComp('emc-project-edit-list-subpackage-sample-order-task-grid').refresh({
                url : IFM_CONTEXT + "/secure/emc/module/bp/subpackages/tasks/queries",
                requestData: {
                    projSchemeId_SEQ : row.id,
                    subpackageId_SEQ : selections[0].id,
                }
            });
        }

    },

    getSubpackageButton : function() {
        var _this = this;
            return  [ {
                type : 'button',
                text : '添加',
                icon : 'add',
                onClick : function() {
                    //区分采样和送样的
                    var proj = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
                    if(proj.sample == 1){
                        //采样进入点位测试选择页面
                        Gikam.create('modal', {
                            title : '选择分包测试',
                            url : emcProject.baseUrl + '/page/proj-order-choose-list' + Gikam.param({
                                    projId : proj.id,
                                    subcontractorid_IN: ',',
                            }),
                            onAfterClose : function(projOrder) {
                                console.log(projOrder);
                                let projOrders = Object.values(projOrder);
                                let projOrderArr = Object.values(projOrders[0]);
                                console.log(projOrderArr);
                                Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
                                    workspace.window.showMask(true);
                                    if (Gikam.isEmpty(proj) || Gikam.isEmpty(projOrder)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }
                                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create-proj-package',
                                        Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ proj ] ], [ 'emcProjOrderServiceImpl', projOrderArr ])).done(function() {
                                        Gikam.getComp('emc-project-edit-list-subpackage-info-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                        Gikam.getComp('emc-project-edit-list-subpackage-info-grid').refresh();
                                    });
                                });

                            }
                        });
                    }else {
                        //送样进入样品分析项组选择页面
                        Gikam.create('modal', {
                            title : '选择分包测试',
                            url : emcProject.baseUrl + '/page/order-itemgroup-choose-list'+ Gikam.param({
                                projId : proj.id,
                                subcontractorid_IN: ',',
                            }),
                            onAfterClose : function(orderItemGroup) {
                                console.log(orderItemGroup);
                                let orderItemGroups = Object.values(orderItemGroup);
                                let orderItemGroupArr = Object.values(orderItemGroups[0]);
                                console.log(orderItemGroupArr);
                                Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
                                    workspace.window.showMask(true);
                                    if (Gikam.isEmpty(proj) || Gikam.isEmpty(orderItemGroup)) {
                                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                        return;
                                    }
                                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create-proj-package', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ proj ] ], [ 'emcOrderItemGroupServiceImpl', orderItemGroupArr ])).done(function() {
                                        Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                        Gikam.getComp('emc-project-edit-list-subpackage-info-grid').refresh();
                                    });
                                });

                            }
                        });
                    }


                }
            },{
                type : 'button',
                text : '提交',
                icon : 'submit',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
                    console.log(selections);
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/submit-to-subpackage', Gikam.getJsonWrapper({
                    }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-project-edit-list-subpackage-info-grid').refresh();
                    });

                }
            },{
                type : 'button',
                text : '完成',
                icon : 'edit',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
                    let hasNoAssigned = selections.some(item => item.status !='1');
                    console.log(hasNoAssigned);
                    if (hasNoAssigned) {
                        Gikam.alert('所选分包单不是委托状态，不能完成！');
                        return;
                    }else{
                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/complete-package',
                            Gikam.getJsonWrapper({}, [ 'emcSubpackageServiceImpl', selections ],)).done(function() {
                            Gikam.getComp('emc-project-edit-list-subpackage-info-grid').refresh();
                        })
                    }

                }
            },{
                type : 'button',
                text : '删除',
                icon : 'remove',
                onClick : function() {
                    //删除未委托的分包单
                    var selections = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    let hasAssigned = selections.some(item => item.status === '1');
                    if (hasAssigned) {
                        Gikam.alert('所选分包单已经被委托，请先撤销再删除!');
                        return;
                    }
                    console.log(selections);
                    Gikam.getComp('emc-project-edit-list-subpackage-info-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/subpackages');
                }
            },{
                type : 'button',
                text : '撤回',
                icon : 'cancel',
                onClick : function() {
                    var selections = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/undo-subpackage', Gikam.getJsonWrapper({
                    }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-project-edit-list-subpackage-info-grid').refresh();
                    });

                }
            },{
                type : 'button',
                text : '查看分包单',
                icon : 'select',
                onClick : function() {
                    Gikam.viewReport('subpackageProject', Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections());
                }
            } ]
    },

    getSampleSubpackageButton : function() {
        var _this = this;
        return  [ {
            type : 'button',
            text : '添加',
            icon : 'add',
            onClick : function() {
                //区分采样和送样的
                var proj = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
                if(proj.sample == 1){
                    //采样进入点位测试选择页面
                    Gikam.create('modal', {
                        title : '选择分包测试',
                        url : emcProject.baseUrl + '/page/proj-order-choose-list' + Gikam.param({
                            projId : proj.id,
                            subcontractorid_IN: ',',
                        }),
                        onAfterClose : function(projOrder) {
                            console.log(projOrder);
                            let projOrders = Object.values(projOrder);
                            let projOrderArr = Object.values(projOrders[0]);
                            console.log(projOrderArr);
                            Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
                                workspace.window.showMask(true);
                                if (Gikam.isEmpty(proj) || Gikam.isEmpty(projOrder)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create-proj-package',
                                    Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ proj ] ], [ 'emcProjOrderServiceImpl', projOrderArr ])).done(function() {
                                    Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                    Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                                });
                            });

                        }
                    });
                }else {
                    //送样进入样品分析项组选择页面
                    Gikam.create('modal', {
                        title : '选择分包测试',
                        url : emcProject.baseUrl + '/page/order-itemgroup-choose-list'+ Gikam.param({
                            projId : proj.id,
                            subcontractorid_IN: ',',
                        }),
                        onAfterClose : function(orderItemGroup) {
                            console.log(orderItemGroup);
                            let orderItemGroups = Object.values(orderItemGroup);
                            let orderItemGroupArr = Object.values(orderItemGroups[0]);
                            console.log(orderItemGroupArr);
                            Gikam.confirm('操作提示', '是否确定创建分包单？', function() {
                                workspace.window.showMask(true);
                                if (Gikam.isEmpty(proj) || Gikam.isEmpty(orderItemGroup)) {
                                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                    return;
                                }
                                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/create-proj-package', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ proj ] ], [ 'emcOrderItemGroupServiceImpl', orderItemGroupArr ])).done(function() {
                                    Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                    Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                                });
                            });

                        }
                    });
                }


            }
        },{
            type : 'button',
            text : '提交',
            icon : 'submit',
            onClick : function() {

                var selections = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }

                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/submit-to-subpackage', Gikam.getJsonWrapper({
                }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                    Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                });


            }
        },{
            type : 'button',
            text : '完成',
            icon : 'edit',
            onClick : function() {
                var selections = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections();
                let hasNoAssigned = selections.some(item => item.status !='1');
                console.log(hasNoAssigned);
                if (hasNoAssigned) {
                    Gikam.alert('所选分包单不是委托状态，不能完成！');
                    return;
                }else{
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/complete-package',
                        Gikam.getJsonWrapper({}, [ 'emcSubpackageServiceImpl', selections ],)).done(function() {
                        Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                    })
                }
            }
        },{
            type : 'button',
            text : '删除',
            icon : 'remove',
            onClick : function() {
                //删除未委托的分包单
                var selections = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                let hasAssigned = selections.some(item => item.status === '1');
                if (hasAssigned) {
                    Gikam.alert('所选分包单已经被委托，请先撤销再删除!');
                    return;
                }
                console.log(selections);
                Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').deleteRows(IFM_CONTEXT + '/secure/emc/module/bp/subpackages');
            }
        },{
            type : 'button',
            text : '撤回',
            icon : 'cancel',
            onClick : function() {
                //撤回发个其他公司的分包单
                var selections = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    return;
                }
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/undo-subpackage', Gikam.getJsonWrapper({
                }, [ 'emcSubpackageServiceImpl', selections ])).done(function() {
                    Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').refresh();
                });

            }
        },{
            type : 'button',
            text : '查看分包单',
            icon : 'select',
            onClick : function() {
                Gikam.viewReport('subpackageProject', Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections());
            }
        } ]
    },


    getProjSchemeSubpackageButton : function() {
        var _this = this;
        return  [{
            type : 'button',
            text : '编辑测试',
            icon : 'edit',
            onClick : function() {
                //区分采样和送样的
                var subpackage = Gikam.getComp('emc-project-edit-list-sample-subpackage-info-grid').getSelections();
                var selections = Gikam.getComp('emc-project-edit-list-subpackage-folder-grid').getSelections();
                console.log(subpackage);

                //采样进入点位测试选择页面
                    Gikam.create('modal', {
                        title : '选择分包测试',
                        url : emcProject.baseUrl + '/page/proj-order-order-choose-list' + Gikam.param({
                            projSchemeId : selections[0].id,
                            subcontractorid_IN: ',',
                        }),
                        onAfterClose : function(projOrder) {
                            let projOrders = Object.values(projOrder);
                            let projOrderArr = Object.values(projOrders[0]);
                            console.log(projOrderArr);
                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/add-to', Gikam.getJsonWrapper({}, [ '', subpackage ], [ '', selections ], [ '', projOrderArr ])).done(function() {
                                Gikam.getComp('emc-project-edit-list-subpackage-folder-grid').refresh();
                            })

                        }
                    });


            }
        }]
    },

    getOrderSubpackageButton : function() {
        var _this = this;
        return  [{
            type : 'button',
            text : '编辑测试',
            icon : 'edit',
            onClick : function() {
                var subpackage = Gikam.getComp('emc-project-edit-list-subpackage-info-grid').getSelections();
                console.log(subpackage);
                var selections = Gikam.getComp('emc-project-edit-list-subpackage-order-grid').getSelections();
                //送样进入样品分析项组选择页面
                Gikam.create('modal', {
                    title : '选择分包测试',
                    url : emcProject.baseUrl + '/page/order-itemgroup-order-choose-list'+ Gikam.param({
                        orderId : selections[0].id,
                        subcontractorid_IN: ',',
                    }),
                    onAfterClose : function(orderItemGroup) {
                        let orderItemGroups = Object.values(orderItemGroup);
                        let orderItemGroupArr = Object.values(orderItemGroups[0]);
                        console.log(orderItemGroup);
                        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/subpackages/action/add-to', Gikam.getJsonWrapper({}, [ '', subpackage ], [ '', selections ], [ '', orderItemGroupArr ])).done(function() {
                            Gikam.getComp('emc-project-edit-list-subpackage-order-grid').refresh();
                        })
                    }
                });

            }
        }]
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-edit-list-layout',
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
