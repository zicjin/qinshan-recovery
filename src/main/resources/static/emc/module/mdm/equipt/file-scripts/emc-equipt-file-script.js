var emcEquiptFileScript = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/file-scripts',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.ID',
            type : 'link',
            field : 'id',
        }, {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.SCRIPTDESC',
            field : 'scriptDesc'
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.ID',
            field : 'id',
            readonly : true
        }, {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.SCRIPTDESC',
            field : 'scriptDesc',
            type : 'textarea',
            height : 100,
            validators : [ 'notEmpty', 'strLength(,90]' ],
            preInsert : true
        }, {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.SCRIPT',
            field : 'script',
            type : 'textarea',
            height : 300,
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.ID',
            field : 'id',
        }, {
            title : 'T_EMC_EQUIPT_FILE_SCRIPT.SCRIPTDESC',
            field : 'scriptDesc'
        } ]
    },
});