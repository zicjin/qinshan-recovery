coreAttorney.editPage = {
    getGridParam : function() {
        var customBaseInfoFormFields = coreAttorney.getBaseInfoFormFields();

        if (workspace.user.userType === 'sysAdmin') {
            customBaseInfoFormFields[1].preInsert = true;
        }
        
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-attorney-list-grid',
            url : coreAttorney.baseUrl + '/queries/raw',
            columns : coreAttorney.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('core-attorney-edit-list-base-info-form').cleanData();
                    Gikam.getComp('core-attorney-edit-list-menu-list-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                var attorneyId = row.id;

                if (!Gikam.isEmpty(attorneyId)) {
                    Gikam.getComp('core-attorney-edit-list-base-info-form').refresh({
                        url : coreAttorney.baseUrl + '/' + attorneyId
                    });

                    Gikam.getComp('core-attorney-edit-list-menu-list-grid').refresh({
                        url : coreAttorney.baseUrl + '/' + attorneyId + '/menus/queries'
                    });
                }
            },
            deleteFormatter : function(row) {
                return Gikam.propI18N('T_CORE_ATTORNEY.TRUSTORNAME') + ":" + row.ext$.trustorname + " / " + Gikam.propI18N('T_CORE_ATTORNEY.TRUSTEENAME') + ":" + row.ext$.trusteename;
            },
            activateFormatter : function(row) {
                return Gikam.propI18N('T_CORE_ATTORNEY.TRUSTORNAME') + ":" + row.ext$.trustorname + " / " + Gikam.propI18N('T_CORE_ATTORNEY.TRUSTEENAME') + ":" + row.ext$.trusteename;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.ATTORNEY.TITLE.ADD_I18N',
                        fields : customBaseInfoFormFields,
                        url : coreAttorney.baseUrl,
                        titleWidth : 110
                    }).done(function(id) {
                        _this.selectRowId = parseInt(id);
                        Gikam.getComp('core-attorney-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-attorney-list-grid').deleteRows(coreAttorney.baseUrl);
                    _this.selectRowId = null;
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-attorney-list-grid').activateRows(coreAttorney.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-attorney-list-grid').getSelections();

                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.viewAudit('T_CORE_ATTORNEY', selectedRows[0].id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.ATTORNEYS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-attorney-edit-list-base-info-form',
                    service : 'coreAttorneyServiceImpl',
                    columns: 2,
                    fields : coreAttorney.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-attorney-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    getMenuGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : "CORE.MODULE.SYS.ATTORNEYS.TAB.MENU",
                items : [ {
                    type : 'grid',
                    id : 'core-attorney-edit-list-menu-list-grid',
                    service : 'coreAttorneyMenuServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.menuname;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var attorneyId = Gikam.getComp('core-attorney-list-grid').getActivedRow().id;

                            if (!Gikam.isEmpty(attorneyId)) {
                                Gikam.create('modal', {
                                    title : Gikam.propI18N('CORE.MODULE.SYS.ATTORNEYS.MODAL.CHOOSE_MENU'),
                                    url : IFM_CONTEXT + '/secure/core/module/sys/menus/page/choose-list' + Gikam.param({
                                        target_filter : encodeURI(JSON.stringify({
                                            type : 'unassigned',
                                            targetTable : 'T_CORE_ATTORNEY_MENU',
                                            targetMatchColumn : 'MENUID',
                                            thisMatchColumn : 'ID',
                                            filter : [ {
                                                targetFilterColumn : 'ATTORNEYID',
                                                targetFilterValue : attorneyId,
                                            } ]
                                        }))
                                    }),
                                    onAfterClose : function(rows) {
                                        if (rows) {
                                            var chooseData = rows.map(function(item) {
                                                return {
                                                    menuId : item.id
                                                }
                                            });

                                            var insertAttorneyMenuUrl = Gikam.printf(coreAttorney.baseUrl + '/{id}/menus', {
                                                id : attorneyId
                                            });

                                            Gikam.getComp('core-attorney-edit-list-menu-list-grid').insert(insertAttorneyMenuUrl, chooseData);
                                        }
                                    }
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            var attorneyId = Gikam.getComp('core-attorney-list-grid').getActivedRow().id;
                            
                            if (!Gikam.isEmpty(attorneyId)) {
                                var deleteAttorneyMenuUrl = Gikam.printf(coreAttorney.baseUrl + '/{id}/menus', {
                                    id : attorneyId
                                });
                                
                                Gikam.getComp('core-attorney-edit-list-menu-list-grid').deleteRows(deleteAttorneyMenuUrl);
                            }
                        }
                    } ],
                    columns : coreAttorney.getMenuListColumns(),
                    genericQuery : false
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-attorney',
            renderTo : workspace.window.$dom,
            west : {
                width : '530',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 178,
                        items : [ _this.getBaseInfoTab() ]
                    }
                    ,
                    center : {
                        items : [ _this.getMenuGridParam() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};