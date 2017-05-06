package top.erian.ebookmark;

import android.app.Application;
import android.content.Context;

/**
 * Created by root on 17-4-24.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public  void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
