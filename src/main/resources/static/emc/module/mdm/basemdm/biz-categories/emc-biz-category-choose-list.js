emcBizCategory.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcBizCategory.getFormStyleListColumns();
        columns.unshift({
            radio : true
        });
        return {
            type : 'grid',
            id : 'emc-biz-category-choose-list-grid',
            url : '/secure/core/module/sys/codes/queries/raw',
            requestData : {
                codecategoryid_SEQ : 'formStyle'
            },
            columns : columns,
            onLoadSuccess : function(data) {
                var selections = Gikam.getComp('emc-biz-category-edit-list-grid').getSelections();
                if (selections.length == 1) {
                    var formStyle = selections[0].formStyle;
                    for (i = 0; i < data.length; i++) {
                        var code = data[i].code;
                        if (code == formStyle) {
                            Gikam.getComp('emc-biz-category-choose-list-grid').activeRowByIndex(i);
                            break;
                        }
                    }
                } else {
                    Gikam.getComp('emc-biz-category-choose-list-grid').activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-biz-category-choose-list-grid').getSelections());
                }
            } ]
        }
    },
    getBaseInfoTab : function() {
        return {
            type : 'tab',
            fill : true,
            id : 'emc-biz-category-choose-list-base-info-tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.BIZCATEGORIES.TAB.PREVIEW',
                id : 'emc-biz-category-choose-list-base-info-panel',
                items : [ {
                    type : 'form',
                    columns : 3,
                    id : 'emc-biz-category-choose-list-base-info-form',
                    fields : emcProject.getBaseInfoFormFields1(),
                    readonly : true
                } ]
            } ]
        }
    },

    onProjectActive : function(row) {
        if (row) {
            var formStyle = row.code;
            var comp = Gikam.getComp('emc-biz-category-choose-list-base-info-form');
            var fields = [];
            if (formStyle === 'form1') {
                fields = emcProject.getBaseInfoFormFields1();
            } else if (formStyle === 'form2') {
                fields = emcProject.getBaseInfoFormFields2();
            } else {
                fields = emcProject.getBaseInfoFormFields3();
            }
            comp.refreshFields(fields);
            comp.setOptions({
                readonly : true
            });
            comp.initReadonly();
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : 270,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}
