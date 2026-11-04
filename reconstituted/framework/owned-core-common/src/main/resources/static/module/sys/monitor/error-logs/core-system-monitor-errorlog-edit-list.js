coreSystemMonitorErrorlog.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-system-monitor-errorlog-edit-list-grid',
            url : coreSystemMonitorErrorlog.baseUrl + '/queries/raw',
            requestData : {
                revisionFlag_SEQ : '0',
            },
            service : 'coreSystemMonitorErrorLogServiceImpl',
            dbTable : 'T_CORE_SYSTEM_MONITOR_ERRORLOG',
            columns : coreSystemMonitorErrorlog.getEditListColumns(),
            onBeforeLoad : function (data){
                data.forEach(function (item){
                    item.viewErrorLog = Gikam.propI18N('MODULE.SYS.MONITOR.ERRORLOGS.BUTTON.VIEW_FULL_INFORMATION')
                });
            },
            onCellClick : function(field, row) {
                field === 'viewErrorLog' && _this.viewErrorLog(row.logDetail,Gikam.getComp('core-system-monitor-errorlog-edit-list-grid'));
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.MONITOR.ERRORLOGS.BUTTON.REVISE',
                icon : 'modify',
                onClick : function() {
                    _this.revisedErrorLog();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {

                    _this.deleteAbnormal();
                }
            },{
                type : 'button',
                text : 'GIKAM.CHEM.BUTTON.CLEAR',
                icon : 'remove-row',
                onClick : function() {
                    _this.clear();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.MONITOR.ABNORMALEVENTS.MODAL.DISPLAY_REVISED_ABNORMAL',
                icon : 'browse',
                onClick : function() {
                    _this.displayRevisedAbnormal();
                }
            }]
        }
    },
    //修订异常
    revisedErrorLog : function (){
        var selectedRows = Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').getSelections();
        if(Gikam.isEmpty(selectedRows)){
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return ;
        }
        for (let i = 0; i < selectedRows.length; i++) {
            if(selectedRows[i].revisionFlag =='1'){
                Gikam.alert('CORE.MODULE.SYS.MONITOR.ERRORLOGS.TIP.NOT_REVISION');
                return ;
            }
        }
        var modal = Gikam.create('modal', {
            id : 'modal',
            title : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.ERRORLOGS.TIPS.CONFIRM_REVISE_THEM'),
            width : 350,
            height : 200,
            onAfterClose() {
                Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').refresh();
            }
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            north : {
                height : 5,
                items : [{
                    type : 'btnToolbar',
                    items : [{
                        type: 'button',
                        text: '保存',
                        icon: 'save',
                        onClick: function () {
                            var data = Gikam.getComp('core-system-monitor-errorlog-edit-list-form').getData();
                            if (!data.remark){
                                Gikam.alert('CORE.MODULE.ITEM.ERROR_LOGS.TIP.NO_EMPTY');
                                return;
                            }
                            workspace.window.showMask(true);
                            Gikam.put(coreSystemMonitorErrorlog.baseUrl + '/action/revise', Gikam.getJsonWrapper({
                                remark : data.remark,
                            }, [ 'CoreSystemMonitorErrorLogServiceImpl', selectedRows ])).done(function() {
                                workspace.window.closeMask();
                                Gikam.getLastModal().close();
                                Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').refresh();
                            })
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.BACK',
                        icon: 'back',
                        onClick: function () {
                            Gikam.getLastModal().close();
                        }
                    }]
                }]
            },
            center : {
                items : [{
                    type: 'form',
                    id: 'core-system-monitor-errorlog-edit-list-form',
                    titleWidthAuto:true,
                    fields : coreSystemMonitorErrorlog.getBaseColumns(),
                }]
            }
        })
    },
    //  删除
    deleteAbnormal :function (){
        var selectedRows = Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').getSelections();
        if(Gikam.isEmpty(selectedRows)){
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return ;
        }

        Gikam.create('confirm', {
            message : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.ERRORLOGS.TIP.DELETE_NOT_REVISION'),
            onYesClick : function() {
                Gikam.del(coreSystemMonitorErrorlog.baseUrl + '/action/delete/no-revision-error-logs', Gikam.getJsonWrapper(null, [ 'CoreSystemMonitorErrorLogServiceImpl', selectedRows])).done(function() {
                    Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').refresh();
                })
            }
        });
    },
    //清空
    clear : function (){
        Gikam.create('confirm', {
            message : Gikam.propI18N('CORE.MODULE.SYS.MONITOR.ERRORLOGS.TIP.CLEAR_NOT_REVISION'),
            onYesClick : function() {
                Gikam.del(coreSystemMonitorErrorlog.baseUrl + '/action/clear', Gikam.getJsonWrapper(null, [ 'CoreSystemMonitorErrorLogServiceImpl', null])).done(function() {
                    Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').refresh();
                })
            }
        });
    },
    //显示已修订异常
    displayRevisedAbnormal : function (){
        var _this = this
        var modal = Gikam.create('modal',{
            id : 'modal',
            title : 'CORE.MODULE.SYS.MONITOR.ERRORLOGS.TIP.REVISIONED_ERROR_LOG',
            width : 1200,
            height: 600,
            toolbarTop : true,
            onAfterClose : function (){
                Gikam.getComp('core-system-monitor-errorlog-edit-list-grid').refresh();
            }
        })
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [{
                    type : 'grid',
                    id : 'core-system-monitor-errorlog-edit-list-grid-revised',
                    url : coreSystemMonitorErrorlog.baseUrl + '/queries/raw',
                    requestData : {
                        revisionFlag_SEQ : '1',
                    },
                    service : 'CoreSystemMonitorErrorLogServiceImpl',
                    dbTable : 'T_CORE_SYSTEM_MONITOR_ERRORLOG',
                    columns : coreSystemMonitorErrorlog.getEditListColumns(),
                    onBeforeLoad : function (data){
                        data.forEach(function (item){
                            item.viewErrorLog = Gikam.propI18N('MODULE.SYS.MONITOR.ERRORLOGS.BUTTON.VIEW_FULL_INFORMATION')
                        });
                    },
                    onCellClick : function(field, row) {
                        field === 'viewErrorLog' && _this.viewErrorLog(row.logDetail,Gikam.getComp('core-system-monitor-errorlog-edit-list-grid-revised'));
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'CORE.MODULE.SYS.MONITOR.ERRORLOGS.BUTTON.CANCEL_REVISE',
                        icon : 'modify',
                        onClick : function() {
                            var selectedRows = Gikam.getComp('core-system-monitor-errorlog-edit-list-grid-revised').getSelections();
                            if(Gikam.isEmpty(selectedRows)){
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return ;
                            }
                            Gikam.create('confirm', {
                                message :  Gikam.propI18N('CORE.MODULE.SYS.MONITOR.ERRORLOGS.TIPS.CONFIRM_REVISE_THEM'),
                                onYesClick : function() {
                                    Gikam.put(coreSystemMonitorErrorlog.baseUrl + '/action/cancel-revise', Gikam.getJsonWrapper(null, [ 'CoreSystemMonitorErrorLogServiceImpl', selectedRows ])).done(function() {
                                        Gikam.getComp('core-system-monitor-errorlog-edit-list-grid-revised').refresh();
                                    })
                                }
                            });
                        }
                    } ]
                }]
            }
        })
    },
    viewErrorLog : function(logDetail){
        var modal = Gikam.create('modal',{
            id : 'modal',
            title : 'T_CORE_SYSTEM_MONITOR_ERRORLOG.LOGDETAIL',
            width : 1200,
            height: 600,
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            north : {
                height : 5,
                items : [{
                    type: 'btnToolbar',
                    items: [{
                        type: 'button',
                        text: 'GIKAM.LES.SYSTEM_FLEXIBILITY.BUTTON.COPY',
                        icon: 'copy',
                        onClick: function () {
                            Gikam.copyText(logDetail)
                        }
                    }, {
                        type: 'button',
                        text: "CORE.MODULE.SYS.MONITOR.ERRORLOGS.BUTTON.DOWNLOAD",
                        icon: 'default',
                        onClick: function () {
                           var date = Gikam.DateUtils.formatter(new Date(), 'yyyyMMddhhmmSS');
                            Gikam.downloadText(date,logDetail)
                        }
                    }]
                }]
          },
          center : {
              items : [{
                  type: 'form',
                  id : 'core-system-monitor-errorlog-edit-list-form',
                  fields : [{
                      field : 'id',
                      renderType : 'html',
                      readonly : true,
                      width : '100%',
                      value : logDetail,
                      textStyleFormatter : function () {
                          return {
                              border : 'none',
                              background : 'none',
                              width : '100%'
                          }
                    }
                  }]
              }]
          },
        })
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-system-monitor-errorlog-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },
    init : function() {
        this.create();
    }
};
