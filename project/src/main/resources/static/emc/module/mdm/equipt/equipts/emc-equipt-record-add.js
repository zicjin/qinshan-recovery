emcEquiptRecord.addPage = {

    getBaseInfoTab : function() {

        var _this = this;

        return {
            type : 'form',
            columns : 1,
            fields : [ {
                field : 'maintId',
                title : 'T_EMC_EQUIPT_PLAN.MAINTEVENT',
                search : true,
                type : 'select',

            }, {
                field : 'verifyOrg',
                title : 'T_EMC_EQUIPT_RECORD.VERIFYORG',
                type : 'select',
                search : true,
                category : 'sys',
                param : {
                    codeCategoryId : 'verifyOrg',
                },

            } ],
            onRendered : function() {
                _this.form = this;
            },

        };

    },

    getBaseInfoBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(_this.form.getData());

                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },

    createPage : function() {
        _this = this;

        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        var url = Gikam.printf(emcEquipt.baseUrl + '/{id}/records/maints/queries/selectable', {
            id : this.param.equiptId
        })
        Gikam.getJson(url).done(function(items) {
            _this.items = items;
            _this.form.setSelectOptions('maintId', items.map(function(item) {
                return {
                    value : item.id,
                    text : item.maintEvent
                };
            }), true);

        });

        return this;

    },

    init : function(param) {

        this.param = param;

        this.createPage();
    }
};