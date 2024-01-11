emcVendor.editPage = {

    getGridParam : function() {
        var _this = Gikam.deepExtend(this, {});
        return {
            type : 'grid',
            id : 'emc-vendor-edit-list-grid',
            service : 'emcVendorServiceImpl',
            url : emcVendor.baseUrl + '/queries/raw',
            requestData : {
                vendorCategory_SEQ : _this.param.category,
	            orgId_SB : workspace.user.ext$.parentorgid
            },
            columns : emcVendor.getEditListColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.vendorName || row.id;
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    var fileGrid = Gikam.getComp('emc-vendor-edit-list-uploader');
                    var mtlGrid = Gikam.getComp('emc-vendor-mtl-list-grid');
                    fileGrid.setOptions({
                        bizId : ''
                    });
                    fileGrid.grid.cleanData();
                    if (mtlGrid)
                        mtlGrid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                if (Gikam.getComp('emc-vendor-mtl-list-grid')) {
                    Gikam.getComp('emc-vendor-mtl-list-grid').refresh({
                        url : Gikam.printf(emcVendor.baseUrl + '/{id}/mtls/queries', {
                            id : row.id
                        })
                    });
                }
                var uploader = Gikam.getComp('emc-vendor-edit-list-uploader');
                uploader.setOptions({
                    bizId : row.id
                })
                uploader.grid.refresh();
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        fields : emcVendor.getEditListColumns(),
                        url : emcVendor.baseUrl
                    }).done(function(id) {
                        var data = {};
                        data.id = id;
                        data.vendorCategory = _this.param.category;
                        workspace.window.showMask(true);
                        Gikam.put(Gikam.printf(emcVendor.baseUrl + "/{id}", {
                            id : id
                        }), Gikam.getJsonWrapper(null, [ 'emcVendorServiceImpl', [ data ] ])).done(function() {
                            Gikam.getComp('emc-vendor-edit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-vendor-edit-list-grid').deleteRows(emcVendor.baseUrl);
                }
            } ]
        }
    },

    getMtlGridParam : function() {
        var _this = this;
        var panels = [];
        if (_this.param.category == 'supply') {
            panels.push({
                title : 'EMC.MODULE.MDM.BASEMDM.VENDORS.TAB.MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-vendor-mtl-list-grid',
                    service : 'emcVendorMtlServiceImpl',
                    deleteFormatter : function(row) {
                        return row.supplyMtlName || row.id;
                    },
                    columns : emcVendor.getMtlListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-vendor-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(data)) {
                                Gikam.alert('EMC.MODULE.MDM.BASEMDM.VENDORS.TIP.CHOOSE_ONE');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.preInsert({
                                fields : emcVendor.getMtlListColumns(),
                                url : emcVendor.baseUrl + '/' + data.id + '/mtls',
                            }).done(function(id) {
                                Gikam.getComp('emc-vendor-mtl-list-grid').refresh();
                            }).always(function(){
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-vendor-mtl-list-grid').deleteRows(emcVendor.baseUrl + "/mtls")
                        }
                    } ]
                } ]
            });
        }
        panels.push({
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                type : 'uploader',
                id : 'emc-vendor-edit-list-uploader',
                filter : false,
                dbTable : 'T_EMC_VENDOR',
                onBeforeUpload : function() {
                    this.setUploadData({})
                }
            } ]
        });
        return {
            type : 'tab',
            fill : true,
            panels : panels
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-vendor-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '45%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getMtlGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
