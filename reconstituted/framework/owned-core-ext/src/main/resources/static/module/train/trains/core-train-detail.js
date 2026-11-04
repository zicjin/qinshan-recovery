coreTrain.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                id : 'core-train-detail-btn-activate',
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activate();
                }
            }, {
                id : 'core-train-detail-btn-back',
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
                title : 'CORE.MODULE.TRAIN.TRAIN.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-train-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreTrainServiceImpl',
                    fields : coreTrain.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getTrainUserTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-train-detail-user-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.TRAIN.TAB.TRAIN_USER',
                id : 'core-train-detail-user-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-train-detail-user-list-grid',
                    url : _this.getCompUrl('trainUserGrid'),
                    service : 'coreTrainUserServiceImpl',
                    columns : coreTrain.getUserColumns(),
                    deleteFormatter : function(row) {
                        return row.userName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertUserRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteUserRow();
                        }
                    } ]
                } ]
            } ]
        }
    },

    activate : function() {
        var _this = this;
        var form = Gikam.getComp('core-train-detail-base-info-form');
        if (form.validate()) {
            var url = coreTrain.baseUrl + '/action/activate';
            workspace.window.showMask(true);
            Gikam.put(url, Gikam.getJsonWrapper(null, [ '', [ form.getData() ] ])).done(function(r) {
                workspace.window.goBack();
            }).always(function() {
                workspace.window.closeMask();
            });
        }
    },

    insertUserRow : function() {
        var _this = this;
        var url = Gikam.printf(coreTrain.baseUrl + '/{id}/users', {
            id : _this.param.id,
        });
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('mult-user').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_TRAIN_USER',
                targetUserIdColumn : 'USERID',
                targetFilterColumn : 'TRAINID',
                targetFilterValue : _this.param.id,
                status_EQ : 'activated'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        userId : item.id,
                        userName : item.userName,
                        trainId : _this.param.id
                    }
                });
                Gikam.getComp('core-train-detail-user-list-grid').insert(url, chooseData);
            }
        });
    },

    deleteUserRow : function() {
        var url = coreTrain.baseUrl + '/users';
        Gikam.getComp('core-train-detail-user-list-grid').deleteRows(url);
    },

    urlManager : {
        baseInfoForm : coreTrain.baseUrl + '/{id}',
        trainUserGrid : coreTrain.baseUrl + '/{id}/users/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-train-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-train-detail-user-list-grid' : this.getCompUrl('trainUserGrid')
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getTrainUserTabParam() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : {
                pageObject : coreTrain,
                bizId : _this.param.id
            }
        });
        return this;
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
}