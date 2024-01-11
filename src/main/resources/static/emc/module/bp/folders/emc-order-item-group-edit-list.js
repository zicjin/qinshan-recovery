/**
 * 项目登记修改测试
 */

emcOrder.testEditPage = {

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
            id : 'emc-order-item-group-show-all-form',
            titleWidthAuto : true,
            columns : 1,
            fields : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
                field : 'showFlag',
                width : 110,
                type : 'simpleCheckbox',
                value : _this.showFlag,
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
                onChange : function(field, value) {
                    _this.showFlag = value;
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable', Gikam.getJsonWrapper({
                        f : {
                            monitorTmplId_SEQ : _this.param.monitorTmplId,
                            schemeName_SEQ : _this.schemeName,
                            defaultFlag_SEQ : value === '1' ? null : '1',
                            activatedFlag_SEQ : '1',
                        },
                        n : -1,
                    })).done(function(row) {
                        _this.setShuttleFrameData(row.rows);
                    });
                },
            } ]
        };
    },

    getSelectForm : function() {
        var _this = this;

        return {
            titleWidthAuto : true,
            type : 'form',
            id : 'emc-order-item-group-edit-list-form',
            columns : 1,
            fields : [ {
                title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
                field : 'schemeName',
                category : 'monitorScheme',
                param : {
                    monitorTmplId : _this.param.monitorTmplId,
                    enableflag :'1'
                },
                type : 'select',
                onChange : function(field, value) {
                    _this.schemeName = value;
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable', Gikam.getJsonWrapper({
                        f : {
                            monitorTmplId_SEQ : _this.param.monitorTmplId,
                            schemeName_SEQ : value,
                            defaultFlag_SEQ : _this.showFlag === '1' ? null : '1',
                            activatedFlag_SEQ : '1',
                            testmethodflag_SEQ :'1'
                        },
                        n : -1
                    })).done(function(row) {
                        _this.setShuttleFrameData(row.rows);
                    });
                }
            } ],
            onRendered : function() {
                _this.form = this;
                _this.form.setData({
                    schemeName : _this.schemeName,
                });
            }

        };
    },

    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-order-item-group-edit-list-shuttle-frame',
            columns : emcOrder.getItemGroupShuttleFrameColumns(),
            // delRepeatField : 'itemGroupId',
            // page : true,
            onRendered : function() {
                _this.shuttleFrame = this;
            },
            onLeftLoadSuccess : function() {
                // 当左侧数据加载完成后，再去初始化右侧的数据，通过rightInited标志位避免右侧数据重复加载
                if (!_this.rightInited) {
                    Gikam.post('/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable', Gikam.getJsonWrapper({
                        f :  {
                            order_assigned : '1',
                            orderId : _this.param.orderIds.split(',')[0],
                            monitorTmplId_SEQ : _this.param.monitorTmplId,
                            activatedFlag_SEQ : '1',
                            testmethodflag_SEQ :'1',
                        },
                        n : -1,
                    })).done(function (rightData) {
                        _this.shuttleFrame.setRightData(rightData.rows);
                        _this.rightSelectedData = rightData.rows;
                        _this.rightInited = true;
                    })
                }
            },
            onAddClick : function(data, left, right) {
                let sortNum = new Date().getTime();
                data.forEach(item => item.sortNum = sortNum);
                _this.rightSelectedData = _this.rightSelectedData.concat(data);
                // _this.filterSameAdd(data, left, right);
            },
            onDelClick : function(data, left, right) {
                // _this.filterSameDel(data, left, right);
                _this.rightSelectedData = _this.rightSelectedData.filter(function(item) {
                    //去掉删除的数据
                    return !data.some(function(rightItem) {
                        return rightItem.id === item.id;
                    });
                });
            }
        }
    },

    // 设置初始数据
    setShuttleFrameData : function(leftData) {
        var _this = this;
        _this.shuttleFrame = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame');
        // 左边初始数据
        _this.shuttleFrameLeftData = leftData;
        // 右边初始数据
        if (!_this.rightDataHaveRecorded) {
            _this.rightDataHaveRecorded = true;
            _this.shuttleFrameRightData = _this.shuttleFrame.rightGrid.getData();
        }

        // 设置左边数据
        _this.filterRepeatData(_this.shuttleFrame.rightGrid.getData());
    },

    // 初始数据去重
    filterRepeatData : function(right) {
        var _this = this;

        // 获取当前测试方案下的
        var allData = _this.getCurAllData(_this.shuttleFrameLeftData, _this.shuttleFrameRightData);

        // 与右边已选数比较去重，测试名相同、方法不同的过滤掉
        var filterLeftData = allData;
        if (!Gikam.isEmpty(allData)) {
            filterLeftData = allData.filter(function(item) {
                // 如果是同一条数据，则不显示
                var isSameData = right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid === item.ext$.methodid && rightItem.itemGroup === item.itemGroup;
                });
                if (isSameData) {
                    return false;
                }
                // 否则就要与右侧数据比较去重
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid !== item.ext$.methodid;
                });
            });
        }

        right.sort(item => item.sortNum);
        _this.shuttleFrame = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame');
        _this.shuttleFrame.rightGrid.loadData(right);
        _this.shuttleFrame.options.rightOriginalData = Gikam.deepExtend(right);
        _this.shuttleFrame.setLeftData(filterLeftData);
    },

    // 选择
    // data变动数据，left变动后左侧数据，right变动后右侧数据
    // 左侧数据，如果与右侧数据同测试名、不同方法，则不显示；
    filterSameAdd : function(data, left, right) {
        var _this = this;

        // 获取当前测试方案下的
        var allData = _this.getCurAllData(_this.shuttleFrameLeftData, _this.shuttleFrameRightData);

        // 与右边已选数比较去重，测试名相同、方法不同的过滤掉
        var filterLeftData = allData;
        if (!Gikam.isEmpty(allData)) {
            filterLeftData = allData.filter(function(item) {
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid !== item.ext$.methodid;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame');
        // 这里必须先设置右侧数据再设置左侧数据
        _this.shuttleFrame.rightGrid.loadData(right);
        _this.shuttleFrame.options.rightOriginalData = Gikam.deepExtend(right);
        _this.shuttleFrame.setLeftData(filterLeftData);
    },

    // 取消选择
    // data变动数据，left变动后左侧数据，right变动后右侧数据
    // 左侧数据，如果与右侧数据同测试名、不同方法，则不显示；
    filterSameDel : function(data, left, right) {
        var _this = this;

        // 获取当前测试方案下的
        var allData = _this.getCurAllData(_this.shuttleFrameLeftData, _this.shuttleFrameRightData);

        // 与右边已选数比较去重，测试名相同、方法不同的过滤掉
        var filterLeftData = allData;
        if (!Gikam.isEmpty(right)) {
            filterLeftData = allData.filter(function(item) {
                // 如果是取消选择的数据，则显示
                var isDelData = right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid === item.ext$.methodid && rightItem.itemGroup === item.itemGroup;
                });
                if (isDelData) {
                    return true;
                }
                // 否则就要与右侧数据比较去重
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid !== item.ext$.methodid;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame');
        // 这里必须先设置右侧数据再设置左侧数据
        _this.shuttleFrame.rightGrid.loadData(right);
        _this.shuttleFrame.options.rightOriginalData = Gikam.deepExtend(right);
        _this.shuttleFrame.setLeftData(filterLeftData);
    },

    // 获取当前方案下的所有数据
    getCurAllData : function(orignalLeftData, orignalRightData) {
        // 将右侧初始数据合并到左侧的初始数据上
        var allData = orignalLeftData;
        if (Gikam.isEmpty(allData)) {
            allData = orignalRightData;
        } else if (!Gikam.isEmpty(orignalRightData)) {
            // 将两个数组合并，要根据测试点、分析项组和方法去重
            orignalRightData.forEach(function(rightItem) {
                var exist = allData.some(function(item) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid === item.ext$.methodid && rightItem.itemGroup === item.itemGroup;
                });
                if (exist)
                    return;
                allData.push(rightItem);
            });
        }
        return allData;
    },

    confirm : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-order-item-group-edit-list-shuttle-frame').getSelectList();
        if (!rows.length) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_TEST');
            return;
        }

        var returnFlag = true;

        var newTestNameAndItemGroupNames = [];
        rows.some(function(item) {
            var newTestNameAndItemGroupName = item.ext$.testname + '&' + item.itemGroup;
            if (newTestNameAndItemGroupNames.indexOf(newTestNameAndItemGroupName) == -1) {
                newTestNameAndItemGroupNames.push(newTestNameAndItemGroupName);
            } else {
                returnFlag = false;
                Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CANNOT_CHOOSE_REITERATED_TEST');
                return true;
            }
        });

        if (returnFlag) {
            var chooseData = rows.map(function(item) {
                return {
                    schemeName : item.schemeName,
                    testName : item.ext$.testname,
                    testId : item.testId,
                    itemGroupId : item.itemGroupId,
                    itemGroup : item.itemGroup,
                    testMethodId : item.testMethodId,
                    methodId : item.ext$.methodid,
                    methodName : item.ext$.methodname,
                    methodVersionId : item.ext$.methodversionid,
                    testMethodBasis : item.ext$.testmethodbasis,
                    analysisOrgId : item.ext$.analysisorgid,
                    analysisOrgName : item.ext$.analysisorgname,
                    inputUserId : item.ext$.inputuserid,
                    inputUserName : item.ext$.inputusername,
                    sortNum : item.sortNum
                }
            });

            Gikam.getLastModal().close(chooseData);
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
        var _this = this;
        _this.showFlag = '1';
        _this.schemeName = 'default';
        // 右侧初始数据是否已被记录
        _this.rightDataHaveRecorded = false;
        // 右侧初始数据
        _this.shuttleFrameRightData = [];
        _this.rightInited = false;
        this.param = param;
        this.create();
    }

}
