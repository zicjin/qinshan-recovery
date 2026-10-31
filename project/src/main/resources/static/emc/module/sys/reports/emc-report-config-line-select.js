Gikam.extend(coreReportConfig.lineSelectPage, {
    getBaseInfoTab : function() {
        return {
            type : 'form',
            columns : 1,
            id : 'core-report-config-line-select-base-info-form',
            fields : coreReportConfig.getLineSelectFormFields(),
            titleWidth : 80
        }
    }
});
