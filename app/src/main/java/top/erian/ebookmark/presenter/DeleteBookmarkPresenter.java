package top.erian.ebookmark.presenter;

import top.erian.ebookmark.model.entity.Bookmark;

/**
 * Created by root on 17-4-30.
 */

public interface DeleteBookmarkPresenter {
    void deleteBookmark(String bookName, long createDate);
    void deleteBookmark(String bookName, Bookmark bookmark);
}
