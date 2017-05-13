package top.erian.ebookmark.presenter;

import top.erian.ebookmark.model.entity.Book;

/**
 * Created by root on 17-4-30.
 */

public interface DeleteBookPresenter {
    void deleteBooks(Book... books);
    void deleteBooks(String... booksName);
}
