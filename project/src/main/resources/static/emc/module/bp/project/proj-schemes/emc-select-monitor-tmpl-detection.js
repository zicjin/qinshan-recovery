/**
 * 方案编制添加测试
 */

emcProjScheme.selectTmplDetectionPage = {

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

    getPreviousBtnItem : function() {
        var _this = this;
        return {
            type : 'button',
            text : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.BUTTON.PREVIOUS',
            hidden : typeof(emcProject) != 'undefined'?emcProject.editPage.param.editMonitorInfo === '1':'true',
            icon : 'back',
            margin : '13px 0 0 15px',
            onClick : function() {
                Gikam.getLastModal().close('PREVIOUS');
            }
        }
    },

    confirm : function() {
        var gridData = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame').getSelectList();
        if (gridData.length > 0) {
            var chooseData = gridData.map(function(item) {
                return {
                    methodName : item.ext$.methodname,
                    testName : item.ext$.testname,
                    methodId : item.ext$.methodid,
                    testId : item.testId,
                    monitorTmplGroupId : item.ext$.monitortmplgroupid,
                    monitorTmplGroupName : item.ext$.monitortmplgroupname,
                    itemGroupId : item.itemGroupId,
                    itemGroupName : item.ext$.itemgroupname,
                    trialScope : item.ext$.trialscope
                }
            });
            Gikam.getLastModal().close(chooseData);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-select-monitor-tmpl-form',
            titleWidthAuto : true,
            columns : 3,
            fields : emcProjScheme.getFilterMonitorTmplDetectionFormFields(),
        };
    },


    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-select-monitor-tmpl-list-shuttle-frame',
            columns : emcProjScheme.getMonitorTmplDetectionListColumns(),
            rightPage : false,
            leftPage : false,
            leftGridUrl : '',
            onAddClick : function(data, left, right) {
                _this.setFormEditStatus(right);
                _this.filterSameAdd(data, left, right);
            },
            onDelClick : function(data, left, right) {
                _this.setFormEditStatus(right);
                _this.filterSameDel(data, left, right);
            },
        }
    },

    setFormEditStatus : function(rightData) {
        var _this = this;

        var form = Gikam.getComp('emc-select-monitor-tmpl-form');

        if (rightData.length > 0) {
            form.toFieldsReadonly([ 'monitorTmplGroupId_SEQ', 'monitorTmplId_SEQ' ]);
        } else {
            form.toFieldsEdit([ 'monitorTmplGroupId_SEQ', 'monitorTmplId_SEQ' ]);
        }

    },

    // 设置初始数据
    setShuttleFrameData : function(leftData) {
        var _this = this;
        _this.shuttleFrame = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame');
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
                    return rightItem.testId === item.testId && rightItem.methodId === item.methodId && rightItem.itemGroup === item.itemGroup;
                });
                if(isSameData) {
                    return false;
                }
                //否则就要与右侧数据比较去重
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.methodId !== item.methodId;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame');
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
                    return rightItem.testId === item.testId && rightItem.methodId !== item.methodId;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame');
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
                    return rightItem.testId === item.testId && rightItem.methodId === item.methodId && rightItem.itemGroup === item.itemGroup;
                });
                if(isDelData) {
                    return true;
                }
                //否则就要与右侧数据比较去重
                return !right.some(function(rightItem) {
                    return rightItem.testId === item.testId && rightItem.methodId !== item.methodId;
                });
            });
        }

        _this.shuttleFrame = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame');
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
                    return rightItem.testId === item.testId && rightItem.methodId === item.methodId && rightItem.itemGroup === item.itemGroup;
                });
                if(exist)
                    return;
                allData.push(rightItem);
            });
        }
        return allData;
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-select-monitor-tmpl-layout',
            north : {
                height : '54px',
                items : [ {
                    type : 'layout',
                    east : {
                        width : 190,
                        items : [ this.getConfirmBtnItem(), this.getPreviousBtnItem() ]
                    },
                    center : {
                        items : [ this.getBaseInfoForm() ]
                    }
                } ]
            },
            center : {
                items : [ this.getShuttleFrameParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        var _this = this;
        _this.showFlag = '0';
        // 右侧初始数据
        _this.shuttleFrameRightData = [];
        _this.param = param;
        _this.createPage();
    }
}
