emcReportInfoReference.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-info-reference-edit-list-grid',
            url : emcReportInfoReference.baseUrl + '/queries/raw',
            columns : emcReportInfoReference.getEditListColumns(),
            checkOnActive : true,
            service : 'emcReportInfoReferenceServiceImpl',
            deleteFormatter : function(row) {
                return row.bizCategory || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-report-info-reference-edit-list-grid').insert(emcReportInfoReference.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-report-info-reference-edit-list-grid').deleteRows(emcReportInfoReference.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copy();
                }
            } ]
        }
    },

    copy : function() {
        var selections = Gikam.getComp('emc-report-info-reference-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.postText(emcReportInfoReference.baseUrl + '/action/copy', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
            Gikam.getComp("emc-report-info-reference-edit-list-grid").refresh();
        });
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
