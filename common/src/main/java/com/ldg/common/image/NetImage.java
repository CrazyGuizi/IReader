package com.ldg.common.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class NetImage extends AppCompatImageView {

    private Build mBuild;

    public NetImage(Context context) {
        this(context, null);
    }

    public NetImage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

    }

    public void setImage(String url) {
        Glide.with(this).load(url).into(this);
    }


    private static class Builder {

    }
}
