coreSms.searchPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-sms-list-grid',
            url : coreSms.baseUrl + '/queries/searchable',
            columns : coreSms.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                var smsId = row.id;

                if (!Gikam.isEmpty(smsId)) {
                    Gikam.getComp('core-sms-edit-list-base-info-form').refresh({
                        url : coreSms.baseUrl + '/' + smsId
                    });

                    Gikam.getComp('core-sms-edit-list-error-log-list-grid').refresh({
                        url : coreSms.baseUrl + '/' + smsId + '/error-logs/queries'
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.RESET',
                icon : 'revert',
                onClick : function() {
                    _this.doAct(coreSms.baseUrl + '/action/reset');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'cancel',
                onClick : function() {
                    _this.doAct(coreSms.baseUrl + '/action/close');
                }
            } ]
        }
    },
    
    doAct : function(url) {
        var grid = Gikam.getComp('core-sms-list-grid');
        
        var rows = grid.getSelections();

        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', rows ])).done(function(r) {
            grid.refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    getBaseInfoTab : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.SMS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-sms-edit-list-base-info-form',
                    columns: 3,
                    fields : coreSms.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-sms-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            } ]
        }
    },

    getLineGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : "CORE.MODULE.SYS.SMS.TAB.ERROR_LOG",
                items : [ {
                    type : 'grid',
                    page : false,
                    id : 'core-sms-edit-list-error-log-list-grid',
                    columns : coreSms.getLineListColumns(),
                    genericQuery : false
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-sms',
            renderTo : workspace.window.$dom,
            west : {
                width : '30%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 250,
                        items : [ _this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ _this.getLineGridParam() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};