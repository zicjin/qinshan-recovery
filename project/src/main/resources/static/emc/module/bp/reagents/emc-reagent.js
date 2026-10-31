var emcReagent = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'reagentName',
            title : 'T_EMC_REAGENT.REAGENTNAME',
            validators : [ 'strLength [0,60]' ],
            editor : true,
            editable : true,
            type : 'choose',
            category : 'mtl',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    managementType_SEQ : 'genericReagent'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        reagentName : rows[0].mtlName
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    reagentName : '',
                })
            },
        }, {
            field : 'ext$.preparebasis',
            title : 'T_EMC_REAGENT.PREPAREBASIS',
            editor : true,
            type : 'choose',
            category : 'method',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        methodId : rows[0].id,
                        ext$ : {
                            preparebasis : rows[0].testMethodBasis
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    methodId : '',
                    ext$ : {
                        preparebasis : '',
                    }
                })
            },
        }, {
            field : 'prepareDate',
            title : 'T_EMC_REAGENT.PREPAREDATE',
            type : 'date',
            editor : true
        }, {
            field : 'temperature',
            title : 'T_EMC_REAGENT.TEMPERATURE',
            editor : true,
            validators : [ 'strLength[0,18)' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_REAGENT.HUMIDITY',
            editor : true,
            validators : [ 'strLength[0,18)' ]
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_REAGENT.EQUIPTNAME',
            editor : true,
            category : 'equipt',
            type : 'choose',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '实验'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var equiptId = [];
                    var equiptName = [];
                    Gikam.each(rows, function(index, item) {
                        equiptId.push(item.id);
                        equiptName.push(item.equiptName);
                    });
                    this.setData({
                        id : Gikam.getComp('emc-reagent-edit-list-grid').getActivedRow().id,
                        ext$ : {
                            equiptid : equiptId.join(','),
                            equiptname : equiptName.join(',')
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-reagent-edit-list-grid').getActivedRow().id,
                    ext$ : {
                        equiptid : '',
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'prepareConcent',
            title : 'T_EMC_REAGENT.PREPARECONCENT',
            validators : [ 'strLength [0,60]' ],
            editor : true
        }, {
            field : 'ext$.preparevolume',
            title : 'T_EMC_REAGENT.PREPAREVOLUME',
            editor : true
        }, {
            field : 'ext$.solvent',
            title : 'T_EMC_REAGENT.SOLVENT',
            editor : true
        }, {
            field : 'validDate',
            title : 'T_EMC_REAGENT.VALIDDATE',
            type : 'date',
            editor : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_REAGENT.PREPARATOR',
        }, {
            field : 'ext$.auditorname',
            title : 'T_EMC_REAGENT.AUDITORNAME',
            editor : true,
            type : 'choose',
            category : 'emp',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        ext$ : {
                            auditorid : rows[0].id,
                            auditorname : rows[0].userName
                        }
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    ext$ : {
                        auditorid : '',
                        auditorname : '',
                    }
                });
            }
        }, {
            field : 'remark',
            title : 'T_EMC_REAGENT.REMARK',
            type : 'richText',
            validators : [ 'strLength [0,200]' ],
            editor : true
        } ];
    },
    
    getCompositionListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'reagentName',
            title : 'T_EMC_REAGENT_COMPOSITION.REAGENTNAME',
            validators : [ 'strLength [0,60]' ],
            editor : true,
            type : 'choose',
            category : 'reagentStock',
            single : true,
            param : {
                stockQty_NG : 0
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        reagentName : rows[0].ext$.mtlname,
                        reagentFactory : rows[0].ext$.vendorid,
                        reagentBatchNo : rows[0].batchNo
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    reagentName : '',
                    reagentFactory : '',
                    reagentBatchNo : ''
                });
            }
        }, {
            field : 'reagentFactory',
            title : 'T_EMC_REAGENT_COMPOSITION.REAGENTFACTORY',
        }, {
            field : 'reagentBatchNo',
            title : 'T_EMC_REAGENT_COMPOSITION.REAGENTBATCHNO',
        }, {
            field : 'weightVolume',
            title : 'T_EMC_REAGENT_COMPOSITION.WEIGHTVOLUME',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_REAGENT_COMPOSITION.REMARK',
            type : 'richText',
            editor : true
        } ];
    },

    getStockChooseListColumns : function () {
        return [ {
            field : 'ext$.mtlname',
            title : 'T_EMC_MTL.MTLNAME',
        }, {
            field : 'mtlId',
            title : 'T_EMC_MTL.ID',
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_MTL.SPECIFICATION',
        }, {
            field : 'ext$.mtlcategoryid',
            title : 'T_EMC_MTL.MTLCATEGORYID',
            type : 'select',
            search : false,
            category : 'mtlCategory',
        }, {
            field : 'batchNo',
            title : 'T_EMC_STOCK.BATCHNO',
        }, {
            field : 'stockQty',
            title : 'T_EMC_MTL.STOCKQTY',
        }, {
            field : 'expireDate',
            title : 'T_EMC_STOCK.EXPIREDATE',
        }, {
            field : 'ext$.vendorid',
            title : 'T_EMC_STOCK_LOG.VENDORID',
        } ];
    },

    getPreInsertFields : function () {
        return [ {
            field : 'ext$.preparebasis',
            title : 'T_EMC_REAGENT.PREPAREBASIS',
            editor : true,
            type : 'choose',
            category : 'method',
            single : true,
            preInsert : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        methodId : rows[0].id,
                        ext$ : {
                            preparebasis : rows[0].testMethodBasis
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    methodId : '',
                    ext$ : {
                        preparebasis : '',
                    }
                })
            },
        } ]
    },

    getShowAllFields : function() {
        return [ {
            title : 'GIKAM.BUTTON.SHOWALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                if (value === '1') {
                    Gikam.getComp('emc-reagent-edit-list-grid').refresh({
                        requestData : {
                            methodId_SEQ : ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    })
                } else {
                    var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                    Gikam.getComp('emc-reagent-edit-list-grid').refresh({
                        requestData : {
                            methodId_SEQ : activedRow.methodId
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    })
                }
            },
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/reagents',

});
