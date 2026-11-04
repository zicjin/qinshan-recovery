coreSelectConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-select-config-edit-list-grid',
            url : coreSelectConfig.baseUrl + '/queries/raw',
            columns : coreSelectConfig.getEditListColumns(),
            service : 'coreAdminSelectConfigServiceImpl',
            deleteFormatter : function(row) {
                return row.name;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-select-config-edit-list-base-info-form" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onConfigActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ADMIN.CHOOSE_CONFIGS.MODAL.ADD',
                        height : 360,
                        titleWidth : 110,
                        fields : coreSelectConfig.getBasePreInsertFields(),
                        url : coreSelectConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-select-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-select-config-edit-list-grid').deleteRows(coreSelectConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'preview',
                onClick : function() {
                	var form_validator_result = Gikam.getComp('core-select-config-edit-list-base-info-form').validate()
                	if (Gikam.isFalse(form_validator_result)) {
                		return
                	}
                	
                    var selectRow = Gikam.getComp('core-select-config-edit-list-grid').getSelections();
                    if (selectRow.length <=0) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return
                    }

                    if(selectRow.length >=2) {
                        Gikam.alert('CORE.MODULE.SYS.MOBILE_MENUS.TIP.CHOOSE_ONE_ITEM');
                        return
                    }
                    
                    Gikam.getJson(Gikam.IFM_CONTEXT + selectRow[0].url).done(function (dataList) {
                        const items = dataList.map(item => {
                             return {
                                 value: Gikam.getFieldValue(item, selectRow[0].valueField),
                                 text: Gikam.getFieldValue(item, selectRow[0].textField)
                             }
                         });
                         items.unshift({
                             text: '',
                             value: ''
                         });

                         const modal = Gikam.create('modal',{
                            title: 'GIKAM.BUTTON.VIEW',
                            width: 400,
                            height: 400,
                            bodyStyle: {'margin' : '20px 20px 0 0' }
                        })

                        Gikam.create('form',{
                            renderTo: modal.window.$dom,
                            type: 'form',
                             id: 'form',
                             columns: 2,
                             fields: [{
                                 title: selectRow[0].name,
                                 field: selectRow[0].category,
                                 type: 'select',
                                 items: items,
                                 colspan: 2
                             }]
                        })
                    }).fail(function(err) {
                        Gikam.alert('CORE.MODULE.ADMIN.SELECT_CONFIGS.TIP.CONFIG_FAILURE');
                    })
                }
            }]
        }
    },

    onConfigActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('core-select-config-edit-list-base-info-form').refresh({
                url : Gikam.printf(coreSelectConfig.baseUrl + '/{id}', {
                    id : row.id
                }),
            });
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ADMIN.CHOOSE_CONFIGS.MODAL.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    id : 'core-select-config-edit-list-base-info-form',
                    fields : coreSelectConfig.getBaseInfoFormFields(),
                    service : 'coreAdminSelectConfigServiceImpl',
                    autoSave : true,
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-select-config-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-select-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
