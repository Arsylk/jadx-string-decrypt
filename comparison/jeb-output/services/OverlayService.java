package com.mistral.jon.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import z.bbs;

public class OverlayService extends Service {
    public static class a extends Binder {
        private final OverlayService a;

        public a(OverlayService overlayService0) {
            this.a = overlayService0;
        }

        public OverlayService a() {
            return this.a;
        }
    }

    private static long[] a;
    protected int a;
    private final a a;
    private bbs a;
    protected boolean a;
    protected boolean b;

    static {
        long[] arr_v = new long[2];
        OverlayService.a = arr_v;
        arr_v[0] = 0x26F27493L;
        arr_v[1] = 0x7ED460C6L;
    }

    public OverlayService() {
        long[] arr_v = OverlayService.a;
        this.b = ((int)arr_v[0]) ^ 0x26F27493;
        this.a = ((int)arr_v[0]) ^ 0x26F27493;
        this.a = ((int)arr_v[0]) ^ 0x26F27493;
        this.a = new a(this);
    }

    public bbs a() {
        return this.a;
    }

    public void a(bbs bbs0) {
        this.a = bbs0;
    }

    @Override  // android.app.Service
    public IBinder onBind(Intent intent0) {
        return this.a;
    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent intent0, int v, int v1) {
        return ((int)OverlayService.a[1]) ^ 0x7ED460C7;
    }
}

