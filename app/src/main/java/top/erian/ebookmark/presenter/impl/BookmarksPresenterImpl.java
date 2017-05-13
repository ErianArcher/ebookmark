package top.erian.ebookmark.presenter.impl;

import java.util.List;

import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.model.impl.BookmarkModel;
import top.erian.ebookmark.presenter.BookmarksPresenter;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.view.ILoadDataView;

/**
 * Created by root on 17-4-30.
 */

public class BookmarksPresenterImpl implements BookmarksPresenter, IGetEntitiesListener<Bookmark> {

    private ILoadDataView<Bookmark> view;

    public BookmarksPresenterImpl(ILoadDataView<Bookmark> view) {
        this.view = view;
    }

    @Override
    public void getBookmarks(String bookName) {
        this.view.startLoading();
        BookmarkModel.getInstance().loadBookmarkEntities(this, bookName);
    }

    @Override
    public void onSuccess(final List<Bookmark> entityList) {
        this.view.finishLoading();
        this.view.loadSuccess(entityList);
    }

    @Override
    public void onError() {
        this.view.finishLoading();
        this.view.loadFailed();
    }
}
