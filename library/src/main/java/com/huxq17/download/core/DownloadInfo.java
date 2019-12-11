package com.huxq17.download.core;


import java.io.File;

public class DownloadInfo {
    protected final String url;
    private final File downloadFile;
    protected final String id;

    private final long completedSize;
    private final long contentLength;
    private final int finished;
    private final Status status;
    private final String speed;
    private final int errorCode;
    private final String tag;
    private final long createTime;

    private DownloadDetailsInfo downloadDetailsInfo;

    DownloadInfo(String url, File downloadFile, String tag, String id, long createTime,
                 String speed, long completedSize, long contentLength, int errorCode,
                 Status status, int finished, DownloadDetailsInfo downloadDetailsInfo) {
        this.url = url;
        this.downloadFile = downloadFile;
        this.tag = tag;
        this.id = id;
        this.createTime = createTime;
        this.speed = speed;
        this.completedSize = completedSize;
        this.contentLength = contentLength;
        this.errorCode = errorCode;
        this.status = status;
        this.finished = finished;
        this.downloadDetailsInfo = downloadDetailsInfo;
    }

    DownloadDetailsInfo getDownloadDetailsInfo() {
        return downloadDetailsInfo;
    }

    public void setExtraData(Object extraData) {
        downloadDetailsInfo.setExtraData(extraData);
    }

    public Object getExtraData() {
        return downloadDetailsInfo.getWfExtraData();
    }

    public String getSpeed() {
        return speed;
    }

    public String getTag() {
        return tag == null ? "" : tag;
    }

    public String getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getUrl() {
        return url;
    }

    public String getFilePath() {
        return downloadFile != null ? downloadFile.getPath() : null;
    }

    public String getName() {
        return downloadFile == null ? "" : downloadFile.getName();
    }

    public long getCompletedSize() {
        return completedSize;
    }

    public long getContentLength() {
        return contentLength;
    }

    public int getProgress() {
        return contentLength == 0 ? 0 : (int) (completedSize * 1f / contentLength * 100);
    }

    public int getFinished() {
        return finished;
    }

    public boolean isFinished() {
        return finished == 1;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        STOPPED, WAIT, PAUSED, PAUSING, RUNNING, FINISHED, FAILED;

        public boolean isRunning() {
            return this == WAIT || this == RUNNING;
        }
    }
}