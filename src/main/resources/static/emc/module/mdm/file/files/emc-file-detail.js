emcFile.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'emc-file-detail-back-button',
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
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.FILE.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-file-detail-base-info-form',
                    columns : 3,
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcFileServiceImpl',
                    fields : emcFile.getBaseInfoFormFields(_this.param.operType),
                    onRendered : function() {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'fileNo' ]);
                        }
                    }
                } ]
            } ]
        }
    },

    getFileVersionTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-file-detail-file-version-tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.FILE.TAB.FILE_VERSION',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-file-detail-file-version-grid',
                    url : _this.getCompUrl('fileVersionGrid'),
                    service : 'emcFileVersionServiceImpl',
                    columns : emcFile.getFileVersionColumns(_this.param.operType),
                    checkOnActive : true,
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            if(Gikam.getComp('emc-file-detail-file-version-dist-grid')) {
                                Gikam.getComp('emc-file-detail-file-version-dist-grid').cleanData();
                            }
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.versionNo || row.id;
                    },
                    onRowActive : function(index, row) {
                        if (row) {
                            var _fileVersionDistUrl = Gikam.printf(_this.urlManager['fileVersionDistGrid'], {
                                id : row.id
                            });
                            Gikam.getComp('emc-file-detail-file-version-dist-grid').refresh({
                                url :_fileVersionDistUrl,
                            });
                            var uploader = Gikam.getComp('emc-file-detail-uploader');
                            uploader.setOptions({
                                bizId : row.id
                            })
                            uploader.grid.refresh();
                        }
                    },
                    toolbar : _this.param.operType === 'search' ? [] : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertFileVersion();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteFileVersion();
                        }
                    }, ]
                } ]
            } ]
        }
    },

    getFileVersionDistTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-file-detail-file-version-dist-tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.FILE.TAB.FILE_VERSION_DIST',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-file-detail-file-version-dist-grid',
                    url : _this.getCompUrl('fileVersionDistGrid'),
                    service : 'emcFileVersionDistServiceImpl',
                    columns : emcFile.getFileVersionDistColumns(_this.param.operType),
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.distNo || row.id;
                    },
                    toolbar : _this.param.operType === 'search' ? [] : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertFileVersionDist();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteFileVersionDist();
                        }
                    }, ]
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',        // 附件
                items : [ {
                    type : 'uploader',
                    id : 'emc-file-detail-uploader',
                    dbTable : 'T_EMC_FILE_VERSION',
                    readonly : _this.param.operType === 'search' ? true : false,
                    bizId : _this.param.id,
                } ]
            } ]
        }
    },

    insertFileVersion : function() {
        var formData = Gikam.getComp('emc-file-detail-base-info-form').getData();
        var url = Gikam.printf(emcFile.baseUrl + '/{id}/versions', {
            id : formData.id
        });
        Gikam.getComp('emc-file-detail-file-version-grid').insert(url, [ {} ]);
    },

    deleteFileVersion : function() {
        var _this = this;
        var formData = Gikam.getComp('emc-file-detail-base-info-form').getData();
        var url = Gikam.printf(emcFile.baseUrl + '/{id}/versions', {
            id : formData.id
        });
        Gikam.getComp('emc-file-detail-file-version-grid').deleteRows(url).done(function (id) {
            Gikam.getComp('emc-file-detail-file-version-grid').refresh({
                url : _this.getCompUrl('fileVersionGrid', formData.id)
            });
        });
    },

    insertFileVersionDist : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-file-detail-file-version-grid').getActivedRow();
        if(activeRow) {
            var url = Gikam.printf(emcFile.baseUrl + '/versions/{id}/dist', {
                id : activeRow.id
            });
            Gikam.getComp('emc-file-detail-file-version-dist-grid').insert(url, [ {} ]);
        } else {
            Gikam.alert("EMC.MODULE.MDM.BASEMDM.FILE.VERSION.TIP.CHOOSE");
        }
    },

    deleteFileVersionDist : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-file-detail-file-version-grid').getActivedRow();
        if (activeRow) {
            var url = Gikam.printf(emcFile.baseUrl + '/versions/{id}/dist', {
                id : activeRow.id
            });
            Gikam.getComp('emc-file-detail-file-version-dist-grid').deleteRows(url);
        } else {
            Gikam.alert("EMC.MODULE.MDM.BASEMDM.FILE.VERSIONDIST.TIP.CHOOSE");
        }
    },

    urlManager : {
        baseInfoForm : emcFile.baseUrl + '/{id}',
        fileVersionGrid : emcFile.baseUrl + '/{id}/versions/queries',
        fileVersionDistGrid : emcFile.baseUrl + '/versions/{id}/dist/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-file-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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
            id : 'emc-file-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getFileVersionTab(), this.getFileVersionDistTab() ]
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
