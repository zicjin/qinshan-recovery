/**
 * 方案编制修改测试
 */
emcProjScheme.updateItemGroupPage = {

    getConfirmBtnItem : function() {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.CONFIRM',
            icon : 'select',
            margin : '13px 0 0 15px',
            onClick : function () {
                _this.save();
            }
        }
    },

    getShowAllForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-project-order-addition-show-all-form',
            titleWidthAuto : true,
            columns : 1,
            fields : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
                field : 'showFlag',
                type : 'simpleCheckbox',
                styleFormatter : function() {
                    return {
                        paddingLeft : '0'
                    }
                },
                onChange : function(field, value) {
                    var _form = this;
                    var schemeName = _form.getData().schemeName;
                    _this.showFlag = value;

                    // 显示全部或者显示默认的
                    var defaultFlag = (value === '1') ? undefined : '1';
                    _this.loadShuttleFrameData(schemeName, _this.param.monitorTmplId, defaultFlag);
                }
            } ]
        };
    },

    getSelectForm : function() {
        var _this = this;
        return {
            titleWidthAuto : true,
            type : 'form',
            id : 'emc-project-order-addition-selection-form',
            columns : 1,
            fields : [ {
                title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
                field : 'schemeName',
                category : 'monitorScheme',
                param : {
                    monitorTmplId : _this.param.monitorTmplId,
                    enableflag :'1',
                },
                type : 'select',
                search : true,
                onChange : function() {
                    var _form = this;
                    var schemeName = _form.getData().schemeName;
                    var defaultFlag = (_this.showFlag === '1') ? undefined : '1';

                    _this.loadShuttleFrameData(schemeName, _this.param.monitorTmplId, defaultFlag);
                },
            } ],
            onRendered : function() {
                var _form = this;
                _form.setData({
                    schemeName : 'default',
                });
            }
        };
    },

    getShuttleFrameParam : function() {
        var _this = this;

        return {
            type : 'shuttleFrame',
            id : 'emc-update-item-group-shuttle-frame',
            columns : emcProjScheme.getMonitorTmplListColumns(),
            delRepeatField : 'itemGroupId',
            page : true,
            onAddClick : function(data, left, right) {
                let sortNum = new Date().getTime();
                data.forEach(item => item.sortNum = sortNum);
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

    loadShuttleFrameData : function(schemeName, monitorTmplId, defaultFlag) {
        var _this = this;
        var leftGridUrl = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable';

        if (!_this.rightInited) {
            var rightGridUrl = Gikam.printf(emcProjScheme.baseUrl + '/{id}/proj-orders/queries', {
                id : _this.param.projSchemeId
            });
            _this.shuttleFrame = Gikam.getComp('emc-update-item-group-shuttle-frame');
            Gikam.post(rightGridUrl, Gikam.getJsonWrapper({
                f : {},
                n : -1,
            })).done(function (rightData) {
                _this.shuttleFrame.setRightData(rightData.rows);
                _this.rightSelectedData = rightData.rows;
                _this.rightInited = true;
            })
        }

        Gikam.post(leftGridUrl, Gikam.getJsonWrapper({
            f : {
                schemeName_SEQ : schemeName,
                monitorTmplId_SEQ : monitorTmplId,
                defaultFlag_SEQ : defaultFlag,
                activatedflag_SEQ : '1',
                testmethodflag_SEQ :'1',
            },
            n : -1,
        })).done(function(row) {
            _this.setShuttleFrameData(row.rows);
        });
    },

    save : function() {
        var _this = this;
        var rows = _this.rightSelectedData;

        if (!rows.length) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_TEST');
            return;
        }

        var tests = rows.map(item => (item.testId + item.itemGroup));
        var flag = {};
        for (let index in tests) {
            if (flag[tests[index]]) {
                let tip = Gikam.propI18N('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CANNOT_CHOOSE_REITERATED_TEST');
                Gikam.alert(tip + ' : ' + rows[index].ext$.testname);
                return;
            }
            flag[tests[index]] = true;
        }

        var returnData = rows.map(function(item) {
            return {
                testId : item.testId,
                methodId : item.methodId ? item.methodId : item.ext$.methodid,
                itemGroupId : item.itemGroupId,
                itemGroup : item.itemGroup,
                schemeName : item.schemeName,
                sortNum : item.sortNum
            }
        });

        Gikam.getLastModal().close(returnData);
    },

    // 设置初始数据
    setShuttleFrameData : function(leftData) {
        var _this = this;
        var leftGridData = leftData.filter(item => {
            return !_this.rightSelectedData.some(selectedItem => {
                return selectedItem.testId === item.testId && selectedItem.methodId !== item.methodId && selectedItem.itemGroupId === item.itemGroupId
            })
        });

        _this.shuttleFrame.setLeftData(leftGridData);
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
        _this.showFlag = '0';
        this.rightSelectedData = [];
        this.rightInited = false;
        this.param = param;
        this.create();
    }

}

