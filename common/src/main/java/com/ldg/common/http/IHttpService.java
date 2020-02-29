package com.ldg.common.http;

import android.util.Pair;

import java.util.List;
import java.util.Map;

public interface IHttpService {

    void exec(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params);

    void execSync(HttpMethod method, String url, Map<String, String> headers, List<Pair<String, String>> params);

}
