emcOrder.checkProjecTestAddPage = {

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

    getSelectForm : function() {
        var _this = this;

        return {
            type : 'form',
            id : 'emc-check-project-order-item-group-addition-form',
            columns : 1,
            titleWidth : 65,
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
                    var leftGrid = Gikam.getComp('emc-check-project-order-item-group-addition-shuttle-frame').leftGrid;

                    leftGrid.setOptions({
                        requestData : {
                            schemeName_SEQ : value,
                            testmethodflag_SEQ :'1'
                        }
                    });

                    leftGrid.refresh();

                }
            }, ],
            onRendered : function() {
                _this.form = this;
            }

        };
    },

    confirm : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-check-project-order-item-group-addition-shuttle-frame').getSelectList();
        var selectionsOrder = Gikam.getComp('emc-check-project-edit-list-order-grid').getSelections();
        if (!rows.length) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_TEST');
            return;
        }
        var newTestNameAndItemGroupNames = [];
        rows.some(function(item) {
            var newTestNameAndItemGroupName = item.ext$.testname + '&' + item.itemGroup;

            if (newTestNameAndItemGroupNames.indexOf(newTestNameAndItemGroupName) === -1) {
                newTestNameAndItemGroupNames.push(newTestNameAndItemGroupName);
            } else {
                Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CANNOT_CHOOSE_REITERATED_TEST');
                return true;
            }
        });

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
            }
        });

        Gikam.getLastModal().window.showMask(true);
        Gikam.put(Gikam.printf(emcOrder.baseUrl + '/{id}/check-projects/action/item-group-add', {
            id : _this.param.orderId
        }), Gikam.getJsonWrapper({}, [ '', chooseData ],['pfOrderServiceImpl',selectionsOrder])).done(function() {
            Gikam.getLastModal().close();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });

    },

    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-check-project-order-item-group-addition-shuttle-frame',
            columns : emcOrder.getItemGroupShuttleFrameColumns(),
            leftGridUrl : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
            leftRequestData : {
                order_task_unassigned : '1',
                orderId : _this.param.orderId,
                monitorTmplId_SEQ : _this.param.monitorTmplId,
                activatedflag_SEQ : '1',
            },
            rightPage : false,
            leftPage : false,
            onRendered : function() {
                _this.shuttleFrame = this;
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
                        width : 280,
                        items : [ this.getSelectForm()]
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
        this.create();
    },

}
