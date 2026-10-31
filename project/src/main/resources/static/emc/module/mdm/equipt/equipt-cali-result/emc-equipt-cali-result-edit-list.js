emcEquiptCaliResult.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.CALIBRATIONRESULT',
            items : [ {
                type : 'grid',
                id : 'emc-equipt-cali-result-edit-list-grid',
                service : 'emcEquiptCaliResultServiceImpl',
                url : emcEquiptCaliResult.baseUrl + '/queries/raw',
                columns : emcEquiptCaliResult.getCaliEditListColumns(),
                deleteFormatter : function(row) {
                    return row.equiptId || row.id;
                },
                onLoadSuccess : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    }else {
                        var uploader = Gikam.getComp('emc-equipt-cali-result-edit-list-uploader');
                        if(uploader && uploader.grid.model) {
                            uploader.setOptions({
                                bizId : '',
                            });
                            uploader.grid.cleanData();
                        }
                        Gikam.getComp('emc-equipt-cali-result-norm-edit-list-grid').cleanData();
                    }
                },
                onRowActive : function(index, row) {
                    _this.onNormActive(row);
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        _this.addConsume();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-cali-result-edit-list-grid').deleteRows(emcEquiptCaliResult.baseUrl);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                    icon : 'submit',
                    onClick : function() {
                        _this.submit();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.PREVIEW',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('DINGAN_EQCALI ', Gikam.getComp('emc-equipt-cali-result-edit-list-grid').getSelections());
                    }
                } ]
            } ],
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-equipt-cali-result-edit-list-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        if (!grid.validate(selections)) {
            return;
        }

        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcEquiptCaliResult
        }).done(function() {
            grid.refresh();
        });
    },

    getNormParam : function(){
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-cali-result-norm-edit-list-grid',
            service : 'emcEquiptCaliResultNormServiceImpl',
            url : emcEquiptCaliResult.baseUrl2 + '/queries/raw',
            columns : emcEquiptCaliResult.getNormEditListColumns(),
            deleteFormatter : function(row) {
                return row.ext$.testcategorydesc || row.id;
            },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        _this.addTestMethod();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-cali-result-norm-edit-list-grid').deleteRows(emcEquiptCaliResult.baseUrl2);
                    }
                }]
        }
    },
    addTestMethod : function() {
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EQUIPT_TEST_METHOD',
                targetTestMethodIdColumn : 'TESTMETHODID',
                targetFilterColumn : 'EQUIPTID',
                targetFilterValue : Gikam.getComp('emc-equipt-cali-result-edit-list-grid').getActivedRow().id,
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var formData = Gikam.getComp('emc-equipt-cali-result-edit-list-grid').getActivedRow();
                var url = Gikam.printf(emcEquiptCaliResult.baseUrl + '/{id}/test-methods', {
                    id : formData.id,
                });
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        caliResultId : formData.id,
                        testCategoryDesc : item.ext$.testcategory,
                        methodName : item.ext$.methodname,
                        testId : item.testId,
                        testName : item.ext$.testname,
                        testMethodBasis : item.ext$.testmethodbasis
                    })
                })
                Gikam.getComp('emc-equipt-cali-result-norm-edit-list-grid').insert(url, chooseData);
            }
        });
    },

    getProjectAnnexPanel : function() {
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.ANNEX',
            items : [ {
                type : 'uploader',
                id : 'emc-equipt-cali-result-edit-list-uploader',
                dbTable : 'T_EMC_EQUIPT_CALI_RESULT',
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-equipt-cali-result-edit-list-base-info-tab',
            panels : [  _this.getGridParam(), _this.getProjectAnnexPanel() ],
        }
    },

    onNormActive : function(row) {
        var _this = this;
        if(row){
            Gikam.getAsyncComp('emc-equipt-cali-result-norm-edit-list-grid').done(function(item) {
                item.refresh({
                    url : _this.getTestNormUrl(row.id)
                });
            });
        }
        Gikam.getAsyncComp('emc-equipt-cali-result-edit-list-uploader').done(function(uploader) {
            uploader.setOptions({
                bizId : row.id
            });
            uploader.grid.refresh();
        });
    },
    getTestNormUrl : function(ItemId) {
        if (ItemId) {
            return Gikam.printf(emcEquiptCaliResult.baseUrl + '/{id}' + '/test-methods/queries', {
                id : ItemId
            })
        }
    },

    addConsume : function() {
            Gikam.create('modal', {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.EQUIPT',
                url : Gikam.choose.getConfig('equiptCali').url + Gikam.param({
                    //single : true,
                    pageType : 'equiptCalibration'
                }),
                onAfterClose : function(rows, index) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    var chooseData = rows.map(function(row) {
                        return {
                            equiptId : row.id,
                            equiptNo : row.equiptId,
                            equiptName : row.equiptName,
                            specification : row.specification,
                            cycle : row.ext$.cycle,
                            validTime : row.ext$.maintenddate,
                            caliReason : row.ext$.gist,
                            unit : row.ext$.verifyorg

                        };
                    });
                    Gikam.getComp('emc-equipt-cali-result-edit-list-grid').insert(emcEquiptCaliResult.baseUrl, chooseData);
                }
            });
    },



    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-cali-result-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '60%',
                items : [ _this.getBaseInfoTab() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    panels : [ {
                    title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_CALI_RESULT_NORM.CALINORM',
                    items : [ _this.getNormParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
