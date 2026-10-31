emcEquiptAcqScript.editPage = {

    mainGrid : null,
    testForm : null,
    getScriptGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.SCRIPT',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-acq-script-list-grid',
                    service : 'emcEquiptAcqScriptServiceImpl',
                    url : emcEquiptAcqScript.baseUrl + '/queries/raw',
	                requestData : {
	                    org_filter : '1',
	                },
	                
                    onRendered : function() {
                        mainGrid = this;
                    },
                    columns : emcEquiptAcqScript.getEditListColumns(),
                    deleteFormatter : function(row) {
                        return row.scriptDesc || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (Gikam.isEmpty(data)) {
                            Gikam.getComp('emc-equipt-acq-script-param-list-grid').cleanData();
                            Gikam.getComp('emc-equipt-acq-script-pre-analy-list-grid').cleanData();
                            Gikam.getComp('emc-equipt-acq-script-result-list-grid').cleanData();
                        } else {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onScriptActive(row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            Gikam.preInsert({
                                fields : emcEquiptAcqScript.getEditListColumns(),
                                url : emcEquiptAcqScript.baseUrl
                            }).done(function(id) {
                                Gikam.getComp('emc-equipt-acq-script-list-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('emc-equipt-acq-script-list-grid').deleteRows(emcEquiptAcqScript.baseUrl);
                        }
                    } ],
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.FILE',
                items : [ {
                    type : 'btnGroup',
                    items : [ {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.BUTTON.UPLOAD',
                        icon : 'add',
                        onClick : function() {
                            _this.uploadFile();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.BUTTON.PREANALY',
                        icon : 'add',
                        onClick : function() {
                            _this.preAnaly();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.BUTTON.TESTANALY',
                        icon : 'print',
                        onClick : function() {
                            _this.testAnaly();
                        }
                    } ]
                }, {
                    type : 'form',
                    id : 'emc-equipt-acq-script-test-form',
                    onRendered : function() {
                        testForm = this;
                    },
                    fields : emcEquiptAcqScript.getTestFields(),
                    columns : 1,
                    onLoadSuccess : function(data) {
                    },
                } ]

            } ]
        }
    },

    paramGrid : null,
    preAnalyGrid : null,
    resultGrid : null,
    getScriptOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.SCRIPT_PARAM',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-acq-script-param-list-grid',
                    service : 'emcEasParamServiceImpl',
                    columns : emcEquiptAcqScript.getScriptParamListColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.fieldname || row.id;
                    },
                    onRendered : function() {
                        paramGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addParam();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.removeParam();
                        }
                    } ],
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.SCRIPT_PREANALY',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-acq-script-pre-analy-list-grid',
                    service : 'emcEasPreAnalyServiceImpl',
                    columns : emcEquiptAcqScript.getScriptPreAnalyListColumns(),
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    onRendered : function() {
                        preAnalyGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addPreAnaly();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.removePreAnaly()
                        }
                    } ],
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.SCRIPT_RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-acq-script-result-list-grid',
                    service : 'emcEasResultServiceImpl',
                    columns : emcEquiptAcqScript.getScriptResultListColumns(),
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    onRendered : function() {
                        resultGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addResult();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.removeResult()
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.IMPORT',
                        icon : 'move-down',
                        onClick : function() {
                            _this.quickImport();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyResult();
                        }
                    } ],
                } ]
            } ]
        }
    },

    onScriptActive : function(row) {
        Gikam.refreshGrids({
            'emc-equipt-acq-script-param-list-grid' : emcEquiptAcqScript.baseUrl + '/{id}/params/queries',
            'emc-equipt-acq-script-pre-analy-list-grid' : emcEquiptAcqScript.baseUrl + '/{id}/pre-analies/queries',
            'emc-equipt-acq-script-result-list-grid' : emcEquiptAcqScript.baseUrl + '/{id}/results/queries',
        }, {
            id : row.id
        });
    },

    addParam : function() {
        var row = mainGrid.getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.SCRIPT_PARAM',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-acq-params/page/choose-list' + Gikam.param({
                id : row.id
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        paramId : item.id,
                        scriptId : row.id,
                    }
                });
                paramGrid.insert(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/params', {
                    id : row.id
                }), chooseData);
            }
        });

    },
    removeParam : function(_this) {
        paramGrid.deleteRows(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/params', {
            id : mainGrid.getActivedRow().id,
        }));
    },
    addPreAnaly : function(_this) {
        preAnalyGrid.insert(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/pre-analies', {
            id : mainGrid.getActivedRow().id,
        }));
    },
    removePreAnaly : function(_this) {
        preAnalyGrid.deleteRows(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/pre-analies', {
            id : mainGrid.getActivedRow().id,
        }));
    },
    addResult : function(_this) {
        resultGrid.insert(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/results', {
            id : mainGrid.getActivedRow().id,
        }));
    },
    removeResult : function(_this) {
        resultGrid.deleteRows(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/results', {
            id : mainGrid.getActivedRow().id
        }));
    },

    /** 导入数据 */
    quickImport : function() {
        var activedRow = Gikam.getComp('emc-equipt-acq-script-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            return;
        }

        Gikam.importExcel({
            url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcEquipDcuImportFileFunction',
            dbTable : 'T_EMC_EAS_RESULT',
            bizId : activedRow.id,
            onImportSuccess : function() {
                Gikam.getComp('emc-equipt-acq-script-result-list-grid').refresh();
            }
        });
    },

    /** 复制采集结果参数 */
    copyResult : function() {
        var activedRow = Gikam.getComp('emc-equipt-acq-script-list-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            return;
        }

        var selections = Gikam.getComp('emc-equipt-acq-script-result-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.postText(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/results/action/copy', {
            id : activedRow.id,
        }), Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
            Gikam.getComp('emc-equipt-acq-script-result-list-grid').refresh();
        });
    },

    urlManager : {},

    setCompUrl : function() {
        Gikam.setCompUrl({})
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        })
    },

    uploadFile : function() {
        _this = this;
        var row = mainGrid.getActivedRow();
        if (row) {
            var bizId = row.id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_EQUIPT_ACQ_SCRIPT$' + bizId
                },
                n : -1,
                s : 50
            })).done(function(r) {
                if (r.total > 0) {
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        Gikam.create('simpleUploader', {
                            id : 'emc-equipt-acq-script-test-form-uploader',
                            dbTable : 'T_EMC_EQUIPT_ACQ_SCRIPT',
                            bizId : bizId,
                            onAfterClose : function(data) {
                                _this.requestAndshowContent(data);
                            }
                        });
                    });
                } else {
                    Gikam.create('simpleUploader', {
                        id : 'emc-equipt-acq-script-test-form-uploader',
                        dbTable : 'T_EMC_EQUIPT_ACQ_SCRIPT',
                        bizId : bizId,
                        onAfterClose : function(data) {
                            _this.requestAndshowContent(data);
                        }
                    });
                }
            });
        } else {
            Gikam.alert("EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TIP.SELECT_BEFORE_ACTION")
        }
    },

    requestAndshowContent : function(data) {
        var fileId;
        if (data && data.length && data[data.length - 1].id) {
            workspace.window.showMask(true);
            fileId = data[data.length - 1].id;
            Gikam.post(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/files/queries/{fileId}', {
                fileId : fileId,
                id : mainGrid.getActivedRow().id
            }), null).done(function(resultObject) {
                if (resultObject) {
                    testForm.setData({
                        content : resultObject.view,
                        result : resultObject.result,
                        fileId : fileId,
                    });
                }
            }).always(function(resultObject) {
                workspace.window.closeMask();
            })
        }
    },

    preAnaly : function() {
        _this = this;
        var row = mainGrid.getActivedRow();
        var data = testForm.getData();
        if (row && data && data.result) {
            workspace.window.showMask(true);
            Gikam.post(Gikam.printf(emcEquiptAcqScript.baseUrl + '/{id}/action/pre-analy-test', {
                id : row.id,
            }), Gikam.getJsonWrapper(null, [ '', [ {
                result : data.result
            } ] ])).done(function(resultObject) {
                if (resultObject) {
                    testForm.setData({
                        content : resultObject.view,
                    });
                }
            }).always(function(resultObject) {
                workspace.window.closeMask();
            })
        } else {
            Gikam.alert("EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TIP.UPLOAD_FILE")
        }
    },

    testAnaly : function() {
        _this = this;
        var row = mainGrid.getActivedRow();
        var data = testForm.getData();
        if (!data.result || !row) {
            Gikam.alert("EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TIP.UPLOAD_FILE")
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_ACQ_SCRIPTS.TAB.TESTANALY',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-acq-scripts/page/analy-result' + Gikam.param({
                id : row.id,
                fileId : data.fileId,
            }),
            onAfterClose : function(rows) {
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            north : {
                height : 300,
                items : [ _this.getScriptGridParam() ]
            },
            center : {
                items : [ _this.getScriptOtherGridParam() ]
            },
        });
    },

    init : function(param) {
        this.create();
    },
};
