(function() {
    var a = {
        exec : function(editor) {
            editor.insertHtml("√");
        }
    }, b = 'tick';

    CKEDITOR.plugins.add(b, {
        init : function(editor) {
            editor.addCommand(b, a);
            editor.ui.addButton('tick', {
                label : '对勾',
                icon : this.path + 'images/tick-icon.png',
                command : b
            });
        }
    });
})();