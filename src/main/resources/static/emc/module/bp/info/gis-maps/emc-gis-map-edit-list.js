emcGisMap.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-gis-map-edit-list-grid',
            columns : emcGisMap.getEditListColumns(),
            onBeforeLoad : function(data) {
                var json = [];
                data.forEach(function(item) {
                    if (!Gikam.isEmpty(item.ext$.longitude) && !Gikam.isEmpty(item.ext$.latitude)) {
                        var el = {
                            T_EMC_REPORT_RESULT$MONITORPOINTNAME : item.ext$.foldermonitorpointname || '',
                            longitude : item.ext$.longitude || '',
                            latitude : item.ext$.latitude || '',
                            T_EMC_REPORT_RESULT$REPORTRESULT : item.reportResult || ''
                        }
                        json.push(el);
                    }
                });
                if(!Gikam.isEmpty(json)) {
                    var message = JSON.parse(JSON.stringify(json).replace(new RegExp("\\$", "g"), '.'));
                    Gikam.getComp('emc-gis-map-edit-list-map').refreshPoint(message);
                } else {
                    Gikam.getComp('emc-gis-map-edit-list-map').refreshPoint(json);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SEARCH',
                icon : 'search',
                onClick : function() {
                    var _form = Gikam.getComp('emc-gis-map-edit-list-form');
                    if (_form.validate()) {
                        var grid = Gikam.getComp('emc-gis-map-edit-list-grid');
                        var beginDate = _form.getField('beginDate').value;
                        var endDate = _form.getField('endDate').value;
                        var monitorPointCategory = _form.getField('monitorPointCategory').value;
                        var testName = _form.getField('testName').value;
                        var itemId = _form.getField('itemAliasName').value;

                        grid.refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/bp/report/reports/results/queries',
                            requestData : {
                                REGISTDATE_DGOE : beginDate,
                                REGISTDATE_DLOE : endDate,
                                MONITORPOINTCATEGORYID_SEQ : monitorPointCategory,
                                TESTMETHODID_SEQ : testName,
                                ITEMID_SEQ : itemId,
                                REPORTDISPLAY_SEQ : '1'
                            }
                        });
                    }
                }
            } ]
        }
    },

    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 3,
            id : 'emc-gis-map-edit-list-form',
            fields : emcGisMap.getBaseInfoFormFields(),
        };
    },

    getMapParam : function() {
        var _this = this;
        return {
            type : 'map',
            id : 'emc-gis-map-edit-list-map',
            key : 'ea30c2cd5f759e749f0ecafc39f7fe0b',
            renderTo : workspace.window.$dom
        };
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-gis-map-edit-list-layout',
            renderTo : workspace.window.$dom,

            north : {
                height : '20%',
                items : [ _this.getFormParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ _this.getGridParam() ]
                    },
                    center : {
                        items : [ _this.getMapParam() ]
                    }
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
