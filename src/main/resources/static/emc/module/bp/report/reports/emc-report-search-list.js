emcReport.searchListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-search-list-grid',
            url : emcReport.baseUrl + '/queries',
            columns : emcReport.getArchivingListColumns(),
            page : true,
            pageSize : 50,
            group : {
                fields : [ 'ext$.projname' ],
                formatter : function(row) {
                    return row.ext$.projname;
                }
            },
            requestData :  {
                status_DIFFER : 'draft',
                orgId_SB : workspace.user.ext$.parentorgid
            },
            order : {
                'ext$.projname' : 'asc',
                projId : 'asc'
            },
            checkOnActive : true,
            readonly : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onReportActive(row);
            },
            toolbar : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.VIEW_REPORT',
                    icon : 'browse',
                    onClick : function() {
                        _this.viewDocument();
                    }
                } ]
            } ]
        }
    },

    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.REPORT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-report-search-list-uploader',
                    dbTable : 'T_EMC_REPORT',
                    readonly : true
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.PROJECT_FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-project-search-list-uploader',
                    dbTable : 'T_EMC_PROJECT',
                    readonly : true
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.FOLDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-folder-search-list-grid',
                    columns : emcReport.getFolderEditListColumns(),
                    readonly : true
                } ]
            }, {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-search-list-grid',
                    columns : emcReport.getOrderEditListColumns(),
                    readonly : true
                } ]
            } ]
        }
    },

    viewDocument : function() {
        var _this=this;
        var rows = Gikam.getComp('emc-report-search-list-grid').getSelections();
        if (rows.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        if (rows[0].reportConfigLineId) {
            Gikam.post(emcReport.baseUrl + '/view/document', Gikam.getJsonWrapper(null, [ '', rows ])).done(function(fileId) {
                var grid = Gikam.getComp('emc-report-search-list-grid');
                grid.refreshRowById(rows[0].id);
                if (grid.getActivedRow()) {
                    _this.onReportActive(grid.getActivedRow());
                }
                _this.openReportBrowseRecord(rows[0].id);
                // Gikam.openFile(fileId, true);
            });
        } else {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_FILE');
        }
    },
    openReportBrowseRecord : function(reportId) {
        var _this = this,
            _choose = Gikam.choose.getConfig('browseReportRecords');

        Gikam.create('modal', {
            width : _choose.width,
            height : '80%',
            title : _choose.title,
            url : _choose.url+Gikam.param({
                reportId : reportId,
            }),
        });
    },
    onReportActive : function(row) {
        var _this = this;
        Gikam.getComp('emc-report-search-list-uploader').setOptions({
            bizId : row.id,
        });
        Gikam.getComp('emc-project-search-list-uploader').setOptions({
            bizId : row.projId,
        });
        Gikam.getAsyncComp('emc-folder-search-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId
                }
            });
        });
        Gikam.getAsyncComp('emc-order-search-list-grid').done(function(grid) {
            grid.refresh({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/searchable',
                requestData : {
                    projId_SEQ : row.projId,
                    monitorTmplId_SEQ : row.monitorTmplId
                }
            });
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-search-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : "50%",
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getOtherGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
