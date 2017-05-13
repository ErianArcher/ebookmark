package top.erian.ebookmark.presenter;

import android.content.Context;

import java.util.List;

import top.erian.ebookmark.model.entity.Book;

/**
 * Created by root on 17-4-21.
 */

public interface IGetEntitiesListener<T> {
    void onSuccess(final List<T> entityList);//get entities
    void onError();
}
