coreFileVolume.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-file-volume-edit-list-grid',
            url : coreFileVolume.baseUrl + '/queries/raw',
            columns : coreFileVolume.getEditListColumns(),
            service : 'coreFileVolumeServiceImpl',
            autoSave : true,
            deleteFormatter : function(row) {
                return row.fileVolumeName;
            },
            activateFormatter : function(row) {
                return row.fileVolumeName;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertFileVolume();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-file-volume-edit-list-grid').deleteRows(coreFileVolume.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'activate',
                onClick : function() {
                    var canActivate = true;
                    var grid = Gikam.getComp('core-file-volume-edit-list-grid');
                    Gikam.each(grid.getSelections(), function(index, row) {
                        if (row.activatedFlag == '1') {
                            canActivate = false;
                            Gikam.alert('GIKAM.TIP.CHOOSE_DEACTIVATED_ITEM');
                            return;
                        }
                    })
                    if (canActivate) {
                        workspace.window.showMask(true);
                        Gikam.put(coreFileVolume.baseUrl + '/action/activate', Gikam.getJsonWrapper({}, [ '', grid.getSelections() ])).done(function(item) {
                            
                        }).always(function() {
                            workspace.window.closeMask();
                            grid.refresh();
                        });
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'logout',
                onClick : function() {
                    Gikam.getComp('core-file-volume-edit-list-grid').deactivateRows(coreFileVolume.baseUrl + '/action/deactivate');
                }
            } ]
        }
    },
    
    insertFileVolume : function() {
        var _this = this;
        var modal = Gikam.create('modal', {
            title: 'CORE.MODULE.QMS.FILEVOLUMES.MODAL.ADD',
            width: 400,
            height: 200
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-file-volume-insert-form',
                    columns : 1,
                    fields : coreFileVolume.getInsertFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-file-volume-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var grid = Gikam.getComp('core-file-volume-edit-list-grid');
                            var rows = grid.getData();
                            var hasSame = false;
                            Gikam.each(rows, function(index, row) {
                                if (row.fileVolumeNo == form.getData().fileVolumeNo) {
                                    Gikam.alert('QMS.FILE.FILEVOLUME.ALERT.EXIST_SAME_FILEVOLUMENO_INPUT_AGIN');
                                    hasSame = true;
                                    return false;
                                }
                                if (row.fileVolumeName == form.getData().fileVolumeName) {
                                    Gikam.alert('QMS.FILE.FILEVOLUME.ALERT.EXIST_SAME_FILEVOLUMENAME_INPUT_AGIN');
                                    hasSame = true;
                                    return false;
                                }
                            })
                            if (hasSame) {
                                return;
                            }
                            var insertData = form.getData();
                            insertData.sortor = _this.getMaxSortor(grid) + 1;
                            grid.insert(coreFileVolume.baseUrl, [ insertData ]);
                            modal.close();
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    getMaxSortor : function(grid) {
        var maxSortor = 0;
        Gikam.each(grid.getData(), function(index, row) {
            if (maxSortor < row.sortor) {
                maxSortor = row.sortor;
            }
        })
        return maxSortor;
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-file-volume-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
