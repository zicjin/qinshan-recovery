coreChooseConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-choose-config-edit-list-grid',
            url : coreChooseConfig.baseUrl + '/queries/raw',
            columns : coreChooseConfig.getEditListColumns(),
            service : 'coreAdminChooseConfigServiceImpl',
            deleteFormatter : function(row) {
                return row.name;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-choose-config-edit-list-base-info-form" ]);
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
                        height : 300,
                        fields : coreChooseConfig.getBasePreInsertFields(),
                        url : coreChooseConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-choose-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-choose-config-edit-list-grid').deleteRows(coreChooseConfig.baseUrl);
                }
            },  {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'preview',
                onClick : function() {
                	var form_validator_result = Gikam.getComp('core-choose-config-edit-list-base-info-form').validate()
                	if (Gikam.isFalse(form_validator_result)) {
                		return
                	}
                	
                    var selectRow = Gikam.getComp('core-choose-config-edit-list-grid').getSelections();
                    if (selectRow.length <=0) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return
                    }
                    
                    if(selectRow.length >=2) {
                        Gikam.alert('CORE.MODULE.SYS.MOBILE_MENUS.TIP.CHOOSE_ONE_ITEM');
                        return
                    }
                    
                    var widthValue = selectRow[0].width;
                    
                    var width = Gikam.isEmpty(widthValue) ? '' : widthValue.indexOf('%') ? widthValue : new Number(widthValue);
                    
                    Gikam.create('modal',{
                         title: selectRow[0].title,
                         width: width,
                         url: IFM_CONTEXT + selectRow[0].url
                    })
                }
            } ]
        }
    },

    onConfigActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('core-choose-config-edit-list-base-info-form').refresh({
                url : Gikam.printf(coreChooseConfig.baseUrl + '/{id}', {
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
                    id : 'core-choose-config-edit-list-base-info-form',
                    fields : coreChooseConfig.getBaseInfoFormFields(),
                    service : 'coreAdminChooseConfigServiceImpl',
                    autoSave : true,
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-choose-config-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-choose-config-edit-list-layout',
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
