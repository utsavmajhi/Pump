package com.huxq17.download.core.connection;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;


public interface DownloadConnection {
    void addHeader(String key, String value);

    String getHeader(String key);

    void connect() throws IOException;

    void connect(@NonNull String method) throws IOException;

    void prepareDownload(File file) throws IOException;

    int downloadBuffer(byte[] buffer) throws IOException;

    void flushDownload() throws IOException;

    int getResponseCode();

    boolean isSuccessful();

    void close();

    void cancel();

    boolean isCanceled();

    interface Factory {
        DownloadConnection create(String url);
    }
}
