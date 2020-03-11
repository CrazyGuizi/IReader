package com.ldg.ireader.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DbBookRecord {

    @Id(autoincrement = true)
    private long _id;
    private String id;
    private String chapterId;
    private String chapterName;
    private int pagePosition;
    private boolean isLocal;

    public DbBookRecord(String id) {
        this.id = id;
    }

    @Generated(hash = 924838294)
    public DbBookRecord(long _id, String id, String chapterId, String chapterName,
            int pagePosition, boolean isLocal) {
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

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
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
