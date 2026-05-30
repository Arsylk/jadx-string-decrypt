package com.mistral.jon.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import p001z.ban;

/* JADX INFO: loaded from: classes.dex */
public class Ov2Service extends Service {

    /* JADX INFO: renamed from: a */
    private View view = null;

    /* JADX INFO: renamed from: a */
    private WindowManager windowManager;

    /* JADX INFO: renamed from: a */
    private static long[] f746a = {767509561, -1709555664, 45502061, -1950345994, 541599969};

    /* JADX INFO: renamed from: a */
    private static final String str = OvService.class.getSimpleName();

    /* JADX INFO: renamed from: a */
    private View m1066a() {
        View view = new View(this);
        view.setSystemUiVisibility(4102);
        view.setBackgroundColor(-16777216);
        return view;
    }

    /* JADX INFO: renamed from: a */
    private WindowManager.LayoutParams m1067a() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2038, 48, -3);
        layoutParams.gravity = 48;
        DisplayMetrics metrics = new DisplayMetrics();
        this.windowManager.getDefaultDisplay().getMetrics(metrics);
        layoutParams.width = metrics.widthPixels;
        layoutParams.height = metrics.heightPixels + 2000;
        return layoutParams;
    }

    /* JADX INFO: renamed from: a */
    private void m1068a() {
        if (this.view == null) {
            try {
                this.view = m1066a();
                this.windowManager.addView(this.view, m1067a());
                ban.m4406a("Showed", str);
            } catch (Exception e) {
                ban.m4407a(e.toString(), str, 6);
                this.view = null;
            }
        }
    }

    /* JADX INFO: renamed from: b */
    private void m1069b() {
        View view = this.view;
        if (view != null) {
            this.windowManager.removeView(view);
            this.view = null;
            ban.m4406a("Hided", str);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.windowManager = (WindowManager) getSystemService("window");
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        m1069b();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        m1068a();
        return 1;
    }
}
