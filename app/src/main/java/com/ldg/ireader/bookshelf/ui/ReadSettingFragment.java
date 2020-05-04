package com.ldg.ireader.bookshelf.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldg.common.view.BaseDialogFragment;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.core.PageStyle;
import com.ldg.ireader.bookshelf.core.anim.PageAnimation;
import com.ldg.ireader.bookshelf.core.anim.PageMode;
import com.ldg.ireader.bookshelf.core.config.PageConfig;

public class ReadSettingFragment extends BaseDialogFragment implements View.OnClickListener {

    private FrameLayout mFLPlace;
    private View mViewColorSelect;
    private View mViewFontSelect;
    private TextView mTvFontSize;


    private Callback mCallback;
    private TextView mViewColorWhite;
    private TextView mViewColorBrown;
    private TextView mViewColorCanary;
    private TextView mViewColorReseda;
    private TextView mViewColorDark;
    private TextView mAnimScroll;
    private TextView mAnimCover;
    private TextView mAnimSlide;
    private TextView mAnimNone;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected int getResLayout() {
        return R.layout.frag_book_read_setting;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getTheme() {
        return android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFLPlace.setVisibility(View.GONE);

        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.dimAmount = 0F;
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(attributes);
        }
    }

    @Override
    protected void initWidgets(View rootView) {
        mFLPlace = rootView.findViewById(R.id.fl_place_holder);
        mViewColorSelect = LayoutInflater.from(getContext()).inflate(R.layout.view_color_style, null);
        mViewFontSelect = LayoutInflater.from(getContext()).inflate(R.layout.view_font_and_anim, null);
        mTvFontSize = mViewFontSelect.findViewById(R.id.tv_font_size);

        mTvFontSize.setText(String.valueOf(PageConfig.get().getTextSize()));

        rootView.findViewById(R.id.iv_back).setOnClickListener(this);
        rootView.findViewById(R.id.lf_menu).setOnClickListener(this);
        rootView.findViewById(R.id.lf_color).setOnClickListener(this);
        rootView.findViewById(R.id.lf_font).setOnClickListener(this);
        rootView.findViewById(R.id.root).setOnClickListener(this);


        mViewColorWhite = mViewColorSelect.findViewById(R.id.color_white);
        mViewColorBrown = mViewColorSelect.findViewById(R.id.color_brown);
        mViewColorCanary = mViewColorSelect.findViewById(R.id.color_canary);
        mViewColorReseda = mViewColorSelect.findViewById(R.id.color_reseda);
        mViewColorDark = mViewColorSelect.findViewById(R.id.color_dark);
        changeSelectedColor();

        mViewColorWhite.setOnClickListener(this);
        mViewColorBrown.setOnClickListener(this);
        mViewColorCanary.setOnClickListener(this);
        mViewColorReseda.setOnClickListener(this);
        mViewColorDark.setOnClickListener(this);

        mViewFontSelect.findViewById(R.id.fl_font_dec).setOnClickListener(this);
        mViewFontSelect.findViewById(R.id.fl_font_add).setOnClickListener(this);
        mAnimScroll = mViewFontSelect.findViewById(R.id.tv_anim_scroll);
        mAnimCover = mViewFontSelect.findViewById(R.id.tv_anim_cover);
        mAnimSlide = mViewFontSelect.findViewById(R.id.tv_anim_slide);
        mAnimNone = mViewFontSelect.findViewById(R.id.tv_anim_none);
        changeSelectedAnim();

        mAnimScroll.setOnClickListener(this);
        mAnimCover.setOnClickListener(this);
        mAnimSlide.setOnClickListener(this);
        mAnimNone.setOnClickListener(this);
    }

    private void changeSelectedColor() {
        PageStyle pageStyle = PageConfig.get().getPageStyle();
        mViewColorWhite.setSelected(pageStyle == PageStyle.BG_WHITE);
        mViewColorBrown.setSelected(pageStyle == PageStyle.BG_BROWN);
        mViewColorCanary.setSelected(pageStyle == PageStyle.BG_CANARY);
        mViewColorReseda.setSelected(pageStyle == PageStyle.BG_RESEDA);
        mViewColorDark.setSelected(pageStyle == PageStyle.BG_DARK);
    }

