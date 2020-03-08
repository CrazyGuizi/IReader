package com.ldg.ireader.bookshelf.presenter;

public interface BookCallback {
    public void onUpdate(boolean isSuccess, Object res);

    void onChapter(boolean isSuccess, Object res);
}
