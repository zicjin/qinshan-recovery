emcReport.itemQcIndexPage = {
    // 检测样品检测表
    getTestMethodGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-report-item-qc-index-list-test-method-grid',
            url: IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/queries',
            requestData: {
                ID_IN: !Gikam.isEmpty(_this.param.testmethodids) ? _this.param.testmethodids : '2',
            },
            columns: emcReport.getQcIndexTestMethodListColumns(),
            onLoadSuccess: function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-report-item-qc-index-list-qc-index-grid').cleanData();
                }
            },
            onRowActive: function (index, row) {
                Gikam.getAsyncComp('emc-report-item-qc-index-list-qc-index-grid').done(function (grid) {
                    grid.refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/items/qc-indexes/queries',
                        requestData: {
                            testmethodId_SEQ: row.id
                        },
                        order: {
                            'ext$.itemname': 'asc'
                        },
                    });
                });
            },
        };
    },

    // 指标分析项表
    getQcIndexGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-report-item-qc-index-list-qc-index-grid',
            columns: emcReport.getQcIndexListColumns(),
        };
    },

    createPage: function () {
        Gikam.create('layout', {
            west: {
                width: '25%',
                items: [this.getTestMethodGridParam()]
            },
            center: {
                items: [this.getQcIndexGridParam()]
            },
            renderTo: Gikam.getLastModal().window.$dom
        });
    },

    init: function (param) {
        this.param = param;
        this.createPage();
    }
};
