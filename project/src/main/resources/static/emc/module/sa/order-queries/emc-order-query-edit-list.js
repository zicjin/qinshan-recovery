emcOrderQuery.editPage = {

    // 查询条件表单
    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 5,
            titleWidth : 80,
            autoSave : false,
            id : 'emc-order-query-edit-list-form',
            fields : emcOrderQuery.getQueryFormFields(),
            onRendered  : function() {
            	  Gikam.getComp('emc-order-query-edit-list-form').hideFields(['bizCategoryCode']) ;
            }
        }
    },

    // 样品列表
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-query-edit-list-grid',
            url : '',
            columns : emcOrderQuery.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-order-query-result-list-grid" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onOrderActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'preview',
                onClick : function() {
                    _this.queryData();
                }
            } ]
        }
    },

    // 基本信息表单
    getBaseInfoTab : function() {
        return {
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.SA.ORDERQUERIES.TAB.BASEINFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    titleWidth : 80,
                    autoSave : false,
                    id : 'emc-order-query-base-info-form',
                    fields : emcOrderQuery.getBaseInfoFormFields(),
                } ]
            } ]
        }
    },

    // 监测数据列表
    getResultDataTab : function() {
        return {
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.SA.ORDERQUERIES.TAB.RESULTDATA',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-query-result-list-grid',
                    url : '',
                    columns : emcOrderQuery.getResultListColumns(),
                }]
            } ]
        }
    },

    // 查询
    queryData : function() {
        var queryForm = Gikam.getComp('emc-order-query-edit-list-form');
        var formData = queryForm.options.data;
        workspace.window.showMask(true);
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/order-data', Gikam.getJsonWrapper({
            f : {
                SAMPLEBEGINDATE_DGOE : formData.beginDate ? formData.beginDate : undefined,
                SAMPLEBEGINDATE_DLOE : formData.endDate ? formData.endDate : undefined,
                BIZCATEGORYCODE_IN : formData.bizCategoryCode ? formData.bizCategoryCode : undefined,
                ORDERNO_SC : formData.orderNo ? formData.orderNo : undefined,
                ENTRUSTORGNAME_SC : formData.entrustOrgName ? formData.entrustOrgName : undefined,
                TESTORGNAME_SC : formData.testOrgName ? formData.testOrgName : undefined ,
                MONITORPOINTNAME_SC : formData.monitorPointName ? formData.monitorPointName : undefined,
                ORDERCATEGORY_IN : formData.sampleCategory ? formData.sampleCategory : undefined,
            }
        }, [ '', [] ])).done(function(data) {
            if (data && data.rows) {
                Gikam.getComp('emc-order-query-edit-list-grid').loadData(data.rows);
            }
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    onOrderActive : function(row) {
        // 基本信息赋值，使用 cleanData() 方法无法完全清除表单数据
        Gikam.getComp('emc-order-query-base-info-form').setData({
            ext$ : {
                testorgname : '',
                entrustorgname : '',
                orderstatus : '',
                monitorpointname : '',
                projno : '',
                sampleusername : ''
            }
        });
        var formData = JSON.parse(JSON.stringify(row));
        formData['ext$'].orderstatus = (row['ext$'].orderstatus === '1') ? Gikam.propI18N('T_EMC_ORDER.DONE') : Gikam.propI18N('T_EMC_ORDER.CHECKING');
        Gikam.getComp('emc-order-query-base-info-form').setData(formData);

        Gikam.getComp('emc-order-query-result-list-grid').refresh({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/queries/raw',
            requestData : {
                orderId_SEQ : row.id
            }
        })
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-order-query-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 100,
                items : [ _this.getFormParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getGridParam() ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            north : {
                                height : 180,
                                items : [ _this.getBaseInfoTab() ]
                            },
                            center : {
                                items : [ _this.getResultDataTab() ]
                            }
                        } ]
                    }
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
