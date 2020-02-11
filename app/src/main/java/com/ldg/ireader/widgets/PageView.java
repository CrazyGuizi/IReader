package com.ldg.ireader.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.ldg.ireader.R;
import com.ldg.ireader.utils.PageHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PageView extends View {

    private PageHelper mPageHelper;
    private int mWidth, mHeight;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPageHelper = new PageHelper(this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPageHelper.setSize(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mPageHelper.getCurBitmap(), null, new RectF(0, 0, mWidth, mHeight), null);
        mPageHelper.update();
    }


}
