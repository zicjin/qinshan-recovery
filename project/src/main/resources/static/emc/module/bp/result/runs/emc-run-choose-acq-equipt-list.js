emcRun.chooseAcqEquiptPage = {

    getEquiptGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-choose-acq-equipt-detail-grid',
            url : emcRun.baseUrl + Gikam.printf('/acqEquipt/{runId}', {
                runId :  _this.param.runId,
            }),
            columns : emcRun.getEquiptChooseListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-run-choose-acq-equipt-detail-grid').getSelections();
                    if(Gikam.isEmpty(data)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_AT_LEAST_ONE_EQUIPT');
                        return;
                    }
                    /*Gikam.getLastModal().close(data);*/

                    Gikam.create('simpleUploader', {
                        id : 'emc-run-edit-list-test-run-uploader',
                        dbTable : 'T_EMC_EQUIPT',
                        bizId : _this.param.runId,
                        multiple : true,

                        onAfterClose : function(fileList) {
                            if (!Gikam.isEmpty(fileList)) {
                                workspace.window.showMask(true);
                                var url = Gikam.printf(emcRun.baseUrl + '/{runId}/equipt-collects', {
                                    runId :  _this.param.runId,
                                });
                                fileList.map(function(file) {
                                    file.size = 0;
                                });
                                Gikam.put(url , Gikam.getJsonWrapper({equiptid : data[0].equiptId}, [ 'coreFileServiceImpl', fileList ])).done(function() {
                                    Gikam.getComp('emc-run-edit-list-test-run-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    })

                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-choose-acq-equipt-detail-layout',
            center : {
                items : [ this.getEquiptGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};
