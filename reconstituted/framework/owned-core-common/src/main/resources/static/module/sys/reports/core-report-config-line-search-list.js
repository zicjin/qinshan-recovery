//@ sourceURL=core-report-config-line-search-list.js
coreReportConfig.lineSearchPage = {

    getCoreReportConfigTree : function() {
        var _this = this;
        return {
            type : 'tree',
            name : 'CORE.MODULE.SYS.REPORTS.TAB.LINE',
            id : 'core-report-config-line-search-list-tree',
            url : coreReportConfig.baseUrl + '/lines/tree',
            requestData : {
                permissionTypeId : 'T_CORE_REPORT_CONFIG_LINE',
                user_assigned : workspace.user.id,
                category_SEQ : 'select',
                activatedFlag_SEQ : '1',
                authority_role : '1'
            },
            async : false,
            edit : false,
            single : true,
            onNodeSelected : function(nodeId, node) {
                _this.onTreeSelected();
            }
        }
    },

    onTreeSelected : function() {
        var _this = this;
        var reportConfigLine = Gikam.getComp('core-report-config-line-search-list-tree').options.selectedNodeData;
        var level = reportConfigLine.lvl;
        if (level == '2') {
            var reportCategory = reportConfigLine.ext$.reportcategory;
            if (Gikam.isEmpty(reportConfigLine.ext$.filename)) {
                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.VIEW_REPORT_MUST_UPLOAD_FILES_FIRST');
                return;
            }
            var reportPath = reportConfigLine.ext$.reportconfiglineno + '/' + encodeURIComponent(reportConfigLine.ext$.filename);

            var param = null;
            var defaultParam = reportConfigLine.ext$.defaultparam;
            var paramValueJson = null;
            if (Gikam.isNotEmpty(defaultParam) && Gikam.isNotEmpty(reportConfigLine.ext$.param)) { // 报表配置参数和默认参数均不为空
                if (!/^{.+}$/.test(defaultParam)) { // 默认参数不是json对象形式,提示错误的默认参数
                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.ERROR_DEFAULT_PARAM');
                    return;
                }

                param = (reportConfigLine.ext$.param).replace(/{[\w]+}/g, function(match) {
                    return match.toLocaleLowerCase();
                });

                paramValueJson = JSON.parse(defaultParam.replace(/\'/g, '"'));
                // 将key值转成小写
                for ( var key in paramValueJson) {
                    paramValueJson[key.toLocaleLowerCase()] = paramValueJson[key];
                    if (key.toLocaleLowerCase() != key) {
                        delete (paramValueJson[key]);
                    }
                }
            }

            var reportUrl = Gikam.getViewReportUrl(reportCategory, reportPath, param, paramValueJson);
            Gikam.getComp('core-report-config-search-list-iframe').refresh({
                src : reportUrl
            });
        }
    },

    getViewReportIframe : function() {
        var _this = this;
        return {
            type : 'iframe',
            id : 'core-report-config-search-list-iframe',
            src : '',
            onRendered : function() {
                _this.printGrid = this;
                Gikam.getComp('core-report-config-line-search-list-tree').onLoadSuccess(function(data) {
                    if (!Gikam.isEmpty(data)) {
                        data[0].expanded = true;
                        this.selectNodeById(data[0].children[0].id);
                    }
                });
            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 320,
                items : [ _this.getCoreReportConfigTree() ]
            },
            center : {
                width : 320,
                items : [ _this.getViewReportIframe() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};