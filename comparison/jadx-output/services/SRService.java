package com.mistral.jon.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.mistral.jon.services.a11y.AcService;
import p001z.ayp;
import p001z.bcs;
import p001z.bcz;
import p001z.bda;
import p001z.bdb;
import p001z.bdc;
import p001z.bdd;

/* JADX INFO: loaded from: classes.dex */
public class SRService extends Service {

    /* JADX INFO: renamed from: a */
    private static final String str = "SRService";

    /* JADX INFO: renamed from: a */
    private static bcz bczVar;

    /* JADX INFO: renamed from: a */
    private static long[] f755a = {1177333807, 881903873, 1980737859, 541668067, 601728714, 1370273170, 1167140563};

    /* JADX INFO: renamed from: a */
    private String m1080a() {
        String str2 = getPackageName();
        NotificationChannel notificationChannel = new NotificationChannel(str2, getClass().getSimpleName(), 2);
        notificationChannel.setLockscreenVisibility(0);
        ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannel);
        return str2;
    }

    /* JADX INFO: renamed from: a */
    private void m1081a() {
        Notification.Builder builder = new Notification.Builder(this, m1080a());
        builder.setContentTitle(getString(2131558458));
        builder.setContentText(getString(2131558461));
        builder.setSmallIcon(17301543);
        startForeground(11, builder.build());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private void m1082a(String str1, int i) {
        m1081a();
        ayp aypVar = ayp.m4254a();
        String str2 = "doRecord: ScreenCaptureDataHolder.data=" + bdb.intent;
        String str3 = str;
        aypVar.m4259a(str2, str3);
        bcs.f1584a = true;
        bdc bdcVar = AcService.acService.bdcVar;
        bdcVar.m4581a(str1);
        bdcVar.m4580a(bdb.f1594a, bdb.intent);
        ayp.m4254a().m4259a("doRecord: Starting screen recording", str3);
        new Thread(new Runnable(bdcVar, str1, i) { // from class: com.mistral.jon.services.-$$Lambda$SRService$yPx8qKXthZp-vu8iSDvgBHDGruc
            public final /* synthetic */ bdc f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ int f$3;

            public /* synthetic */ $$Lambda$SRService$yPx8qKXthZpvu8iSDvgBHDGruc(bdc bdcVar2, String str4, int i2) {
                this.f$1 = bdcVar2;
                this.f$2 = str4;
                this.f$3 = i2;
            }

            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                SRService.m10592lambda$yPx8qKXthZpvu8iSDvgBHDGruc(this.f$0, this.f$1, this.f$2, this.f$3);
            }
        }).start();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: a */
    private /* synthetic */ void m1083a(bdc bdcVar, String str2, int i) throws Throwable {
        bdd.m4585a(this, bdcVar, str2, i, bczVar);
        bda.f1592a = false;
        bczVar = null;
        bcs.f1584a = false;
        m1084b();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: b */
    private void m1084b() {
        stopForeground(true);
    }

    /* JADX INFO: renamed from: lambda$yPx8qKXthZp-vu8iSDvgBHDGruc */
    public static /* synthetic */ void m10592lambda$yPx8qKXthZpvu8iSDvgBHDGruc(SRService sRService, bdc bdcVar, String str2, int i) throws Throwable {
        sRService.m1083a(bdcVar, str2, i);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        ayp aypVar = ayp.m4254a();
        String str1 = "onStartCommand: started foreground service data=" + bdb.intent + " isCapture=" + bcs.f1584a;
        String str2 = str;
        aypVar.m4259a(str1, str2);
        if (bdb.intent != null && !bcs.f1584a) {
            ayp.m4254a().m4259a("onStartCommand: starting recording", str2);
            m1082a(intent.getStringExtra("filename"), intent.getIntExtra("timeout", 10000));
        }
        return super.onStartCommand(intent, i, i2);
    }
}
