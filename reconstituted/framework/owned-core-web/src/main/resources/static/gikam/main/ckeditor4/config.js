/* eslint-disable no-undef */
/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function(config) {
    config.toolbarGroups = [
        { name: 'document', groups: ['mode', 'document', 'doctools'] },
        { name: 'clipboard', groups: ['clipboard', 'undo'] },
        { name: 'editing', groups: ['find', 'selection', 'spellchecker', 'editing'] },
        { name: 'insert', groups: ['insert'] },
        '/',
        { name: 'basicstyles', groups: ['basicstyles', 'cleanup'] },
        { name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi', 'paragraph'] },
        { name: 'links', groups: ['links'] },
        '/',
        { name: 'styles', groups: ['styles'] },
        { name: 'colors', groups: ['colors'] },
        { name: 'tools', groups: ['tools'] },
        { name: 'others', groups: ['others'] },
        { name: 'forms', groups: ['forms'] },
        { name: 'about', groups: ['about'] },
    ];

    // config.toolbarGroups = [
    //     { name: 'clipboard', groups: ['clipboard', 'undo'] },
    //     { name: 'editing', groups: ['find', 'selection', 'spellchecker'] },
    //     { name: 'links' },
    //     { name: 'insert' },
    //     { name: 'forms' },
    //     { name: 'tools' },
    //     { name: 'document', groups: ['mode', 'document', 'doctools'] },
    //     { name: 'others' },
    //     '/',
    //     { name: 'basicstyles', groups: ['basicstyles', 'cleanup'] },
    //     { name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi'] },
    //     { name: 'styles' },
    //     { name: 'colors' },
    //     { name: 'about' },
    // ];

    //	config.removeButtons = 'Save,NewPage,PasteText,PasteFromWord,Paste,Copy,Cut,Scayt,Form,Radio,TextField,Textarea,Select,Button,HiddenField,Language,Iframe,Flash,Link,Unlink,Anchor,About';

    /*Link,Unlink*/
    config.toolbarCanCollapse = true; //折叠工具栏

    config.allowedContent = true;

    // 兼容ie选中时不出现拖拽边框
    config.enterMode = CKEDITOR.ENTER_BR;

    config.shiftEnterMode = CKEDITOR.ENTER_P;

    config.skin = 'moono-lisa';

    config.filebrowserImageUploadUrl = top.Gikam.IFM_CONTEXT + '/core/module/item/files/action/ckeditor-upload';

    config.uploadUrl = top.Gikam.IFM_CONTEXT + '/core/module/item/files/action/ckeditor-upload';

    // config.removePlugins = 'resize';

    // config.resize_enabled = false;

    config.imageUploadUrl = top.Gikam.IFM_CONTEXT + '/core/module/item/files/action/ckeditor-upload';

    config.baseHref = top.Gikam.IFM_CONTEXT + '/';

    config.font_names =
        '宋体/SimSun;新宋体/NSimSun;仿宋_GB2312/FangSong_GB2312;楷体_GB2312/KaiTi_GB2312;黑体/SimHei;微软雅黑/Microsoft YaHei;' +
        config.font_names;

    config.fontSize_sizes =
        '初号/56px;小初/48px;一号/34px;小一/32px;二号/29px;小二/24px;三号/21px;小三/20px;四号/18px;小四/16px;五号/14px;小五/12px;六号/10px;小六/8px' +
        '9/9px;11/11px;22/22px;26/26px;28/28px;36/36px;72/72px';

    config.specialChars = config.specialChars.concat([
        ['&alpha;', 'alpha'],
        ['&beta;', 'beta'],
        ['&gamma;', 'gamma'],
        ['&delta;', 'delta'],
        ['&epsilon;', 'epsilon'],
        ['&zeta;', 'zeta'],
        ['&eta;', 'eta'],
        ['&theta;', 'theta'],
        ['&iota;', 'iota'],
        ['&kappa;', 'kappa'],
        ['&lambda;', 'lambda'],
        ['&mu;', 'mu'],
        ['&nu;', 'nu'],
        ['&xi;', 'xi'],
        ['&omicron;', 'omicron'],
        ['&pi;', 'pi'],
        ['&rho;', 'rho'],
        ['&sigma;', 'sigma'],
        ['&tau;', 'tau'],
        ['&upsilon;', 'upsilon'],
        ['&phi;', 'phi'],
        ['&chi;', 'chi'],
        ['&psi;', 'psi'],
        ['&omega;', 'omega'],
    ]);

    // 特殊字符不转义设置
    config.entities_greek = false;
};
