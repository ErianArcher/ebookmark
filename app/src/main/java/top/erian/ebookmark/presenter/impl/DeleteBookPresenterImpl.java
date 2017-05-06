package top.erian.ebookmark.presenter.impl;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.presenter.DeleteBookPresenter;
import top.erian.ebookmark.presenter.IDeleteEntitiesListener;
import top.erian.ebookmark.view.IDeleteDataView;

/**
 * Created by root on 17-4-30.
 */

public class DeleteBookPresenterImpl implements DeleteBookPresenter, IDeleteEntitiesListener {

    private IDeleteDataView view;

    public DeleteBookPresenterImpl(IDeleteDataView view) {
        this.view = view;
    }

    @Override
    public void deleteBooks(Book... books) {
        this.view.startDeleting();
        BookModel.getInstance().deleteBookEntities(this, books);
    }

    @Override
    public void onSuccess() {
        this.view.deleteFinished();
        this.view.deleteSuccess();
    }

    @Override
    public void onError() {
        this.view.deleteFinished();
        this.view.deleteFailed();
    }
}
