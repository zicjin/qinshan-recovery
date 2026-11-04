coreSqlGenerateTable.sqlPage = {

    getSqlForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'core-generate-sql-form',
            fields : coreSqlGenerateTable.getSqlFields(),
            columns : 1,
            onRendered : function() {
                this.setData(coreSqlGenerateTable.generateSql);
            }
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getSqlForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function() {
        this.createPage();
    }

}