package com.ldg.ireader.db;

import com.ldg.ireader.App;
import com.ldg.ireader.db.entity.DbBookRecord;
import com.ldg.ireader.db.gen.DaoMaster;
import com.ldg.ireader.db.gen.DaoSession;
import com.ldg.ireader.db.gen.DbBookRecordDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

public class DbHelp {

    public static final String DB_NAME = "IREADER_DB.db";
    private final DaoMaster.DevOpenHelper mHelper;
    private final DaoSession mDaoSession;
    private final DaoMaster mDaoMaster;

    private DbHelp() {
        mHelper = new DaoMaster.DevOpenHelper(App.get(), DB_NAME);
        mDaoMaster = new DaoMaster(mHelper.getWritableDb());
        mDaoSession = mDaoMaster.newSession();
    }

    public static DbHelp get() {
        return Holder.HOLDER;
    }

    private static class Holder {
        private static final DbHelp HOLDER = new DbHelp();
    }

    public DbBookRecord queryRecord(String id) {
        return mDaoSession.getDbBookRecordDao()
                .queryBuilder()
                .where(DbBookRecordDao.Properties.Id.eq(id))
                .build()
                .unique();
    }

    public boolean insertBookRecord(DbBookRecord bookRecord) {
        return mDaoSession.insertOrReplace(bookRecord) > 0;
    }


}
