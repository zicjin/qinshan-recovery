emcReport.sendInfoPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [  {
                       title : '发送纸质报告',
                       items : [ {
                           type : 'form',
                           url : Gikam.printf(emcReport.baseUrl + '/{projId}/report-send-info', {
                               projId : _this.param.projId
                           }),
                           id : 'emc-report-send-info-paper-form',
                           columns : 1,
                           service : 'emcReportSendInfoServiceImpl',
                           fields : emcReport.getPaperReportFormFields()
                       } ]
                   } ,{
                title : '发送电子报告',
                items : [ {
                    type : 'form',
                    url : Gikam.printf(emcReport.baseUrl + '/{projId}/report-send-info', {
                        projId : _this.param.projId
                    }),
                    id : 'emc-report-send-info-elec-form',
                    columns : 1,
                    service : 'emcReportSendInfoServiceImpl',
                    fields : emcReport.getElectronReportFormFields()
                } ]
            },]
        }
    },

    // 确认按钮
    getConfirmBtnItem : function() {
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.CONFIRM',
            class: 'blue',
            margin : '10px 10px 0 15px',
            onClick : function () {
                let electForm = Gikam.getComp('emc-report-send-info-elec-form');
                let paperForm = Gikam.getComp('emc-report-send-info-paper-form');
                if (electForm.validate() && paperForm.validate()) {
                    debugger;
                    let electData = electForm.getData();
                    let paperData = paperForm.getData();
                   var grid = Gikam.getComp('emc-report-audit-list-grid');
                        var selections = grid.getSelections();
                        var updateData = selections.map(function (item) {
                            return{
                                projId: item.projId,
                                username:paperData.ext$.entrustorgcontact,
                                address: paperData.ext$.address,
                                phone: paperData.ext$.phone,
                                pic: paperData.ext$.pic,
                                expressType: paperData.expressType,
                                expressNo: paperData.expressNo,
                                remark: paperData.remark,
                            }
                        });
                        workspace.window.showMask();
                        Gikam.put(emcReport.baseUrl + '/updateByProjectId', Gikam.getJsonWrapper({}, ['', updateData])).done(function () {
                             Gikam.getLastModal().close(Object.assign({}, electData, paperData));
                        }).always(function () {
                            workspace.window.closeMask();
                        });


                }
            }
        }
    },

    getCancelBtnItem : function() {
        return {
            type : 'button',
            margin : '10px 0 0 0',
            text : 'GIKAM.BUTTON.CANCEL',
            onClick : function () {
                Gikam.getLastModal().close();
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-undo-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
            south : {
                height : 56,
                items : [ _this.getConfirmBtnItem(), _this.getCancelBtnItem() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
