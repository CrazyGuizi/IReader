package com.ldg.common.subscribe;

import java.util.ArrayList;
import java.util.List;

public class BaseObserver<T> {
    protected List<T> mObservables = new ArrayList<>();

    public boolean isRegister(T t) {
        return mObservables != null && mObservables.contains(t);
    }

    public void regist(T t) {
        if (t == null) {
            throw new IllegalArgumentException("the observer is null");
        }

        if (isRegister(t)) {
            throw new IllegalArgumentException("you have register");
        }

        if (mObservables == null) {
            mObservables = new ArrayList<>();
        }

        synchronized (mObservables) {
            mObservables.add(t);
        }
    }

    public void unregist(T t) {
        if (t == null) {
            throw new IllegalArgumentException("the observer is null");
        }
        int index;
        synchronized (mObservables) {
            if (mObservables != null && (index = mObservables.indexOf(t)) > 0) {
                if (index > 0) {
                    mObservables.remove(index);
                }
            }
        }
    }

    public void unregistAll() {
        synchronized (mObservables) {
            if (mObservables != null) {
                mObservables.clear();
            }
        }
    }
}
