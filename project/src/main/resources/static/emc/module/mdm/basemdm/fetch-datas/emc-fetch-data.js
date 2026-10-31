var emcFetchData = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/mdm/basemdm/fetch-datas",
    baseLineUrl : IFM_CONTEXT + "/secure/emc/module/mdm/basemdm/fetch-data-lines",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_FETCH_DATA.ACTIVATEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            },
            readonly : true,
            width : 80
        }, {
            field : 'tableName',
            title : 'T_EMC_FETCH_DATA.TABLENAME',
            editor : true
        }, {
            field : 'fieldOneName',
            title : 'T_EMC_FETCH_DATA.FIELDONENAME',
            editor : true
        }, {
            field : 'fieldOne',
            title : 'T_EMC_FETCH_DATA.FIELDONE',
            editor : true,
            type : 'number',
            isInteger : true,
            range : '[1, 100]',
            hasArrow : true,
        }, {
            field : 'fieldTwoName',
            title : 'T_EMC_FETCH_DATA.FIELDTWONAME',
            editor : true
        }, {
            field : 'fieldTwo',
            title : 'T_EMC_FETCH_DATA.FIELDTWO',
            editor : true,
            type : 'number',
            isInteger : true,
            range : '[1, 100]',
            hasArrow : true,
        }, {
            field : 'fieldThreeName',
            title : 'T_EMC_FETCH_DATA.FIELDTHREENAME',
            editor : true
        }, {
            field : 'fieldThree',
            title : 'T_EMC_FETCH_DATA.FIELDTHREE',
            editor : true,
            type : 'number',
            isInteger : true,
            range : '[1, 100]',
            hasArrow : true,
        } ];
    },

    getFetchDataLineColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'content',
            title : 'T_EMC_FETCH_DATA_LINE.CONTENT',
            editor : true
        } ];
    }

})
