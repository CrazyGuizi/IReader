package com.ldg.ireader.db.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DbBookRecord {

    // 前一个章节的标志
    public static final int POSITION_PRE = -1;
    // 后一个章节的标志
    public static final int POSITION_NEXT = -2;

    @Id(autoincrement = true)
    private Long _id;
    private String id;
    private String chapterId;
    private String chapterName;
    private int pagePosition;
    private boolean isLocal;

    public DbBookRecord(String id) {
        this.id = id;
    }

    public DbBookRecord(String id, String chapterId, String chapterName) {
        this.id = id;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
    }

    public DbBookRecord(String id, String chapterId, String chapterName, int pagePosition, boolean isLocal) {
        this.id = id;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.pagePosition = pagePosition;
        this.isLocal = isLocal;
    }

    @Generated(hash = 944959424)
    public DbBookRecord(Long _id, String id, String chapterId, String chapterName, int pagePosition,
            boolean isLocal) {
        this._id = _id;
        this.id = id;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.pagePosition = pagePosition;
        this.isLocal = isLocal;
    }

    @Generated(hash = 1711340376)
    public DbBookRecord() {
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public int getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(int pagePosition) {
        this.pagePosition = pagePosition;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public boolean getIsLocal() {
        return this.isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }
}
