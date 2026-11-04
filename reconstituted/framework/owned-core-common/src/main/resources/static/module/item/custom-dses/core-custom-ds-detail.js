coreCustomDs.detailPage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        var _this = this;

        var dsId = _this.param.id;

        var detailUrl = Gikam.printf(coreCustomDs.baseUrl + '/{id}', {
            id : dsId
        });

        window.workspace.window.showMask();
        Gikam.getJson(detailUrl).done(function(ds) {

            var dsBody = null;
            if (!Gikam.isEmpty(ds.dsBody)) {
                dsBody = JSON.parse(ds.dsBody);
            }

            Gikam.create('layout', {
                renderTo : workspace.window.$dom,
                showTitle : false,
                center : {
                    items : [ {
                        type : 'DataSource',
                        id : 'dataSource',
                        bizId : dsId,
                        dataSourceName : ds.dsName,
                        config : dsBody,
                        buttonGroups : [ {
                            type : 'button',
                            title : 'CORE.MODULE.ITEM.CUSTOM_DSES.BUTTON.VIEW_SQL',
                            text : 'CORE.MODULE.ITEM.CUSTOM_DSES.BUTTON.VIEW_SQL',
                            icon : 'preview',
                            color : 'info',
                            class : 'primary',
                            onClick : function() {
                                _this.previewPage(dsId);
                            }
                        } ],
                        // 数据库表相关接口baseUrl
                        modelTableBaseUrl : '/secure/core/module/item/custom-tables',
                        // 自定义SQL相关接口baseUrl
                        modelViewBaseUrl : '/secure/core/module/item/custom-sqls',
                        // 数据源相关接口baseUrl
                        dataSourceBaseUrl : '/secure/core/module/item/custom-dses',
                    } ]
                }
            });
        }).always(() => {
            window.workspace.window.closeMask();
        });

    },
    
    previewPage : function(dsId) {
        // 左侧SQL只读框，右侧字段列表可修改别名
        var _this = this;
        var detailUrl = Gikam.printf(coreCustomDs.baseUrl + '/{id}', {
            id : dsId
        });

        window.workspace.window.showMask();
        Gikam.getJson(detailUrl).done(function(ds) {
            const dsSql = ds.dsSql;
            if (Gikam.isEmpty(ds.dsSql)) {
                Gikam.alert('CORE.MODULE.ITEM.CUSTOM_DSES.TIP.SQL_EMPTY_NEED_SAVE');
                return;
            }
            
            const modalId = 'custom-ds-preview-page-model';
            const modal = Gikam.create('modal', {
                id: modalId,
                isFullScreen: true,
                title: 'CORE.MODULE.ITEM.CUSTOM_DSES.BUTTON.VIEW_SQL',
                width: 1300,
                height: 700,
                items: [{
                    type: 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getPreviewForm() ]
                    },
                    center : {
                        items : [ _this.getPreviewFieldGridParam() ]
                    }
                }]
            }); 

        }).always(() => {
            window.workspace.window.closeMask();
        });
    },

    previewData : function(dsId) {
        window.workspace.window.showMask();
        
        Gikam.post(Gikam.printf(coreCustomDs.baseUrl + '/{id}/preview-columns/queries', {
            id : dsId
        })).done(fields => {
            const columns = [{ index: true }];
            
            fields.forEach(columnName => {
                columns.push({
                    title: columnName,
                    field: columnName,
                    readonly: true,
                    width: 150,
                });
            });
            const modalId = 'custom-ds-preview-data-model';
            const modal = Gikam.create('modal', {
                id: modalId,
                title: 'CORE.MODULE.ITEM.CUSTOM_DSES.BUTTON.PREVIEW',
                width: 900,
                height: 480,
                toolbarTop: false,
                showMinBtn: false,
            });
            
            Gikam.post(Gikam.printf(coreCustomDs.baseUrl + '/{id}/preview', {
                id : dsId
            })).done(rows => {
                Gikam.create('layout', {
                    renderTo: modal.window.$dom,
                    center: {
                        items: [
                            {
                                type: 'grid',
                                page: false,
                                toolbarHidden: true,
                                columns: columns,
                                filterOpen: false,
                                data: rows,
                                onRendered: () => {
                                    const toolbar = document
                                         .getElementById(modalId)
                                        ?.querySelector('.toolbar.right');
                                    if (!toolbar) {
                                        return;
                                    }
                                    toolbar.parentElement.style.height = '40px';
                                    toolbar.setAttribute('style', 'justify-content: flex-end');
                                    toolbar.innerHTML = '<div>' + Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_DSES.TIP.DATA_MOST_COUNT') + '</div>';
                                },
                            },
                        ],
                    },
                });
            }).always(() => {
                window.workspace.window.closeMask();
            });
            
        }).always(() => {
            window.workspace.window.closeMask();
        });
    },
    
    previewForm : null,
    getPreviewForm : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            panels : [ {
                title : 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.SQL',
                id : 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.SQL',
                items : [ {
                    type : 'form',
                    id : 'core-custom-ds-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreCustomDsServiceImpl',
                    fields : coreCustomDs.getPreviewFormFields(),
                    columns : 3,
                    onRendered : function() {
                        _this.previewForm = this;
                        var containerDom = Array.from(Gikam.getComp('custom-ds-preview-page-model').window.$dom.querySelectorAll('.tab-pane')).filter(item => item.id === 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.SQL')[0];
                        containerDom.querySelector('textarea').style.height = containerDom.offsetHeight - 23 + 'px'
                    },
                    onLoadSuccess : function(data) {
                        //_this.previewForm.toFieldsReadonly([ 'dsSql' ]);
                    }
                } ]
            } ]
        }
    },
    
    fieldGrid : null,
    getPreviewFieldGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            panels : [ {
                title : 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.DS_FIELD',
                id : 'CORE.MODULE.ITEM.CUSTOM_DSES.TAB.DS_FIELD',
                items : [ {
                    type : 'grid',
                    id : 'core-custom-ds-field-list-grid',
                    url : _this.getCompUrl('fieldGrid'),
                    service : 'coreCustomDsFieldServiceImpl',
                    onRendered : function() {
                        _this.fieldGrid = this;
                    },
                    columns : coreCustomDs.getFieldEditListColumns(),
                    page : false,
                    toolbar : [ {
                        type : 'button',
                        title : 'CORE.MODULE.ITEM.CUSTOM_DSES.BUTTON.PREVIEW',
                        text : 'CORE.MODULE.ITEM.CUSTOM_DSES.BUTTON.PREVIEW',
                        color : 'info',
                        icon : 'preview',
                        onClick : function() {
                            _this.previewData(_this.param.id);
                        }
                    } ]
                } ]
            } ]
        }
    },
    
    urlManager : {
        baseInfoForm : coreCustomDs.baseUrl + '/{id}',
        fieldGrid : coreCustomDs.baseUrl + '/{id}/fields/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-custom-ds-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-custom-ds-field-list-grid' : this.getCompUrl('fieldGrid'),
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

}
