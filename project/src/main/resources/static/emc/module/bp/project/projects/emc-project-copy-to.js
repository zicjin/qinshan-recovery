emcProject.copyToPage = {

    // 查询按钮
    getQueryBtnItem: function () {
        return {
            type: 'button',
            text: 'EMC.MODULE.BP.PROJECT.PROJECTS.MODAL.BUTTON.QUERY',
            class: 'blue',
            margin: '10px 0 0 15px',
            onClick: function () {
                var form = Gikam.getComp("emc-project-copy-to-query-form");
                var formData = form.options.data;
                Gikam.getComp('emc-project-copy-to-project-list-grid').refresh({
                    url: emcProject.baseUrl + '/queries/searchable',
                    requestData: {
                        bizCategoryId_SEQ: formData.bizCategoryId,
                        sample_SEQ: formData.sample,
                        projNo_CISC: formData.projNo,
                        projName_CISC: formData.projName,
                        org_filter: '1',
                    },
                })
            }
        }
    },

    // 查询表单
    getQueryFormItem: function () {
        var _this = this;

        return {
            type: 'form',
            id: 'emc-project-copy-to-query-form',
            columns: 4,
            url: '',
            titleWidth: 80,
            fields: emcProject.getProjectCopyToQueryFormFields(),
            autoSave: false,
            onRendered: function () {
                var form = Gikam.getComp('emc-project-copy-to-query-form');
                form.setData({
                    bizCategoryId: _this.param.bizCategoryId,
                    sample: _this.param.sample,
                })
            },
        }
    },

    // 项目列表
    getProjectTab: function () {
        var _this = this;
        return {
            type: 'tab',
            id: 'emc-project-copy-to-project-list-tab',
            width: Gikam.width.S,
            panels: [{
                title: 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.PROJECT',
                items: [{
                    type: 'grid',
                    id: 'emc-project-copy-to-project-list-grid',
                    url: '',
                    filter: false,
                    columns: emcProject.getProjectQueryListColumns(),
                    onLoadSuccess: function (data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData(["emc-project-copy-to-monitor-point-grid",
                                "emc-project-copy-to-order-grid"]);
                        }
                    },
                    onRowActive: function (index, row) {
                        _this.onCopyProjectActive(row);
                    }
                }]
            }]
        }
    },

    // 选中项目
    onCopyProjectActive: function (row) {

        var tab = Gikam.getComp('emc-project-copy-to-monitor-point-order-tab');
        if (row.sample === '1') {
            tab.revertPanel('emc-project-copy-to-monitor-point-panel');
            tab.removePanel('emc-project-copy-to-order-panel');
            Gikam.getComp('emc-project-copy-to-monitor-point-grid').refresh({
                url: IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes/queries/raw',
                requestData: {
                    projId_SEQ: row.id
                },
            })
        } else {
            tab.removePanel('emc-project-copy-to-monitor-point-panel');
            tab.revertPanel('emc-project-copy-to-order-panel');
            Gikam.getComp('emc-project-copy-to-order-grid').refresh({
                url: Gikam.printf(emcProject.baseUrl + '/{id}/orders/queries', {
                    id: row.id
                })
            });
        }
    },

    // 监测点列表
    getMonitorPointTab: function () {
        return {
            type: 'tab',
            id: 'emc-project-copy-to-monitor-point-order-tab',
            width: Gikam.width.S,
            show: true,
            panels: [{
                id: 'emc-project-copy-to-monitor-point-panel',
                title: 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.MONITORPOINT',
                items: [{
                    type: 'grid',
                    filter: false,
                    id: 'emc-project-copy-to-monitor-point-grid',
                    url: '',
                    columns: emcProject.getMonitorPointListColumns(),
                }]
            }, {
                id: 'emc-project-copy-to-order-panel',
                title: 'EMC.MODULE.BP.PROJECT.PROJECTS.TAB.ORDER',
                items: [{
                    type: 'grid',
                    filter: false,
                    id: 'emc-project-copy-to-order-grid',
                    url: '',
                    columns: emcProject.getProjectOrderColumns(),
                }]
            }]
        }
    },

    // 确认取消按钮组
    getConfirmCancelBtnItems: function () {
        var _this = this;
        return [{
            type: 'button',
            text: 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CONFIRM',
            class: 'blue',
            margin: '12px 15px 0 30px',
            onClick: function () {
                var selections = Gikam.getComp('emc-project-copy-to-project-list-grid').getSelections();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SELECT_AT_LEAST_ONE');
                    return;
                }

                var url = emcProject.baseUrl + '/copy-to';
                Gikam.getLastModal().window.showMask(true);
                Gikam.postText(url, Gikam.getJsonWrapper({id: _this.param.id}, ['', selections])).always(function () {
                    Gikam.getLastModal().window.closeMask();
                    Gikam.getLastModal().close();
                });

            }
        }, {
            type: 'button',
            margin: '12px 0 0 0',
            text: 'EMC.MODULE.BP.ORDER.ORDERRETAINS.MODAL.BTN.CANCEL',
            onClick: function () {
                Gikam.getLastModal().close();
            }
        }]
    },

    create: function () {
        var _this = this;
        Gikam.create('layout', {
            renderTo: Gikam.getLastModal().window.$dom,
            north: {
                height: 130,
                items: [_this.getQueryBtnItem(), _this.getQueryFormItem()]
            },
            center: {
                items: [{
                    type: 'layout',
                    north: {
                        height: '50%',
                        items: [
                            this.getProjectTab()
                        ]
                    },
                    center: {
                        items: [
                            this.getMonitorPointTab()
                        ]
                    }
                }]
            },
            south: {
                height: 56,
                items: [{
                    type: 'layout',
                    center: {
                        items: _this.getConfirmCancelBtnItems()
                    },
                }]
            }
        });
    },

    init: function (param) {
        this.param = param;
        this.create();
    }
}
