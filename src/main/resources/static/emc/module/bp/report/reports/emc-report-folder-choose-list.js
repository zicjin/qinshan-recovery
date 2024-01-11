emcReport.folderChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcReport.getFolderEditListColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-report-folder-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries',
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            group : {
                fields : [ "orderCategory"],
                formatter : function(row) {
                    return row.orderCategory;
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    let folderIds = data.map(item => item.id).join();
                    let taskRequestData = Object.assign({}, requestData);
                    taskRequestData['folderId_IN'] = folderIds;
                    Gikam.getComp('emc-report-order-task-choose-list-grid').refresh({
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/unique-tasks/queries',
                        requestData : taskRequestData
                    })
                } else {
                    Gikam.getComp('emc-report-order-task-choose-list-grid').cleanData();
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let folders = Gikam.getComp('emc-report-folder-choose-list-grid').getSelections();
                    if (Gikam.isEmpty(folders)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    Gikam.getLastModal().close({
                        folders : folders,
                        orderTasks : Gikam.getComp('emc-report-order-task-choose-list-grid').getSelections()
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getOrderTaskGridParam : function() {
        var _this = this, columns = emcReport.getFolderOrderTaskColumns(), requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-report-order-task-choose-list-grid',
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            columnsFill : true,
            toolbar : []
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : '55%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getOrderTaskGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
