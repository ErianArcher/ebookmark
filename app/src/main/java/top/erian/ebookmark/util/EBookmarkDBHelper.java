package top.erian.ebookmark.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 17-4-22.
 */

public class EBookmarkDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ebookmark.db";

    public static final String CREATE_BOOKS = new String("CREATE TABLE IF NOT EXISTS books" +
            " (bookName CHAR(50) PRIMARY KEY NOT NULL," +
            "cover BLOB," +
            "page INTEGER NOT NULL CHECK(page >= 0) DEFAULT 0)");

    public static final String CREATE_BOOKMARKS = new String("CREATE TABLE IF NOT EXISTS bookmarks" +
            " (createDate INTEGER PRIMARY KEY NOT NULL, " +
            "bookName CHAR(50) NOT NULL, " +
            "currentPage INTEGER NOT NULL DEFAULT 0 CHECK(currentPage >= 0), " +
            "note TEXT," +
            "FOREIGN KEY(bookName) REFERENCES books(bookName))");

    public EBookmarkDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOKS);
        db.execSQL(CREATE_BOOKMARKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db);
    }
}
