package com.ldg.ireader.bookshelf.model;

import java.io.Serializable;

public class BookModel implements Serializable {
    private static final long serialVersionUID = -935820639380721547L;

    private boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
