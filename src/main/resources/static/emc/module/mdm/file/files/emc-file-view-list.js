emcFile.viewPage = {
    getUploaderGrid : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-file-view-list-uploader',
            dbTable : _this.param.table,
            bizId : _this.param.id,
            bizCategory : !Gikam.isEmpty(_this.param.bizCategory) ? _this.param.bizCategory : '',
            readonly : !Gikam.isEmpty(_this.param.readonly),
            formatterGridColumns : function() {
                return emcFile.getMethodFileListColumns();
            },   
        }
    },

    getMultiUploaderGrid : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-file-view-list-multi-uploader',
            //dbTable : _this.param.table,
            //bizId : _this.param.id,
            //bizCategory : !Gikam.isEmpty(_this.param.bizCategory) ? _this.param.bizCategory : '',
            readonly : !Gikam.isEmpty(_this.param.readonly),
            formatterGridColumns : function() {
                return emcFile.getMethodFileListColumns();
            },
            onRendered : function() {
                var idArr = _this.param.ids.split(",");
                var targetIds = idArr.map(function (item) {
                    return _this.param.table + '$' + item;
                }).join(',');
                Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                    f: {
                        targetId_IN: targetIds,
                        bizCategory_SEQ: _this.param.bizCategory
                    },
                    n: -1
                })).done(function (page) {
                    var rows = page.rows;
                    Gikam.getComp('emc-file-view-list-multi-uploader').grid.loadData(rows);
                })
            }
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getUploaderGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    createMultiPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getMultiUploaderGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        if (!Gikam.isEmpty(this.param.id)) {
            _this.createPage();
        } else if (!Gikam.isEmpty(this.param.ids)) {
            _this.createMultiPage();
        }

    },
}