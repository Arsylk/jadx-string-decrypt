package com.mistral.jon.ngrok;

import android.content.Context;

/* JADX INFO: renamed from: com.mistral.jon.ngrok.-$$Lambda$DownloadWorker$o5FMEgmvT3LvmQqMwwFv54BLFXY */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$DownloadWorker$o5FMEgmvT3LvmQqMwwFv54BLFXY implements Runnable {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Context f$3;

    public /* synthetic */ $$Lambda$DownloadWorker$o5FMEgmvT3LvmQqMwwFv54BLFXY(String str1, String str2, boolean z2, Context context) {
        this.f$0 = str1;
        this.f$1 = str2;
        this.f$2 = z2;
        this.f$3 = context;
    }

    @Override // java.lang.Runnable
    public final void run() throws Throwable {
        DownloadWorker.lambda$o5FMEgmvT3LvmQqMwwFv54BLFXY(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
