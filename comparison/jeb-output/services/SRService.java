package com.mistral.jon.services;

import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.mistral.jon.services.a11y.AcService;
import z.ayp;
import z.bcs;
import z.bcz;
import z.bda;
import z.bdb;
import z.bdc;
import z.bdd;

public class SRService extends Service {
    private static final String a = "SRService";
    private static bcz a;
    private static long[] a;

    static {
        long[] arr_v = new long[7];
        SRService.a = arr_v;
        arr_v[0] = 0x462CB02FL;
        arr_v[1] = 0x3490C901L;
        arr_v[2] = 0x760FA943L;
        arr_v[3] = 0x204932E3L;
        arr_v[4] = 601728714L;
        arr_v[5] = 1370273170L;
        arr_v[6] = 1167140563L;
    }

    private String a() {
        NotificationChannel notificationChannel0 = new NotificationChannel("com.mistral.jon", this.getClass().getSimpleName(), ((int)SRService.a[5]) ^ 0x51ACB590);
        notificationChannel0.setLockscreenVisibility(((int)SRService.a[6]) ^ 1167140563);
        ((NotificationManager)this.getSystemService("notification")).createNotificationChannel(notificationChannel0);
        return "com.mistral.jon";
    }

    private void a() {
        Notification.Builder notification$Builder0 = new Notification.Builder(this, this.a());
        notification$Builder0.setContentTitle(this.getString(((int)SRService.a[1]) ^ 0x4B9DC93B));
        notification$Builder0.setContentText(this.getString(((int)SRService.a[2]) ^ 0x902A97E));
        notification$Builder0.setSmallIcon(((int)SRService.a[3]) ^ 557920964);
        this.startForeground(((int)SRService.a[4]) ^ 601728705, notification$Builder0.build());
    }

    private void a(String s, int v) {
        this.a();
        ayp.a().a("doRecord: ScreenCaptureDataHolder.data=" + bdb.a, "SRService");
        bcs.a = ((int)SRService.a[0]) ^ 0x462CB02E;
        bdc bdc0 = AcService.a.a;
        bdc0.a(s);
        bdc0.a(bdb.a, bdb.a);
        ayp.a().a("doRecord: Starting screen recording", "SRService");
        new Thread(() -> {
            bdd.a(this, bdc0, s, v, SRService.a);
            bda.a = ((int)SRService.a[6]) ^ 1167140563;
            SRService.a = null;
            bcs.a = ((int)SRService.a[6]) ^ 1167140563;
            this.b();
        }).start();
    }

    // Detected as a lambda implementation
    private void a(bdc bdc0, String s, int v) [...]

    private void b() {
        this.stopForeground(((boolean)(((int)SRService.a[0]) ^ 0x462CB02E)));
    }

    @Override  // android.app.Service
    public IBinder onBind(Intent intent0) {
        return null;
    }

    @Override  // android.app.Service
    public int onStartCommand(Intent intent0, int v, int v1) {
        ayp.a().a("onStartCommand: started foreground service data=" + bdb.a + " isCapture=" + bcs.a, "SRService");
        if(bdb.a != null && !bcs.a) {
            ayp.a().a("onStartCommand: starting recording", "SRService");
            this.a(intent0.getStringExtra("filename"), intent0.getIntExtra("timeout", 10000));
        }
        return super.onStartCommand(intent0, v, v1);
    }
}

