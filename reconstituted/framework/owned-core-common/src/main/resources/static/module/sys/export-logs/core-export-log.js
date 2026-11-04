var coreExportLog = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/export-logs',

    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            title : 'T_CORE_EXPORT_LOG.MENUNAME',
            field : 'menuName',
            width : 150
        }, {
            title : 'T_CORE_EXPORT_LOG.CREATEDTIME',
            field : 'createdTime',
            width : 200
        }, {
            title : 'T_CORE_EXPORT_LOG.CREATEDBYNAME',
            field : 'createdByName',
            width : 150
        }, {
            title : 'T_CORE_EXPORT_LOG.SUCCESS',
            field : 'success',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'success'
            },
            width : 100
        }, {
            title : 'T_CORE_EXPORT_LOG.FILENAME',
            field : 'fileName',
            width : 400,
        }, {
            width: 170,
            title: 'T_CORE_EXPORT_LOG.OPERATION',
            formatter: function(i, v, r) {
                let str = '<div style= "display: flex; justify-content: space-between ">';
                const mode = 'view';
                const fileType = 'xlsx';
                const key = r.id + ''; // key不变时反复预览会通过缓存打开文档，速度更快
                const fileName = r.fileName;

                let downloadUrl = r.url;
                let viewUrl = r.url;

                if (viewUrl.startsWith('http://') || viewUrl.startsWith('https://')) { // 可能是独立文件服务器，sunway.config.file.server.url和自身不是同一个地址
                    const _file = top.Gikam.file ?? Gikam.file;
                    const systemFileDownloadUrl = _file?.downloadUrl;
                    const sunwayOfficeFileUrl = Gikam.base64Decode(top.Gikam.sunwayoffice.downloadUrl);
                    viewUrl = viewUrl.replace(systemFileDownloadUrl, sunwayOfficeFileUrl);
                } else {
                    downloadUrl = IFM_CONTEXT + downloadUrl;
                }

                const callbackUrl = IFM_CONTEXT + '/core/module/item/files/action/sunwayoffice-external-invoke';
                const preview = Gikam.component.Uploader.getDefaultOptions().preview;
                if (preview) { // 上传组件的全局预览按钮是否显示配置
                    const fileViewType = (Gikam.sunwayoffice.viewType || 'sunwayoffice').toLocaleLowerCase();;

                    if (fileViewType === 'sunwayoffice') {
                        str += `<a href="javascript:;" onclick="Gikam.openExternalOfficeFile({mode :'${mode}',fileType : '${fileType}',key : '${key}',fileName : '${fileName}',fileUrl : '${viewUrl}',callbackUrl : '${callbackUrl}'})" style="flex: 1;text-align: center;">预览</a>`;
                    } else if (fileViewType === 'pageoffice') {
                        str += `<a href="javascript:;" onclick="Gikam.openFileByPageOffice(null, true, '${viewUrl}')" style="flex: 1;text-align: center;">预览</a>`;
                    }
                }
                str += `<a href="javascript:;" onclick="Gikam.download('${downloadUrl}')" key = "download" style="flex: 1;text-align: center;">下载</a>`;
                str += '</div>';
                return str;
            },
            field : 'operation',
            sort: false
        }, {
            title : 'T_CORE_EXPORT_LOG.EXPORTMODE',
            field : 'exportMode',
            width : 100,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'requestMode'
            }
        } ]
    },

});