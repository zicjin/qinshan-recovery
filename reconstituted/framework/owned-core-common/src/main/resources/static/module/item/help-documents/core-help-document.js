var coreHelpDocument = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'helpDocumentCatalog',
            title : 'T_CORE_HELP_DOCUMENT_CATALOG.HELPDOCUMENTCATALOG'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'helpDocumentCatalog',
            title : 'T_CORE_HELP_DOCUMENT_CATALOG.HELPDOCUMENTCATALOG',
            validators : [ 'strLength[0,40]' ]
        }, ];
    },

    getDocumentListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.name',
            title : 'T_CORE_HELP_DOCUMENT.NAME',
            width : 160
        }, {
            field : 'documentNote',
            title : 'T_CORE_HELP_DOCUMENT.DOCUMENTNOTE',
            editor : true,
            width : 240
        }, {
            field : 'download',
            title : 'T_CORE_HELP_DOCUMENT.DOWNLOAD',
            type : 'simpleCheckbox',
            editor : true,
            width : 100
        }, {
            field : 'operation',
            title : 'T_CORE_HELP_DOCUMENT.OPERATION',
            width : 100,
            formatter : function(index, value, row, text) {
                var view = Gikam.propI18N('GIKAM.BUTTON.VIEW');
                var down = Gikam.propI18N('CORE.MODULE.ITEM.HELP_DOCUMENTS.BUTTON.DOWN');
                if (row.download == '0') {
                    return '<div style="">' +
                        '<a href="javascript:;" onclick="Gikam.openFile(' + row.ext$.fileid + ', true)" style="text-decoration:underline;">' + view + '</a> ' +
                        '</div>'
                } else {
                    return '<div style="">' +
                        '<a href="javascript:;" onclick="Gikam.openFile(' + row.ext$.fileid + ', true)" style="text-decoration:underline;">' + view + '</a> ' +
                        '<a href="javascript:;" onclick="coreHelpDocument.downloadFile(' + row.ext$.fileid + ')" style="text-decoration:underline;">' + down + '</a> ' +
                        '</div>'
                }
            },
        }, {
            field : 'createdByName',
            title : 'T_CORE_HELP_DOCUMENT.CREATEDBYNAME',
            width : 120
        }, {
            field : 'createdTime',
            title : 'T_CORE_HELP_DOCUMENT.CREATEDTIME',
            width : 120
        }, Gikam.status.getActiveColumn() ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreHelpDocument.workflow.dbTable), {
            field : 'helpDocumentCatalog',
            title : 'T_CORE_HELP_DOCUMENT_CATALOG.HELPDOCUMENTCATALOG'
        } ];
    },

    downloadFile: function (fileId) {
        if (fileId) {
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f: {
                    id_EQ: fileId
                },
            })).done(function (r) {
                if (r.rows.length > 0) {
                    Gikam.download((IFM_CONTEXT + r.rows[0].downloadUrl), r.rows[0].name);
                }
            })
        } else {
            Gikam.alert('CORE.MODULE.ITEM.HELP_DOCUMENTS.TIP.NO_FILE');
        }

    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/help-document-catalogs'
});
