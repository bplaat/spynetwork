package com.example.spynetwork.tasks;

import android.net.Uri;

public interface Task {
    public Uri getUri();

    public boolean isCanceled();

    public boolean isFinished();

    public void cancel();

    public void finish();
}
