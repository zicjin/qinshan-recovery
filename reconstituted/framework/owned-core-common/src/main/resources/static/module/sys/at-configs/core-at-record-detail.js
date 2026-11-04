coreAtRecord.detailPage = {

    getTableGridParam : function() {
        var _this = this;

        return {
            type : 'grid',
            id : 'core-at-record-detail-grid',
            url : coreAtRecord.baseUrl + '/record-lines/queries',
            columns : coreAtRecord.getLineListColumns(),
            requestData : {
                targetId_EQ : _this.param.tableName + '$' + _this.param.id
            },
            serverSearch : false,
            sort: false,
            onBeforeLoad : function(data) {
                data.forEach(function(item) {
                    item.columnNew = Gikam.propI18N(item.tableName + '.' + item.column.toUpperCase());
                    item.auditTrailName = Gikam.propI18N(item.ext$.audittrailname)
                });
            }
        };
    },

    createTreeParam : function(){
        return '<div class="starter-audit-trail">' +
                   '<div class="audit-body"></div>' +
                   '<div class="details-body"></div>' +
               '</div>';
    },
    
    renderTree : function(){
        var _this = this, 
        url = Gikam.printf(coreAtRecord.baseUrl + '/queries/atrecord/{tableName}/{id}', {
            tableName : _this.param.tableName,
            id : _this.param.id
        });

        Gikam.post(url, null).done(function(dataList) {
            _this.$dom.children('.audit-body').html(_this.renderBranches(dataList));
            Gikam.jQuery('.starter-audit-trail .audit-li').click(function() {
                _this.auditDetails(Gikam.jQuery(this).attr('node-id'));
                Gikam.jQuery('.starter-audit-trail .audit-li').removeClass('active');
                Gikam.jQuery(this).addClass('active');
            });
            dataList.length > 0 && _this.auditDetails(dataList[0].id);
        });
    },
    
    renderBranches : function(list) {
        var treeTpl =  '<div class="title">修改历史</div>'+'<ul>{nodeHtml}</ul>', 
        nodeHtml = '',
        nodeTpl = '<li class="node close audit-li" node-id="{id}" table-name="{tableName}" record-id="{recordId}">{text}</li>'; 
        
        Array.isArray(list) && list.forEach(function(item, i) {
            if(item.tableName) {
                nodeHtml += Gikam.printf(nodeTpl, {
                    text : Gikam.propI18N(item.tableName + '.SERVICE_NAME'),
                    id : item.id
                });
            } else {
                var explainColor = '';
                if(Gikam.propI18N(item.auditTrailName) === '更新'){
                    explainColor = 'yellow';
                } else if(Gikam.propI18N(item.auditTrailName) === '新增') {
                    explainColor = 'green';
                } else if(Gikam.propI18N(item.auditTrailName) === '删除') {
                    explainColor = 'red';
                }

                var comment = item.comment ? item.comment : '';
                nodeHtml += Gikam.printf(nodeTpl, {
                    text : '<span class="name" title="'+ item.createdByName +'">'+ item.createdByName +'</span>' +
                        '<span class="time">'+ item.createdTime +'</span>' +
                        '<span class="explain '+ explainColor +'">'+ Gikam.propI18N(item.auditTrailName) +'</span>' + 
                        '<span class="time">'+ (Gikam.propI18N(item.comment) || '') +'</span>',
                    id : item.id
                });
            }
            
        });
        return Gikam.printf(treeTpl, {nodeHtml : nodeHtml});
    },

    // 创建右边的详情表
    creatDetailsTable: function (record, list) {
        var tableTpl = '<div class="node close details-item audit-trail-details-{recordId}">' +
                       '<div class="title">' + Gikam.propI18N(record.tableName + ".SERVICE_NAME") + '</div>'+ 
                       '</div>';
        
        var girdTpl = Gikam.printf(tableTpl, {
            recordId : record.recordId,
        });
        
        list.forEach(function(item) {
            item.columnNew = Gikam.propI18N(item.tableName + '.' + item.column.toUpperCase());
        });
        
        Gikam.jQuery('.starter-audit-trail .details-body').append(girdTpl);
        Gikam.create('grid', {
            renderTo: Gikam.jQuery('.audit-trail-details-' + record.recordId)[0],
            page: false,
            filter: false,
            columns: [
                {
                    title: 'T_CORE_AT_RECORD_LINE.COLUMNNAME',
                    field: 'columnNew'
                },
                {
                    title: 'T_CORE_AT_RECORD_LINE.OLDVALUE',
                    field: 'oldValue'
                },
                {
                    title: 'T_CORE_AT_RECORD_LINE.NEWVALUE',
                    field: 'newValue'
                }
            ],
            data: list
        });
    },

    // 点击列表 - recordId,tableName
    auditDetails: function (id) {
        var _this = this;
        url = Gikam.printf(coreAtRecord.baseUrl + '/queries/record-lines/{id}', {
            id : id,
        });

        Gikam.post(url, null).done(function(data) {
            _this.auditDetailsList(data);
        });
    },

    // 获取详情list
    auditDetailsList: function(data) {
        var _this = this;
        Gikam.jQuery('.starter-audit-trail .details-body').html(null); // 每次点击前清空详情

        if (data.length > 0) {
            data.forEach(function(item) {
                url = Gikam.printf(coreAtRecord.baseUrl + '/queries/record-lines/{recordId}/{tableName}', {
                    recordId: item.recordId,
                    tableName: item.tableName 
                });
                Gikam.post(url, null).done(function(list) {
                    _this.creatDetailsTable(item, list);
                });
            });
        }
    },

    create : function() {
        var _this = this;
        
        Gikam.create('layout', {
            center : {
                items : [ {
                    type : 'tab',
                    onRendered : function() {
                         var $container = Gikam.jQuery('div[id="core-at-record-detail-time-panel"]').find('.panel-body');
                         _this.renderTree();
                         _this.$dom = Gikam.jQuery(_this.createTreeParam());
                         _this.$dom.appendTo($container);
                    },
                    panels : [ {
                        title : 'CORE.MODULE.SYS.ATS.TAB.TIME',
                        id : 'core-at-record-detail-time-panel',
                        items : [ ]
                    }, {
                        title : 'CORE.MODULE.SYS.ATS.TAB.TABLE',
                        items : [ this.getTableGridParam() ]
                    } ]
                } ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
    },
    
    init : function(param) {
        this.param = param;
        this.create();
    }
};