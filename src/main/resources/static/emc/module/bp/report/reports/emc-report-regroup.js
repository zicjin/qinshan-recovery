emcReport.regroupPage = {

    // 查询按钮
    getQueryBtnItem : function() {
        return {
            type: 'button',
            text: 'EMC.MODULE.BP.REPORT.REPORTS.MODAL.BUTTON.QUERY',
            class: 'blue',
            margin : '10px 0 0 15px',
            onClick : function () {
                var form = Gikam.getComp("emc-report-regroup-query-form");
                var formData = form.options.data;

                Gikam.getComp('emc-report-regroup-report-list-grid').refresh({
                    url : emcReport.baseUrl + '/queries/searchable',
                    requestData : {
                        monitorTmplId_SEQ : formData.monitorTmplId,
                        projName_SEQ : formData.projName,
                        bizId_SEQ : formData.bizcategoryId,
                        testOrgName_SEQ : formData.testOrgName,
                        entrustOrgName_SEQ : formData.entrustOrgName,
                        reportNo_SEQ : formData.reportNo,
                    },
                })
            }
        }
    },

    // 查询表单
    getQueryFormItem : function() {
        return {
            type : 'form',
            id : 'emc-report-regroup-query-form',
            columns : 5,
            titleWidth : 80,
            fields : emcReport.getReportQueryFormFields(),
            autoSave : false
        }
    },

    // 项目列表
    getReportTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-report-regroup-report-list-tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.REPORT_DATA',
                items : [ {
                    type : 'grid',
                    id : 'emc-report-regroup-report-list-grid',
                    url : '',
                    filter : false,
                    columns : emcReport.getReportQueryListColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.cleanCompData([ "emc-report-regroup-folder-list-grid" ]);
                            Gikam.getComp('emc-report-regroup-ordertask-shuttle-frame').setLeftData([]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onReportActive(row);
                    }
                } ]
            } ]
        }
    },
    
    // 项目列表
    getFolderTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-report-regroup-folder-list-tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.FOLDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-report-regroup-folder-list-grid',
                    url : '',
                    filter : false,
                    columns : emcReport.getFolderQueryListColumns(),
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            Gikam.getComp('emc-report-regroup-ordertask-shuttle-frame').setLeftData([]);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onFolderActive(row);
                    }
                } ]
            } ]
        }
    },

    // 选中报告
    onReportActive : function(row) {

        Gikam.getComp('emc-report-regroup-folder-list-grid').refresh({
            url : Gikam.printf(emcReport.baseUrl + '/{id}/folders/queries', {
                id : row.id
            }),
        })
    },

    // 选中点位
    onFolderActive : function(row) {

        Gikam.post( '/secure/emc/module/bp/bp/order-tasks/queries', Gikam.getJsonWrapper({
            f : {
                folderId_SEQ : row.id,
            },
            n : -1,
        })).done(function(data) {
            Gikam.getComp('emc-report-regroup-ordertask-shuttle-frame').setLeftData(data.rows);
        });
    },

    // 样品测试穿梭框
    getOrderTaskShuttleFrame : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-report-regroup-ordertask-shuttle-frame',
            columns : emcReport.getReportOrderTaskShuttleFrameColumns(),
            rightPage : false,
            leftPage : false,
            onRendered : function() {
                _this.shuttleFrame = this;
            },
        }
    },

    // 确认取消按钮组
    getConfirmCancelBtnItems : function() {
        return [ {
            type : 'button',
            text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.CONFIRM',
            class : 'blue',
            margin : '12px 15px 0 30px',
            onClick : function() {
                var selections = Gikam.getComp('emc-report-regroup-ordertask-shuttle-frame').rightGrid.getData();
                if (Gikam.isEmpty(selections)) {
                    Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.SELECT_AT_LEAST_ONE');
                    return;
                }
                var url = emcReport.baseUrl + '/action/regroup-report';
                Gikam.getLastModal().window.showMask(true);
                Gikam.postText(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                    Gikam.getComp('emc-report-edit-list-grid').refresh();
                }).always(function () {
                    Gikam.getLastModal().window.closeMask();
                    Gikam.getLastModal().close();
                });
            }
        }, {
            type : 'button',
            margin : '12px 0 0 0',
            text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.CANCEL',
            onClick : function () {
                Gikam.getLastModal().close();
            }
        } ]
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 80,
                items : [ _this.getQueryBtnItem(), _this.getQueryFormItem() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ {
                            type : 'layout',
                            west : {
                                width : '50%',
                                items : [
                                    this.getReportTab()
                                ]
                            },
                            center : {
                                items : [
                                    this.getFolderTab()
                                ]
                            }
                        } ]
                    },
                    center : {
                        items : [
                            this.getOrderTaskShuttleFrame()
                        ]
                    }
                } ]
            },
            south : {
                height : 56,
                items : _this.getConfirmCancelBtnItems()
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}
