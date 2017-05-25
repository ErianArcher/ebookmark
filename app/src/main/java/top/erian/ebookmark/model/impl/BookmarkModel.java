package top.erian.ebookmark.model.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import top.erian.ebookmark.MyApplication;
import top.erian.ebookmark.model.IBookmarkModel;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.IDeleteEntitiesListener;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.util.EBookmarkDBHelper;

/**
 * Created by root on 17-5-7.
 */

public class BookmarkModel implements IBookmarkModel {

    /***
     * BookmarkModel is a singleton, aimed at reducing too much unused BookmarkModel
     */
    private static final BookmarkModel INSTANCE = new BookmarkModel();
    private BookmarkModel() {}
    public static BookmarkModel getInstance() {return INSTANCE;}

    // Table name of ebookmark.db
    public static final String BOOKS = new String("books");
    public static final String BOOKMARKS = new String("bookmarks");

    @Override
    public void loadBookmarkEntities(final IGetEntitiesListener listener, final String bookName) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                EBookmarkDBHelper eBookmarkDBHelper = null;
                ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();

                try {
                    eBookmarkDBHelper = new EBookmarkDBHelper(MyApplication.getContext());
                    SQLiteDatabase db = eBookmarkDBHelper.getReadableDatabase();

                    Log.d("Bookname", "BookmarkModel run: " + bookName);
                    Cursor cursor = db.rawQuery("SELECT  * FROM " + BOOKMARKS +
                                                " WHERE bookName = ? " +
                                                "ORDER BY createDate DESC",
                                                new String[] {bookName});
                    if (cursor.moveToFirst()) {
                        do {
                            Bookmark bm = new Bookmark();
                            bm.setCreateDate(new Date(cursor.getLong(cursor.getColumnIndex
                                    ("createDate"))));
                            bm.setCurrentPage(cursor.getInt(cursor.getColumnIndex
                                    ("currentPage")));
                            bm.setNote(cursor.getString(cursor.getColumnIndex
                                    ("note")));
                            bookmarks.add(bm);
                        } while (cursor.moveToNext());
                        listener.onSuccess(bookmarks);
                    } else listener.onError();
                } catch (Exception e) {
                    listener.onError();
                } finally {
                    eBookmarkDBHelper.close();
                }
            }
        });
    }

    @Override
    public void saveBookmarkEntity(final ISaveEntitiesListener listener, final Bookmark newbookmark, final String bookName) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                EBookmarkDBHelper eBookmarkDBHelper = null;

                try {
                    eBookmarkDBHelper = new EBookmarkDBHelper(MyApplication.getContext());
                    boolean isInDB = checkItemInDB(eBookmarkDBHelper.getReadableDatabase(),
                            newbookmark, bookName);
                    SQLiteDatabase db = eBookmarkDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    if (isInDB) { // The case that the bookmark is in the database
                        // Do update query here
                        values.put("currentPage", newbookmark.getCurrentPage());
                        values.put("note", newbookmark.getNote());
                        db.update(BOOKMARKS, values,
                                "WHERE createDate = ? AND bookName = ?",
                                new String[] {String.valueOf(getCreateDate(newbookmark)), bookName});
                    } else { // The case that the bookmark is not in the database
                        // Do insert query here
                        values.put("createDate", String.valueOf(getCreateDate(newbookmark)));
                        values.put("bookName", bookName);
                        values.put("currentPage", String.valueOf(newbookmark.getCurrentPage()));
                        values.put("note", newbookmark.getNote());
                        db.insert(BOOKMARKS, null, values);
                    }
                    BookModel.getInstance().latest = false;
                    listener.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError();
                } finally {
                    eBookmarkDBHelper.close();
                }
            }
        });
    }

    @Override
    public void deleteBookmarkEntity(final IDeleteEntitiesListener listener, final Bookmark delbookmark, final String bookName) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                EBookmarkDBHelper eBookmarkDBHelper = null;

                try {
                    eBookmarkDBHelper = new EBookmarkDBHelper(MyApplication.getContext());
                    boolean inDB = checkItemInDB(eBookmarkDBHelper.getReadableDatabase(),
                            delbookmark, bookName);// Check if there exist such a bookmark in db
                    SQLiteDatabase db = eBookmarkDBHelper.getWritableDatabase();

                    if (inDB) {
                        // Do delete query
                        db.delete(BOOKMARKS, "createDate = ? AND bookName = ?",
                                new String[] {String.valueOf(getCreateDate(delbookmark)),
                                bookName});
                        BookModel.getInstance().latest = false;
                        listener.onSuccess();
                    } else // There does not exist such a bookmark in db, report error
                        listener.onError();
                } catch (Exception e){
                    listener.onError();
                    e.printStackTrace();
                } finally {
                    eBookmarkDBHelper.close();
                }
            }
        });
    }

    private boolean checkItemInDB (SQLiteDatabase readableDB, Bookmark bookmark, String bookName) {
        //Log.d("createDate", "checkItemInDB: " + bookmark.getCreateDate());
        Cursor cursor = readableDB.rawQuery("SELECT * FROM " + BOOKMARKS +
                " WHERE bookName = ? AND createDate = ?",
                new String[] { bookName, String.valueOf(getCreateDate(bookmark))});
        int rowCount = cursor.getCount();
        cursor.close();
        readableDB.close();

        if (rowCount == 0) return false;
        return true;
    }

    private long getCreateDate(Bookmark bm) {
        return bm.getCreateDate().getTime();
    }
}
