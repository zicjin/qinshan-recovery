emcInfoData.editPage = {

    getInfoConfigTree : function() {
        var _this = this;
        return {
            type : 'tree',
            id : 'emc-info-data-edit-list-config-tree',
            nodeTextField : 'configName',
            name : 'EMC.MODULE.BP.INFO.INFODATAS.TITLE.INFO_QUERY',
            url : IFM_CONTEXT + '/secure/emc/module/bp/info/info-configs/tree/{parentId}',
            requestData : {},
            onNodeSelected : function() {
                _this.onTreeSelected();
            },
        }
    },

    onTreeSelected : function() {
        var _this = this;
        var treeSelections = Gikam.getComp('emc-info-data-edit-list-config-tree').options.selectedNodeData;
        var infoTmplId = treeSelections.infoTmplId;
        var configType = treeSelections.configType;
        var tab = Gikam.getComp('emc-info-data-edit-list-base-info-tab');
        var nodeText = treeSelections.text;
        var parentId = treeSelections.parentId;

        tab.show();
        switch (configType) {
        case 'BCY':
            tab.hide();
            break;
        case 'SCY':
            tab.revertPanel('emc-info-data-edit-list-panel');
            tab.revertPanel('emc-info-data-edit-list-uploader-panel');
            tab.showPanel('emc-info-data-edit-list-panel');
            _this.showInfoData(infoTmplId, treeSelections.id);
            _this.showFileData(treeSelections.id);
            break;
        case 'DCY':
            tab.removePanel('emc-info-data-edit-list-uploader-panel');
            tab.revertPanel('emc-info-data-edit-list-panel');
            _this.showInfoData(infoTmplId, treeSelections.parentId);
            break;
        case 'ACY':
            tab.removePanel('emc-info-data-edit-list-panel');
            tab.revertPanel('emc-info-data-edit-list-uploader-panel');
            _this.showFileData(treeSelections.parentId);
            break;
        default:
            break;
        }
        if (!Gikam.isEmpty(parentId)) {
            Gikam.getJson('/secure/emc/module/bp/info/info-configs/'+parentId).done(function(data) {
                if(nodeText.includes(Gikam.propI18N('EMC.MODULE.BP.INFO.INFODATAS.TITLE.WATER')) || data.configName.includes(Gikam.propI18N('EMC.MODULE.BP.INFO.INFODATAS.TITLE.WATER'))) {
                    Gikam.getComp('emc-info-data-edit-list-button-month').show();
                    Gikam.getComp('emc-info-data-edit-list-button-year').show();
                }else{
                    Gikam.getComp('emc-info-data-edit-list-button-month').hide();
                    Gikam.getComp('emc-info-data-edit-list-button-year').hide();
                }
            });
        }
    },

    showInfoData : function(infoTmplId, infoConfigId) {
        var _this = this;
        _this.infoDataGrid.options.requestData = {};
        _this.infoDataGrid.options.columns = [];
        var columns = _this.getColumns(infoTmplId);
        _this.infoDataGrid.refreshColumns(columns);
        var url = emcInfoData.baseUrl + '/queries/raw';
        this.infoDataGrid.refresh({
            url : url,
            requestData : {
                infoConfigId_SEQ : infoConfigId,
            }
        });
    },

    showFileData : function(infoConfigId) {
        var uploader = Gikam.getComp('emc-info-data-edit-list-uploader');
        uploader.setOptions({
            bizId : infoConfigId
        })
        uploader.grid.refresh();
    },

    // 获取动态表头，如果需要调整列宽度，修改字各width即可
    getColumns : function(infoTmplId) {
        var _this = this;
        var columns = [];
        columns.push({
            checkbox : true,
        }, {
            title : 'T_EMC_INFO_DATA.EXPORTTIME',
            field : 'createdTime',
            width : 150,
            fixed : true
        }, {
            index : true,
            fixed : true
        });
        var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/info/info-tmpls/fields/queries/selectable');
        var form = Gikam.getComp('emc-info-data-edit-list-query-form');
        form.cleanData();
        Gikam.getJsonSync(url + Gikam.param({
            infoTmplId : infoTmplId
        })).done(function(data) {
            data.forEach(function(item) {
                if (Gikam.isNotEmpty(item.field) && Gikam.isNotEmpty(item.fieldTitle)) {
                    if(item.primaryName==='MonitorDate'){
                        columns.push({
                            title : item.fieldTitle,
                            field : 'monitorDate',
                            width : item.width || 120,
                        });
                    }else{
                        columns.push({
                            title : item.fieldTitle,
                            field : item.field.toLowerCase(),
                            width : item.width || 120,
                        }); 
                    }
                }
            });
        });
        return columns;
    },
    
    infoDataGrid : null,
    getBaseInfoTab : function() {
        var _this = this;
        var columns = [];
        return {
            type : 'tab',
            id : 'emc-info-data-edit-list-base-info-tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.INFO.INFODATAS.TAB.DATA_DETAIL',
                id : 'emc-info-data-edit-list-panel',
                items : [ {
                    type : 'grid',
                    id : 'emc-info-data-edit-list-grid',
                    filter : false,
                    onRendered : function() {
                        _this.infoDataGrid = this;
                        Gikam.getComp('emc-info-data-edit-list-button-month').hide();
                        Gikam.getComp('emc-info-data-edit-list-button-year').hide();
                    },
                    order : false,
                    columns : columns,
                    toolbar : [ {
                        type : 'form',
                        id : 'emc-info-data-edit-list-query-form',
                        columns : 4,
                        fields : [ {
                            title : 'EMC.MODULE.BP.INFO.INFODATAS.FORM.YEAR',
                            field : 'year',
                            type : 'year',
                            refreshGrid : false
                        }, {
                            title : 'EMC.MODULE.BP.INFO.INFODATAS.FORM.MONTH',
                            field : 'month',
                            type : 'select',
                            search : true,
                            category : 'sys',
                            param : {
                                codeCategoryId : 'month',
                            },
                            refreshGrid : false
                        } ],
                    }, {
                        type : 'button',
                        text : 'GIKAM.SEARCH',
                        icon : 'search',
                        onClick : function() {
                            var form = Gikam.getComp('emc-info-data-edit-list-query-form');
                            var year = form.getData().year;
                            var month = form.getData().month;
                            _this.infoDataGrid.refresh({
                                requestData : {
                                    year : year,
                                    month : month
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-info-data-edit-list-grid').deleteRows(emcInfoData.baseUrl);
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.INFO.INFODATAS.BUTTON.EXCELIMPORT',
                        icon : 'add',
                        onClick : function() {
                            var treeSelections = Gikam.getComp('emc-info-data-edit-list-config-tree').options.selectedNodeData;
                            if (Gikam.isEmpty(treeSelections)) {
                                Gikam.alert('EMC.MODULE.BP.INFO.INFODATAS.TIP.CHOOSE_ONE');
                                return;
                            }
                            var configType = treeSelections.configType;
                            var nodeId = '';
                            if (configType === 'SCY') {
                                nodeId = treeSelections.id;
                            } else {
                                nodeId = treeSelections.parentId;
                            }
                            Gikam.importExcel({
                                url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcInfoDataImportFileFunction',
                                dbTable : 'T_EMC_INFO_CONFIG',
                                bizId : nodeId,
                                onImportSuccess : function(files) {
                                    Gikam.getComp('emc-info-data-edit-list-grid').refresh();
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.INFO.INFODATAS.BUTTON.CALCULATE_MONTH',
                        icon : 'modify',
                        id : 'emc-info-data-edit-list-button-month',
                        onClick : function() {
                            _this.calculateByMonth();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.BP.INFO.INFODATAS.BUTTON.CALCULATE_YEAR',
                        icon : 'modify',
                        id : 'emc-info-data-edit-list-button-year',
                        onClick : function() {
                            _this.calculateByYear();
                        }
                    } ]
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                id : 'emc-info-data-edit-list-uploader-panel',
                items : [ {
                    type : 'uploader',
                    id : 'emc-info-data-edit-list-uploader',
                    filter : false,
                    readonly : true,
                    dbTable : 'T_EMC_INFO_CONFIG',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : 240,
                items : [ _this.getInfoConfigTree() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
        Gikam.getAsyncComp('emc-info-data-edit-list-base-info-tab').done(function(tab) {
            tab.show = false;
        });
    },
    
    calculateByMonth : function() {
        var url = '/secure/emc/module/bp/info/info-datas/action/calculate';
        var form = Gikam.getComp('emc-info-data-edit-list-query-form');
        var year = form.getData().year;
        var month = form.getData().month;
        if(Gikam.isEmpty(year) || Gikam.isEmpty(month)){
            Gikam.alert('EMC.MODULE.BP.INFO.INFODATAS.TIP.CALCULATE_MONTH_CHOOSE_DATE');
            return;
        }
        var treeSelections = Gikam.getComp('emc-info-data-edit-list-config-tree').options.selectedNodeData;
        var jsonWrapper = Gikam.getJsonWrapper({
            f : {
                year : year,
                month : month,
                infoConfigId_SEQ : treeSelections.parentId,
                infoConfigId : treeSelections.parentId,
                calType : 'month'
            },
        });
        Gikam.post(url, jsonWrapper);
    },
    calculateByYear : function() {
        var url = '/secure/emc/module/bp/info/info-datas/action/calculate';
        var form = Gikam.getComp('emc-info-data-edit-list-query-form');
        var year = form.getData().year;
        if(Gikam.isEmpty(year) || Gikam.isEmpty(month)){
            Gikam.alert('EMC.MODULE.BP.INFO.INFODATAS.TIP.CALCULATE_YEAR_CHOOSE_DATE');
            return;
        }
        var treeSelections = Gikam.getComp('emc-info-data-edit-list-config-tree').options.selectedNodeData;
        var jsonWrapper = Gikam.getJsonWrapper({
            f : {
                year : year,
                infoConfigId_SEQ : treeSelections.parentId,
                infoConfigId : treeSelections.parentId,
                calType : 'year'
            },
        });
        Gikam.post(url, jsonWrapper);
    }

};