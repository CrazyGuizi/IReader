package com.ldg.ireader.bookshelf.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookModel implements Serializable {
    private static final long serialVersionUID = -935820639380721547L;

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("classify")
    private String classify;
    @SerializedName("cover")
    private String coverUrl;
    @SerializedName("cover_width")
    private int coverWidth;
    @SerializedName("cover_height")
    private int coverHeight;
    @SerializedName("author")
    private String author;
    @SerializedName("desc")
    private String description;

    private boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

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

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCoverWidth() {
        return coverWidth;
    }

    public void setCoverWidth(int coverWidth) {
        this.coverWidth = coverWidth;
    }

    public int getCoverHeight() {
        return coverHeight;
    }

    public void setCoverHeight(int coverHeight) {
        this.coverHeight = coverHeight;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
