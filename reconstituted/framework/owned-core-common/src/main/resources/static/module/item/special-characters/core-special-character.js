var coreSpecialCharacter = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/special-characters',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'specialCharacter',
            title : 'T_CORE_SPECIAL_CHARACTER.SPECIALCHARACTER',
            width : 120,
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.category == 'custom') {
                    return false;
                }

                return true;
            }
        }, {
            field : 'category',
            title : 'T_CORE_SPECIAL_CHARACTER.CATEGORY',
            type : 'select',
            category : 'special-character-category',
            // editor : true, // 测试提出的不允许编辑更合理
            width : 120,
            firstBlank : false,
            onChange : function(field, value, index) {
                var _this = this;
                if (value == 'custom') {
                    _this.toFieldsReadonly(index, [ 'specialCharacter', 'characterCode' ]);

                    var characterCode = _this.getData()[index].characterCode;
                    if (!Gikam.isEmpty(characterCode)) {
                        var span = document.createElement("span");
                        span.innerHTML = '&#' + characterCode + ';';
                        this.setData({
                            index : index,
                            specialCharacter : span.innerHTML,
                            category : value
                        });
                        span = null;
                    }
                } else {
                    _this.toFieldsEdit([ 'specialCharacter', 'characterCode' ])
                }
            }
        }, {
            field : 'characterCode',
            title : 'T_CORE_SPECIAL_CHARACTER.CHARACTERCODE',
            editor : true,
            validators : [ 'remote[/secure/core/module/item/special-characters/action/validate-unique]', 'notEmpty', 'strLength(0,20]' ],
            width : 150,
            onChange : function(field, value, index, d) {
                var _this = this;
                if (_this.getData()[index].category == 'custom') {
                    var span = document.createElement("span");
                    span.innerHTML = '&#' + value + ';';
                    this.setData({
                        index : index,
                        specialCharacter : span.innerHTML,
                        characterCode : value
                    });
                    span = null;
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.category == 'custom') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'pronunciation',
            title : 'T_CORE_SPECIAL_CHARACTER.PRONUNCIATION',
            editor : true,
            validators : [ 'strLength(0,20]' ],
            width : 150
        }, {
            field : 'remark',
            title : 'T_CORE_SPECIAL_CHARACTER.REMARK',
            editor : true,
            validators : [ 'strLength(0,180]' ]
        }, {
            field : 'orderNo',
            title : 'T_CORE_SPECIAL_CHARACTER.ORDERNO',
            editor : true,
            type : 'number',
            hasArrow : true,
            validators : [ 'notEmpty', 'numRange(0,99999)' ],
            isInteger : true
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'specialCharacter',
            title : 'T_CORE_SPECIAL_CHARACTER.SPECIALCHARACTER',
            validators : [ 'strLength(0,12]' ],
            preInsert : true
        }, {
            field : 'category',
            title : 'T_CORE_SPECIAL_CHARACTER.CATEGORY',
            type : 'select',
            category : 'special-character-category',
            preInsert : true,
            validators : [ 'notEmpty' ],
            firstBlank : false,
            onChange : function(field, value, c, d) {
                var _this = this;
                if (value == 'custom') {
                    _this.toFieldsReadonly([ 'specialCharacter' ]);
                    _this.addFieldValidator('specialCharacter', []);

                    _this.addFieldValidator('characterCode', [ 'notEmpty', 'remote[/secure/core/module/item/special-characters/action/validate-unique]', 'strLength(0,20]' ]);

                    var characterCode = _this.getData().characterCode;
                    if (!Gikam.isEmpty(characterCode)) {
                        var span = document.createElement("span");
                        span.innerHTML = '&#' + characterCode + ';';
                        this.setData({
                            specialCharacter : span.innerHTML
                        });
                        span = null;
                    }
                } else {
                    _this.toFieldsEdit([ 'specialCharacter' ]);
                    _this.addFieldValidator('specialCharacter', [ 'notEmpty' ]);

                    _this.addFieldValidator('characterCode', [ 'notEmpty', 'remote[/secure/core/module/item/special-characters/action/validate-unique]', 'strLength(0,20]' ]);
                }
            }
        }, {
            field : 'characterCode',
            title : 'T_CORE_SPECIAL_CHARACTER.CHARACTERCODE',
            validators : [ 'remote[/secure/core/module/item/special-characters/action/validate-unique]', 'notEmpty', 'strLength(0,20]' ],
            preInsert : true,
            onChange : function(field, value, c, d) {
                var _this = this;
                if (_this.getData().category == 'custom') {
                    var span = document.createElement("span");
                    span.innerHTML = '&#' + value + ';';
                    this.setData({
                        specialCharacter : span.innerHTML
                    });
                    span = null;
                }
            }
        } ]
    }
});