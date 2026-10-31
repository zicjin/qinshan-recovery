emcInfoTmpl.tmplPointEditPage = {

    getGridParam : function() {
        var _this = this;
        var columns = emcInfoTmpl.getTmplPointEditListColumns();
        return {
            type : 'grid',
            id : 'emc-info-tmpl-point-edit-list-grid',
            url : Gikam.printf(emcInfoTmpl.baseUrl + '/{tmplId}/points/queries', {
                tmplId : _this.param.id
            }),
            requestData : _this.param,
            service : 'emcInfoTmplServiceImpl',
            columns : columns,
            deleteFormatter : function(row) {
                return row.ext$.monitorpointname || row.monitorpointno;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.loadPointPage(_this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-info-tmpl-point-edit-list-grid').deleteRows(emcInfoTmpl.baseUrl + '/points');
                }
            } ]
        }
    },

    loadPointPage : function(tmplId) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.INFO.INFOTMPLS.MODAL.POINT',
            url : Gikam.choose.getConfig('monitorPointSimple').url + Gikam.param({
                notExistFilter : '1',
                tmplId : tmplId
            }),
            width : 500,
            height : 500,
            onAfterClose : function(rows) {
                if (!rows || Gikam.isEmpty(rows)) {
                    return;
                }
                var tmplPoints = rows.map(function(row) {
                    return {
                        infoTmplId : tmplId,
                        monitorPointId : row.id
                    }
                });
                Gikam.getLastModal().window.showMask(true);
                Gikam.postText(emcInfoTmpl.baseUrl + '/points', Gikam.getJsonWrapper(null, [ '', tmplPoints ])).done(function() {
                    Gikam.getComp('emc-info-tmpl-point-edit-list-grid').refresh({
                        url : Gikam.printf(emcInfoTmpl.baseUrl + '/{tmplId}/points/queries', {
                            tmplId : _this.param.id
                        }),
                        requestData : _this.param
                    });
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-info-tmpl-point-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
