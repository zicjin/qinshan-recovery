//@ sourceURL=emc-info-tmpl-edit-list.js
emcInfoTmpl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-tmpl-edit-list-grid',
            url : emcInfoTmpl.baseUrl + '/queries/raw',
            service : 'emcInfoTmplServiceImpl',
            columns : emcInfoTmpl.getTmplEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-info-tmpl-edit-list-grid').activeRowByIndex(0);
                } else {
                    if (Gikam.getComp('emc-info-tmpl-edit-list-field-grid')) {
                        Gikam.getComp('emc-info-tmpl-edit-list-field-grid').cleanData();
                    }
                    if (Gikam.getComp('emc-info-tmpl-edit-list-item-grid')) {
                        Gikam.getComp('emc-info-tmpl-edit-list-item-grid').cleanData();
                    }
                }
            },
            deleteFormatter : function(row) {
                return row.tmplName;
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-info-tmpl-edit-list-field-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcInfoTmpl.baseUrl + '/{id}/fields/queries', {
                            id : row.id
                        }),
                    });
                });
                Gikam.getAsyncComp('emc-info-tmpl-edit-list-item-grid').done(function(grid) {
                    grid.refresh({
                        url : emcInfoTmpl.baseUrl + '/items/queries',
                        requestData : _this.getItemRequestData(row.id)
                    });
                });
            },
            onCellClick : function(field, row) {
                if (field === 'ext$.excelname') {
                    // 先获取模板文件信息，再下载之
                    Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + row.ext$.excelid).done(function(coreFile) {
                        Gikam.emcDownloadFile(IFM_CONTEXT + coreFile.downloadUrl);
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.INFO.INFOTMPLS.MODAL.ADD',
                        fields : emcInfoTmpl.getTmplBaseInfoFormFields(),
                        url : emcInfoTmpl.baseUrl,
                        width : 350,
                        height : 150,
                        titleWidth : 70
                    }).done(function(id) {
                        Gikam.getComp('emc-info-tmpl-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-tmpl-edit-list-grid').deleteRows(emcInfoTmpl.baseUrl);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.INFO.INFOTMPLS.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var activeRow = Gikam.getComp('emc-info-tmpl-edit-list-grid').getActivedRow();
                    if (!activeRow) {
                        Gikam.alert("EMC.MODULE.BP.INFO.INFOTMPLS.TIP.COPY_TMPL_UNSELECTED");
                        return;
                    }
                    var url = Gikam.printf(emcInfoTmpl.baseUrl + '/{tmplId}/copy', {
                        tmplId : activeRow.id
                    });
                    Gikam.getComp('emc-info-tmpl-edit-list-grid').insert(url).done(function(id) {
                        Gikam.getComp('emc-info-tmpl-edit-list-grid').refresh().done(function(id) {
                            if(!Gikam.getComp('emc-info-tmpl-edit-list-grid').data
                                    || Gikam.getComp('emc-info-tmpl-edit-list-grid').data.length === 0)
                                return;
                            Gikam.getComp('emc-info-tmpl-edit-list-grid').selectRowById(id);
                        });
                    });
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.INFO.INFOTMPLS.BUTTON.UPLOAD',
                icon : 'upload',
                onClick : function() {
                    _this.uploadTmplFile();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.INFO.INFOTMPLS.BUTTON.TMPL_POINT',
                icon : 'link',
                onClick : function() {
                    _this.loadPointPage();
                }
            } ]
        }
    },

    getFieldGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-tmpl-edit-list-field-grid',
            columns : emcInfoTmpl.getFieldEditListColumns(),
            service : 'emcInfoTmplFieldServiceImpl',
            onUpdated : function(data, fields) {
                if(fields.indexOf('fieldTitle') < 0)
                    return;
                var grid = this;
                var url = Gikam.select.getConfigParam('infoTmplField').url + Gikam.param({
                    infoTmplId : _this.getTmplId()
                });
                Gikam.getJson(url).done(function(items) {
                    // 设置相关列下拉选项
                    var itemGrid = Gikam.getComp('emc-info-tmpl-edit-list-item-grid');
                    var indexs = [];
                    for(var i = 0; itemGrid.getData() && i < itemGrid.getData().length; i++) {
                        indexs[i] = i;
                    }
                    _this.setGridSelectFieldItems(itemGrid, indexs, 'infoTmplFieldId', items, 'id', 'fieldTitle');
                });
            }
        };
    },

    getItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-tmpl-edit-list-item-grid',
            columns : emcInfoTmpl.getItemEditListColumns(),
            url : emcInfoTmpl.baseUrl + '/items/queries',
            requestData : _this.getItemRequestData(),
            service : 'emcTmplFieldItemServiceImpl',
            onLoadSuccess : function(data) {
                var grid = this;
                var url = Gikam.select.getConfigParam('infoTmplField').url + Gikam.param({
                    infoTmplId : _this.getTmplId()
                });
                Gikam.getJson(url).done(function(items) {
                    // 设置相关列下拉选项
                    var indexs = [];
                    for(var i = 0; data && i < data.length; i++) {
                        indexs[i] = i;
                    }
                    _this.setGridSelectFieldItems(grid, indexs, 'infoTmplFieldId', items, 'id', 'fieldTitle');
                });
            },
            deleteFormatter : function(row) {
                /*var grid = Gikam.getComp('emc-info-tmpl-edit-list-item-grid');
                var index = grid.getRowIndexById(row.id);
                var fieldEditor = grid.getField(index, 'infoTmplFieldId');*/
                return row.infoTmplFieldId;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.INFO.INFOTMPLS.MODAL.ADD',
                        fields : emcInfoTmpl.getItemBaseInfoFormFields(),
                        url : Gikam.printf(emcInfoTmpl.baseUrl + '/{tmplId}/items', {
                            tmplId : _this.getTmplId()
                        }),
                        columns : 1,
                        width : 500,
                        height : 320,
                        titleWidth : 80
                    }).done(function(id) {
                        Gikam.getComp('emc-info-tmpl-edit-list-item-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-tmpl-edit-list-item-grid').deleteRows(emcInfoTmpl.baseUrl + '/items');
                }
            } ]
        }
    },

    getItemRequestData : function(tmplId) {
        var _this = this;
        return {
            target_filter : JSON.stringify({
                type : 'assigned',
                targetTable : 'T_EMC_INFO_TMPL_FIELD',
                targetMatchColumn : 'ID',
                thisMatchColumn : 'INFOTMPLFIELDID',
                filter : [ {
                    targetFilterColumn : 'INFOTMPLID',
                    targetFilterValue : (tmplId ? tmplId : _this.getTmplId())
                } ]
            })
        };
    },

    uploadTmplFile : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-info-tmpl-edit-list-grid').getSelections();
        if (!rows || rows.length == 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        } else if (rows.length > 1) {
            Gikam.alert('EMC.MODULE.BP.INFO.INFOTMPLS.TIP.TMPL_ONLY_ONE');
            return;
        }
        var templId = rows[0].id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_EMC_INFO_TMPL$' + templId
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.confirm('EMC.MODULE.BP.INFO.INFOTMPLS.TIP.FILE_OVERRIDE', 'EMC.MODULE.BP.INFO.INFOTMPLS.TIP.FILE_OVERRIDE', function() {
                    workspace.window.showMask(true);
                    Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                        _this.createTmplUploader(templId);
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                });
            } else {
                _this.createTmplUploader(templId);
            }
        });
    },

    createTmplUploader : function(bizId) {
        Gikam.create('simpleUploader', {
            id : 'emc-info-tmpl-edit-list-uploader',
            dbTable : 'T_EMC_INFO_TMPL',
            bizId : bizId,
            multiple : false,
            onAfterClose : function() {
                Gikam.getComp('emc-info-tmpl-edit-list-grid').refresh()
            }
        });
    },

    // 单个模板对应的监测点页面
    loadTmplPointPage : function(tmplId) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.INFO.INFOTMPLS.MODAL.TMPL_POINT',
            url : emcInfoTmpl.baseUrl + '/page/tmpl-point' + Gikam.param({
                id : tmplId
            }),
            width : 600,
            height : 500,
            titleWidth : 100
        });
    },

    // 多个模板对应的监测点页面
    loadPointPage : function() {
        var _this = this;
        var tmplSelections = Gikam.getComp('emc-info-tmpl-edit-list-grid').getSelections();
        if (Gikam.isEmpty(tmplSelections)) {
            Gikam.alert(Gikam.propI18N('EMC.MODULE.BP.INFO.INFOTMPLS.TIP.LINK_TMPL_UNSELECTED'));
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.INFO.INFOTMPLS.MODAL.POINT',
            url : Gikam.choose.getConfig('monitorPointSimple').url,
            width : 500,
            height : 500,
            onAfterClose : function(rows) {
                // 取消则返回
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                Gikam.confirm(Gikam.propI18N('EMC.MODULE.BP.INFO.INFOTMPLS.TIP.CLEAR_WARNING_TITLE'), Gikam.propI18N('EMC.MODULE.BP.INFO.INFOTMPLS.TIP.CLEAR_WARNING_MESSAGE'), function() {
                    workspace.window.showMask(true);
                    Gikam.put(emcInfoTmpl.baseUrl + '/points', Gikam.getJsonWrapper(null, [ 'emcInfoTmplServiceImpl', tmplSelections ], [ 'emcMonitorPointServiceImpl', rows ])).always(function() {
                        workspace.window.closeMask();
                    });
                });
            }
        });
    },

    getTmplId : function() {
        var grid = Gikam.getComp('emc-info-tmpl-edit-list-grid');
        if (!grid || !grid.getActivedRow())
            return;
        return grid.getActivedRow().id;
    },

    setFormSelectFieldItems(form, field, data, valueCol, textCol){
        var items = [];
        if(data) {
            items = data.filter(function(item) {
                return !Gikam.isEmpty(item[textCol]);
            }).map(function(item) {
                return {
                    value : item[valueCol],
                    text : item[textCol]
                };
            });
        }
        form.setSelectOptions(field, items);
    },
    
    setGridSelectFieldItems(grid, indexs, field, data, valueCol, textCol){
        var colIndex = grid.getColumnsIndex(field);
        if(!colIndex || colIndex < 0) {
            return;
        }
        var items = [];
        if(data) {
            items = data.filter(function(item) {
                return !Gikam.isEmpty(item[textCol]);
            }).map(function(item) {
                return {
                    value : item[valueCol],
                    text : item[textCol]
                };
            });
        }
        grid.options.columns[colIndex].items = items;
        if(!indexs) {
            return;
        }
        indexs.forEach(function(index) {
            grid.getField(index, field).items = items;
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-info-tmpl-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.INFO.INFOTMPLS.TAB.TMPL',
                        items : [ _this.getGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.INFO.INFOTMPLS.TAB.FIELD',
                        items : [ _this.getFieldGridParam() ]
                    } ]
                } ]
            },
            east : {
                width : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.INFO.INFOTMPLS.TAB.ITEM',
                        items : [ _this.getItemGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
