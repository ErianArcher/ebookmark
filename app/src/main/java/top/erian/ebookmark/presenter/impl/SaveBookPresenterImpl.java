package top.erian.ebookmark.presenter.impl;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.presenter.SaveBookPresenter;
import top.erian.ebookmark.util.ImageOperation;
import top.erian.ebookmark.view.ISaveDataView;

/**
 * Created by root on 17-4-30.
 */

public class SaveBookPresenterImpl implements SaveBookPresenter, ISaveEntitiesListener {

    private ISaveDataView view;

    public SaveBookPresenterImpl(ISaveDataView view) {
        this.view = view;
    }

    @Override
    public void saveBooks(final Book book) {
        book.setCover(ImageOperation.compressBitmap(book.getCover()));
        //Log.d("Cover size", "saveBooks: " + book.getCover().getByteCount()/1024 + " bytes");
        this.view.startSaving();
        BookModel.getInstance().saveBookEntities(this, book);
    }

    @Override
    public void saveBooks(String bookName, Bitmap cover, int page) {
        Book book = new Book();
        book.setBookName(bookName);
        book.setPage(page);
        book.setCover(ImageOperation.compressBitmap(cover));
        //Log.d("Cover size", "saveBooks: " + book.getCover().getByteCount()/1024 + " bytes");
        this.view.startSaving();
        BookModel.getInstance().saveBookEntities(this, book);
    }

    @Override
    public void onSuccess() {
        this.view.saveFinished();
        this.view.saveSuccess();
    }

    @Override
    public void onError() {
        this.view.saveFinished();
        this.view.saveFailed();
    }
}
