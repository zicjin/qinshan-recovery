emcBiology.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-biology-edit-list-grid',
            url : emcBiology.baseUrl + '/queries/raw',
            requestData : {
                parentId : 'root'
            },
            columns : emcBiology.getEditListColumns(),
            service : 'emcBiologyServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            deleteFormatter : function(row) {
                return row.speciesName || row.id;
            }
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.BUTTON.INSERT',
            icon : 'add',
            onClick : function() {
                var node = Gikam.getComp('emc-biology-edit-list-tree').getSelectedNode();
                var parentId = node ? node.nodeId : '';
                
                  Gikam.preInsert({                   
                      modalTitle :  'EMC.MODULE.MDM.BASEMDM.BIOLOGYS.MODAL.ADD', 
                      fields     :emcBiology.getPreInsertFields(), 
                      url : emcBiology.baseUrl,
                      param : {
                          parentId : parentId
                      }
                  }).done(function (data) {
                 Gikam.getComp('emc-biology-edit-list-tree').refreshNode(parentId);
                 Gikam.getComp('emc-biology-edit-list-grid').refresh();
                  
                  });                        
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.DELETE',
            icon : 'remove',
            onClick : function() {
                var node = Gikam.getComp('emc-biology-edit-list-tree').getSelectedNode();
                var parentId = node ? node.nodeId : '';
                Gikam.getComp('emc-biology-edit-list-grid').deleteRows(emcBiology.baseUrl).done(function (data) {
                    Gikam.getComp('emc-biology-edit-list-tree').refreshNode(parentId);
                    
                });             
            }
        } ];
    },

    getTree : function() {
        return {
            type : 'tree',
            id : 'emc-biology-edit-list-tree',
            name : 'EMC.MODULE.MDM.BASEMDM.BIOLOGYS.TREE.NAME',
            url : emcBiology.baseUrl + '/tree/{parentId}',
            onNodeSelected : function(nodeId) {
                Gikam.getComp('emc-biology-edit-list-grid').refresh({
                    requestData : {
                        parentId : Gikam.isEmpty(nodeId) ? 'root' : nodeId
                    }
                });
            }
        };
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-biology-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 320,
                items : [ this.getTree() ]
            },
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
