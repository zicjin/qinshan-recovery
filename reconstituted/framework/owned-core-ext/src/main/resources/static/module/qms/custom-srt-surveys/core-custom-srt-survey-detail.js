coreCustomSrtSurvey.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_CUSTOM_SRT_SURVEY', _this.param.id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CUSTOMSRTSURVEYS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreCustomSrtSurveyServiceImpl',
                    id : 'core-custom-srt-survey-detail-base-info-form',
                    fields : coreCustomSrtSurvey.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getSrtSurveyItemGrid() ]
        }
    },

    getSrtSurveyItemGrid : function() {
        var _this = this;
        return {
            title : 'CUSTOMSRTSURVEYS.TAB.SRT_SURVEY_ITEM',
            items : [ {
                type : 'grid',
                id : 'core-custom-srt-survey-edit-list-srt-survey-item-grid',
                service : 'coreCustomSrtSurveyItemServiceImpl',
                url : _this.getCompUrl('srtSurveyItemGrid'),
                deleteFormatter : function(row) {
                    return row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.getComp('core-custom-srt-survey-edit-list-srt-survey-item-grid').insert(Gikam.printf(coreCustomSrtSurvey.baseUrl + '/{id}/srt-survey-items', {
                            id : _this.param.id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-custom-srt-survey-edit-list-srt-survey-item-grid').deleteRows(Gikam.printf(coreCustomSrtSurvey.baseUrl + '/{id}/srt-survey-items', {
                            id : _this.param.id
                        }));
                    }
                } ],
                columns : coreCustomSrtSurvey.getSrtSurveyItemListColumns()
            } ]
        };
    },

    urlManager : {
        baseInfoForm : coreCustomSrtSurvey.baseUrl + '/{id}',
        srtSurveyItemGrid : coreCustomSrtSurvey.baseUrl + '/{id}/srt-survey-items/queries',
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : '50%',
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            center : {
                items : [ this.getSouthTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
