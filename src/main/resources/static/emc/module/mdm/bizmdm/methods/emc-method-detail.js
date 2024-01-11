emcMethod.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.METHODS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcMethodServiceImpl',
                    id : 'emc-method-detail-base-info-form',
                    fields : emcMethod.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getVersionGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.METHODS.TAB.METHOD_VERSION',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-method-detail-version-list-grid',
                    url : _this.getCompUrl('versionGrid'),
                    service : 'emcMethodVersionServiceImpl',
                    onCellClick : function(field, row) {
                        field === 'ext$.filename' && Gikam.openFile(row.ext$.fileid, true);
                    },
                    deleteFormatter : function(row) {
                        return row.versionNo || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertVersionRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteVersionRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                        icon : 'upload',
                        onClick : function() {
                            _this.uploadVersionFile();
                        }
                    } ],
                    columns : emcMethod.getVersionListColumns()
                } ]
            } ]
        }
    },

    insertVersionRow : function() {
        var formData = Gikam.getComp('emc-method-detail-base-info-form').getData();
        var url = Gikam.printf(emcMethod.baseUrl + '/{id}/versions', {
            id : formData.id
        });

        Gikam.getComp('emc-method-detail-version-list-grid').insert(url, [ {
            methodId : formData.id
        } ]);
    },

    deleteVersionRow : function() {
        var formData = Gikam.getComp('emc-method-detail-base-info-form').getData();
        var url = Gikam.printf(emcMethod.baseUrl + '/{id}/versions', {
            id : formData.id
        });
        Gikam.getComp('emc-method-detail-version-list-grid').deleteRows(url);
    },

    uploadVersionFile : function() {
        var formData = Gikam.getComp('emc-method-detail-base-info-form').getData();
        var rows = Gikam.getComp('emc-method-detail-version-list-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_METHOD_VERSION$' + bizId
                },
                n : -1,
                s : 50
            })).done(function(r) {
                if (r.total > 0) {
                    Gikam.confirm('EMC.MODULE.MDM.BIZMDM.METHODS.TIP.FILE_OVERRIDE', 'EMC.MODULE.MDM.BIZMDM.METHODS.TIP.FILE_OVERRIDE', function() {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                            Gikam.create('simpleUploader', {
                                id : 'emc-method-detail-version-uploader',
                                dbTable : 'T_EMC_METHOD_VERSION',
                                bizId : bizId,
                                multiple : false,
                                onAfterClose : function() {
                                    Gikam.getComp('emc-method-detail-version-list-grid').refresh()
                                }
                            });
                        });
                    })
                } else {
                    Gikam.create('simpleUploader', {
                        id : 'emc-method-detail-version-uploader',
                        dbTable : 'T_EMC_METHOD_VERSION',
                        bizId : bizId,
                        multiple : false,
                        onAfterClose : function() {
                            Gikam.getComp('emc-method-detail-version-list-grid').refresh()
                        }
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    urlManager : {
        baseInfoForm : emcMethod.baseUrl + '/{id}',
        versionGrid : emcMethod.baseUrl + '/{id}/versions/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-method-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-method-detail-version-list-grid' : this.getCompUrl('versionGrid')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getVersionGridParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
