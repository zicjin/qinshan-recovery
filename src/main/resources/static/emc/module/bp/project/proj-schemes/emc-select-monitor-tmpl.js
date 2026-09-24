/**
 * 方案编制添加测试
 */

emcProjScheme.selectTmplPage = {

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
            hidden : _this.param.editMonitorInfo === '1',
            icon : 'back',
            margin : '13px 0 0 15px',
            onClick : function() {
                Gikam.getLastModal().close('PREVIOUS');
            }
        }
    },

    confirm : function() {
        var formData = Gikam.getComp('emc-select-monitor-tmpl-form').getData();
        var gridData = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame').getSelectList();
        if (Gikam.isEmpty(formData.monitorSchemeId_SEQ)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_SCHEME');
            return;
        }
        if (gridData.length > 0) {

            var returnFlag = true;

            // var newTestNameAndItemGroupNames = [];
            // gridData.some(function(item) {
            //     var newTestNameAndItemGroupName = item.ext$.testname + '&' + item.itemGroup;
            //     if (newTestNameAndItemGroupNames.indexOf(newTestNameAndItemGroupName) === -1) {
            //         newTestNameAndItemGroupNames.push(newTestNameAndItemGroupName);
            //     } else {
            //         returnFlag = false;
            //         Gikam.alert(Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CANNOT_CHOOSE_REITERATED_TEST') + ':' + '测试名称('+item.ext$.testname+'),'+ '分析项组名称('+item.itemGroup+')');
            //         return true;
            //     }
            // });
            var newItemGroupNames = [];
            gridData.some(function(item) {
                var newItemGroupName = item.itemGroup;
                if (newItemGroupNames.indexOf(newItemGroupName) === -1) {
                    newItemGroupNames.push(newItemGroupName);
                } else {
                    returnFlag = false;
                    Gikam.alert(Gikam.propI18N('不能选择重复分析项组') + ': 分析项组名称('+item.itemGroup+')');
                    return true;
                }
            });
            if (returnFlag) {
                var chooseData = gridData.map(function(item) {
                    return {
                        monitorTmplId : formData.monitorTmplId_SEQ,
                        testId : item.testId,
                        methodId : item.methodId  ? item.methodId : item.ext$.methodid,
                        itemGroupId : item.itemGroupId,
                        itemGroup : item.itemGroup,
                        schemeName : item.schemeName,
                        sortNum : item.sortNum
                        //schemeName : formData.monitorSchemeId_SEQ
                    }
                });
                Gikam.getLastModal().close(chooseData);
            }
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
            fields : emcProjScheme.getFilterMonitorTmplFormFields(),
        };
    },

    getShowAllForm : function() {
         return {
            type : 'form',
            autoSave : false,
            titleWidthAuto : true,
            columns : 1,
            fields : emcProjScheme.getShowAllFormFields()
         }
    },

    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-select-monitor-tmpl-list-shuttle-frame',
            columns : emcProjScheme.getMonitorTmplListColumns(),
            rightPage : false,
            leftPage : false,
            leftGridUrl : '',
            initRequest : false,
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
        _this.shuttleFrame.options.rightOriginalData = Gikam.deepExtend(right);
        _this.shuttleFrame.setLeftData(filterLeftData);
    },

    // 选择
    // data变动数据，left变动后左侧数据，right变动后右侧数据
    // 左侧数据，如果与右侧数据同测试名、不同方法，则不显示；
    filterSameAdd : function(data, left, right) {
        // 添加时，如果选择了重复测试，则弹出提示信息
        let originRightData = right.filter(item => data.every(d => d.id !== item.id));
        let repeatTestNames = data.filter(item => originRightData.some(rightItem => item.testId === rightItem.testId && item.itemGroup === rightItem.itemGroup)).map(item => item.itemGroup);
        if (repeatTestNames.length > 0) {
            Gikam.alert(Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CANNOT_CHOOSE_REITERATED_TEST') + ': ' + repeatTestNames.join());
        }
        let repeatItemGroupNames = data.filter(item => originRightData.some(rightItem => item.itemGroup === rightItem.itemGroup)).map(item => item.itemGroup);
        if (repeatItemGroupNames.length > 0) {
            Gikam.alert(Gikam.propI18N('不能选择重复分析项组') + ': ' + repeatItemGroupNames.join());
        }
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

        // 设置排序码
        let sortNum = new Date().getTime();
        let addIds = data.map(item => item.id);
        right.filter(item => addIds.indexOf(item.id) >= 0).forEach(item => item.sortNum = sortNum);

        _this.shuttleFrame = Gikam.getComp('emc-select-monitor-tmpl-list-shuttle-frame');
        // 这里必须先设置右侧数据再设置左侧数据
        _this.shuttleFrame.rightGrid.loadData(right);
        //备注:穿梭框升级后不需要这两行
        /*_this.shuttleFrame.options.rightOriginalData = Gikam.deepExtend(right);
        _this.shuttleFrame.setLeftData(filterLeftData);*/
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
          //备注:穿梭框升级后不需要这两行
/*        _this.shuttleFrame.options.rightOriginalData = Gikam.deepExtend(right);
*//*        _this.shuttleFrame.setLeftData(filterLeftData);
*/    },

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
                    west : {
                        width : 120,
                        items : [ this.getShowAllForm() ]
                    },
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
