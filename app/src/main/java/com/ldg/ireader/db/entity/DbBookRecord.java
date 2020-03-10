package com.ldg.ireader.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DbBookRecord {

    @Id(autoincrement = true)
    private long _id;

    private String id;

    private int chapterId;

    private int pagePosition;

    public DbBookRecord(String id, int chapterId, int pagePosition) {
        this.id = id;
        this.chapterId = chapterId;
        this.pagePosition = pagePosition;
    }

    @Generated(hash = 1047067131)
    public DbBookRecord(long _id, String id, int chapterId, int pagePosition) {
        this._id = _id;
        this.id = id;
        this.chapterId = chapterId;
        this.pagePosition = pagePosition;
    }

    @Generated(hash = 1711340376)
    public DbBookRecord() {
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getChapterId() {
        return this.chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getPagePosition() {
        return this.pagePosition;
    }

    public void setPagePosition(int pagePosition) {
        this.pagePosition = pagePosition;
    }
}
