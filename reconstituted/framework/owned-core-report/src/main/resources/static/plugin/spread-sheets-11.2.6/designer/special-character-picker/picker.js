class SpecialCharacterPicker {

    constructor(args = {}) {
        this.init(args);
    }

    async init(args) {
        this.url = "/secure/core/module/item/special-characters/queries/choose-qty-desc";
        this.characterWidth = args.characterWidth || 30;
        this.characterHeight = args.characterHeight || 30;
        this.maxHeight = args.maxHeight || 150;
        this.minHeight = args.minHeight || 30;
        this.target = args.target;
        this.addCharacter = args.addCharacter || (() => {});
        this.pickerWidth = this.getPickerWidth(args.width);
        this.left = this.calcLeft();
        this.characterData = await this.getCharacterData();
        this.top = this.calcTop(this.characterData.length);
        window.specialCharacterPicker = this;
        this.render();
    }

    render() {
        const picker = this.picker = document.createElement('div');
        picker.classList.add("special-character-picker");
        picker.style.top = this.top + 'px';
        picker.style.left = this.left + 'px';
        document.body.appendChild(picker);

        const content = document.createElement('div');
        content.classList.add('characters-content');
        content.style.width = this.pickerWidth + 'px';
        content.style.maxHeight = this.maxHeight + 'px';
        content.style.minHeight = this.minHeight + 'px';
        picker.appendChild(content);

        for (let i = 0; i < this.characterData.length; i++) {
            const element = this.characterData[i];
            const character = document.createElement("div");
            character.classList.add("character");
            character.style.width = this.characterWidth - 6 + 'px';
            character.style.height = this.characterHeight - 6 + 'px';
            content.appendChild(character);

            character.innerHTML = element.specialCharacter;
            character.onclick = () => {
                this.addCharacter(element);
            }
        }

        this.expand = true;
    }

    async getCharacterData() {
        let url = this.url;
        let data = JSON.stringify({
            p: {
                f: {
                    CATEGORY_EQ: "custom",
                },
            },
        });

        let d = await $.ajax({
            url,
            data,
            dataType: "json",
            type: "post",
            contentType: "application/json",
        });

        let characterData = [];

        if (d && d.rows && d.rows.length > 0) {
            characterData = d.rows.map(item => {
                return item;
            });
        }

        return characterData;
    }

    getPickerWidth(width) {
        const $inputEl = $(this.target);
        let pickerWidth = $inputEl.width();
        if (width > 0) {
            pickerWidth = width;
        }
        return Math.ceil(pickerWidth / this.characterWidth) * this.characterWidth;
    }

    calcLeft() {
        let $inputEl = $(this.target);
        return $inputEl.offset().left;
    }


    calcTop(itemNum) {
        let rowNum = Math.ceil(itemNum / (this.pickerWidth / this.characterWidth));
        let pickerHeight =
            rowNum * this.characterHeight > this.maxHeight ? this.maxHeight : rowNum * this.characterHeight;
        let $inputEl = $(this.target);
        const $elTop = $inputEl.offset().top;
        let top = 0;
        if ($elTop + $inputEl.height() + this.maxHeight > $(window).height()) {
            if (pickerHeight > $elTop) {
                pickerHeight = this.maxHeight = Math.floor($elTop / this.characterHeight) * this.characterHeight;
            }
            top = $elTop - pickerHeight - 10;
        } else {
            top = $elTop + $inputEl.height() + 6;
        }

        return top;
    }

    getContextPath() {
        const pathName = document.location.pathname;
        const index = pathName.substring(1).indexOf("/");
        const result = pathName.substring(0, index + 1);
        return result;
    }

    hide() {
        this.picker.style.display = 'none';
        this.expand = false;
    }

    show() {
        this.picker.style.display = 'block';
        this.expand = true;
    }
}