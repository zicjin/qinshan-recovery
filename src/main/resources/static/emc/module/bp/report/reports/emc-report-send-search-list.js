emcReport.searchSendListPage = {

    getGridParam : function() {
        var _this = this;
        return {
               type : 'grid',
               id : 'emc-report-send-report-send-info-grid',
               filterOpen : false,
               service : 'emcReportSendInfoServiceImpl',
               url : emcReport.baseUrl + '/report-send-info/queries',
               requestData : {
                  orgid_seq:workspace.user.orgId
               },
               readonly : true,
               columns : emcReport.getReportSendInfoListColumns(),
                onRowActive : function(index, row) {
                     Gikam.getComp('emc-report-send-uploader').setOptions({
                                  bizId : row.id,
                              });
                }
        }
    },



    getUploaderGrid : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-report-send-uploader',
            dbTable : 'T_EMC_REPORT_SEND_INFO',
        }
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
                items : [ _this.getUploaderGrid() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
