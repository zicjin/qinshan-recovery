emcEquiptRequisition.instrumentOutPage = function () {
    return {
        getGridParam: function () {
            var _this = this;
            var requestData = _this.param;
            requestData.isBack_SEQ = '0';
            requestData.rentUser_SEQ = workspace.user.userName;
            return {
                type: 'grid',
                id: 'emc-instrument-out-edit-list-grid',
                url: emcEquiptRequisition.baseUrl + '/queries/raw',
                service: 'emcEquiptRequisitionServiceImpl',
                columns: emcEquiptRequisition.getInstrumentOutEditListColumns(),
                page: true,
                requestData: requestData,
                deleteFormatter: function (row) {
                    return row.ext$.equiptcode;
                },
                toolbar: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.INSERT',
                    icon: 'add',
                    onClick: function () {
                        Gikam.preInsert({
                            modalTitle: '仪器使用',
                            fields: emcEquiptRequisition.getRentColumns(),
                            displayReadonly : false,
                            url: emcEquiptRequisition.baseUrl,
                            param: {
                                projIds: _this.param.projIds,
                                projNos: _this.param.projNos,
                            },
                            onFormRendered : function(form) {
                                this.removeFieldValidator('accessory', [ 'notEmpty' ]);
                            }
                        }).done(function (id) {
                            Gikam.getComp('emc-instrument-out-edit-list-grid').refresh();
                        });
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.DELETE',
                    icon: 'remove',
                    onClick: function () {
                    	var selections = Gikam.getComp('emc-instrument-out-edit-list-grid').getSelections();
                    	if(Gikam.isEmpty(selections)){
                    		Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    		return;
                    	}
                    	Gikam.post(emcEquiptRequisition.baseUrl + '/delete-instrument-out',Gikam.getJsonWrapper({
                    		projIds : _this.param.projIds,
                    		projNos: _this.param.projNos,
                    	},['emcEquiptRequisitionServiceImpl',selections])).always(function(){
                    		Gikam.getComp('emc-instrument-out-edit-list-grid').refresh();
                    	});
                    }
                },],
            }
        },

        create: function () {
            var _this = this;
            Gikam.create('layout', {
                id: 'emc-instrument-out-edit-list-layout',
                renderTo: Gikam.getLastModal().window.$dom,
                center: {
                    items: [_this.getGridParam()]
                },
            });
        },

        init: function (param) {
            this.param = param;
            this.create();
        }
    }
};
