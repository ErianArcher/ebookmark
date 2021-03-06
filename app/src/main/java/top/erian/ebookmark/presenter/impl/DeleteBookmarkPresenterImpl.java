package top.erian.ebookmark.presenter.impl;

import java.util.Date;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.model.impl.BookModel;
import top.erian.ebookmark.model.impl.BookmarkModel;
import top.erian.ebookmark.presenter.DeleteBookmarkPresenter;
import top.erian.ebookmark.presenter.IDeleteEntitiesListener;
import top.erian.ebookmark.view.IDeleteDataView;

/**
 * Created by root on 17-4-30.
 */

public class DeleteBookmarkPresenterImpl implements DeleteBookmarkPresenter, IDeleteEntitiesListener {

    private IDeleteDataView view;

    public DeleteBookmarkPresenterImpl(IDeleteDataView view) {
        this.view = view;
    }

    @Override
    public void deleteBookmark(String bookName, long createDate) {
        Bookmark bookmark = new Bookmark();
        bookmark.setCreateDate(new Date(createDate));
        this.view.startDeleting();
        BookmarkModel.getInstance().deleteBookmarkEntity(this, bookmark, bookName);
    }

    @Override
    public void deleteBookmark(String bookName, Bookmark bookmark) {
        this.view.startDeleting();
        BookmarkModel.getInstance().deleteBookmarkEntity(this, bookmark, bookName);
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
