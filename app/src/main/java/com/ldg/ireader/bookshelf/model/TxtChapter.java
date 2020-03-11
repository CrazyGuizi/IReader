package com.ldg.ireader.bookshelf.model;

public class TxtChapter {

    private String id;
    private String title;
    private String bookId;

    public TxtChapter(String id, String title, String bookId) {
        this.id = id;
        this.title = title;
        this.bookId = bookId;
    }

    public TxtChapter(ChapterModel chapterModel) {
        if (chapterModel == null) {
            return;
        }
        id = chapterModel.getId();
        title = chapterModel.getName();
        bookId = chapterModel.getNovelId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

}
