crmOpportunityContract.orderChoosePage = {

    // 样品和项目标签组件
    getContractOrderAndTaskConfig: function () {
        var _this = this;
        return {
            type: 'layout',
            north: {
                // 样品列表
                height: '50%',
                items: [_this.getOrderGridConfig()]
            },
            center: {
                // 检验项目列表
                items: [_this.getOrderTaskGridConfig()]
            }
        }
    },

    // 样品列表
    getOrderGridConfig: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'crm-opportunity-contract-order-grid',
            service: 'crmOpportunityOrdersServiceImpl',
            filterOpen: false,
            filter: true,
            page: false,
            url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/orders/queries',
            requestData: _this.param,
            order : {
                'testType' : 'asc',
                'id' : 'desc'
            },
            group : {
                fields : [ "testType"],
                formatter : function(row) {
                    return  "检品类型 ：" + row.testType
                }
            },
            columns: crmOpportunityContract.getHjOrderGridColumns(),
            onRowActive: function (index, row) {
                Gikam.getComp('crm-opportunity-contract-order-task-grid').refresh({
                    url : IFM_CONTEXT + '/secure/emc/module/mdm/crm/ordtasks/queries',
                    requestData: {
                        ordersId_SEQ: row.id
                    }
                })
            },
            onLoadSuccess: function (data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData(['crm-opportunity-contract-order-task-grid']);
                } else {
                    this.activeRowByIndex(1);
                }
            },
            toolbar : [ {
                type : 'form',
                id : 'emc-opportunity-biz-category-form',
                titleWidthAuto : false,
                titleWidth : 80,
                fields : [ {
                    title : '委托类别',
                    field : 'bizCategoryId',
                    type : 'select',
                    firstBlank : false,
                    selectedIndex : 0,
                    category : 'bizCategory',
                    param : {
                        bizCategoryId_IN : _this.param.bizCategoryCode
                    },
                    validators : ['notEmpty'],
                    refreshGrid : false
                }, {
                    field : 'sampleSource',
                    title : '样品来源',
                    type : 'select',
                    firstBlank : false,
                    category : 'sys',
                    value : _this.param.sampleSource.indexOf(',') > 0 ? '' : _this.param.sampleSource ,
                    param : {
                        codeCategoryId : 'sampleSource',
                    },
                    validators : ['notEmpty'],
                    refreshGrid : false
                } ]
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let form = Gikam.getComp('emc-opportunity-biz-category-form');
                    if (!form.validate()) {
                        return;
                    }

                    let orders = Gikam.getComp('crm-opportunity-contract-order-grid').getSelections();
                    if (Gikam.isEmpty(orders)) {
                        Gikam.alert('请至少选择一个点位');
                        return;
                    }
                    let orderTasks = Gikam.getComp('crm-opportunity-contract-order-task-grid').getSelections();
                    if (orders.length === 1 && Gikam.isEmpty(orderTasks)) {
                        Gikam.alert('请至少选择一个测试');
                        return;
                    }

                    let formData = form.getData();
                    Gikam.getLastModal().close({
                        bizCategoryId : formData.bizCategoryId,
                        sampleSource : formData.sampleSource,
                        orders : orders,
                        orderTasks : orderTasks
                    });
                }
            } ]
        }
    },

    // 检验项目列表
    getOrderTaskGridConfig: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'crm-opportunity-contract-order-task-grid',
            service: 'crmOpportunityOrdtaskServiceImpl',
            columns: crmOpportunityContract.getContractHjOrderTaskGridColumns(),
            filterOpen: false,
            filter: false,
            page: false,
            order : {
                groupfields : 'desc',
            },
            readonly: true,
            group : {
                fields : [ "ext$.groupfields"],
                formatter : function(row) {
                    if(row.schemeName === 'default'){
                        return  "项目 ：" + row.testNo
                    }else{
                        return  "项目 ：" + row.schemeName
                    }
                }
            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-opportunity-order-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getContractOrderAndTaskConfig() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
