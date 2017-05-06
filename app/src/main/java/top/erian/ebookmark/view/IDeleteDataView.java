package top.erian.ebookmark.view;

/**
 * Created by root on 17-4-26.
 */

public interface IDeleteDataView {
    void startDeleting();

    void deleteFailed();

    void deleteSuccess();

    void deleteFinished();
}
