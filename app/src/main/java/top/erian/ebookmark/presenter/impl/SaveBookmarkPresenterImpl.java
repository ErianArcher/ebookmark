package top.erian.ebookmark.presenter.impl;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.presenter.SaveBookmarkPresenter;
import top.erian.ebookmark.view.ISaveDataView;

/**
 * Created by root on 17-4-30.
 */

public class SaveBookmarkPresenterImpl implements SaveBookmarkPresenter, ISaveEntitiesListener {

    private ISaveDataView view;

    public SaveBookmarkPresenterImpl(ISaveDataView view) {
        this.view = view;
    }

    @Override
    public void saveBookmark(String bookName, Bookmark bookmark) {
        Book newbook = new Book();
        newbook.setBookName(bookName);
        newbook.addBookmark(bookmark);
        this.view.startSaving();
        BookModel.getInstance().saveBookEntities(this, newbook);
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
