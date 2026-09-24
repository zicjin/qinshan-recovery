emcFixedasset.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-fixedasset-edit-list-grid',
            url : emcFixedasset.baseUrl + '/queries/raw',
            columns : emcFixedasset.getEditListColumns(),
            service : 'emcFixedassetServiceImpl',
	        requestData : {
                useOrg_filter : '1',
	        }, 
            toolbar : _this.getToolBar(),
            filterOpen : true,
            deleteFormatter : function(row) {
                return row.assetName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getAsyncComp('emc-fixedasset-edit-list-form').done(function(fixedassetForm) {
                        fixedassetForm.cleanData();
                        fixedassetForm.setOptions({
                            url : ''
                        });
                    });
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-fixedasset-edit-list-form').refresh({
                    url : Gikam.printf(emcFixedasset.baseUrl + "/{id}", {
                        id : row.id
                    }),
                });
            }
        }
    },

    getToolBar : function() {
        var _this = this;
        return [ {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.FIXEDASSETS.MODAL.INSERT',
                        fields : emcFixedasset.getPreInsertFields(),
                        url : emcFixedasset.baseUrl,
                        onFormRendered : function(form) {
                            setTimeout(() => {
                                var orgId = workspace.user.ext$.parentorgid;
                                this.setData({orgId : orgId},false);
                            }, 10)
                        }
                    }).done(function(data) {

                        Gikam.getComp('emc-fixedasset-edit-list-grid').refresh(emcFixedasset.baseUrl);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-fixedasset-edit-list-grid').deleteRows(emcFixedasset.baseUrl);
                }
            } ]
        } ];
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.FIXEDASSETS.TOP.BASIC',
                items : [ {
                    type : 'form',
                    id : 'emc-fixedasset-edit-list-form',
                    service : 'emcFixedassetServiceImpl',
                    columns : 3,
                    fields : emcFixedasset.getBaseInfoFormFields(),
                    onUpdated : function(data) {
                        Gikam.getComp('emc-fixedasset-edit-list-grid').setData(data);
                    }
                }, ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-fixedasset-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '36%',
                items : [ this.getGridParam() ]
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
