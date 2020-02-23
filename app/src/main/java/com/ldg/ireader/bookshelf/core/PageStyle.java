package com.ldg.ireader.bookshelf.core;

import androidx.annotation.ColorRes;

import com.ldg.ireader.R;


/**
 * Created by newbiechen on 2018/2/5.
 * 作用：页面的展示风格。
 */

public enum PageStyle {
    BG_GREEN(R.color.nb_read_font_1, R.color.nb_read_bg_1);

    private int fontColor;
    private int bgColor;

    PageStyle(@ColorRes int fontColor, @ColorRes int bgColor) {
        this.fontColor = fontColor;
        this.bgColor = bgColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }
}
