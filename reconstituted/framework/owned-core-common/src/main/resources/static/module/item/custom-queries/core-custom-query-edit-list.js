coreCustomQuery.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-query-edit-list-grid',
            url : coreCustomQuery.baseUrl + '/queries/raw',
            service : 'coreCustomQueryServiceImpl',
            dbTable : 'T_CORE_CUSTOM_QUERY',
            columns : coreCustomQuery.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'queryName' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function (row) {
                return row.queryName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.preInsert();
                }
            },  {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'preview',
                onClick : function() {
                    var selections = Gikam.getComp('core-custom-query-edit-list-grid').getSelections()
                    if(Gikam.isEmpty(selections)){
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    if(selections.length > 1){
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if(Gikam.isEmpty(selections[0].reportConfigLineId)){
                        Gikam.create('modal', {
                            url: IFM_CONTEXT + '/secure/core/module/item/custom-queries/page/grid-menu-list' + Gikam.param({
                                customQueryId : selections[0].id,
                                modal : '1'
                            }),
                            title: 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.PREVIEW',
                            width: Gikam.width.L,
                        })
                    }else {
                        Gikam.create('modal', {
                            url: IFM_CONTEXT + '/secure/core/module/item/custom-queries/page/report-menu-list' + Gikam.param({
                                customQueryId : selections[0].id,
                                reportConfigLineId :  selections[0].reportConfigLineId,
                                modal : '1'
                            }),
                            title: 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.PREVIEW'
                        })
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-custom-query-edit-list-grid').getSelections();
                    for (let selectedRow of selectedRows) {
                        if(selectedRow.publishStatus === '1'){
                            Gikam.alert('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.NO_DELETE');
                            return false;
                        }
                    }
                    Gikam.getComp('core-custom-query-edit-list-grid').deleteRows(coreCustomQuery.baseUrl)
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.RELEASE',
                icon : 'pass',
                onClick : function() {
                    var selections = Gikam.getComp('core-custom-query-edit-list-grid').getSelections()
                    if(Gikam.isEmpty(selections)){
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    if(selections.length > 1){
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if(selections[0].publishStatus ==='1'){
                        Gikam.alert('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.ALREADY_RELEASE');
                        return;
                    }
                    _this.release(selections[0]);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.CANCEL_RELEASE',
                icon : 'cancel',
                onClick : function() {
                    var selections = Gikam.getComp('core-custom-query-edit-list-grid').getSelections()
                    if(Gikam.isEmpty(selections)){
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    if(selections.length > 1){
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if(selections[0].publishStatus ==='0'){
                        Gikam.alert('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.NO_RELEASE');
                        return;
                    }
                    Gikam.create('confirm', {
                        message : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.CANCEL_RELEASE'),
                        onYesClick : function() {
                            workspace.window.showMask(true);
                            Gikam.put(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/action/revoke',{
                                id : selections[0].id,
                            })).done(function () {
                                Gikam.getComp('core-custom-query-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            }, ]
        }
    },

    stepName : 'customQuery',
    queryForm : {},
    customQueryId : null,
    preInsert : function () {
        var _this = this;
        Gikam.getJsonSync(coreCustomQuery.baseUrl + '/action/next-identity').done(
            function (data) {
                _this.customQueryId = data;
            }
        )
        var modal = Gikam.create('modal', {
            id : 'modal',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD',
            width : 900,
            height : 550,
            onAfterClose() {
                _this.stepName = 'customQuery'
                Gikam.getComp('core-custom-query-edit-list-grid').refresh();
                _this.queryForm = {};
            }
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            id : 'core-custom-query-layout',
            north : {
                height : 32,
                items : [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_FIELD'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '4',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '5',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            },
            center : {
                items : [{
                    type: 'form',
                    id: 'core-custom-query-edit-list-form',
                    autoSave : false,
                    caption : {
                        text : _this.getCaptionHtml(['查询类型分为表格类型和帆软类型','表格查询类型：维护自定义查询信息之后发布生成一个grid表格的自定义查询菜单，将自定义数据源以表格的形式展示','帆软查询类型：维护自定义查询信息之后发布生成一个帆软报表的自定义查询菜单，给帆软报表提供自定义查询条件'])
                    },
                    fields : coreCustomQuery.getBaseInfoFormFields()
                }]
            },
            south : {
                height : 5,
                items : [{
                    type : 'btnToolbar',
                    items : [{
                        type : 'button',
                        id : 'core-custom-query-last-step-button',
                        text : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.LAST_STEP'),
                        icon : 'move-left',
                        onClick : function () {
                            if(_this.stepName === 'customQuery'){

                            } else if(_this.stepName === 'dataSource'){
                                _this.stepName = 'customQuery'
                                Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                Gikam.getComp('core-custom-query-last-step-button').hide();
                                Gikam.getComp('core-custom-query-edit-list-form').setData({
                                    queryName : _this.queryForm.queryName,
                                    queryCategory : _this.queryForm.queryCategory
                                })
                            }else if(_this.stepName === 'field'){
                                _this.stepName = 'dataSource'
                                Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                if(_this.queryForm.dsType === 'customDs'){
                                    Gikam.getComp('core-custom-query-edit-list-form').setData({
                                        dsType : _this.queryForm.dsType,
                                        customDs : _this.queryForm.customDs,
                                        customDsId : _this.queryForm.customDsId
                                    })
                                }else {
                                    Gikam.getComp('core-custom-query-edit-list-form').setData({
                                        dsType : _this.queryForm.dsType,
                                        customSql : _this.queryForm.customSql
                                    })
                                }
                            }else if(_this.stepName === 'button'){
                                _this.stepName = 'field'
                                Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                            }else {
                                if(_this.queryForm.queryCategory === 'fine'){
                                    _this.stepName = 'dataSource'
                                    Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                    Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                    Gikam.getComp('core-custom-query-edit-list-form').setData({
                                        report : _this.queryForm.report,
                                        reportConfigLineId : _this.queryForm.reportConfigLineId
                                    })
                                }else {
                                    _this.stepName = 'button'
                                    Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                    Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                }
                                Gikam.getComp("core-custom-query-next-step-button").show();
                                Gikam.getComp('core-custom-query-finish-button').hide();

                            }
                        },
                        onRendered : function () {
                            if(_this.stepName === 'customQuery'){
                                this.hide();
                            }
                        }
                    },{
                        type : 'button',
                        id : 'core-custom-query-next-step-button',
                        text : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.NEXT_STEP'),
                        icon : 'move-right',
                        onClick : function () {
                            if(_this.stepName === 'customQuery'){
                                var form = Gikam.getComp('core-custom-query-edit-list-form').getData();
                                _this.queryForm.queryName = form.queryName;
                                _this.queryForm.queryCategory = form.queryCategory;
                                _this.queryForm.id = _this.customQueryId;
                                if(Gikam.getComp('core-custom-query-edit-list-form').validate()){
                                    _this.stepName = 'dataSource';
                                    Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                    Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                    if(_this.queryForm.queryCategory !== 'fine'){
                                        Gikam.getComp('core-custom-query-edit-list-form').setData({
                                            dsType : _this.queryForm.dsType,
                                            customDs : _this.queryForm.customDs,
                                            customDsId : _this.queryForm.customDsId
                                        })
                                    }else {
                                        Gikam.getComp('core-custom-query-edit-list-form').setData({
                                            report : _this.queryForm.report,
                                            reportConfigLineId : _this.queryForm.reportConfigLineId
                                        })
                                        _this.queryForm.dsType = '';
                                        _this.queryForm.customSql = '';
                                        _this.queryForm.customDs = '';
                                    }
                                    Gikam.getComp('core-custom-query-last-step-button').show();
                                }
                            }else if(_this.stepName === 'dataSource'){
                                var form = Gikam.getComp('core-custom-query-edit-list-form').getData();
                                if(_this.queryForm.queryCategory === 'fine'){
                                    _this.queryForm.reportConfigLineId = form.reportConfigLineId;
                                    _this.queryForm.report = form.report;
                                    if(Gikam.getComp('core-custom-query-edit-list-form').validate()){
                                        _this.stepName = 'condition';
                                        Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                        Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                        Gikam.getComp('core-custom-query-finish-button').show();
                                        Gikam.getComp('core-custom-query-next-step-button').hide();
                                    }

                                }else{
                                    _this.queryForm.dsType = form.dsType;
                                    _this.queryForm.customDsId = form.customDsId;
                                    _this.queryForm.customDs = form.customDs;
                                    if(Gikam.getComp('core-custom-query-edit-list-form').validate()){
                                        _this.stepName = 'field'
                                        // if(_this.queryForm.dsType === 'customDs'){
                                        // }
                                        Gikam.postText(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/{customDsId}/action/add-query-field', {
                                            id : _this.customQueryId,
                                            customDsId : form.customDsId
                                        })).done(function () {
                                            Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                            Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                        })
                                        // else {
                                        //     Gikam.post(Gikam.printf(coreCustomQuery.baseUrl + '/{customQueryId}/{sql}/action/add-fields', {
                                        //         customQueryId : _this.customQueryId,
                                        //         sql : _this.queryForm.customSql
                                        //     })).done(function () {
                                        //         Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                        //         Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                        //     })
                                        // }
                                    }
                                }
                            }else if(_this.stepName === 'field'){
                                _this.stepName = 'button'
                                Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                            }else if(_this.stepName === 'button'){
                                _this.stepName = 'condition'
                                Gikam.getComp('core-custom-query-layout').getPosition('north').refreshItems(_this.getItems());
                                Gikam.getComp('core-custom-query-layout').getPosition('center').refreshItems(_this.getCenterItems());
                                Gikam.getComp('core-custom-query-finish-button').show();
                                Gikam.getComp('core-custom-query-next-step-button').hide();
                            }
                        }
                    },{
                        type : 'button',
                        id : 'core-custom-query-finish-button',
                        text : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.COMPLETE'),
                        icon : 'default',
                        onClick : function () {
                            _this.stepName = 'customQuery';
                            Gikam.postSync(coreCustomQuery.baseUrl , Gikam.getJsonWrapper(null, [ 'coreCustomQueryServiceImpl', [_this.queryForm]])).done(function () {
                                modal.close();
                            })
                        },onRendered : function () {
                            if(_this.stepName !== 'condition'){
                                this.hide();
                            }
                        }
                    },{
                        type : 'button',
                        id : 'core-custom-query-cancel-button',
                        text : Gikam.propI18N('GIKAM.BUTTON.CANCEL'),
                        icon : 'cancel',
                        onClick : function () {
                            _this.stepName = 'customQuery'
                            _this.queryForm = {};
                            modal.close();
                        }
                    }]
                }]
            }
        })
    },

    release : function (data) {
        var modal = Gikam.create('modal', {
            id : 'modal',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.RELEASE_MENU',
            width : 400,
            height : 260,
            onAfterClose() {
                Gikam.getComp('core-custom-query-edit-list-grid').refresh();
            }
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            north : {
                height : 5,
                items : [{
                    type : 'btnToolbar',
                    items : [{
                        type : 'button',
                        text : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.RELEASE'),
                        icon : 'default',
                        onClick : function() {
                            if(!Gikam.getComp('core-custom-query-release-list-form').validate()){
                                return;
                            }
                            var releaseForm = Gikam.getComp('core-custom-query-release-list-form');
                            Gikam.getComp('core-custom-query-release-list-form').setData({
                                id : data.id,
                                orderNo : data.id,
                                menuInitName : releaseForm.getData().menuName
                            });
                            var querys = [releaseForm.getData()];
                            workspace.window.showMask(true);
                            Gikam.postText(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/action/publish',{
                                id : data.id
                            }), Gikam.getJsonWrapper(null, [ 'coreMenuServiceImpl', querys])).done(function () {
                                Gikam.create('confirm', {
                                    message : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.RELEASE'),
                                    onYesClick : function() {
                                        modal.close();
                                    }
                                });
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }]
                }]
            },
            center : {
                items : [{
                    type: 'form',
                    id: 'core-custom-query-release-list-form',
                    fields : coreCustomQuery.getReleaseColumns()
                }]
            }
        })

    },

    getItems : function () {
        var _this = this;
        if(_this.queryForm.queryCategory === 'fine'){
            if(_this.stepName === 'customQuery'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            }
            if(_this.stepName === 'dataSource'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            }
            if(_this.stepName === 'condition'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'next',
                        onItemClick(item) { }
                    }]
                }]
            }
        }else {
            if(_this.stepName === 'customQuery'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_FIELD'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '4',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '5',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            }
            if(_this.stepName === 'dataSource'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_FIELD'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '4',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '5',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            }
            if(_this.stepName === 'field'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_FIELD'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '4',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                        status: 'wait',
                        onItemClick(item) { }
                    }, {
                        value: '5',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            }
            if(_this.stepName === 'button'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_FIELD'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '4',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                        status: 'next',
                        onItemClick(item) { }
                    }, {
                        value: '5',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'wait',
                        onItemClick(item) { }
                    }]
                }]
            }
            if(_this.stepName === 'condition'){
                return [{
                    type: 'steps',
                    id: 'steps',
                    items: [{
                        value: '1',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.QUERY_TYPE'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '2',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CHOOSE_DS'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '3',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_FIELD'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '4',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                        status: 'process',
                        onItemClick(item) { }
                    }, {
                        value: '5',
                        text: Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                        status: 'next',
                        onItemClick(item) { }
                    }]
                }]
            }
        }

    },

    getCenterItems : function (items) {
        var _this = this;
        if(_this.stepName === 'customQuery'){
            return [{
                type: 'form',
                id: 'core-custom-query-edit-list-form',
                autoSave : false,
                fields : coreCustomQuery.getBaseInfoFormFields(),
                caption : {
                    text  : _this.getCaptionHtml(['查询类型分为表格类型和帆软类型','表格查询类型：维护自定义查询信息之后发布生成一个grid表格的自定义查询菜单，将自定义数据源以表格的形式展示','帆软查询类型：维护自定义查询信息之后发布生成一个帆软报表的自定义查询菜单，给帆软报表提供自定义查询条件']),
                },
            }]
        }
        if(_this.stepName === 'dataSource'){

            if(_this.queryForm.queryCategory === 'fine'){
                return [{
                    type: 'form',
                    id: 'core-custom-query-edit-list-form',
                    autoSave : false,
                    fields : coreCustomQuery.getFrDataResource(),
                    caption : {
                        text  : _this.getCaptionHtml(['帆软报表类型请在报表配置菜单维护好报表配置信息，并启用，报表参数需要置空，报表查询条件请在后续步骤配置','帆软报表类型自定义查询旨在让帆软报表支持自定义条件在合理化的范围内进行查询'])
                    }
                }]
            }else {
                return [{
                    type: 'form',
                    id: 'core-custom-query-edit-list-form',
                    fields : coreCustomQuery.getDataResource(),
                    caption : {
                        text  : _this.getCaptionHtml(['自定义数据源取自于自定义数据源菜单里面已发布的数据，其自定义的查询列作为表格的展示列也可作为数据源的查询条件','使用该类型自定义查询时，请先在自定义数据源菜单维护好自定义数据源基本信息，并选择启用'])
                    },
                    onRendered : function () {
                        // if(_this.queryForm.dsType === 'customDs'){
                        //     Gikam.getComp('core-custom-query-edit-list-form').hideFields(['customSql'])
                        // } else if(_this.queryForm.dsType === 'customSql'){
                        //     Gikam.getComp('core-custom-query-edit-list-form').hideFields(['customDs'])
                        // }else {
                        //     Gikam.getComp('core-custom-query-edit-list-form').hideFields(['customSql','customDs'])
                        // }

                    }
                }]
            }
        }
        if(_this.stepName === 'field'){
            return [ {
                type : 'grid',
                id : 'core-custom-query-field-list-grid',
                service : 'coreCustomQueryFieldServiceImpl',
                url : coreCustomQuery.baseUrl + '/fields/queries',
                requestData : {
                    customQueryId_EQ : _this.customQueryId
                },
                columns : coreCustomQuery.getFieldEditListColumns(_this.selectItems),
                page : false,
                onLoadSuccess : function (rows) {
                    for (let i=0; i<rows.length;i++) {
                        if(rows[i].dsCategory === 'system'){
                            Gikam.getComp('core-custom-query-field-list-grid').getField(i,'dataSource').setItems(_this.selectItems);
                        }
                    }
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.MOVE_UP',
                    icon : 'move-up',
                    onClick : function() {
                        Gikam.getComp('core-custom-query-field-list-grid').moveUp(coreCustomQuery.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.MOVE_DOWN',
                    icon : 'move-down',
                    onClick : function() {
                        Gikam.getComp('core-custom-query-field-list-grid').moveDown(coreCustomQuery.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                    }
                } ]
            } ]
        }
        if(_this.stepName === 'button'){
            return [ {
                type : 'grid',
                id : 'core-custom-query-button-list-grid',
                service : 'coreCustomQueryButtonServiceImpl',
                url : coreCustomQuery.baseUrl + '/buttons/queries',
                requestData : {
                    customQueryId_EQ : _this.customQueryId
                },
                columns : coreCustomQuery.getButtonEditListColumns(),
                deleteFormatter : function(row) {
                    return row.buttonName || row.id;
                },
                page : false,
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.create('modal', {
                            url : IFM_CONTEXT + '/secure/core/module/item/custom-buttons/page/choose-list',
                            title : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON'),
                            width : Gikam.width.L,
                            onAfterClose : function (rows) {
                                if(rows){
                                    var buttons = [];
                                    for (let row of rows) {
                                        buttons.push({
                                            buttonName : row.buttonName,
                                            buttonIcon : row.buttonIcon,
                                            buttonLogic : row.buttonLogic,
                                            customQueryId : _this.customQueryId
                                        });
                                    }
                                    Gikam.postText(coreCustomQuery.baseUrl + '/query-buttons', Gikam.getJsonWrapper(null, [ 'coreCustomQueryButtonServiceImpl', buttons])).done(
                                        function () {
                                            Gikam.getComp('core-custom-query-button-list-grid').refresh();
                                        }
                                    );
                                }
                            }
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-custom-query-button-list-grid').deleteRows(coreCustomQuery.baseUrl + '/query-buttons');
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.MOVE_UP',
                    icon : 'move-up',
                    onClick : function() {
                        Gikam.getComp('core-custom-query-button-list-grid').moveUp(coreCustomQuery.baseUrl + '/buttons/action/swap/{firstId}/{secondId}')
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.MOVE_DOWN',
                    icon : 'move-down',
                    onClick : function() {
                        Gikam.getComp('core-custom-query-button-list-grid').moveDown(coreCustomQuery.baseUrl + '/buttons/action/swap/{firstId}/{secondId}')
                    }
                } ]
            } ]
        }
        if(_this.stepName === 'condition'){
            var _this = this;
            if(_this.queryForm.queryCategory === 'fine'){
                return  [ {
                    type : 'grid',
                    id : 'core-custom-query-condition-fr-list-grid',
                    service : 'coreCustomQueryConditionServiceImpl',
                    url : coreCustomQuery.baseUrl + '/conditions/queries',
                    requestData : {
                        customQueryId_EQ : _this.customQueryId
                    },
                    columns : coreCustomQuery.getFrConditionEditListColumns(_this.selectItems,_this.dsCategory),
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    page : false,
                    onLoadSuccess : function (rows) {
                        _this.initSelect(rows,'core-custom-query-condition-fr-list-grid',_this.selectItems,_this.dsCategory)
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.conditionPreInsert(_this.selectItems,_this.dsCategory);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-custom-query-condition-fr-list-grid').deleteRows(coreCustomQuery.baseUrl + '/query-conditions')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-custom-query-condition-fr-list-grid').moveUp(coreCustomQuery.baseUrl + '/conditions/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-custom-query-condition-fr-list-grid').moveDown(coreCustomQuery.baseUrl + '/conditions/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]

            }else {
                return  [ {
                    type : 'grid',
                    id : 'core-custom-query-condition-list-grid',
                    service : 'coreCustomQueryConditionServiceImpl',
                    url : coreCustomQuery.baseUrl + '/conditions/queries',
                    requestData : {
                        customQueryId_EQ : _this.customQueryId
                    },
                    columns : coreCustomQuery.getConditionEditListColumns(_this.selectItems,_this.dsCategory),
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    page : false,
                    onLoadSuccess : function (rows) {
                        _this.initSelect(rows,'core-custom-query-condition-list-grid',_this.selectItems,_this.dsCategory)
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            Gikam.create('modal', {
                                url : IFM_CONTEXT + '/secure/core/module/item/custom-queries/page/field-choose-list'+ Gikam.param({
                                    show_SEQ : '1',
                                    customQueryId_EQ : _this.customQueryId,
                                }),
                                title : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
                                width : Gikam.width.L,
                                onAfterClose : function (rows) {
                                    if(rows){
                                        var conditions = [];
                                        for (let row of rows) {
                                            conditions.push({
                                                fieldName : row.fieldName,
                                                title : row.columnName,
                                                customQueryId : _this.customQueryId
                                            });
                                        }
                                        Gikam.postSync(coreCustomQuery.baseUrl + '/query-conditions', Gikam.getJsonWrapper(null, [ 'coreCustomQueryConditionServiceImpl', conditions]))
                                        Gikam.getComp('core-custom-query-condition-list-grid').refresh();
                                    }
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            Gikam.getComp('core-custom-query-condition-list-grid').deleteRows(coreCustomQuery.baseUrl + '/query-conditions').done(function () {
                                Gikam.getComp('core-custom-query-field-list-grid').refresh();
                            })
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-custom-query-condition-list-grid').moveUp(coreCustomQuery.baseUrl + '/conditions/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-custom-query-condition-list-grid').moveDown(coreCustomQuery.baseUrl + '/conditions/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]
            }

        }
    },



    getCaptionHtml:function(textArr){
        let html = '<div class="custom-query-form-caption-container"><div class="custom-query-form-caption-header"><img height="20" width="20" src='+Gikam.IFM_CONTEXT+'/static/module/item/custom-queries/img/info.svg/><div>说明</div></div><div class="custom-query-form-caption-content">';
        textArr.forEach(function(text){
            html+='<li>' + text + '</li>'
        });
        html+='</div></div>';
        return html;
    },


    conditionPreInsert : function (items,dsCategory) {
        var _this = this;
        Gikam.preInsert({
            modalTitle : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION'),
            id : 'core-custom-query-condition-list-form',
            fields : coreCustomQuery.getFrConditionAddListColumns(items,dsCategory),
            url : coreCustomQuery.baseUrl+'/query-conditions',
            height : 430,
            param : { customQueryId : _this.customQueryId},
            onFormRendered : function () {
                this.hideFields(['dataSource','customSql','dsCategory','chooseField'])
            }
        }).done(function () {
            Gikam.getComp('core-custom-query-condition-fr-list-grid').refresh();
        })
    },

    initSelect : function (rows,gridId,items,dsCategory) {
        for (let i=0; i<rows.length;i++) {
            if (rows[i].conditionCategory === 'string'){
                coreCustomQuery.getMacthItems('string-match-pattern').done(function (data) {
                    Gikam.getComp(gridId).getField(i,'matchPattern').setItems(data);
                })
            }
            if (rows[i].conditionCategory === 'number'){
                coreCustomQuery.getMacthItems('number-match-pattern').done(function (data) {
                    Gikam.getComp(gridId).getField(i,'matchPattern').setItems(data);
                })
            }
            if (rows[i].conditionCategory === 'select'){
                if(rows[i].dsCategory === 'system'){
                    Gikam.getComp(gridId).getField(i,'dataSource').setItems(items);
                }
                coreCustomQuery.getMacthItems('select-match-pattern').done(function (data) {
                    Gikam.getComp(gridId).getField(i,'matchPattern').setItems(data);
                });
                Gikam.getComp(gridId).getField(i,'dsCategory').setItems(coreCustomQuery.getDsCategory('select'));
            }
            if (rows[i].conditionCategory === 'choose'){
                if(rows[i].dsCategory === 'system'){
                    Gikam.getComp(gridId).getField(i,'dataSource').setItems(dsCategory);
                }
                coreCustomQuery.getMacthItems('select-match-pattern').done(function (data) {
                    Gikam.getComp(gridId).getField(i,'matchPattern').setItems(data);
                });
                Gikam.getComp(gridId).getField(i,'dsCategory').setItems(coreCustomQuery.getDsCategory('choose'));
            }
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreCustomQuery.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        var items = [];
        var dsCategory = [];
        Gikam.getJson(IFM_CONTEXT + '/secure/core/module/admin/select-configs/queries/selectable').done(
            function (r) {
                for (let rElement of r) {
                    items.push({
                        text : rElement.name,
                        value : rElement.category
                    })
                }
            }
        );
        Gikam.getJson(IFM_CONTEXT + '/secure/core/module/admin/choose-configs/queries/selectable').done(
            function (r) {
                for (let rElement of r) {
                    dsCategory.push({
                        text : rElement.name,
                        value : rElement.category
                    })
                }
            }
        )
        this.selectItems = items;
        this.dsCategory = dsCategory;
        Gikam.create('layout', {
            id : 'core-custom-query-edit-list-layout',
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
