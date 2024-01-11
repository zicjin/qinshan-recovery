emcPretreatment.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-pretreatment-edit-list-grid',
            service : 'emcPretreatmentServiceImpl',
            columns : emcPretreatment.getEditListColumns(),
            url : emcPretreatment.baseUrl + '/queries/raw',
            page : true,
            filterOpen : true,
	        requestData : {
	            orgId_SEQ : workspace.user.ext$.parentorgid,
                status_SEQ : '0',
                sampleReceiverFlag_SEQ : '1'
	        },
            onCellClick : function(field, row) {
                field === 'ext$.extractmethod' && _this.loadExtractMethodPage(row.id);
                field === 'ext$.purifymethod' && _this.loadPurifyMethodPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.COMPLETE_PRETREAT',
                icon : 'submit',
                onClick : function() {
                    _this.completePretreatment();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPYTO',
                icon : 'copy',
                onClick : function() {
                    _this.copyTo();
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : [ {
                    title : '显示已完成',
                    field : 'showAll',
                    type : 'simpleCheckbox',
                    refreshGrid : false,
                    styleFormatter : function() {
                        return {
                            paddingLeft : '0'
                        }
                    },
                    onChange : function(field, value, index) {
                        workspace.window.showMask(true);
                        Gikam.getComp('emc-pretreatment-edit-list-grid').refresh({
                            requestData : {
                                status_SEQ : value
                            }
                        }).done(function() {
                            workspace.window.closeMask();
                        });
                    },
                } ]
            } ],
            group : {
                fields : [ 'ext$.ordercategory' ],
                formatter : function(row) {
                    return row.ext$.ordercategory || '-';
                }
            }
        }
    },

    loadExtractMethodPage(id) {
        Gikam.create('modal', {
            title : '提取方法',
            url : emcPretreatment.baseUrl + '/page/extract-method-choose-list' + Gikam.param({
                pretreatmentId_SEQ : id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                let pretreatment = {
                    id : id,
                    extractMethodId: rows[0].id
                };
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcPretreatmentServiceImpl', [pretreatment] ])).done(function(id) {
                    Gikam.getComp('emc-pretreatment-edit-list-grid').refresh();
                });
            },
            height: '70%'
        })
    },

    loadPurifyMethodPage(id) {
        Gikam.create('modal', {
            title : '净化方法',
            url : emcPretreatment.baseUrl + '/page/purify-method-choose-list' + Gikam.param({
                pretreatmentId_SEQ : id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                let pretreatment = {
                    id : id,
                    purifyMethodId: rows[0].id
                };
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcPretreatmentServiceImpl', [pretreatment] ])).done(function(id) {
                    Gikam.getComp('emc-pretreatment-edit-list-grid').refresh();
                });
            },
            height: '70%'
        })
    },

    completePretreatment : function() {
        var selections = Gikam.getComp('emc-pretreatment-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('EMC.MODULE.BP.RESULT.RUNS.TITLE.COMPLETE_PRETREAT', 'EMC.MODULE.BP.RESULT.RUNS.TIP.COMPLETE_PRETREAT', function() {
            Gikam.postText(emcPretreatment.baseUrl + '/action/complete', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                Gikam.getComp('emc-pretreatment-edit-list-grid').refresh();
            });
        });
    },

    copyTo : function() {
        let selections = Gikam.getComp('emc-pretreatment-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 800,
            height : 500,
            title : 'EMC.MODULE.BP.INFO.REPORTTMPLS.MODAL.COPYTO',
            url : '/secure/emc/module/bp/result/pretreatments/page/choose-list'+ Gikam.param({
                pretreatmentId : selections[0].id
            }),
            onAfterClose : function(data) {
                if (Gikam.isEmpty(data)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText(emcPretreatment.baseUrl + '/action/copy-to', Gikam.getJsonWrapper({}, [ '', selections ],[ '', data ])).done(function() {
                    Gikam.getComp('emc-pretreatment-edit-list-grid').refresh();
                }).always(function(){
                    workspace.window.closeMask();
                });
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
