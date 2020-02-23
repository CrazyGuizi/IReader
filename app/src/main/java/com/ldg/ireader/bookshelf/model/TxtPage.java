package com.ldg.ireader.bookshelf.model;

import java.util.List;

public class TxtPage {
    public String title;
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines;
    public int position;
}
