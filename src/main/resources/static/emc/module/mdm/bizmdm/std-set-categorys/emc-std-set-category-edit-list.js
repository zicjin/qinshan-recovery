emcStdSetCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-std-set-category-edit-list-grid',
            service : 'emcStdSetCategoryServiceImpl',
            url : emcStdSetCategory.baseUrl + '/queries/raw',
            columns : emcStdSetCategory.getEditListColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.standardSet || row.id;
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    var standardSetGrid = Gikam.getComp('emc-std-set-category-edit-list-standard-set-grid');
                    var fieldSetGrid = Gikam.getComp('emc-std-set-category-edit-list-field-set-grid');
                    standardSetGrid.cleanData();
                    fieldSetGrid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-std-set-category-edit-list-standard-set-grid').refresh({
                    url : Gikam.printf(emcStdSetCategory.baseUrl + '/{id}/standard-sets/queries', {
                        id : row.id
                    })
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.STDSETCATEGORYS.TAB.STD_SET_CATEGORY_ADD',
                        fields : emcStdSetCategory.getBaseInfoFormFields(),
                        url : emcStdSetCategory.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-std-set-category-edit-list-grid').refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-std-set-category-edit-list-grid').deleteRows(emcStdSetCategory.baseUrl);
                }
            } ]
        }
    },

    getStandardSetGridParam : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.STANDARDSETS.TAB.STANDARD_SET',
                items : [ {
                    type : 'grid',
                    id : 'emc-std-set-category-edit-list-standard-set-grid',
                    service : 'emcStandardSetServiceImpl',
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.setName || row.id;
                    },
                    columns : emcStdSetCategory.getStandardSetListColumns(),
                    onLoadSuccess : function(data) {
                        if (data.length > 0) {
                            this.activeRowByIndex(0);
                        } else {
                            var fieldSetGrid = Gikam.getComp('emc-std-set-category-edit-list-field-set-grid');
                            fieldSetGrid.cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        Gikam.getComp('emc-std-set-category-edit-list-field-set-grid').refresh({
                            url : Gikam.printf(emcStdSetCategory.baseUrl + '/{id}/field-sets/queries', {
                                id : row.id
                            })
                        });
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-std-set-category-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(data)) {
                                Gikam.alert('EMC.MODULE.MDM.BIZMDM.STANDARDSETS.TIP.CHOOSE_ONE');
                                return;
                            }
                            Gikam.preInsert({
                                fields : emcStdSetCategory.getStandardSetListColumns(),
                                url : emcStdSetCategory.baseUrl + '/' + data.id + '/standard-sets',
                            }).done(function(id) {
                                Gikam.getComp('emc-std-set-category-edit-list-standard-set-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-std-set-category-edit-list-standard-set-grid').deleteRows(emcStdSetCategory.baseUrl + "/standard-sets")
                        }
                    } ]
                } ]
            } ]
        }
    },
    getFieldSetGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.FIELDSETS.TAB.FIELD_SET',
                items : [ {
                    type : 'grid',
                    id : 'emc-std-set-category-edit-list-field-set-grid',
                    service : 'emcFieldSetServiceImpl',
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    columns : emcStdSetCategory.getFieldSetListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertFieldSet();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-std-set-category-edit-list-field-set-grid').deleteRows(emcStdSetCategory.baseUrl + "/field-sets")
                        }
                    } ]
                } ]
            } ]
        }
    },

    insertFieldSet : function() {
        var activedRow = Gikam.getComp('emc-std-set-category-edit-list-standard-set-grid').getActivedRow();
        if (Gikam.isEmpty(activedRow)) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.FIELDSETS.TIP.CHOOSE_ONE');
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.FIELDSETS.TAB.FIELD_SET_ADD',
            width : 350,
            heigth : 500,
            url : '/secure/emc/module/mdm/bizmdm/std-set-categorys/page/field-info/choose-list' + Gikam.param({
                tableName_SEQ : 'T_EMC_ORDER_TASK_RESULT',
                standardSetId : activedRow.id
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcStdSetCategory.baseUrl + '/{standardSetId}/field-sets', {
                    standardSetId : activedRow.id,
                });
                var chooseData = [];
                Gikam.each(rows, function(index, field) {
                    chooseData.push({
                        fieldName : field.name,
                    })
                })
                Gikam.getComp('emc-std-set-category-edit-list-field-set-grid').insert(url, chooseData);
            }
        });
    },

    // 计算公式
    popScript : function(id, field) {
        _this = this;
        Gikam.create('modal', {
            width : '35%',
            url : emcStdSetCategory.baseUrl + '/page/stand-set-script-detail' + Gikam.param({
                id : id,
                field : field
            }),
            onAfterClose : function(script) {
                Gikam.getComp('emc-std-set-category-edit-list-standard-set-grid').refreshRowById(Number(id));
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-std-set-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '20%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '350',
                        items : [ _this.getStandardSetGridParam() ]
                    },
                    center : {
                        items : [ _this.getFieldSetGridParam() ]
                    }
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
