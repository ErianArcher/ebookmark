package top.erian.ebookmark.model.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import top.erian.ebookmark.MyApplication;
import top.erian.ebookmark.model.IBookModel;
import top.erian.ebookmark.presenter.IDeleteEntitiesListener;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.util.EBookmarkDBHelper;

/**
 * Created by root on 17-4-21.
 */

public class BookModel implements IBookModel {

    /***
     * BookModel is a singleton, aimed at reducing too much unused BookModel
     */
    private List<Book> books;
    private boolean latest;

    public static final String BOOKS = new String("books");
    public static final String BOOKMARKS = new String("bookmarks");
    public static final IGetEntitiesListener FAKELISTENER = new IGetEntitiesListener<Book>() {
        @Override
        public void onSuccess(List<Book> list) {
            //Do nothing
        }

        @Override
        public void onError() {
            //Do nothing
        }
    };

    private static final BookModel INSTANCE = new BookModel();
    private BookModel() {books = new ArrayList<Book>(); latest = false;}
    public static BookModel getInstance() {return INSTANCE;}

    @Override
    synchronized public void loadBookEntities(final IGetEntitiesListener listener) {

        if(latest) {
            // If the data store in memory is latest data, then load this.books.
            listener.onSuccess(books);
            return;
        }

        new Handler().postDelayed(new Runnable() {
            // Get Book from SQLite
            @Override
            public void run() {
                EBookmarkDBHelper eBookmarkDBHelper = null;
                if (books != null) books.clear(); // Clear the original data

                try {
                    eBookmarkDBHelper = new EBookmarkDBHelper(MyApplication.getContext());
                    SQLiteDatabase db = eBookmarkDBHelper.getReadableDatabase();

                    Cursor cursor = db.rawQuery(
                            "SELECT * FROM " + BOOKS, null);

                    if (cursor.moveToFirst()) {
                        do {
                            Book newBook = new Book();
                            newBook.setBookName(cursor.getString(cursor.getColumnIndex
                                    ("bookName")));
                            newBook.setCover(blob2Bitmap(cursor.getBlob(cursor.getColumnIndex
                                    ("cover"))));
                            newBook.setPage(cursor.getInt(cursor.getColumnIndex
                                    ("page")));

                            /* Retrieve the bookmarks of this book*/
                            Cursor c_bm = db.rawQuery("SELECT * FROM " + BOOKMARKS +
                                    " WHERE bookName = ? " +
                                    "ORDER BY createDate DESC", new String[] {newBook.getBookName()});

                            if (c_bm.moveToFirst()) {
                                do {
                                    Bookmark newBM = new Bookmark();
                                    newBM.setCreateDate(new Date(c_bm.getLong(c_bm.getColumnIndex
                                            ("createDate"))));
                                    newBM.setCurrentPage(c_bm.getInt(c_bm.getColumnIndex
                                            ("currentPage")));
                                    newBM.setNote(c_bm.getString(c_bm.getColumnIndex
                                            ("note")));
                                    newBook.addBookmark(newBM);
                                } while (cursor.moveToNext());
                            }
                            c_bm.close();

                            books.add(newBook);
                        } while (cursor.moveToNext());
                    }
                    latest = true;//After successfully loading data from database, set the flag to true.

                    listener.onSuccess(books);
                    cursor.close();
                    db.close();
                } catch (Exception e) {
                    //toast an error message to activity
                    /*Toast.makeText(MyApplication.getContext(),
                            "Cannot load the information of books from database.",
                            Toast.LENGTH_LONG).show();*/
                    e.printStackTrace();
                    listener.onError();
                    latest = false;
                } finally {
                    eBookmarkDBHelper.close();
                }
            }
        }, 3000);

    }

