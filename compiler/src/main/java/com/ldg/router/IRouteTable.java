package com.ldg.router;

import java.util.Map;

/**
 * created by gui 2020/8/9
 */
public interface IRouteTable {
    void handleTable(Map<String, Class<?>> map);
}
