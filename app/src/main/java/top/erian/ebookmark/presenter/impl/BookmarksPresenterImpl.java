package top.erian.ebookmark.presenter.impl;

import java.util.Arrays;
import java.util.List;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.presenter.BookmarksPresenter;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.view.ILoadDataView;

/**
 * Created by root on 17-4-30.
 */

public class BookmarksPresenterImpl implements BookmarksPresenter, IGetEntitiesListener<Book> {

    private ILoadDataView<Bookmark> view;

    public BookmarksPresenterImpl(ILoadDataView<Bookmark> view) {
        this.view = view;
    }

    @Override
    public void getBookmarks(String bookName) {
        this.view.startLoading();
        BookModel.getInstance().searchBookEntity(this, bookName);
    }

    @Override
    public void onSuccess(List<Book> entityList) {
        this.view.finishLoading();
        this.view.loadSuccess(Arrays.asList(entityList.get(0).getBookmarks()));
    }

    @Override
    public void onError() {
        this.view.finishLoading();
        this.view.loadFailed();
    }
}
