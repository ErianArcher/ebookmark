package top.erian.ebookmark.presenter;

import top.erian.ebookmark.model.entity.Book;
import top.erian.ebookmark.view.ILoadDataView;

/**
 * Created by root on 17-4-28.
 */

public interface BooksPresenter {
    void getBooks();
    void getBook(String bookName);
}
