package top.erian.ebookmark.presenter.impl;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.presenter.SaveBookPresenter;
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
    public void saveBooks(Book book) {
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
