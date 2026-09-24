emcOrder.checkProjecItemGroupEditPage = {

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

    confirm : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-check-project-order-item-group-edit-add-grid').getData();
        var delData = Gikam.getComp('emc-check-project-order-item-group-edit-del-grid').getData();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.getLastModal().window.showMask(true);
        Gikam.put(Gikam.printf(emcOrder.baseUrl + '/{id}/check-projects/action/item-group-edit', {
            id: _this.param.orderId,
        }), Gikam.getJsonWrapper({delCapacityItemGroupId: delData[0].id}, ['emcMonitorSchemeServiceImpl', selections]
        )).done(function () {
            Gikam.getLastModal().close();
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });
    },

    getDelItemGroupGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-check-project-order-item-group-edit-del-grid',
            columns : emcOrder.getItemGroupEditDelItemGroupColumns(),
            url : '/secure/emc/module/mdm/bizmdm/test/test-capacitys/item-groups/queries/choosable',
            requestData : {
                id_seq : _this.param.itemGroupId,
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-check-project-order-item-group-edit-del-item-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-check-project-order-item-group-edit-del-item-grid').done(function(grid) {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries',
                        requestData : {
                            orderId_seq : _this.param.orderId,
                            testMethodId_seq : _this.param.testMethodId,
                            analysisType_SEQ : 'sampling',
                        },
                    });
                });
            },
        }
    },

    getDelGroupItemGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-check-project-order-item-group-edit-del-item-grid',
            columns : emcOrder.getItemGroupEditDelItemColumns(),
        }
    },

    getAddItemGroupGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-check-project-order-item-group-edit-add-grid',
            columns : emcOrder.getItemGroupEditAddItemGroupColumns(),
            // url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/choosable',
            // requestData : {
            //     monitorTmplId_SEQ : _this.param.monitorTmplId,
            //     activatedflag_SEQ : '1',
            //     schemeName_seq : 'default'
            // },
            // page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-check-project-order-item-group-edit-add-item-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-check-project-order-item-group-edit-add-item-grid').done(function(grid) {
                    grid.refresh({
                        url : '/secure/emc/module/mdm/bizmdm/test/test-capacitys/capacity-items/queries',
                        requestData : {
                            capItemGroupId_SEQ : row.itemGroupId,
                            analysisCategory_SEQ : 'sampling',
                        },
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : '选择测试',
                icon : 'edit',
                onClick : function() {
                    _this.addItemGroup();
                },
            },  ]
        }
    },

    addItemGroup : function() {
        var _this = this;

        Gikam.create('modal', {
            title : '测试方法选择',
            width : '60%',
            height : '60%',
            url : Gikam.choose.getConfig('monitorScheme').url + Gikam.param({
                monitorTmplId_SEQ : _this.param.monitorTmplId,
                activatedflag_SEQ : '1',
                schemeName_seq : 'default'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var grid = Gikam.getComp('emc-check-project-order-item-group-edit-add-grid');
                var oldData = grid.getData();

                Gikam.jQuery.map(rows, function(rowData, index) {
                    oldData.push(rowData);
                });
                grid.refreshByData({
                    rows : _this.unique(oldData)
                });
            }
        });
    },

    unique : function(arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i].id]) {
                res.push(arr[i]);
                json[arr[i].id] = 1;
            }
        }
        return res;
    },

    getAddGroupItemGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-check-project-order-item-group-edit-add-item-grid',
            columns : emcOrder.getItemGroupEditAddItemColumns(),
        }
    },

    create : function() {
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 52,
                items : [ this.getConfirmBtnItem() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ {
                            type : 'layout',
                            north : {
                                height : '50%',
                                items : [ this.getDelItemGroupGrid()]
                            },
                            center : {
                                items : [ this.getDelGroupItemGrid() ]
                            }
                        }]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            north : {
                                height : '50%',
                                items : [ this.getAddItemGroupGrid()]
                            },
                            center : {
                                items : [ this.getAddGroupItemGrid() ]
                            }
                        } ]
                    }
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

}
