package com.ldg.ireader.bookshelf.ui;

import com.ldg.common.view.activity.BaseActivity;
import com.ldg.ireader.R;
import com.ldg.ireader.bookshelf.core.draw.IPageController;
import com.ldg.ireader.bookshelf.core.draw.ReadPageManager;
import com.ldg.ireader.bookshelf.core.loader.PageLoader;
import com.ldg.ireader.bookshelf.core.widgets.PageView;
import com.ldg.ireader.bookshelf.model.BookModel;

public class ReadActivity extends BaseActivity {

    private PageView mPageView;
    private IPageController mPageController;

    @Override
    public void doBeforeInit() {

    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_read;
    }

    @Override
    public void initWidgets() {
        mPageView = findViewById(R.id.page_view);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doAfterInit() {
        mPageController = new ReadPageManager(new BookModel());
        mPageView.setPageController(mPageController);
    }
}
