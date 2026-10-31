var emcAdmArea = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_EMC_ADM_AREA.ID'
        }, {
            field : 'admAreaName',
            title : 'T_EMC_ADM_AREA.ADMAREANAME'
        }, {
            field : 'parentId',
            title : 'T_EMC_ADM_AREA.PARENTID'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_ADM_AREA.ID',
            preInsert : true,
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'admAreaName',
            title : 'T_EMC_ADM_AREA.ADMAREANAME',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'parentId',
            title : 'T_EMC_ADM_AREA.PARENTID',
            validators : [ 'strLength[0,40]' ]
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_ADM_AREA.ID'
        }, {
            field : 'admAreaName',
            title : 'T_EMC_ADM_AREA.ADMAREANAME'
        }, {
            field : 'parentId',
            title : 'T_EMC_ADM_AREA.PARENTID'
        }, ];
    },

    getChooseFields : function() {
        return [ {
            title : '省',
            field : 'sheng',
            type : 'select',
            category : 'emc-adm-area',
            search : true,
            param : {
                parentid_IN : '-1,',
            },
            firstBlank : false,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var _form = this;
                _form.setData({
                    sheng: value,
                    shengText: text,
                });
                var url = IFM_CONTEXT + '/secure/emc/module/item/adm-areas/queries/selectable';
                if (!Gikam.isEmpty(value)) {
                    url += Gikam.param({
                        parentId_SEQ: value,
                    });
                    Gikam.getJson(url).done(function (items) {
                        _form.setSelectOptions('shi', items.map(function (item) {
                            return {
                                text: item.admAreaName,
                                value: item.id,
                            };
                        }), true);
                        if (items.length > 0) {
                            // _form.setData({
                            //     shi: items[0].id,
                            //     shiText: items[0].admAreaName,
                            // });
                        } else {
                            _form.setData({
                                shi: '',
                                shiText: '',
                                qu: '',
                                quText: '',
                            });
                        }
                    });
                } else {
                    _form.setSelectOptions('shi', []);
                }
            },
        }, {
            title : '市',
            field : 'shi',
            type : 'select',
            search : true,
            firstBlank : false,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var _form = this;
                _form.setData({
                    shi: value,
                    shiText: text,
                });
                var url = IFM_CONTEXT + '/secure/emc/module/item/adm-areas/queries/selectable';
                if (!Gikam.isEmpty(value)) {
                    url += Gikam.param({
                        parentId_SEQ: value,
                    });
                    Gikam.getJson(url).done(function (items) {
                        _form.setSelectOptions('qu', items.map(function (item) {
                            return {
                                text: item.admAreaName,
                                value: item.id,
                            };
                        }), true);
                        if (items.length > 0) {
                            // _form.setData({
                            //     qu: items[0].id,
                            //     quText: items[0].admAreaName,
                            // });
                        } else {
                            _form.setData({
                                qu: '',
                                quText: '',
                            });
                        }
                    });
                } else {
                    _form.setSelectOptions('qu', []);
                }
            },
        }, {
            title : '区',
            field : 'qu',
            type : 'select',
            search : true,
            firstBlank : false,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var _form = this;
                _form.setData({
                    qu: value,
                    quText: text,
                });
            },
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/adm-areas'
});
