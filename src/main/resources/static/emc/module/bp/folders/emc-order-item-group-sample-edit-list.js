//@ sourceURL=emc-order-item-group-sample-edit-list.js
emcOrder.testSampleEditPage = {

    getConfirmBtnItem : function() {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.CONFIRM',
            icon : 'select',
            margin : '13px 0 0 15px',
            onClick : function () {
                _this.confirm();
            }
        }
    },

    getShowAllForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-order-item-group-sample-edit-list-show-all-form',
            titleWidthAuto : true,
            columns : 1,
            fields : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
                field : 'showFlag',
                width : 110,
                type : 'simpleCheckbox',
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
                onChange : function(field, value) {
                    _this.showFlag = value;
                    var leftGrid = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame').leftGrid;
                    leftGrid.setOptions({
                        requestData : {
                            monitorTmplId_SEQ : _this.param.monitorTmplId,
                            schemeName_SEQ : _this.schemeName,
                            defaultFlag_SEQ : value === '1' ? null : '1',
                            activatedFlag_SEQ : '1',
                        }
                    });
                    leftGrid.refresh();
                },
            } ]
        };
    },

    getSelectForm : function() {
        var _this = this;

        return {
            titleWidthAuto : true,
            type : 'form',
            id : 'emc-order-item-group-sample-edit-list-form',
            columns : 1,
            fields : [ {
                title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
                field : 'schemeName',
                category : 'monitorScheme',
                param : {
                    monitorTmplId : _this.param.monitorTmplId
                },
                type : 'select',
                onChange : function(field, value) {
                    _this.schemeName = value;
                    var leftGrid = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame').leftGrid;

                    leftGrid.setOptions({
                        requestData : {
                            monitorTmplId_SEQ : _this.param.monitorTmplId,
                            schemeName_SEQ : value,
                            defaultFlag_SEQ : _this.showFlag === '1' ? null : '1',
                            activatedFlag_SEQ : '1',
                        }
                    });

                    leftGrid.refresh();
                }
            } ],
            onRendered : function() {
                _this.form = this;
            }
        };
    },

    confirm : function() {
        var _this = this;
        var rows = _this.rightSelectedData;
        if (Gikam.isEmpty(rows)) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_TEST');
            return;
        }

        var tests = rows.map(item => (item.testId + item.itemGroup));
        var flag = {};
        for (let index in tests) {
            if (flag[tests[index]]) {
                Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CANNOT_CHOOSE_REITERATED_TEST');
                return;
            }
            flag[tests[index]] = true;
        }

        var chooseData = rows.map(function(item) {
            return {
                schemeName : item.schemeName,
                testName : item.ext$.testname,
                testId : item.testId,
                itemGroupId : item.itemGroupId,
                itemGroup : item.itemGroup,
                testMethodId : item.testMethodId,
                methodId : item.methodId!=undefined?item.methodId:item.ext$.methodid,
                methodName : item.ext$.methodname,
                methodVersionId : item.ext$.methodversionid,
                testMethodBasis : item.ext$.testmethodbasis,
                analysisOrgId : item.ext$.analysisorgid,
                analysisOrgName : item.ext$.analysisorgname,
                inputUserId : item.ext$.inputuserid,
                inputUserName : item.ext$.inputusername,
                monitorTmplId : _this.param.monitorTmplId,
                orderCategory : _this.param.orderCategory
            }
        });

        Gikam.getLastModal().window.showMask(true);
        Gikam.put(emcOrder.baseUrl + '/action/edit-tests', Gikam.getJsonWrapper({
            ids : _this.param.orderIds
        }, [ '', [ {
            monitorTmplId : _this.param.monitorTmplId,
            orderCategory : _this.param.orderCategory
        } ] ], [ '', chooseData ])).done(function() {
            Gikam.getLastModal().close();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    getShuttleFrameParam : function() {
        var _this = this;
        var leftGridUrl = '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable';
        var rightGridUrl = '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable';
        var leftRequestData = {
            order_task_unassigned : '1',
            orderId : _this.param.orderIds.split(',')[0],
            monitorTmplId_SEQ : _this.param.monitorTmplId,
            defaultFlag_SEQ : '1',
            activatedFlag_SEQ : '1',
        };

        var rightRequestData = {
            order_task_assigned : '1',
            orderId : _this.param.orderIds.split(',')[0],
            monitorTmplId_SEQ : _this.param.monitorTmplId,
            activatedFlag_SEQ : '1',
        };

        return {
            type : 'shuttleFrame',
            id : 'emc-order-item-group-edit-list-shuttle-frame',
            columns : emcOrder.getItemGroupShuttleFrameColumns(),
            leftGridUrl : leftGridUrl,
            leftRequestData : leftRequestData,
            onRendered : function() {
                _this.shuttleFrame = this;
            },
            onLeftLoadSuccess : function() {
                // 当左侧数据加载完成后，再去初始化右侧的数据，通过rightInited标志位避免右侧数据重复加载
                if (!_this.rightInited) {
                    _this.shuttleFrame = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame');
                    Gikam.post(rightGridUrl, Gikam.getJsonWrapper({
                        f : rightRequestData,
                        n : -1,
                    })).done(function (rightData) {
                        _this.shuttleFrame.setRightData(rightData.rows);
                        _this.rightSelectedData = rightData.rows;
                        _this.rightInited = true;
                    })
                }
            },
            onAddClick : function(data, left, right) {
                _this.rightSelectedData = _this.rightSelectedData.concat(data);
            },
            onDelClick : function(data, left, right) {
                _this.rightSelectedData = _this.rightSelectedData.filter(function(item) {
                    //去掉删除的数据
                    return !data.some(function(rightItem) {
                        return rightItem.id === item.id;
                    });
                });
            }
        }
    },

    create : function() {
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 52,
                items : [ {
                    type : 'layout',
                    west : {
                        width : 380,
                        items : [ {
                            type : 'layout',
                            west : {
                                width : 100,
                                items : [ this.getShowAllForm() ]
                            },
                            center : {
                                items : [ this.getSelectForm() ]
                            }
                        } ]
                    },
                    center : {
                        items : [ this.getConfirmBtnItem() ]
                    }
                } ]
            },
            center : {
                items : [ this.getShuttleFrameParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.rightSelectedData = [];
        this.rightInited = false;
        this.create();
        var _this = this;
        _this.showFlag = '0';
    },

}
