package com.ldg.ireader.bookshelf.model;

import java.io.Serializable;

public class TxtChapter implements Serializable {
    private static final long serialVersionUID = 397545697458318195L;


    //章节名(共用)
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
