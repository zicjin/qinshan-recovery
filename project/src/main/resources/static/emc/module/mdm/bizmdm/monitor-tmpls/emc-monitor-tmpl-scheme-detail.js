emcMonitorTmpl.schemeDetailPage = {

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


    getBaseInfoTab : function() {
        var _this = this;
        var fields = emcMonitorTmpl.getEditSchemeFormFields();
        if (_this.param.schemeName !== 'default') {
            fields.shift();
        }

        return {
            type : 'form',
            id : 'emc-monitor-tmpl-scheme-detail-form',
            columns : 3,
            fields : fields,
            titleWidth : 80,
            onRendered : function() {
                _this.schemeForm = this;
                this.setData(_this.param);
                this.toFieldsReadonly('schemeName');
            }
        }
    },

    save : function() {
        var _this = this;
        var formData = Gikam.getComp('emc-monitor-tmpl-scheme-detail-form').getData();
        var rows = _this.rightSelectedData;
        if (!_this.schemeForm.validate()) {
            return;
        }
        if (!rows.length) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_TEST');
            return;
        }

        var chooseData;
        if (_this.param.schemeName === 'default') {
            chooseData = rows.map(function(itemGroup) {
                return {
                    testMethodId : itemGroup.testMethodId,
                    testId : itemGroup.testId,
                    methodId : itemGroup.ext$.methodid,
                    itemGroupId : itemGroup.id,
                    itemGroup : itemGroup.itemGroup,
                    schemeName : formData.schemeName,
                    monitorTmplId : _this.param.monitorTmplId,
                    ext$ :{
                        defaultflag : itemGroup.ext$.defaultflag,
                    }
                }
            });
        } else {
            chooseData = rows.map(function(itemGroup) {
                return {
                    testMethodId : itemGroup.testMethodId,
                    testId : itemGroup.testId,
                    methodId : itemGroup.ext$.methodid,
                    itemGroupId : itemGroup.itemGroupId,
                    itemGroup : itemGroup.itemGroup,
                    schemeName : formData.schemeName,
                    monitorTmplId : _this.param.monitorTmplId,
                    ext$ :{
                        defaultflag : itemGroup.ext$.defaultflag,
                    }
                }
            });
        }

        Gikam.getLastModal().window.showMask(true);
        Gikam.postText(Gikam.printf(emcMonitorTmpl.baseUrl + '/{id}/schemes/{schemeName}/action/edit', {
            id : _this.param.monitorTmplId,
            schemeName : _this.param.schemeName,
        }), Gikam.getJsonWrapper(null, [ '', chooseData ])).done(function(id) {
            Gikam.getLastModal().close();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });

    },

    getShuttleFrameParam : function() {
        var _this = this;
        var columns = emcMonitorTmpl.getItemGroupShuttleFrameColumns();

        var leftRequestData = {
            org_filter : '1',
            target_filter : JSON.stringify({
                type : 'unassigned',
                targetTable : "T_EMC_MONITOR_SCHEME",
                targetMatchColumn : "ITEMGROUPID",
                filter : [ {
                    targetFilterColumn : 'schemeName',
                    targetFilterValue : 'default'
                }, {
                    targetFilterColumn : 'monitorTmplId',
                    targetFilterValue : _this.param.monitorTmplId
                } ]
            })
        };

        var rightRequestData = {
            org_filter : '1',
            target_filter : JSON.stringify({
                type : 'assigned',
                targetTable : "T_EMC_MONITOR_SCHEME",
                targetMatchColumn : "ITEMGROUPID",
                filter : [ {
                    targetFilterColumn : 'schemeName',
                    targetFilterValue : _this.param.schemeName
                }, {
                    targetFilterColumn : 'monitorTmplId',
                    targetFilterValue : _this.param.monitorTmplId
                } ]
            })
        };

        //default 默认进入左侧不加载数据
        // var leftGridUrl = '/secure/emc/module/mdm/bizmdm/test/test-capacitys/item-groups/queries/choosable';
        var leftGridUrl = '';
        var rightGridUrl = '/secure/emc/module/mdm/bizmdm/test/test-capacitys/item-groups/queries/choosable';
        if (_this.param.schemeName !== 'default') {
            leftGridUrl = emcMonitorTmpl.baseUrl + '/schemes/queries/choosable';
            rightGridUrl = emcMonitorTmpl.baseUrl + '/schemes/queries/choosable';
            leftRequestData = {
                schemeName_SEQ : 'default',
                monitorTmplId_SEQ : _this.param.monitorTmplId,
                org_filter : '1',
                target_filter : JSON.stringify({
                    type : 'unassigned',
                    targetTable : "T_EMC_MONITOR_SCHEME",
                    targetMatchColumn : "ITEMGROUPID",
                    thisMatchColumn : "ITEMGROUPID",
                    filter : [ {
                        targetFilterColumn : 'schemeName',
                        targetFilterValue : _this.param.schemeName
                    }, {
                        targetFilterColumn : 'monitorTmplId',
                        targetFilterValue : _this.param.monitorTmplId
                    } ]

                })
            };

            rightRequestData = {
                schemeName_SEQ : _this.param.schemeName,
                monitorTmplId_SEQ : _this.param.monitorTmplId,
                org_filter : '1',
            };
        }


        return {
            type : 'shuttleFrame',
            id : 'emc-monitor-tmpl-scheme-addition-scheme-choose-mult-list-shuttle-frame',
            columns : columns,
            leftGridUrl : leftGridUrl,
            leftRequestData : leftRequestData,
            rightPage : false,
            leftPage : false,
            initRequest : false,
            onLeftLoadSuccess : function() {
                // 当左侧数据加载完成后，再去初始化右侧的数据，通过rightInited标志位避免右侧数据重复加载
                if (!_this.rightInited) {
                    _this.shuttleFrame = Gikam.getComp('emc-monitor-tmpl-scheme-addition-scheme-choose-mult-list-shuttle-frame');
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
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 55,
                items : [ {
                    type : 'layout',
                    east : {
                        width : 150,
                        items : [ _this.getConfirmBtnItem() ]
                    },
                    center : {
                        items : [ _this.getBaseInfoTab() ]
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
    }

}
