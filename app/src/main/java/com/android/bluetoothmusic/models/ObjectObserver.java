package com.android.bluetoothmusic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ObjectObserver extends RecordObserver<ObjectObserver> {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("observer")
    @Expose
    private ObjectObserver objectObserver;
    @SerializedName("songs_length")
    @Expose
    private int songsLength;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjectObserver getObjectObserver() {
        return objectObserver;
    }

    public void setObjectObserver(ObjectObserver objectObserver) {
        this.objectObserver = objectObserver;
    }

    public int getSongsLength() {
        return songsLength;
    }

    public void setSongsLength(int songsLength) {
        this.songsLength = songsLength;
    }
}
