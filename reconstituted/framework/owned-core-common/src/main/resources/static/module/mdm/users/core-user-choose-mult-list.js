coreUser.chooseMultPage = {

    getChooseGridParam: function() {
        var _this = this, columns = coreUser.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio: true
        }) : columns.unshift({
            checkbox: true
        });
        _this.param.status_DIFFER = 'deprecated';
        return {
            type: 'grid',
            id: 'core-user-choose-mult-list-grid',
            url: coreUser.baseUrl + '/queries/choosable',
            requestData: _this.param,
            columns: columns,
            filterOpen: true,
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function() {
                    _this.add();
                }
            }],
            onBeforeLoad: function(data) {
                var choosenData = Gikam.getComp('core-user-choosen-mult-list-grid').getData();
                if (!Gikam.isEmptyObject(choosenData)) {
                    data = data.filter(function(item) {
                        var flag = true;
                        Gikam.each(choosenData, function(i, row) {
                            if (row.id === item.id) {
                                flag = false;
                            }
                        });
                        return flag;
                    });
                }
                return data;
            }
        }
    },

    getChoosenGridParam: function() {
        var _this = this, columns = coreUser.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio: true
        }) : columns.unshift({
            checkbox: true
        });
        return {
            type: 'grid',
            id: 'core-user-choosen-mult-list-grid',
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function() {
                    _this.remove();
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.CONFIRM',
                icon: 'select',
                onClick: function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-user-choosen-mult-list-grid').getData());
                }
            }],
            columns: columns,
            page: false
        }
    },

    add: function() {
        var _this = this;
        var chooseMultListGrid = Gikam.getComp('core-user-choose-mult-list-grid');
        var choosenMultListGrid = Gikam.getComp('core-user-choosen-mult-list-grid');
        var addUserData = [];
        var choosenData = choosenMultListGrid.getData() ? choosenMultListGrid.getData() : [];
        var addRowIndexs = chooseMultListGrid.getSelections().map(function(item) {
            return item.gridIndex
        });

        var chooseData = Gikam.jQuery.map(chooseMultListGrid.getData(), function(rowData, index) {
            if (Gikam.jQuery.inArray(index, addRowIndexs) < 0) {
                return rowData;
            }
            addUserData.push(rowData);
        });

        choosenData = choosenData.concat(addUserData);

        _this.arrangeGrid(choosenData);

    },

    remove: function() {
        var _this = this, chooseMultListGrid = Gikam.getComp('core-user-choose-mult-list-grid'), choosenMultListGrid = Gikam.getComp('core-user-choosen-mult-list-grid'), oldChoosenData = choosenMultListGrid.getData();
        var removedRowIndexs = choosenMultListGrid.getSelections().map(function(item) {
            return item.gridIndex
        });
        var choosenData = Gikam.jQuery.map(oldChoosenData, function(rowData, index) {
            if (Gikam.jQuery.inArray(index, removedRowIndexs) < 0) {
                return rowData;
            }
        });

        _this.arrangeGrid(choosenData);
    },

    arrangeGrid: function(choosenData) {
        var chooseMultListGrid = Gikam.getComp('core-user-choose-mult-list-grid'),
            choosenMultListGrid = Gikam.getComp('core-user-choosen-mult-list-grid');

        choosenMultListGrid.refreshByData({
            rows: choosenData
        });

        chooseMultListGrid.refresh();

        choosenMultListGrid.selectedRowIndex = [];

    },

    create: function() {
        var _this = this;
        Gikam.create('layout', {
            id: 'core-user-choose-list-layout',
            renderTo: Gikam.getLastModal().window.$dom,
            west: {
                width: '50%',
                items: [this.getChooseGridParam()]
            },
            center: {
                items: [this.getChoosenGridParam()]
            }
        });
    },
    init: function(param) {
        this.param = param;
        this.create();
    }
};