    @Override
    synchronized public void saveBookEntities(final ISaveEntitiesListener listener, final Book... newbooks) {
        //Check if the new book is duplicated
        //If the data is not latest
        if (!this.isLatest()) {
            //Just call this function to update "books"
            this.loadBookEntities(FAKELISTENER);
        }
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EBookmarkDBHelper eBookmarkDBHelper = new EBookmarkDBHelper(MyApplication.getContext());
                SQLiteDatabase db = eBookmarkDBHelper.getWritableDatabase();

                for (Book b:
                     newbooks) {
                    int index = books.indexOf(b);
                    //The first case: the book is totally new.
                    if (index == -1) {
                        ContentValues values = new ContentValues();
                        values.put("bookName", b.getBookName());
                        values.put("page", b.getPage());
                        values.put("cover", bitmap2Blob(b.getCover()));
                        db.insert(BOOKS, null, values);
                        values.clear();

                        if (b.noBookmarks() == false) {
                            for (Bookmark bm :
                                    b.getBookmarks()) {
                                values.put("createDate", bm.getCreateDate().getTime());
                                values.put("bookName", b.getBookName());
                                values.put("currentPage", bm.getCurrentPage());
                                values.put("note", bm.getNote());
                                db.insert(BOOKMARKS, null, values);
                                values.clear();
                            }
                        }

                        values = null; //Garbage collection
                        latest = false; //Database is changed
                        listener.onSuccess();
                    } else /*The second case: the book is partly changed*/{
                        Book bookDB = books.get(index);
                        ContentValues values = new ContentValues();
                        if (!bookDB.samePage(b)) {values.put("page", b.getPage());}
                        if (!bookDB.sameCover(b)) {values.put("cover", bitmap2Blob(b.getCover()));}
                        if (values.size() > 0) {
                            db.update(BOOKS, values, "bookName = ?", new String[]{bookDB.getBookName()});
                            latest = false; //Database is changed
                        }
                        values.clear();

                        if (!bookDB.sameBookmark(b)) {
                            latest = false; //Database is changed
                            //Update BOOKMARKS table
                            List<Bookmark> bookmarksDB = Arrays.asList(bookDB.getBookmarks());
                            for (Bookmark bm:
                                 b.getBookmarks()) {
                                int bmIndex = bookmarksDB.indexOf(bm);
                                if (bmIndex == -1) {// The first case: a totally new bookmark

                                    values.put("createDate", bm.getCreateDate().getTime());
                                    values.put("currentPage", bm.getCurrentPage());
                                    values.put("bookName", bookDB.getBookName());
                                    values.put("note", bm.getNote());
                                    db.insert(BOOKMARKS, null, values);

                                } else {// The second case: the bookmark exists

                                    Bookmark bookmarkDB = bookmarksDB.get(bmIndex);

                                    if (!bookmarkDB.sameCurrentPage(bm)) {
                                        values.put("currentPage", bm.getCurrentPage());
                                    }
                                    if (!bookmarkDB.sameNote(bm)) {
                                        values.put("note", bm.getNote());
                                    }
                                    //Update bookmark
                                    db.update(BOOKMARKS, values, "bookName = ?",
                                            new String[] {bookDB.getBookName()});
                                    if (values.size() < 1) listener.onError();
                                }
                                values.clear();
                            }
                        } else if(latest == true){
                            // bookDB.sameBookmark and latest == true means two book is exactly the same
                            // And it is inferred that is an existing book in database, and it cannot be inserted
                            listener.onError();
                        }

                        values = null; //Garbage collection

                    }
                }
                
                db.close();
                eBookmarkDBHelper.close();
            }
        }, 3000);
    }

    @Override
    synchronized public void deleteBookEntities(final IDeleteEntitiesListener listener, final Book... delbooks) {
        //If the data is not latest
        if (!this.isLatest()) {
            //Just call this function to update "books"
            this.loadBookEntities(FAKELISTENER);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EBookmarkDBHelper eBookmarkDBHelper = new EBookmarkDBHelper(MyApplication.getContext());
                SQLiteDatabase db = eBookmarkDBHelper.getWritableDatabase();

                for (Book b:
                     delbooks) {
                    int index = books.indexOf(b);
                    if (index == -1){
                        listener.onError(); // There does not exist a book with the same name in db
                    } else {
                        Book bookDB = books.get(index);
                        if (bookDB.samePage(b) &&
                                bookDB.sameCover(b)) {
                            if (!bookDB.sameBookmark(b)) {
                                /*If the bookmarks are not the same,
                                that means one or some of the bookmarks are deleted*/
                                List<Bookmark> bookmarksDB = Arrays.asList(bookDB.getBookmarks());
                                List<Bookmark> bookmarks_del = Arrays.asList(b.getBookmarks());
                                for (Bookmark bm:
                                     bookmarksDB) {
                                    int bmIndex = bookmarks_del.indexOf(bm);
                                    if (bmIndex == -1) {
                                        // This means the bookmark does not need to be deleted
                                        // Because it is not in the list of bookmarks that need to be deleted
                                    } else {

                                        db.delete(BOOKMARKS, "createDate = ?",
                                                new String[]{String.valueOf(bm.getCreateDate())});

                                        bookmarksDB.remove(bm);
                                    }
                                }
                                // Update the books stored in memory
                                bookDB.replaceBookmarks(bookmarksDB.toArray(new Bookmark[1]));
                                // Garbage Collection
                                bookmarksDB = null;
                                bookmarks_del = null;
                            } else {
                                //Delete this book from database and memory (all information is the same)
                                db.delete(BOOKS, "bookName = ?",
                                        new String[] {bookDB.getBookName()});
                                // Update the books stored in memory
                                books.remove(index);
                            }
                        } else {
                            // This book differs from the book in db with same name in some part,
                            // This program classifies this book as a book to be deleted.
                            // Delete this book from database.
                            db.delete(BOOKS, "bookName = ?",
                                    new String[] {bookDB.getBookName()});
                            // Update the books stored in memory
                            books.remove(index);
                        }

                        bookDB = null; // Garbage Collection
                    }
                }

                db.close();
                eBookmarkDBHelper.close();
            }
        }, 3000);
    }

    @Override
    synchronized public void searchBookEntity(final IGetEntitiesListener listener, final String bookName) {
        //If the data is not latest
        if (!this.isLatest()) {
            //Just call this function to update "books"
            this.loadBookEntities(FAKELISTENER);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Book tar = new Book();
                tar.setBookName(bookName);
                int index = books.indexOf(tar);
                tar = null; //Garbage collection
                if (index == -1) {
                    listener.onError();
                } else {
                    listener.onSuccess(Arrays.asList(books.get(index)));
                }
            }
        }, 3000);
    }

    public boolean isLatest(){return latest;}

    //convert a bitmap to a blob MIME in order to store bitmap in SQLite
    private byte[] bitmap2Blob (Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();
        }
        return null;
    }

    //convert blob data from SQLite to bitmap
    private Bitmap blob2Bitmap (byte[] blob) {
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }
}
