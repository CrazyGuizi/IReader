package com.ldg.common.util;

import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PairUtils {

    private List<Pair<String, String>> mPairList = new ArrayList<>();

    public static final PairUtils init() {
        return new PairUtils();
    }

    public final PairUtils add(String key, String val) {
        if (!TextUtils.isEmpty(key)) {
            mPairList.add(new Pair<>(key, val));
        }
        return this;

    } public final PairUtils add(String key, int val) {
        if (!TextUtils.isEmpty(key)) {
            mPairList.add(new Pair<>(key, String.valueOf(val)));
        }
        return this;
    }

    public List<Pair<String, String>> build() {
        return mPairList;
    }
}
