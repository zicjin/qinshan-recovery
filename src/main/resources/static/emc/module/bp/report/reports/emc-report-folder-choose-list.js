emcReport.folderChoosePage = {

    getGridParam: function () {
        this.param.orderDate_filter = '1';
        var _this = this, columns = emcReport.getFolderEditListColumns();
        var requestData = {};
        requestData.TERMINATION_SEQ = '0';
        requestData.projId_SEQ = _this.param.projId_SEQ;
        requestData.targetType = _this.param.targetType;
        requestData.orderDate_filter = '1';
        return {
            type: 'grid',
            id: 'emc-report-folder-choose-list-grid',
            url: IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries',
            requestData: requestData,
            columns: columns,
            filterOpen: true,
            group: {
                fields: ["orderCategory"],
                formatter: function (row) {
                    return row.orderCategory;
                }
            },
            order: {
                'orderno': 'asc',
            },
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    let folderIds = data.map(item => item.id).join();
                    let taskRequestData = Object.assign({}, _this.param);
                    taskRequestData['folderId_IN'] = folderIds;
                    Gikam.getComp('emc-report-order-task-choose-list-grid').refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/unique-tasks/queries',
                        requestData: taskRequestData
                    })
                } else {
                    Gikam.getComp('emc-report-order-task-choose-list-grid').cleanData();
                }
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.CONFIRM',
                icon: 'select',
                onClick: function () {
                    let folders = Gikam.getComp('emc-report-folder-choose-list-grid').getSelections();
                    var proj = Gikam.getComp('emc-report-edit-project-list-grid').getActivedRow();
                    if (Gikam.isEmpty(folders)) {
                        Gikam.confirm('确认', '当前未选择点位，是否确认生成报告？', function () {
                            workspace.window.showMask(true);
                            debugger;
                            Gikam.postText(emcReport.baseUrl + '/action/create', Gikam.getJsonWrapper({"projid": proj.id}, ['emcFolderServiceImpl', []], ['emcOrderTaskServiceImpl', Gikam.getComp('emc-report-order-task-choose-list-grid').getSelections()])).done(function () {
                                Gikam.getComp('emc-report-edit-list-grid').refresh();
                            }).always(function () {
                                workspace.window.closeMask();
                                Gikam.getLastModal().close();
                            });
                        });

                    } else {
                        Gikam.getLastModal().close({
                            folders: folders,
                            orderTasks: Gikam.getComp('emc-report-order-task-choose-list-grid').getSelections()
                        });
                    }
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.CANCEL',
                icon: 'cancel',
                onClick: function () {
                    Gikam.getLastModal().close();
                }
            }, {
                type: 'form',
                autoSave: false,
                fields: _this.getShowAllField()
            }, {
                type: 'caption',
                text: '终止点位默认不显示',
                color: 'red'
            }]
        }
    },
    getShowAllField: function () {
        var _this = this;
        return [{
            title: '显示全部',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '0',
            onChange: function (field, value, index) {
                var grid = Gikam.getComp('emc-report-folder-choose-list-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            TERMINATION_SEQ: '',
                        }
                    })
                } else {
                    grid.refresh({
                        requestData: {
                            TERMINATION_SEQ: '0',
                        }
                    })
                }
            },
        }]
    },
    getOrderTaskGridParam: function () {
        var _this = this, columns = emcReport.getFolderOrderTaskColumns(), requestData = this.param;
        return {
            type: 'grid',
            id: 'emc-report-order-task-choose-list-grid',
            requestData: requestData,
            columns: columns,
            filterOpen: true,
            columnsFill: true,
            toolbar: []
        }
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '55%',
                items: [_this.getGridParam()]
            },
            center: {
                items: [_this.getOrderTaskGridParam()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
};
