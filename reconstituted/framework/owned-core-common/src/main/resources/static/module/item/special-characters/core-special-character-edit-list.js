coreSpecialCharacter.editPage = {
    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-special-character-list-grid',
            url : coreSpecialCharacter.baseUrl + '/queries/raw',
            service : 'coreSpecialCharacterServiceImpl',
            columns : coreSpecialCharacter.getEditListColumns(),
            deleteFormatter : function (row) {
                return row.specialCharacter || row.id;
            },
            order : {
                id : "desc"
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'CORE_SPECIAL_CHARACTER.MODAL.ADD_MODAL',
                        fields : coreSpecialCharacter.getBaseInfoFormFields(),
                        url : coreSpecialCharacter.baseUrl,
                        disableNotEmptyValidate : true,
                        titleWidth : 100,
                    }).done(function (id) {
                        Gikam.getComp('core-special-character-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function () {
                    Gikam.getComp('core-special-character-list-grid').deleteRows(coreSpecialCharacter.baseUrl);
                }
            } ]
        }
    },

    create : function () {
        Gikam.create('layout', {
            id : 'core-special-character-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function () {
        this.create();
    }
};