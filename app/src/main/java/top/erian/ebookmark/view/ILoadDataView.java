package top.erian.ebookmark.view;

import java.util.List;

/**
 * Created by root on 17-4-21.
 */

public interface ILoadDataView<T> {
    void startLoading();

    void loadFailed();

    void loadSuccess(List<T> list);

    void finishLoading();

    void update();
}
