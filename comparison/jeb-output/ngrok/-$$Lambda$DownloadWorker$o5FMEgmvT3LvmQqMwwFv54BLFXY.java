package com.mistral.jon.ngrok;

import android.content.Context;

public final class -..Lambda.DownloadWorker.o5FMEgmvT3LvmQqMwwFv54BLFXY implements Runnable {
    public final String f$0;
    public final String f$1;
    public final boolean f$2;
    public final Context f$3;

    public -..Lambda.DownloadWorker.o5FMEgmvT3LvmQqMwwFv54BLFXY(String s, String s1, boolean z, Context context0) {
        this.f$0 = s;
        this.f$1 = s1;
        this.f$2 = z;
        this.f$3 = context0;
    }

    @Override
    public final void run() {
        DownloadWorker.a(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}

