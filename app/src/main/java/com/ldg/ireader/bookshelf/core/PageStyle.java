package com.ldg.ireader.bookshelf.core;

import androidx.annotation.ColorRes;

import com.ldg.ireader.R;


/**
 * Created by newbiechen on 2018/2/5.
 * 作用：页面的展示风格。
 */

public enum PageStyle {
    BG_WHITE(R.color.color_font_day, R.color.white),
    BG_BROWN(R.color.color_font_day, R.color.color_d9c68c),
    BG_CANARY(R.color.color_font_day, R.color.color_efe5ce),
    BG_RESEDA(R.color.color_font_day, R.color.color_c3e4c4),
    BG_DARK(R.color.color_font_night, R.color.color_181715);

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
