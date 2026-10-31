//@ sourceURL=emc-index-category.js
var emcIndexCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories',
    getEditListColumns : function() {
        return [ {
            checkbox : true
        },{
            field : 'activatedFlag',
            title : '状态',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            }
        }, {
            field : 'id',
            title : 'T_EMC_INDEX_CATEGORY.VIEW',
            formatter : function(i, v, r) {
                if (Gikam.isEmpty(r.id)) {
                    return;
                }
                return '<a href="javascript:;" onclick="emcIndexCategory.loadDetailPage('+r.id+')">'+Gikam.propI18N('T_EMC_INDEX_CATEGORY.VIEW')+'</a>';
            },
            width : 80
        }, {
            field : 'stdName',
            title : 'T_EMC_INDEX_CATEGORY.STDNAME',
            width : 380
        }, {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_INDEX_CATEGORY.MONITORTMPLGROUPID',
        }, {
            title : 'T_EMC_INDEX_CATEGORY.FILE',
            field : 'ext$.filename',
            type : 'link'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'stdName',
            title : 'T_EMC_INDEX_CATEGORY.STDNAME',
            preInsert : true,
            // validators : [ 'remote[' + emcIndexCategory.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,200]' ],
            // readonly : true
        }, {
            field : 'ext$.templatename',
            title : 'T_EMC_INDEX_CATEGORY.PROPERTYTMPLID',
            type : 'choose',
            single : true,
            preInsert : true,
            category : 'propertyTmpl',
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        propertyTmplId : rows[0].id,
                        ext$ : {
                            templatename : rows[0].templateName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    propertyTmplId : '',
                    ext$ : {
                        templatename : ''
                    }
                });
            },
            onBeforeChoose : function() {
                return {
                    templateUse_SEQ : 'evaluationStandard'
                }
            }
        }, {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_INDEX_CATEGORY.MONITORTMPLGROUPID',
            type : 'choose',
            category : 'monitorTmplGroup',
            preInsert : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        monitorTmplGroupId : rows[0].id,
                        ext$ : {
                            monitortmplgroup : rows[0].monitorTmplGroup
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorTmplGroupId : '',
                    ext$ : {
                        monitortmplgroup : ''
                    }
                });
            }
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_INDEX_CATEGORY.ACTIVATEDFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            }
        }, {
            field : 'startDate',
            title : 'T_EMC_INDEX_CATEGORY.STARTDATE',
            type : 'date'
        }, {
            field : 'expireDate',
            title : 'T_EMC_INDEX_CATEGORY.EXPIREDATE',
            type : 'date'
        }, {
            field : 'eStandard',
            title : 'T_EMC_INDEX_CATEGORY.ESTANDARD',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'remark',
            title : 'T_EMC_INDEX_CATEGORY.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 2
        } ];
    },

    getOrderIndexColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_ORDER_INDEX.ACTIVATEDFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            },
            width : 70,
            editor : true,
        }, {
            field : 'indexName',
            title : 'T_EMC_ORDER_INDEX.INDEXALL',
            validators : [ 'strLength[0,100]' ],
            width : 450,
            formatter : function (i, v, r) {
                let indexName = [r.index1 || '', r.index2 || '', r.index3 || '', r.index4 || '', r.index5 || '', r.index6 || ''].filter(item => item.length > 0).join('-');
                indexName = indexName.replace('<', '&lt;').replace('>', '&gt;')

                return '<a href="javascript:;" onclick="emcIndexCategory.loadOrderIndexDetail(' + r.id + ')">' + indexName + '</a>';
            }
        }, {
            field : 'reportDisplayName',
            title : 'T_EMC_ORDER_INDEX.ALIASNAME',
            editor : true,
        }, ];
    },

    getPropertyOrderIndexFields : function() {
        return [{
            field : 'index1',
            title : 'T_EMC_ORDER_INDEX.INDEX1'
        }, {
            field : 'index2',
            title : 'T_EMC_ORDER_INDEX.INDEX2'
        }, {
            field : 'index3',
            title : 'T_EMC_ORDER_INDEX.INDEX3'
        }, {
            field : 'index4',
            title : 'T_EMC_ORDER_INDEX.INDEX4'
        }, {
            field : 'index5',
            title : 'T_EMC_ORDER_INDEX.INDEX5'
        }, {
            field : 'index6',
            title : 'T_EMC_ORDER_INDEX.INDEX6'
        } ];
    },

    getIndexSetFields : function() {
        return [ {
            field : 'evalIndex',
            title : 'T_EMC_INDEX_SET.EVALINDEX',
            preInsert : true,
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'ext$.indexItemName',
            title : 'T_EMC_INDEX_SET.INDEXITEMID',
            type : 'choose',
            category : 'indexItem',
            editor : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        indexItemId : rows[0].id,
                        ext$ : {
                            indexItemName : rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    indexItemId : '',
                    ext$ : {
                        indexItemName : '',
                    }
                });
            },
        }, ];
    },

    getIndexSetColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sortNo',
            title : 'T_EMC_INDEX_SET.SORTNO',
            type : 'number',
            validators : ['integer', 'numRange[1,99999]' ],
            hasArrow : true,
            editor : true,
        }, {
            field : 'evalIndex',
            title : 'T_EMC_INDEX_SET.EVALINDEX',
            preInsert : true,
            validators : [ 'strLength[0,30]' ],
            editor : true,
        }, {
            field : 'ext$.indexitemname',
            title : 'T_EMC_INDEX_SET.INDEXITEMID',
            type : 'choose',
            category : 'indexItem',
            single : true,
            editor : true,
            onBeforeChoose : function() {
                var row = Gikam.getComp('emc-index-set-detail-field-grid').getActivedRow();
                return {
                    indexItemName_CISC : row.evalIndex,
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        indexItemId : rows[0].id,
                        ext$ : {
                            indexitemname : rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    indexItemId : '',
                    ext$ : {
                        indexitemname : '',
                    }
                });
            },
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_INDEX_SET.INDEXLOWLIMIT',
            validators : [ 'strLength[0,30]' ],
            editor : true,
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_INDEX_SET.INDEXHIGHLIMIT',
            validators : [ 'strLength[0,30]' ],
            editor : true,
        }, {
            field : 'textIndexValue',
            title : 'T_EMC_INDEX_SET.TEXTINDEXVALUE',
            validators : [ 'strLength[0,500]' ],
            editor : true,
        }, {
            field : 'unit',
            title : 'T_EMC_INDEX_SET.UNIT',
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        unit : rows[0].unitName
                    })
                }
            },
            editor : true,
            editable : true,
        },{
            field : 'applyScope',
            title : '适用范围',
            validators : [ 'strLength[0,100]' ],
            editor : true,
        }, ];
    },

    getIndexSetBatchUpdadeFields : function() {
        return [ {
            field : 'unit',
            title : 'T_EMC_INDEX_SET.UNIT',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        unit : rows[0].unitName
                    })
                }
            }
        } ];
    },

    getIndexPreviewColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'stdName',
            title : 'T_EMC_INDEX_PREVIEW.STDNAME',
            width : 400,
            validators : [ 'strLength[0,200]' ],
        }, {
            field : 'expireDate',
            title : 'T_EMC_INDEX_PREVIEW.EXPIREDATE',
            type : 'date',
        }, {
            field : 'index1',
            title : 'T_EMC_INDEX_PREVIEW.INDEX1',
            width : 260,
            validators : [ 'strLength[0,100]' ],
        }, {
            field : 'index2',
            title : 'T_EMC_INDEX_PREVIEW.INDEX2',
            width : 260,
            validators : [ 'strLength[0,100]' ],
        }, {
            field : 'ext$.indexitemname',
            title : 'T_EMC_INDEX_PREVIEW.INDEXITEMID',
            width : 300,
            type : 'choose',
            category : 'indexItem',
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        indexItemId : rows[0].id,
                        ext$ : {
                            indexItemName : rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    indexItemId : '',
                    ext$ : {
                        indexItemName : '',
                    }
                });
            },
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_INDEX_PREVIEW.INDEXLOWLIMIT',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_INDEX_PREVIEW.INDEXHIGHLIMIT',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'textIndexValue',
            title : 'T_EMC_INDEX_PREVIEW.TEXTINDEXVALUE',
            width : 300,
            validators : [ 'strLength[0,500]' ],
        }, {
            field : 'unit',
            title : 'T_EMC_INDEX_PREVIEW.UNIT',
            validators : [ 'strLength[0,80]' ],
        } ];
    },

    getOrderIndexChooseListColumns : function() {
        return [ {
            field : 'activatedFlag',
            title : 'T_EMC_ORDER_INDEX.ACTIVATEDFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            },
        }, {
            field : 'id',
            title : 'T_EMC_ORDER_INDEX.ID',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'indexName',
            title : 'T_EMC_ORDER_INDEX.INDEXNAME',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'index1',
            title : 'T_EMC_ORDER_INDEX.INDEX1',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'index2',
            title : 'T_EMC_ORDER_INDEX.INDEX2',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'index3',
            title : 'T_EMC_ORDER_INDEX.INDEX3',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'index4',
            title : 'T_EMC_ORDER_INDEX.INDEX4',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'index5',
            title : 'T_EMC_ORDER_INDEX.INDEX5',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'index6',
            title : 'T_EMC_ORDER_INDEX.INDEX6',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'reportDisplayName',
            title : 'T_EMC_ORDER_INDEX.REPORTDISPLAYNAME',
            validators : [ 'strLength[0,10]' ],
        } ];
    },
    loadOrderIndexDetail : function(id,insertFlag){
        var modal = Gikam.create('modal', {
            id : 'emc-order-index-base-info-modal',
            width : '60%',
            height : '70%',
            toolbarTop : true
        });
        modal.window.load(emcIndexCategory.baseUrl + '/page/order-index-detail' + Gikam.param({
            id : id,
            insertFlag : insertFlag
        }));
    },
    loadDetailPage : function (id) {
        var modal = Gikam.create('modal', {
            id : 'emc-index-category-base-info-modal',
            width : '80%',
            height : '70%',
            onAfterClose : function(data) {
                var grid = Gikam.getComp('emc-index-category-edit-list-grid');
                grid.refreshRowById(id);
            }
        });
        modal.window.load(emcIndexCategory.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },
});
