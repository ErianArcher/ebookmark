package top.erian.ebookmark.view;

/**
 * Created by root on 17-4-26.
 */

public interface ISaveDataView {
    void startSaving();

    void saveFailed();

    void saveSuccess();

    void saveFinished();
}
