/**
 * 合同登记添加测试
 */
emcMonitorTmpl.schemeTestItemChoosePage = {

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
            id : 'emc-select-monitor-tmpl-show-all-form',
            titleWidthAuto : true,
            columns : 1,
            fields : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
                field : 'showFlag',
                width : 100,
                type : 'simpleCheckbox',
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
                onChange : function(field, value) {
                    _this.showFlag = value;
                    _this.findItemGroup();
                },
            } ]
        };
    },

    findItemGroup : function() {
        var _this = this;

        if (_this.showFlag === '1') {
            Gikam.post(emcMonitorTmpl.baseUrl + '/schemes/queries/choosable', Gikam.getJsonWrapper({
                f : {
                    schemeName_SEQ : _this.schemeName,
                    monitorTmplId_SEQ : _this.monitorTmplId,
                    activatedflag_SEQ : '1',
                },
                n : -1,
            })).done(function(data) {
                _this.setShuttleFrameData(data.rows);
            });
        } else {
            Gikam.post(emcMonitorTmpl.baseUrl + '/schemes/queries/choosable', Gikam.getJsonWrapper({
                f : {
                    schemeName_SEQ : _this.schemeName,
                    monitorTmplId_SEQ : _this.monitorTmplId,
                    defaultFlag_SEQ : '1',
                    activatedflag_SEQ : '1',
                },
                n : -1,
            })).done(function(data) {
                _this.setShuttleFrameData(data.rows);
            });
        }
    },

    getSelectForm : function() {
        var _this = this;

        return {
            type : 'form',
            id : 'emc-monitor-tmpl-scheme-item-group-choose-list-form',
            columns : 3,
            titleWidthAuto : true,
            fields : [ {
                title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUP',
                field : 'monitorTmplGroupId',
                type : 'select',
                category : 'monitorTmplGroup',
                onChange : function(field, value) {

                    _this.shuttleFrame = Gikam.getComp('emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame');
                    _this.setShuttleFrameData([]);

                    if (!Gikam.isEmpty(value)) {
                        var url = emcMonitorTmpl.baseUrl + '/queries/selectable' + Gikam.param({
                            monitorTmplGroupId : value
                        });

                        Gikam.getJson(url).done(function(items) {
                            _this.form.setSelectOptions('monitorTmplId', items.map(function(item) {
                                return {
                                    text : item.monitorTmplName,
                                    value : item.id
                                };
                            }), true);

                            if (items.length > 0) {
                                _this.form.setData({
                                    monitorTmplId : items[0].id
                                });
                            } else {
                                _this.form.setData({
                                    monitorTmplId : "",
                                    schemeName : ""
                                });
                            }
                        });

                    } else {
                        _this.form.setSelectOptions('monitorTmplId', []);
                        _this.form.setSelectOptions('schemeName', []);
                        _this.form.setData({
                            monitorTmplId : "",
                            schemeName : ""
                        });
                    }
                }
            }, {
                title : 'T_EMC_MPC_TMPL.MONITORTMPLNAME',
                field : 'monitorTmplId',
                type : 'select',
                onChange : function(field, value, index, row, text) {

                    if (!Gikam.isEmpty(value)) {

                        _this.monitorTmplId = value;
                        _this.monitorTmplName = text;

                        var url = emcMonitorTmpl.baseUrl + '/schemes/queries/selectable' + Gikam.param({
                            monitorTmplId : value
                        });

                        Gikam.getJson(url).done(function(items) {

                            _this.form.setSelectOptions('schemeName', items.map(function(item) {
                                return {
                                    text : item.schemeName,
                                    value : item.schemeName
                                };
                            }), true);

                            _this.findItemGroup();

                            if (items.length > 0) {
                                _this.form.setData({
                                    schemeName : "default"
                                });
                            } else {
                                _form.setData({
                                    schemeName : ""
                                });
                            }
                        });
                    }
                },
            }, {
                title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
                field : 'schemeName',
                type : 'select',
                onChange : function(field, value) {
                    _this.schemeName = value;
                    _this.findItemGroup();
                },
            } ],
            onRendered : function() {
                _this.form = this;
            }

        };
    },

    confirm : function() {
        var _this = this;
        var formData = Gikam.getComp('emc-monitor-tmpl-scheme-item-group-choose-list-form').getData();
        var rows = Gikam.getComp('emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame').getSelectList();
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
                    schemeName : _this.schemeName,
                    testName : item.ext$.testname,
                    testId : item.testId,
                    monitorTmplId : _this.monitorTmplId,
                    monitorTmplName : _this.monitorTmplName,
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
                }
            });

            Gikam.getLastModal().close(chooseData);
        }
    },

    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame',
            columns : emcMonitorTmpl.getItemGroupShuttleFrameColumns(),
            rightPage : false,
            leftPage : false,
            onAddClick : function(data, left, right) {
                _this.setFormEditStatus(right);
                _this.filterSameAdd(data, left, right);
            },
            onDelClick : function(data, left, right) {
                _this.setFormEditStatus(right);
                _this.filterSameDel(data, left, right);
            },
            onRendered : function() {
                _this.shuttleFrame = this;
            }
        }
    },

    setFormEditStatus : function(rightData) {
        var _this = this;

        if (rightData.length > 0) {
            _this.form.toFieldsReadonly([ 'monitorTmplGroupId', 'monitorTmplId' ]);
        } else {
            _this.form.toFieldsEdit([ 'monitorTmplGroupId', 'monitorTmplId' ]);
        }

    },

    // 设置初始数据
    setShuttleFrameData : function(leftData) {
        var _this = this;
        _this.shuttleFrame = Gikam.getComp('emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame');
        // 左边初始数据
        _this.shuttleFrameLeftData = leftData;
        // 右边初始数据
        _this.shuttleFrameRightData = _this.shuttleFrame.rightGrid.getData();
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
                //如果是同一条数据，则不显示
                var isSameData = right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid  === item.ext$.methodid  && rightItem.itemGroup === item.itemGroup;
                });
                if(isSameData) {
                    return false;
                }
                //否则就要与右侧数据比较去重
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid  !== item.ext$.methodid ;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame');
        _this.shuttleFrame.rightGrid.loadData(right);
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
                    return rightItem.testId === item.testId && rightItem.ext$.methodid  !== item.ext$.methodid ;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame');
        // 这里必须先设置右侧数据再设置左侧数据
        _this.shuttleFrame.rightGrid.loadData(right);
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
                //如果是取消选择的数据，则显示
                var isDelData = right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid === item.ext$.methodid && rightItem.itemGroup === item.itemGroup;
                });
                if(isDelData) {
                    return true;
                }
                //否则就要与右侧数据比较去重
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid !== item.ext$.methodid;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-monitor-tmpl-order-task-addition-item-group-choose-mult-list-shuttle-frame');
        // 这里必须先设置右侧数据再设置左侧数据
        _this.shuttleFrame.rightGrid.loadData(right);
        _this.shuttleFrame.setLeftData(filterLeftData);
    },

    //获取当前方案下的所有数据
    getCurAllData : function(orignalLeftData, orignalRightData) {
        // 将右侧初始数据合并到左侧的初始数据上
        var allData = orignalLeftData;
        if(Gikam.isEmpty(allData)) {
            allData = orignalRightData;
        } else if(!Gikam.isEmpty(orignalRightData)) {
            // 将两个数组合并，要根据测试点、分析项组和方法去重
            orignalRightData.forEach(function(rightItem) {
                var exist = allData.some(function(item) {
                    return rightItem.testId === item.testId && rightItem.ext$.methodid === item.ext$.methodid && rightItem.itemGroup === item.itemGroup;
                });
                if(exist)
                    return;
                allData.push(rightItem);
            });
        }
        return allData;
    },

    create : function() {
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 54,
                items : [ {
                    type : 'layout',
                    west : {
                        width : 95,
                        items : [ this.getShowAllForm() ]
                    },
                    east : {
                        width : 120,
                        items : [ this.getConfirmBtnItem()]
                    },
                    center : {
                        items : [ this.getSelectForm() ]
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
        _this.showFlag = '0';
        // 右侧初始数据
        _this.shuttleFrameRightData = [];
        this.param = param;
        this.create();
    },

}
