package com.satiate.movies.interfaces;

import com.satiate.movies.network.WebResponse;

/**
 * Created by Rishabh Bhatia on 8/11/16.
 */

public interface IAsyncCallback {
    void onComplete(WebResponse responseContent);
    void onError(String errorData);
}