(function() {
    var a = {
        exec : function(editor) {
            Gikam.create('modal', {
                url : IFM_CONTEXT + '/secure/core/module/item/les-templates/page/weighing',
                title : '天平称重',
                width : 600,
                onAfterClose : function(data) {
                    var tableTpl = '<table contenteditable="false" style="min-width:500px;border-collapse: collapse;">' +
                                        '<thead>' +
                                             '<tr>' +
                                                 '<th style="border: 1px solid #999;width:35%;height:30px;">物质名称</th>' +
                                                 '<th style="border: 1px solid #999;width:35%;height:30px;">称重</th>' +
                                                 '<th style="border: 1px solid #999;width:35%;height:30px;">备注</th>' +
                                             '</tr>' +
                                        '</thead>' +
                                        '<tbody>{rows}</tbody>' +
                                   '</table>',
                        rowTpl = '<tr>' +
                                    '<td style="border: 1px solid #999;width:35%;height:30px;">{substanceName}</td>' +
                                    '<td style="border: 1px solid #999;width:30%;height:30px;">{weight}</td>' +
                                    '<td style="border: 1px solid #999;width:35%;height:30px;">{remark}</td>' +
                                 '</tr>';
                    if (Gikam.isEmpty(data)) {
                        var rows = '';
                        data.forEach(function(item) {
                            rows += Gikam.printf(rowTpl, item);
                        });
                        editor.insertHtml(Gikam.printf(tableTpl, {
                            rows : rows
                        }));
                    }
                }
            });
        }
    }, b = 'balance';

    CKEDITOR.plugins.add(b, {
        init : function(editor) {
            editor.addCommand(b, a);
            editor.ui.addButton('balance', {
                label : '插入称量记录',
                icon : this.path + '/images/balance.png',
                command : b
            });
        }
    });
})();