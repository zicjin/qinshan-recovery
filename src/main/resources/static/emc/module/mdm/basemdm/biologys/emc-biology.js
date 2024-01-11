var emcBiology = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'speciesCode',
            title : 'T_EMC_BIOLOGY.SPECIESCODE',
            editor : true,
            validators : [ 'strLength[0, 36]' ]
        }, {
            field : 'speciesName',
            title : 'T_EMC_BIOLOGY.SPECIESNAME',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        }, {
            field : 'speciesEngLishName',
            title : 'T_EMC_BIOLOGY.SPECIESENGLISHNAME',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        } ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'speciesCode',
            title : 'T_EMC_BIOLOGY.SPECIESCODE',
            editor : true,
            validators : [ 'strLength[0, 36]' ],
            preInsert : true
        }, {
            field : 'speciesName',
            title : 'T_EMC_BIOLOGY.SPECIESNAME',
            editor : true,
            validators : [ 'strLength[0, 120]' ],
            preInsert : true
        }, {
            field : 'speciesEngLishName',
            title : 'T_EMC_BIOLOGY.SPECIESENGLISHNAME',
            editor : true,
            validators : [ 'strLength[0, 120]' ],
            preInsert : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biologys',

    workflow : {
        dbTable : 'T_EMC_BIOLOGY',
        columns : [ {
            field : 'speciesCode',
            title : 'T_EMC_BIOLOGY.SPECIESCODE'
        } ]
    }
});
