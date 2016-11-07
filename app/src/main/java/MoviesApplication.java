import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public class MoviesApplication extends Application {

    private static MoviesApplication sInstance;
    private RequestQueue mRequestQueue;

    public synchronized static MoviesApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mRequestQueue = Volley.newRequestQueue(this);
    }
}
