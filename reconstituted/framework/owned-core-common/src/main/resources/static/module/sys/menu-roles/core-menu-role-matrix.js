coreMenuRole.matrixPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'treeGrid',
            id : 'core-menu-role-matrix-grid',
            columns : coreMenuRole.getMatrixColumns(),
            page : false,
            autoSave : false,
            generalButtonGroup : [ 'refresh', 'export' ],
            checkbox : false,
            url : coreMenuRole.baseUrl + '/queries/matrix-rows',
            onRendered : function() {
                Gikam.postSync(coreMenuRole.baseUrl + '/queries/matrix-headers').done(function(columns) {
                    columns.map(function(data) {
                        data['width'] = '100';
                        data['sort'] = false;
                        data.onChange = function(field, value, index) {
                            var grid = Gikam.getComp('core-menu-role-matrix-grid');
                            var gridData = grid.getData();
                            var row = gridData[index];

                            var selectedRows = _this.getSelectedRows(value, field, row, gridData);
                            var updatedNodes = _this.getUpdatedNodes(value, field, row, selectedRows);
                            _this.doUpdate(updatedNodes, field, selectedRows, gridData);
                        }
                    });
                    var oldColumns = coreMenuRole.getMatrixColumns();
                    columns = oldColumns.concat(columns);
                    Gikam.getComp('core-menu-role-matrix-grid').refreshColumns(columns);
                });
            }
        }
    },
    
    freshCascade : function(field, selectedRows, gridData) {
        for (var i = 0; i < selectedRows.length; i++) {
            var row = selectedRows[i];
            for (var j = 0; j < gridData.length; j++) {
                if (row.id === gridData[j].id) {
                    gridData[j][field] = row[field];
                }
            }
        }
        Gikam.getComp('core-menu-role-matrix-grid').refreshByData({rows : gridData, initPageData : false}, false);
    },

    getNodeType : function(row) {
        var nodeType = null;
        if (Gikam.isEmpty(row.parentid)) {
            nodeType = 'root'; // 根节点
        } else {
            if (Gikam.isEmpty(row.children)) {
                nodeType = 'leaf'; // 叶子节点
            } else {
                nodeType = 'other'; // 其它
            }
        }
        return nodeType;
    },

    /**
     * 获取需要选中的行 
     *    (1)根节点:获取该节点及该节点的所有子节点。
     *    (2)叶子节: 获取该节点，然后根据操作类型判断，如果操作类型为选中，获取选中操作时需级联选中的父节点。如果操作类型为取消选中，获取取消选中操作需要级联取消的父节点。
     *    (3)既不是叶子节点也不是根节点：获取该节点及该节点的所有子节点。然后根据操作类型判断，如果操作类型为选中，获取选中需选中的父节点。如果操作类型为取消选中，获取需要取消选中的父节点。
     * 
     * @param nodeType 节点类型 (root:根节点/leaf:叶子节点/other:既不是根节点也不是叶子节点)
     * @param optType 操作类型(1:选中/0:取消选中)
     * @param field 字段名（选中/取消选中所在的列名）
     * @param row 选中/取消选中操作所在的行
     * @param gridData 表格数据
     */
    getSelectedRows : function(optType, field, row, gridData) {
        var _this = this;
        var nodeType = _this.getNodeType(row);
        var rowArray = new Array();

        if ('root' === nodeType) { // 根节点
            rowArray = rowArray.concat(_this.getSelfAndAllChildRows(optType, row.id, field, row, gridData));
        } else if ('leaf' === nodeType) { // 叶子节点
            row['modifyFlag'] = '1';
            row[field] = optType;
            rowArray.push(row);
            if ('1' === optType) { // 选中操作
                rowArray = rowArray.concat(_this.getCheckedParentRows(field, row, gridData));
            } else if ('0' === optType) { // 取消选中操作
                rowArray = rowArray.concat(_this.getUncheckedParentRows(field, row, gridData));
            }
        } else if ('other' === nodeType) { // 既不是根节点，也不是叶子节点。
            rowArray = rowArray.concat(_this.getSelfAndAllChildRows(optType, row.id, field, row, gridData));
            if ('1' === optType) { // 选中操作
                rowArray = rowArray.concat(_this.getCheckedParentRows(field, row, gridData));
            } else if ('0' === optType) { // 取消选中操作
                rowArray = rowArray.concat(_this.getUncheckedParentRows(field, row, gridData));
            }
        }
        return rowArray;
    },

    // 根据选中的行，获取需更新的节点集合
    getUpdatedNodes : function(optType, field, row, rowArray) {
        var _this = this;
        var nodeArray = new Array();

        rowArray.forEach(function(item) {
            if (Gikam.isNotEmpty(item.modifyFlag) && item.modifyFlag === '1') {
                var node = {};
                node.id = item.id;
                node[field] = optType;
                nodeArray.push(node);
            }
        });
        return nodeArray;
    },

    doUpdate : function(nodeArray, field, selectedRows, gridData) {
        var _this = this;
        var url = coreMenuRole.baseUrl + '/instant';
        workspace.window.showMask();
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', nodeArray ])).done(function() {
            _this.freshCascade(field, selectedRows, gridData);
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    getSelfAndAllChildRows : function(optType, id, field, row, gridData) {
        var _this = this;
        var rowArray = new Array();
        
        if (row.id === id) { // 本身
            row['modifyFlag'] = '1';
            rowArray.push(row);
        } else {
            if (optType === '1') { // 选中操作
                if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 节点本身为未选中状态, 需要向后端传值，更新选中状态为1
                    row['modifyFlag'] = '1';
                    row[field] = '1';
                    rowArray.push(row);
                } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 节点本身为半选中状态，不需要向后端传值，但需要更新选中状态为1
                    row['modifyFlag'] = '0';
                    row[field] = '1';
                    rowArray.push(row);
                } else if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 本身为选中状态，无需改动，忽略
                    // 忽略
                }
            } else if (optType === '0') { // 取消选中操作
                if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 节点本身为半选中状态，需要向后端传值，需要更新选中状态为0
                    row['modifyFlag'] = '1';
                    row[field] = '0';
                    rowArray.push(row);
                } else if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 本身为选中状态，需要向后端传值，需要更新选中状态为0
                    row['modifyFlag'] = '1';
                    row[field] = '0';
                    rowArray.push(row);
                } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 节点本身为未选中状态，无需改动，忽略
                    // 忽略
                }
            }
        }
        
        if (Gikam.isEmpty(row.children)) {
            return rowArray;
        } else {
            var childRows = _this.getChildRows(optType, field, row, gridData);
            for (var i = 0; i < childRows.length; i++) {
                rowArray = rowArray.concat(_this.getSelfAndAllChildRows(optType, id, field, childRows[i], gridData));
            }
            return rowArray;
        }
    },

    getChildRows : function(optType, field, row, gridData) {
        var childRows = new Array();

        for (var i = 0; i < gridData.length; i++) {
            if (row.id === gridData[i].parentid) {
                childRows.push(gridData[i]);
            }
        }
        return childRows;
    },

    getBrotherRows : function(row, gridData) {
        var parentId = row.parentid;

        var brotherRows = new Array();
        
        for (var i = 0; i < gridData.length; i++) {
            if (parentId === gridData[i].parentid && row.id !== gridData[i].id) {
                brotherRows.push(gridData[i]);
            }
        }
        return brotherRows;
    },

    getParentRow : function(row, gridData) {
        var parentId = row.parentid;
        if (Gikam.isEmpty(parentId)) {
            return null;
        }
        var parentRow = null;

        for (var i = 0; i < gridData.length; i++) {
            if (parentId === gridData[i].id) {
                parentRow = gridData[i];
                break;
            }
        }
        return parentRow;
    },

    getUncheckedParentRows : function(field, row, gridData) {
        var _this = this;
        var rowArray = new Array();
        if (Gikam.isEmpty(row.parentid)) {
            return rowArray;
        }
        var brotherRows = _this.getBrotherRows(row, gridData);
        var parentRow = _this.getParentRow(row, gridData);
        
        if (Gikam.isEmpty(brotherRows)) { // 没有兄弟节点
            if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 当前节点为选中
                if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为1（实际可能不存在此种情况）
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '1';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，不需向后端传值，需更新选中状态为1（实际可能不存在此种情况）
                    parentRow['modifyFlag'] = '0';
                    parentRow[field] = '1';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 当前节点为半选中
                if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2（实际可能不存在此种情况）
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '2';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动（实际可能不存在此种情况）
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,不需要向后端传值，更新选中为2无需改动
                    parentRow['modifyFlag'] = '0';
                    parentRow[field] = '2';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 当前节点为未选中
                if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，需向后端传值，更新选中状态为0
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '0';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,需要向后端传值，更新选中状态为0
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '0';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            }
        } else { // 有兄弟节点
            var flag = false;
            for (var i = 0; i < brotherRows.length; i++) {
                if (Gikam.isNotEmpty(brotherRows[i][field]) && (brotherRows[i][field] === '1' || brotherRows[i][field] === '2')) {
                    flag = true;
                    break;
                }
            }
            if (flag) { // 有选中或半选中的兄弟节点
                if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为已选中状态，不需要向后端传值，但需置更新选中状态为2
                    parentRow['modifyFlag'] = '0';
                    parentRow[field] = '2';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            } else { // 没有选中或半选中的兄弟节点
                if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 当前节点为选中
                    if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为1（实际可能不存在此种情况）
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '1';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需改动
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    }
                } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 当前节点为半选中
                    if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2（实际可能不存在此种情况）
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '2';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动（实际可能不存在此种情况）
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,不需要向后端传值，更新选中为2无需改动
                        parentRow['modifyFlag'] = '0';
                        parentRow[field] = '2';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    }
                } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 当前节点为未选中
                    if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，无需改动
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，需向后端传值，更新选中状态为0
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '0';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,需要向后端传值，更新选中状态为0
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '0';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    }
                }
            }
        }
    },

    getCheckedParentRows : function(field, row, gridData) {
        var _this = this;
        var rowArray = new Array();
        if (Gikam.isEmpty(row.parentid)) {
            return rowArray;
        }
        
        var parentRow = _this.getParentRow(row, gridData);
        var brotherRows = _this.getBrotherRows(row, gridData);
        
        if (Gikam.isEmpty(brotherRows)) { // 没有兄弟节点
            if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 当前节点为选中
                if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为1
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '1';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，不需向后端传值，需更新选中状态为1
                    parentRow['modifyFlag'] = '0';
                    parentRow[field] = '1';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 当前节点为半选中
                if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '2';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,不需要向后端传值，更新选中为2
                    parentRow['modifyFlag'] = '0';
                    parentRow[field] = '2';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 当前节点为未选中（实际可能不存在此种情况）
                if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，无需改动
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，需向后端传值，更新选中状态为0
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '0';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,需要向后端传值，更新选中状态为0
                    parentRow['modifyFlag'] = '1';
                    parentRow[field] = '0';
                    rowArray.push(parentRow);
                    return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                }
            }
        } else { // 有兄弟节点
            var flag = true;
            var halfCheckedNum = 0; // 半选中
            var checkedCheckedNum = 0; // 选中
            var unCheckedNum = 0; // 未选中
            for (var i = 0; i < brotherRows.length; i++) {
                if (Gikam.isEmpty(brotherRows[i][field]) || brotherRows[i][field] === '0') {
                    unCheckedNum = unCheckedNum + 1;
                } else if (Gikam.isNotEmpty(brotherRows[i][field]) && brotherRows[i][field] === '1') {
                    checkedCheckedNum = checkedCheckedNum + 1;
                } else if (Gikam.isNotEmpty(brotherRows[i][field]) && brotherRows[i][field] === '2') {
                    halfCheckedNum = halfCheckedNum + 1;
                }
            }
            
            if (unCheckedNum > 0 && unCheckedNum == brotherRows.length) { // 兄弟节点全部未选中
                if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 当前节点为选中
                    if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '2';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需向后端传值，更新选中状态为2
                        parentRow['modifyFlag'] = '0';
                        parentRow[field] = '2';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    }
                } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 当前节点为半选中
                    if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2（实际可能不存在此种情况）
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '2';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,不需要向后端传值，更新选中为2
                        parentRow['modifyFlag'] = '0';
                        parentRow[field] = '2';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    }
                } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 当前节点为未选中
                    if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，无需改动
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，需向后端传值，更新选中状态为0
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '0';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,需要向后端传值，更新选中状态为0
                        parentRow['modifyFlag'] = '1';
                        parentRow[field] = '0';
                        rowArray.push(parentRow);
                        return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                    }
                }
            } else {
                if (halfCheckedNum > 0 || unCheckedNum > 0) { // 存在半选中的兄弟节点
                    if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 当前节点为选中
                        if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2
                            parentRow['modifyFlag'] = '1';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需向后端传值，更新选中状态为2
                            parentRow['modifyFlag'] = '0';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        }
                    } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 当前节点为半选中
                        if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为2（实际可能不存在此种情况）
                            parentRow['modifyFlag'] = '1';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,不需要向后端传值，更新选中为2
                            parentRow['modifyFlag'] = '0';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        }
                    } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 当前节点为未选中
                        if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需向后端传值，更新选中状态为2
                            parentRow['modifyFlag'] = '1';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，无需改动
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需要向后端传值，更新选中状态为2
                            parentRow['modifyFlag'] = '0';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        }
                    }
                } else { // 兄弟节点全部为选中的节点
                    if (Gikam.isNotEmpty(row[field]) && row[field] === '1') { // 当前节点为选中
                        if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需要向后端传值，更新选中状态为1
                            parentRow['modifyFlag'] = '1';
                            parentRow[field] = '1';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需向后端传值，更新选中状态为1
                            parentRow['modifyFlag'] = '0';
                            parentRow[field] = '1';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需向后端传值，更新选中状态为2
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        }
                    } else if (Gikam.isNotEmpty(row[field]) && row[field] === '2') { // 当前节点为半选中
                        if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需向后端传值，更新状态为2
                            parentRow['modifyFlag'] = '1';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态,无需改动
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,不需要向后端传值，更新选中为2
                            parentRow['modifyFlag'] = '0';
                            parentRow[field] = '2';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        }
                    } else if (Gikam.isEmpty(row[field]) || row[field] === '0') { // 当前节点为未选中
                        if (Gikam.isEmpty(parentRow[field]) || parentRow[field] === '0') { // 父节点本身为未选中状态，需向后端传值，更新选中状态为1
                            parentRow['modifyFlag'] = '1';
                            parentRow[field] = '1';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '2') { // 父节点本身为半选中状态，无需向后端传值，更新选中状态为1
                            parentRow['modifyFlag'] = '0';
                            parentRow[field] = '1';
                            rowArray.push(parentRow);
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        } else if (Gikam.isNotEmpty(parentRow[field]) && parentRow[field] === '1') { // 父节点本身为选中状态,无需改动
                            return rowArray.concat(_this.getUncheckedParentRows(field, parentRow, gridData));
                        }
                    }
                }
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-menu-role-matrix-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};