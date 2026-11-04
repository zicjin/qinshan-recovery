coreCustomSrtSurvey.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-srt-survey-edit-list-grid',
            url : coreCustomSrtSurvey.baseUrl + '/queries/raw',
            requestData : {processStatus_IN : 'draft,draft_rejected'},
            columns : coreCustomSrtSurvey.getListColumns(),
            deleteFormatter : function(row) {
                return row.surveyNo;
            },
            onCellClick : function(field, row) {
                _this.compRefresh(row.id);
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    //新增一条数据
                    Gikam.postSync(coreCustomSrtSurvey.baseUrl, Gikam.getJsonWrapper({}, ['', [{}]])
                    ).done(function(item) {
                        var comps = _this.compManager();
                        comps.surveyGrid.refresh().done(function(i) {
                            comps.surveyGrid.selectRowByIndex(0);
                            _this.compRefresh(item);
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.compManager().surveyGrid.deleteRows(coreCustomSrtSurvey.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }]
        }
    },

    compRefresh : function(id) {
        var comps = this.compManager();
        var itemBaseUrl = coreCustomSrtSurvey.baseUrl + '/' + id + '/srt-survey-items/queries';
        comps.basicForm.refresh({
            url : Gikam.printf(coreCustomSrtSurvey.baseUrl + '/{id}', {
                id : id
            })
        }, true);
        comps.auditForm.refresh({
            url : Gikam.printf(coreCustomSrtSurvey.baseUrl + '/{id}', {
                id : id
            })
        }, true);
        comps.itemGrid.refresh({
            url : Gikam.printf(itemBaseUrl, {
                id : id
            })
        });
        Gikam.getAsyncComp('core-custom-srt-surveys-attachment-panel-uploader').done(function(uploader) {
            uploader.setOptions({
                bizId : id
            });
        });
        comps.basicForm.toFieldsEdit(['auditUserName', 'auditDate', 'beAuditOrgName']);
    },

    compClean : function() {
        var comps = this.compManager();
        comps.basicForm.cleanData();
        comps.auditForm.cleanData();
        comps.itemGrid.cleanData();
        comps.basicForm.toFieldsReadonly(['auditUserName', 'auditDate', 'beAuditOrgName']);
        comps.auditForm.toFieldsReadonly(['beAuditOrgAdvice', 'qsOrgAdvice', 'qmAdvice', 'remark']);
    },

    compManager : function() {
        return {
            basicForm : Gikam.getComp('core-custom-srt-survey-basic-form'),
            auditForm : Gikam.getComp('core-custom-srt-survey-audit-form'),
            itemGrid : Gikam.getComp('core-custom-srt-survey-item-list-grid'),
            surveyGrid : Gikam.getComp('core-custom-srt-survey-edit-list-grid')
        }
    },

    submit : function() {
        var _this = this;
        var comps = this.compManager();
        Gikam.create('workflow').submit({
            data : comps.surveyGrid.getSelections(),
            pageObject : coreCustomSrtSurvey
        }).done(function() {
            comps.surveyGrid.refresh();
            _this.compClean();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-srt-survey-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [coreCustomSrtSurvey.getBtnToolbar('core-custom-srt-survey-edit-list-grid'),coreCustomSrtSurvey.getDetailTab()]
            },
        });
    },

    init : function() {
        this.create();
    }
};
