package top.erian.ebookmark.presenter;

import top.erian.ebookmark.model.entity.Bookmark;

/**
 * Created by root on 17-4-30.
 */

public interface SaveBookmarkPresenter {
    void saveBookmark(String bookName, Bookmark bookmark);
    void saveBookmark(String bookName, long createDate, int currentPage, String note);
}
