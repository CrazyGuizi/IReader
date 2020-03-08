package com.ldg.ireader.bookshelf.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookShelfMyBooksModel implements Serializable {
    private static final long serialVersionUID = -848867799513478193L;

    @SerializedName("books")
    private List<BookModel> books;

    public List<BookModel> getBooks() {
        return books;
    }

    public void setBooks(List<BookModel> books) {
        this.books = books;
    }
}
