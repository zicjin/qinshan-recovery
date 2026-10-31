emcRunWeighing.editPage = {

    getProjectGrid: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-weighing-list-project-grid',
            url: IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries',
            filterOpen: true,
            page: true,
            requestData: {
                status_SEQ: 'auditFinished',
                orgId: workspace.user.ext$.parentorgid,
                targetType : 'Preprocessing',
            },
            columns: emcRunWeighing.getProjectListColumns(),
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([
                        "emc-run-weighing-list-order-grid"]);
                }
            },
            onRowActive: function (index, row) {
                _this.onProjectActive(row);
            },
            // onCellClick : function(field, row) {
            //     field === 'methodName' && _this.loadMethodEdit(row);
            // },
            toolbar: [{
                type: 'button',
                text: '样品称重录入',
                icon: 'add',
                onClick: function () {
                    var data = Gikam.getComp('emc-run-weighing-list-project-grid').getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var projIds = data.map(function (e) {
                        return e.id;
                    });
                    _this.loadResultInputPage(projIds);
                }
            },]
        }
    },

    getOrderGrid: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-run-weighing-list-order-grid',
            filterOpen: true,
            columns: emcRunWeighing.getOrderListColumns(),
        }
    },

    onProjectActive: function (row) {
        var _this = this;
        if (row) {

            Gikam.getAsyncComp('emc-run-weighing-list-order-grid').done(function (grid) {
                grid.refresh({
                    url: IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries',
                    requestData: {
                        projId_SEQ: row.id,
                        TEST_NAME_QUERIES: '1'
                    }
                })
            });
        }
    },

    loadResultInputPage: function (projIds) {
        var _this = this;
        Gikam.create('modal', {
            height: '95%',
            width: '95%',
            title: '测试批详情',
            url: emcRunWeighing.baseUrl + '/page/preprocessing-result-input' + Gikam.param({
                projIds: projIds,
            }),
            onAfterClose: function () {
            }
        });
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-run-weighing-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: '40%',
                items: [_this.getProjectGrid()]
            },
            center: {
                items: [{
                    type: 'layout',
                    center: {
                        items: [_this.getOrderGrid()]
                    },
                }]
            }
        });
    },

    init: function () {
        this.create();
    }
};
