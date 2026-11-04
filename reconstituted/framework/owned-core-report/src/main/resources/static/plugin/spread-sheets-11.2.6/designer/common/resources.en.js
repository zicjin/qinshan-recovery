(function() {
    'use strict';

    var designer = GC.Spread.Sheets.Designer;
    var cn_res = {};

    cn_res.title = "Spread.Sheets Designer";
    cn_res.defaultFont = "11pt Calibri";
    cn_res.ok = "OK";
    cn_res.yes = "Yes";
    cn_res.no = "No";
    cn_res.apply = "Apply";
    cn_res.cancel = "Cancel";
    cn_res.close = "close";

    cn_res.fileAPINotSupported = "The current browser does not provide API to read local files, please use other modern browsers to use the functions related to reading local files.";
    cn_res.blobNotSupported = "The current browser does not support Blob and cannot save local files. Please use other modern browsers/formats to save local files.";

    cn_res.saveFileDialogTitle = "Save As";
    cn_res.openFileDialogTitle = "Open";
    cn_res.allSpreadFileFilter = 'All Spreadsheet files (*.ssjson *.xlsx)';
    cn_res.spreadFileFilter = 'SpreadJS file (*.ssjson)';
    cn_res.ssJSONToJSFilter = 'Javascript file (*.js)';
    cn_res.allExcelFilter = "All Excel files (*.xlsx *.xls)";
    cn_res.excelFileFilter = 'Excel Workbook(*.xlsx)';
    cn_res.excelFileFilter2 = 'Excel 97-2003 Workbook (*.xls)';
    cn_res.csvFileFilter = "CSV file (*.csv)";
    cn_res.pdfFileFilter = "PDF file(*.pdf)";
    cn_res.allFileFilter = 'All files (*.*)';
    cn_res.importFileDialogTitle = "Import";
    cn_res.exportFileDialogTitle = "Export";

    cn_res.insertCellInSheet = "Cannot move entire worksheet cell";
    cn_res.insertCellInMixtureRange = "This command cannot be used on a selection range that contains both an entire row or column and other cells. Try selecting only the entire row or column or group of cells.";
    cn_res.NotExecInMultiRanges = "This command cannot be used with multiple selection regions. Please select a single region and try this command again.";
    cn_res.unsavedWarning = "This file is not saved, save it?";

    cn_res.requestTempalteFail = "Tempal file request error.";
    cn_res.requestTemplateConfigFail = "Template configuration file request error.";
    cn_res.openFileFormatError = "The file format is incorrect.";

    cn_res.closingNotification = "Warning: The current file has been modified.\nDo you want to save changes to this file?";

    cn_res.sameSlicerName = "The slicer name is already in use. Please enter a unique name.";
    cn_res.nullSlicerName = "Invalid slicer name.";

    cn_res.changePartOfArrayWarning = "Array formula does not allow changing part content!";
    cn_res.exportCsvSheetIndexError = "Invalid sheet index.";

    cn_res.fontPicker = {
        familyLabelText: 'Font:',
        styleLabelText: 'Font:',
        sizeLabelText: 'Font size:',
        weightLabelText: 'Font weight:',
        colorLabelText: 'Color:',
        normalFontLabelText: 'normal font',
        previewLabelText: 'Preview',
        previewText: 'AaBbCcYyZz',
        effects: "special effects",
        underline: "underline",
        doubleUnderline: "Double Underline",
        strikethrough: "strikethrough",
        //
        // Fonts shown in font selector.
        //
        // the property's name means the font family name.
        // the property's value means the text shown in drop down list.
        //
        fontFamilies: {
            "Song style": "Song style",
            "Kaiti": "Kaiti",
            "Imitate Song": "Imitate Song",
            "bold": "bold",
            "New Song Style": "New Song Style",
            "SimSun": "SimSun",
            "KaiTi": "KaiTi",
            "FangSong": "FangSong",
            "SimHei": "SimHei",
            "NSimSun": "NSimSun",
            "Arial": "Arial",
            "'Arial Black'": "Arial Black",
            "Calibri": "Calibri",
            "Cambria": "Cambria",
            "Candara": "Candara",
            "Century": "Century",
            "'Courier New'": "Courier New",
            "'Comic Sans MS'": "Comic Sans MS",
            "Garamond": "Garamond",
            "Georgia": "Georgia",
            "'Malgun Gothic'": "Malgun Gothic",
            "Mangal": "Mangal",
            "Tahoma": "Tahoma",
            "Times": "Times",
            "'Times New Roman'": "Times New Roman",
            "'Trebuchet MS'": "Trebuchet MS",
            "Verdana": "Verdana",
            "Wingdings": "Wingdings",
            "Meiryo": "Meiryo",
            "'MS Gothic'": "MS Gothic",
            "'MS Mincho'": "MS Mincho",
            "'MS PGothic'": "MS PGothic",
            "'MS PMincho'": "MS PMincho"
        },
        fontStyles: {
            'normal': 'normal',
            'italic': 'italic',
            'oblique': 'oblique'
        },
        fontWeights: {
            'normal': 'normal',
            'bold': 'bold',
            'bolder': 'bolder ',
            'lighter': 'lighter'
        },
        alternativeFonts: "'Microsoft Yahei UI', 'Microsoft Yahei', Microsoft Yahei, SimSun, Song, sans-serif",
        defaultSize: '10'
    };

    cn_res.commonFormats = {
        Number: {
            format: "0.00",
            label: "Number"
        },
        Currency: {
            format: "$#,##0.00",
            label: "currency"
        },
        Accounting: {
            format: "$ #,##0.00;$ (#,##0.00);$ \"-\"??;@",
            label: "Accounting Special"
        },
        ShortDate: {
            format: "m/d/yyyy",
            label: "Short Date"
        },
        LongDate: {
            format: "dddd, mmmm dd, yyyy",
            label: "Long Date"
        },
        Time: {
            format: "h:mm:ss AM/PM",
            label: "time"
        },
        Percentage: {
            format: "0%",
            label: "percentage"
        },
        Fraction: {
            format: "# ?/?",
            label: "score"
        },
        Scientific: {
            format: "0.00E+00",
            label: "Scientific Notation"
        },
        Text: {
            format: "@",
            label: "text"
        },
        Comma: {
            format: " #,##0.00; (#,##0.00); \"-\"??;@",
            label: "Thousands"
        }
    };
    cn_res.customFormat = "Custom";
    cn_res.generalFormat = "General";

    cn_res.colorPicker = {
        themeColorsTitle: "Custom",
        standardColorsTitle: "standard",
        noFillText: "No Color",
        moreColorsText: "Other Colors...",
        colorDialogTitle: "color",
        red: "red: ",
        green: "green: ",
        blue: "Blue: ",
        newLabel: "Add",
        currentLabel: "current"
    };

    cn_res.formatDialog = {
        title: "Format Cells",
        number: 'number',
        alignment: 'alignment',
        fonts: "font",
        font: 'font',
        border: 'border',
        padding: 'padding',
        label: 'label',
        cellContent: "Cell Content",
        labelContent: "label content",
        text: "text",
        margin: "Margin",
        fill: 'fill',
        protection: 'protection',
        category: 'category:',
        backColor: 'background color',
        textAlignment: 'Text alignment',
        horizontalAlignment: 'Horizontal alignment:',
        verticalAlignment: 'Vertical alignment:',
        indent: 'Indent:',
        textControl: 'Text Control',
        wrapText: 'Wrap text',
        shrink: 'Shrink the font to fill',
        merge: 'merge cells',
        top: 'top',
        bottom: 'bottom',
        left: 'Left',
        right: 'to the right',
        center: 'Center',
        general: 'General',
        sampleText: 'text',
        cantMergeMessage: 'Overlapping ranges cannot be merged. ',
        lock: "lock",
        lockComments: 'Locking cells only works if the sheet is protected (click "Protect Sheet " in the "Format " drop-down list in the "Cells " area on the "Home " tab).',
        backGroundColor: "Background Color:",
        moreColorsText: "Other Colors",
        sample: "sample",
        preview: "Preview",
        paddingPreviewText: "Preview content",
        visibility: "visible",
        labelVisibility: {
            visible: "visible",
            hidden: "hidden",
            auto: "auto"
        },
        vertical: "Vertical text"
    };

    cn_res.formatComment = {
        title: "Format",
        protection: "protection",
        commentLocked: "Locked",
        commentLockText: "Lock Text",
        commentLockComments: 'Locking objects only works if the sheet is protected. To protect a sheet, click "Protect Sheet " in the "Format " drop-down list in the "Cells " area of the "Home " tab.',
        properties: "properties",
        positioning: "object position",
        internalMargin: "Inner Margin",
        moveSize: "Size and position change with cell",
        moveNoSize: "The size is fixed, and the position changes with the cell",
        noMoveSize: "fixed size and position",
        automatic: "automatic",
        autoSize: "Auto Size",
        colors: "colors and lines",
        size: "size",
        fill: "fill",
        line: "Line",
        height: "height",
        width: "width",
        lockRatio: "Lock Aspect Ratio",
        color: "color",
        transparency: "transparency",
        style: "style",
        dotted: "dotted line",
        dashed: "dashed line",
        solid: "solid line",
        double: "double line",
        none: "No bar",
        groove: "General",
        ridge: "ridge line",
        inset: "Inset",
        outset: "Convex"
    };

    cn_res.categories = {
        general: "General",
        numbers: "Number",
        currency: "currency",
        accounting: "Accounting only",
        date: "date",
        time: "time",
        percentage: "percentage",
        fraction: "fraction",
        scientific: "scientific notation",
        text: "text",
        special: "special",
        custom: "custom"
    };

    cn_res.formatNumberComments = {
        generalComments: "General cell formatting does not contain any specific number formatting.",
        numberComments: "The numeric format is used for the representation of general numbers. The currency and accounting formats provide specialized formats for monetary value calculations.",
        currencyComments: "The currency format is used to represent general monetary values. The accounting format can align a column of values with decimal points.",
        accountingComments: "Accounting format can align a column of values with currency symbols and decimal points.",
        dateComments: "The date format displays date and time series numeric values as date values.",
        timeComments: "The time format displays date and time series numeric values as time values.",
        percentageComments: "The percentage format multiplies the value in the cell by 100 and displays it as a percentage.",
        textComments: "In text cell formatting, numbers are treated as text. The cell displays exactly what is entered.",
        specialComments: "Special formats can be used to track data lists and database values.",
        customComments: "Generates a custom number format based on an existing format."
    };

    cn_res.formatNumberPickerSetting = {
        type: "Type:",
        decimalPlaces: "Decimal places:",
        symbol: "Currency symbol:",
        negativeNumber: "Negative Number:",
        separator: "Use thousands separator (,)",
        deleted: "delete",
        locale: "Locale (Country):",
        calendar: "Calendar type:"
    };

    cn_res.localeType = {
        en_us: "English (United States)",
        ja_jp: "Japanese"
    };

    cn_res.calendarType = {
        western: "Gregorian calendar",
        JER: "Japanese era name"
    };

    cn_res.fractionFormats = ["# ?/?", "# ??/??", "# ???/???", "# ?/2", "# ?/4", "# ?/8", "# ??/16", "# ?/10", "# ??/100"];

    cn_res.numberFormats = ["0", "0;[Red]0", "0_);(0)", "0_);[Red](0)", "#,##0", "#,##0;[Red]#,##0", "#,##0_);(#,##0)", "#,##0_);[Red](#,##0)"];

    cn_res.dateFormats = ["m/d/yyyy", "[$-F800]dddd, mmmm dd, yyyy", "m/d;@", "m/d/yy;@", "mm/dd/yy;@", "[$-409]d-mmm;@", "[$-409]d-mmm-yy;@", "[$-409]dd-mmm-yy;@", "[$-409]mmm-yy;@", "[$-409]mmmm-yy;@", "[$-409]mmmm d, yyyy;@", "[$-409]m/d/yy h:mm AM/PM;@", "m/d/yy h:mm;@", "[$-409]mmmmm;@", "[$-409]mmmmm-yy;@", "m/d/yyyy;@", "[$-409]d-mmm-yyyy;@"];

    cn_res.japanWesternDateFormat = ["yyyy' year 'm' month 'd' day';@", "yyyy'year'm'month';@", "m'month'd'day';@", "yyyy/m/d;@", "[$-409]yyyy/m/dh:mm AM/PM;@", "yyyy/m/dh:mm;@", "m/d;@", "m/d/yy;@", "mm/dd/yy;@", "[$-409]d-mmm;@", "[$-409]d-mmm-yy;@", "[$-409]dd-mmm-yy;@", "[$-409]mmm-yy;@", "[$-409]mmmm-yy;@", "[$-409]mmmmm;@", "[$-409]mmmmm-yy;@"];

    cn_res.japanEmperorReignDateFormat = ["[$-411]ge.md;@", "[$-411]ggge'year'm'month'd'day';@"];

    cn_res.timeFormats = ["[$-F400]h:mm:ss AM/PM", "h:mm;@", "[$-409]h:mm AM/PM;@", "h:mm:ss;@", "[$-409]h:mm:ss AM/PM;@", "mm:ss.0;@", "[h]:mm:ss;@", "[$-409]m/d/yy h:mm AM/PM;@", "m/d/yy h:mm;@"];

    cn_res.japanTimeFormats = ["h:mm;@", "[$-409]h:mm AM/PM;@", "h:mm:ss;@", "[$-409]h:mm:ss AM/PM;@", "[$-409]yyyy/m/dh:mm AM/PM;@", "yyyy/m/dh:mm;@", "h'hour'mm'minute';@", "h'hour'mm'minute'ss'second';@"];

    cn_res.textFormats = ["@"];

    cn_res.specialFormats = ["00000", "00000-0000", "[<=9999999]###-####;(###) ###-####", "000-00-0000"];

    cn_res.specialJapanFormats = ["[<=999]000;[<=9999]000-00;000-0000", "[<=99999999]####-####;(00) ####-####", "'△' #,##0;'▲' #,##0", "[DBNum1][$-411]General", "[DBNum2][$-411]General", "[DBNum3][$-411]0", "[DBNum3][$-411]#,##0"];

    cn_res.currencyFormats = ["#,##0", "#,##0;[Red]#,##0", "#,##0;-#,##0", "#,##0;[Red]-#,##0"];

    cn_res.percentageFormats = ["0%"];

    cn_res.scientificFormats = ["0E+00"];

    cn_res.accountingFormats = ["_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)", '_(* #,##0.00_);_(* (#,##0.00);_(* "-"_);_(@_)', ' $* #,##0.00_; $* #,##0.00_; $* "-"_;_@_', '_-[$¥-804]* #,##0.00_-;-[$¥-804]* #,##0.00_-;_-[$¥-804]* "-"_-;_ -@_-', '_-[$¥-411]* #,##0.00_-;-[$¥-411]* #,##0.00_-;_-[$¥-411]* "-"_-;_ -@_-', '_-[$₩-412]* #,##0.00_-;-[$₩-412]* #,##0.00_-;_-[$₩-412]* "-"_-;_ -@_-'];

    cn_res.customFormats = ["General", "0", "0.00", "#,##0", "#,##0.00", "#,##0;(#,##0)", "#,##0;[Red](#,##0)", "#,##0.00;(#,##0.00)", "#,##0.00;[Red](#,##0.00)", "$#,##0;($#,##0)", "$#,##0;[Red]($#,##0)", "$#,##0.00;($#,##0.00)", "$#,##0.00;[Red]($#,##0.00)", "0%", "0.00%", "0.00E+00", "##0.0E+0", "# ?/?", "# ??/??", "m/d/yyyy", "d-mmm-yy", "d-mmm", "mmm-yy", "h:mm AM/PM", "h:mm:ss AM/PM", "hh:mm", "hh:mm:ss", "m/d/yyyy hh:mm", "mm:ss", "mm:ss.0", "@", "[h]:mm:ss", "$ #,##0;$ (#,##0);$ \"-\";@", " #,##0; (#,##0); \"-\";@", "$ #,##0.00;$ (#,##0.00);$ \"-\"??;@", " #,##0.00; (#,##0.00); \"-\"??;@", "hh:mm:ss", "00000", "# ???/???", "000-00-0000", "[$-4]dddd, mmmm dd, yyyy", "m/d;@", "[<=9999999]###-####;(###) ###-####", "# ?/8"];

    cn_res.accountingSymbol = [["none", null, null], ["$", "$", "en-US"], ["¥(Chinese)", "¥", "zh-cn"], ["¥(Japanese)", "¥", "ja-jp"], ["₩(Korean)", "₩", "ko-kr"]];

    cn_res.specialType = ["Zip Code", "Zip Code + 4", "Phone Number", "Social Security Number"];

    cn_res.specialJapanType = ["Postal Number", "Phone number (Tokyo)", "Sign (+ = △; - = ▲)", "Chinese numerals (123,400)", "Big character (Yi Shibi Wanshenqian four hundred)", "Full-width (12345)", "Full-width girder cut り (12,345)"];

    cn_res.fractionType = ["The denominator is one digit (1/4)", "The denominator is two digits (21/25)", "The denominator is three digits (312/943)", "With 2 as the denominator (1/2)", "With 4 as the denominator (2/4)", "With 8 as the denominator (4/8)", "With 16 as the denominator (8/16)", "With 10 as the denominator (3/10)", "Percent (30/100)"];

    cn_res.negativeNumbers = {
        "-1234.10": "-1234.10",
        "red:1234.10": "1234.10",
        "(1234.10)": "(1234.10)",
        "red:(1234.10)": "(1234.10)"
    };

    cn_res.currencyNegativeNumbers = {
        "number1": "-1,234.10",
        "red:number2": "1,234.10",
        "number3": "-1,234.10",
        "red:number4": "-1,234.10"
    };

    cn_res.passwordDialog = {
        title: "Password",
        passwordLabel: "Password:"
    };
    cn_res.rowHeightDialog = {
        title: "Line Height",
        rowHeight: "Row height:",
        exception: "Row height must be an integer or a decimal.",
        exception2: "Row height must be between 0 and 9999999."
    };

    cn_res.chart = {
        formatChartArea: "Settings",
        properties: 'properties',
        moveAndSizeWithCells: 'Size and position change with cells',
        moveButDoNotSizeWithCells: 'The size is fixed and the position changes with the cells',
        locked: 'locked',
        color: 'color',
        transparency: 'transparency',
        selectedOption: {
            series: 'Series options',
            chartArea: 'Chart Options',
            chartTitle: 'Title option',
            legend: 'Legend options',
            plotArea: 'Plot area options',
            dataLabels: 'label options',
            primaryCategory: 'Axis options',
            primaryValue: 'Axis options',
            primaryCategoryTitle: 'Title options',
            primaryValueTitle: 'Title option',
            primaryCategoryMajorGridLine: 'Primary gridline options',
            primaryValueMajorGridLine: 'Primary gridline options',
            primaryCategoryMinorGridLine: 'Minor gridline options',
            primaryValueMinorGridLine: 'Minor gridline options',
            secondaryCategory: 'Axis options',
            secondaryValue: 'Axis options',
            secondaryCategoryTitle: 'Title options',
            secondaryValueTitle: 'Title option',
            secondaryCategoryMajorGridLine: 'Main gridline option',
            secondaryValueMajorGridLine: 'Main gridline options',
            secondaryCategoryMinorGridLine: 'Minor gridline options',
            secondaryValueMinorGridLine: 'Secondary gridline options'
        },
        selectedText: {
            series: 'series',
            chartArea: 'Chart area',
            chartTitle: 'Chart title',
            legend: 'Legend',
            dataLabels: 'Data Labels',
            plotArea: 'plot area',
            primaryCategory: 'Horizontal (category) axis',
            primaryValue: 'Vertical (value) axis',
            primaryCategoryTitle: 'Horizontal (category) axis title',
            primaryValueTitle: 'Vertical (value) axis title',
            primaryCategoryMajorGridLine: 'Horizontal (category) axis primary gridline',
            primaryValueMajorGridLine: 'Vertical (value) axis major gridline',
            primaryCategoryMinorGridLine: 'Horizontal (category) axis minor gridline',
            primaryValueMinorGridLine: 'Vertical (value) axis minor gridline',
            secondaryCategory: 'Secondary axis horizontal (category) axis',
            secondaryValue: 'Secondary axis vertical (value) axis',
            secondaryCategoryTitle: 'Secondary axis horizontal (category) axis title',
            secondaryValueTitle: 'Secondary axis vertical (value) axis title',
            secondaryCategoryMajorGridLine: 'Secondary axis horizontal (category) axis main grid line',
            secondaryValueMajorGridLine: 'Secondary axis vertical (value) axis main grid line',
            secondaryCategoryMinorGridLine: 'Secondary axis horizontal (category) axis secondary grid line',
            secondaryValueMinorGridLine: 'Secondary axis vertical (value) axis secondary grid line'
        },
        selectedBarChartText: {
            primaryCategory: 'Vertical (category) axis',
            primaryValue: 'Horizontal (value) axis',
            primaryCategoryTitle: 'Vertical (category) axis title',
            primaryValueTitle: 'Horizontal (value) axis title',
            primaryCategoryMajorGridLine: 'Vertical (category) axis primary gridline',
            primaryValueMajorGridLine: 'Horizontal (value) axis primary gridline',
            primaryCategoryMinorGridLine: 'Vertical (category) axis minor gridline',
            primaryValueMinorGridLine: 'Horizontal (value) axis minor gridline',
            secondaryCategory: 'Secondary axis vertical (category) axis',
            secondaryValue: 'Secondary axis horizontal (value) axis',
            secondaryCategoryTitle: 'Secondary axis vertical (category) axis title',
            secondaryValueTitle: 'Secondary axis horizontal (value) axis title',
            secondaryCategoryMajorGridLine: 'Secondary axis vertical (category) axis main grid line',
            secondaryValueMajorGridLine: 'Secondary axis horizontal (value) axis main grid line',
            secondaryCategoryMinorGridLine: 'Secondary axis vertical (category) axis secondary grid line',
            secondaryValueMinorGridLine: 'Secondary axis horizontal (value) axis secondary grid line'
        },
        formatChart: {
            dataSeries: 'Data Series Format',
            axis: 'axis format',
            legend: 'Legend format',
            dataLable: 'Data label format',
            chartTitle: 'Chart title format',
            plotArea: 'plot area format',
            chartArea: 'Chart area format'
        }
    };

    cn_res.chartSliderPanel = {
        tick: {
            cross: "cross",
            inside: 'inside',
            none: 'none',
            outSide: 'outside'
        },
        noLine: 'No Line',
        solidLine: "solid line",
        width: "width",
        fontFamily: "font",
        fontSize: "font size",
        noFill: 'No Fill',
        solidFill: "Solid Fill",
        automatic: 'automatic',
        color: 'color',
        text: 'text',
        majorType: 'major tick type',
        minorType: 'Minor tick type',
        textAxis: 'text axis',
        dateAxis: 'date axis',
        unitsMajor: 'Units (large)',
        unitsMinor: 'units (small)',
        maximum: 'maximum',
        minimum: 'minimum',
        height: 'height',
        top: 'top',
        bottom: 'bottom',
        left: 'Left',
        right: 'to the right',
        primaryAxis: 'primary axis',
        secondaryAxis: 'Secondary axis',
        tickMarks: 'tick marks',
        axisOptions: 'Axis options',
        line: 'line',
        font: 'font',
        textFill: 'Text Fill',
        textEditor: 'Text Editor',
        size: 'size',
        fill: 'fill',
        legendPosition: 'Legend Position',
        seriesOptions: 'Series Options',
        border: 'border'
    };

    cn_res.moveChartDialog = {
        title: "Mobile Chart",
        description: "Choose where to place the chart:",
        newSheet: "New sheet:",
        existingSheet: "Object is located at:",
        errorPrompt: {
            sameSheetNameError: "This sheet already exists in which your chart is embedded. Please specify a different sheet name."
        }
    };

    cn_res.selectChartDialog = {
        title: "Insert Chart",
        insertChart: "Insert Chart",
        changeChartType: "Change Chart Type",
        defaultRowColumn: "Default Row Column Layout",
        switchedRowColumn: "Switched Row Column Layout",
        column: "Column Chart",
        columnClustered: "Clustered column chart",
        columnStacked: "Stacked Column Chart",
        columnStacked100: "Percent Stacked Column Chart",
        line: "Line chart",
        lineStacked: "Stacked Line Chart",
        lineStacked100: 'Percent Stacked Line Chart',
        lineMarkers: "Line chart with data markers",
        lineMarkersStacked: "Stacked Line Chart with Markers",
        lineMarkersStacked100: "Percent Stacked Line Chart with Data Markers",
        pie: "pie chart",
        doughnut: "Doughnut",
        bar: "Bar Chart",
        area: "Area Chart",
        XYScatter: "XY Scatter",
        stock: "Stock Chart",
        combo: "combination",
        barClustered: "Clustered Bar Chart",
        barStacked: "Stacked Bar Chart",
        barStacked100: "Percent Stacked Bar Chart",
        areaStacked: "Stacked Area Chart",
        areaStacked100: "Percent Stacked Area Chart",
        xyScatter: "Scatter",
        xyScatterSmooth: "Scatter chart with smooth lines and data markers",
        xyScatterSmoothNoMarkers: "Scatter with smooth lines",
        xyScatterLines: "Scatter plot with lines and data markers",
        xyScatterLinesNoMarkers: "Scatter with lines",
        bubble: "Bubble Chart",
        stockHLC: "High-Low-Close Chart",
        stockOHLC: "open-high-low-close chart",
        stockVHLC: "Volume-High-Low-Close Chart",
        stockVOHLC: "Volume - Open - High - Low - Close Chart",
        columnClusteredAndLine: "Clustered Column Chart - Line Chart",
        columnClusteredAndLineOnSecondaryAxis: "Clustered Column Chart - Line Chart on Secondary Axis",
        stackedAreaAndColumnClustered: "Stacked Area Chart - Clustered Column Chart",
        customCombination: "Custom Combination",
        seriesModifyDescription: "Select the icon type and axis for your data series:",
        seriesName: "Series Name",
        chartType: "Chart Type",
        secondaryAxis: "Secondary axis",
        errorPrompt: {
            stockHLCErrorMsg: "To create this stock chart, arrange the data in the worksheet in the following order: High - Low - Close. Use the date as a label.",
            stockOHLCErrorMsg: "To create this stock chart, arrange the data in the worksheet in the following order: Open - High - Low - Close. Use the date as a label.",
            stockVHLCErrorMsg: "To create this stock chart, arrange the data in the worksheet in the following order: Volume-High-Low-Close. Use the date as a label.",
            stockVOHLCErrorMsg: "To create this stock chart, arrange the data in the worksheet in the following order: Volume - Open - High - Low - Close. Use the date as a label.",
            emptyDataErrorMsg: "To create a chart, select the cells that contain the data you want to use. If the rows and columns have names and you want to use them as labels, include them in the selection.",
            unexpectedErrorMsg: "An unknown error occurred, please try again. If it still doesn't work, please contact our after-sales department."
        }
    };

    cn_res.columnWidthDialog = {
        title: "Column Width",
        columnWidth: "Column width:",
        exception: "Column width must be an integer or a decimal.",
        exception2: "Column width must be between 0 and 9999999."
    };
    cn_res.standardWidthDialog = {
        title: "Standard Column Width",
        columnWidth: "Standard column width:",
        exception: "Incorrect input. An integer or decimal is required."
    };
    cn_res.standardHeightDialog = {
        title: "Standard Row Height",
        rowHeight: "Standard row height:",
        exception: "Incorrect input. An integer or decimal is required."
    };
    cn_res.insertCellsDialog = {
        title: "Insert",
        shiftcellsright: "Shift the active cell right",
        shiftcellsdown: "move the active cell down",
        entirerow: "entire row",
        entirecolumn: "entire column"
    };
    cn_res.deleteCellsDialog = {
        title: "Delete",
        shiftcellsleft: "Shift cells on the right to the left",
        shiftcellsup: "Move the lower cell up",
        entirerow: "entire row",
        entirecolumn: "entire column"
    };
    cn_res.groupDialog = {
        title: "Create Group",
        rows: "rows",
        columns: "columns"
    };
    cn_res.ungroupDialog = {
        title: "Ungroup"
    };
    cn_res.findDialog = {
        title: "Find",
        findwhat: "Find what:",
        within: "Scope:",
        matchcase: "case sensitive",
        search: "Search:",
        matchexactly: "Cell match",
        lookin: "Look in:",
        usewildcards: "Use wildcards",
        option: "option",
        findall: "find all",
        findnext: "find next",
        exception: "Could not find what you were looking for."
    };
    cn_res.gotoDialog = {
        title: "Location",
        goto: "Location:",
        reference: "reference location:",
        exception: "The reference you entered is invalid or the name is a definition.",
        wrongName: "The operation failed to execute."
    };
    cn_res.nameManagerDialog = {
        title: "Name Manager",
        newName: "New...",
        edit: "Edit...",
        deleteName: "delete",
        filterWith: {
            title: "Filter:",
            clearFilter: "Clear Filter",
            nameScopedToWorkbook: "Name is expanded to workbook scope",
            nameScopedToWorksheet: "Name is expanded to worksheet scope",
            nameWithErrors: "There are wrong names",
            nameWithoutErrors: "No wrong names"
        },
        nameColumn: "Name",
        valueColumn: "Value",
        refersToColumn: "Reference Position",
        scopeColumn: "scope",
        commentColumn: "Comment",
        exception1: "The entered name is invalid.",
        exception2: "The entered name already exists. Please enter a unique name.",
        deleteAlert: "Are you sure you want to delete the name {0}?"
    };
    cn_res.newNameDialog = {
        titleNew: "New name",
        titleEdit: "Edit name",
        name: "Name:",
        scope: {
            title: "Scope:",
            workbook: "workbook"
        },
        referTo: "Reference location:",
        comment: "Comment:",
        wrongName: "The operation failed to execute."
    };
    /** modified by zhanghang@sunwayworld.com begin **/
    cn_res.setRepeatDialog = {
        titleNew: "Set Repeat Region",
        direction: {
            title: "Direction:",
            toRight: "Right",
            toDown: "Down"
        },
        blankCount: "Number of spaces",
        referTo: "Reference location:",
        wrongArea: "Failed to execute the operation",
        wrongBlankCount: "Please enter the number of intervals"
    };
    cn_res.autoMergeCellDialog = {
        titleNew: "Set Vertical Auto Merge",
        mergeCount: "Number of merged lines",
        wrongMergeCount: "Please enter the number of merged rows"
    };
    /** modified by zhanghang@sunwayworld.com end **/
    cn_res.insertFunctionDialog = {
        title: "Insert Function",
        functionCategory: "Function Category:",
        functionList: "Select function:",
        formula: "Formula:",
        functionCategorys: "All, Database, Date & Time, Engineering, Finance, Information, Logic, Lookup & Reference, Math & Trigonometry, Statistics, Text"
    };
    cn_res.buttonCellTypeDialog = {
        title: "Button",
        marginGroup: "Margin:",
        left: "left:",
        top: "Top:",
        right: "right:",
        bottom: "bottom:",
        text: "Text:",
        backcolor: "Background color:",
        other: "Other:"
    };
    cn_res.checkBoxCellTypeDialog = {
        title: "Checkbox",
        textGroup: "Text:",
        "true": "Selected:",
        indeterminate: "Indeterminate:",
        "false": "Not selected:",
        align: "align:",
        other: "Other:",
        caption: "Title:",
        isThreeState: "Three State",
        checkBoxTextAlign: {
            top: "top",
            bottom: "bottom",
            left: "Left",
            right: "to the right"
        }
    };
    cn_res.comboBoxCellTypeDialog = {
        title: "Combo Box",
        editorValueTypes: "Edit value types:",
        items: "Item:",
        itemProperties: "Item Properties:",
        text: "Text:",
        value: "value:",
        add: "Add",
        remove: "remove",
        editorValueType: {
            text: "text",
            index: "index",
            value: "value"
        },
        editable: "editable",
        itemHeight: "Item Height"
    };
    cn_res.hyperLinkCellTypeDialog = {
        title: "Hyperlink",
        link: "Link:",
        visitedlink: "Visited:",
        text: "Text:",
        linktooltip: "Screen Tip:",
        color: "color:",
        other: "Other:"
    };
    /** added by zhanghang@sunwayworld.com begin **/
    cn_res.swSignCellTypeDialog = {
        title: "Signature",
        username: "Username:",
        pwd: "password:"
    };
    cn_res.swGetDateTimeCellTypeDialog = {
        title: "Get system time",
        formatStr: "Format:"
    };
    cn_res.headerCellsDialog = {
        title: "Title cell",
        rowHeader: "row header",
        columnHeader: "Column header",
        backColor: "background color",
        borderBottom: "Bottom Border",
        borderLeft: "Left Border",
        borderRight: "Right border",
        borderTop: "Top border",
        diagonalUp: "diagonal up",
        diagonalDown: "Diagonal down",
        font: "font",
        foreColor: "Foreground Color",
        formatter: "format",
        hAlign: "Vertical Alignment",
        height: "height",
        locked: "locked",
        resizable: "resizable",
        shrinkToFit: "Shrink the font to fit",
        value: "value",
        textIndent: "Indent",
        vAlign: "Vertical Align",
        visible: "visible",
        width: "width",
        wordWrap: "Word Wrap",
        popUp: "...",
        merge: "merge cells",
        unmerge: "Unmerge cells",
        insertRows: "Insert Rows",
        addRows: "Add Rows",
        deleteRows: "Delete Rows",
        insertColumns: "Insert Columns",
        addColumns: "Add Columns",
        deleteColumns: "Delete Columns",
        clear: "clear",
        top: 'top',
        bottom: 'bottom',
        left: 'Left',
        right: 'to the right',
        center: 'Center',
        general: 'General',
        verticalText: "Vertical text",
        exception: "The setting is invalid. Please check the red area."
    };
    cn_res.fontPickerDialog = {
        title: "Font"
    };
    cn_res.fillDialog = {
        title: "Series"
    };

    cn_res.zoomDialog = {
        title: "Zoom",
        double: "200%",
        normal: "100%",
        threeFourths: "75%",
        half: "50%",
        quarter: "25%",
        fitSelection: "Adjust to fit",
        custom: "Custom:",
        exception: "Incorrect input. An integer or a decimal is required.",
        magnification: "magnification",
        percent: "%"
    };
    cn_res.contextMenu = {
        cut: "cut",
        copy: "copy",
        paste: "Paste Options:",
        pasteAll: 'Paste all',
        pasteFormula: 'Paste formula only',
        pasteValue: 'Paste value only',
        pasteFormatting: 'Paste formatting only',
        insertDialog: "Insert...",
        deleteDialog: "Delete...",
        clearcontents: "Clear Contents",
        filter: "Filter",
        sort: "sort",
        sortAToZ: "Ascending",
        sortZToA: "Descending",
        customSort: "Custom Sort...",
        formatCells: "Format cells...",
        defineName: "Define Name...",
        tag: "tag...",
        flowInfo: "Set float...",
        sunwayMenus: "Custom Menu...",
        printInfo: "Print...",
        rowHeight: "Row height...",
        columnWidth: "Column width...",
        hide: "hide",
        unhide: "unhide",
        headers: "Header...",
        insert: "insert",
        "delete": "Delete",
        protectsheet: "Protect sheet...",
        unprotectsheet: "Unprotect sheet...",
        comments: "The workbook contains at least one visible sheet.",
        insertComment: "Insert Comment",
        editComment: "Edit Comment",
        deleteComment: "Delete Comment",
        hideComment: "Hide Comment",
        editText: "Edit Text",
        exitEditText: "Exit text editing",
        formatComment: "Format Comment...",
        unHideComment: "Show/Hide Comment",
        sheetTabColor: "Sheet Tab Color",
        remove: "remove",
        slicerProperty: "Size and properties...",
        slicerSetting: "Slicer Settings...",
        changeChartType: "Change Chart Type...",
        selectData: "Select data...",
        moveChart: "Move the chart...",
        resetChartColor: "Reset to match style",
        formatChart: {
            chartArea: "Format the plot area...",
            series: "Format data series...",
            axis: "Set the axis format...",
            legend: "Format the legend...",
            dataLabels: "Format data labels...",
            chartTitle: "Format the chart title...",
        },
        setPrintRepeat: "Set print repeat...",
        removePrintRepeat: "Cancel print repeat...",
        showTime: "Show current time",
        exportPdfSetting: "Export PDF Settings",
        pdfPortrait: "Portrait",
        pdfLandscape: "Landscape",
        autoMergeCell: "Automatically merge vertically",
        cancelMergeCell: "Cancel Vertical Auto Merge",
        setDateTimeCellType: "Sets format to date and time",
        setDateCellType: "Set Format to date ",
        setTimeCellType: "Sets format to the time"
    };
    cn_res.tagDialog = {
        cellTagTitle: "Cell Tag",
        rowTagTitle: "row tag",
        columnTagTitle: "Column Tag",
        sheetTagTitle: "Form Tag",
        tag: "Tag:"
    };
    cn_res.flowInfoDialog = {
        title: "Set Float",
        editWinTitle: "Edit floating information",
        groupCellSetings: "Cell Settings:",
        sameValueSpan: "Same text merge:",
        sameValueSpanNone: "Do not merge",
        sameValueSpanUp: "Span Up",
        sameValueSpanLeft: "Merge Left",
        sameValueSpanUpLeft: "Bidirectional Merge",
        groupSheetSetings: "Floating settings on this page:",
        flowName: "name",
        flowType: "type",
        flowTypeRow: "Row float",
        flowTypeCol: "Column float",
        startCell: "Start Cell",
        startRow: "Start Row",
        startCol: "Start column",
        rowCount: "Number of rows",
        colCount: "Number of columns",
        bindingPathPrefix: "Binding data",
        dataKey: "Binding Field",
        refFlowInfo: "Associative Float",
        optType: "Operation",
        optTypeAdd: "Add",
        optTypeModify: "Edit",
        optTypeDel: "Delete"
    };
    cn_res.borderPicker = {
        lineStyleTitle: "Line:",
        borderColorTitle: "Color:",
        none: "none"
    };
    cn_res.borderDialog = {
        border: "border",
        presets: "Presets",
        none: "none",
        outline: "Outer Border",
        inside: "inside",
        line: "border",
        text: "text",
        comments: "Click the preset option to preview the sketch and the button above to add border styles."
    };

    cn_res.conditionalFormat = {
        highlightCellsRules: "Highlight Cells Rules",
        topBottomRules: "Item selection rules",
        dataBars: "Data Bars",
        colorScales: "color scales",
        iconSets: "Icon Sets",
        newRule: "New Rule...",
        clearRules: "Clear rules...",
        manageRules: "Manage Rules...",
        greaterThan: "greater than...",
        lessThan: "less than...",
        between: "Between...",
        equalTo: "Equal to...",
        textThatContains: "Text contains...",
        aDateOccurring: "The date of occurrence...",
        duplicateValues: "Duplicate Values...",
        moreRules: "Other rules...",
        top10Items: "Top 10 items with the largest value...",
        bottom10Items: "The 10 items with the smallest value...",
        aboveAverage: "Above Average...",
        belowAverage: "Below average...",
        gradientFill: "Gradient Fill",
        solidFill: "Solid Fill",
        directional: "Direction",
        shapes: "shape",
        indicators: "marker",
        ratings: "rank",
        clearRulesFromSelectedCells: "Clear the rules of selected cells",
        clearRulesFromEntireSheet: "Clear rules for entire sheet"
    };

    cn_res.fileMenu = {
        new: "New",
        open: "open",
        save: "Save",
        saveAs: "Save As",
        export: "export",
        import: "import",
        exit: "close",
        recentWorkbooks: "Recently used workbooks",
        computer: "computer",
        currentFolder: "Current Folder",
        recentFolders: "Recently used folders",
        browse: "Browse",
        spreadSheetJsonFile: "SpreadSheet file (JSON)",
        excelFile: "Excel file",
        csvFile: "CSV file",
        pdfFile: "PDF file",
        importSpreadSheetFile: "Import SSJSON file",
        importExcelFile: "Import Excel file",
        importCsvFile: "Import CSV file",
        exportSpreadSheetFile: "Export SSJSON file",
        exportExcelFile: "Export Excel File",
        exportCsvFile: "Export CSV file",
        exportPdfFile: "Export PDF file",
        exportJSFile: "Export Javascript file",
        openFlags: "Open Options",
        importIgnoreStyle: 'Import data only',
        importIgnoreFormula: 'Do not import formula',
        importDoNotRecalculateAfterLoad: "Do not automatically calculate formulas after importing",
        importRowAndColumnHeaders: "Import frozen columns and rows as headers",
        importRowHeaders: "Import frozen rows as column headers",
        importColumnHeaders: "Import frozen columns as row headers",
        importPassword: "Password",
        importIncludeRowHeader: "Import Row Header",
        importIncludeColumnHeader: "Import column header",
        importUnformatted: "Keep unformatted data",
        importImportFormula: "Import cell formula",
        importRowDelimiter: "Row Delimiter",
        importColumnDelimiter: "Column Delimiter",
        importCellDelimiter: "Cell Delimiter",
        importEncoding: "File encoding",
        saveFlags: "Save Options",
        exportIgnoreStyle: "Export data only",
        exportIgnoreFormulas: "Do not export formulas",
        exportAutoRowHeight: "Auto Row Height Adjustment",
        exportSaveAsFiltered: "Export as filter",
        exportSaveAsViewed: "Export as preview",
        exportSaveBothCustomRowAndColumnHeaders: "Export row headers and column headers as Excel's frozen columns and frozen rows",
        exportSaveCustomRowHeaders: "Export row headers as frozen columns in Excel",
        exportSaveCustomColumnHeaders: "Export column headers as frozen rows in Excel",
        exportPassword: "Password",
        exportExcel97_2003File: "Export Excel 97-2003 File",
        exportIncludeRowHeader: "Include row header",
        exportIncludeColumnHeader: "Include column header",
        exportUnFormatted: "Does not contain any style information",
        exportFormula: "Contain formula",
        exportAsViewed: "Export as preview",
        exportSheetIndex: "Sheet Index",
        exportEncoding: "encoding",
        exportRow: "row index",
        exportColumn: "Column Index",
        exportRowCount: "Number of rows",
        exportColumnCount: "Column count",
        exportRowDelimiter: "Row Delimiter",
        exportColumnDelimiter: "Column Delimiter",
        exportCellDelimiter: "Cell Delimiter",
        exportVisibleRowCol: "Only include visible rows and columns",
        pdfBasicSetting: "Basic Settings",
        pdfTitle: "Title:",
        pdfAuthor: "Author:",
        pdfApplication: "Application:",
        pdfSubject: "Subject:",
        pdfKeyWords: "Keywords:",
        pdfExportSetting: "Export Settings",
        exportSheetLabel: "Select sheet to export:",
        allSheet: "all",
        pdfDisplaySetting: "Display Settings",
        centerWindowLabel: "Center on window",
        showTitleLabel: "Show title",
        showToolBarLabel: "Show ToolBar",
        fitWindowLabel: "Fit Window",
        showMenuBarLabel: "Show Menu Bar",
        showWindowUILabel: "Show Window UI",
        destinationTypeLabel: "Destination Type:",
        destinationType: {
            autoDestination: "auto",
            fitPageDestination: "Page Fit",
            fitWidthDestination: "Width adaptation",
            fitHeightDestination: "Height adaptive",
            fitBoxDestination: "Box Fit"
        },
        openTypeLabel: "Open Type:",
        openType: {
            autoOpen: "auto",
            useNoneOpen: "Not used",
            useOutlinesOpen: "Use Outlines",
            useThumbsOpen: "Use Thumbs",
            fullScreenOpen: "full screen",
            useOCOpen: "Use OC",
            useAttachmentsOpen: "Use Attachments"
        },
        pdfPageSetting: "Page Setting",
        openPageNumberLabel: "Open page number:",
        pageLayoutLabel: "Page Layout:",
        pageLayout: {
            autoLayout: "auto",
            singlePageLayout: "Single Page",
            oneColumnLayout: "Single Column",
            twoColumnLeftLayout: "two columns on the left",
            twoColumnRightLayout: "Two columns on the right",
            twoPageLeftLayout: "Left two pages",
            twoPageRight: "Two pages to the right"
        },
        pageDurationLabel: "Page range:",
        pageTransitionLabel: "Page Transition:",
        pageTransition: {
            defaultTransition: "default",
            splitTransition: "split",
            blindsTransition: "Blinds",
            boxTransition: "box",
            wipeTransition: "open",
            dissolveTransition: "melt",
            glitterTransition: "glitter",
            flyTransition: "fly in",
            pushTransition: "Push",
            coverTransition: "cover",
            uncoverTransition: "uncover",
            fadeTransition: "Fade Out"
        },
        printerSetting: "Printer Settings...",
        printerSettingDialogTitle: "Printer Setting",
        copiesLabel: "Number of copies:",
        scalingTypeLabel: "Scaling Type:",
        scalingType: {
            appDefaultScaling: "App Default",
            noneScaling: "none"
        },
        duplexModeLabel: "Duplex Mode:",
        duplexMode: {
            defaultDuplex: "default",
            simplexDuplex: "simple",
            duplexFlipShortEdge: "Flip Short Edge",
            duplexFlipLongEdge: "Flip Long Edge"
        },
        choosePaperSource: "Choose paper source by pdf page size",
        printRanges: "Print Ranges",
        indexLabel: "index",
        countLabel: "Number",
        addRange: "Add",
        removeRange: "Remove",
        addRangeException: "The value is invalid, the index must be greater than or equal to 0, and the number must be greater than 0.",
        noRecentWorkbooks: "No workbooks have been opened recently. Please open a browse workbook first.",
        noRecentFolders: "No folders have been opened recently. Please open a browse workbook first."
    };

    cn_res.formatTableStyle = {
        name: "Name:",
        tableElement: "Table Element:",
        preview: "Preview",
        format: "format",
        tableStyle: "Table Style",
        clear: "clear",
        stripeSize: "Stripe Size",
        light: "light color",
        medium: "Medium light",
        dark: "dark",
        newTableStyle: "New table style...",
        custom: "custom",
        exception: "This style name already exists.",
        title: "SpreadJS Designer"
    };
    cn_res.tableElement = {
        wholeTableStyle: "Whole Table",
        firstColumnStripStyle: "First column stripe",
        secondColumnStripStyle: "Second Column Stripe",
        firstRowStripStyle: "First RowStripStyle",
        secondRowStripStyle: "Second RowStrip",
        highlightLastColumnStyle: "Last Column",
        highlightFirstColumnStyle: "First Column",
        headerRowStyle: "Header Row",
        footerRowStyle: "Summary row",
        firstHeaderCellStyle: "First header cell",
        lastHeaderCellStyle: "Last header cell",
        firstFooterCellStyle: "First summary cell",
        lastFooterCellStyle: "Last summary cell"
    };
    cn_res.conditionalFormatting = {
        common: {
            'with': "set to",
            selectedRangeWith: "For the selected area, set to",
            and: "and"
        },
        greaterThan: {
            title: "greater than",
            description: "Format cells greater than:"
        },
        lessThan: {
            title: "less than",
            description: "Format cells less than:"
        },
        between: {
            title: "Between",
            description: "Format cells between:"
        },
        equalTo: {
            title: "Equal to",
            description: "Format cells equal to:"
        },
        textThatCotains: {
            title: "Text contains",
            description: "Format cells containing the following text:"
        },
        dateOccurringFormat: {
            title: "Date of occurrence",
            description: "Format cells containing the following dates:",
            date: {
                yesterday: "yesterday",
                today: "today",
                tomorrow: "tomorrow",
                last7days: "Last 7 days",
                lastweek: "last week",
                thisweek: "this week",
                nextweek: "next week",
                lastmonth: "Last month",
                thismonth: "this month",
                nextmonth: "next month"
            }
        },
        duplicateValuesFormat: {
            title: "Duplicate Value",
            description: "Format cells containing values of the following types:",
            type: {
                duplicate: "duplicate",
                unique: "unique"
            },
            valueswith: "value"
        },
        top10items: {
            title: "Top 10",
            description: "Set cells for those cells with the largest value:"
        },
        bottom10items: {
            title: "Last 10 items",
            description: "Format those cells with the smallest value:"
        },
        aboveAverage: {
            title: "Above Average",
            description: "Format cells above average:"
        },
        belowAverage: {
            title: "Below Average",
            description: "Format cells below average:"
        },
        newFormattingRule: {
            title: "New Formatting Rule",
            title2: "Edit Format Rules",
            description1: "Select rule type:",
            description2: "Edit rule description:",
            ruleType: {
                formatOnValue: "►Format all cells based on their respective values",
                formatContain: "►Format only cells that contain the following content",
                formatRankedValue: "►Format only top or bottom ranked values",
                formatAbove: "►Format only values above or below average",
                formatUnique: "►Format only unique or duplicate values",
                useFormula: "►Use a formula to determine which cells to format"
            },
            formatOnValue: {
                description: "Format all cells based on their values:",
                formatStyle: "Format Style:",
                formatStyleSelector: {
                    color2: "Two-color scale",
                    color3: "Three-color scale",
                    dataBar: "Data Bar",
                    iconSets: "Icon Sets"
                },
                color2: {
                    min: "minimum value",
                    max: "Maximum value",
                    type: "Type:",
                    value: "value:",
                    color: "color:",
                    preview: "Preview",
                    minSelector: {
                        lowest: "lowest value"
                    },
                    maxSelector: {
                        highest: "highest value"
                    }
                },
                color3: {
                    mid: "middle value"
                },
                dataBar: {
                    showBarOnly: "Only show the data bar",
                    auto: "auto",
                    description2: "Bar Chart Appearance:",
                    fill: "fill",
                    color: "color",
                    border: "border",
                    fillSelector: {
                        solidFill: "Solid Fill",
                        gradientFill: "Gradient Fill"
                    },
                    borderSelector: {
                        noBorder: "No Border",
                        solidBorder: "Solid Border"
                    },
                    negativeBtn: "Negative value and axis...",
                    barDirection: "Bar direction:",
                    barDirectionSelector: {
                        l2r: "from left to right",
                        r2l: "right to left"
                    },
                    preview: "Preview",
                    negativeDialog: {
                        title: "Negative values and axis settings",
                        group1: {
                            title: "Negative Bar Chart Fill Color",
                            fillColor: "Fill Color:",
                            apply: "Apply the same fill color as the positive value bar"
                        },
                        group2: {
                            title: "Negative Bar Chart Border Color",
                            borderColor: "border color:",
                            apply: "Apply the same border color as the positive value bar"
                        },
                        group3: {
                            title: "Axis Settings",
                            description: "Select the cell's axis position to change the appearance of negative bars",
                            radio: {
                                auto: "Auto (display in variable position based on negative value)",
                                cell: "Cell midpoint value",
                                none: "None (display negative bars in the same direction as positive bars)"
                            },
                            axisColor: "Axis Color:"
                        }
                    }
                },
                iconSets: {
                    iconStyle: "Icon Style:",
                    showIconOnly: "Show icon only",
                    reverseIconOrder: "Reverse Icon Order",
                    display: "Display each icon according to the following rules:",
                    icon: "icon",
                    value: "value",
                    type: "type",
                    description1: "When the value is",
                    description2: "When < ",
                    operator: {
                        largeOrEqu: "> =",
                        large: ">"
                    },
                    customIconSet: "custom",
                    noCellIcon: "No cell icon"
                },
                commonSelector: {
                    num: "Number",
                    percent: "percent",
                    formula: "formula",
                    percentile: "Percentile"
                }
            },
            formatContain: {
                description: "Only format cells that meet the following conditions:",
                type: {
                    cellValue: "Cell Value",
                    specificText: "specific text",
                    dateOccurring: "Occurring date",
                    blanks: "null value",
                    noBlanks: "No Blanks",
                    errors: "error",
                    noErrors: "No errors"
                },
                operator_cellValue: {
                    between: "between",
                    notBetween: "Not Between",
                    equalTo: "equal to",
                    notEqualTo: "Not equal to",
                    greaterThan: "greater than",
                    lessThan: "less than",
                    greaterThanOrEqu: "greater than or equal to",
                    lessThanOrEqu: "Less than or equal to"
                },
                operator_specificText: {
                    containing: "contains",
                    notContaining: "Not Containing",
                    beginningWith: "begins with",
                    endingWith: "ends at"
                }
            },
            formatRankedValue: {
                description: "Format the values within the following ranks:",
                type: {
                    top: "top",
                    bottom: "back"
                }
            },
            formatAbove: {
                description: "Format a value that meets the following conditions:",
                type: {
                    above: "above",
                    below: "below",
                    equalOrAbove: "Equal to or greater than",
                    equalOrBelow: "Equal to or lower than",
                    std1Above: "Standard deviation above 1",
                    std1Below: "Standard deviation below 1",
                    std2Above: "Standard deviation above 2",
                    std2Below: "Standard deviation below 2",
                    std3Above: "Standard deviation above 3",
                    std3Below: "standard deviation below 3"
                },
                description2: "Average of selected range"
            },
            formatUnique: {
                description: "All format:",
                type: {
                    duplicate: "duplicate",
                    unique: "unique"
                },
                description2: "Numbers in the selected range"
            },
            useFormula: {
                description: "Format values matching this formula:"
            },
            preview: {
                description: "Preview:",
                buttonText: "Format...",
                noFormat: "No format is set",
                hasFormat: "AaBbCcYyZz"
            }
        },
        withStyle: {
            lightRedFill_DarkRedText: "Light RedFill Dark Red Text",
            yellowFill_DrakYellowText: "Yellow Fill Dark Yellow Text",
            greenFill_DarkGreenText: "GreenFill Dark Green Text",
            lightRedFill: "Light red fill",
            redText: "red text",
            redBorder: "red border",
            customFormat: "Custom format..."
        },
        exceptions: {
            e1: "The input value is not a valid number, date, time or string.",
            e2: "Enter a value.",
            e3: "Enter an integer between 1 and 1000.",
            e4: "The input value cannot be empty.",
            e5: "You cannot directly reference a worksheet range in a conditional formatting formula.\nPlease change the reference to a single cell reference, or use a worksheet function such as =SUM(A1:E5).",
            e6: "The source range of a formula rule can only be a single range!"
        }
    };

    cn_res.formattingRulesManagerDialog = {
        title: "Conditional Formatting Rules Manager",
        rulesScopeLabel: "Show its format rules: ",
        rulesScopeForSelection: "Current Selection",
        rulesScopeForWorksheet: "Current Worksheet",
        newRule: "New Rule...",
        editRule: "Edit Rule...",
        deleteRule: "Delete rule...",
        gridTitleRule: "Rules (applied in the order shown)",
        gridTitleFormat: "Format",
        gridTitleAppliesTo: "Applies to",
        gridTitleStopIfTrue: "Stop if true",
        ruleDescriptions: {
            valueBetween: 'The cell value is between {0} and {1}',
            valueNotBetween: 'The cell value is not between {0} and {1}',
            valueEquals: 'Cell value = {0}',
            valueNotEquals: 'Cell value <> {0}',
            valueGreateThan: 'Cell value > {0}',
            valueGreateThanOrEquals: 'Cell value>= {0}',
            valueLessThan: 'Cell value < {0}',
            valueLessThanOrEquals: 'Cell value <= {0}',
            valueContains: 'The cell value contains "{0}"',
            valueNotContains: 'The cell value does not contain "{0}"',
            valueBeginsWith: 'The cell value starts with "{0}"',
            valueEndsWith: 'The end of the cell value is "{0}"',
            last7Days: 'Last 7 days',
            lastMonth: 'Last month',
            lastWeek: 'last week',
            nextMonth: 'Next month',
            nextWeek: 'Next week N',
            thisMonth: 'this month',
            thisWeek: 'this week',
            today: 'today',
            tomorrow: 'tomorrow',
            yesterday: 'yesterday',
            duplicateValues: 'Duplicate Values',
            uniqueValues: 'Unique Values',
            top: 'Top {0}',
            bottom: 'The next {0}',
            above: 'above average',
            above1StdDev: 'Standard deviation is 1 above mean',
            above2StdDev: 'Standard deviation is 2 above mean',
            above3StdDev: 'The standard deviation is 3 above the mean',
            below: 'below average',
            below1StdDev: 'Standard Deviation Below Mean 1',
            below2StdDev: 'Standard deviation below mean 2',
            below3StdDev: 'Standard deviation is 3 below mean',
            equalOrAbove: 'Equal to or above average',
            equalOrBelow: 'Equal to or below average',
            dataBar: 'Data bar',
            twoScale: 'gradient color scale',
            threeScale: 'Gradient color scale',
            iconSet: 'icon set',
            formula: 'Formula: {0}'
        },
        previewText: 'AaBbCcYyZz'
    };

    cn_res.cellStylesDialog = {
        cellStyles: "Cell Styles",
        custom: "custom",
        goodBadAndNeutral: "Good, Bad and Neutral",
        dataAndModel: "Data and Model",
        titlesAndHeadings: "Titles",
        themedCellStyle: "Themed Cell Style",
        numberFormat: "Number Format",
        goodBadAndNeutralContent: {
            "Normal": "Normal",
            "Bad": "Bad",
            "Good": "Good",
            "Neutral": "Moderate"
        },
        dataAndModelContent: {
            "Calculation": "Calculation",
            "Check Cell": "Check Cell",
            "Explanatory Text": "Explanatory Text...",
            "Input": "Input",
            "Linked Cell": "Linked Cell ",
            "Note": "Note",
            "Output": "Output",
            "Warning Text": "Warning Text"
        },
        titlesAndHeadingsContent: {
            "Heading 1": "Heading 1",
            "Heading 2": "Heading 2",
            "Heading 3": "Heading 3",
            "Heading 4": "Heading 4",
            "Title": "Title",
            "Total": "Total"
        },
        themedCellStyleContent: {
            "20% - Accent1": "20% - Accent Text Color 1",
            "20% - Accent2": "20% - Accent Text Color 2",
            "20% - Accent3": "20% - Accent Text Color 3",
            "20% - Accent4": "20% - Accent Text Color 4",
            "20% - Accent5": "20% - Accent text color 5",
            "20% - Accent6": "20% - Accent text color 6",
            "40% - Accent1": "40% - Accent Text Color 1",
            "40% - Accent2": "40% - Accent Text Color 2",
            "40% - Accent3": "40% - Accent Text Color 3",
            "40% - Accent4": "40% - Accent Text Color 4",
            "40% - Accent5": "40% - Accent text color 5",
            "40% - Accent6": "40% - Accent text color 6",
            "60% - Accent1": "60% - Accent Text Color 1",
            "60% - Accent2": "60% - Accent Text Color 2",
            "60% - Accent3": "60% - Accent Text Color 3",
            "60% - Accent4": "60% - Accent Text Color 4",
            "60% - Accent5": "60% - Accent text color 5",
            "60% - Accent6": "60% - Accent text color 6",
            "Accent1": "Accent text color 1",
            "Accent2": "Accent Text Color 2",
            "Accent3": "Accent text color 3",
            "Accent4": "Accent text color 4",
            "Accent5": "Accent text color 5",
            "Accent6": "Accent text color 6"
        },
        numberFormatContent: {
            "Comma": "Thousands separated",
            "Comma[0]": "Thousands Separator[0]",
            "Currency": "Currency",
            "Currency[0]": "Currency[0]",
            "Percent": "Percent"
        },
        newCellStyle: "New cell style..."
    };

    cn_res.newCellStyleDialog = {
        style: "style",
        styleName: "Style Name:",
        defaultStyleName: "Style 1",
        format: "Format...",
        message: "This style name already exists."
    };

    cn_res.cellStyleContextMenu = {
        "delete": "Delete",
        modify: "modify"
    };

    cn_res.insertPictureDialogTitle = "Insert Picture";
    cn_res.pictureFormatFilter = {
        jpeg: "JPEG File Interchange Format (*.jpg;*.jpeg)",
        png: "Portable Network Graphics (*.png)",
        bmp: "Windows Bitmap (*.bmp)",
        allFiles: "All files (*.*)"
    };

    cn_res.ribbon = {
        accessBar: {
            undo: "undo",
            redo: "restore",
            save: "Save",
            New: "New",
            open: "open",
            active: "Activate"
        },
        home: {
            file: "file",
            home: "start",
            clipboard: "clipboard",
            fonts: "font",
            alignment: "Alignment",
            numbers: "numbers",
            cellType: "Cell Type",
            styles: "style",
            cells: "cell",
            editing: "edit",
            paste: "paste",
            all: "all",
            formulas: "formulas",
            values: "value",
            formatting: "format",
            cut: "cut",
            copy: "copy",
            fontFamily: "font",
            fontSize: "font size",
            increaseFontSize: "Increase font size",
            decreaseFontSize: "Decrease font size",
            bold: "bold",
            italic: "tilt",
            underline: "underline",
            doubleUnderline: "Double Underline",
            border: "border",
            bottomBorder: "Bottom Border",
            topBorder: "Top Border",
            leftBorder: "Left Border",
            rightBorder: "Right Border",
            noBorder: "No border",
            allBorder: "All borders",
            outsideBorder: "Outside Border",
            thickBoxBorder: "thick box border",
            bottomDoubleBorder: "Double Bottom Border",
            thickBottomBorder: "Thick Bottom Border",
            topBottomBorder: "Top and bottom border",
            topThickBottomBorder: "Top border and thick bottom border",
            topDoubleBottomBorder: "Top border and double bottom border",
            moreBorders: "Other borders...",
            backColor: "fill color",
            fontColor: "font color",
            topAlign: "Top Align",
            middleAlign: "Vertical Center",
            bottomAlign: "Bottom Align",
            leftAlign: "Text Left Align",
            centerAlign: "Center",
            rightAlign: "Text Right Align",
            increaseIndent: "Increase indent",
            decreaseIndent: "Decrease Indent",
            wrapText: "Wrap text",
            mergeCenter: "Center after merging",
            mergeAcross: "Merge Across",
            mergeCells: "Merge Cells",
            unMergeCells: "Unmerge cells",
            numberFormat: "Number Format",
            general: "General",
            Number: "Number",
            currency: "currency",
            accounting: "Accounting only",
            shortDate: "Short Date",
            longDate: "Long Date",
            time: "time",
            percentage: "percentage",
            fraction: "fraction",
            scientific: "scientific notation",
            text: "text",
            moreNumberFormat: "Other number formats...",
            percentStyle: "Percent style",
            commaStyle: "Thousands Separation Style",
            increaseDecimal: "Increase decimal places",
            decreaseDecimal: "Reduce decimal places",
            buttonCellType: "Button Cell Type",
            checkboxCellType: "Checkbox Cell Type",
            comboBoxCellType: "Combo Box Cell Type",
            hyperlinkCellType: "Hyperlink Cell Type",
            swSignCellType: "Signature Cell Type",
            swAutoSignCellType: "AutoSign Cell Type",
            swGetDateTimeCellType: "Get system time cell type",
            clearCellType: "Clear<br>Cell<br>Type",
            clearCellType1: "Clear cell type",
            conditionFormat: "Conditional Format",
            conditionFormat1: "Conditional Format",
            formatTable: "Apply <br>table style",
            formatTable1: "Apply table style",
            insert: "insert",
            insertCells: "Insert cells...",
            insertSheetRows: "Insert Sheet Rows",
            insertSheetColumns: "Insert Sheet Columns",
            insertSheet: "Insert Sheet",
            Delete: "Delete",
            deleteCells: "Delete cells...",
            deleteSheetRows: "Delete Sheet Rows",
            deleteSheetColumns: "Delete Sheet Columns",
            deleteSheet: "Delete Sheet",
            format: "format",
            rowHeight: "Row height...",
            autofitRowHeight: "Autofit RowHeight",
            defaultHeight: "Default row height...",
            columnWidth: "Column width...",
            autofitColumnWidth: "Autofit column width",
            defaultWidth: "Default column width...",
            hideRows: "Hide Rows",
            hideColumns: "Hide Columns",
            unHideRows: "Unhide Rows",
            unHideColumns: "Unhide columns",
            protectSheet: "Protect Sheet...",
            unProtectSheet: "Unprotect sheet...",
            lockCells: "Lock Cells",
            unLockCells: "Unlock Cells",
            autoSum: "AutoSum",
            sum: "Sum",
            average: "average",
            countNumbers: "count",
            max: "Maximum value",
            min: "minimum value",
            fill: "fill",
            down: "down",
            right: "right",
            up: "up",
            left: "left",
            series: "Series...",
            clear: "clear",
            clearAll: "Clear all",
            clearFormat: "Clear Format",
            clearContent: "Clear Content",
            clearComments: "Clear Comments",
            sortFilter: "Sort and <br>Filter",
            sortFilter1: "Sort and filter",
            sortAtoZ: "Ascending",
            sortZtoA: "Descending",
            customSort: "Custom Sort...",
            filter: "Filter",
            clearFilter: "Clear Filter",
            reapply: "reapply",
            find: "find",
            find1: "Find...",
            goto: "Go to...",
            rotateText: "Orientation",
            verticalText: "Vertical text",
            specialCharacter: "Special characters"
        },
        insert: {
            insert: "insert",
            table: "table",
            chart: "Chart",
            sparklines: "Sparklines",
            line: "Line chart",
            column: "Column Chart",
            winloss: "Profit and Loss Chart",
            insertTable: "Insert Table",
            insertChart: "Insert Chart",
            insertPicture: "Insert Picture",
            insertLineSparkline: "Insert Line Chart",
            insertColumnSparkline: "Insert Column Chart",
            insertWinlossSparkline: "Insert Profit and Loss Chart",
            picture: "picture",
            illustrations: "illustration",
            insertPieSparkline: "Insert Pie Chart",
            insertAreaSparkline: "Insert Area Chart",
            insertScatterSparkline: "Insert Scatter",
            pie: "pie chart",
            area: "Area Chart",
            scatter: "scatterplot",
            insertBulletSparkline: "Insert bullet graph",
            bullet: "Bullet Graph",
            insertSpreadSparkline: "Insert Spread",
            spread: "scatterplot",
            insertStackedSparkline: "Insert Stacked Chart",
            stacked: "stacked graph",
            insertHbarSparkline: "Insert Horizontal Bar Chart",
            hbar: "Horizontal<br>Bar",
            insertVbarSparkline: "Insert Vertical Bar Chart",
            vbar: "Vertical<br>Bar",
            insertVariSparkline: "Insert variance plot",
            variance: "variance plot",
            insertBoxPlotSparkline: "Insert boxplot",
            boxplot: "boxplot",
            insertCascadeSparkline: "Insert composition waterfall",
            cascade: "compose <br>waterfall chart",
            insertParetoSparkline: "Insert ladder waterfall chart",
            pareto: "Staircase<br>Waterfall Chart",
            insertMonthSparkline: "Insert Monthly Chart",
            month: "month",
            insertYearSparkline: "Insert Year Chart",
            year: "year"
        },
        formulas: {
            formulas: "formulas",
            insertFunction: "Insert <br>Function",
            insertFunction1: "Insert Function",
            functions: "function library",
            names: "Defined names",
            nameManager: "Name<br>Manager",
            nameManager1: "NameManager"
        },
        elnoperations: {
            elnoperations: "ELN",
            binddatasource: "Bind<br>Datasource",
            binddatasource_title: "Bind Datasource",
            binddatasources: "Datasources",
            saveleave: "Save and <br>Exit",
            saveleave_title: "Save and Exit",
            auxiliary: "auxiliary function",
            numpad: "floating numeric keypad",
            numpad_title: "Floating Numeric Keyboard",
            saveserver: "Save",
            saveserver_title: "Save",
            saveservers: "Save",
            supcornermark: "supercorner mark",
            subcornermark: "subcornermark",
            modifyrecord: "Audit<br>trace",
            modifyrecord_title: "Audit Trail",
            template: "Data Source",
            elnDatasourceShow: "Data source binding",
            uploadpdf: "Upload<br>PDF",
            uploadpdf_title: "Upload PDF",
            initeln: "Initialize <br>template",
            initeln_title: "Initialization Template",
            showprintline: "Show print line",
            showprintline_title: "Show print line",
            previewdata: "preview",
            previewdata_title: "Preview"
        },
        /** added by zhanghang@sunwayworld.com end * */
        data: {
            data: "data",
            sortFilter: "Sort and filter",
            dataTools: "Data Tools",
            outline: "Outline",
            sortAtoZ: "Ascending",
            sortZtoA: "Descending",
            sort: "sort",
            customSort: "Sort...",
            filter: "Filter",
            clear: "clear",
            clearFilter: "Clear Filter",
            reapply: "reapply",
            dataValidation: "Data Validation",
            dataValidation1: "Data Validation",
            circleInvalidData: "Circle invalid data",
            clearInvalidCircles: "Clear invalid data circles",
            group: "Create group",
            unGroup: "Ungroup",
            showDetail: "Show detailed data",
            hideDetail: "Hide detail data",
            designMode: "Design Mode",
            enterTemplate: "Enter Design Mode Template",
            template: "template",
            bindingPath: "Binding Path",
            loadSchemaTitle: "Load template structure to get tree diagram",
            loadSchema: "Load Template Schema",
            loadDataSourceFilter: {
                json: "JSON file (*.json)",
                txt: "Text file (*.txt)"
            },
            saveDataSourceFilter: {
                json: "JSON file (*.json)"
            },
            saveSchemaTitle: "Save template structure information to json file",
            saveSchema: "Save template structure",
            autoGenerateColumns: "Auto Generate Columns",
            columns: "columns",
            name: "name",
            details: "detail data",
            ok: "OK",
            cancel: "Cancel",
            loadDataError: "Please check whether there is data with empty field information in the data source definition, if there is any data, please delete it.",
            initError: "Error initializing Spreadjs, please configure License or change the local Host alias to a single word.",
            addNode: "Add Node",
            remove: "remove",
            rename: "rename",
            table: "table",
            selectOptions: "Select Options",
            clearBindingPath: "Clear binding path",
            dataField: "Data Field",
            warningTable: "The number of columns in the table will change. Do you want to continue?",
            warningDataField: "Should auto-generate columns be set to no and data fields set anyway?",
            checkbox: "checkbox",
            hyperlink: "hyperlink",
            combox: "Combo Box",
            button: "button",
            text: "text",
            autoGenerateLabel: "Auto Generate Label",
            autoGenerateLabelTip: "Auto Generate Data Labels",
            unallowableTableBindingTip: "Data fields can only be set on tables, please select a table.",
            overwriteCellTypeWarning: "Some cell types on the worksheet will be overwritten or modified. Do you want to continue?",
            removeNodeWarning: "The deleted node contains child nodes, do you want to delete it?",
            unallowComboxBindingTip: "The collection of combo boxes can only be set in a combo box, please select a combo box."
        },
        view: {
            view: "view",
            showHide: "Show/Hide",
            zoom: "display scale",
            viewport: "window",
            rowHeader: "row header",
            columnHeader: "Column header",
            verticalGridline: "Vertical Gridline",
            horizontalGridline: "Horizontal gridline",
            tabStrip: "Sheet Tab",
            newTab: "New Worksheet",
            rowHeaderTip: "Show/Hide Row Header",
            columnHeaderTip: "Show/Hide Column Header",
            verticalGridlineTip: "Show/hide vertical gridlines",
            horizontalGridlineTip: "Show/Hide Horizontal Gridlines",
            tabStripTip: "Show/Hide Sheet Tabs",
            newTabTip: "Show/Hide New Sheet",
            zoomToSelection: "Zoom to Selection",
            zoomToSelection1: "Zoom to Selection",
            freezePane: "Freeze Pane",
            freezePane1: "Freeze Pane",
            freezeTopRow: "Freeze the first row",
            freezeFirstColumn: "Freeze first column",
            freezeBottomRow: "Freeze Bottom Row",
            freezeLastColumn: "Freeze the last column",
            unFreezePane: "Unfreeze the pane",
            unFreezePane1: "Unfreeze Pane"
        },
        setting: {
            setting: "Settings",
            spreadSetting: "Spread Setting",
            sheetSetting: "Sheet Settings",
            general: "General",
            generalTip: "General Settings",
            scrollBars: "scroll bar",
            tabStrip: "tab",
            gridLines: "Gridlines",
            calculation: "Calculation",
            headers: "Headers"
        },
        sparkLineDesign: {
            design: "design",
            type: "type",
            show: "show",
            style: "style",
            groups: "grouping",
            line: "Line",
            column: "Column Chart",
            winLoss: "Profit and Loss",
            lineTip: "Line Chart",
            columnTip: "Column Chart",
            winLossTip: "P&L Sparkline",
            highPoint: "High Point",
            lowPoint: "low point",
            negativePoint: "negative point",
            firstPoint: "First Point",
            lastPoint: "End Point",
            markers: "markers",
            highPointTip: "Toggle Sparkline High Point",
            lowPointTip: "Toggle Sparkline Low Point",
            negativePointTip: "Toggle Sparkline Negative Point",
            firstPointTip: "Toggle sparkline first point",
            lastPointTip: "Toggle Sparkline End Point",
            markersTip: "Toggle Sparkline Markers",
            sparklineColor: "Sparkline Color",
            markerColor: "marker color",
            sparklineWeight: "Sparkline Weight",
            customWeight: "Custom Weight...",
            group: "group",
            groupTip: "Group selected sparklines",
            unGroupTip: "Ungroup selected sparklines",
            unGroup: "Ungroup",
            clear: "clear",
            clearSparkline: "Clear Selected Sparklines",
            clearSparklineGroup: "Clear the selected sparkline group"
        },
        formulaSparklineDesign: {
            design: "design",
            argument: "parameter",
            settings: "Settings"
        },
        tableDesign: {
            design: "design",
            tableName: "table name",
            resizeTable: "Resize table",
            tableOption: "Table style option",
            property: "property",
            headerRow: "Header Row",
            totalRow: "Total Row",
            bandedRows: "Banded Rows",
            firstColumn: "First column",
            lastColumn: "Last column",
            bandedColumns: "Banded Columns",
            filterButton: "Filter Button",
            tableStyle: "Table Style",
            style: "style",
            tools: "tools",
            insertSlicer: "Insert Slicer"
        },
        chartDesign: {
            design: "design",
            chartLayouts: "Chart Layouts",
            addChartElement: "Add a chart <br> element",
            addChartElement1: "Add Chart Element",
            quickLayout: "Quick Layout",
            changeColors: "Change Colors",
            axes: "axis",
            chartStyles: "Chart Styles",
            switchRowColumn: "Switch row/column",
            selectData: "Select Data",
            data: "data",
            changeChartType: "Change Chart<br>Type",
            changeChartType1: "Change Chart Type",
            type: "type",
            moveChart: "Move Chart",
            location: "Location",
            chartTemplate: "Chart Template"
        },
        fontFamilies: {
            cnff1: {
                name: "Arial",
                text: "Arial"
            },
            cnff2: {
                name: "Kaiti",
                text: "Kaiti"
            },
            cnff3: {
                name: "Fake Song",
                text: "Fake Song"
            },
            cnff4: {
                name: "bold",
                text: "bold"
            },
            cnff5: {
                name: "New Song Ti",
                text: "New Song Ti"
            },
            cnff6: {
                name: "SimSun",
                text: "SimSun"
            },
            cnff7: {
                name: "KaiTi",
                text: "KaiTi"
            },
            cnff8: {
                name: "FangSong",
                text: "FangSong"
            },
            cnff9: {
                name: "SimHei",
                text: "SimHei"
            },
            cnff10: {
                name: "NSimSun",
                text: "NSimSun"
            },
            ff1: {
                name: "Arial",
                text: "Arial"
            },
            ff2: {
                name: "Arial Black",
                text: "Arial Black"
            },
            ff3: {
                name: "Calibri",
                text: "Calibri"
            },
            ff4: {
                name: "Cambria",
                text: "Cambria"
            },
            ff5: {
                name: "Candara",
                text: "Candara"
            },
            ff6: {
                name: "Century",
                text: "Century"
            },
            ff7: {
                name: "Courier New",
                text: "Courier New"
            },
            ff8: {
                name: "Comic Sans MS",
                text: "Comic Sans MS"
            },
            ff9: {
                name: "Garamond",
                text: "Garamond"
            },
            ff10: {
                name: "Georgia",
                text: "Georgia"
            },
            ff11: {
                name: "Malgun Gothic",
                text: "Malgun Gothic"
            },
            ff12: {
                name: "Mangal",
                text: "Mangal"
            },
            ff13: {
                name: "Meiryo",
                text: "Meiryo"
            },
            ff14: {
                name: "MS Gothic",
                text: "MS Gothic"
            },
            ff15: {
                name: "MS Mincho",
                text: "MS Mincho"
            },
            ff16: {
                name: "MS PGothic",
                text: "MS PGothic"
            },
            ff17: {
                name: "MS PMincho",
                text: "MS PMincho"
            },
            ff18: {
                name: "Tahoma",
                text: "Tahoma"
            },
            ff19: {
                name: "Times",
                text: "Times"
            },
            ff20: {
                name: "Times New Roman",
                text: "Times New Roman"
            },
            ff21: {
                name: "Trebuchet MS",
                text: "Trebuchet MS"
            },
            ff22: {
                name: "Verdana",
                text: "Verdana"
            },
            ff23: {
                name: "Wingdings",
                text: "Wingdings"
            }
        },
        slicerOptions: {
            options: "options",
            slicerCaptionShow: "Slicer Caption:",
            slicerCaption: "Slicer Caption",
            slicerSettings: "Slicer Settings",
            slicer: "slicer",
            styles: "style",
            slicerStyles: "Slicer Styles",
            columnsShow: "Column:",
            heightShow: "Height:",
            widthShow: "width:",
            columns: "columns",
            height: "height",
            width: "width",
            buttons: "button",
            size: "size",
            shapeHeight: "shape height",
            shapeWidth: "shape width"
        }
    };

    cn_res.seriesDialog = {
        series: "sequence",
        seriesIn: "series generated in",
        rows: "rows",
        columns: "columns",
        type: "type",
        linear: "Algorithmic sequence",
        growth: "equivalent sequence",
        date: "date",
        autoFill: "AutoFill",
        dateUnit: "date unit",
        day: "Day",
        weekday: "weekday",
        month: "month",
        year: "year",
        trend: "forecast trend",
        stepValue: "step value",
        stopValue: "stop value"
    };

    cn_res.customSortDialog = {
        sort: "sort",
        addLevel: "Add condition",
        deleteLevel: "Delete Condition",
        copyLevel: "Copy Condition",
        options: "Options...",
        sortBy: "column",
        sortBy2: "Primary Keyword",
        thenBy: "Secondary Keyword",
        sortOn: "Sort by",
        sortOrder: "Order",
        sortOptions: "Sort Options",
        orientation: "orientation",
        sortTopToBottom: "Sort by Column",
        sortLeftToRight: "Sort by row",
        column: "column",
        row: "row",
        values: "value",
        ascending: "Ascending",
        descending: "Descending"
    };

    cn_res.createTableDialog = {
        createTable: "Create Table",
        whereYourTable: "Source of table data"
    };

    cn_res.createSparklineDialog = {
        createSparkline: "Create Chart",
        dataRange: "Chart data area",
        locationRange: "Chart location area",
        chooseData: "Choose Data Source",
        chooseWhere: "Choose where to place the chart",
        warningText: "The location is invalid because the cells are not in the same column or row. Please select a cell in the same column or the same row.",
        notSingleCell: "Invalid reference range, please select a single cell reference"
    };

    cn_res.dataValidationDialog = {
        dataValidation: "Data Validation",
        settings: "Settings",
        validationCriteria: "Validity Criteria",
        allow: "Allow",
        anyValue: "any value",
        wholeNumber: "Integer",
        decimal: "decimal",
        list: "sequence",
        date: "date",
        textLength: "Text Length",
        custom: "custom",
        data: "data",
        dataLabel: "Data:",
        between: "between",
        notBetween: "Not Between",
        equalTo: "equal to",
        notEqualTo: "Not equal to",
        greaterThan: "greater than",
        lessThan: "less than",
        greaterEqual: "greater than or equal to",
        lessEqual: "less than or equal to",
        minimum: "Minimum:",
        maximum: "Maximum value:",
        value: "Value:",
        startDate: "Start Date:",
        endDate: "End Date:",
        dateLabel: "Date:",
        length: "Length:",
        source: "Source:",
        formula: "Formula:",
        ignoreBlank: "Ignore empty values",
        inCellDropDown: "Provide drop down arrow",
        inputMessage: "input message",
        errorAlert: "Error warning",
        showMessage: "Show input message when cell is selected",
        showMessage2: "Show the following input message when a cell is selected: ",
        title: "title",
        showError: "Show error warning when entering invalid data",
        showError2: "Show the following error warning when entering invalid data:",
        style: "Style:",
        stop: "stop",
        warning: "warning",
        information: "information",
        errorMessage: "Error message",
        clearAll: "Clear all",
        valueEmptyMessage: "A value must be entered.",
        minimumMaximumMessage: "The maximum value must be greater than or equal to the minimum value.",
        errorMessage1: "The input value is invalid.\r\nThe user has restricted the input value of this cell.",
        errorMessage2: "The input value is invalid.\r\nThe user has restricted the input value of the secondary cell.\r\nDo you want to continue?"
    };

    cn_res.spreadSettingDialog = {
        spreadSetting: "Spread Setting",
        general: "General",
        settings: "Settings",
        allowDragMerge: "Allow drag and drop to merge cells",
        allowDragDrop: "Allow drag and drop",
        allowFormula: "Allow users to enter formulas",
        allowDragFill: "Run drag and fill",
        allowZoom: "Allow Zoom",
        allowUndo: "Allow Undo",
        allowOverflow: "Allow overflow",
        scrollBars: "scroll bar",
        visibility: "Visibility",
        verticalScrollBar: "Vertical scroll bar",
        horizontalScrollBar: "Horizontal scroll bar",
        scrollbarShowMax: "Maximum scroll bar display",
        scrollbarMaxAlign: "Scroll bar maximum alignment",
        tabStrip: "Sheet Tab",
        tabStripVisible: "Sheet tabs are visible",
        tabStripEditable: "Worksheet tabs are editable",
        newTabVisible: "New Tab Visible",
        tabStripRatio: "Worksheet tab ratio (percentage)",
        clipboard: "clipboard",
        allowCopyPasteExcelStyle: "Allow copy paste Excel style",
        allowExtendPasteRange: "Allow extended paste range",
        headerOptions: "Copy and paste row and column header options",
        noHeaders: "Does not include row and column headers",
        rowHeaders: "Include row headers",
        columnHeaders: "Include column headers",
        allHeaders: "Include line headers and column headers"
    };

    cn_res.sheetSettingDialog = {
        sheetSetting: "Sheet Settings",
        general: "General",
        columnCount: "Column count",
        rowCount: "Number of rows",
        frozenColumnCount: "Number of frozen columns",
        frozenRowCount: "Number of frozen rows",
        trailingFrozenColumnCount: "Number of last frozen columns",
        trailingFrozenRowCount: "Number of last frozen rows",
        selectionPolicy: "Selection Policy",
        singleSelection: "Single Selection",
        rangeSelection: "Range Selection",
        multiRangeSelection: "Multi-Range Selection",
        protect: "protect",
        gridlines: "gridlines",
        horizontalGridline: "Horizontal gridline",
        verticalGridline: "Vertical Gridline",
        gridlineColor: "Gridline Color",
        calculation: "Calculation",
        referenceStyle: "reference style",
        a1: "A1",
        r1c1: "R1C1",
        headers: "Headers",
        columnHeaders: "Column headers",
        rowHeaders: "row headers",
        columnHeaderRowCount: "Column header row count",
        columnHeaderAutoText: "Column header auto-generated text",
        columnHeaderAutoIndex: "Column header auto index",
        defaultRowHeight: "Default row height",
        columnHeaderVisible: "Column header is visible",
        blank: "blank",
        numbers: "numbers",
        letters: "letters",
        rowHeaderColumnCount: "Number of row header columns",
        rowHeaderAutoText: "Automatically generate text for row headers",
        rowHeaderAutoIndex: "Row header auto index",
        defaultColumnWidth: "Default column width",
        rowHeaderVisible: "Row header is visible",
        sheetTab: "Sheet Tab",
        sheetTabColor: "Sheet Tab Color:"
    };

    cn_res.groupDirectionDialog = {
        settings: "Settings",
        direction: "direction",
        rowDirection: "below the detail data",
        columnDirection: "Right side of detail data"
    };

    cn_res.insertSparklineDialog = {
        createSparklines: "Insert Chart",
        dataRange: "Chart data range:",
        dataRangeTitle: "Select data source",
        locationRange: "Location to place the chart",
        locationRangeTitle: "Choose where to place the chart",
        errorDataRangeMessage: "Please enter a valid range",
        isFormulaSparkline: "Formula Chart"
    };

    cn_res.sparklineWeightDialog = {
        sparklineWeight: "Chart Line Weight",
        inputWeight: "Input chart line width (pt)",
        errorMessage: "Please enter a valid line width."
    };

    cn_res.sparklineMarkerColorDialog = {
        sparklineMarkerColor: "Chart marker color:",
        negativePoints: "Negative number:",
        markers: "markers:",
        highPoint: "Maximum value:",
        lowPoint: "Minimum:",
        firstPoint: "First value:",
        lastPoint: "Last value:"
    };

    cn_res.resizeTableDialog = {
        title: "Resize Table",
        dataRangeTitle: "Select a new data range for the table:",
        note: "Note: Header must remain on the same line,\r\nResult table area must overlap original table area."
    };

    cn_res.saveAsDialog = {
        title: "Save file",
        fileNameLabel: "File name:"
    };

    cn_res.statusBar = {
        ready: "ready",
        enter: "Enter",
        edit: "edit"
    };

    cn_res.calendarSparklineDialog = {
        calendarSparklineDialog: "Calendar Sparkline",
        monthSparklineDialog: "Monthly Sparkline",
        yearSparklineDialog: "Year Sparkline",
        emptyColor: "empty value color",
        startColor: "Start value color",
        middleColor: "middle value color",
        endColor: "End value color",
        rangeColor: "range color",
        year: "year",
        month: "month"
    };
    cn_res.pieSparklineDialog = {
        percentage: "percentage",
        color: "color",
        addColor: "Add Color",
        pieSparklineSetting: "Pie Chart Settings"
    };

    cn_res.areaSparklineDialog = {
        title: "Area Chart Formula",
        points: "points",
        min: "minimum value",
        max: "Maximum value",
        line1: "Line 1",
        line2: "Line 2",
        positiveColor: "Positive Color",
        negativeColor: "Negative Color",
        areaSparklineSetting: "Area Chart Settings"
    };

    cn_res.scatterSparklineDialog = {
        points1: "point 1",
        points2: "point 2",
        minX: "X-axis minimum value",
        maxX: "X-axis maximum value",
        minY: "Y-axis minimum value",
        maxY: "Y-axis maximum value",
        hLine: "horizontal axis",
        vLine: "vertical axis",
        xMinZone: "X-axis minimum zone",
        yMinZone: "Y-axis minimum zone",
        xMaxZone: "X-axis maximum zone",
        yMaxZone: "Y-axis maximum zone",
        tags: "tags",
        drawSymbol: "Draw Symbol",
        drawLines: "Draw Lines",
        color1: "color1",
        color2: "color2",
        dash: "dash",
        scatterSparklineSetting: "scatterplot settings"
    };

    cn_res.compatibleSparklineDialog = {
        title: "compatibleSparkline formula",
        style: "style",
        show: "show",
        group: "group",
        data: "data",
        dataOrientation: "Data Orientation",
        dateAxisData: "date axis data",
        dateAxisOrientation: "Date Axis Orientation",
        setting: "settings",
        axisColor: "axis",
        firstMarkerColor: "First Marker",
        highMarkerColor: "Highest Marker",
        lastMarkerColor: "Tail Marker",
        lowMarkerColor: "Lowest Marker",
        markersColor: "markers",
        negativeColor: "negative number",
        seriesColor: "Series",
        displayXAxis: "Display X-axis",
        showFirst: "Show First",
        showHigh: "Show Highest",
        showLast: "Show last",
        showLow: "Show Lowest",
        showNegative: "Show negative numbers",
        showMarkers: "Show Markers",
        lineWeight: "Line Weight",
        displayHidden: "Display data in hidden rows and columns",
        displayEmptyCellsAs: "Empty cells are displayed as",
        rightToLeft: "From right to left",
        minAxisType: "Minimum axis type",
        maxAxisType: "Maximum axis type",
        manualMax: "manual maximum",
        manualMin: "Manual minimum",
        gaps: "gap",
        zero: "0",
        connect: "connect",
        vertical: "vertical",
        horizontal: "horizontal",
        stylesetting: "Style Setting",
        individual: "individual",
        custom: "custom",
        compatibleSparklineSetting: "compatibleSparkline Setting",
        styleSetting: "Style Setting",
        errorMessage: "Please enter a valid area."
    };

    cn_res.bulletSparklineDialog = {
        bulletSparklineSetting: "Bullet Chart Settings",
        measure: "measure",
        target: "target",
        maxi: "Maxi",
        good: "good",
        bad: "bad",
        forecast: "forecast",
        tickunit: "unit",
        colorScheme: "Theme Color",
        vertical: "Vertical"
    };

    cn_res.spreadSparklineDialog = {
        spreadSparklineSetting: "SpreadSparkline Configuration",
        points: "points",
        showAverage: "Show Average",
        scaleStart: "scale start",
        scaleEnd: "scale end",
        style: "style",
        colorScheme: "Theme Color",
        vertical: "vertical",
        stacked: "stacked",
        spread: "spread",
        jitter: "jitter",
        poles: "poles",
        stackedDots: "Stacked Dots",
        stripe: "stripe"
    };

    cn_res.stackedSparklineDialog = {
        stackedSparklineSetting: "StackedSparkline Setting",
        points: "points",
        colorRange: "Color Range",
        labelRange: "Label Range",
        maximum: "maximum value",
        targetRed: "target red",
        targetGreen: "target green",
        targetBlue: "target blue",
        targetYellow: "Target Yellow",
        color: "color",
        highlightPosition: "Highlight Position",
        vertical: "vertical",
        textOrientation: "Text Orientation",
        textSize: "Text Size",
        textHorizontal: "Horizontal",
        textVertical: "Vertical",
        px: "px"
    };

    cn_res.barbaseSparklineDialog = {
        hbarSparklineSetting: "HbarSparkline Setting",
        vbarSparklineSetting: "VbarSparkline Setting",
        value: "value",
        colorScheme: "Theme Color"
    };

    cn_res.variSparklineDialog = {
        variSparklineSetting: "VariSparkline Setting",
        variance: "variance",
        reference: "reference",
        mini: "minimum",
        maxi: "maximum",
        mark: "mark",
        tickunit: "tick unit",
        legend: "Legend",
        colorPositive: "Positive Color",
        colorNegative: "Negative Color",
        vertical: "Vertical"
    };
    cn_res.boxplotSparklineDialog = {
        boxplotSparklineSetting: "BoxPlotSparkline Setting",
        points: "points",
        boxPlotClass: "Block Plot Class",
        showAverage: "Show Average",
        scaleStart: "scale start",
        scaleEnd: "scale end",
        acceptableStart: "Acceptable Start",
        acceptableEnd: "Acceptable End",
        colorScheme: "Theme Color",
        style: "style",
        vertical: "vertical",
        fiveNS: "5NS",
        sevenNS: "7NS",
        tukey: "Tukey",
        bowley: "Bowley",
        sigma: "Sigma 3",
        classical: "tradition",
        neo: "new"
    };
    cn_res.cascadeSparklineDialog = {
        cascadeSparklineSetting: "CascadeSparkline Setting",
        pointsRange: "Point Range",
        pointIndex: "Point Index",
        labelsRange: "label range",
        minimum: "minimum",
        maximum: "maximum value",
        colorPositive: "Positive Color",
        colorNegative: "Negative Color",
        vertical: "Vertical"
    };

    cn_res.multiCellFormula = {
        warningText: "The selection range may contain different formula categories. Please select a new range."
    };

    cn_res.paretoSparklineDialog = {
        paretoSparklineSetting: "ParetoSparkline Setting",
        points: "points",
        pointIndex: "Point Index",
        colorRange: "Color Range",
        target: "target",
        target2: "target2",
        highlightPosition: "Highlight Position",
        label: "label",
        vertical: "vertical",
        none: "none",
        cumulated: "stacked",
        single: "single"
    };

    cn_res.sliderPanel = {
        title: "Field List"
    };

    cn_res.protectionOptionDialog = {
        title: "Include Worksheet",
        label: "Allow all users of this sheet to:",
        allowSelectLockedCells: "Select Locked Cells",
        allowSelectUnlockedCells: "Select unlocked cells",
        allowSort: "Sort",
        allowFilter: "Use AutoFilter",
        allowResizeRows: "Resize Rows",
        allowResizeColumns: "Resize columns",
        allowEditObjects: "Edit Objects",
        allowDragInsertRows: "Drag Insert Rows",
        allowDragInsertColumns: "Drag Insert Columns",
        allowInsertRows: "Insert Rows",
        allowInsertColumns: "Insert Columns",
        allowDeleteRows: "Delete Rows",
        allowDeleteColumns: "Delete Columns"
    };

    cn_res.insertSlicerDialog = {
        insertSlicer: "Insert Slicer"
    };

    cn_res.formatSlicerStyle = {
        custom: "custom",
        light: "light color",
        dark: "dark",
        other: "other",
        newSlicerStyle: "New Slicer Style...",
        slicerStyle: "Slicer Style",
        name: "name",
        slicerElement: "Slicer Element",
        format: "format",
        clear: "clear",
        preview: "Preview",
        exception: "This style name already exists."
    };

    cn_res.slicerElement = {
        wholeSlicer: "Whole Slicer",
        header: "Header",
        selectedItemWithData: "Selected item with data",
        selectedItemWithNoData: "Item without data has been selected",
        unselectedItemWithData: "Item with data unselected",
        unselectedItemWithNoData: "Item without data has been unselected",
        hoveredSelectedItemWithData: "Hover the selected item with data",
        hoveredSelectedItemWithNoData: "Hovered selected item without data",
        hoveredUnselectedItemWithData: "Hovering unselected item with data",
        hoveredUnselectedItemWithNoData: "Hovering unselected item with no data"
    };

    cn_res.slicerSettingDialog = {
        slicerSetting: "Slicer Settings",
        sourceName: "Source Name:",
        name: "Name:",
        header: "Header",
        display: "Display header",
        caption: "Title:",
        items: "Item Sorting and Filtering",
        ascending: "Ascending (smallest to largest)",
        descending: "Descending (largest to smallest)",
        customList: "Use a custom list when sorting",
        hideItem: "Hide items without data",
        visualItem: "Visually indicate empty data items",
        showItem: "Last show empty data item"
    };

    cn_res.slicerPropertyDialog = {
        formatSlicer: "Format Slicer",
        position: "Position and Layout",
        size: "size",
        properties: "properties",
        pos: "position",
        horizontal: "horizontal:",
        vertial:"Vertical:",
        disableResizingMoving: "Disable resizing and moving",
        layout: "frame",
        numberColumn: "Number of columns:",
        buttonHeight: "Button height:",
        buttonWidth: "Button width:",
        height: "Height:",
        width: "width:",
        scaleHeight: "Scale height:",
        scaleWidth: "Scale width:",
        moveSize: "Size and position change with cell",
        moveNoSize: "The size is fixed, and the position changes with the cell",
        noMoveSize: "fixed size and position",
        locked: "locked"
    };

    cn_res.selectData = {
        changeDataRange: 'Chart data range:',
        switchRowColumn: 'Switch row/column',
        legendEntries: 'Legend entries (series)',
        moveUp: 'move up',
        moveDown: 'Move down',
        horizontalAxisLabels: 'Horizontal (category) axis labels',
        add: 'Add',
        edit: 'edit',
        remove: 'remove',
        selectDataSource: 'Select data source',
        addSeries: 'Edit data series',
        editSeries: 'Edit data series',
        editSeriesName: 'Edit data series',
        editSeriesValue: 'Edit data series',
        seriesName: 'series name',
        seriesYValue: 'Y-axis series value',
        seriesXValue: 'X-axis series value',
        seriesSize: 'Series bubble size',
        errorPrompt: {
            cantRemoveLastSeries: "If you want to use a chart, make sure the chart has at least one series.",
            seriesValueIsIllegal: 'Unsupported series reference',
            cantSwitchRowColumn: "The current data area does not support switching rows/columns",
            categoryValueIsIllegal: "Category reference not yet supported"
        },
        noDataRange: 'The data range is too complex to display. If you select a new data range, all series in the series pane will be replaced. '
    };

    cn_res.addChartElement = {
        axes: {
            axes: 'coordinate axes',
            moreAxisOption: 'More axis options'
        },
        axisTitles: {
            axisTitles: 'Axis titles',
            moreAxisTitlesOption: 'More axis title options'
        },
        chartTitle: {
            chartTitle: 'Chart title',
            moreChartTitleOption: 'More chart title options'
        },
        gridLines: {
            gridLines: 'Gridlines',
            moreGridLinesOption: 'More gridline options'
        },
        dataLabels: {
            dataLabels: 'Data Labels',
            moreDataLabelsOption: 'More data label options'
        },
        legend: {
            legend: 'Legend',
            moreLegendOption: 'More legend options'
        },
        primaryHorizontal: 'Primary horizontal axis',
        primaryVertical: 'Primary vertical axis',
        secondaryHorizontal: 'Secondary horizontal axis',
        secondaryVertical: 'Secondary vertical axis',
        none: 'none',
        aboveChart: 'above the chart',
        primaryMajorHorizontal: 'Major main horizontal grid lines',
        primaryMajorVertical: 'The primary major vertical grid line',
        primaryMinorHorizontal: 'Main axis minor horizontal grid lines',
        primaryMinorVertical: 'Main Minor Vertical Gridlines',
        secondaryMajorHorizontal: 'The main horizontal grid line of the secondary axis',
        secondaryMajorVertical: 'The main vertical grid line of the secondary axis',
        secondaryMinorHorizontal: 'Secondary axis minor horizontal grid lines',
        secondaryMinorVertical: 'Secondary axis minor vertical grid line',
        center: 'Center',
        insideEnd: 'inside the data tag',
        outsideEnd: 'Outside the data label',
        bestFit: 'Best Fit',
        above: 'above',
        below: 'below',
        show: 'show',
        right: 'right',
        top: 'top',
        left: 'left',
        bottom: 'bottom'
    };
    
    /** modified by zhanghang@sunwayworld.com begin **/
    cn_res.sunwayTips = {
        tipTitle: 'tip',
        SignCellErrorUserNameOrPasswordNotNull: 'Username and password are not allowed to be empty! ',
        SignCellErrorUserNameOrPasswordError: 'The account or password is wrong! ',
        SignCellErrorUserWithoutSignature: 'The user has not maintained an electronic signature! ',
        systemError: 'System failure! Please contact the administrator! ',
        pdfUploadFailed: 'PDF upload failed, try again or contact administrator',
        loadFontFailed: 'The font file has deserted! Please refresh the page! ',
        missingBizParam: 'Missing business parameter!',
        selectCells: 'Please select some content! ',
        userLoginTimeOut: 'The login information has expired, please keep it, do not refresh the ELN editing interface, refresh the system page and log in again and try again! ',
        saveError: 'Error saving! Please contact the administrator! ',
        initError: 'Initialization error! Please contact the administrator! ',
        selectRegionAgain: 'The data does not correspond or the selection is wrong, please re-select the region',
        selectRegion: 'Please select the cell range',
        pdfUploadSuccess: 'PDF uploaded successfully',
        saveSuccess: 'Successfully saved',
        maskTitleRequestData: 'Data requesting...',
        maskTitleSave: 'ELN is saving',
        mastTitleUpload:'Uploading...'
    };
    /** modified by zhanghang@sunwayworld.com end **/

    cn_res.name = "cn";

    designer.res = cn_res;

})();