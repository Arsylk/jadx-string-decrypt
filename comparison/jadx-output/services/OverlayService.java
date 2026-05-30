package com.mistral.jon.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import p001z.bbs;

/* JADX INFO: loaded from: classes.dex */
public class OverlayService extends Service {

    /* JADX INFO: renamed from: a */
    private static long[] f751a = {653423763, 2127847622};

    /* JADX INFO: renamed from: a */
    private bbs bbsVar;

    /* JADX INFO: renamed from: b */
    protected boolean f754b = false;

    /* JADX INFO: renamed from: a */
    protected boolean f753a = false;

    /* JADX INFO: renamed from: a */
    protected int f752a = 0;

    /* JADX INFO: renamed from: a */
    private final BinderImpl2 binder = new BinderImpl2(this);

    /* JADX INFO: renamed from: com.mistral.jon.services.OverlayService$a */
    public static class BinderImpl2 extends Binder {

        /* JADX INFO: renamed from: a */
        private final OverlayService overlayService;

        public BinderImpl2(OverlayService overlayService) {
            this.overlayService = overlayService;
        }

        /* JADX INFO: renamed from: a */
        public OverlayService getOverlayService() {
            return this.overlayService;
        }
    }

    /* JADX INFO: renamed from: a */
    public bbs getBbsVar() {
        return this.bbsVar;
    }

    /* JADX INFO: renamed from: a */
    public void setBbsVar(bbs bbsVar) {
        this.bbsVar = bbsVar;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }
}
