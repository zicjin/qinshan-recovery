var emcFile = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/file/files',
    getMethodFileListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'name',
            title : 'T_CORE_FILE.NAME',
            width : 300,
            formatter : function(i, v, r) {
                // 如果是word文档则打开编辑否则为下载文件
                var typeArray = [
                    'doc', 'docm', 'docx', 'dot', 'dotm', 'dotx',
                    'epub', 'fodt', 'htm', 'html', 'mht', 'odt',
                    'ott', 'pdf', 'rtf', 'txt', 'djvu', 'xps',
                    'csv', 'fods', 'ods', 'ots', 'xls', 'xlsm',
                    'xlsx', 'xlt', 'xltm', 'xltx','fodp', 'odp', 
                    'otp', 'pot', 'potm', 'potx','pps', 'ppsm', 
                    'ppsx', 'ppt', 'pptm', 'pptx'
                ];
                if (typeArray.includes(r.fileExt)) {
                    var documentParam = `fileId: '${r.id}', mode: 'edit'`;
                    return `<a href="javascript:;" onclick="Gikam.openFileByOffice({${documentParam}})">${r.name}</a>`;
                } else {
                    return `<a href="javascript:;" onclick="Gikam.printFile('${r.id}', true, null, false, true)">${r.name}</a>`;
                }
            }
        }, {
            field : 'bizCategory',
            title : 'T_CORE_FILE.FILECATEGORY',
            type : 'select',
            search : true,
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'fileBizCategory',
            }
        }, {
            field : 'createdByName',
            title : 'T_CORE_FILE.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_CORE_FILE.CREATEDTIME',
        }, {
            width: 100,
            title: '操作',
            formatter: function(i, v, r) {
                let str = '<div style= "display: flex; justify-content: space-between ">';
                str += `<a href="javascript:;" onclick="Gikam.download('${Gikam.IFM_CONTEXT}${r.downloadUrl}','${r.name}')" key = "download" style="flex: 1;text-align: center;">下载</a>`;
                str += '</div>';
                return str;
            }
        } ];
    },
});
