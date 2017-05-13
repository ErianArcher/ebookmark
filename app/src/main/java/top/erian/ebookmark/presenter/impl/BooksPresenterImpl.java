package top.erian.ebookmark.presenter.impl;

import java.util.List;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.presenter.BooksPresenter;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.view.IDeleteDataView;
import top.erian.ebookmark.view.ILoadDataView;
import top.erian.ebookmark.view.ISaveDataView;

/**
 * Created by root on 17-4-28.
 */

public class BooksPresenterImpl implements BooksPresenter, IGetEntitiesListener<Book> {

    private ILoadDataView<Book> view;


    public BooksPresenterImpl(ILoadDataView<Book> view) {
        this.view = view;
    }

    @Override
    public void getBooks() {
        view.startLoading();
        BookModel.getInstance().loadBookEntities(this);
    }

    @Override
    public void getBook(String bookName) {
        this.view.startLoading();
        BookModel.getInstance().searchBookEntity(this, bookName);
    }

    @Override
    public void onSuccess(final List<Book> bookList) {
        this.view.finishLoading();
        this.view.loadSuccess(bookList);
    }

    @Override
    public void onError() {
        this.view.finishLoading();
        this.view.loadFailed();
    }
}
