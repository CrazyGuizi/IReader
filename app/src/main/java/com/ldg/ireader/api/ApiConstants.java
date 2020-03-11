package com.ldg.ireader.api;


public class ApiConstants {

    public static final String HOST = "http://127.0.0.1:5000/";

    /************************************* request Code *************************************/

    public static final int REQUEST_GET_MY_BOOK_INFO = 20001;

    public static final int REQUEST_GET_BOOK_DETAIL = 20002;

    public static final int REQUEST_GET_CHAPTER = 20003;

    public static final int REQUEST_GET_CATALOGUE = 20004;

    /************************************* request Url *************************************/

    public static final String URL_GET_MY_BOOK_INFO = HOST + "novel/my";

    public static final String URL_GET_BOOK_DETAIL = HOST + "novel/detail";

    public static final String URL_GET_CHAPTER = HOST + "novel/chapter";

    public static final String URL_GET_CATALOGUE = "novel/catalogue";
}
