package com.d.lib.cache.component.fetcher;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.d.lib.cache.utils.Preconditions;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Fetches an {@link InputStream} using the okhttp library.
 */
public class OkHttpStreamFetcher implements DataFetcher<InputStream>, okhttp3.Callback {
    private static final String TAG = "OkHttpFetcher";
    private final Call.Factory client;
    private final String url;
    private InputStream stream;
    private ResponseBody responseBody;
    private DataCallback<? super InputStream> callback;
    // call may be accessed on the main thread while the object is in use on other threads. All other
    // accesses to variables may occur on different threads, but only one at a time.
    private volatile Call call;

    private static volatile Call.Factory internalClient;

    private static Call.Factory getInternalClient() {
        if (internalClient == null) {
            synchronized (OkHttpStreamFetcher.class) {
                if (internalClient == null) {
                    internalClient = new OkHttpClient();
                }
            }
        }
        return internalClient;
    }

    // Public API.
    @SuppressWarnings("WeakerAccess")
    public OkHttpStreamFetcher(String url) {
        this.client = getInternalClient();
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority,
                         @NonNull final DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        Request request = requestBuilder.build();
        this.callback = callback;

        call = client.newCall(request);
        call.enqueue(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "OkHttp failed to obtain result", e);
        }

        callback.onLoadFailed(e);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        responseBody = response.body();
        if (response.isSuccessful()) {
            long contentLength = Preconditions.checkNotNull(responseBody).contentLength();
            stream = ContentLengthInputStream.obtain(responseBody.byteStream(), contentLength);
            callback.onDataReady(stream);
        } else {
            callback.onLoadFailed(new NetworkErrorException(response.message() + " Code: " + response.code()));
        }
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
        if (responseBody != null) {
            responseBody.close();
        }
        callback = null;
    }

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}

