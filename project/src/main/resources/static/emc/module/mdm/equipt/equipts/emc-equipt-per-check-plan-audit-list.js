// This page already queries formal CHECK records; keep its raw-query scope.
emcEquiptPerCheck.planRecordPage = {

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt/equipt-per-check',

    getReadonlyFields : function(fields) {
        return fields.filter(function(field) {
            return !field.checkbox;
        }).map(function(field) {
            var readonlyField = Gikam.deepExtend(field);
            readonlyField.readonly = true;
            delete readonlyField.editor;
            delete readonlyField.preInsert;
            delete readonlyField.onChange;
            delete readonlyField.onAfterChoose;
            delete readonlyField.onClean;
            return readonlyField;
        });
    },

    showCheck : function(row) {
        var _this = this;
        var generation = this.selectionGeneration = (this.selectionGeneration || 0) + 1;
        if (this.detailRequest) {
            this.detailRequest.abort();
            this.detailRequest = null;
        }
        Gikam.getAsyncComp('emc-equipt-per-check-plan-audit-list-uploader').done(function(uploader) {
            if (_this.selectionGeneration !== generation) {
                return;
            }
            if (uploader.grid.requestDeferred) {
                uploader.grid.requestDeferred.abort();
            }
            uploader.setOptions({ bizId : '' });
            uploader.grid.cleanData();
        });
        Gikam.getAsyncComp('emc-equipt-per-check-plan-audit-list-base-info-form').done(function(form) {
            if (_this.selectionGeneration !== generation) {
                return;
            }
            form.cleanData().done(function() {
                if (_this.selectionGeneration !== generation || !row) {
                    return;
                }
                // Read through the existing CHECK detail permission validator.
                _this.detailRequest = Gikam.getJson(Gikam.printf(_this.baseUrl + '/{id}', { id : row.id }));
                _this.detailRequest.done(function(data) {
                    if (_this.selectionGeneration !== generation || !data || Gikam.isEmpty(data.id)) {
                        return;
                    }
                    form.setOptions({ data : { id : data.id } });
                    form.setData(data, false);
                    Gikam.getAsyncComp('emc-equipt-per-check-plan-audit-list-uploader').done(function(uploader) {
                        if (_this.selectionGeneration !== generation) {
                            return;
                        }
                        // setOptions refreshes the attachment grid itself.
                        uploader.setOptions({ bizId : data.id });
                    });
                });
            });
        });
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-per-check-plan-audit-list-grid',
            url : this.baseUrl + '/queries/raw',
            service : 'emcEquiptPerCheckServiceImpl',
            dbTable : 'T_EMC_EQUIPT_PER_CHECK',
            readonly : true,
            autoSave : false,
            columns : this.getReadonlyFields(emcEquiptPerCheck.getSearchListColumns()),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    _this.showCheck(null);
                }
            },
            onRowActive : function(index, row) {
                _this.showCheck(row);
            },
            toolbar : []
        };
    },

    getBaseInfoTab : function() {
        var identityFields = emcEquiptPerCheck.getSearchListColumns().filter(function(field) {
            return ['equiptNo', 'equiptName', 'specification', 'checkPerson'].indexOf(field.field) !== -1;
        });
        return {
            type : 'tab',
            panels : [ {
                title : '基本信息',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptPerCheckServiceImpl',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK',
                    id : 'emc-equipt-per-check-plan-audit-list-base-info-form',
                    readonly : true,
                    autoSave : false,
                    fields : this.getReadonlyFields(identityFields.concat(emcEquiptPerCheck.getBaseInfoFormSupFields()))
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-per-check-plan-audit-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK',
                    readonly : true,
                    gridOptions : { readonly : true, autoSave : false },
                    formatterGridToolbar : function() { return []; }
                } ]
            } ]
        };
    },

    create : function() {
        Gikam.create('layout', {
            id : 'emc-equipt-per-check-plan-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
