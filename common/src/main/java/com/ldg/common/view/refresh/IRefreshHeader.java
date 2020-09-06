package com.ldg.common.view.refresh;

import android.view.View;

import androidx.annotation.NonNull;

public interface IRefreshHeader {

    @NonNull View getHeader();

    /**
     *
     * @return true：内容随着头部一起滚动，false：指滚动头部
     */
    boolean isScrollContent();

    /**
     * 达成刷新条件
     */
    void onRefreshTrigger();

    /**
     * 刷新条件失效
     */
    void onRefreshShut();

    /**
     * 取消刷新
     */
    void onRefreshCancel();

    /**
     * 开始刷新
     */
    void onRefreshStart();

    /**
     * 刷新完成
     */
    void onRefreshFinish();

    /**
     * 刷新完毕
     */
    void onRefreshEnd();


    void onOffsetChange(float percent);
}
