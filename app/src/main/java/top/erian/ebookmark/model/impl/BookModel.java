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
import top.erian.ebookmark.R;
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
    protected boolean latest;

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

        new Handler().post(new Runnable() {
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

                            /* Retrieve the bookmarks of this book, and get the latest currentPage*/
                            Cursor c_bm = db.rawQuery("SELECT * FROM " + BOOKMARKS +
                                    " WHERE bookName = ? " +
                                    "ORDER BY createDate DESC", new String[] {newBook.getBookName()});
                            if (c_bm.moveToFirst()) {
                                int currentPageTemp = c_bm.getInt(c_bm.getColumnIndex
                                                ("currentPage"));
                                newBook.setCurrentPage(currentPageTemp > newBook.getPage()?
                                newBook.getPage():currentPageTemp);
                            } else {
                                // The situation that this book haven't got its bookmark added,
                                // the table "bookmarks" is empty
                                newBook.setCurrentPage(0);
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
        });

    }

    @Override
    synchronized public void saveBookEntities(final ISaveEntitiesListener listener, final Book... newbooks) {
        //Check if the new book is duplicated
        //If the data is not latest
        if (!this.isLatest()) {
            //Just call this function to update "books"
            this.loadBookEntities(FAKELISTENER);
        }
        
        new Handler().post(new Runnable() {
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

                        values = null; //Garbage collection
                        latest = false; //Database is changed
                        listener.onSuccess();
                    } else /*The second case: the book is partly changed*/{
                        Book bookDB = books.get(index);
                        ContentValues values = new ContentValues();
                        if (!bookDB.samePage(b)) {
                            if (!b.isDefaultPage()) values.put("page", b.getPage());
                        }
                        if (!bookDB.sameCover(b)) {
                            if (!b.isDefaultCover()) values.put("cover", bitmap2Blob(b.getCover()));
                        }
                        if (values.size() > 0) {
                            db.update(BOOKS, values, "bookName = ?", new String[]{bookDB.getBookName()});
                            latest = false; //Database is changed
                        }
                        values.clear();

                        values = null; //Garbage collection

                    }
                }
                
                db.close();
                eBookmarkDBHelper.close();
            }
        });
    }

    @Override
    synchronized public void deleteBookEntities(final IDeleteEntitiesListener listener, final Book... delbooks) {
        //If the data is not latest
        if (!this.isLatest()) {
            //Just call this function to update "books"
            this.loadBookEntities(FAKELISTENER);
        }

        new Handler().post(new Runnable() {
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
                        // This book differs from the book in db with same name in some part,
                        // This program classifies this book as a book to be deleted.
                        // Delete the bookmarks of this book from database
                        db.delete(BOOKMARKS, "bookName = ?",
                                new String[] {bookDB.getBookName()});
                        // Delete this book from database.
                        db.delete(BOOKS, "bookName = ?",
                                new String[] {bookDB.getBookName()});
                        // Update the books stored in memory
                        books.remove(index);

                        bookDB = null; // Garbage Collection
                        latest = false;
                        listener.onSuccess();
                    }
                }

                db.close();
                eBookmarkDBHelper.close();
            }
        });
    }

    @Override
    synchronized public void searchBookEntity(final IGetEntitiesListener listener, final String bookName) {
        //If the data is not latest
        if (!this.isLatest()) {
            //Just call this function to update "books"
            this.loadBookEntities(FAKELISTENER);
        }

        new Handler().post(new Runnable() {
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
        });
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
        if (blob == null) return BitmapFactory
                .decodeResource(MyApplication.getContext().getResources(), R.drawable.ic_tick);
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }
}
