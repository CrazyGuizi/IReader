package com.ldg.ireader.widgets.bottom;

import android.app.Activity;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.BookShelfFragment;
import com.ldg.ireader.explore.ExploreFragment;
import com.ldg.ireader.havealook.HaveALookFragment;
import com.ldg.ireader.mine.MineFragment;
import com.ldg.ireader.utils.Constants;

import java.util.HashMap;

public class TabManager implements BottomTabLayout.OnTabSelectedListener {

    private HashMap<String, Fragment> mFragments = new HashMap<>();

    private FragmentManager mFragmentManager;
    private BottomTabLayout mBottomTabLayout;
    private String mCurKey = "";


    public TabManager(AppCompatActivity activity, BottomTabLayout bottomTabLayout) {
        if (activity == null) {
            return;
        }

        mFragmentManager = activity.getSupportFragmentManager();
        mBottomTabLayout = bottomTabLayout;
    }

    public void init() {
        mBottomTabLayout.setTabSelectedListener(this);
        mBottomTabLayout.init();
    }

    @Override
    public void onTabSelected(String key) {
        selectFragment(key);
    }

    private void selectFragment(String key) {
        if (TextUtils.isEmpty(key) || key.equals(mCurKey)) {
            return;
        }

        Fragment fragment = mFragments.get(key);
        if (fragment == null) {
            fragment = generateFragment(key);
            mFragments.put(key, fragment);
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mFragments.get(mCurKey) != null) {
            transaction.hide(mFragments.get(mCurKey));
        }
        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment);
        }
        transaction.show(fragment).commitAllowingStateLoss();

        mCurKey = key;
    }

    private Fragment generateFragment(String key) {
        Fragment fragment = null;
        if (Constants.TAB_EXPLORE.equals(key)) {
            fragment = ExploreFragment.newInstance(null);
        } else if (Constants.TAB_BOOK_SHELF.equals(key)) {
            fragment = BookShelfFragment.newInstance(null);
        } else if (Constants.TAB_HAVE_A_LOOK.equals(key)) {
            fragment = HaveALookFragment.newInstance(null);
        } else if (Constants.TAB_MINE.equals(key)) {
            fragment = MineFragment.newInstance(null);
        }
        return fragment;
    }
}
