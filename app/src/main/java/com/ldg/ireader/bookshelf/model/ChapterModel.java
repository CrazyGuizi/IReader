package com.ldg.ireader.bookshelf.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChapterModel implements Serializable {
    private static final long serialVersionUID = -2255174617827620100L;
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("href")
    private String href;
    @SerializedName("content")
    private String content;
    @SerializedName("novel_id")
    private String novelId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }
}

