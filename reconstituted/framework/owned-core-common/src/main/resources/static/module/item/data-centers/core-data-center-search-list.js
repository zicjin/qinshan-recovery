//@ sourceURL=core-data-center-search-list.js
coreDataCenter.searchPage = {

    getDataCenterForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'core-data-center-search-list-form',
            columns : 1,
            titleWidthAuto : true,
            fields : coreDataCenter.getDataCenterFormFields()
        };
    },

    getPreviewIframe : function() {
        return {
            type : 'iframe',
            id : 'core-data-center-search-list-preview-iframe',
            src : ''
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    fill : true,
                    north : {
                        height : '55',
                        items : [ this.getDataCenterForm() ]
                    },
                    center : {
                        items : [ this.getPreviewIframe() ]
                    }
                } ]
            }

        });
    },

    init : function() {
        this.create();
    }
};
