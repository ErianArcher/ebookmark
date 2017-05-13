package top.erian.ebookmark.model;

import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.IDeleteEntitiesListener;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;

/**
 * Created by root on 17-5-7.
 */

public interface IBookmarkModel {
    void loadBookmarkEntities(IGetEntitiesListener listener, String bookName);
    void saveBookmarkEntity(ISaveEntitiesListener listener, Bookmark newbookmark, String bookName);
    void deleteBookmarkEntity(IDeleteEntitiesListener listener, Bookmark delbookmark, String bookName);
}
