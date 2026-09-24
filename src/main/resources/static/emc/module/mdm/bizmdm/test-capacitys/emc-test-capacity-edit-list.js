emcTestCapacity.editPage = {

    getGridParam : function() {
        var _this = this;
        var yesText = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.CONFIRM.YES_TEXT');
        var noText = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.CONFIRM.NO_TEXT');
        return {
            type : 'grid',
            id : 'emc-test-capacity-edit-list-grid',
            url : emcTestCapacity.baseUrl + '/queries/raw',
            service : 'emcTestCapacityServiceImpl',
            columns : emcTestCapacity.getEditListColumns(),
            requestData : {
              org_filter : '1',
            },
            checkOnActive : true,
            filterOpen : true,
            page : true,
            pageSize : 100,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-test-capacity-edit-list-base-info-form','emc-test-capacity-item-group-list-grid' ]);
                }
            },
            onRendered : function() {
                var grid = Gikam.getComp('emc-test-capacity-edit-list-grid');
           	    Gikam.getComp('emc-test-capacity-copy-method-param').hide();
            	console.log(workspace.user);
            	console.log(workspace.user.isAdmin);
                if(!workspace.user.isAdmin){
                	 Gikam.getComp('emc-test-capacity-copy-all').hide();

                	 //Gikam.getComp('emc-test-capacity-copy-method-param').hide();
                }
            },
            onRowActive :  function(index, row){
                Gikam.getAsyncComp('emc-test-capacity-item-group-list-grid').done(function(grid) {
                    grid.refresh({
                        url : _this.getItemGroupUrl(row.id)
                    });
                });

                Gikam.getAsyncComp('emc-test-capacity-equipt-list-grid').done(function(grid) {
                    grid.refresh({
                        url : _this.getEquiptUrl(row.testMethodId)
                    });
                });

                Gikam.getAsyncComp('emc-test-capacity-default-qc-list-grid').done(function(grid) {
                    grid.refresh({
                        url : _this.getDefaultQcUrl(row.id)
                    });
                });

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.create('modal', {
                        width : '40%',
                        height :  '70%',
                        title : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.MODAL.TESTMETHOD.ADD',
                        url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                            target_unassigned : '1',
                            targetTable : 'T_EMC_TEST_CAPACITY',
                            targetTestMethodIdColumn : 'TESTMETHODID',
                            targetFilterColumn : 'ORGID',
                            targetFilterValue : workspace.user.ext$.parentorgid
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/test/test-capacitys/batchInsert', Gikam.getJsonWrapper({}, [ '', rows ] )).done(function(data) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-test-capacity-edit-list-grid').deleteRows(emcTestCapacity.baseUrl);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_ORG',
                icon : 'modify',
                hidden : true,
                onClick : function() {
                    var rows = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(rows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                    Gikam.create('modal', {
                        title : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_ORG',
                        width : '40%',
                        url : IFM_CONTEXT + '/secure/core/module/mdm/orgs/page' + '/simple-choose-list',
                        requestData: {
                            orgCategory_SEQ : 'department',
                        },
                        onAfterClose : function (data) {
                            if (!Gikam.isEmpty(data) && data !== undefined) {
                                for (var row of rows) {
                                    this.activeGrid.setData({
                                        index : row.gridIndex - 1,
                                        analysisOrgId : data[0].id,
                                        ext$ : {
                                            analysisorgname : data[0].orgName
                                        }
                                    });
                                }
                            }
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_AUTH',
                icon : 'modify',
                hidden : true,
                onClick : function() {
                    var rows = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(rows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                    var title = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_AUTH');
                    var message = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.MESSAGE_AUTH');
                    Gikam.create('confirm', {
                        title : title,
                        message : message,
                        yesText : yesText,
                        noText : noText,
                        onYesClick : function () {
                            for (var row of rows) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                                    index : row.gridIndex - 1,
                                    ext$ : {
                                        authentication : '1'
                                    }
                                }, true);
                            }
                        },
                        onNoClick : function () {
                            for (var row of rows) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                                    index : row.gridIndex - 1,
                                    ext$ : {
                                        authentication : '0'
                                    }
                                });
                            }
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_APPROVAL',
                icon : 'modify',
                hidden : true,
                onClick : function() {
                    var rows = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(rows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                    var title = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_APPROVAL');
                    var message = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.MESSAGE_APPROVAL');
                    Gikam.create('confirm', {
                        title : title,
                        message : message,
                        yesText : yesText,
                        noText : noText,
                        onYesClick : function () {
                            for (var row of rows) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                                    index : row.gridIndex - 1,
                                    ext$ : {
                                        approval : '1'
                                    }
                                });
                            }
                        },
                        onNoClick : function () {
                            for (var row of rows) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                                    index : row.gridIndex - 1,
                                    ext$ : {
                                        approval : '0'
                                    }
                                });
                            }
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_OUT',
                icon : 'modify',
                hidden : true,
                onClick : function() {
                    var rows = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(rows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                    var title = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE_BATCH_OUT');
                    var message = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.MESSAGE_OUT');
                    Gikam.create('confirm', {
                        title : title,
                        message : message,
                        yesText : yesText,
                        noText : noText,
                        onYesClick : function () {
                            for (var row of rows) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                                    index : row.gridIndex - 1,
                                    ext$ : {
                                        outsourcing : '1'
                                    }
                                });
                            }
                        },
                        onNoClick : function () {
                            for (var row of rows) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                                    index : row.gridIndex - 1,
                                    ext$ : {
                                        outsourcing : '0'
                                    }
                                });
                            }
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_UPDATE',
                icon : 'edit',
                onClick : function () {
                    _this.batchInput();
                }
            }, {
                type : 'button',
                text : '管理员初始化复制数据',
                id : 'emc-test-capacity-copy-all',
                icon : 'edit',
                onClick : function () {
                    _this.copyAllToOtherOrg();
                }
            },{
                type : 'button',
                text : '批量设置方法参数1',
                icon : 'edit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-test-capacity-edit-list-grid');
                    var gridSelections = grid.getSelections();
                    if (gridSelections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                        return;
                    }
                   /* var filterData = gridSelections.filter(item => item.methodId != gridSelections[0].methodId);
                    if (filterData.length != 0) {
                        Gikam.alert('批量修改方法参数信息时，清选择相同的方法进行修改');
                        return;
                    }*/

                    var _this = this;
                    Gikam.create('modal', {
                        height : 430,
                        title: '批量设置测试方法参数属性',
                        url : emcTestCapacity.baseUrl + '/page/test-capacity-method-update-param-batch-detail' + Gikam.param({
                            flag : 1,
                        }),
                        onAfterClose : function() {
                            Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                        }
                    });
                }
            },{
            //     type : 'button',
            //     text : '批量设置方法参数2',
            //     icon : 'edit',
            //     onClick : function() {
            //         var grid = Gikam.getComp('emc-test-capacity-edit-list-grid');
            //         var gridSelections = grid.getSelections();
            //         if (gridSelections.length < 1) {
            //             Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
            //             return;
            //         }
            //        /* var filterData = gridSelections.filter(item => item.methodId != gridSelections[0].methodId);
            //         if (filterData.length != 0) {
            //             Gikam.alert('批量修改方法参数信息时，清选择相同的方法进行修改');
            //             return;
            //         }*/
            //
            //         var _this = this;
            //         Gikam.create('modal', {
            //             height : 430,
            //             title: '批量设置测试方法参数属性',
            //             url : emcTestCapacity.baseUrl + '/page/test-capacity-method-update-param-batch-detail' + Gikam.param({
            //                 flag : 0,
            //             }),
            //             onAfterClose : function() {
            //                 Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
            //             }
            //         });
            //     }
            // }, {
                type : 'button',
                text : '管理员复制测试管理对应测试方法方法参数',
                id : 'emc-test-capacity-copy-method-param',
                icon : 'edit',
                onClick : function () {
                    _this.copyMethodParam();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.BUTTON.COPY_INFO_TO',
                icon : 'copy',
                onClick : function() {
                    var selections = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.create('modal', {
                        width : '40%',
                        height :  '70%',
                        title : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE.CHOOSE_TEST_CAPACITY',
                        url : Gikam.choose.getConfig('testCapacity').url + Gikam.param({
                            id_DIFFER : selections[0].id
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.postText(emcTestCapacity.baseUrl + '/action/copy-info-to', Gikam.getJsonWrapper({}, [ '', selections ], [ '', rows ] )).done(function(data) {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            // }, {
            //     type : 'button',
            //     text : '重新获取分析项',
            //     icon : 'refresh',
            //     onClick : function() {
            //         _this.refetchItemByTest();
            //     }
            } ]
        }
    },

    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 450,
            height : 360,
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
                            var updateData = selections.map(item => {
                                return {
                                    id : item.id,
                                    analysisOrgId : formData.analysisOrgId,
                                    ext$ : {
                                        analysisorgname : formData.ext$.analysisorgname,
                                        authentication : formData.ext$.authentication,
                                        approval : formData.ext$.approval,
                                        outsourcing : formData.ext$.outsourcing,
                                        effectivedate : formData.effectivedate,
                                        conrolledno : formData.ext$.conrolledno,
                                        analysisrecordname : formData.ext$.analysisrecordname,
                                        testitemsort : formData.ext$.testitemsort,
                                        siterecordno : formData.ext$.siterecordno,
                                        inputuserid : formData.ext$.inputuserid,
                                        inputusername : formData.ext$.inputusername,
                                        weighinguserid : formData.ext$.weighinguserid,
                                        weighingusername : formData.ext$.weighingusername,
                                        pretreatuserid : formData.ext$.pretreatuserid,
                                        pretreatusername : formData.ext$.pretreatusername,
                                        batchno :  formData.ext$.batchno,
                                        isdefault  :  formData.ext$.isdefault,
                                        qcrecordcontrolno  :  formData.ext$.qcrecordcontrolno,
                                        calicurvecontrolno  :  formData.ext$.calicurvecontrolno,
                                        methodscope  :  formData.ext$.methodscope,


                                    }
                                }
                            });

                            Gikam.getLastModal().window.showMask(true);

                            Gikam.put('/secure/emc/module/mdm/bizmdm/test/test-capacitys/'+ selections[0].id, Gikam.getJsonWrapper({}, [ 'emcTestCapacityServiceImpl', updateData ])).done(function() {
                                Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                            }).always(function() {
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
            id : 'emc-test-capacity-batch-input-form',
            columns: 1,
            titleWidth: 80,
            fields: emcTestCapacity.getBatchInputFields(),
            autoSave: false
        });
    },

    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [  {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ITEM_GROUP',
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ {
                            type : 'grid',
                            id : 'emc-test-capacity-item-group-list-grid',
                            url : _this.getItemGroupUrl(),
                            columns : emcTestCapacity.getItemGroupListColumns(),
                            service : 'emcCapacityItemGroupServiceImpl',
                            deleteFormatter : function(row) {
                                return row.itemGroup || row.id;
                            },
                            filter : false,
                            onLoadSuccess : function(data) {
                                if (!Gikam.isEmpty(data)) {
                                    this.activeRowByIndex(0);
                                } else {
                                    Gikam.cleanCompData([ 'emc-capacity-item-list-grid' ]);
                                }
                            },
                            onRowActive :  function(index, row){
                                Gikam.getAsyncComp('emc-capacity-item-list-grid').done(function(grid) {
                                    grid.refresh({
                                        url : emcTestCapacity.baseUrl + '/capacity-items/queries',
                                        requestData : {
                                            capItemGroupId_SEQ : row.id
                                        }
                                    });
                                });
                            },
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    _this.insertItemGroupRow();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                hidden : false,
                                onClick : function() {
                                    _this.deleteItemGroupRow();
                                }
                            // }, {
                            //     type : 'button',
                            //     text : '同步分析项',
                            //     icon : 'refresh',
                            //     onClick : function() {
                            //         _this.syncItem();
                            //     }
                            }, {
                                type : 'button',
                                text : '重新获取分析项',
                                icon : 'refresh',
                                onClick : function() {
                                    _this.refetchItem();
                                }
                            }]
                        } ]
                    },
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_ITEM',
                items : [ {
                    type : 'grid',
                    id : 'emc-capacity-item-list-grid',
                    service : 'emcCapacityItemServiceImpl',
                    url : '',
                    columns : emcTestCapacity.getItemListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.EDITBATCH',
                        icon : 'add',
                        onClick : function() {
                            _this.editBatchItem();
                        }
                    }]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.TEST_EQUIPT',
                items : [ {
                    type : 'grid',
                    fill : true,
                    id : 'emc-test-capacity-equipt-list-grid',
                    url : _this.getEquiptUrl(),
                    columns : emcTestCapacity.getEquiptListColumns(),
                    service : 'emcEquiptTestMethodServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    requestData : {
                        orgId_filter : '1',
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertEquiptRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteEquiptRow();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyEquiptTo();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TESTS.BUTTON.DEFAULT',
                        icon : 'default',
                        onClick : function() {
                            _this.setDefaultEquiptRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInpuAnalysis();
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.DEFAULT_QC',
                items : [ {
                    type : 'grid',
                    fill : true,
                    id : 'emc-test-capacity-default-qc-list-grid',
                    columns : emcTestCapacity.getDefaultQcListColumns(),
                    service : 'emcTestMethodDefaultQcServiceImpl',
                    deleteFormatter : function(row) {
                        return row.qcName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertDefaultQc();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteDefaultQc();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.BUTTON.COPY_INFO_TO',
                        icon : 'copy',
                        onClick : function() {
                            var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                            var selections = Gikam.getComp('emc-test-capacity-default-qc-list-grid').getSelections();
                            if (Gikam.isEmpty(selections)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }

                            Gikam.create('modal', {
                                width : '40%',
                                height :  '70%',
                                title : 'EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TITLE.CHOOSE_TEST_CAPACITY',
                                url : Gikam.choose.getConfig('testCapacity').url + Gikam.param({
                                    id_DIFFER : activedRow.id
                                }),
                                onAfterClose : function(rows) {
                                    if (Gikam.isEmpty(rows)) {
                                        return;
                                    }
                                    workspace.window.showMask(true);
                                    Gikam.postText(emcTestCapacity.baseUrl + '/action/copy-qc-to', Gikam.getJsonWrapper({}, [ '', rows ], [ '', selections ] )).done(function(data) {
                                        Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                                    }).always(function() {
                                        workspace.window.closeMask();
                                    });
                                }
                            });
                        }
                    } ]
                } ]
            } ]
        }
    },

    editBatchItem : function() {
        var selections = Gikam.getComp('emc-capacity-item-list-grid').getSelections();
        if (selections.length > 0) {
            var capacityData = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
            var modal = Gikam.create('modal', {
                title: "批录入",
                width : 600,
                height : 600,
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
                                    var data = form.getData();
                                    if (!Gikam.isEmpty(data)) {
                                        Gikam.getLastModal().window.showMask(true);
                                        selections.map(function(item) {
                                            item.orderNo = data.orderNo;
                                            item.lowLimit = data.lowLimit;
                                            item.ext$.limitunitname = data.ext$.limitunitname;
                                            item.unitName = data.unitName;
                                            item.revisionRule = data.revisionRule;
                                            item.analysisCategory = data.analysisCategory;
                                            item.resultCategory = data.resultCategory;
                                            item.defaultResult = data.defaultResult;
                                            item.qcCategoryId = data.qcCategoryId;
                                            item.qcCategoryName = data.qcCategoryName;
                                            item.originalRecordDisplay = data.originalRecordDisplay;
                                            item.reportDisplay = data.reportDisplay;
                                            item.standSetId = data.standSetId;
                                            item.interceptRevision = data.interceptRevision;
                                            item.slopeRevision = data.slopeRevision;
                                            item.ext$.detectedquantity= data.ext$.detectedquantity;
                                            item.ext$.detectedquantityunit= data.ext$.detectedquantityunit;
                                            item.ext$.usetype= data.ext$.usetype
                                            item.param = data.param;
                                            item.ignorezero = data.ignorezero;
                                            item.originalRecordName= data.originalRecordName;
                                            item.highLimit= data.highLimit;
                                            item.pnFunc= data.pnFunc;
                                            item.limitRevision = data.limitRevision;
                                            item.calQcResult = data.calQcResult;
                                            item.scientificNotation = data.scientificNotation;
                                            item.negativeToZero = data.negativeToZero;
                                            item.allowableError = data.allowableError;
                                            item.ext$.theorycontent= data.ext$.theorycontent;
                                            item.sampleParam = data.sampleParam;

                                        });
                                        Gikam.put(emcTestCapacity.baseUrl + '/' + capacityData.id, Gikam.getJsonWrapper({}, [ 'emcCapacityItemServiceImpl', selections ])).done(function() {
                                            Gikam.getComp('emc-capacity-item-list-grid').refresh();
                                        }).always(function() {
                                            Gikam.getLastModal().window.closeMask();
                                            modal.close();
                                        });
                                    }
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
                fields: emcTestCapacity.getBatchItemFormFields(),
                autoSave: false
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    insertItemGroupRow : function() {
        var formData = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(formData.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : '25%',
            height :  '70%',
            title : Gikam.choose.getConfig('testItemGroup').title,
            url : Gikam.choose.getConfig('testItemGroup').url + Gikam.param({
                testMethodId_SEQ : formData.testMethodId,
                target : 'testCapacity'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                rows.forEach(item => item['itemGroupId'] = item.id);
                workspace.window.showMask(true);
                var url =  Gikam.printf(emcTestCapacity.baseUrl + '/{id}/item-groups/insert', {
                    id : formData.id
                });
                Gikam.postText(url, Gikam.getJsonWrapper({}, [ '', rows ] )).done(function(data) {
                    Gikam.getComp('emc-test-capacity-item-group-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    deleteItemGroupRow : function() {
        var selections = Gikam.getComp('emc-test-capacity-item-group-list-grid').getSelections();
        if (selections.length > 0) {
            var formData = Gikam.getComp('emc-test-capacity-item-group-list-grid').getActivedRow();
            var url = Gikam.printf(emcTestCapacity.baseUrl + '/{id}/item-groups', {
                id : formData.id
            });
            Gikam.getComp('emc-test-capacity-item-group-list-grid').deleteRows(url);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    syncItem : function() {
        var selections = Gikam.getComp('emc-test-capacity-item-group-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(emcTestCapacity.baseUrl + '/action/sync-item', Gikam.getJsonWrapper({}, [ '', selections ] )).done(function() {
            Gikam.getComp('emc-capacity-item-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    refetchItem : function() {
        var selections = Gikam.getComp('emc-test-capacity-item-group-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(emcTestCapacity.baseUrl + '/action/refetch-item', Gikam.getJsonWrapper({}, [ '', selections ] )).done(function() {
            Gikam.getComp('emc-capacity-item-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    refetchItemByTest : function() {
        var selections = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postText(emcTestCapacity.baseUrl + '/action/refetch-item-by-test', Gikam.getJsonWrapper({}, [ '', selections ] )).done(function() {
            Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    insertEquiptRow : function() {
        var formData = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
        if (Gikam.isEmpty(formData.id)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/{id}/equipts', {
            id : formData.testMethodId
        });

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('equipt').title,
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIPT_TEST_METHOD',
                targetEquiptColumn : 'EQUIPTID',
                targetFilterColumn : 'TESTMETHODID',
                targetFilterValue : formData.id,
                status_SEQ : 'inUse',
                orgId_filter : '1',
                orgid_seq : workspace.user.ext$.parentorgid
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        testMethodId : formData.testMethodId,
                        equiptId : item.id,
                        orgId :  workspace.user.ext$.parentorgid,
                    }
                });
                Gikam.getComp('emc-test-capacity-equipt-list-grid').insert(url, chooseData);
            }
        });
    },

    deleteEquiptRow : function() {
        var formData = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/{id}/equipts', {
            id : formData.testMethodId
        });
        Gikam.getComp('emc-test-capacity-equipt-list-grid').deleteRows(url);
    },

    copyEquiptTo : function() {
        var selections = Gikam.getComp('emc-test-capacity-equipt-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testCapacity').title,
            url : Gikam.choose.getConfig('testCapacity').url + Gikam.param({

            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                var chooseData = rows.map(function(item) {
                    return {
                        id : item.testMethodId
                    }
                });
                url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/equipts/action/copyto';
                Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', chooseData ], [ 'emcEquiptTestMethodServiceImpl', selections ])).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    setDefaultEquiptRow : function() {
        var selections = Gikam.getComp('emc-test-capacity-equipt-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/{id}/equipts/action/set-default', {
            id : formData.testMethodId
        });

        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
            Gikam.getComp('emc-test-capacity-equipt-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    insertDefaultQc : function() {
        var selections = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
        if (selections.length < 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var testCapacity = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();

        Gikam.create('modal', {
            width : 500,
            title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.ADD_QC',
            url : '/secure/emc/module/bp/bp/order-tasks/page/run-qc-category' + Gikam.param({
                USECATEGORY_IN : 'lab,scene,orderAccept'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows[0]) || Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests' + '/{id}/default-qcs', {
                    id : testCapacity.id
                });
                var data = [];
                for (var i in selections) {
                    for (let j in rows[0]) {
                        var item = rows[0][j];
                        data.push({
                            testCapacityId : selections[i].id,
                            testMethodId : selections[i].testMethodId,
                            qcCategoryId : item.id,
                            qcCategoryCode : item.qcCategoryId,
                            qcName : item.qcName,
                            addNum : item.addNum,
                            orgId : workspace.user.ext$.parentorgid
                        })
                    }
                }
                Gikam.getComp('emc-test-capacity-default-qc-list-grid').insert(url, data);

            }
        });
    },

    deleteDefaultQc : function() {
        var testCapacity = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests' + '/{id}/default-qcs', {
            id : testCapacity.id
        });
        Gikam.getComp('emc-test-capacity-default-qc-list-grid').deleteRows(url);
    },

    getItemGroupUrl : function(testCapacityId) {
        if (testCapacityId) {
            return Gikam.printf(emcTestCapacity.baseUrl + '/{id}/item-groups/queries', {
                id : testCapacityId
            })
        }
    },

    getEquiptUrl : function(testMethodId) {
        if (testMethodId) {
            return Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/{id}/equipts/queries', {
                id : testMethodId
            })
        }
    },

    getDefaultQcUrl : function(testCapacityId) {
        if (testCapacityId) {
            return Gikam.printf(emcTestCapacity.baseUrl  + '/{id}/default-qcs/queries', {
                id : testCapacityId
            })
        }
    },


    // 批录入分析类型
    batchInpuAnalysis : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-test-capacity-equipt-list-grid').getSelections();
        var row = selections[0];
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
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
                                    item.analysisCategory = formData.analysisCategory;

                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(emcTestCapacity.baseUrl +'/' + row.id, Gikam.getJsonWrapper({}, [ 'emcEquiptTestMethodServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-test-capacity-equipt-list-grid').refresh();
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
            fields: emcTestCapacity.getBatchInputAnalysis(),
            autoSave: false
        });

    },

    loadMethodParamPage : function(id) {
        // 先选择方法参数模板的模板名称，之后点击可以查看且维护字段。 删除，先查有没有，再删 （bizId）。
        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.METHOD_PARAM_PROPERTY_SET',
            url : emcTestCapacity.baseUrl + '/page/method-param-detail' + Gikam.param({
                id : id,
                flag1 :'1',
            }),
        });
    },

    loadMethodParamPage2 : function(id) {
        // 先选择方法参数模板的模板名称，之后点击可以查看且维护字段。 删除，先查有没有，再删 （bizId）。
        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            title : 'EMC.MODULE.MDM.BIZMDM.TESTS.MODAL.METHOD_PARAM_PROPERTY_SET',
            url : emcTestCapacity.baseUrl + '/page/method-param-detail' + Gikam.param({
                id : id,
                flag1 :'0',
            }),
        });
    },


    copyAllToOtherOrg : function() {
    	//选择分公司
        Gikam.create('modal', {
            title : '选择复制到的公司',
            url : '/secure/core/module/mdm/orgs/page/choose-list'+ Gikam.param({
            	parentId_SEQ : '05',
            	orgcategory_SEQ : 'company',
            }),
            onAfterClose : function(data) {
            	//将三个表的数据复制以监测能力库为主表
                Gikam.postText(emcTestCapacity.baseUrl+'/copy-all-to-org', Gikam.getJsonWrapper(
                		{
                            f : {
                                orgId : data[0].id
                            }
                        }, [])).done(function(data) {
                        	alert("生成完成");
                    Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                }).always(function() {});
                Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();

            },
            height: '95%'
        })
    },

    copyMethodParam : function() {

        Gikam.confirm('复制方法参数', '是否复制', function() {
            Gikam.postText(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/property-tmpls/copy-test-method-param', Gikam.getJsonWrapper({}, [  ])).done(function() {
                Gikam.getComp('emc-test-capacity-edit-list-grid').refresh();
                Gikam.alert("复制成功");
            }).always(function () {
            });
        })
    },

    beforeHandlequery: function(index) {
        var grid = Gikam.getComp('emc-test-capacity-edit-list-grid');
        var row = grid.getData()[index];
        Gikam.create('modal', {
            width : '60%',
            height : 400,
            url : Gikam.choose.getConfig('beforeHandle').url + Gikam.param({
                testMethodId_EQ : row.testMethodId
            })
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-test-capacity-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : "50%",
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getOtherGridParam()
                 ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
