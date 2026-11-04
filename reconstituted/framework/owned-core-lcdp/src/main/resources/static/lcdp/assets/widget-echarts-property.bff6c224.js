import{d as p,l as o,n as u}from"./index.09c835c7.js";import{C as l}from"./index.98543100.js";import"./openAnimation.10286f8e.js";const d=p({__name:"widget-echarts-property",props:{widgetConfig:null},emits:["change"],setup(s,{emit:n}){const a=l.Panel,e=[{label:o.value["widget.grid.component.id"],valueName:"id",type:"IdCreator"},{label:o.value["widget.echarts.option"],desc:o.value["widget.echarts.option.desc"],valueName:"getOption",type:"InputScript",body:`return {
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        },
        yAxis: {
            type: 'value',
        },
        series: [
            {
                data: [150, 230, 224, 218, 135, 147, 260],
                type: 'line',
            },
        ],
    }`},{label:o.value["widget.echarts.config"],desc:o.value["widget.echarts.config.desc"],valueName:"getConfig",type:"InputScript",body:`return {
        dropDownMenu: [
            {
                type: 'button',
                text: '\u6309\u94AE\u4E00',
                onClick: function () {
                    alert('\u6309\u94AE\u4E00');
                },
            },
            {
                type: 'button',
                text: '\u6309\u94AE\u4E00',
                onClick: function () {
                    alert('\u6309\u94AE\u4E00');
                },
            },
        ],
        search: {
            type: 'form',
            fields: [
                {
                    field: 'field',
                    title: '\u67E5\u8BE2\u6761\u4EF6\u4E00',
                },
            ],
        },
    }`}],t=[{label:"onReady",valueName:"onReady",type:"InputScript",args:"echarts, myEcharts"}];function i(c,r){n("change",c,r instanceof Event?r.target.value:r)}return{__sfc:!0,CollapsePanel:a,echartsConfigList:e,events:t,emits:n,propertyChangeHandle:i,Collapse:l,locale:o}}});var g=function(){var n=this,a=n._self._c,e=n._self._setupProxy;return a(e.Collapse,{attrs:{expandIconPosition:"right",activeKey:["echarts","events"]},scopedSlots:n._u([{key:"expandIcon",fn:function(t){return[t.isActive?a("LcdpIconLinearArrowsDown"):a("LcdpIconLinearArrowsRight")]}}])},[a(e.CollapsePanel,{key:"echarts",attrs:{header:e.locale["normal.base.property"]}},n._l(e.echartsConfigList,function(t){return a(`Item${t.type}`,{key:t.label,tag:"component",attrs:{formItemProperty:t,widgetConfig:n.widgetConfig},on:{change:e.propertyChangeHandle}})}),1),a(e.CollapsePanel,{key:"events",attrs:{header:e.locale["widget.grid.events"]}},n._l(e.events,function(t){return a(`Item${t.type}`,{key:t.label,tag:"component",attrs:{formItemProperty:t,widgetConfig:n.widgetConfig},on:{change:e.propertyChangeHandle}})}),1)],1)},f=[],y=u(d,g,f,!1,null,null,null,null);const v=y.exports;export{v as default};
