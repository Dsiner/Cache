package com.d.lib.cache.component.imageloader.fetcher;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.d.lib.cache.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches an {@link InputStream} using the HttpUrlConnection library.
 */
public class HttpStreamFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "OkHttpFetcher";
    private final String url;
    private InputStream stream;
    private DataCallback<? super InputStream> callback;
    // call may be accessed on the main thread while the object is in use on other threads. All other
    // accesses to variables may occur on different threads, but only one at a time.
    private volatile HttpURLConnection call;

    // Public API.
    @SuppressWarnings("WeakerAccess")
    public HttpStreamFetcher(String url) {
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority,
                         @NonNull final DataCallback<? super InputStream> callback) {
        this.callback = callback;
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(url, "GET");
            this.call = conn;
            conn.setDoInput(true);
            conn.connect();
            onResponse(conn);
        } catch (Exception e) {
            if (conn != null) {
                conn.disconnect();
            }
            onFailure(e);
        }
    }

    public void onFailure(@NonNull Exception e) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "OkHttp failed to obtain result", e);
        }

        callback.onLoadFailed(e);
    }

    public void onResponse(@NonNull HttpURLConnection response) {
        int code = 0;
        String message = null;
        InputStream inputStream = null;
        try {
            code = response.getResponseCode();
            message = response.getResponseMessage();
            inputStream = response.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (code >= 200 && code < 300) {
            long contentLength = Preconditions.checkNotNull(response).getContentLength();
            stream = ContentLengthInputStream.obtain(inputStream, contentLength);
            callback.onDataReady(stream);
        } else {
            callback.onLoadFailed(new NetworkErrorException(message + " Code: " + code));
        }
    }

    private HttpURLConnection getHttpURLConnection(String requestURL, String requestMethod)
            throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10 * 1000);
        conn.setReadTimeout(10 * 1000);
        conn.setRequestMethod(requestMethod);
        return conn;
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // Ignored
        }
        callback = null;
    }

    @Override
    public void cancel() {
        HttpURLConnection local = call;
        if (local != null) {
            local.disconnect();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}

