const u=`return [
        {
            // \u5408\u5E76\u663E\u793A\u5B57\u6BB5
            field: "name",
            // \u5B50\u5408\u5E76\u663E\u793A\u5B57\u6BB5
            children: [
                {
                    field: "age"
                }
            ]
        },
        {
            // \u7B2C\u4E8C\u4E2A\u5408\u5E76\u663E\u793A\u5B57\u6BB5
            field: "hobby"
        }
    ];`,t=`return {
        // fieldName_SEQ: '\u7B5B\u9009\u5185\u5BB9'
    };`,e=`return {
        // fieldName_EQ: '\u7B5B\u9009\u5185\u5BB9'
    };`,r=`return {
        // "userName": "desc"
    };`,a="// return row.id + '_' + row.name;",o="// return row.id + '_' + row.name;",n="// return {color:'#333', backgroundColor: '#F7FF96'};",s=`// return {
    //     13: 'right',
    // };`,i="// return row.id + '_' + row.name;",c=`return {
        // backgroundColor:'red',
        // fontWeight:600
    }`,l=`// \u8FD4\u56DEtrue\u9690\u85CF\uFF0C\u8FD4\u56DEfalse\u663E\u793A         
    return false;
`,d="// return [Gikam.DateUtils.formatter(new Date(0),'yyyy-MM-dd'),Gikam.DateUtils.formatter(new Date(),'yyyy-MM-dd')]",F="// return [{text:'sunway',value:'01'},{text:'world',value:'02'}]",m="// return {color: 'red'}",E=`// return {
    //     levels: [
    //         {
    //             name: 'day',
    //             label: '\u5929',
    //             scale_height: 27,
    //             min_column_width: 80,
    //             scales: [{ unit: 'day', step: 1, format: '%d %M' }]
    //         },
    //         {
    //             name: 'week',
    //             label: '\u5468',
    //             scale_height: 50,
    //             min_column_width: 50,
    //             scales: [
    //                 {
    //                     unit: 'week',
    //                     step: 1,
    //                     format: function (date) {
    //                         var dateToStr = this.$gantt.date.date_to_str('%d %M');
    //                         var endDate = this.$gantt.date.add(date, -6, 'day');
    //                         var weekNum = this.$gantt.date.date_to_str('%W')(date);
    //                         return '#' + weekNum + ', ' + dateToStr(date) + ' - ' + dateToStr(endDate);
    //                     }
    //                 },
    //                 {
    //                     unit: 'day',
    //                     step: 1,
    //                     format: '%j %D'
    //                 }
    //             ]
    //         },
    //         {
    //             name: 'month',
    //             label: '\u6708',
    //             scale_height: 50,
    //             min_column_width: 120,
    //             scales: [
    //                 { unit: 'month', format: '%F, %Y' },
    //                 { unit: 'week', format: 'Week #%W' }
    //             ]
    //         },
    //         {
    //             name: 'year',
    //             label: '\u5E74',
    //             scale_height: 50,
    //             min_column_width: 120,
    //             scales: [
    //                 { unit: 'year', format: 'Year #%Y' },
    //                 { unit: 'month', format: '%F, %Y' }
    //             ]
    //         }]
    // }
`,B="// gantt.init('gantt_here');",f="// \u4E0B\u62C9\u6846\u63D0\u4F9B\u7B2C\u56DB\u4E2A\u53C2\u6570 selectText",g="// return [{text:'sunway',value:'01'},{text:'world',value:'02'}]",C="// return row.id",h=" // return {}",A=`// return [
    //  {
    //      icon: 'cube-select',
    //      color: 'red',
    //      title: '\u89E3\u91CA\u4E00\u4E0B\u4E3A\u4EC0\u4E48\u662F\u7EA2\u8272',
    //      onClick: function (value, setValue) {

    //      },
    //   }
    // ]`,w="// return {type: 'success',value:'\u542F\u7528'}",D="// return [{fieldInputName: 'chooseFieldName'}]",p=`// return [
    //     {
    //         type: "form",
    //         ...
    //     },
    //     {
    //         type: "grid",
    //         ...
    //     },
    // ];
`,y=`  
       // \u793A\u4F8B
    return parseFloat(val.replace(/\\s+/g, "").toString());
`,_=`
    return {
        // \u56FE\u6807\u7F16\u7801,\u6765\u6E90\u4E8E\u4E09\u7EF4\u56FE\u6807\u5E93
        icon: 'search',
        // \u56FE\u6807\u7D27\u8DDF\u7684\u6587\u672C,\u975E\u5FC5\u9700
        text: '\u67E5\u8BE2',
        // \u9F20\u6807\u60AC\u6D6E\u56FE\u6807\u65F6\u63D0\u793A\u4FE1\u606F\uFF0C\u975E\u5FC5\u9700
        title: '\u9F20\u6807\u60AC\u6D6E\u63D0\u793A\u5185\u5BB9',
        // \u56FE\u6807\u989C\u8272,\u652F\u6301rgb\u548C16\u8FDB\u5236\u8272\u503C,\u975E\u5FC5\u9700
        color: '#007aff',
        // \u56FE\u6807\u5927\u5C0F\uFF0C\u975E\u5FC5\u9700
        size: '16px',
        // \u56FE\u6807\u70B9\u51FB\u4E8B\u4EF6\uFF0C\u975E\u5FC5\u9700
        onClick: function (row, field) {
            console.info(row);
        }
    };
`,x="return '\u9F20\u6807\u60AC\u6D6E\u5355\u5143\u683C\u63D0\u793A\u5185\u5BB9';",k=`
    return {
        // \u80CC\u666F\u8272
        backgroundColor: '#007aff',
        // \u5B57\u4F53\u989C\u8272
        color: '#fff',
        // \u5B57\u4F53\u5927\u5C0F
        fontSize: '12px'
    }
`,G=`
    return {
        pageSize: 18
    }
`,b=`
   //  \u793A\u4F8B
    return [
        {
            text: '\u81EA\u5B9A\u4E49',
            onClick: () => {
                console.log(123)
            }
        }
    ]
`,S=`
   //  \u793A\u4F8B
   return [
    {
        field: 'ext$.batchno',
        title: '\u6279\u53F7',
        width: 150,
    }, {
        field: '\u5730\u533A',
        title: '\u5730\u533A',
        width: 150,
    }, {
        field: 'ext$.\u4EBA\u6570',
        title: '\u4EBA\u6570',
        width: 150,
        calculated: true // \u9700\u8981\u8BA1\u7B97\u6570\u636E\u7684\u5B57\u6BB5
    }
]
`,v=`
    // \u793A\u4F8B
    return [
        {
            type: 'continuous',
            value: 3
        }
    ]
`,T=`
    // \u793A\u4F8B
    return [
        {
            text: '\u81EA\u5B9A\u4E49',
            value: 'a'
        }
    ]
`,N=`
    // \u793A\u4F8B
    options.columns = [
        {
            field: 'title',
            title: '\u6807\u9898'
        },
        {
            field: 'name',
            title: '\u540D\u5B57'
        }
    ];
    options.firstRowFixed = true;
    return options;
 `;export{i as A,s as B,a as C,o as D,n as E,G as F,p as G,k as H,l as a,F as b,c,D as d,A as e,m as f,d as g,S as h,b as i,v as j,T as k,N as l,E as m,B as n,f as o,w as p,_ as q,x as r,g as s,y as t,h as u,t as v,e as w,r as x,u as y,C as z};
