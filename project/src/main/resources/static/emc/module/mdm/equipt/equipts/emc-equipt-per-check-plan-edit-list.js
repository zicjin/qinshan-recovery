emcEquiptPerCheckPlan.editPage = {

    // Global Plan reads are separate from the shared formal CHECK resource.
    // Plan maintenance is not restored here until the device/maint authorization is verified.
    getReadonlyFields : function(fields) {
        return [ {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT.EQUIPTID',
            readonly : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
            readonly : true
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
            readonly : true
        } ].concat(fields.filter(function(field) {
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
        }));
    },

    showPlan : function(row) {
        var _this = this;
        var generation = this.selectionGeneration = (this.selectionGeneration || 0) + 1;
        var data = row && Gikam.deepExtend(row);
        Gikam.getAsyncComp('emc-equipt-per-check-plan-edit-list-base-info-form').done(function(form) {
            if (_this.selectionGeneration !== generation) {
                return;
            }
            form.cleanData().done(function() {
                if (_this.selectionGeneration !== generation || !data) {
                    return;
                }
                // Form.setData preserves its existing id; seed the actual Plan id after clearing.
                form.setOptions({ data : { id : data.id } });
                form.setData(data, false);
            });
        });
        Gikam.getAsyncComp('emc-equipt-per-check-plan-edit-list-uploader').done(function(uploader) {
            if (_this.selectionGeneration !== generation) {
                return;
            }
            // Grid.refresh returns early for an empty URL before aborting its previous request.
            if (!data && uploader.grid.requestDeferred) {
                uploader.grid.requestDeferred.abort();
            }
            // Uploader.setOptions refreshes its own grid; do not issue a second request.
            uploader.setOptions({ bizId : data ? data.id : '' });
            if (!data) {
                uploader.grid.cleanData();
            }
        });
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-per-check-plan-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipts/per-check-plans/queries',
            service : 'emcEquiptPerCheckPlanServiceImpl',
            dbTable : 'T_EMC_EQUIPT_PER_CHECK_PLAN',
            readonly : true,
            autoSave : false,
            columns : this.getReadonlyFields(emcEquiptPerCheckPlan.getPerCheckListColumns()),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    _this.showPlan(null);
                }
            },
            onRowActive : function(index, row) {
                _this.showPlan(row);
            },
            toolbar : []
        }
    },

    getBaseInfoTab : function() {
        return {
            type : 'tab',
            panels : [ {
                title : '基本信息',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptPerCheckPlanServiceImpl',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK_PLAN',
                    id : 'emc-equipt-per-check-plan-edit-list-base-info-form',
                    readonly : true,
                    autoSave : false,
                    fields : this.getReadonlyFields(emcEquiptPerCheckPlan.getBaseInfoFormFields())
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-per-check-plan-edit-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK_PLAN',
                    readonly : true,
                    gridOptions : { readonly : true, autoSave : false },
                    formatterGridToolbar : function() { return []; }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-per-check-plan-edit-list-layout',
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
