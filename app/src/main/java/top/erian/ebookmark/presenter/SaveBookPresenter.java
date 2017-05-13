package top.erian.ebookmark.presenter;

import android.graphics.Bitmap;

import top.erian.ebookmark.model.entity.Book;

/**
 * Created by root on 17-4-30.
 */

public interface SaveBookPresenter {
    void saveBooks(Book book);
    void saveBooks(String bookName, Bitmap cover, int page);
}