    private void changeSelectedAnim() {
        PageMode pageMode = PageConfig.get().getPageMode();
        mAnimScroll.setSelected(pageMode == PageMode.SCROLL);
        mAnimCover.setSelected(pageMode == PageMode.COVER);
        mAnimSlide.setSelected(pageMode == PageMode.SLIDE);
        mAnimNone.setSelected(pageMode == PageMode.NONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.lf_color) {
            mFLPlace.removeAllViews();
            mFLPlace.addView(mViewColorSelect);
            mFLPlace.setVisibility(View.VISIBLE);
        } else if (id == R.id.lf_font) {
            mFLPlace.removeAllViews();
            mFLPlace.addView(mViewFontSelect);
            mFLPlace.setVisibility(View.VISIBLE);
        } else if (id == R.id.lf_menu) {
            dismiss();
            if (mCallback != null) {
                mCallback.onClickMenu();
            }
        } else if (id == R.id.iv_back) {
            if (mCallback != null) {
                mCallback.onClickBack();
            }
        } else if (id == R.id.color_brown
                || id == R.id.color_canary
                || id == R.id.color_canary
                || id == R.id.color_reseda
                || id == R.id.color_dark
                || id == R.id.color_white) {
            PageStyle pageStyle = PageStyle.BG_BROWN;
            if (id == R.id.color_canary) {
                pageStyle = PageStyle.BG_CANARY;
            } else if (id == R.id.color_canary) {
                pageStyle = PageStyle.BG_CANARY;
            } else if (id == R.id.color_reseda) {
                pageStyle = PageStyle.BG_RESEDA;
            } else if (id == R.id.color_dark) {
                pageStyle = PageStyle.BG_DARK;
            } else if (id == R.id.color_white) {
                pageStyle = PageStyle.BG_WHITE;
            }

            if (PageConfig.get().getPageStyle() != pageStyle) {
                PageConfig.get().setPageStyle(pageStyle);
                changeSelectedColor();
                if (mCallback != null) {
                    mCallback.onChangeColor(pageStyle);
                }
            }
        } else if (id == R.id.root) {
            dismiss();
        } else if (id == R.id.fl_font_dec) {
            int size = PageConfig.get().getTextSize() - 1;
            if (size >= PageConfig.MIN_TEXT_SIZE) {
                changeFontSize(size);
            }
        } else if (id == R.id.fl_font_add) {
            int size = PageConfig.get().getTextSize() + 1;
            if (size <= PageConfig.MAX_TEXT_SIZE) {
                changeFontSize(size);
            }
        } else if (id == R.id.tv_anim_scroll
                || id == R.id.tv_anim_cover
                || id == R.id.tv_anim_slide
                || id == R.id.tv_anim_none) {
            PageMode mode = PageMode.SCROLL;
            if (id == R.id.tv_anim_cover) {
                mode = PageMode.COVER;
            } else if (id == R.id.tv_anim_slide) {
                mode = PageMode.SLIDE;
            } else if (id == R.id.tv_anim_none) {
                mode = PageMode.NONE;
            }

            if (PageConfig.get().getPageMode() != mode) {
                PageConfig.get().setPageMode(mode);
                changeSelectedAnim();
                if (mCallback != null) {
                    mCallback.onChangeAnimMode(mode);
                }
            }
        }
    }

    private void changeFontSize(int size) {
        mTvFontSize.setText(String.valueOf(size));
        if (mCallback != null) {
            mCallback.onChangeFont(size);
        }
    }


    public interface Callback {
        void onClickBack();

        void onClickMenu();

        void onChangeColor(PageStyle style);

        void onChangeFont(int size);

        void onChangeAnimMode(PageMode pageMode);
    }
}
