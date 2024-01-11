var emcBrowseRecord = Gikam.getPageObject({

    getFileSelectFormFields : function() {
        return [ {
            title : 'EMC.MODULE.BP.RUNS.TAB.OTHER_BROWSE_RECORDS_CATEGORY',
            field : 'otherFileCategory',
            type : 'select',
            firstBlank : false,
            onChange : function(field, value) {
                var iframe = Gikam.getComp('emc-browse-other-record-iframe');
                if (value) {
                    Gikam.openFileByOffice({
                        fileId: value,
                        mode: 'view',
                        iframe: Gikam.getComp("emc-browse-other-record-iframe").iframe,
                        viewType: 'iframe'
                    });
                }
            },
        } ];
    },
});
