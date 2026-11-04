coreAttorney.searchPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-attorney-list-grid',
            url : coreAttorney.baseUrl + '/queries/searchable',
            columns : coreAttorney.getSearchListColumns(),
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
            suspendFormatter : function(row) {
                return Gikam.propI18N('T_CORE_ATTORNEY.TRUSTORNAME') + ":" + row.ext$.trustorname + " / " + Gikam.propI18N('T_CORE_ATTORNEY.TRUSTEENAME') + ":" + row.ext$.trusteename;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SUSPEND',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-attorney-list-grid').suspendRows(coreAttorney.baseUrl + '/action/suspend');
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
                    fields : coreAttorney.getSearchBaseInfoFormFields()
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
                    page : false,
                    id : 'core-attorney-edit-list-menu-list-grid',
                    service : 'coreAttorneyMenuServiceImpl',
                    columns : coreAttorney.getSearchMenuListColumns(),
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
                width : '630',
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