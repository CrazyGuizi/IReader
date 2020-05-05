package com.ldg.ireader.bookshelf.core.loader;

public enum LoadingStatus {
    // 正在加载
    STATUS_LOADING,
    // 加载完成
    STATUS_FINISH,
    // 加载错误 (一般是网络加载情况)
    STATUS_ERROR,
    // 空数据
    STATUS_EMPTY,
    // 正在解析 (装载本地数据)
    STATUS_PARING,
    // 本地文件解析错误(暂未被使用)
    STATUS_PARSE_ERROR,
    // 获取到的目录为空
    STATUS_CATEGORY_EMPTY;
}
