package top.erian.ebookmark.model;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.presenter.IDeleteEntitiesListener;
import top.erian.ebookmark.presenter.IGetEntitiesListener;
import top.erian.ebookmark.presenter.ISaveEntitiesListener;
import top.erian.ebookmark.view.IDeleteDataView;

/**
 * Created by root on 17-4-21.
 */

public interface IBookModel {
    void loadBookEntities(IGetEntitiesListener listener);
    void saveBookEntities(ISaveEntitiesListener listener, Book... newbooks);
    void deleteBookEntities(IDeleteEntitiesListener listener, Book... delbooks);
    void searchBookEntity(IGetEntitiesListener listener, String bookName);
}
