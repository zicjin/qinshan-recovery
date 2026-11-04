coreCustomQuery.detailPage = {

    formData : null,

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [{
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'preview',
                onClick : function() {
                    if(Gikam.isEmpty(_this.formData.reportConfigLineId)){
                        Gikam.create('modal', {
                            url: IFM_CONTEXT + '/secure/core/module/item/custom-queries/page/grid-menu-list' + Gikam.param({
                                customQueryId : _this.formData.id,
                                modal : '1'
                            }),
                            title: 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.PREVIEW',
                            width: Gikam.width.L,
                        })
                    }else {
                        Gikam.create('modal', {
                            url: IFM_CONTEXT + '/secure/core/module/item/custom-queries/page/report-menu-list' + Gikam.param({
                                customQueryId : _this.formData.id,
                                reportConfigLineId :  _this.formData.reportConfigLineId,
                                modal : '1'
                            }),
                            title: 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.PREVIEW'
                        })
                    }
                }
            },  {
                type : 'button',
                text : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.RELEASE',
                icon : 'pass',
                onClick : function() {
                    if(_this.formData.publishStatus ==='1'){
                        Gikam.alert('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.ALREADY_RELEASE');
                        return;
                    }
                    _this.release(_this.formData);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.BUTTON.CANCEL_RELEASE',
                icon : 'cancel',
                onClick : function() {
                    if(_this.formData.publishStatus === '0'){
                        Gikam.alert('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.NO_RELEASE');
                        return;
                    }
                    Gikam.create('confirm', {
                        message : Gikam.propI18N('CORE.MODULE.ITEM.CUSTOM_QUERIES.TIP.CANCEL_RELEASE'),
                        onYesClick : function() {
                            workspace.window.showMask(true);
                            Gikam.put(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/action/revoke',{
                                id : _this.formData.id,
                            })).done(function () {
                                Gikam.getComp('core-custom-query-detail-base-info-form').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            },  {
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
                title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreCustomQueryServiceImpl',
                    dbTable : 'T_CORE_CUSTOM_QUERY',
                    id : 'core-custom-query-detail-base-info-form',
                    fields : coreCustomQuery.getCustomFormFields(),
                    onLoadSuccess : function(data) {
                        _this.formData = data;

                        var fieldGrid = Gikam.getComp('core-custom-query-field-list-grid');
                        var buttonGrid = Gikam.getComp('core-custom-query-button-list-grid');
                        var conditionGrid = Gikam.getComp('core-custom-query-condition-list-grid');
                        var frConditionGrid = Gikam.getComp('core-custom-query-condition-fr-list-grid');
                        if(data.queryCategory === 'grid'){
                            Gikam.getComp('core-custom-query-edit-list-tab').revertPanel('core-custom-query-field-list-panel');
                            Gikam.getComp('core-custom-query-edit-list-tab').revertPanel('core-custom-query-button-list-panel');
                            Gikam.getComp('core-custom-query-edit-list-tab').revertPanel('core-custom-query-condition-grid-panel');
                            Gikam.getComp('core-custom-query-edit-list-tab').removePanel('core-custom-query-condition-fr-panel');
                            this.hideFields(['ext$.reportname']);
                            if(data.dsType === 'customSql'){
                                this.hideFields(['ext$.dsname']);
                            }else {
                                this.hideFields(['customSql']);
                            }
                        }else {
                            Gikam.getComp('core-custom-query-edit-list-tab').removePanel('core-custom-query-field-list-panel');
                            Gikam.getComp('core-custom-query-edit-list-tab').removePanel('core-custom-query-button-list-panel');
                            Gikam.getComp('core-custom-query-edit-list-tab').removePanel('core-custom-query-condition-grid-panel');
                            Gikam.getComp('core-custom-query-edit-list-tab').revertPanel('core-custom-query-condition-fr-panel');
                            this.hideFields(['ext$.dsname','customSql']);
                        }
                        if(data.publishStatus === '1'){
                            fieldGrid.setReadonly(true);
                            fieldGrid.hideToolbar();
                            buttonGrid.setReadonly(true);
                            buttonGrid.hideToolbar();
                            conditionGrid.setReadonly(true);
                            conditionGrid.hideToolbar();
                            frConditionGrid.setReadonly(true);
                            frConditionGrid.hideToolbar();
                        }else {
                            fieldGrid.setReadonly(false);
                            fieldGrid.showToolbar();
                            buttonGrid.setReadonly(false);
                            buttonGrid.showToolbar();
                            conditionGrid.setReadonly(false);
                            conditionGrid.showToolbar();
                            frConditionGrid.setReadonly(false);
                            frConditionGrid.showToolbar();
                        }
                    },
                } ]
            } ]
        }
    },

    getQueryGridParam : function(items,dsCategory) {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-custom-query-edit-list-tab',
            panels : [ _this.getFieldGridParam(items), _this.getButtonGridParam(), _this.getConditionGridParam(items,dsCategory), _this.getConditionFrParam(items,dsCategory) ]
        }
    },

    getFieldGridParam : function (items) {
        return {
            id : 'core-custom-query-field-list-panel',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.FIELD',
            items : [ {
                type : 'grid',
                id : 'core-custom-query-field-list-grid',
                service : 'coreCustomQueryFieldServiceImpl',
                url : coreCustomQuery.baseUrl + '/fields/queries',
                fill : true,
                requestData : {
                    customQueryId_EQ : this.param.id
                },
                columns : coreCustomQuery.getFieldEditListColumns(items),
                page : false,
                onLoadSuccess : function (rows) {
                    for (let i=0; i<rows.length;i++) {
                        if(rows[i].dsCategory === 'system'){
                            Gikam.getComp('core-custom-query-field-list-grid').getField(i,'dataSource').setItems(items);
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
    },

    getButtonGridParam : function () {
        var _this = this;
        return {
            id : 'core-custom-query-button-list-panel',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.BUTTON',
            items : [ {
                type : 'grid',
                id : 'core-custom-query-button-list-grid',
                service : 'coreCustomQueryButtonServiceImpl',
                url : coreCustomQuery.baseUrl + '/buttons/queries',
                requestData : {
                    customQueryId_EQ : this.param.id
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
                            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_BUTTON',
                            width : Gikam.width.L,
                            onAfterClose : function (rows) {
                                if(rows){
                                    var buttons = [];
                                    for (let row of rows) {
                                        buttons.push({
                                            buttonName : row.buttonName,
                                            buttonIcon : row.buttonIcon,
                                            buttonLogic : row.buttonLogic,
                                            customQueryId : _this.formData.id
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
    },

    getConditionGridParam : function (items,dsCategory) {
        var _this = this;
        return {
            id : 'core-custom-query-condition-grid-panel',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CONDITION',
            items : [ {
                type : 'grid',
                id : 'core-custom-query-condition-list-grid',
                service : 'coreCustomQueryConditionServiceImpl',
                url : coreCustomQuery.baseUrl + '/conditions/queries',
                requestData : {
                    customQueryId_EQ : this.param.id
                },
                columns : coreCustomQuery.getConditionEditListColumns(items,dsCategory),
                deleteFormatter : function(row) {
                    return row.fieldName || row.id;
                },
                page : false,
                onLoadSuccess : function (rows) {
                    _this.initSelect(rows,'core-custom-query-condition-list-grid',items,dsCategory)
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.create('modal', {
                            url : IFM_CONTEXT + '/secure/core/module/item/custom-queries/page/field-choose-list'+ Gikam.param({
                                show_SEQ : '1',
                                customQueryId_EQ : _this.formData.id,
                            }),
                            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION',
                            width : Gikam.width.L,
                            onAfterClose : function (rows) {
                                if(rows){
                                    var conditions = [];
                                    for (let row of rows) {
                                        conditions.push({
                                            fieldName : row.fieldName,
                                            title : row.columnName,
                                            customQueryId : _this.formData.id
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
    },

    getConditionFrParam : function (items,dsCategory) {
        var _this = this;
        return {
            id : 'core-custom-query-condition-fr-panel',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.CONDITION',
            items : [ {
                type : 'grid',
                id : 'core-custom-query-condition-fr-list-grid',
                service : 'coreCustomQueryConditionServiceImpl',
                url : coreCustomQuery.baseUrl + '/conditions/queries',
                requestData : {
                    customQueryId_EQ : this.param.id
                },
                columns : coreCustomQuery.getFrConditionEditListColumns(items,dsCategory),
                deleteFormatter : function(row) {
                    return row.fieldName || row.id;
                },
                page : false,
                onLoadSuccess : function (rows) {
                    _this.initSelect(rows,'core-custom-query-condition-fr-list-grid',items,dsCategory)
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        _this.conditionPreInsert(items,dsCategory);
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
        }
    },

    release : function (form) {
        var modal = Gikam.create('modal', {
            id : 'modal',
            title : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.RELEASE_MENU',
            width : 400,
            height : 260,
            onAfterClose() {
                Gikam.getComp('core-custom-query-detail-base-info-form').refresh();
                Gikam.getComp('core-custom-query-field-list-grid').refresh();
                Gikam.getComp('core-custom-query-button-list-grid').refresh();
                Gikam.getComp('core-custom-query-condition-list-grid').refresh();
                Gikam.getComp('core-custom-query-condition-fr-list-grid').refresh();
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
                            var releaseForm = Gikam.getComp('core-custom-query-release-list-form');
                            if(!releaseForm.validate()){
                                return;
                            }
                            releaseForm.setData({
                                id : form.id,
                                orderNo : form.id,
                                menuInitName : releaseForm.getData().menuName
                            });
                            var querys = [releaseForm.getData()];
                            workspace.window.showMask(true);
                            Gikam.postText(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/action/publish',{
                                id : form.id
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

    conditionPreInsert : function (items,dsCategory) {
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.CUSTOM_QUERIES.TITLE.ADD_CONDITION',
            id : 'core-custom-query-condition-list-form',
            fields : coreCustomQuery.getFrConditionAddListColumns(items,dsCategory),
            url : coreCustomQuery.baseUrl+'/query-conditions',
            height : 430,
            param : { customQueryId : _this.formData.id},
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


    urlManager : {
        baseInfoForm : coreCustomQuery.baseUrl + '/{id}',
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
        var _this = this;
        var items = [];
        var dsCategory = [];
        Gikam.getJsonSync(IFM_CONTEXT + '/secure/core/module/admin/select-configs/queries/selectable').done(
            function (r) {
                for (let rElement of r) {
                    items.push({
                        text : rElement.name,
                        value : rElement.category
                    })
                }
            }
        )
        Gikam.getJsonSync(IFM_CONTEXT + '/secure/core/module/admin/choose-configs/queries/selectable').done(
            function (r) {
                for (let rElement of r) {
                    dsCategory.push({
                        text : rElement.name,
                        value : rElement.category
                    })
                }
            }
        )
        Gikam.create('layout', {
            id : 'core-custom-query-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 200,
                items : [this.getBtnToolbar(), this.getBaseInfoTab()]
            },
            center : {
                items : [ this.getQueryGridParam(items,dsCategory) ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
