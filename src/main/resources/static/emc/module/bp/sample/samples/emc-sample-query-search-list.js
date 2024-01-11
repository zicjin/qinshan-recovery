emcSample.sampleQuerySearchPage = {
    getSampleQueryTab: function () {
        var _this = this, columns = emcSample.getSampleQuerySearchColumns(), order = {};
        return {
            id: 'emc-sample-query-search-list-tab',
            type: 'tab',
            fill: true,
            panels: [
                {//我的记录
                    title: 'EMC.MODULE.BP.SAMPLE.TAB.MYSELF_RECORD',
                    id: 'emc-sample-query-search-myself-panel',
                    items: [
                        _this.getSampleQuerySearchPanel(
                            'emc-sample-query-search-myself-grid',
                            order,
                            columns,
                            _this.getSampleButton('emc-sample-query-search-myself-grid'))
                    ]
                },
                {//校核记录
                    title: 'EMC.MODULE.BP.SAMPLE.TAB.MY_APPROVE_RECORD',
                    id: 'emc-sample-query-search-my-approve-panel',
                    items: [
                        _this.getSampleQuerySearchPanel(
                            'emc-sample-query-search-my-approve-grid',
                            order,
                            columns,
                            _this.getSampleButton('emc-sample-query-search-my-approve-grid'))
                    ]
                },
                {//审核记录
                    title: 'EMC.MODULE.BP.SAMPLE.TAB.MY_AUDIT_RECORD',
                    id: 'emc-sample-query-search-my-audit-panel',
                    items: [
                        _this.getSampleQuerySearchPanel(
                            'emc-sample-query-search-my-audit-grid',
                            order,
                            columns,
                            _this.getSampleButton('emc-sample-query-search-my-audit-grid'))
                    ]
                },
                {//采样记录
                    title: 'EMC.MODULE.BP.SAMPLE.TAB.ALL_RECORD',
                    id: 'emc-sample-query-search-all-panel',
                    items: [
                        _this.getSampleQuerySearchPanel(
                            'emc-sample-query-search-all-grid',
                            order,
                            columns,
                            _this.getSampleButton('emc-sample-query-search-all-grid'))
                    ]
                }
            ],
            onActived: function (data) {
                var url = emcSample.baseUrl + '/queries',requestData = {},gridId = '';
                if (data.index == 0) {
                    gridId = 'emc-sample-query-search-myself-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                        createdById_SEQ: workspace.user.id,
                    };
                }
                if (data.index == 1) {
                    gridId = 'emc-sample-query-search-my-approve-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                        approveUserId: workspace.user.id,
                    };
                }
                if (data.index == 2) {
                    gridId = 'emc-sample-query-search-my-audit-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                        auditedUserId: workspace.user.id,
                    };
                }
                if (data.index == 3) {
                    gridId = 'emc-sample-query-search-all-grid';
                    requestData = {
                        orgId_SB : workspace.user.ext$.parentorgid,
                    };
                }

                Gikam.getAsyncComp(gridId).done(function (grid) {
                    grid.refresh({
                        url: url,
                        requestData: requestData
                    });
                });
            }
        }
    },
    getSampleButton: function (gridId) {
        var _this = this;
        return [{
            type : 'button',
            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
            icon : 'upload',
            onClick : function() {
                var _this = this
                var rows = Gikam.getComp(gridId).getSelections();
                if (rows && rows.length === 1) {
                    Gikam.create('modal', {
                        width : 800,
                        height : 500,
                        title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                        url : emcSample.baseUrl + '/page/sample-annex-list?id=' + rows[0].id
                    });
                } else {
                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                }
            }
        },{
            type : 'button',
            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_SAMPLING_REPORT',
            icon : 'print',
            onClick : function() {
                var selections = Gikam.getComp(gridId).getSelections();
                if (selections.length !== 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                    return;
                }
                Gikam.viewReport(selections[0].reportConfigLineId, selections[0]);
            }
        }, {
            type : 'button',
            text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.VIEW_CALIBRATION_REPORT',
            icon : 'print',
            onClick : function() {
                var selections = Gikam.getComp(gridId).getSelections();
                if (selections.length !== 1) {
                    Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                    return;
                }
                Gikam.viewReport(selections[0].caliReportLineId, selections[0]);
            }
        }]
    },
    /** 加载采样信息页面 */
    loadSampleExpandPage: function (id, propertyTmplId, monitorTmplId, sampleFlag, projId, orderCategory, sampleUserId, sampleDate) {
        var _param = {
            sampleId: id,
            propertyTmplId: propertyTmplId,
            monitorTmplId: monitorTmplId,
            sampleFlag: sampleFlag,
            projId: projId,
            orderCategory: orderCategory,
            sampleUserId: sampleUserId,
            sampleDate: sampleDate,
            readonly : true
        };
        Gikam.create('modal', {
            title: '采样信息',
            width: '90%',
            height: '90%',
            url: emcSample.baseUrl + '/page/sample-expand-detail' + Gikam.param(_param),
        });
    },
    loadSamplePropertyPage : function(id, tmplId, monitorTmplId) {
        // 查询 sample 的时候，如果存在样品，则需要更新 采样单的 monitorTmplId，并插入数据到 property data 表
        Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/tmpls/' + id + '/judge-samples/' + monitorTmplId).done(function() {
            Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh();
        });
        // 打开采样单属性的时候，看 模板 id 是否存在，没有就要提示进行添加；有的话要判断是否添加过
        if (Gikam.isEmpty(monitorTmplId)) {
            Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.TIP.UNSET_SAMPLE_PROPERTY");
            return;
        }

        var _this = this;
        Gikam.create('modal', {
            width : '60%',
            bodyStyle : {
                padding : '0px 20px'
            },
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DETAIL_INFO',
            url : emcSample.baseUrl + '/page/info-detail' + Gikam.param({
                id : id,
                tmplId : tmplId,
                monitorTmplId : monitorTmplId,
                readonly : true
            }),
        });
    },
    loadDataInputPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.DATA_INPUT',
            url : emcSample.baseUrl + '/page/data-input-detail' + Gikam.param({
                id : id,
                readonly : true
            }),
        });
    },
    loadCalibrationPage : function(id) {
        Gikam.create('modal', {
            width : '80%',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.INSTRUMENT_ADJUSTMENT',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-calibrations/page/detail-list' + Gikam.param({
                id : id,
                readonly : true
            }),
        });
    },
    getSampleQuerySearchPanel: function (gridId, order, columns, toolbar) {
        var _this = this;
        return {
            type: 'layout',
            center: {
                items: [{
                    type: 'grid',
                    id: gridId,
                    service: 'emcSampleServiceImpl',
                    columns: columns,
                    order: order,
                    page: true,
                    filterOpen : true,
                    toolbar: toolbar
                }]
            },
        }
    },
    create: function () {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-sample-query-search-list-layout',
            renderTo: workspace.window.$dom,
            center: {
                items: [this.getSampleQueryTab()]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
};
